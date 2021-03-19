/*
 * Free for charge
 */
package ru.npptmk.plcinterfaces.s7_1200;

import com.ghgande.j2mod.modbus.ModbusException;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import static java.util.logging.Level.SEVERE;
import javax.swing.Timer;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.logging.Logger;
import ru.npptmk.plcinterfaces.s7_1200.S7_1200;
import ru.npptmk.plcinterfaces.s7_1200.TagDef;

/**
 * <b>Описание</b><br>
 * Класс используется в качестве модели для {@link javax.swing.JTable}.<br>
 * Данная модель заполняет таблицу тагами получаемыми по протоколу modbus от PLC.<br>
 * Для работы с контроллером по протоколу modbus модель использует интерфейс
 * {@link ru.npptmk.plcinterfaces.s7_1200.S7_1200}.<br>
 * Запись информации из модели в PLC производится методом {@link #writeToPlc() writeToPLC}<br>
 * Обязательно посмотрите документацию к конструктору. <br>
 * <b>Использование</b><br>
 * <code>
 *      &nbsp ...<br>
 *      &nbsp JTable someJTable = new JTable();<br>
 *      &nbsp someJTable.setModel(new PLC_TableModel("PLC_IP", inputs, outputs, regs, dwreg);<br>
 *      &nbsp ...<br></code>
 * @author RazumnovAA
 */
public class PLC_TableModel implements TableModel {
    //Интерфейся для обмена данными с PLC по modbus
    private S7_1200 plc = null;
    //Таймер опроса PLC
    private final Timer timer;
    //Список входов PLC
    private final TagDef[] inputs;
    //Список выходов PLC
    private final TagDef[] outputs;
    //Список отслеживаемых регистров
    private final TagDef[] registers;
    //Список двойных слов
    private final TagDef[] dwReg;
    //Число строк с данными для таблицы
    private int rowsCount;
    //Список слушателей модели
    private List<TableModelListener> listenersList;
    //Для хранения комментариев в таблице
    private final List<String> comments;
    //Для хранения флагов установки нового значения
    private final List<Boolean> isSettingList;
    //Для хранения задаваемых значений
    private final List<Short> valuesToSetList;
    
    
    /**
     * Для создания модели требуется только IP адрес PLC<br>
     * и списки данных которые модель будет читать по протоколу modbus.
     * @param ip IP адрес PLC в виде строки
     * @param inputs список входов PLC
     * @param outputs список выходов PLC
     * @param registers список регистров PLC
     * @param dwReg список двойных слов PLC
     */
    public PLC_TableModel(String ip, TagDef[] inputs, TagDef[] outputs, TagDef[] registers, TagDef[] dwReg) {
        //Список слушателей модели
        this.listenersList = new ArrayList<>();
        // Создаем интерфейс для контроллера.
        this.plc = new S7_1200(ip, inputs, outputs, registers, dwReg);
        try {
            plc.connect();
        } catch (Exception ex) {
            Logger.getLogger(PLC_TableModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Создаем счётчик
        //передаём ему в конструктор слушатель счётчика
        this.timer = new Timer(
                //Задержка
                200,
                //Слушатель итераций таймера
                (ActionEvent e) -> {
                    try {
                        //Если есть соединение с контроллером
                        if (!plc.isConnected()) {
                            return;
                        }
                        // Читаем значения входов из контроллера
                        plc.readInputs();
                        // Читаем значения выходов
                        plc.readOutputs();
                        // Читаем значения регистров
                        plc.readRegisters("Error", 14);
                    } catch (ModbusException ex) {
                        Logger.getLogger(PLC_TableModel.class.getName()).log(SEVERE, null, ex);
                    }
                });
        timer.setInitialDelay(2000);//таймер
        timer.setRepeats(true);
        timer.start();
        
        this.inputs = inputs;
        this.outputs = outputs;
        this.registers = registers;
        this.dwReg = dwReg;
        
        //Подсчитываем количество столбцов в таблице
        {
            this.rowsCount += inputs.length;
            this.rowsCount += outputs.length;
            this.rowsCount += registers.length;
        }
        
        //Инициальзируем листы для хранения комментариев, флагов записи
        //и значений для записи.
        comments = new ArrayList<>(rowsCount);
        isSettingList = new ArrayList<>(rowsCount);
        valuesToSetList = new ArrayList<>(rowsCount);
        
        for (int i = 0; i < rowsCount; i++){
            comments.add("");
            isSettingList.add(false);
            valuesToSetList.add(null);
        }
        
    }

    

    @Override
    /**
     * Число строк в таблице рассчитывается в конструкторе
     * на основании количества отслеживаемых тагов.
     * @return количество столбцов.
     */
    public int getRowCount() {
        return rowsCount;
    }

    
    @Override
    /**
     * Всегда постоянно и равно 6
     */
    public int getColumnCount() {
        return 7;
    }
    

    /**
     * Возвращает имя тага PLC в зависимости от строки таблицы данных.<br>
     * Порядок следования тагов по строкам определяется последовательностью их<br>
     * нахождения в с писках передаваемых в конструктор.<br>
     * Это нужно ещё и для того, что значения из PLC читаются не по адресам, а<br>
     * а по именами тагов.
     * @param index номер строки в таблице.
     * @return имя тага в текстовом формате.
     */
    private String getTagNameByOrderNumber(int index) {
        //Если мы в inputs
        if ((index >= 0) &&(index < (inputs.length))){
            return inputs[index].name;
        //Если мы в outputs
        }else if((index >= inputs.length) &&(index < (inputs.length + outputs.length))){
            return outputs[index - inputs.length].name;
        //Если мы в registers
        }else if((index >= (inputs.length + outputs.length)) &&(index < (inputs.length + outputs.length + registers.length))){
            return registers[index - inputs.length - outputs.length].name;
        }
        return "Недоступно";
    }
    
    /**
     * Возвращает адрес тага PLC от строки таблицы данных.
     * @param index номер строки в таблице.
     * @return адрес тага.
     */
    private String getTagAddressByOrderNumber(int index){
        //Если мы в inputs
        if ((index >= 0) &&(index < (inputs.length))){
            return "I" + inputs[index].address/10 + "." + inputs[index].address%10;
        //Если мы в outputs
        }else if((index >= inputs.length) &&(index < (inputs.length + outputs.length))){
            return "Q" + outputs[index - inputs.length].address/10 + "." + outputs[index - inputs.length].address%10;
        //Если мы в registers
        }else if((index >= (inputs.length + outputs.length)) &&(index < (inputs.length + outputs.length + registers.length))){
            return registers[index - inputs.length - outputs.length].address/10 + "." + registers[index - inputs.length - outputs.length].address%10;
        }
        return "N/A";//
    }

    /**
     * Возвращает значение тага PLC в зависимости от строки таблицы данных.
     * @param index номер строки в таблице.
     * @return значение тага для заданной строки таблицы.
     */
    private Object getTagValueByOrderNumber(int index) {
        int tmpIndex;
        if (plc.isConnected()) {
            try {
                //Если мы в inputs
                if ((index >= 0) && (index < (inputs.length))) {
                    return plc.getInputState(getTagNameByOrderNumber(index));
                    //Если мы в outputs
                } else if ((index >= inputs.length) && (index < (inputs.length + outputs.length))) {
                    return plc.getOuputState(getTagNameByOrderNumber(index));
                    //Если мы в registers
                } else if ((index >= (inputs.length + outputs.length)) && (index < (inputs.length + outputs.length + registers.length))) {
                    return plc.getShortRegister(getTagNameByOrderNumber(index));
                }
            } catch (ModbusException ex) {
                return ex.getMessage();
            }
        }
        return "Нет соединения с PLC";
    }


    @Override
    /**
     * Возвращает название колонки в зависимости от номера столбца.
     * @param columnIndex номер колонки в таблице.
     * @return имя колонки.
     */
    public String getColumnName(int columnIndex) {
        String result = "";
        switch (columnIndex) {
            case 0:
                result = "№";
                break;
            case 1:
                result = "Имя переменной";
                break;
            case 2:
                result = "Адресс";
                break;
            case 3:
                result = "Значение переменной";
                break;
            case 4:
                result = "Задаваемое значение";
                break;
            case 5:
                result = "Задать значение";
                break;
            case 6:
                result = "Комментарий";
                break;
            default:
                result = "Неправильный индекс заголовка";
        }
        return result;
    }
    

    @Override
    /**
     *Используется для определения типа дынных хранимых в столбцах.
     *От этого зависит внешней вид столбца, а также ограничения
     *на ввод в конкретную ячейку.
     * @param columnIndex номер колонки
     * @return тип класа данных в указанном столбце.
     */
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex){
            case 4:
                return Short.class;
            case 5:
                return Boolean.class;
            default:
                return String.class;
        }
    }
    

    @Override
    /**
     * Можно редактировать только следующие столбцы:
     * · Задаваемое значение
     * · Задать значение
     * · Комментарий
     * @param rowIndex номер строки
     * @param columnIndex номер колонки
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex){
            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
                return rowIndex + 1;
            case 1:
                return getTagNameByOrderNumber(rowIndex);
            case 2:
                return getTagAddressByOrderNumber(rowIndex);
            case 3:
                return getTagValueByOrderNumber(rowIndex);
            case 4:
                return valuesToSetList.get(rowIndex);
            case 5:
                return isSettingList.get(rowIndex);
            case 6:
                return comments.get(rowIndex);
            default:
                return null;
        }
    }
    
    
    /**
     * Метод записывает переменные помеченные для записи в PLC.<br>
     * Чтобы вызвать этот метод в вашей программе можно использовать<br>
     * следующую конструкцию:<br>
     * <code>
     * ...<br>
     *  &nbsp ((PLC_TableModel)someJTable.getModel()).writeToPLC();<br>
     * ...
     * </code>
     */
    public void writeToPlc() {
        try {
            for (int i = 0; i < rowsCount; i++) {
                if (isSettingList.get(i)) {
                    plc.setShortRegister(getTagNameByOrderNumber(i), valuesToSetList.get(i));
                }
            }
            plc.writeData();
        } catch (ModbusException ex) {
            Logger.getLogger(PLC_TableModel.class.getName()).log(SEVERE, null, ex);
        }
    }
    
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 4:
                valuesToSetList.set(rowIndex, (Short) aValue);
                break;
            case 5:
                isSettingList.set(rowIndex, (Boolean) aValue);
                break;
            case 6:
                comments.set(rowIndex, aValue.toString());
                break;
            default:
                //do nothing;
        }
    }

    
    @Override
    public void addTableModelListener(TableModelListener l) {
        listenersList.add(l);
    }

    
    @Override
    public void removeTableModelListener(TableModelListener l) {
        listenersList.add(l);
    }

}

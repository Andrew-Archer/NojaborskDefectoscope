/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.plcinterfaces.s7_1200;

import com.ghgande.j2mod.modbus.ModbusCoupler;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleDigitalIn;
import com.ghgande.j2mod.modbus.procimg.SimpleDigitalOut;
import com.ghgande.j2mod.modbus.procimg.SimpleProcessImage;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.BitVector;
import java.util.HashMap;

/**
 * Интерфейсный класс с контроллером Siemens S7-1200. <br>
 * Интерфейс построен на базе протокола Modbus TCP. <br>
 * Схема интерфейса.<br>
 * Контроллер, выступая как slave модуль, обеспечивает посредством протокола
 * Modbus TCP доступ к своим дискретным входам, выходам, а также к некоторой
 * области памати (holding regidters). Прикладная программа с помощью данного
 * класса может читать значения входов, читать и записывать значения выходов и
 * регистров.<br>
 * Кроме slave модуля контроллер может выступить в роли master модуля,
 * передающего прикладной программе пакеты данных в виде набора значений
 * регистров. Эти пакеты принимаются данным объектом и передаются на обработку
 * прикладной программе. Такой режим работы используется в ситуации, когда
 * контролер генерирует значительный объем онлайн данных и получение их путем
 * периодических запросов нерационально.<br>
 * В любом случае, в качестве основного режима для контроллера является slave
 * режим. Этот режим должен быть реализован в обязательном порядке для любого
 * кнтроллера и доступен всегда.<br>
 * Master режим включается и выключается по специальному запросу прикладной
 * программы. Он может остутствовать на некоторых контроллерах. Данные,
 * передаваемые в режиме master модуля, доступны и путем запроса в slave
 * режиме.<br>
 * Все регистры, входы и выходв доступны прикладной программе по именам тегов.
 * Таблица тегов передается конструктору объекта.
 *
 * @author MalginAS
 */
public class S7_1200 extends ModbusTCPMaster {

    private final HashMap<String, Short> inputs = new HashMap<>();
    private final HashMap<String, Short> outputs = new HashMap<>();
    private final HashMap<String, Short> registers = new HashMap<>();

    private I_PLCDataUpdated[] upd;    // Массив обработчиков изменений регистров.

    private final SimpleProcessImage plcImg = new SimpleProcessImage();
    private final S7_1200SlaveListener listener;
    private int inpMinAddr;         // Минимальный адрес дискретного входа
    private int inpMaxAddr;         // Максимальный адрес дискретного входа
    private int outMinAddr;         // Минимальный адрес дискретного выхода
    private int outMaxAddr;         // Максимальный адрес дискретного выхода
    private int regMinAddr;         // Минимальный адрес регистра
    private int regMaxAddr;         // Максимальный адрес регистра
    // Диапазоны адресов для отправки в контроллер
    private int outMinToSend;       // Начальный адрес выходов
    private int outMaxToSend;       // Конечный адрес выходов
    private int regMinToSend;       // Начальный адрес регистров
    private int regMaxToSend;       // Конечный адрес регистров
    // Флаг состояния контроллера
    private boolean isConnected = false;

    /**
     * Конструктор интерфейсного объекта с контроллером при использовании
     * контроллером стандарного порта для протокола Mudbus TCP (502).
     *
     * @param addr IP адрес контроллера.
     * @param inputs
     * @param outputs
     * @param registers
     * @param dwReg
     */
    public S7_1200(String addr, TagDef[] inputs, TagDef[] outputs, TagDef[] registers, TagDef[] dwReg) {
        this(addr, 502, inputs, outputs, registers, dwReg);
    }

    /**
     * Конструктор интерфейсного объекта в общем случае.
     *
     * @param addr IP адрес контроллера.
     * @param port Порт, используемый контроллером. Этот же порт используется
     * для приема данных от контроллера.
     * @param inp Теги дискретных входов
     * @param out Теги дискретных выходов
     * @param reg Теги регистров данных
     * @param dwRegs Теги двойных регистров данных.
     */
    public S7_1200(String addr, int port, TagDef[] inp, TagDef[] out, TagDef[] reg, TagDef[] dwRegs) {
        super(addr, port);
        inputs.clear();
        inpMaxAddr = Short.MIN_VALUE;
        inpMinAddr = Short.MAX_VALUE;
        outMaxAddr = Short.MIN_VALUE;
        outMinAddr = Short.MAX_VALUE;
        regMaxAddr = Short.MIN_VALUE;
        regMinAddr = Short.MAX_VALUE;
        for (TagDef tag : inp) {
            short modAddr = tag.getBitModbusAddr();
            inputs.put(tag.name, modAddr);
            inpMaxAddr = Math.max(inpMaxAddr, modAddr);
            inpMinAddr = Math.min(inpMinAddr, modAddr);
        }
        for (int i = inpMinAddr; i <= inpMaxAddr; i++) {
            plcImg.addDigitalIn(i, new SimpleDigitalIn());
        }
        outputs.clear();
        for (TagDef tag : out) {
            short modAddr = tag.getBitModbusAddr();
            outputs.put(tag.name, modAddr);
            outMaxAddr = Math.max(outMaxAddr, modAddr);
            outMinAddr = Math.min(outMinAddr, modAddr);
        }
        for (int i = outMinAddr; i <= outMaxAddr; i++) {
            plcImg.addDigitalOut(i, new SimpleDigitalOut());
        }
        registers.clear();
        for (TagDef tag : reg) {
            short modAddr = tag.getWordModbusAddr();
            registers.put(tag.name, modAddr);
            regMaxAddr = Math.max(regMaxAddr, modAddr);
            regMinAddr = Math.min(regMinAddr, modAddr);
        }
        for (TagDef tag : dwRegs) {
            short modAddr = tag.getWordModbusAddr();
            registers.put(tag.name, modAddr);
            regMaxAddr = Math.max(regMaxAddr, modAddr + 1);
            regMinAddr = Math.min(regMinAddr, modAddr);
        }
        for (int i = regMinAddr; i <= regMaxAddr; i++) {
            plcImg.addRegister(i, new SimpleRegister(0));
        }
        upd = new I_PLCDataUpdated[regMaxAddr];
        for (int i = 0; i < regMaxAddr; i++) {
            upd[i] = null;
        }
        resetModAddrs();
        ModbusCoupler.getReference().setProcessImage(plcImg);
        ModbusCoupler.getReference().setMaster(false);
        listener = new S7_1200SlaveListener(3, upd);
        listener.setPort(port);
        listener.listen();
    }

    /**
     * Установка обработчика изменения заданного регистра.<br>
     * Привязывает к адресу регситра контроллера обработчик, вызываемый после
     * записи в этот регистр нового значения.
     *
     * @param addr Modbus адрес регистра.
     * @param proc Обработчик изменения значения регистра. Для удаления
     * обработчика можно задать {@code null}.
     */
    public void setUpdateProc(int addr, I_PLCDataUpdated proc) {
        if (addr < 0 || addr > regMaxAddr) {
            return;
        }
        upd[addr] = proc;
    }

    /**
     * Сбрасывает на исходное значение адреса для отправки данных кнтроллеру
     */
    private void resetModAddrs() {
        outMinToSend = Short.MAX_VALUE;
        outMaxToSend = Short.MIN_VALUE;
        regMinToSend = Short.MAX_VALUE;
        regMaxToSend = Short.MIN_VALUE;
    }

    /**
     * Помечает адрес дискретного выхода, как требующицй отправки в контроллер.
     *
     * @param addr адрес
     */
    private void setModOutput(short addr) {
        outMinToSend = Math.min(outMinToSend, addr);
        outMaxToSend = Math.max(outMaxToSend, addr);
    }

    /**
     * Помечает адрес регистра, как требующицй отправки в контроллер.
     *
     * @param addr адрес
     */
    private void setModWreg(short addr) {
        regMinToSend = Math.min(regMinToSend, addr);
        regMaxToSend = Math.max(regMaxToSend, addr);
    }

    /**
     * Помечает адрес двойного регистра, как требующицй отправки в контроллер.
     *
     * @param addr адрес
     */
    private void setModDWreg(short addr) {
        regMinToSend = Math.min(regMinToSend, addr);
        regMaxToSend = Math.max(regMaxToSend, addr + 1);
    }

    /**
     * Получить значение дискретного входа.
     *
     * @param name Имя тега
     * @return Состояние входа
     * @throws com.ghgande.j2mod.modbus.ModbusException Если тег с заданным
     * именем отсутствует
     */
    public boolean getInputState(String name) throws ModbusException {
        if (!inputs.containsKey(name)) {
            throw new ModbusException("Нет дискретного входа с тегом " + name);
        }
        Short addr = inputs.get(name);
        return plcImg.getDigitalIn(addr).isSet();
    }

    /**
     * Получить значение дискретного выхода.
     *
     * @param name Имя тега
     * @return Состояние выхода
     * @throws com.ghgande.j2mod.modbus.ModbusException Если тег с указанным
     * именем отсутствует.
     */
    public boolean getOuputState(String name) throws ModbusException {
        if (!outputs.containsKey(name)) {
            throw new ModbusException("Нет дискретного выхода с тегом " + name);
        }
        Short addr = outputs.get(name);
        return plcImg.getDigitalOut(addr).isSet();
    }

    /**
     * Получить значение регистра в виде короткого цеого.
     *
     * @param name Имя тега
     * @return Значение регистра.
     * @throws com.ghgande.j2mod.modbus.ModbusException Если тег с указанным
     * именем тсутствует.
     */
    public short getShortRegister(String name) throws ModbusException {
        if (!registers.containsKey(name)) {
            throw new ModbusException("Нет регистра с тегом " + name);
        }
        Short addr = registers.get(name);
        return (short) plcImg.getRegister(addr).toUnsignedShort();
    }

    /**
     * Получить значение регистра в виде вещественного числа.
     *
     * @param name Имя тега
     * @return Значение регистра.
     */
    public float getFloatRegister(String name) {
        return 0f;
    }

    /**
     * Установить значение дискретного выхода.
     *
     * @param name Имя тега
     * @param value Новое значение дискретного выхода.
     * @throws com.ghgande.j2mod.modbus.ModbusException Если тег не найден.
     */
    public void setOuputState(String name, boolean value) throws ModbusException {
        if (!outputs.containsKey(name)) {
            throw new ModbusException("Нет дискретного выхода с тегом " + name);
        }
        Short addr = outputs.get(name);
        plcImg.getDigitalOut(addr).set(value);
        setModOutput(addr);
    }

    /**
     * Установить значение регистра в виде короткого цеого.
     *
     * @param name Имя тега
     * @param value Новое значение регистра.
     * @throws com.ghgande.j2mod.modbus.ModbusException Если указанный тег не
     * найден.
     */
    public void setShortRegister(String name, short value) throws ModbusException {
        if (!registers.containsKey(name)) {
            throw new ModbusException("Нет регистра с тегом " + name);
        }
        Short addr = registers.get(name);
        plcImg.getRegister(addr).setValue(value);
        setModWreg(addr);
    }

    /**
     * Установить значение регистра в виде вещественного числа.
     *
     * @param name Имя тега
     * @param value Новое значение регистра.
     */
    public void setFloatRegister(String name, float value) {
    }

    /**
     * Читает из контроллера значения всех дискретных входов. Полученные
     * значения размещаются в образе процессора и доступны через функции
     * поучения значений по именам тегов.@param count
     *
     * @throws ModbusException При возникновении ошибки.
     */
    public void readInputs() throws ModbusException {
        int cou = inpMaxAddr - inpMinAddr + 1;
        if (cou > 0) {
            readInputs(inpMinAddr, cou);
        }
    }

    /**
     * Читает из контроллера значения указанного диапахона дискретных входов.
     * Полученные значения размещаются в образе процессора и доступны через
     * функции поучения значений по именам тегов.@param count
     *
     * @param name Имя первого тега для чтения.
     * @param count Количество читаемых входов
     * @throws ModbusException При возникновении ошибки.
     */
    public void readInputs(String name, int count) throws ModbusException {
        if (!inputs.containsKey(name)) {
            throw new ModbusException("Не найден искретный вход с именем " + name);
        }
        readInputs(inputs.get(name), count);
    }

    /**
     * Читает из контроллера значения указанного диапахона дискретных входов.
     * Полученные значения размещаются в образе процессора и доступны через
     * функции поучения значений по именам тегов.
     *
     * @param ref Modbus адрес первого читаемого дискретного входа.
     * @param count Количество читаемых входов
     * @throws ModbusException При возникновении ошибки.
     */
    public void readInputs(int ref, int count) throws ModbusException {
        if (!isConnected){
            return;
        }
        BitVector vr = readInputDiscretes(ref, count);
        for (int i = 0; i < vr.size(); i++) {
            ((SimpleDigitalIn) plcImg.getDigitalIn(ref + i)).set(vr.getBit(i));
        }
    }

    /**
     * Читает из контроллера значения всех дискретных выходов. Полученные
     * значения размещаются в образе процессора и доступны через функции
     * поучения значений по именам тегов.@param count
     *
     * @throws ModbusException
     */
    public void readOutputs() throws ModbusException {
        int cou = outMaxAddr - outMinAddr + 1;
        if (cou > 0) {
            readOutputs(outMinAddr, cou);
        }
    }

    /**
     * Читает из контроллера значения указанного диапахона дискретных входов.
     * Полученные значения размещаются в образе процессора и доступны через
     * функции поучения значений по именам тегов.@param count
     *
     * @param name Имя первого тега для чтения.
     * @param count Количество читаемых входов
     * @throws ModbusException При возникновении ошибки.
     */
    public void readOutputs(String name, int count) throws ModbusException {
        if (!outputs.containsKey(name)) {
            throw new ModbusException("Не найден дискретный выход с именем " + name);
        }
        readOutputs(outputs.get(name), count);
    }

    /**
     * Читает из контроллера значения указанного диапахона дискретных выходов.
     * Полученные значения размещаются в образе процессора и доступны через
     * функции поучения значений по именам тегов.
     *
     * @param ref Modbus адрес первого читаемого дискретного выхода.
     * @param count Количество читаемых выходов.
     * @throws ModbusException При возникновении ошибки.
     */
    public void readOutputs(int ref, int count) throws ModbusException {
        if (!isConnected){
            return;
        }
        BitVector vr = readCoils(ref, count);
        for (int i = 0; i < vr.size(); i++) {
            ((SimpleDigitalOut) plcImg.getDigitalOut(ref + i)).set(vr.getBit(i));
        }
    }

    /**
     * Читает из контроллера значения всех регистров данных.
     *
     * @throws ModbusException При возникновении ошибки.
     */
    public void readRegisters() throws ModbusException {
        if (!isConnected){
            return;
        }
        int cou = regMaxAddr - regMinAddr + 1;
        if (cou > 0) {
            readRegisters(regMinAddr, cou);
        }
    }

    /**
     * Читает из контроллера значения указанного диапахона регистров. Полученные
     * значения размещаются в образе процессора и доступны через функции
     * поучения значений по именам тегов.
     *
     * @param name Имя первого тега для чтения.
     * @param count Количество читаемых регистров.
     * @throws ModbusException При возникновении ошибки.
     */
    public void readRegisters(String name, int count) throws ModbusException {
        if (!registers.containsKey(name)) {
            throw new ModbusException("Не найден регистр с именем " + name);
        }
        readRegisters(registers.get(name), count);
    }

    /**
     * Читает из контроллера значения указанного диапахона дискретных выходов.
     * Полученные значения размещаются в образе процессора и доступны через
     * функции поучения значений по именам тегов.
     *
     * @param ref Modbus адрес первого читаемого регистра.
     * @param count Количество читаемых регистров.
     * @throws ModbusException При возникновении ошибки.
     */
    public void readRegisters(int ref, int count) throws ModbusException {
        if(!isConnected()) return;
        Register[] vr = readMultipleRegisters(ref, count);
        for (int i = 0; i < vr.length; i++) {
            ((SimpleRegister) plcImg.getRegister(ref + i)).setValue(vr[i].toBytes());
        }
    }

    /**
     * Функция записывает в контроллер все измененные данные.
     *
     * @throws com.ghgande.j2mod.modbus.ModbusException При возникновении
     * ошибки.
     */
    public void writeData() throws ModbusException {
        if (!isConnected) {
            return;
        }
        int count = outMaxToSend - outMinToSend + 1;
        if (count == 1) {
            writeCoil(plcImg.getUnitID(), outMinToSend, plcImg.getDigitalOut(outMinToSend).isSet());
        } else if (count > 1) {
            BitVector bv = new BitVector(count);
            for (int i = outMinToSend; i <= outMaxToSend; i++) {
                bv.setBit(i - outMinToSend, plcImg.getDigitalOut(i).isSet());
            }
            writeMultipleCoils(outMinToSend, bv);
        }
        count = regMaxToSend - regMinToSend + 1;
        if (count == 1) {
            writeSingleRegister(regMinToSend, plcImg.getRegister(regMinToSend));
        } else if (count > 1) {
            Register[] bv = plcImg.getRegisterRange(regMinToSend, count);
            writeMultipleRegisters(regMinToSend, bv);
        }
        resetModAddrs();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        isConnected = false;
    }

    @Override
    public void connect() throws Exception {
        super.connect();
        isConnected = true;
    }

    public boolean isConnected() {
        return isConnected;
    }
}

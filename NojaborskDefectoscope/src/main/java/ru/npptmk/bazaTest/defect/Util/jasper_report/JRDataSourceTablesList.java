/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.Util.jasper_report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import ru.npptmk.bazaTest.defect.TubeType;
import ru.npptmk.bazaTest.defect.model.BasaTube;

/**
 * Класс обертка вокруг {@code List<BasaTube>}.
 *
 * @author RazumnovAA
 */
public class JRDataSourceTablesList implements JRDataSource {

    private final Object[] thickValues;

    /**
     * Список труб которые необходимо вывести в таблицу отчета.
     */
    private final List<BasaTube> tubes;
    /**
     * Так как в BasaTube есть только поле с ID для TubeType, то приходится
     * хранить еще и все типы труб.
     */
    private final List<TubeType> tubeTypes;
    /**
     * Немер текущей строчки в списке труб, при проходе по списку труб.
     */
    private int index;
    /**
     * Форматирует даты.
     */
    private final DateFormat dateFormater;

    /**
     * Нет конструктора без параметров, так как объект данного класса бесполезен
     * без данных которые он оборачивает.
     *
     * @param tubes Список труб которые будут выводится в таблицу отчета.
     * @param tubeTypes Список типов труб на которые ссылается поле
     * BasaTube.tubeType.
     */
    public JRDataSourceTablesList(
            List<BasaTube> tubes,
            List<TubeType> tubeTypes,
            Object[] thickValues) {
        this.dateFormater = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        this.index = -1;
        this.tubes = tubes;
        this.tubeTypes = tubeTypes;
        this.thickValues = thickValues;
    }

    @Override
    public Object getFieldValue(JRField jrf) throws JRException {
        //Получаме имя запрашиваемого поля.
        String fieldName = jrf.getName();
        //Выбираем возвращаемы еданные в зависимости от типа поля
        if (fieldName != null) {
            switch (fieldName) {
                case "rowNumber":
                    return index + 1;
                case "tubeNumber":
                    return tubes.get(index).getId();
                case "defectDetectionTime":
                    return dateFormater.format(tubes.get(index).getDateCreate());
                case "status":
                    return tubes.get(index).getStatusToString();
                case "tubeType":
                    return tubeTypes.get(Math.toIntExact(tubes.get(index).getTypeID() - 1)).toString()
                            + (tubes.get(index).isSample() ? " СОП" : "");
                case "minThick":
                    return "0f";
                case "length":
                    return tubes.get(index).getLengthInMeters();
                case "customer":
                    try {
                        return tubes.get(index).getCustomer().toString();
                    } catch (Exception ex) {
                        return "";
                    }
                case "thickValue":
                    if(thickValues[index] != null && ((Vector)thickValues[index]).elementAt(4) !=null){
                        return ((Vector)thickValues[index]).elementAt(4);
                    } else{
                        return "нет данных";
                    }
                case "operator":
                    try {
                        return tubes.get(index).getShift().getOperator() != null ? tubes.get(index).getShift().getOperator().toString() : "";
                    } catch (Exception ex) {
                        return "";
                    }
                case "durabilityGroup":
                    try {
                        return tubes.get(index).getDurabilityGroup();
                    } catch (Exception ex) {
                        return "";
                    }
                default:
                    return "Не верное имя поля.";
            }
        }

        return "У поля нет имени.";
    }

    @Override
    public boolean next() throws JRException {
        //Передвигаем указатель на следующую строку.
        index++;
        //Проверяем, что строка на которую ссылается указатель
        //не вышла за пределы списка.
        return index < tubes.size();
    }

}

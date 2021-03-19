/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import static ru.npptmk.bazaTest.defect.TubeType.ThickClasses.*;

/**
 * Класс реализует {@link TableModel} для хранения {@link TubeType}.
 *
 * @author RazumnovAA
 */
public class TubeTypesTableModel implements TableModel {

    /**
     * Для хранения слушателей изменения модели, то есть <tt>tubeTypes</tt>.
     */
    private List<TableModelListener> listeners;

    /**
     * Для хранения имеющегося списка типов труб.
     */
    private List<TubeType> tubeTypes;

    public TubeTypesTableModel() {
        tubeTypes = new ArrayList<>();
        listeners = new ArrayList<>();
    }
    
    public TubeTypesTableModel(List<TubeType> tubeTypes) {
        this.tubeTypes = tubeTypes;
        listeners = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return tubeTypes.size();
    }

    public void addTubeType(TubeType tubeType) {
        tubeTypes.add(tubeType);
    }

    @Override
    public int getColumnCount() {
        /**
         * Колличество колонок всегда постоянное.
         */
        return 7;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return "№";
            case 1:
                return "Наименование";
            case 2:
                return "Диаметр";
            case 3:
                return "Толщина стенки, мм";
            case 4:
                return "1 класс, мм";
            case 5:
                return "2 класс, мм";
            case 6:
                return "3 класс, мм";
            default:
                return "Неверный номер колонки, мм";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return Long.class;
            case 1:
                return String.class;
            case 2:
                return Float.class;
            case 3:
                return Float.class;
            case 4:
                return Float.class;
            case 5:
                return Float.class;
            case 6:
                return Float.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        /**
         * Все ячейки редактируемые.
         */
        if(columnIndex == 0) return false;
        return true;
    }

    public void addRow() {
        tubeTypes.add(new TubeType(new Long(tubeTypes.size()+1), "", 0, 0));
        listeners.forEach((listener) -> {
            listener.tableChanged(new TableModelEvent(this));
        });
    }

    public void removeRow(int rowIndex) {
        tubeTypes.remove(rowIndex);
        listeners.forEach((listener) -> {
            listener.tableChanged(new TableModelEvent(this));
        });
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        //Выбираем тип трбы соостветсвующий номеру строкию.
        TubeType tubeType = tubeTypes.get(rowIndex);

        try {
            //Возвращаем значение поля, соответствующее номеру колнки.
            switch (columnIndex) {
                case 0:
                    return rowIndex;
                case 1:
                    return tubeType.getName();
                case 2:
                    return tubeType.getDiameter();
                case 3:
                    return tubeType.getThick();
                case 4:
                    return tubeType.getThickClassBorderValue(CLASS_1);
                case 5:
                    return tubeType.getThickClassBorderValue(CLASS_2);
                case 6:
                    return tubeType.getThickClassBorderValue(CLASS_3);
                default:
                    return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        //Выбираем тип трбы соостветсвующий номеру строкию.
        TubeType tubeType = tubeTypes.get(rowIndex);

        try {
            //Возвращаем значение поля, соответствующее номеру колнки.
            switch (columnIndex) {
                case 0:
                    tubeType.setIdTubeType((Long) aValue);
                    break;
                case 1:
                    tubeType.setName((String) aValue);
                    break;
                case 2:
                    tubeType.setDiameter((Float) aValue);
                    break;
                case 3:
                    tubeType.setThick((Float) aValue);
                    break;
                case 4:
                    tubeType.setThickClassBorder(CLASS_1, (Float) aValue);
                    break;
                case 5:
                    tubeType.setThickClassBorder(CLASS_2, (Float) aValue);
                    break;
                case 6:
                    tubeType.setThickClassBorder(CLASS_3, (Float) aValue);
                    break;

                default:
                //Do nothing
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    @Override
    public void addTableModelListener(TableModelListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeTableModelListener(TableModelListener listener) {
        listeners.remove(listener);
    }

    /**
     * Просто возвращает список параметров.
     *
     * @return Лист типов труб
     */
    List<TubeType> getTubeTypesList() {
        return tubeTypes;
    }

    void removeRow(int minSelectionIndex, int maxSelectionIndex) {
        //Удаляем выделенные строки
        for (int i = minSelectionIndex; i <= maxSelectionIndex; i++) {
            tubeTypes.remove(minSelectionIndex);
        }

        //Оповещвем всех слушателей
        listeners.forEach((listener) -> {
            listener.tableChanged(new TableModelEvent(this));
        });
    }
    
    /**
     * Производит грлубинное клонирование данного листа типов. Клонирование
     * производится методом серриализации/десерриализации из потока объектов.
     *
     * @param tubeTypes лист заданный для клонирования.
     */
    void cloneContainer(List<TubeType> tubeTypes) {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);) {

            //Записать пустой список типов труб, если на входе null.
            if (tubeTypes == null) {
                this.tubeTypes = new ArrayList<>();
                //Если список труб не пустой, то клонируем его серриализацией.    
            } else {
                oos.writeObject(tubeTypes);
                oos.flush();

                ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
                ObjectInputStream ois = new ObjectInputStream(bais);

                this.tubeTypes = (List<TubeType>) ois.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            showMessageDialog(null, "Не получилось скопировать список типов труб.");
            showMessageDialog(null, "Будет создан пустой список типов труб.");
            this.tubeTypes = new ArrayList<>();
        }

    }

}

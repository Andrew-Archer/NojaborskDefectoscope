/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;

/**
 *
 * @author RazumnovAA
 */
public class TableDatePicker extends JTable {

    private final static int COLUMN_DAY_NUMBER = 1;
    private final static int COLUMN_MONTH_NUMBER = 2;
    private final static int COLUMN_YEAR_NUMBER = 3;
    private final static int COLUMN_HOUR_NUMBER = 4;
    private final static int COLUMN_MINUTE_NUMBER = 5;
    private final static int COLUMN_SECOND_NUMBER = 6;

    /**
     *
     */
    @SuppressWarnings("LeakingThisInConstructor")
    public TableDatePicker() {
        super();
        LocalDateTime now = LocalDateTime.now();
        this.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                    {"Начало",
                        now.getDayOfMonth(),
                        now.getMonthValue(),
                        now.getYear(),
                        now.getHour(),
                        now.getMinute(),
                        now.getSecond()},
                    {"Конец",
                        now.getDayOfMonth(),
                        now.getMonthValue(),
                        now.getYear(),
                        now.getHour(),
                        now.getMinute(),
                        now.getSecond()}
                },
                new String[]{
                    "Период", "День", "Месяц", "Год", "Час", "Мин.", "Сек."
                }
        ) {
            boolean[] canEdit = new boolean[]{
                false, true, true, true, true, true, true
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        this.getModel().addTableModelListener(this);
        setDimensions();
        //Обновляем выбор дат.
        JComboBox comboBox_days = new JComboBox();
        for (int i = 1; i < 32; i++) {
            comboBox_days.addItem(i);
        }
        getColumnModel().getColumn(COLUMN_DAY_NUMBER).setCellEditor(new DefaultCellEditor(comboBox_days));

        JComboBox comboBox_monthes = new JComboBox();
        for (int i = 1; i <= 12; i++) {
            comboBox_monthes.addItem(i);
        }
        getColumnModel().getColumn(COLUMN_MONTH_NUMBER).setCellEditor(new DefaultCellEditor(comboBox_monthes));

        JComboBox comboBox_years = new JComboBox();
        for (int i = 2017; i <= 2100; i++) {
            comboBox_years.addItem(i);
        }
        getColumnModel().getColumn(COLUMN_YEAR_NUMBER).setCellEditor(new DefaultCellEditor(comboBox_years));

        JComboBox comboBox_hours = new JComboBox();
        for (int i = 0; i < 24; i++) {
            comboBox_hours.addItem(i);
        }
        getColumnModel().getColumn(COLUMN_HOUR_NUMBER).setCellEditor(new DefaultCellEditor(comboBox_hours));

        JComboBox comboBox_minutes = new JComboBox();
        for (int i = 0; i < 60; i++) {
            comboBox_minutes.addItem(i);
        }
        getColumnModel().getColumn(COLUMN_MINUTE_NUMBER).setCellEditor(new DefaultCellEditor(comboBox_minutes));

        JComboBox comboBox_seconds = new JComboBox();
        for (int i = 0; i < 60; i++) {
            comboBox_seconds.addItem(i);
        }
        getColumnModel().getColumn(COLUMN_SECOND_NUMBER).setCellEditor(new DefaultCellEditor(comboBox_seconds));
    }

    private LocalDateTime parseRowToDateTime(int rowNumber) {
        return LocalDateTime.of(
                getCellAsInt(rowNumber, COLUMN_YEAR_NUMBER),
                getCellAsInt(rowNumber, COLUMN_MONTH_NUMBER),
                getCellAsInt(rowNumber, COLUMN_DAY_NUMBER),
                getCellAsInt(rowNumber, COLUMN_HOUR_NUMBER),
                getCellAsInt(rowNumber, COLUMN_MINUTE_NUMBER),
                getCellAsInt(rowNumber, COLUMN_SECOND_NUMBER)
        );
    }

    /**
     * Возвращает дату начала интервала за который необходимо вывести отчет.
     *
     * @return дата начала отчета.
     */
    public LocalDateTime getStartLocalDateTime() {
        return parseRowToDateTime(0);
    }

    /**
     * Возвращает дату начала интервала за который необходимо вывести отчет.
     *
     * @return дата начала отчета.
     */
    public Date getStartDate() {
        LocalDateTime localDateTime = parseRowToDateTime(0);
        ZonedDateTime zoneDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zoneDateTime.toInstant());
    }

    /**
     * Возвращает дату конца интервала за который необходимо вывести отчет.
     *
     * @return дата конца отчета.
     */
    public Date getEndDate() {
        LocalDateTime localDateTime = parseRowToDateTime(1);
        ZonedDateTime zoneDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zoneDateTime.toInstant());
    }

    private void setCorrectNumberOfdaysInMonth(int selectedRow) {
        int daysInMonth = 0;
        //Определяем количество дней в месяце.
        switch ((Integer) getModel().getValueAt(selectedRow, COLUMN_MONTH_NUMBER)) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                daysInMonth = 31;
                break;
            case 2:
                int selectedYear = (Integer) getModel().getValueAt(selectedRow, COLUMN_MONTH_NUMBER);
                //Если год високосный
                if (((selectedYear % 4 == 0) && (selectedYear % 100 != 0)) || (selectedYear % 400 == 0)) {
                    daysInMonth = 29;
                } else {
                    daysInMonth = 28;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                daysInMonth = 30;
                break;
        }
        //Обновляем список редактирования колноки с днями.
        JComboBox daysEditor = (JComboBox) getColumn(1).getCellEditor().getTableCellEditorComponent(this, null, true, selectedRow, COLUMN_DAY_NUMBER);
        //Очищаем списко дней
        daysEditor.removeAllItems();
        //Заполняем список дней
        for (int i = 1; i <= daysInMonth; i++) {
            daysEditor.addItem(i);
        }
    }

    /**
     * Возвращает дату конца интервала за который необходимо вывести отчет.
     *
     * @return дата конца интервала.
     */
    public LocalDateTime getEndLocalDateTime() {
        return parseRowToDateTime(1);
    }

    private int getCellAsInt(int rowNumber, int columnNumber) {
        return Integer.valueOf(getModel().getValueAt(rowNumber, columnNumber).toString());
    }

    /**
     * Изменяет размеры и возможность изменения размеров.
     */
    private void setDimensions() {
        if (this.getColumnModel().getColumnCount() > 0) {
            this.getColumnModel().getColumn(0).setResizable(false);
            this.getColumnModel().getColumn(1).setResizable(false);
            this.getColumnModel().getColumn(1).setPreferredWidth(45);
            this.getColumnModel().getColumn(2).setResizable(false);
            this.getColumnModel().getColumn(2).setPreferredWidth(65);
            this.getColumnModel().getColumn(3).setResizable(false);
            this.getColumnModel().getColumn(3).setPreferredWidth(65);
            this.getColumnModel().getColumn(4).setResizable(false);
            this.getColumnModel().getColumn(4).setPreferredWidth(40);
            this.getColumnModel().getColumn(5).setResizable(false);
            this.getColumnModel().getColumn(5).setPreferredWidth(40);
            this.getColumnModel().getColumn(6).setResizable(false);
            this.getColumnModel().getColumn(6).setPreferredWidth(40);
        }
    }

}

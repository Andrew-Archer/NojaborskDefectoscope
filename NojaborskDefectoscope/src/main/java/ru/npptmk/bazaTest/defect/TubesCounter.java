package ru.npptmk.bazaTest.defect;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Предназначен для счета труб. Счетчик может включаться и выключатся, также у
 * него есть методы для увеличения количества труб. Можно добавить слушателя
 * изменения количества труб.
 *
 * @author RazumnovAA
 */
public class TubesCounter {

    /**
     * Все слушатели обновления количества труб, должны реализовывать этот
     * интерфейс.
     */
    public interface TubesCounterUpdatedListener {

        /**
         * Метод вызываемый у слушателей изменения количества труб.
         *
         * @param goodTubesCount обновленное количество годных труб.
         * @param badTubesCount обновленное количество бракованных труб.
         */
        public void reactOnTubesCountUpdated(long goodTubesCount, long badTubesCount);
    }
    /**
     * Количество годных труб.
     */
    private long goodTubesCount;
    /**
     * Количество забракованных.
     */
    private long badTubesCount;

    /**
     * Список слушателей изменения количества труб.
     */
    private List<TubesCounterUpdatedListener> listeners = new ArrayList<>();

    /**
     * Добавляет слушателя в список.
     *
     * @param listener объект который необходимо уведомлять об изменении
     * количества труб.
     */
    public void addListener(TubesCounterUpdatedListener listener) {
        listeners.add(listener);
    }

    /**
     * Увеличить количество годных труб на 1.
     */
    public void addGoodTube() {
        //Увеличиваем количество годных труб.
        goodTubesCount++;
        //Уведомляем слушателей об изменившемся количестве труб.
        listeners.forEach((listener) -> {
            listener.reactOnTubesCountUpdated(goodTubesCount, badTubesCount);
        });
    }

    /**
     * Увеличить количество бракованных труб на 1.
     */
    public void addBadTube() {
        //Увеличиваем количество бракованных труб.
        badTubesCount++;
        //Уведомляем слушателей об изменившемся количестве труб.
        listeners.forEach((listener) -> {
            listener.reactOnTubesCountUpdated(goodTubesCount, badTubesCount);
        });
    }

    /**
     * Задает текущее количество годных труб.
     *
     * @param count количество годных труб для задания.
     */
    public void setGoodTubesCount(long count) {
        goodTubesCount = count;
        //Уведомляем слушателей об изменившемся количестве труб.
        listeners.forEach((listener) -> {
            listener.reactOnTubesCountUpdated(goodTubesCount, badTubesCount);
        });
    }

    /**
     * Задает текущее количество бракованных труб.
     *
     * @param count количество бракованных труб для задания.
     */
    public void setBadTubesCount(long count) {
        badTubesCount = count;
        //Уведомляем слушателей об изменившемся количестве труб.
        listeners.forEach((listener) -> {
            listener.reactOnTubesCountUpdated(goodTubesCount, badTubesCount);
        });
    }

    /**
     * Обновляет счетчик в соответствии с данными в заданной tableModel.
     *
     * @param tableModel модель в соответствии с которой необходимо обновить
     * счетчик.
     */
    public void updateToTableModel(DefaultTableModel tableModel) {
        dropCounter();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            //Если труба не СОП
            if (!((String) tableModel.getValueAt(i, 1)).contains("СОП")) {
                //Если труба в архиве
                if (tableModel.getValueAt(i, 5).equals("Архив")) {
                    //Если труба брак.
                    switch ((String) tableModel.getValueAt(i, 3)) {
                        case "брак":
                            addBadTube();
                            break;
                        case "годная":
                            addGoodTube();
                            break;
                    }
                }
            }
        }
    }

    /**
     * Сбрасывает счётчики годных и бракованных труб.
     */
    public void dropCounter() {
        //Сбрасываем количество годных труб.
        goodTubesCount = 0;
        //Сбрасываем количество забракованных труб.
        badTubesCount = 0;
        //Уведомляем слушателей об изменившемся количестве труб.
        listeners.forEach((listener) -> {
            listener.reactOnTubesCountUpdated(goodTubesCount, badTubesCount);
        });
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;

/**
 * Класс параметров панели самописца толщиномера.<br>
 * Дополнительно к базовому классу содержит массив цветов для 
 * отображения порогов и массив цветов для отображения значений
 * внутри интервалов порогов.
 * @author MalginAS
 */
public class SamopPnlTLSParams extends SamopPnlDfParams{
    /**
     * Массив цветов для изображения порогов толщины.
     * Пороги следуют от максимального к минимальному.
     */
    public Color[] levels;
    /**
     * Цвета для изображения значений толщины находящихся между
     * заданными порогами.<br>
     * Цвет с индексом 0 служит для изображения толщин выше порога с индексом 0.<br>
     * Цвет с индексом 1 служит для изображения толщин ниже порога с индексом 0, 
     * но выше порога с индексом 1, и так дале. Всего элементов
     * в массиве цветов должно быть на 1 больше, чем в массиве порогов.
     */
    public Color[] values;
    /**
     * Максимальное значение толщины для самописца.
     */
    private static final Color[] DEFLEVS = {Color.YELLOW, Color.RED};
    private static final Color[] DEFVALS ={Color.GREEN, Color.YELLOW, Color.RED};
    /**
     * Конструктор параметров по умолчанию. Предполагается наличие двух порогов
     * - желтый и красный, и трех цветов значений - зеленый, желтый, красный.
     * @param name наименование канала.
     * @param devId идентификатор источника данных.
     * @param chanId идентификатор канала в источнике данных.
     */
    public SamopPnlTLSParams(String name, long devId, int chanId) {
        this(DEFLEVS, DEFVALS, name, devId, chanId);
    }
    /**
     * Конструктор параметров с полным функционалом.
     * @param levels массив цветов для рисования уровней. Первый самый толстый,
     * последний самый тонкий.
     * @param values массив цветов для росования значений. Цвет с индексом 
     * 0 служит для изображения толщин выше порога с индексом 0.<br>
     * Цвет с индексом 1 служит для изображения толщин ниже порога с индексом 0, 
     * но выше порога с индексом 1, и так дале. Всего элементов
     * в массиве цветов должно быть на 1 больше, чем в массиве порогов.
     * @param name наименование канала.
     * @param devId идентификатор источник данных.
     * @param chanId идентификатор канала в источнике данных.
     */
    public SamopPnlTLSParams(Color[] levels, Color[] values, String name, long devId, int chanId) {
        super(devId, chanId, name, (short)0, (short)20480, 5, 20);
        this.levels = levels;
        this.values = values;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;

/**
 * Параметры средства отображения графиков дефектоскопии.
 * @author MalginAS
 */
public class GraphicsPnlDfParams extends GraphicsPnlParams{
    /**
     * Идентификаторы источников данных.
     */
    public long[] devIds;
    /**
     * Идентификаторы каналов.
     */
    public int[] chanIds;
    /**
     * Цвета каналов.
     */
    public Color[] colors;
    /**
     * Наименования каналов.
     */
    public String[] names;
    /**
     * Конструктор с полным набором параметров.
     * @param devIds массив идентификаторов источников данных.
     * @param chanIds массив идентификаторов каналов.
     * @param colors массив цветов для изображения каналов.
     * @param names массиав наименований каналов.
     * @param name заголовок компонента.
     * @param maxX длина графика.
     * @param nHorLines количество горизонтальных линий, включая верхнюю и нижнюю
     * границы графика.
     * @param nVertLines количество вертикальных линий, включая правую и левую
     * границу графика.
     */
    public GraphicsPnlDfParams(long[] devIds, int[] chanIds, Color[] colors, String[] names, String name, int maxX, int nHorLines, int nVertLines) {
        super(name, 100, maxX, nHorLines, nVertLines);
        this.devIds = devIds;
        this.chanIds = chanIds;
        this.colors = colors;
        this.names = names;
    }
    
}

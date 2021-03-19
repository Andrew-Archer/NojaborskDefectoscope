/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;

/**
 * Параметры набора графиков толщинометрии.
 * @author MalginAS
 */
public class GraphicsPnlTlsParams extends GraphicsPnlParams{
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
     * Цвета отображения классов толщин.
     */
    public Color[] levels;

    public GraphicsPnlTlsParams(long[] devIds, int[] chanIds, Color[] colors, String[] names, Color[] levels, String name, int maxY, int maxX, int nHorLines, int nVertLines) {
        super(name, maxY, maxX, nHorLines, nVertLines);
        this.devIds = devIds;
        this.chanIds = chanIds;
        this.colors = colors;
        this.names = names;
        this.levels = levels;
    }
    
}

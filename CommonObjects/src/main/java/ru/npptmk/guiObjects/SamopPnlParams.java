/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.io.Serializable;

/**
 * Параметры панели самописцев.<br>
 * Это базовый класс для всех остальных типов самописцев.
 * @author SmorkalovAV
 */
public class SamopPnlParams implements Serializable {
    /**
     * Конструктор с полным набором параметров.
     * @param name заголовок компонента.
     * @param minY минимальное значение по оси Y.
     * @param maxY максимальное значенн=ие по оси Y.
     * @param nHLine количество горизонтальных линий координатной сетки.
     * @param vLinestep шаг вертикальных линий в пикселах.
     */
    public SamopPnlParams(String name, short minY, short maxY, int nHLine, int vLinestep) {
        this.name = name;
        this.minY = minY;
        this.maxY = maxY;
        this.nHLine = nHLine;
        this.vLinestep = vLinestep;
    }
    /**Имя, отображаемое на панельке*/
    public String name;
    /**
     * Минимальное значение по оси Y
     */
    public short minY;
    /**
     * Максимальное значение по оси Y
     */
    public short maxY;
    /**
     * Количество горизонтальных линий координатной сетки.
     */
    public int nHLine;
    /**
     * Шаг вертикальных линий сетки в пикселах.
     */
    public int vLinestep;
}

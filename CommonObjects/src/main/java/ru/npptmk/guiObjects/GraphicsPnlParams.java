/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.io.Serializable;

/**
 * Параметры средства отображения данных {@code GraphicPanel}<br>
 * Содержит все данные, необходимые для построения панельки отображения графиков.
 * @author SmorkalovAV
 */
class GraphicsPnlParams implements Serializable {
    /**Имя, отображаемое на панельке*/
    public String name;
    /**
     * Размер графика по вертикали.
     */
    public int maxY;
    /**
     * Размер графика по горизонтали.
     */
    public int maxX;
    /**
     * Количество горизонтальных линий координатной сетки, включая 
     * верхнюю и нижнюю границы графика.
     */
    public int nHorLines;
    /**
     * Количество вертикальных линий сетки, включая левую и правую
     * границы графика.
     */
    public int nVertLines;

    /**
     * Коснтруктор параметров панели графиков.
     * @param name Заголовок панели
     * @param maxY Максимальное отображаемое значение графика по вертикали.
     * @param maxX Максимальное отображаемое значение графика по горизонтали.
     * @param nHorLines Количество горизонтальных линий координатной сетки,
     * включая верхнюю и нижнюю границы.
     * @param nVertLines Количество вертикальных линий сетки включая левую и 
     * правую границы графика.
     */
    public GraphicsPnlParams(String name, int maxY, int maxX, int nHorLines, int nVertLines) {
        this.name = name;
        this.maxY = maxY;
        this.maxX = maxX;
        this.nHorLines = nHorLines;
        this.nVertLines = nVertLines;
    }
}

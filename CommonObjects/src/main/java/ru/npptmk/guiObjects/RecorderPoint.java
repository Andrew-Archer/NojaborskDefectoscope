/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;

/**
 * Отметка на самописце. <br>
 * Этот класс обозначает одну отметку на графке самописца. Эта отметка
 * представляет собой вертикальную линию шириной в один пиксел заданного цвета
 * расположенную в заданном диапазоне значений оси Y. Как минимум, отметка
 * занимает один пиксел повертикали.<br>
 * Значения верхнего и нижнего предела отметки задаются в пространстве координат
 * оси Y самописца.
 * @author MalginAS
 */
public class RecorderPoint {
    public Color color;
    public int up;
    public int low;

    /**
     * Конструктор одной отметки самописца.
     * 
     * @param color Цвет отметки на самописце
     * @param up Верхнее значение отметки в системе координат оси Y.
     * @param low Нижнее значение отметки в системе координат оси Y.
     * Если верхнее и нижнее значения в параметрах конструктора перепутаны, 
     * то в конструкторе они автоматически переставляются, так, чтобы в созднном
     * объекте {@code up >= low}.
     */
    public RecorderPoint(Color color, int up, int low) {
        this.color = color;
        this.up = Math.max(up, low);
        this.low = Math.min(up, low);
    }
    
}

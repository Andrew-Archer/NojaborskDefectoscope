/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

/**
 * Параметры панельки отображения самописцев дефектоскопии и толщинометрии.
 * @author Александр
 */
public class SamopPnlDfParams extends SamopPnlParams {
    /**
     * Индентификатор источника данных.
     */
    public long devId;
    /**
     * Идентификатор канала данных в источнике.
     */
    public int chanId;
    /**
     * Конструктор параметров самописца по умолчанию.
     * @param name наименование канала.
     * @param devId идентификатор источника данных.
     * @param chanId дентификатор канала данных.
     */
    public SamopPnlDfParams(String name, long devId, int chanId) {
        super(name, (short)0, (short)100, 5, 20);
        this.devId = devId;
        this.chanId = chanId;
    }

    /**
     * Конструктор с полным набором параметров.
     * @param devId идентификатор источника данных.
     * @param chanId идентификатор канала данных.
     * @param name наименование канала данных.
     * @param minY минимальное значение по оси Y.
     * @param maxY максимальное значение по оси Y.
     * @param nHLine количество горизонтальных линий сетки, вклбчая нижнюю и 
     * верхнюю границы.
     * @param vLinestep шаг вертикальных линий в пикселах.
     */
    public SamopPnlDfParams(long devId, int chanId, String name, short minY, short maxY, int nHLine, int vLinestep) {
        super(name, minY, maxY, nHLine, vLinestep);
        this.devId = devId;
        this.chanId = chanId;
    }
    
    
}

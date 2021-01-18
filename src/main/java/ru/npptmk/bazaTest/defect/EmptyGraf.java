/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.Serializable;
import ru.npptmk.guiObjects.IDefectScanDataProvider;
import ru.npptmk.guiObjects.IScanDataProvider;
import ru.npptmk.guiObjects.IThickScanDataProvider;

/**
 * Поставщик результатов для очистки графика.
 * @author MalginAS
 */
public class EmptyGraf implements Serializable,IDefectScanDataProvider, IThickScanDataProvider,
        IScanDataProvider {

    private final long devId;

    public EmptyGraf(long devId) {
        this.devId = devId;
    }
    
    @Override
    public int getMinValue(int nc) {
        return -1;
    }

    @Override
    public int getMaxValue(int nc) {
        return -1;
    }

    @Override
    public int getThreshold(int nc) {
        return -1;
    }

    @Override
    public int getGraficLength(int nc) {
        return 0;
    }

    @Override
    public int getGrafic(int nc, float[] x, float[] y) {
        return 0;
    }

    @Override
    public long getDeviceId() {
        return devId;
    }

    @Override
    public float getThick(int nc) {
        return -1;
    }

    @Override
    public float getMaxThick(int nc) {
        return -1;
    }

    @Override
    public int getThickGrafLength(int nc) {
        return 0;
    }

    @Override
    public int getThickGrafic(int nc, float[] x, float[] y) {
        return 0;
    }
    
}

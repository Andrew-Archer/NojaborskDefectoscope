/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.Serializable;
import ru.npptmk.guiObjects.IDefectScanDataProvider;

/**
 * Результат магнитной дефектоскопии.
 *
 * @author MalginAS
 */
public class BazaMDResult implements Serializable, IDefectScanDataProvider {

    private final long devId;
    private final int[] thresh = new int[8];
    private final float[][] x = new float[8][];
    private final float[][] y = new float[8][];

    public BazaMDResult(IDefectScanDataProvider drv) {
        devId = drv.getDeviceId();
        for (int i = 0; i < 8; i++) {
            thresh[i] = drv.getThreshold(1);
            int lg = drv.getGraficLength(i);
            x[i] = new float[lg];
            y[i] = new float[lg];
            drv.getGrafic(i, x[i], y[i]);
        }
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
        return thresh[nc];
    }

    @Override
    public int getGraficLength(int nc) {
        return x[nc].length;
    }

    @Override
    public int getGrafic(int nc, float[] x, float[] y) {
        System.arraycopy(this.x[nc], 0, x, 0, this.x[nc].length);
        System.arraycopy(this.y[nc], 0, y, 0, this.y[nc].length);
        return this.x[nc].length;
    }

    @Override
    public long getDeviceId() {
        return devId;
    }

}

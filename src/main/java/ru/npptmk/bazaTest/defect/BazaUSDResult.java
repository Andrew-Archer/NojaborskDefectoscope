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
//import ru.npptmk.drivers2.IDefectScanDataProvider;
//import ru.npptmk.drivers2.IScanDataProvider;
//import ru.npptmk.drivers2.IThickScanDataProvider;

/**
 * Класс результатов ультразвуковой дефектоскопии.
 *
 * @author MalginAS
 */
public class BazaUSDResult implements Serializable, IDefectScanDataProvider, IThickScanDataProvider {

    private final static long serialVersionUID = 1235511034568167212L;
    private final long devId;
    private final int[] thresh = new int[8];
    public final float[][] dfx = new float[8][];
    public final float[][] dfy = new float[8][];
    public final float[][] tx = new float[8][];
    public final float[][] ty = new float[8][];
    private final float[] maxThick = new float[8];

    public BazaUSDResult(IScanDataProvider drv) {
        devId = drv.getDeviceId();
        // Результаты дефектоскопии.
        IDefectScanDataProvider dd = (IDefectScanDataProvider) drv;
        IThickScanDataProvider dt = (IThickScanDataProvider) drv;
        for (int i = 0; i < 8; i++) {
            thresh[i] = dd.getThreshold(i);
            int lg = dd.getGraficLength(i);
            dfx[i] = new float[lg];
            dfy[i] = new float[lg];
            dd.getGrafic(i, dfx[i], dfy[i]);
            lg = dt.getThickGrafLength(i);
            tx[i] = new float[lg];
            ty[i] = new float[lg];
            dt.getThickGrafic(i, tx[i], ty[i]);
            maxThick[i] = dt.getMaxThick(i);
        }
        // Результаты толщинометрии
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
        return dfx[nc].length;
    }

    @Override
    public int getGrafic(int nc, float[] x, float[] y) {
        System.arraycopy(dfx[nc], 0, x, 0, dfx[nc].length);
        System.arraycopy(dfy[nc], 0, y, 0, dfy[nc].length);
        return dfx[nc].length;
    }

    @Override
    public long getDeviceId() {
        return devId;
    }

    /**
     * Просматривает графики толщиномера и возвращает минимальное значение
     * толщины.
     *
     * @param firstThickChannel номер первого канала толщинометрии.
     * @param lastThickChannel номер последнего канала толщиномтерии.
     * @param lostContactValue все что равно или ниже этого порога расценивается
     * как потеря акустического контакта.
     * @return минимальное значение толщины в мм или -1 при потере акустического
     * контакта.
     */
    public float getMinThick(
            int firstThickChannel,
            int lastThickChannel,
            float lostContactValue) {
        float minThick = Float.MAX_VALUE;
        //Если есть данные

        //Получаем минимальное значение значение толщины
        for (int channelNum = firstThickChannel; channelNum < lastThickChannel; channelNum++) {
            //Если есть данные в каналае
            if (ty[channelNum] != null
                    && ty[channelNum].length != 0) {
                for (int thickValueNum = 0;
                        thickValueNum < ty[channelNum].length;
                        thickValueNum++) {
                    //Если уровень сигнала ниже потери контакта
                    if (ty[channelNum][thickValueNum] < lostContactValue 
                            && ty[channelNum][thickValueNum] > 0) {
                        return -1;
                    }
                    //Если текущее значение толщины меньше minThick
                    //И болльше 0
                    if (minThick > ty[channelNum][thickValueNum]
                            && ty[channelNum][thickValueNum] > 0) {
                        minThick = ty[channelNum][thickValueNum];
                    }
                }

            }
        }
        return minThick;
    }

    @Override
    public float getThick(int nc) {
        return -1f;
    }

    @Override
    public float getMaxThick(int nc) {
        return maxThick[nc];
    }

    @Override
    public int getThickGrafLength(int nc) {
        return tx[nc].length;
    }

    @Override
    public int getThickGrafic(int nc, float[] x, float[] y) {
        System.arraycopy(tx[nc], 0, x, 0, tx[nc].length);
        System.arraycopy(ty[nc], 0, y, 0, ty[nc].length);
        return tx[nc].length;
    }

}

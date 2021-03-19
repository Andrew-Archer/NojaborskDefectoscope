/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ru.npptmk.guiObjects.ITubeDataProvider;

/**
 * Общие характеристики трубы после дефектоскопии.<br>
 * Формируется на основании результатов главного окна приложения.
 *
 * @author MalginAS
 */
public class BazaTubeResult implements Serializable, ITubeDataProvider {
    private static final long serialVersionUID = 8292712334053359040L;
    private final long deviceID;      // Идентификатор источника данных
    private final int tubeLength;     // Длина трубы в миллиметрах.
    private final float[][] defects;  // Координаты дефектов по типам. 
    private final float[] thicks;     // Значения толщин по классам.
    private final String tubeType;    // Обозначение типа трубы.

    /**
     * Построение набора результатов на основании данных драйвера.
     *
     * @param drv источник данных.
     * @param nch количество каналов типов дефектов.
     */
    public BazaTubeResult(ITubeDataProvider drv, int nch) {
        deviceID = drv.getDeviceId();
        tubeLength = drv.getTubeLength();
        thicks = drv.getTubeThicks();
        tubeType = drv.getTubeType();
        defects = new float[nch][];
        for (int i = 0; i < nch; i++) {
            defects[i] = drv.getDefects(i);
        }

    }

    @Override
    public int getTubeLength() {
        return tubeLength;
    }

    @Override
    public String getTubeType() {
        return tubeType;
    }

    @Override
    public float[] getTubeThicks() {
        return thicks;
    }

    @Override
    public float[] getDefects(int ch) {
        return defects[ch];
    }

    public List<Integer> getAllDefectsPositions() {
        List<Integer> defectsPositions = new ArrayList<>();
        for (float[] defectsPos : defects){
            for (float defectPos : defectsPos){
                defectsPositions.add(Math.round(defectPos * 1000));
            }
        }
        return defectsPositions;
    }

    @Override
    public long getDeviceId() {
        return deviceID;
    }

}

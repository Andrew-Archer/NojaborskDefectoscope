/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;

/**
 * Панелька для отрисовки самописца магнитной или ультразвуковой дефектоскопии
 * максимальное значение У координаты равно 100.
 *
 * @author SmorkalovAV
 */
public class SamopPnlDf extends SamopPnl implements IDrvsDataReader{

    private final  long idDvs;  // Идентификатор источника данных
    private final int chnl;     // Идентификатор канала данных.
    private final RecorderPoint[] pts = new RecorderPoint[2];
    public SamopPnlDf(PanelForGraphics prn, int ind) {
        super(prn, ind);
        SamopPnlDfParams par = (SamopPnlDfParams) prn.getParamCollection().get(ind);
        idDvs = par.devId;
        chnl = par.chanId;
        pts[0] = new RecorderPoint(Color.GREEN, 0, 0);
        pts[1] = new RecorderPoint(Color.YELLOW, 0, 0);
    }

    @Override
    public void dataForGrphs(IScanDataProvider driver) {
        if (driver.getDeviceId() != idDvs) {
            return;
        }
        if (driver instanceof IDefectScanDataProvider) {
            IDefectScanDataProvider dr = (IDefectScanDataProvider) driver;
            pts[0].low = 0;
            pts[0].up = dr.getMaxValue(chnl);
            pts[1].low =  dr.getThreshold(chnl);
            if (pts[0].up < 0 || pts[1].low <0 ){
                return;
            }
            pts[1].up = pts[1].low;
            if (pts[0].up > pts[1].up) {
                pts[0].color = Color.RED;
            } else {
                pts[0].color = Color.GREEN;
            }
            addPoint(pts);
            setCurVal(String.format("%d", pts[0].up), pts[0].color);
        }
    }

}

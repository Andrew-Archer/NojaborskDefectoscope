/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;
import java.util.Arrays;

/**
 * Средство отображения графиков толщинометрии.<br>
 * Этот компонент используется для отображения произвольного набора графиков с
 * результатами толщинометрии. Компонент используется в универсальном контейнере
 * {@code PanelForGraphics}.<br>
 * График одного канала толщинометрии изображаются в виде сплошной ломаной линии
 * заданного цвета, изображающей изменение величины толщины стенки. 
 * На компоненте может одновременно
 * отображаться несколько графиков. Графики могут поставляться различными
 * источниками данных.<br>
 * Помимо графика толщины на компонент выводятся значения порогов для
 * различных классо втруб. Цвета изображения порогов задаются в параметрах.
 * Максимальное значение координаты Y для всех графиков представляет собой
 * увеличенное на 1 значение округления номинальной толщины стенки трубы до
 * целых миллиметров.

 * @author MalginAS
 */
public class GraphicsPnlTls extends GraphicsPnl implements IDrvsDataReader {
    private final long[] devIds;
    private final int[] chanIds;
    private final GraphicsPnlTlsParams par;

    public GraphicsPnlTls(PanelForGraphics prn, int vizPos) {
        super(prn, vizPos);
        par = (GraphicsPnlTlsParams) prn.getParamCollection().get(vizPos);
        devIds = Arrays.copyOf(par.devIds, par.devIds.length);
        chanIds = Arrays.copyOf(par.chanIds, par.chanIds.length);
        for (int i = 0; i < par.names.length; i++) {
            addGraphic(new Grafic("Толщина " + par.names[i], 0,
                    par.colors[i], new float[1000], new float[1000]),i);
        }
        Color[] levels = par.levels;
        for (int i=0; i<levels.length; i++){
            Grafic porog = new Grafic("Класс " + i, 2,
                    levels[i], new float[2], new float[2]);
            porog.xPo[0] = 0;
            porog.xPo[1] = 12000;
            addGraphic(porog, i + par.names.length);
        }
    }

    @Override
    public void dataForGrphs(IScanDataProvider driver) {
        if (driver instanceof IThickScanDataProvider) {
            IThickScanDataProvider ds = (IThickScanDataProvider) driver;
            for (int i = 0; i < devIds.length; i++) {
                if (ds.getDeviceId() == devIds[i]) {
                    int np = ds.getThickGrafLength(chanIds[i]);
                    Grafic gr = getGrafsHash().get(i);
                    if (np <= 1) {
                        gr.setGrLen(0);
                    }
                    if (np > gr.getGrLen()) {
                        gr.setGrLen(ds.getThickGrafic(chanIds[i], gr.xPo, gr.yPo));
                    }
                }
            }
            refresh();
        }
        if (driver instanceof ITubeDataProvider){
            // Изменение глобальных параметров трубы
            ITubeDataProvider dp = (ITubeDataProvider) driver;
            int len = (Math.round((float)dp.getTubeLength()/1000f) + 1) * 1000;
            if (len == 1000){
                len = 12000;
            }
            setMaxX(len, len/1000);
            float[] nomThick = dp.getTubeThicks();
            int maxT = 10;
            if (nomThick != null){
                maxT = Math.round(nomThick[0]) + 1;
                for (int i=0; i<par.levels.length; i++){
                    Grafic porog = getGrafsHash().get(i + par.names.length);
                    if(i < nomThick.length && nomThick[i] > 0){
                        porog.yPo[0] = nomThick[i];
                        porog.yPo[1] = nomThick[i];
                    }
                }
            }
            setMaxY(maxT,maxT);
            refresh();
        }
    }
    
}

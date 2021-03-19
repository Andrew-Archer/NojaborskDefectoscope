/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.BasicStroke;
import java.util.Arrays;

/**
 * Средство отображения графиков дефектоскопии.<br>
 * Этот компонент используется для отображения произвольного набора графиков с
 * результатами дефектоскопии. Компонент используется в универсальном контейнере
 * {@code PanelForGraphics}.<br>
 * График одного канала дефектоскопии изображаются в виде сплошной ломаной линии
 * заданного цвета, изображающей изменение величины сигнала и пунктирной прямой
 * линии того же цвета, изображающей порог. На компоненте может одновременно
 * отображаться несколько графиков. Графики могут поставляться различными
 * источниками данных.<br>
 * Максимальное значение координаты Y для всех графиков равно 100.
 *
 * @author MalginAS
 */
public class GraphicsPnlDf extends GraphicsPnl implements IDrvsDataReader {

    private final long[] devIds;
    private final int[] chanIds;
    private static final float[] dh = {8.0f, 10.0f};

    public GraphicsPnlDf(PanelForGraphics prn, int vizPos) {
        super(prn, vizPos);
        GraphicsPnlDfParams par = (GraphicsPnlDfParams) prn.getParamCollection().get(vizPos);
        devIds = Arrays.copyOf(par.devIds, par.devIds.length);
        chanIds = Arrays.copyOf(par.chanIds, par.chanIds.length);
        for (int i = 0; i < par.names.length; i++) {
            addGraphic(new Grafic("Дефект " + par.names[i], 0,
                    par.colors[i], new float[1000], new float[1000]), 2 * i);
            Grafic porog = new Grafic("Порог " + par.names[i], 2,
                    par.colors[i], new float[2], new float[2]);
            porog.xPo[0] = 0;
            porog.xPo[1] = 12000;
            porog.str = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 1, dh, 0);
            addGraphic(porog, 2 * i + 1);
        }
    }

    @Override
    public void dataForGrphs(IScanDataProvider driver) {
        if (driver instanceof IDefectScanDataProvider) {
            IDefectScanDataProvider ds = (IDefectScanDataProvider) driver;
            for (int i = 0; i < devIds.length; i++) {
                if (ds.getDeviceId() == devIds[i]) {
                    int np = ds.getGraficLength(chanIds[i]);
                    Grafic gr = getGrafsHash().get(2 * i);
                    Grafic porog = getGrafsHash().get(2 * i + 1);
                    if (np <= 1) {
                        gr.setGrLen(0);
                    }
                    if (np > gr.getGrLen()) {
                        gr.setGrLen(ds.getGrafic(chanIds[i], gr.xPo, gr.yPo));
                    }
                    porog.yPo[0] = ds.getThreshold(chanIds[i]);
                    porog.yPo[1] = porog.yPo[0];
//                    if (ds.getDeviceId() == 2l) {
//                        System.out.println("Рисуем точку " + gr.getGrLen());
//                    }
                }
            }
            refresh();
        }
        if (driver instanceof ITubeDataProvider){
            ITubeDataProvider dp = (ITubeDataProvider) driver;
            int len = (Math.round((float)dp.getTubeLength()/1000f) + 1) * 1000;
            if (len == 1000){
                len = 12000;
            }
            setMaxX(len, len/1000);
            refresh();
        }
    }
}

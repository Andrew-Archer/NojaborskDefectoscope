/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;

/**
 * Панелька для отрисовки самописца толщинометрии.<br>
 * Рисуется график самописца для одного канала, но допускается наличие
 * произвольного количества порогов. Для каждого порога задается цвет, которым
 * изображается значение толщины, попадающее в данный диапазон. По умолчанию
 * ппорогов 2. Все, что выше первого, изображается зеленым, то, что выше второго
 * но ниже первого - желтым. То, что ниже второго - красным. Если порогов нет,
 * то все значения отображаются зеленым. Количество порогов и цвета могут быть
 * изменены.<br>
 * В качестве поставщика данных используется класс
 * {@code IThickScanDataProvider}.
 *
 * @author SmorkalovAV
 */
public class SamopPnlTLS extends SamopPnl implements IDrvsDataReader {

    private final Color[] levels;         // Цветв отображения уровней.
    private final Color[] values;         // Цвета отображения толщин.
    private int[] classes;         // Значения порогов.
    private final long devId;             // Идентификатор источника данных.
    private final int chanId;             // Идентификатор канала в источнике данных.
    private int curMaxVal = 0;  // Текущее максимальное значение графика толщиномера.
    private RecorderPoint[] pts;    // Точки графика.

    public SamopPnlTLS(PanelForGraphics prn, int ind) {
        super(prn, ind);
        SamopPnlTLSParams prm = (SamopPnlTLSParams) prn.getParamCollection().get(ind);
        levels = prm.levels;
        values = prm.values;
        devId = prm.devId;
        chanId = prm.chanId;
    }

    /**
     * Обновление графика толщины.<br>
     * График обновляется, если это нужный драйвер, и в нужном канале есть
     * данные. Перед обновлением проверяется текущее максимальное значение для
     * графика, и если оно сменилось, то картинка графика очищается и создаетс
     * заново.<br>
     * Значения и пороги изображаются заданными цветами.<br>
     * Так как график отображает только целые значения, то для изображения
     * толщину в мм умножаем на 10. Таким образом толщина на графике рисуется в
     * десятых долях мм. При этом, чтобы не плодить лишний код, значения времени
     * также умножаюися на 10, хотя они и являются целыми.
     *
     * @param driver
     */
    @Override
    public void dataForGrphs(IScanDataProvider driver) {
        if (driver.getDeviceId() != devId) {
            // Неправильный драйвер.
            return;
        }
        // Убеждаемся, что поддерживается нужный интерфейс.
        if (driver instanceof IThickScanDataProvider) {
            IThickScanDataProvider dr = (IThickScanDataProvider) driver;
            int t = (int) (dr.getThick(chanId) * 10);
            if (t < 0) {
                // Данных нет.
                setCurVal(String.format("%6.1f", dr.getThick(chanId)), Color.RED);
                return;
            }
            if (dr.getMaxThick(chanId) * 10 != curMaxVal) {
                curMaxVal = (int) (dr.getMaxThick(chanId) * 10);
                setGrafParams(0, curMaxVal, 6, 20);
                pts = new RecorderPoint[1];
                pts[0] = new RecorderPoint(Color.WHITE, 0, 0);
            }
            // Формируем сначала график толщины, затем пороги.
            pts[0].low = 0;
            pts[0].up = t;
            pts[0].color = values[0];
            addPoint(pts);
            setCurVal(String.format("%6.1f", dr.getThick(chanId)), pts[0].color);
        }
    }

}

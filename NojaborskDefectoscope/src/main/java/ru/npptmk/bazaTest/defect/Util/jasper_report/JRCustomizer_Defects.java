/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.Util.jasper_report;

import net.sf.jasperreports.engine.JRAbstractChartCustomizer;
import net.sf.jasperreports.engine.JRChart;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

/**
 *
 * @author RazumnovAA
 */
public class JRCustomizer_Defects extends JRAbstractChartCustomizer {

    @Override
    public void customize(JFreeChart chart, JRChart jasperChart) {

        for (int i = 0;
                i < chart.getXYPlot().getDataset().getSeriesCount();
                i++) {
            if (chart.getXYPlot().getDataset().getSeriesKey(i).toString().equals("УЗК")
                    || chart.getXYPlot().getDataset().getSeriesKey(i).toString().equals("Магнитка")
                    || chart.getXYPlot().getDataset().getSeriesKey(i).toString().equals("Толщиномер")) {
                chart.getXYPlot().setRenderer(new XYLineAndShapeRenderer(false, true));
            }
        }
    }

}

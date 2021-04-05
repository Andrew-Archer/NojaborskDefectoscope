/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.baza_test.defect.util.jasper_report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 *
 * @author RazumnovAA
 */
public class XYChartBean implements Serializable {

    private String chartName;
    private Float maxHight;
    private Float maxWidth;
    private boolean showPoints;
    private boolean showLine;
    private JRBeanCollectionDataSource graphData;

    public XYChartBean() {
        this(
                "Не указано",
                0f,
                0f,
                true,
                true,
                null);
        List<XYChartPointBean> emptyGraphData = new ArrayList<>();
        emptyGraphData.add(new XYChartPointBean("Неинициализоровано", 0d, 0d));
        this.graphData = new JRBeanCollectionDataSource(emptyGraphData);

    }

    public XYChartBean(
            String chartName,
            Float maxHight,
            Float maxWidth,
            boolean showPoints,
            boolean showLine,
            JRBeanCollectionDataSource graphData) {
        this.chartName = chartName;
        this.maxHight = maxHight;
        this.maxWidth = maxWidth;
        this.showPoints = showPoints;
        this.showLine = showLine;
        this.graphData = graphData;
    }

    public String getChartName() {
        return chartName;
    }

    public void setChartName(String chartName) {
        this.chartName = chartName;
    }

    public Float getMaxHight() {
        return maxHight;
    }

    public void setMaxHight(Float maxHight) {
        this.maxHight = maxHight;
    }

    public Float getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(Float maxWidth) {
        this.maxWidth = maxWidth;
    }

    public boolean isShowPoints() {
        return showPoints;
    }

    public void setShowPoints(boolean showPoints) {
        this.showPoints = showPoints;
    }

    public boolean isShowLine() {
        return showLine;
    }

    public void setShowLine(boolean showLine) {
        this.showLine = showLine;
    }

    public JRBeanCollectionDataSource getGraphData() {
        return graphData;
    }

    public void setGraphData(JRBeanCollectionDataSource graphData) {
        this.graphData = graphData;
    }
}

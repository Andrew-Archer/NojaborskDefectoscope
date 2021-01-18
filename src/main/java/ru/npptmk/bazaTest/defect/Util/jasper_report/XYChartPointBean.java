/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.Util.jasper_report;

/**
 *
 * @author RazumnovAA
 */
public class XYChartPointBean {

    private String series;
    private Double xValue;
    private Double yValue;

    public XYChartPointBean(String series, Double xValue, Double yValue) {
        this.series = series;
        this.xValue = xValue;
        this.yValue = yValue;
    }

    public XYChartPointBean() {
        this("unnamed_series", 0.0, 0.0);
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Double getxValue() {
        return xValue;
    }

    public void setxValue(Double xValue) {
        this.xValue = xValue;
    }

    public Double getyValue() {
        return yValue;
    }

    public void setYValue(Double yValue) {
        this.yValue = yValue;
    }

}

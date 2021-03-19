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
public class BarChartPointBean {
    private String series;
    private Double value;

    public BarChartPointBean(String series, Double value) {
        this.series = series;
        this.value = value;
    }

    public BarChartPointBean() {
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
    
    
}

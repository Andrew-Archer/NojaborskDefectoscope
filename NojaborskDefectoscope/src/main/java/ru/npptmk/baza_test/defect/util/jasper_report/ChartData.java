/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.baza_test.defect.util.jasper_report;

/**
 *
 * @author RazumnovAA
 */
public class ChartData {

    private String serie;
    private Double category;
    private Double value;

    public ChartData(String serie, Double category, Double value) {
        this.serie = serie;
        this.category = category;
        this.value = value;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Double getCategory() {
        return category;
    }

    public void setCategory(Double category) {
        this.category = category;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }
    
    
}

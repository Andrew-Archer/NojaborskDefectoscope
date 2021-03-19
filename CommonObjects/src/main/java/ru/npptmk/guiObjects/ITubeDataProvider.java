/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

/**
 * Предназначен для передачи от источника денных средству отображения
 * общих сведений о текущей трубе.
 * 
 * @author SmorkalovAV
 */
public interface ITubeDataProvider extends IScanDataProvider {
    /**Возвращает длину трубы, мм
     * @return текущая длина трубы, мм Если труба на данный момент не известна -
     * возвращается 0.
     */
    public int getTubeLength();
    /**
     * Возвращает строковое обозначение типа трубы.
     * @return Тип трубы в виде строки.
     */
    public String getTubeType();
    /**
     * Возвращает массив толщин труб для различных класов.<br>
     * Толщины упорядочены в порядке убывания. Первым эоементом в массиве
     * идет номинальная толщина трубы, последним - граница отбраковки
     * по толщине.
     * @return Массив толщин или {@code null}, если данные отсутствуют.
     */
    public float[] getTubeThicks();
    /**Возвращает координаты дефектов дпнного класса. дефекты в виде.
     * @param ch Идентификатор класса дефектов.
     * @return Массив координат дефектов данного класса или {@code null},
     * если данные отсутствуют.
     */
    public float[] getDefects(int ch);
}

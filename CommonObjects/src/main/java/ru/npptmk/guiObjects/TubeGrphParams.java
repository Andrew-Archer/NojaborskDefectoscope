/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.guiObjects;

import java.awt.Color;
import java.io.Serializable;

/**
 * Объект предназначен для сохранения в базе данных информации о
 * панельке, рисующей схему трубы с линиями дефектов. В частности
 * информацию о цвете линий для различных видов дефектов (магнитка,
 * толщинометрия, УЗК).<br> 
 * Код вида дефекта используем из {@link Defect}.
 * @author SmorkalovAV
 */
public class TubeGrphParams implements Serializable {
    public Color[] clrs;     // Набор цветов для отображения каналов дефектов.
    public String[] names;   // Наименования каналов дефектов.
    public long [] devIds;   // Идетнификаторы источников данных
    public int[] cahsnIds;  // Идентификаторы каналов данных.
    /**
     * Конструктор параметров.<br>
     * Создает объект с полным списком параметров.
     * @param clrs массив цветов для каналов дефектов. Должен содержать
     * элменты для всех идентификаторов каналов. Идентификатор канала
     * являеся индексом цвета в массиве.
     * @param names наименования каналов дефектов. Должен иметь тот же
     * размер, что и {@code clrs}.
     * @param devIds индентификаторы источников данных о метках.
     * @param cahsnIds идентификаторы каналов меток для указанных
     * устройств.
     */
    public TubeGrphParams(Color[] clrs, String[] names, long[] devIds, int[] cahsnIds){
        this.clrs = clrs;
        this.names = names;
        this.devIds = devIds;
        this.cahsnIds = cahsnIds;
    }

 }

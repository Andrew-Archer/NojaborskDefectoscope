/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.sortoscope.model;

import java.util.Arrays;

/**
 * Класс для работы с результатами измерений сортоскопа.
 * Класс конструируется из пототка <tt>int</tt>.
 * @author RazumnovAA
 */
public class SortoscopeRsults {
    /***
     * Номер трубы кторой соответствуют данные результаты измерений.
     */
    private int tubeNumber;
    
    /**
     * Количество значений полученных в результате измерений.
     */
    private int measurementsNumber;
    
    /**
     * Наименьшее значение из результатов измерений.
     */
    private int minValue;
    
    /**
     * Наиболшее значение из результатов измерений.
     */
    private int maxValue;
    
    /**
     * Среднее значение полученное из текущих результатов измерений.
     */
    private int averageValue;
    
    /**
     * Частота задаваемая для получения результатов измерений.
     */
    private int frequency;
    
    /**
     * Значение соостветствующее остутствию трубы.
     */
    private int noTubeValue;
    
    /**
     * Список пороговых значений,
     * установленных для каждой группы прочности при получении
     * текущих результатов (Установленные настройки соротоскопа). 
     */
    private final DurabilityGroupsSignals durabilityGroupsSignals;
    
    /**
     * Массив значений полученных сортоскопом.
     */
    private final Short[] value;
    
    /**
     * Конструктор создающий объект из потока <tt>int</tt>.
     * @param sortoscopeResultsArray
     * @throws WrongLengthOfResultDataException
     */
    public SortoscopeRsults(Short[] sortoscopeResultsArray) throws WrongLengthOfResultDataException{
        //Проверяем, что размер блока данных верный.
        if (sortoscopeResultsArray.length != 2013){
            throw new WrongLengthOfResultDataException(sortoscopeResultsArray.length);
        }
        
        tubeNumber = sortoscopeResultsArray[0];
        
        measurementsNumber = sortoscopeResultsArray[1];
        
        minValue = sortoscopeResultsArray[2];
        
        maxValue = sortoscopeResultsArray[3];
        
        averageValue = sortoscopeResultsArray[4];
        
        frequency = sortoscopeResultsArray[5];
        
        noTubeValue = sortoscopeResultsArray[6];
        
        durabilityGroupsSignals = new DurabilityGroupsSignals(sortoscopeResultsArray);
        
        //Получаем список значений измерений.
        value = Arrays.copyOfRange(sortoscopeResultsArray, 13, 2012);  
    }
}

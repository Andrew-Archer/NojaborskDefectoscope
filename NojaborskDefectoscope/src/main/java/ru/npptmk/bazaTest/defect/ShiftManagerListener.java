/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

/**
 *  Слушатель менеджера смен который будет уведомляться об изменении
 * состояния смены контроллируемой менеджером.
 * 
 * @author RazumnovAA
 */
public interface ShiftManagerListener {
    /**
     * Метод вызываемый у слушателя измений состояния смены.
     * 
     * @param shift смена состояние которой было изменено.
     */
    public void doThingsOnShiftChaned(Shift shift);
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.util.List;

/**
 * Управляет рабочей сменой.
 *
 * @author RazumnovAA
 */
public interface ShiftManager {
    public void addListener(ShiftManagerListener listener);
    
    public void addListeners(List<ShiftManagerListener> listeners);
    
    public void removeListener(ShiftManagerListener listener);
    
    public void removeListeners(List<ShiftManagerListener> listeners);

    /**
     * Начинает смену.
     * <P>
     * Если текущая смена не была начата, то устанавливает текущее время как
     * время начала смены.
     * <P>
     * Если текущая смена была начата но не была закрыта, то выбрасывает
     * исключение.
     * <P>
     * Если текущая смена закрыта, то создает новую и начинает ее.
     *
     * @throws Exception связанное с тем что вы пытаетесь начать начатую смену.
     */
    public void startShift() throws Exception;
    
    /**
     * Закрывает текущую смену.
     * <P>
     * Если текущая смена открыта, то менеджер её закрывает, устанавливая
     * текущую дату как время завершения смены.
     * <P>
     * Если текущая смена не открыта, то выбрасывает исключение.
     *
     * @throws Exception связанное с тем, что текущая начатая смена отсутствует.
     */
    public void endShift() throws Exception;

    /**
     * Возвращает текущую мену с которой работает менеджер.
     *
     * @return смена с которой работает менеджер.
     */
    public Shift getShift();
    
    public ShiftState getState();
    
    public enum ShiftState{
        /**
         * Смена не начата getBeginning() == null.
         */
        SHIFT_IS_NOT_STARTED,
        /**
         * Смена идет getBeginning() != null и getFinish() == null.
         */
        SHIFT_IS_RUNNING,
        /**
         * Смена закончена getFininsh() == null.
         */
        SHIFT_IS_FINISHED,
        
        /**
         * Неопределенное состояние смены.
         */
        SHIFT_IS_IN_A_WRONG_STATE
    }
    
    

}

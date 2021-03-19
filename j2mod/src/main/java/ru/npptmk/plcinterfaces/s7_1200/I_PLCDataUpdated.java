/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.plcinterfaces.s7_1200;

/**
 * Интерфейс обработчика события змнения данных регистра контроллера.<br>
 * Используется для организауии обработки нового значения указанного регистра.
 * @author MalginAS
 */
public interface I_PLCDataUpdated {
    /**
     * Обработка факта изменения значения регистра.
     */
    public void newData();
}

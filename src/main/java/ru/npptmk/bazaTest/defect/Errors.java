/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

/**
 * Флаги ошибок режима работы установки УНКТ-500.
 *
 * @author RazumnovAA
 */
public enum Errors {
    /**
     * Необходимо перевести установку в наладочный режим.
     */
    SWITCH_TO_MANUAL_MODE,
    /**
     * 1 прижим не в поднятом положении.
     */
    FIX1_IS_NOT_UP,
    /**
     * 2 прижим не в поднятом положении.
     */
    FIX2_IS_NOT_UP,
    /**
     * 3 прижим не в поднятом положении.
     */
    FIX3_IS_NOT_UP,
    /**
     * Входной перекладчик неправильное положение.
     */
    IN_REALOADER_WRONG_STATE,
    /**
     * Выходной перекладчик неправильное положение.
     */
    OUT_RELOADER_WRONG_STATE,
    /**
     * Сработало тепловое реле моторов рольганга.
     */
    THERMAL_RELAY_ROLLGANG_MOTORS,
    /**
     * Сработало тепловое реле насосов.
     */
    THERMAL_RELAY_PUMPS
}

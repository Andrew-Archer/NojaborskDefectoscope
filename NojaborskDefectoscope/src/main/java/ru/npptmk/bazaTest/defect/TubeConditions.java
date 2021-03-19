/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

/**
 *  Список состояний трубы.
 * 
 * @author RazumnovAA
 */
public interface TubeConditions {
    /**
     * Брак.
     */
    int BAD = 0;
    /**
     * Годная.
     */
    int GOOD = 1;
    /**
     * Не проверена.
     */
    int NOT_CHECKED = 2; 
    /**
     * Годная класс 2
     */
    int GOOD_CLASS_2 = 3;
    /**
     * Ремонтная класс 2
     */
    int GOOD_REAPAIR_CLASS_2 = 4;
}

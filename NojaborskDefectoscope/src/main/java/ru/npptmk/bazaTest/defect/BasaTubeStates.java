/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

/**
 *
 * @author RazumnovAA
 */
public class BasaTubeStates {
    /**
     * Труба не проверена.
     */
    public static int UNKNOWN = 1;
    
    /**
     * Труба имеет дефекты.
     */
    public static int BAD = 2;
    
    /**
     * Труба не имеет дефектов.
     */
    public static int GOOD;
}

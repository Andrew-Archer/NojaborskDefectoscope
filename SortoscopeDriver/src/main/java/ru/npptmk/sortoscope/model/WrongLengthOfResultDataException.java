/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.sortoscope.model;

/**
 * Класс для идикации ошибки неверной длинны блока данных полученных от
 * соротоскопа. Используется в {@link SortoscopeRsults#SortoscopeRsults(int[])
 * SortoscopeRsults.SortoscopeRsults(int[])}.
 *
 * @author RazumnovAA
 */
public class WrongLengthOfResultDataException extends Exception {

    /**
     * Конструктор по умолчанию. Этот конструктор дает значение фактической
     * длины блока данных полученных от сортоскопа.
     */
    public WrongLengthOfResultDataException() {
        super("Блок данных полученных от соротоскопа имеет неверную длину.");
    }

    /**
     * Конструктор сообщающей о фактической длине блока дынных полученных от
     * соротоскопа.
     *
     * @param wordsNumber число слов в массиве возвращенных данных.
     */
    public WrongLengthOfResultDataException(int wordsNumber) {
        super("Ожидаемый размер блока данных 2013 слов, а получено " + 
                String.valueOf(wordsNumber) + " слов.");
    }
}

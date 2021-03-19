/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.sortoscope.main;

/**
 * Исключение выбрасываемое в случае если
 * ответ сортоскопа не содержит результатов замера, так како ни ещё не готовы.
 * 
 * @author RazumnovAA
 */
public class NoMeasurementsReadyException extends Exception {
    
    /**
     * Содежит сообщение по умочанию.
     */
    public NoMeasurementsReadyException(){
        super("В сортоскопе нет готовых для обработки данных.");
    }
    
    /**
     * Конструтор в котором можно указать выводимое
     * исключением сообщение.
     *
     * @param message свободнозадаваемое сообщение для исключения.
     */
    public NoMeasurementsReadyException(String message){
        super(message);
    }
}

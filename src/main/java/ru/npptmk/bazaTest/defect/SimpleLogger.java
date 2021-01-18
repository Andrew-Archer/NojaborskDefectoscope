/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Простой класс для записи в лог.</p>
 * <p>
 * ИСПОЛЬЗОВАНИЕ: Объект класса следует создавать при запуске приложения и затем
 * вызывать его методы.</p>
 *
 * @author RazumnovAA
 */
public class SimpleLogger {

    public interface SimpleLoggerListener {

        /**
         * Метод, вызываемый у слушателей при изменении состояния ведения
         * журнала.
         *
         * @param loggingIsEnabled если true то журнал ведется, и не ведется,
         * если false.
         */
        public void processStateUpdatedSignal(boolean loggingIsEnabled);
    }

    private boolean loggingIsEnabled;
    private final SimpleDateFormat dateFormater;
    private List<SimpleLoggerListener> listeners;

    public SimpleLogger() {
        dateFormater = new SimpleDateFormat("dd.MM.yy HH:mm:ss.SSS");
        listeners = new ArrayList<>();
    }

    /**
     * Конструктор позволяет указать слушателя при создании объекта.
     *
     * @param listener Объект который необходимо уведомлять о включение
     * выключении записи в журнал.
     */
    public SimpleLogger(SimpleLoggerListener listener) {
        this();
        listeners.add(listener);
    }

    private void notifyListenersOfLoggingState() {
        listeners.forEach((listener) -> {
            listener.processStateUpdatedSignal(loggingIsEnabled);
        });
    }

    /**
     * Делает запись в лог с указанием времени и сообщения, в случае если запись
     * отладочной информации включена.
     *
     * @param logmessage сообщение для записи в журнал.
     */
    public void log(String logmessage) {
        if (this.loggingIsEnabled) {
            System.out.println(dateFormater.format(new Date()) + "|> " + logmessage);
        }
    }

    /**
     * Делает запись в лог с указанием времени и сообщения по принципу
     * String.format(String, Object...), в случае если запись отладочной
     * информации включена.
     *
     * @param logmessage сообщение для записи в журнал.
     */
    public void log(String message, Object... params) {
        log(String.format(message, params));
    }

    /**
     * Делает запись в лог независимо о том включена запись в журнал или
     * отключена.
     *
     * @param ex исключение для записи в журнал.
     */
    public void log(Exception ex) {
        System.out.println(dateFormater.format(new Date()) + "|> " + "Ошибка: " + ex.toString());
        System.out.println("Причина: " + ex.getCause());
        System.out.println("Сообщение: " + ex.getMessage());
        ex.printStackTrace();
    }

    /**
     * Включает запись отладочной информации в журнал. Вызывает уведомление всех
     * слушателей об изменившимся состоянии.
     */
    public void enableLogging() {
        loggingIsEnabled = true;
        notifyListenersOfLoggingState();
    }

    /**
     * Выключает запись отладочной информации в журнал. Вызывает уведомление
     * всех слушателей об изменившимся состоянии.
     */
    public void disableLogging() {
        loggingIsEnabled = false;
        notifyListenersOfLoggingState();
    }

    /**
     * Позволяет понять ведется ли запись отладочной информации в журнал.
     *
     * @return Возвращает true если запись в журнал включена, иначе false.
     */
    public boolean isLoggingEnabled() {
        return loggingIsEnabled;
    }
}

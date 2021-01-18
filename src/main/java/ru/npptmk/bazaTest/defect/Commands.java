/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

/**
 * Содержит команды для контроллерной программы.
 *
 * @author RazumnovAA
 */
public class Commands {

    /**
     * Отсутствие команд.
     */
    public static final int NO_COMMANDS = 0;

    /**
     * Наличие трубы на установке, по учету транспортного менеджера.
     */
    public static final int TUBE_IS_ON_THE_DEVICE = 1;
    /**
     * Наличие образца на установке, по учету транспортного менеджера.
     */
    public static final int SAMPLE_IS_ON_THE_DEVICE = 2;
    /**
     * На установке нет ни трубы, ни образца.
     */
    public static final int NOTHING_IS_ON_THE_DEVICE = 3;
    /**
     * Создана новая труба и зарегистрирована в транспортном менеджере и базе
     * данных.
     */
    public static final int NEW_TUBE_IS_REGISTRED = 4;
    /**
     * Результаты дефектоскопии сохранены в базу данных.
     */
    public static final int DEFECTS_DETECTION_RESULTS_ARE_SAVED = 5;
    /**
     * Оператор решил пометить трубу годной.
     */
    public static final int MARK_AS_GOOD = 6;
    /**
     * Оператор решил пометить трубу как брак.
     */
    public static final int MARK_AS_BAD = 7;
    /**
     * Оператор решил повторить поиск дефектов.
     */
    public static final int REPEAT_DEFECT_DETECTION = 8;
    /**
     * По учёту транспортного менеджера труба переведена в архив.
     */
    public static final int TUBE_IS_IN_ARCHIVE = 9;
    /**
     * Означает, что смена начата, выбран производитель и оператор.
     */
    public static final int SHIFT_IS_RUNNING_PROPERLY = 10;
    /**
     * Оператор решил проконтролировать трубу как рабочую трубу.
     */
    public static final int CONTROL_AS_TUBE = 11;
    /**
     * Оператор решил проконтролировать найденную на установке трубу как
     * образец.
     */
    public static final int CONTROL_AS_SAMPLE = 12;
    /**
     * Оператор решил убрать с установки обнаруженную на ней трубу.
     */
    public static final int TAKE_TUBE_OUT = 13;
    /**
     * Новый образец зарегистрирован в транспортном менеджере и системе учета.
     */
    public static final int NEW_SAMPLE_IS_REGISTRED = 14;

}

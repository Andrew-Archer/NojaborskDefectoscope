/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

/**
 * Список этапов техпроцесса дефектоскопии.
 *
 * @author RazumnovAA
 */
public class States {

    /**
     * Состояние 0 Ожидание начала работы.
     */
    public static final int STATE_0 = 0;
    /**
     * Состояние 1 Проверка видимости трубы датчиками.
     */
    public static final int STATE_1 = 1;
    /**
     * Состояние 2 Поиск трубы.
     */
    public static final int STATE_2 = 2;
    /**
     * Состояние 3 Ожидание подтверждения отсутствия трубы.
     */
    public static final int STATE_3 = 3;
    /**
     * Состояние 4 Ожидание трубы на входном перекладчике.
     */
    public static final int STATE_4 = 4;
    /**
     * Состояние 5 Загрузка трубы на установку.
     */
    public static final int STATE_5 = 5;
    /**
     * Состояние 6 Ожидание регистрации новой трубы.
     */
    public static final int STATE_6 = 6;
    /**
     * Состояние 7 Отвод трубы в начало рольганга.
     */
    public static final int STATE_7 = 7;
    /**
     * Состояние 8 Подвод трубы к началу участка калибровки измерителя длины.
     */
    public static final int STATE_8 = 8;
    /**
     * Состояние 9 Опускание первого прижима и возврат трубы за датчик начала
     * калибровки.
     */
    public static final int STATE_9 = 9;
    /**
     * Состояние 10 Калибровка 1 измерителя длины.
     */
    public static final int STATE_10 = 10;
    /**
     * Состояние 11 Движение трубы до начала участка магнитной дефектоскопии.
     */
    public static final int STATE_11 = 11;
    /**
     * Состояние 12 Сведение датчиков магнитной дефектоскопии.
     */
    public static final int STATE_12 = 12;
    /**
     * Состояние 13 Магнитная дефектоскопия, измерение координаты 1 лиром.
     */
    public static final int STATE_13 = 13;
    /**
     * Состояние 14 Магнитная дефектоскопия, калибровка 2 лира по первому.
     */
    public static final int STATE_14 = 14;
    /**
     * Состояние 15 Подъем каретки УЗК и заполнение ее водой.
     */
    public static final int STATE_15 = 15;
    /**
     * Состояние 16 Магнитная дефектоскопия, совместно с УЗК.
     */
    public static final int STATE_16 = 16;
    /**
     * Состояние 17 Магнитная дефектоскопия, совместно с УЗК при использовании
     * трех прижимов.
     */
    public static final int STATE_17 = 17;
    /**
     * Состояние 18 Магнитная дефектоскопия, совместно с УЗК при использовании
     * второго и третьего прижима.
     */
    public static final int STATE_18 = 18;
    /**
     * Состояние 19 УЗК без магнитной дефектоскопии с использованием второго и
     * третьего прижимов.
     */
    public static final int STATE_19 = 19;
    /**
     * Состояние 20 УЗК без магнитной дефектоскопии с использованием третьего
     * прижима.
     */
    public static final int STATE_20 = 20;
    /**
     * Состояние 21 Вывод трубы из установки с 3 прижимом.
     */
    public static final int STATE_21 = 21;
    /**
     * Состояние 22 Ожидаем выхода трубы из установки.
     */
    public static final int STATE_22 = 22;
    /**
     * Состояние 23 Отвод трубы на позицию выгрузки и получение результатов от
     * сортоскопа.
     */
    public static final int STATE_23 = 23;
    /**
     * Состояние 24 Регистрация результатов.
     */
    public static final int STATE_24 = 24;
    /**
     * Состояние 25 Ожидание принятия решения оператора.
     */
    public static final int STATE_25 = 25;
    /**
     * Состояние 26 Выгрузить трубу.
     */
    public static final int STATE_26 = 26;
    /**
     * Состояние 27 Отправка трубы в архив.
     */
    public static final int STATE_27 = 27;
    /**
     * Состояние 28 Ожидание подтверждения наличия трубы на установке.
     */
    public static final int STATE_28 = 28;
    /**
     * Состояние 29 Ожидание решения оператора по поводу тубы.
     */
    public static final int STATE_29 = 29;
    /**
     * Состояние 30 Ожидание регистрации нового образца.
     */
    public static final int STATE_30 = 30;
    /**
     * Состояние 34 Ожидание начала смены.
     */
    public static final int STATE_31 = 31;
}

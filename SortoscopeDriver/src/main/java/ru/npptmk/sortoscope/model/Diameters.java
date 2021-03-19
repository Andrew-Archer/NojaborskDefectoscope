/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.sortoscope.model;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс содержит статичесткие вспомогательные методы для работы с
 * {@link Diameters} такие как конвертация из массива <tt>short</tt>.
 *
 * @author RazumnovAA
 */
public class Diameters {

    /**
     * Позволяет преобразовать значение в мм в {@link DiametersValues}.
     *
     * @param diameterInmm
     * @return
     */
    public static DiametersValues mmToDiametersValues(short diameterInmm) {
        switch (diameterInmm) {
            case 60:
                return DiametersValues.D60мм;
            case 73:
                return DiametersValues.D73мм;
            case 89:
                return DiametersValues.D89мм;
            case 102:
                return DiametersValues.D102мм;
            case 114:
                return DiametersValues.D114мм;
            default:
                return null;
        }
    }
        /**
     * Позволяет преобразовать значение в мм в {@link DiametersValues}.
     *
     * @param diameterInmm
     * @return
     */
    public static DiametersValues mmToDiametersValues(Float diameterInmm) {
        switch ((short)Math.round(diameterInmm)) {
            case 60:
                return DiametersValues.D60мм;
            case 73:
                return DiametersValues.D73мм;
            case 89:
                return DiametersValues.D89мм;
            case 102:
                return DiametersValues.D102мм;
            case 114:
                return DiametersValues.D114мм;
            default:
                return null;
        }
    }
    /**
     * Преобразует лист диаметров в массив <tt>short</tt>. Данный массив
     * сортоскоп понимает как данные для комманды установки параметров.
     *
     * @param diameters Список диаметров из которых только 1 имеет значение.
     * @return массив ктороый сортоскоп понимает как данные для комманды
     * установки параметров.
     */
    public static short[] getAsArrayOfShort(List<Diameter> diameters) {
        //Длина возвращаемого массива
        //При 6 группах прочности и 5 диаметрах
        int returnLength = 41;

        //Возвращаемый массив
        short[] result = new short[returnLength];

        //Ищем диаметр который должен быть установлен как текущий
        //и записываем его номер.
        for (Diameter diameter : diameters) {
            if (diameter.isCurrent()) {
                result[0] = (short) (diameter.getDiameter().ordinal());
                break;
            }
        }

        //Если ни один из диаметров не установлени как текущий
        if (result[0] < 0 && result[0] > 4) {
            result[0] = 0;
            Logger.getGlobal().log(
                    Level.INFO,
                    "Не один диаметр не бы установлен как текущие настройки "
                    + "сортоскопа.\n"
                    + "Первый диаметр будет установлен как текущие "
                    + "настройки сортоскопа.");
        }

        //Записываем чатсоты измерения для каждого диаметра
        for (int i = 0; i < diameters.size(); i++) {
            result[i + 1] = diameters.get(i).getMesurmentFrequency();
        }

        //Записываем пороги наличия изделия
        for (int i = 0; i < diameters.size(); i++) {
            result[i + diameters.size() + 1] = diameters.get(i).getEmptySignal();
        }

        //Записываем пороги для групп прочности для каждого диаметра.
        for (int i = 0; i < diameters.size(); i++) {
            for (int j = 0; j < 6; j++) {
                //11 short уже заняты предыдущими данными.
                //Пишем начиная с 12-го short.
                result[i * 6 + 11+ j] = diameters
                        .get(i)
                        .getDurabilityGroupsSignals()
                        .getGroupsSignalsMap()
                        .get(DurabilityGroups.values()[j]);
            }
        }
        return result;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.sortoscope.main;

import java.io.IOException;
import ru.npptmk.sortoscope.model.Diameter;
import ru.npptmk.sortoscope.model.DurabilityGroups;

/**
 * Интерфейс для работы с сортоскопом. Служит для: передачи настроек считывания
 * настроек получения результатов.
 *
 * @author RazumnovAA
 */
public interface SortoscopeDriver {

    /**
     * Позволяет получить текущие настройки для первого из 5 ти диаметров.
     *
     * @return
     */
    public Diameter getDiameterParameters();

    /**
     * Устанавливает текущий деаметр трубы и параметры для её проверки.
     *
     * @param diameter
     * @throws java.io.IOException
     */
    public void setDiameterParameters(Diameter diameter) throws IOException;

    /**
     * Получить группу прочности по результатам последних замеров.
     *
     * @return группа прочности на основании результатов замеров сортоскопа.
     * @throws NoMeasurementsReadyException исключение выбрасывается когда от
     * сортоскопа не приходит данных готовых для обработки.
     */
    public DurabilityGroups getDurabilityGroup() throws NoMeasurementsReadyException;

    /**
     * Получить внутренние данные соротоскопа. К внутренним данным относятся
     * такие данные как версия прошивки.
     */
    public void getInternalData();
}

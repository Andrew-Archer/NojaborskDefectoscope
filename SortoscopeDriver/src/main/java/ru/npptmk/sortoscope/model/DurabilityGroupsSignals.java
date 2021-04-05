package ru.npptmk.sortoscope.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс предназначен для хранения сигналов сортоскопа соответстсующих группам
 * прочности.
 *
 * @author RazumnovAA
 */
public class DurabilityGroupsSignals implements Serializable {

    /**
     * Карта для хранения состветствия сигналов группам прочности труб.
     */
    private final Map<DurabilityGroups, Short> groupsSignalsMap;

    /**
     * Конструктор создает карту "группа проности - сигнал" заполняя значения
     * сигналов 0. В дальнейшем используйте метод {@link #updateSignalForDurabilityGroup(
     * DurabilityGroups, Short) updateSignalForDurabilityGroup(DurabilityGroups, Short)}
     * чтобы задать правльные значения сигналов. Для получения значения сигнала,
     * соостветствующего группе прочности используйте метод
     * {@link #getSignalByDurabilityGroup(DurabilityGroups) 
     * getSignalByDurabilityGroup(DurabilityGroups)}
     */
    public DurabilityGroupsSignals() {
        groupsSignalsMap = new HashMap<>();
        for (DurabilityGroups durabilityGroup : DurabilityGroups.values()) {
            groupsSignalsMap.put(durabilityGroup, (short)0);
        }
    }

    /**
     * Конструктор создает карту "группа проности - сигнал" на основании
     * значений массива sortoscopeResultsArray с 7 по 12 включительно.
     *
     * @param sortoscopeResultsArray массив для создания объекта.
     */
    DurabilityGroupsSignals(Short[] sortoscopeResultsArray) {
        //Создаем карту данных
        groupsSignalsMap = new HashMap<>();

        //Заполняем внутреннюю карту данных.
        groupsSignalsMap.put(DurabilityGroups.Д, sortoscopeResultsArray[7]);
        groupsSignalsMap.put(DurabilityGroups.К, sortoscopeResultsArray[8]);
        groupsSignalsMap.put(DurabilityGroups.Е, sortoscopeResultsArray[9]);
        groupsSignalsMap.put(DurabilityGroups.Л, sortoscopeResultsArray[10]);
        groupsSignalsMap.put(DurabilityGroups.М, sortoscopeResultsArray[11]);
        groupsSignalsMap.put(DurabilityGroups.Р, sortoscopeResultsArray[12]);
    }

    /**
     * Возвращает карту "группа прочности трубы - сигнал сортоскопа" для
     * дальнейшей работы с ней.
     *
     * @return возвращаемая карта "группа прочности - сигнал".
     */
    public Map<DurabilityGroups, Short> getGroupsSignalsMap() {
        return groupsSignalsMap;
    }
}

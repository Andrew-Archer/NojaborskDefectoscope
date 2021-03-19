/*
 * Free for charge.
 */
package ru.npptmk.sortoscope.model;

import java.io.Serializable;

/**
 * Класс диаметра трубы проверяемой на сортоскопе.
 *
 * @author RazumnovAA
 */
public final class Diameter implements Serializable {

    private final static long serialVersionUID = 7790151182535416175L;
    /**
     * Даметр в милиметрах.
     */
    private short diameterInmm;

    /**
     * Значение измерений соостветствующее отсутствию трубы.
     */
    private short noTubeSignal;

    /**
     * Одно из значений доступных диаметров. Для исключчения ошибки при вводе, а
     * также для однозначного понимания списка доступных диаметров.
     */
    private DiametersValues diameter;

    /**
     * Карта значений соостветствующих классу прочности.
     */
    private final DurabilityGroupsSignals durabilityGroupsSignals;

    /**
     * Частота для проведения замеров.
     */
    private Short mesurmentFrequency;

    /**
     * Индикатор того, что этот диаметр используется как текущая настройка
     * сортоскопа.
     */
    private boolean current;

    /**
     * Создает полностью инициализированный класс. При создании все параметры
     * задются стандартными для выбранного при вызове конструктора диаметра.
     *
     * @param diameter выбранный для создания параметров диаметр. Определяет
     * какие настройки по умочанию вы получите.
     */
    public Diameter(DiametersValues diameter) {
        //Создаем новый пустой диаметр

        setNoTubeSignal((short) -3000);
        durabilityGroupsSignals = new DurabilityGroupsSignals();
        setMesurmentFrequency((short) -16);
        switch (diameter) {
            case D60мм:
                setDiameter(DiametersValues.D60мм);
                setDurabilityGropSignalValue(DurabilityGroups.Д, (short) -373);
                setDurabilityGropSignalValue(DurabilityGroups.Е, (short) -472);
                setDurabilityGropSignalValue(DurabilityGroups.К, (short) -808);
                setDurabilityGropSignalValue(DurabilityGroups.Л, (short) -1027);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -1151);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -1606);
                break;
            case D73мм:
                setDiameter(DiametersValues.D73мм);
                setDurabilityGropSignalValue(DurabilityGroups.Д, (short) 14);
                setDurabilityGropSignalValue(DurabilityGroups.Е, (short) -92);
                setDurabilityGropSignalValue(DurabilityGroups.К, (short) -453);
                setDurabilityGropSignalValue(DurabilityGroups.Л, (short) -688);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -820);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -1309);
                break;
            case D89мм:
                setDiameter(DiametersValues.D89мм);
                setDurabilityGropSignalValue(DurabilityGroups.Д, (short) 563);
                setDurabilityGropSignalValue(DurabilityGroups.Е, (short) 469);
                setDurabilityGropSignalValue(DurabilityGroups.К, (short) 147);
                setDurabilityGropSignalValue(DurabilityGroups.Л, (short) -63);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -181);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -618);
                break;
            case D102мм:
                setDiameter(DiametersValues.D102мм);
                setDurabilityGropSignalValue(DurabilityGroups.Д, (short) 877);
                setDurabilityGropSignalValue(DurabilityGroups.Е, (short) 755);
                setDurabilityGropSignalValue(DurabilityGroups.К, (short) 262);
                setDurabilityGropSignalValue(DurabilityGroups.Л, (short) -2);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -116);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -640);
                break;
            case D114мм:
                setDiameter(DiametersValues.D114мм);
                setDurabilityGropSignalValue(DurabilityGroups.Д, (short) 1902);
                setDurabilityGropSignalValue(DurabilityGroups.Е, (short) 1731);
                setDurabilityGropSignalValue(DurabilityGroups.К, (short) 1056);
                setDurabilityGropSignalValue(DurabilityGroups.Л, (short) 702);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) 541);
                setDurabilityGropSignalValue(DurabilityGroups.М, (short) -208);
                break;
        }
    }

    /**
     * Конструктор для совместимости с JavaBeans.
     */
    public Diameter() {
        durabilityGroupsSignals = new DurabilityGroupsSignals();
        mesurmentFrequency = 0;
    }

    /**
     * Конструктор позволяющий создать класс диаметра трубы без заполнения
     * сиганлов соостветствующих классам прочночти. Чтобы изменять
     *
     * @param diameter диаметр для которого определяются параметры сигналов и
     * частота сигнала для замера.
     * @param mesurmentFrequency частота сигнала с которым проводят замеры.
     * @deprecated Не используйте этот конструктор, так как он создает не
     * полностью инициализированный объект. Используйте вместо него
     * {@link #Diameter(ru.npptmk.sortoscope.model.DiametersValues)}.
     */
    @Deprecated
    public Diameter(DiametersValues diameter, Short mesurmentFrequency) {
        this.diameter = diameter;
        this.mesurmentFrequency = mesurmentFrequency;
        this.durabilityGroupsSignals = new DurabilityGroupsSignals();
    }

    public short getNoTubeSignal() {
        return noTubeSignal;
    }

    public final void setNoTubeSignal(short noTubeSignal) {
        this.noTubeSignal = noTubeSignal;
    }

    /**
     * Предназвначен для выявления факта заполнения всех параметров 0.
     *
     * @return true если все параметры 0.
     */
    public boolean isAllFieldZero() {
        if (diameter == null
                && diameterInmm == 0
                && mesurmentFrequency == 0
                && noTubeSignal == 0
                && durabilityGroupsSignals.getGroupsSignalsMap().get(DurabilityGroups.Д) == 0
                && durabilityGroupsSignals.getGroupsSignalsMap().get(DurabilityGroups.Е) == 0
                && durabilityGroupsSignals.getGroupsSignalsMap().get(DurabilityGroups.К) == 0
                && durabilityGroupsSignals.getGroupsSignalsMap().get(DurabilityGroups.Л) == 0
                && durabilityGroupsSignals.getGroupsSignalsMap().get(DurabilityGroups.М) == 0
                && durabilityGroupsSignals.getGroupsSignalsMap().get(DurabilityGroups.Р) == 0) {
            return true;
        }else{
            return false;
        }
    }

    /**
     * Позволяет установить, что данный диаметр является текущей настройкой
     * сортоскопа.
     *
     * @return true - если является текущей настройкойсортоскопа. false - если
     * не является текущей настройкой сортоскопа.
     */
    public boolean isCurrent() {
        return current;
    }

    /**
     * Не используйте этот метод, потомучто драйвер все равно правильно устновит
     * значение поля current перед отправкой в сортоскоп, не зависимо от того,
     * что вы установите.
     *
     * @param current true - если надо сделать текущей настройкой сортоскопа,
     * false - если не надо делать текущей настройкой.
     */
    public void setCurrent(boolean current) {
        this.current = current;
    }

    /**
     * Возвращает сигнал на основании заданной группы прочности.
     *
     * @param durabilityGroup грппа прочности для которой надо вернуть значение
     * сигнала.
     * @return сигнал соответствующий заданной группе прочности.
     */
    public Short getSignalByDurabilityGroup(DurabilityGroups durabilityGroup) {
        return durabilityGroupsSignals.getGroupsSignalsMap().get(durabilityGroup);
    }

    /**
     * Обновляет значение сигнала для заданной группы прочности.
     *
     * @param durabilityGroup группа прочности для которой задается значение
     * сигнала.
     * @param signal значение сигнала обновляемое для заданной группы прочности.
     */
    public void updateSignalForDurabilityGroup(DurabilityGroups durabilityGroup,
            Short signal) {
        durabilityGroupsSignals.getGroupsSignalsMap().put(durabilityGroup, signal);
    }

    //Simple setter and getters blocks.=========================================
    /**
     * Возвращает значение диаметра в мм.
     *
     * @return возвращаемое значение диаметра в мм.
     */
    public DiametersValues getDiameter() {
        return diameter;
    }

    public short getDiameterInmm() {
        return diameterInmm;
    }

    /**
     * Задает значение диаметра выбором из доступного списка
     * {@link DiametersValues}. Также устанавливает соостветствующее значение
     * диметра в мм в переменную {@link diameterInmm}. Получить значение
     * диаметра в мм можно вызывом метода {@link getDiameterInmm()}.
     *
     * @param diameter задаваемое занчение диаметра из списка
     * {@link DiametersValues}.
     */
    public void setDiameter(DiametersValues diameter) {
        this.diameter = diameter;

        switch (diameter) {
            case D60мм:
                diameterInmm = 60;
                break;
            case D73мм:
                diameterInmm = 73;
                break;
            case D89мм:
                diameterInmm = 89;
                break;
            case D102мм:
                diameterInmm = 102;
                break;
            case D114мм:
                diameterInmm = 114;
        }
    }

    public Short getMesurmentFrequency() {
        return mesurmentFrequency;
    }

    public final void setMesurmentFrequency(Short mesurmentFrequency) {
        this.mesurmentFrequency = mesurmentFrequency;
    }

    public DurabilityGroupsSignals getDurabilityGroupsSignals() {
        return durabilityGroupsSignals;
    }

    /**
     * Записывает соответствующий прого для заданной группы прочности.
     *
     * @param durGroups заданная группа прочночти.
     * @param readShort значение порого которое необходимо присвоить заданной
     * группе прочности.
     */
    public void setDurabilityGropSignalValue(DurabilityGroups durGroups, short readShort) {
        durabilityGroupsSignals.getGroupsSignalsMap().put(durGroups, (short) (readShort * 1));
    }

    public short getEmptySignal() {
        return noTubeSignal;
    }
}

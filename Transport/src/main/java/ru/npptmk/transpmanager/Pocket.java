/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.transpmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Установка накопления деталей "карман".
 *
 * @author SerpokrylovDV
 */
public class Pocket extends Device {

    public final static int ERR_POS = TranspManager.SPEC_ERRS + 1;

    private final HashSet<LocationEnt> positions = new HashSet<>();
    int maxpos = 0;

    public Pocket(TranspManager tm) {
        super(tm);
    }

    /**
     * Добавление размещения детали на установку в конец коллекции.<br>
     * Параметр {@code position} в методе не исползуется.
     * @param pos сущность размещений.
     * @param position не используется.
     * @return false Если возникла ошибка, {@code TranspManager.getError()} возвращает код ошибки с описанием.
     */
    @Override
    public boolean addLocation(LocationEnt pos, int position) {
        //Добавить расположение в указанную позицию локальной коллекции расположений.
        //Использовать метод {@code TranspManager.updateLocation} для изменения данных в бд и 
        //передать в параметрах старое расположение, максимальное кол-во деталей(+1) и идентификатора установки. 
        //Вернуть true
        positions.add(pos);
        
        return tm.updateLocation(pos, maxpos++, ent.getIdDev());
    }

    /**
     * Метод удаления позиции детали с установки.<br>
     * Позциией по умолчанию {@code Device.DEFAULT_VALUE} принята 
     * позиция с номером 0.
     * @param pos позиция детали на установке.
     * @return Удаленное размещение, или null.
     */
    @Override
    public LocationEnt remLocation(int pos) {

        if (pos == Device.DEFAULT_VALUE) {
            pos = 0;
        }
        LocationEnt s;
        Iterator iter = positions.iterator();
        while (iter.hasNext()) {
            s = (LocationEnt) iter.next();
            if (s.getPosition() == pos) {
                positions.remove(s);
                return s;
            }
        }
        tm.setErr(Table.ERR_POS, "Не найдена позиция " + pos + " для удаления из кармана №" + getIdDev() + ".");
        s = null;
        return s;
    }

    /**
     * Метод обновления позиций на установках.
     *
     * @param dv сущность установок.
     * @return метод восстановления размещений.`
     */
    @Override
    public boolean refreshDev(DeviceEnt dv) {
        ent = dv;
        List<LocationEnt> ls = tm.getLocations(dv.getIdDev());
        //очищение коллекции размещений.
        positions.clear();
        //заполняем коллекцию новыми данными размещений.
        for (LocationEnt ps : ls) {
            positions.add(ps);
            if(maxpos < ps.getPosition()) maxpos = ps.getPosition();
        }
        maxpos++;
        return true;
    }

    /**
     * Метод возвращающий идентификатор детали находящейся в
     * заданной позиции.<br>
     * Позциией по умолчанию {@code Device.DEFAULT_VALUE} принята 
     * позиция с номером 0.
     * @param position позиция детали на установке.
     * @return 0 либо, возвращает идентификатор детали.
     */
    @Override
    public long getIdDet(int position) {
        LocationEnt loc;

        if (position == Device.DEFAULT_VALUE) {
            position = 0;
        }
        if (position >= maxpos) {
            tm.setErr(Pocket.ERR_POS, "Недопустимое значение позиции " + position + " в кармане №" + getIdDev() + ".");
            return 0;
        }
        Iterator iter = positions.iterator();
        while (iter.hasNext()) {
            loc = (LocationEnt) iter.next();
            if (loc == null) {
                continue;
            }
            if(loc.getPosition() == position){
                return loc.getIdDet();
            }
        }
        tm.setErr(Pocket.ERR_POS, "Недопустимое значение позиции " + position + " в кармане №" + getIdDev() + ".");
        return 0;
    }

    /**
     * Удаление размещений всех деталей с установки.<br>
     * Создается коллекция размещений типа ArrayList. Переписываются элементы из
     * коллекции HashSet в ArrayList. Удаляются все элементы из коллекции
     * {@code HashSet} размещений установки.
     *
     * @return Коллекция удаленных размещений.
     */
    @Override
    public List<LocationEnt> clearAllLoc() {

        ArrayList<LocationEnt> s = new ArrayList<>();
        Iterator iter = positions.iterator();
        while (iter.hasNext()) {
            s.add((LocationEnt) iter.next());
        }
        positions.clear();
        maxpos = 0;
        return s;
    }

    @Override
    public int getCountDetails() {
        return positions.size();
    }
}

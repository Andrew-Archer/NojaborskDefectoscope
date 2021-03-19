/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.transpmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Установка для накопления деталей типа "стеллажа".<br>
 * В локальной коллекции размещений индекс элемента соответствует
 * позиции детали на накопителе.<br>
 * "Позиция по умолчанию" - при удалении детали - нулевая позиция детали,
 * при добавлении это конец коллекции.
 * @author SmorkalovAV
 */
public class Table extends Device {
    /**Ошибка возникает при передаче неправильной позиции детали*/
    public final static int ERR_POS = TranspManager.SPEC_ERRS + 1;

    private final ArrayList<LocationEnt> positions = new ArrayList<>();

    public Table(TranspManager tm) {
        super(tm);
    }
    /**
     * Добавление расположения на установку в указанную позицию.<br>
     * Значение по умолчанию это позиция равная размеру коллекции, т.е.
     * деталь добавляется в конец коллекции.<br>
     * Если при добавлении детали необходимо изменить расположения
     * деталей на установке они изменяются.
     * @param pos Новоге расположение.
     * @param position Позиция нового расположения.
     * @return {@code  false) при неудачном завершении, описание ошибки можно
     * получить методом {@code TransManager.getError}.
     */
    @Override
    public boolean addLocation(LocationEnt pos, int position) {
        try {
            if (position == Device.DEFAULT_VALUE) {
                positions.add(positions.size(), pos);
            } else {
                positions.add(position, pos);
            }
        } catch (IndexOutOfBoundsException ex) {
            tm.setErr(Table.ERR_POS, "Недопустимая позиция " + position + " детали. Установка №" + getIdDev()+".");
            return false;
        }
        return restoreLocations();
    }
    /**
     * Метод перебирает коллекцию расположений установки.
     * Если идентификатор установки или позиция детали в расположении
     * не соответствует идентификатору этой установки или индексу в 
     * локальной коллекции расположений, то выполняется соответсвующие 
     * изменения в базе и коллекции.
     * 
     * @return {@code  false) при неудачном завершении, описание ошибки можно
     * получить методом {@code TransManager.getError}.
     */
    private boolean restoreLocations() {
        for (int i = 0; i < positions.size(); i++) {
            LocationEnt ps = positions.get(i);
            if (ps == null) {
                positions.remove(i--);
                continue;
            }
            if (ps.getIdDev() != ent.getIdDev()
                    || ps.getPosition() != i) {
                if (!tm.updateLocation(ps, i, ent.getIdDev())) {
                    return false;
                }
            }
        }
        return true;
    }
    /**
     * Удаление расположения детали из указанной позиции установки.<br>
     * Значение по умолчанию это нулевая позиция, т.е.
     * деталь удаляется с начала коллекции.<br>
     * Удалится и сущность расположения из локальной коллекции.
     * Если при удалении детали необходимо изменить расположения
     * деталей на установке они изменяются.
     * @param pos Позиция в которой находится удаляемое расположение.
     * @return Удаленное расположение при удачном завершении, иначе null.
     */
    @Override
    public LocationEnt remLocation(int pos) {
        LocationEnt ret;
        try {
            if (pos == Device.DEFAULT_VALUE) {
                ret = positions.get(0);
                positions.remove(0);
            } else {
                ret = positions.get(pos);
                positions.remove(pos);
            }
        } catch (IndexOutOfBoundsException ex) {
            tm.setErr(Table.ERR_POS, "Недопустимая позиция детали " + pos + " детали. Установка №" + getIdDev()+".");
            return null;
        }
        if (restoreLocations()) {
            return ret;
        }
        return null;
    }
    /**
     * Обновление коллекции расположений установки 
     * переданными значениями.
     * @param dv Сущность установки с коллекцией расположений.
     * @return {@code  false) при неудачном завершении, описание ошибки можно
     * получить методом {@code TransManager.getError}.
     */
    @Override
    public boolean refreshDev(DeviceEnt dv) {
        ent = dv;
        List<LocationEnt> ls = tm.getLocations(dv.getIdDev());
        positions.clear();
        for (LocationEnt ps : ls) {
            positions.add(ps);
        }
        return restoreLocations();
    }
    /**
     * Возвращает идентификатор детали.
     * @param position позиция расположения детали на установке.
     * @return Идентификатор детали из указанной позиции при удачном завершении,
     * иначе null.
     */     
    @Override
    public long getIdDet(int position) {
        LocationEnt lo;
        if (position == DEFAULT_VALUE) {
            position = 0;
        }
        if(positions.isEmpty()){
            return 0;
        }
        if(position >= positions.size()){
            tm.setErr(Table.ERR_POS, "Недопустимая позиция " + position + " детали. Установка №" + getIdDev()+".");
            return 0;
        }
        lo = positions.get(position);
        if(lo == null) {
            tm.setErr(Table.ERR_POS, "Не найдена указанная позиция " + position + " детали. Установка №" + getIdDev()+".");
            return 0;
        }
       return lo.getIdDet();
    }

    /**
     * Удаление размещений всех деталей с установки.<br>
     * Создается дублирующая коллекция размещений методом {@code Object.clone}.
     * Удаляются все элементы коллекции размещений установки.
     * @return Коллекция удаленных размещений.
     */
    @Override
    public List<LocationEnt> clearAllLoc() {    
        
        ArrayList<LocationEnt> s = (ArrayList<LocationEnt>) positions.clone();
     
        positions.clear();
        return s;
    }

    @Override
    public int getCountDetails() {
        return positions.size();
    }
}

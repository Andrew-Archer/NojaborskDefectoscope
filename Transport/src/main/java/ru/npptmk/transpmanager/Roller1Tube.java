/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.transpmanager;

import java.util.ArrayList;
import java.util.List;

/**
 * Установка рольганг, оперирует одной трубой.
 * 
 * @author SmorkalovAV
 */
public class Roller1Tube extends Device {
    /**
     * Идентификатор трубы, 
     */
    private LocationEnt loc = null;
    private ArrayList<LocationEnt> clrLoc = null;
    /**
     * 
     * @param tm 
     */
    public Roller1Tube(TranspManager tm) {
        super(tm);
    }
    /**
     * Установка оперирует одной трубой, поэтому независимо от
     * позиции труба помещается в одно и то же место. Если 
     * это место занято, то возвращается ошибка "Установка переполнена".
     * @param loc Добавляемое расположение.
     * @param position Не исползуется.
     * @return {@code true} если все прошло удачно.
     * {@code false} если возникла ошибка, описание ошибки вернет метод {@code TranspManager.getError}.
     */
    @Override
    public boolean addLocation(LocationEnt loc, int position) {
        if(this.loc != null){
            tm.setErr(TranspManager.DEVICE_FILL, "Добавление детали на рольганг №" + getIdDev() + " с одной трубой."
                    + "Рольганг занят");
            return false;
        }
        this.loc = loc;
        return tm.updateLocation(loc, 0, getIdDev());
    }
    /**
     * Удаление единственной детали с установки. Позиция
     * удаления смысла не имеет.
     * @param pos Не используется.
     * @return Если все удачно, то вернет удаленное размещение.
     * Если возникла ошибка, то вернет {@code null} и описание ошибки вернет метод {@code TranspManager.getError}.
     */
    @Override
    public LocationEnt remLocation(int pos) {
        LocationEnt ret = loc;
        loc = null;
        return ret;
    }
    /**
     * В единственную переменную расположения заносит первый элемент
     * коллекции переданных расположений. Если переденная коллекция
     * пуста не делает ничего.
     * @param dv новые данные установки.
     * @return {@code true} если все прошло удачно.
     * {@code false}, описание ошибки вернет метод {@code TranspManager.getError}.
     */
    @Override
    public boolean refreshDev(DeviceEnt dv) {
        ent = dv;
        List<LocationEnt> locs = tm.getLocations(dv.getIdDev());
        if(!locs.isEmpty()){
            loc = locs.get(0);
        }
        return true;
    }
    /**
     * Возвращает идентификатор детали.<br>
     * Если деталь есть на установке, вернет идентификатор
     * детали иначе 0.
     * @param position Не используется.
     * @return {@code idDet} идентификатор детали при удачном завершении.
     * {@code 0} если на установке нет детали.
     */
    @Override
    public long getIdDet(int position) {
        if(loc != null){
            return loc.getIdDet();
        }
        return 0;
    }
    /**
     * Удаление размещений деталей с установки.<br>
     * Возвращает коллекцию с одним расположением.
     * @return Если всё прошло удачно, вернет коллекцию с одним расположением.
     * Если возникла ошибка, то вернет описание ошибки, методом {@code TranspManager.getError}.
     */
    @Override
    public List<LocationEnt> clearAllLoc() {
        if(clrLoc == null){
            clrLoc = new ArrayList<>();
        } else {
            clrLoc.clear();
        }
        clrLoc.add(loc);
        loc = null;
        return clrLoc;
    }
    /**
     * Возвращает 1 если деталь есть на установке и 0 иначе.
     * @return Возвращает 1 если деталь есть на установке и 0 иначе.
     */
    @Override
    public int getCountDetails() {
        if(loc == null){
            return 0;
        }
        return 1;
    }
    
}

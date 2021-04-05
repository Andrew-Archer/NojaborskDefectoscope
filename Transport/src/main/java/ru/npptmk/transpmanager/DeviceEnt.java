/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.transpmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

/**
 * Сущность установки.<br>
 * Все технологическое оборудование цеха представлено в транспортном менеджере в
 * виде набора установок. Каждая установка в транспортном менеджере имеет
 * уникальный идентификатор, класс, набор транспортных ограничений, наименование
 * и произвольный набор параметров. Транспортный менеджер обеспечивает
 * сохранение наименования установки и ее параметров в базе данных, но не
 * использует их в своей работе.
 */
@Entity
@NamedQueries({
    @NamedQuery(name = "getAllDevices", query = "select e from DeviceEnt e")
})
public class DeviceEnt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private long idDev;         // id установки, первичный ключ.
    
    private Serializable param; // Параметры установки.
    private String name;        // Наименование установки.
    private Class devCl;        // Класс установки.
    private int maxDet;     // Максимальное кол-во деталей на установке.
    @Transient
    private boolean busy;         // Флаг доступности установки.

//    private List<LocationEnt> locations = new ArrayList<>();
    
    /**
     * Конструктор по умолчанию
     */
    public DeviceEnt() {
    }


    /**
     * Конструктор сущности установки.
     * @param param Параметры установки. Транспортный менеджер их не использует.
     * прикладная программа может использовать их в своих целях.
     * @param name Название установки. Транспортный менеджер показывает название
     * установки.
     * @param devCl Класс установки. Используется для факта разрешения добавления или изъятия трубы с установки.
     * @param idDev Идентификатор установок. Транспортный менеджер использует его 
     * для связи с сущностью перемещения деталей.
     * @param maxDet Максимальное кол-во деталей на установке. Служит транспортным ограничением.
     * 
     */
    public DeviceEnt(long idDev, Serializable param, String name, Class devCl, int maxDet) {

        this.idDev = idDev;
        this.param = param;
        this.name = name;
        this.devCl = devCl;
        this.maxDet = maxDet;
    }

    //Обращение к параметрам сущности установок.
    /**
     * Все необходимые для специфичных устройств параметры.
     * @return параметры установки.
     */
    public Serializable getParam() {
        return param;
    }

    /**
     * Все необходимые для специфичных устройств параметры.
     * @param param параметры установки.
     */
    public void setParam(Serializable param) {
        this.param = param;
    }

    /**
     * Название установки.
     * @return название установки.
     */
    public String getName() {
        return name;
    }

    /**
     * Название установки.
     * @param name название установи.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Класс установки.
     * @return класс установки.
     */
    public Class getDevCl() {
        return devCl;
    }

    /**
     * Метод получения коллекции размещений.
     * @return коллекцию размещений.
     */
//    public List<LocationEnt> getLocations() {
//        return locations;
//    }
//
//    /**
//     * Позиция установки.
//     * @param locations позиция установки.
//     */
//    public void setLocations(List<LocationEnt> locations) {
//        this.locations = locations;
//    }

    /**
     * Максимальное кол-во деталей на установке.
     * @return максимальное кол-во деталей на установке.
     */
    public int getMaxDet() {
        return maxDet;
    }

    /**
     * Максимальное кол-во деталей на установке.
     * @param maxDet максимальное кол-во деталей на установке.
     */
    public void setMaxDet(int maxDet) {
        this.maxDet = maxDet;
    }

    /**
     * Флаг доступности установки.
     * @return {@code true} Установка работает, операции удаления деталей с установки запрещены
     * {@code false} Разрешены операции удаления деталей с установки.
     */
    public boolean isBusy() {
        return busy;
    }

    /**
     * Флаг доступности установки.
     * @param fl флаг доступности установки. 
     * {@code true} Установка работает, операции удаления деталей с установки запрещены.
     * {@code false} Разрешены операции удаления деталей с установки.
     */
    public void setBusy(boolean fl) {
        this.busy = fl;
    }

    /**
     * Первичный ключ установки.
     * @return {@code idDev} идентификатор установки.
     */
    public long getIdDev() {
        return idDev;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DeviceEnt)) {
            return false;
        }
        DeviceEnt other = (DeviceEnt) object;
        return this.idDev == other.idDev;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (int) (this.idDev ^ (this.idDev >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "Номер установки " + idDev + " " + name;
    }
    

}

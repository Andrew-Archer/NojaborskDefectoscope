/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.transpmanager;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.eclipse.persistence.annotations.Index;

/**
 * Сущность размещения деталей на установках
 * @author SerpokrylovDV
 */
@Entity
@NamedQueries({
    //Расположения деталей на нескольких установках, сортировка по возрастанию идентификатора установки 
    //затем возрастанию позиции DESC
    @NamedQuery(name = "getLocationsDevAscAsc", query = "select e from LocationEnt e where e.idDev in :dev "
            + "order by e.idDev, e.position"),
    //Расположения деталей на нескольких установках, сортировка по возрастанию идентификатора установки 
    //затем убыванию позиции
    @NamedQuery(name = "getLocationsDevAscDesc", query = "select e from LocationEnt e where e.idDev in :dev "
            + "order by e.idDev, e.position desc"),
    //Расположения деталей на нескольких установках, сортировка по убыванию идентификатора установки 
    //затем возрастанию позиции
    @NamedQuery(name = "getLocationsDevDescAsc", query = "select e from LocationEnt e where e.idDev in :dev "
            + "order by e.idDev desc, e.position"),
    //Расположения деталей на нескольких установках, сортировка по убыванию идентификатора установки 
    //затем убыванию позиции
    @NamedQuery(name = "getLocationsDevDescDesc", query = "select e from LocationEnt e where e.idDev in :dev "
            + "order by e.idDev desc, e.position desc"),
    //Расположения деталей на всех установках, сортировка по возрастанию идентификатора установки 
    //затем возрастанию позиции
    @NamedQuery(name = "getAllLocationsAscAsc", query = "select e from LocationEnt e "
            + "order by e.idDev, e.position"),
    //Расположения деталей на всех установках, сортировка по возрастанию идентификатора установки 
    //затем убыванию позиции
    @NamedQuery(name = "getAllLocationsAscDesc", query = "select e from LocationEnt e "
            + "order by e.idDev, e.position desc"),
    //Расположения деталей на всех установках, сортировка по убыванию идентификатора установки 
    //затем возрастанию позиции
    @NamedQuery(name = "getAllLocationsDescAsc", query = "select e from LocationEnt e "
            + "order by e.idDev desc, e.position asc"),
    //Расположения деталей на указанной установке, сортировка по возрастанию позиции
    @NamedQuery(name = "getDeviceLocations", query = "select e from LocationEnt e "
            + " where e.idDev = :dev order by e.position asc"),
    //Удаление расположений не связанных с установками
    @NamedQuery(name = "remInvalidLocations", query = "delete from LocationEnt s where s.idDev not "
            + "in (select f.idDev from DeviceEnt f)"),
    //Расположения деталей на всех установках, сортировка по убыванию идентификатора установки 
    //затем убыванию позиции
    @NamedQuery(name = "getAllLocationsDescDesc", query = "select e from LocationEnt e "
            + "order by e.idDev desc, e.position desc")
})
public class LocationEnt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private long idDet;          // Первичный ключ детали.
    private int position;    // позиция детали на установке. 
    @Index
    private long idDev;          // id установки.
    
    /**
     * Конструктор по умолчанию.
     */
    public LocationEnt() {
        
    }
    /**
     * @param idDet Первичный ключ детали. Используется транспортным менеджером для
     * идентификации детали.
     */
    public LocationEnt(long idDet) {
        this.idDet = idDet;
    }

    /**
     * Позиция детали на установке.
     * @return позицию детали на установке.
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * Позиция детали на установке.
     * @param position задает позицию детали на установке.
     */
    public void setPosition(int position) {
         this.position = position;
    }
    
    /**
     * Идентификатор установки.
     * @return {@code idDev} идентификатор установки.
     */
    public long getIdDev() {
        return idDev;
    }
    
    /**
     * Идентификатор установки. 
     * @param idDev номер установки.
     */
    public void setIdDev(long idDev) {
        this.idDev = idDev;
    }
    
    /**
     * Идентификатор детали.
     * @return номер детали.
     */
    public long getIdDet() {
        return idDet;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LocationEnt)) {
            return false;
        }
        LocationEnt other = (LocationEnt) object;
        return this.idDet == other.idDet;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + (int) (this.idDet ^ (this.idDet >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "Номер детали " + idDet;
    }
    
}

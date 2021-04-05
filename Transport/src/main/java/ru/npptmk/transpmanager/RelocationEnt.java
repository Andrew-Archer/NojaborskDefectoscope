/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.transpmanager;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import org.eclipse.persistence.annotations.Index;

/**
 * Сущность перемещения деталей между установками.
 * @author SerpokrylovDV
 */
@Entity
public class RelocationEnt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Index
    private long idDet;  // ID детали
    private int oldPos; // начальное расположение детали.       
    private int newPos; // конечное расположение детали.
    @Index
    private long oldDev; // старое полож установки.
    @Index
    private long newDev; // новое полож установки.
    @Index
    private long idEvt; // идентификатор события с которым связаны перемещения.
    /**
     * Идентификатор события с которым связано перемещение
     * @return идентификатор события.
     */
    public long getIdEvt() {
        return idEvt;
    }
    
    /**
     * Конструктор по умолчанию
     */
    public RelocationEnt() {
        
    }
    /**
     * Конструктор сущности перемещения.
     * @param idEvt Событие с которым связано перемещение.
     * @param idDet Используется транспортным менеджером для идентификации детали.
     * @param oldPos Используется транспортным менеджером для указания позиции детали на установке,
     * с которой перемещается деталь.
     * @param newPos Используется транспортным менеджером для указания позиции детали на установке,
     * на которую перемещается, деталь.
     * @param oldDev Используется транспортным менеджером для указания установки, с которой перемещается деталь.
     * @param newDev Используется транспортным менеджером для указания установки, на которую перемещается деталь.
     */
    public RelocationEnt(long idEvt, long idDet, int oldPos, int newPos, long oldDev, long newDev) {
        
        this.idDet = idDet;
        this.oldPos = oldPos;
        this.newPos = newPos;
        this.oldDev = oldDev;
        this.newDev = newDev;
        this.idEvt = idEvt;
    }
    
    //Обращение к полям сущностей размещения деталей.
    /**
     * Идентификатор установки.
     * @return {@code idDet} номер детали.
     */
    public long getIdDet() {
        return idDet;
    }
    
    /**
     * Старая позиция установки.
     * @return позицию детали на установке, с которой перемещаем деталь.
     */
    public int getOldPos() {
        return oldPos;
    }
    
    /**
     * Новая позиция установки.
     * @return позицию детали на установке, на которую перемещаем деталь.
     */
    public int getNewPos() {
        return newPos;
    }
    
    /**
     * Старая установка.
     * @return установку, с которой перемещаем деталь.
     */
    public long getOldDev() {
        return oldDev;
    }
    
    /**
     * Новая установка.
     * @return установку, на которую перемещаем деталь.
     */
    public long getNewDev() {
        return newDev;
    }
    
    /**
     * Идентификатор перемещения.
     * @return номер перемещения.
     */
    public long getId() {
        return id;
    }
    
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RelocationEnt)) {
            return false;
        }
        RelocationEnt other = (RelocationEnt) object;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "Номер позиции " + id;
    }
    
}

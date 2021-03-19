/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.transpmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.eclipse.persistence.annotations.Index;

/**
 * Сущность событий.
 * Кроме этого используется как объект возврата методов, работающих
 * с деталями класса {@code TranspManager}.
 * @author SerpokrylovDV
 */
@Entity
public class EventEnt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id; // номер события.
    // Аннотация говорит о том, что реквизит будет использоваться для хранения даты и времени.
    @Index
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateS;          // дата совершения события. yyyy-mm-dd hh:mm:ss
    private String comment;     // строковое описание события.
    @Index
    private long idDet;           // id детали.
    
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "idEvt")
    private List<RelocationEnt> relocations = new ArrayList<>();
    
    /**
     * Код ошибки, поле для использования этого класса как
     * объект возврата методов работы с деталями класса {@code TranspManager}.<br>
     * Если это поле равно 0, то этот объект представляет собой
     * событие записанное в базу данных, иначе это ошибка работы метода.
     */
    @Transient
    public int codeErr = 0;
    
    /**Установка комментария. При использовании как объект
     * результата работы методов.
     * @param comment Коментарий.*/
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Конструктор по умолчанию
     */
    public EventEnt(){
    }
     /**
     * Конструктор сущности событий.
     * @param dateS Отображает дату совершения события в таблице. yyyy-mm-dd hh:mm:ss
     * @param comment Отображает строковое описание произошедшего события в таблице.
     * @param idDet Обозначает номер детали. Транспортный менеджер использует её
     * для перемещения между позициями на установках.
     * 
     */
    public EventEnt(Date dateS, String comment, long idDet) {
        
        this.dateS = dateS;
        this.comment = comment;
        this.idDet = idDet;
    }
     /**
     * Конструктор сущности событий.
     * @param codeErr Код ошибки, расшифровку кодов можно найти в
     * {@code TranspManager} или в классах наследующих {@code Device}.
     * @param comment Отображает строковое описание ошибки.
     */
    public EventEnt(int codeErr, String comment) {
        this.codeErr = codeErr;
        this.comment = comment;
    }

    /**
     * Коллекция перемещений, связанных с этим событием.
     * @return коллекция перемещений.
     */
    public List<RelocationEnt> getRelocations() {
        return relocations;
    }

    //Обращение к полям сущности событий
    /**
     * Дата совершения события.
     * @return время события в формате yyyy-mm-dd hh:mm:ss
     */
    public Date getDate() {
        return dateS;
    }
    
    /**
     * Описание события.
     * @return строковое описание события.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Идентификатор детали.
     * @return {@code idDet} идентификатор детали.
     */
    public long getIdDet() {
        return idDet;
    }
    
    /**
     * Номер события.
     * @return {@code id} номер события.
     */
    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EventEnt)) {
            return false;
        }
        EventEnt other = (EventEnt) object;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        if(codeErr == 0){
            return "Событие №" + id + " деталь: " + getIdDet() 
                    + " от " + String.format("%1$td/%1$tm/%1$ty %1$tH:%1$tM:%1$tS ", getDate());
        } else {
            return "Ошибка №" + codeErr + " - " + comment;
        }
    }
    
}

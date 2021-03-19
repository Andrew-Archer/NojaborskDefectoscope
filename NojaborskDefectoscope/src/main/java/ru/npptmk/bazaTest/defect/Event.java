/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

/**
 * Сущность, для отображения в базе технологических событий,
 * произошедших с трубой.
 * @author SmorkalovAV
 */
@Entity
public class Event implements Serializable {

    private static final long serialVersionUID = 111111L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    
    private long id; // номер события.
    // Аннотация говорит о том, что реквизит будет использоваться для хранения даты и времени.
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dateS = new Date(); // дата совершения события. yyyy-mm-dd hh:mm:ss
    private String comment;     // строковое описание события.
    private long idDet;           // id детали.
    /**Установка комментария. При использовании как объект
     * результата работы методов.
     * @param comment Коментарий.*/
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Конструктор по умолчанию
     */
    public Event(){
    }
     /**
     * Конструктор сущности событий.
     * @param comment Отображает строковое описание произошедшего события в таблице.
     * @param idDet Обозначает номер детали. Транспортный менеджер использует её
     * для перемещения между позициями на установках.
     * 
     */
    public Event(String comment, long idDet) {
        
        this.comment = comment;
        this.idDet = idDet;
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
    
}

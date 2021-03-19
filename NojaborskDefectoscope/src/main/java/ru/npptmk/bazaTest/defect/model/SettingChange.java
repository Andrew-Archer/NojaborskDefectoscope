/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author razumnov
 */
@Entity
public class SettingChange implements Serializable{


    private static final long serialVersionUID = -7346684763406315445L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name="PARAM_GROUP")
    private String paramGroup;
    
    @Column(name="PARAM_NAME")
    private String paramName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private SettingsChangeEvent settingsChangeEvent;
    
    @Column(name="OLD_VALUE")
    private String oldValue;
    
    @Column(name="NEW_VALUE")
    private String newValue;
    /**
     * @return the paramGroup
     */
    public String getParamGroup() {
        return paramGroup;
    }
    /**
     * @param paramGroup the paramGroup to set
     */
    public void setParamGroup(String paramGroup) {
        this.paramGroup = paramGroup;
    }
    /**
     * @return the settingsChangeEvent
     */
    public SettingsChangeEvent getSettingsChangeEvent() {
        return settingsChangeEvent;
    }
    /**
     * @param settingsChangeEvent the settingsChangeEvent to set
     */
    public void setSettingsChangeEvent(SettingsChangeEvent settingsChangeEvent) {
        this.settingsChangeEvent = settingsChangeEvent;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @return the paramName
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * @return the oldValue
     */
    public String getOldValue() {
        return oldValue;
    }

    /**
     * @return the newValue
     */
    public String getNewValue() {
        return newValue;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SettingChange other = (SettingChange) obj;
        return Objects.equals(this.id, other.id);
    }
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author razumnov
 */
@Entity
public class SettingsChangeEvent implements Serializable {

    private static final long serialVersionUID = -7746684763406315445L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name="EVENT_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date eventDate;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Operator author;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy="settingsChangeEvent")
    private List<SettingChange> settingsChanges;
    /**
     * @return the settingsChanges
     */
    public List<SettingChange> getSettingsChanges() {
        return settingsChanges;
    }
    /**
     * @param settingsChanges the settingsChanges to set
     */
    public void setSettingsChanges(List<SettingChange> settingsChanges) {
        this.settingsChanges = settingsChanges;
    }

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the eventDate
     */
    public Date getEventDate() {
        return eventDate;
    }

    /**
     * @param eventDate the eventDate to set
     */
    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * @return the author
     */
    public Operator getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(Operator author) {
        this.author = author;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.id);
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
        final SettingsChangeEvent other = (SettingsChangeEvent) obj;
        return Objects.equals(this.id, other.id);
    }
    
    
}

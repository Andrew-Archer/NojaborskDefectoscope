/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.model;

import ru.npptmk.bazaTest.defect.*;
import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author RazumnovAA
 */
@Entity
@Table(name = "TUBERESULT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tuberesult.findAll", query = "SELECT t FROM Tuberesult t")
    , @NamedQuery(name = "Tuberesult.findById", query = "SELECT t FROM Tuberesult t WHERE t.id = :id")})
public class Tuberesult implements Serializable {

    private static final long serialVersionUID = 15151673355448661L;
    @Id
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Lob
    @Column(name = "RESULT")
    private Serializable result;

    public Tuberesult() {
    }
    
    public Tuberesult(List<BazaDefectResults> results, Long id){
        this.result = (Serializable)results;
        this.id = id;
    }

    public Tuberesult(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Serializable getResult() {
        return result;
    }

    public void setResult(Serializable result) {
        this.result = result;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tuberesult)) {
            return false;
        }
        Tuberesult other = (Tuberesult) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ru.npptmk.bazaTest.defect.Tuberesult[ id=" + id + " ]";
    }
    
}

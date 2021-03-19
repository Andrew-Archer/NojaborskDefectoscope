/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import ru.npptmk.bazaTest.defect.model.Operator;

/**
 * Класс представляет собой модель рабочей смены на предприятии.
 *
 * @author RazumnovAA
 */
@Entity
public class Shift implements Serializable {

    private static final long serialVersionUID = 1584879322587654281L;
    @Temporal(TemporalType.TIMESTAMP)
    private Date beginning;
    @Temporal(TemporalType.TIMESTAMP)
    private Date finish;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Operator operator;
    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Shift)) {
            return false;
        }
        Shift other = (Shift) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


    /**
     * @return the beginning
     */
    public Date getBeginning() {
        return beginning;
    }    
    

    /**
     * @param beginning the beginning to set
     */
    public void setBeginning(Date beginning) {
        this.beginning = beginning;
    }

    /**
     * @return the finish
     */
    public Date getFinish() {
        return finish;
    }

    /**
     * @param finish the finish to set
     */
    public void setFinish(Date finish) {
        this.finish = finish;
    }

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }
    /**
     * @param operator the operator to set
     */
    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }


    @Override
    public String toString() {
        return "ru.npptmk.bazaTest.defect.model.Shift[ id=" + id + " ]";
    }

}

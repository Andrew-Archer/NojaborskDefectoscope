/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.npptmk.bazaTest.defect.model;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author RazumnovAA
 */
@Entity
@NamedQuery(name="findByNameAndCode", query="select op from Operator op "
        + "where op.firstName = :firstName and op.lastName = :lastName and op.middleName = :middleName and op.personalNumber = :personalNumber")
@NamedQuery(name="findAllAdmins", query="select op from Operator op "
        + "where op.userRole = ru.npptmk.bazaTest.defect.model.UserRoles.ADMIN")
public class Operator implements Serializable {

    private static final long serialVersionUID = 0737672306132424747L;
    private String firstName;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String lastName;
    private String middleName;
    private String personalNumber;
    @OneToMany(fetch = FetchType.LAZY, mappedBy="author")
    private List<SettingsChangeEvent> settingsChangeEvents;
    @Column(name = "USER_ROLE")
    @Enumerated(EnumType.STRING)
    private UserRoles userRole;
    @Column(name = "USER_PASS")
    private String pass;
    public Operator() {
        this(
                "Не указано",
                "Не указано",
                "Не указано",
                "0"
        );

    }

    public Operator(
            String lastName,
            String firstName,
            String middleName,
            String personalNumber) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.personalNumber = personalNumber;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return pass;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.pass = password;
    }
    /**
     * @return the userRole
     */
    public UserRoles getUserRole() {
        return userRole;
    }
    /**
     * @param userRole the userRole to set
     */
    public void setUserRole(UserRoles userRole) {
        this.userRole = userRole;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Operator)) {
            return false;
        }
        Operator other = (Operator) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    /**
     * @return the fristName
     */
    public String getFristName() {
        return firstName;
    }

    /**
     * @param fristName the fristName to set
     */
    public void setFristName(String fristName) {
        this.firstName = fristName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the middleName
     */
    public String getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName the middleName to set
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    /**
     * @return the personalNumber
     */
    public String getPersonalNumber() {
        return personalNumber;
    }

    /**
     * @param personalNumber the personalNumber to set
     */
    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return getLastName()
                + " " + getFristName()
                + " " + getMiddleName()
                + " №" + getPersonalNumber();
    }

}

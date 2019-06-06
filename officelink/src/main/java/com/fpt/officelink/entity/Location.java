package com.fpt.officelink.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.CascadeType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Location implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String county;

    @Column
    private String address;
    
    @Column
    private String city;
    
    @Column
    private Date date_created;

//    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
//    @JsonIgnore
//    @JoinTable(
//            name = "department_in_location",
//            joinColumns = @JoinColumn(name = "location_id"),
//            inverseJoinColumns = @JoinColumn(name = "department_id"))
//    private Set<Department> department;
//
//    @ManyToOne
//    @JoinColumn(name = "workplace_id")
//    private int workplace;

    @Column
    private boolean isDeleted;
    
    //Getter and Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

//    public Set<Department> getDepartment() {
//        return department;
//    }
//
//    public void setDepartment(Set<Department> department) {
//        this.department = department;
//    }
//
//    public int getWorkplace() {
//        return workplace;
//    }
//
//    public void setWorkplace(int workplace) {
//        this.workplace = workplace;
//    }    

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    
    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.dto;

import java.util.Date;

/**
 *
 * @author Thai Phu Cuong
 */
public class LocationDTO {
    
    private int id;

    private Date dateCreated;
    
    private Date dateModified;

    private String county;

    private String city;

    private int workplaceId;

    private String address;

    private boolean isDeleted;
    
    //Getter and Setter
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getWorkplaceId() {
        return workplaceId;
    }

    public void setWorkplaceId(int workplaceId) {
        this.workplaceId = workplaceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public boolean isIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}

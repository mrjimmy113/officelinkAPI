package com.fpt.officelink.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountDTO implements Serializable {
    private Integer id;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private String address;
    private boolean isDeleted ;
    private Integer role_id;
//    private String workspacename;
    private LocationDTO location;
    private WorkplaceDTO workplace;
    private Date dateCreated;
    private Date dateModified;
    private Integer roleId;
    private List<TeamDTO> teams;


    public List<TeamDTO> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamDTO> teams) {
        this.teams = teams;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateModified() {
        return dateModified;
    }

    public void setDateModified(Date dateModified) {
        this.dateModified = dateModified;
    }

//    public String getWorkspacename() {
//        return workspacename;
//    }
//
//    public void setWorkspacename(String workspacename) {
//        this.workspacename = workspacename;
//    }


    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public WorkplaceDTO getWorkplace() {
        return workplace;
    }

    public void setWorkplace(WorkplaceDTO workplace) {
        this.workplace = workplace;
    }



    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public AccountDTO() {
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

//    public boolean isIsDeleted() {
//        return isDeleted;
//    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }



}

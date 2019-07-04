package com.fpt.officelink.dto;

import java.io.Serializable;

public class RoleDTO implements Serializable {
        private Integer id;
        private String role;

    public RoleDTO(Integer id, String role) {
        this.id = id;
        this.role = role;
    }

    public RoleDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

package com.fpt.officelink.dto;

import com.fpt.officelink.entity.Account;

import java.io.Serializable;
import java.util.Collection;

public class RoleDTO implements Serializable {
        private Integer id;
        private String role;
        private Collection<AccountDTO> accountDTOS;

    public Collection<AccountDTO> getAccountDTOS() {
        return accountDTOS;
    }

    public void setAccountDTOS(Collection<AccountDTO> accountDTOS) {
        this.accountDTOS = accountDTOS;
    }

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

package com.fpt.officelink.dto;

import java.io.Serializable;

public class ResetAccountDTO implements Serializable {
    private  String emailToken;
    private  String newPassword ;

    public ResetAccountDTO() {

    }

    public String getEmailToken() {
        return emailToken;
    }

    public void setEmailToken(String emailToken) {
        this.emailToken = emailToken;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}

package com.fpt.officelink.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.fpt.officelink.dto.AuthDTO;
import com.fpt.officelink.entity.Account;

public interface AccountService {

    Page<Account> searchWithPagination(String term, int pageNum);
    boolean addNewAccount(Account account, Integer roleId ,  String workplaceName , String addressName);
    void modifyAccount(Account account , Integer roleId , String addressName);
    void removeAccount(int id);
    boolean checkAccountExisted(String email);
    Optional<Account> getAccountByEmail(String email);
	AuthDTO getAuthenticationInfor(String email, String password);
}

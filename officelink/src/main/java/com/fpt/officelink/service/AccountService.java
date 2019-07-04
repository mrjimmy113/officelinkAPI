package com.fpt.officelink.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.fpt.officelink.dto.AuthDTO;
import com.fpt.officelink.entity.Account;

public interface AccountService {

    Page<Account> searchWithPagination(String term, int pageNum);


    boolean addNewAccount(Account account , Integer roleId);

    void modifyAccount(Account account);

    void removeAccount(int id);




    Optional<Account> getAccountByEmail(String email);


	AuthDTO getAuthenticationInfor(String email, String password);
}

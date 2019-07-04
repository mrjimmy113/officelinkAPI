package com.fpt.officelink.service;

import com.fpt.officelink.entity.Account;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AccountService {

    Page<Account> searchWithPagination(String term, int pageNum);


    boolean addNewAccount(Account account , Integer roleId);

    void modifyAccount(Account account, Integer roleId);

    void removeAccount(int id);




    Optional<Account> getAccountByEmail(String email);
}

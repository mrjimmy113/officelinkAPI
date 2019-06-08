package com.fpt.officelink.service;

import com.fpt.officelink.entity.Account;
import org.springframework.data.domain.Page;

public interface AccountService {

    Page<Account> searchWithPagination(String term, int pageNum);


    void addNewAccount(Account account);

    void modifyAccount(Account account);

    void removeAccount(int id);

    String findEmailAddress(String email);
}

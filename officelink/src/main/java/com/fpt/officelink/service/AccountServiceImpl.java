package com.fpt.officelink.service;

import com.fpt.officelink.entity.Account;
import com.fpt.officelink.repository.AccountRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private static final int PAGEMAXSIZE = 9;


    @Autowired
    AccountRespository accountRespository;

    @Override
    public Page<Account> searchWithPagination(String term, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, PAGEMAXSIZE);
        return accountRespository.findAllByNameContainingAndIsDelete(term, false,  pageable);
    }

    @Override
    public void addNewAccount(Account account) {
             accountRespository.save(account);
    }

    @Override
    public void modifyAccount(Account account) {
        accountRespository.save(account);
    }

    @Override
    public void removeAccount(int id) {
        Account account = accountRespository.findById(id).get();
        if(account != null){
            account.setDelete(true);
        }
        accountRespository.save(account);
    }

    @Override
    public String findEmailAddress(String email) {
        Account account =  accountRespository.findAllByEmail(email);
        return account.getEmail();

    }
}

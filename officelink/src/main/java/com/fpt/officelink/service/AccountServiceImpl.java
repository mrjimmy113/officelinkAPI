package com.fpt.officelink.service;

import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.Role;
import com.fpt.officelink.repository.AccountRespository;
import com.fpt.officelink.repository.RoleRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private static final int PAGEMAXSIZE = 9;


    @Autowired
    AccountRespository accountRespository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public AccountServiceImpl(AccountRespository accountRespository,
                           BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.accountRespository = accountRespository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Autowired
    RoleRespository roleRespository;

    @Override
    public Page<Account> searchWithPagination(String term, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, PAGEMAXSIZE);
        return accountRespository.findAllByFirstnameContainingAndIsDeleted(term, false,  pageable);
    }

    @Override
    public boolean addNewAccount(Account account , Integer roleId) {
        Optional<Role> optionalRole = roleRespository.findById(roleId);
        Optional<Account> optionalAccount = accountRespository.findAccountByEmailAndWorkspacename( account.getEmail(), account.getWorkspacename());
            if(optionalAccount.isPresent()){
                return false;
            }else{
                account.setRole(optionalRole.get());
                account.setPassword(bCryptPasswordEncoder.encode(account.getPassword()));
                accountRespository.save(account);
                return true;
            }

    }

    @Override
    public void modifyAccount(Account account, Integer roleId) {
        Optional<Role> optionalRole = roleRespository.findById(roleId);
        if(optionalRole.isPresent()){
            account.setRole(optionalRole.get());
            accountRespository.save(account);
        }

    }

    @Override
    public void removeAccount(int id) {
        Account account = accountRespository.findById(id).get();
        if(account != null){
            account.setDeleted(true);
        }
        accountRespository.save(account);
    }



    @Override
    public Optional<Account> getAccountByEmail(String email) {
        Optional<Account> account = accountRespository.findAccountByEmail(email);
        return account;
    }


}

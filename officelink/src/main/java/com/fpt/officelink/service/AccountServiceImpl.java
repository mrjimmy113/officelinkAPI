package com.fpt.officelink.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.AuthDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.repository.AccountRespository;

@Service
public class AccountServiceImpl implements AccountService {

    private static final int PAGEMAXSIZE = 9;


    @Autowired
    AccountRespository accountRespository;
    
    @Autowired
    JwtService jwtSer;

    @Override
    public Page<Account> searchWithPagination(String term, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, PAGEMAXSIZE);
        return accountRespository.findAllByFirstnameContainingAndIsDelete(term, false,  pageable);
    }

    @Override
    public boolean addNewAccount(Account account) {
        Optional<Account> optionalAccount = accountRespository.findAccountByEmailAndWorkspacename( account.getEmail(), account.getWorkspacename());
        if(optionalAccount.isPresent()){
            return false;
        }else{
            accountRespository.save(account);
            return true;
        }


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
    public Optional<Account> getAccountByEmail(String email) {
        Optional<Account> account = accountRespository.findAccountByEmail(email);
        return account;
    }
    
    @Override
    public AuthDTO getAuthenticationInfor(String email, String password) {
    	AuthDTO result = null;
    	Optional<Account> acc = accountRespository.findByEmailAndPassword(email, password);
    	if(acc.isPresent()) {
    		result = new AuthDTO();
    		result.setRole(acc.get().getRole());
    		result.setToken(jwtSer.createTokenWithEmail(acc.get().getEmail()));
    	}
    	return result;
    }


}

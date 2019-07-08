package com.fpt.officelink.service;

import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.AuthDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.repository.AccountRespository;
import com.fpt.officelink.repository.WorkplaceRepository;

@Service
public class AccountServiceImpl implements AccountService {

    private static final int PAGEMAXSIZE = 9;


    @Autowired
    AccountRespository accountRespository;
    
    @Autowired
    WorkplaceService workplaceService; 
    
    @Autowired
    WorkplaceRepository workplaceRepository;
    
    @Autowired
    JwtService jwtSer;

    @Override
    public Page<Account> searchWithPagination(String term, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, PAGEMAXSIZE);
        return accountRespository.findAllByFirstnameContainingAndIsDelete(term, false,  pageable);
    }

    @Transactional
    @Override
    public boolean addNewAccount(Account account, String workplaceName) {
        Optional<Account> optionalAccount = accountRespository.findAccountByEmail( account.getEmail());
        if(optionalAccount.isPresent()){
            return false;
        }else{
        	// create workplace
        	Workplace workplace = new Workplace();
        	workplace.setName(workplaceName);
        	workplace.setDateCreated(new Date());
        	workplaceRepository.save(workplace);
        	
        	// create account with the new workplace
        	account.setWorkplace(workplace);
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
    		result.setName(acc.get().getFirstname() + " " + acc.get().getLastname());
    		result.setToken(jwtSer.createTokenWithEmail(acc.get().getEmail()));
    	}
    	return result;
    }


}

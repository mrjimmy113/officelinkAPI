package com.fpt.officelink.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Role;
import com.fpt.officelink.repository.LocationRepository;
import com.fpt.officelink.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    LocationRepository locationRepository;

    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    JwtService jwtSer;


    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }



    @Override
    public Page<Account> searchWithPagination(String term, Integer workplaceId, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, PAGEMAXSIZE);
        return accountRespository.findAllByFirstnameAndWorkplace(term, workplaceId, false , pageable);
    }

    @Transactional
    @Override
    public boolean addNewAccount(Account account, Integer roleId , String workplaceName) {
        Optional<Account> acc = accountRespository.findAccountByEmail( account.getEmail());
        Optional<Role> optionalRole = roleRepository.findById(roleId);
        if(acc.isPresent()){
            return false;
        }else{
        	// create workplace
        	Workplace workplace = new Workplace();
        	workplace.setName(workplaceName);
        	workplace.setDateCreated(new Date());
        	workplaceRepository.save(workplace);




        	// create account with the new workplace
        	account.setWorkplace(workplace);

            account.setRole(optionalRole.get());
            account.setPassword(passwordEncoder().encode(account.getPassword()));
            account.setDateCreated(new Date());
            accountRespository.save(account);
            return true;
        }


    }




    @Override
    public boolean modifyAccount(Account account, Integer roleId , Location location , Workplace workplace) {
        Optional<Account> optionalAccount = accountRespository.findById(account.getId());
        Optional<Role> optionalRole = roleRepository.findById(roleId);

        if(optionalAccount.isPresent()){

            location.setAddress(location.getAddress());
            location.setDateModified(new Date());
            locationRepository.save(location);

            workplace.setName(workplace.getName());
            workplaceRepository.save(workplace);

            account.setRole(optionalRole.get());
            account.setDateModified(new Date());
            account.setWorkplace(workplace);
            account.setLocation(location);
            accountRespository.save(account);
            return true;
        }

        return false;

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
        Optional<Account> account = accountRespository.findByEmail(email);
        return account;
    }
    
    @Override
    public AuthDTO getAuthenticationInfor(String email, String password) {
    	AuthDTO result = null;
    	Optional<Account> acc = accountRespository.findByEmailAndPassword(email, password);
    	if(acc.isPresent()) {
    		result = new AuthDTO();
    		result.setRole(acc.get().getRole().getRole());
    		result.setName(acc.get().getFirstname() + " " + acc.get().getLastname());
    		result.setToken(jwtSer.createTokenWithEmail(acc.get().getEmail()));
    	}
    	return result;
    }

    @Override
    public List<Account> findAccountByWorkplaceId(Integer id) {
        return accountRespository.findAllByWorkplaceId(id, false);
    }

    @Override
    public boolean checkAccountExisted(String email) {
        Optional<Account> acc = accountRespository.findAccountByEmail( email);
        if(acc.isPresent()){
            return false;
        }
        return true;
    }


}

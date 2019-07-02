package com.fpt.officelink.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Account;

@Repository
public interface AccountRespository extends CrudRepository<Account, Integer> {
    Page<Account> findAllByFirstname(String firstname, Pageable pageable);
    Page<Account> findAllByFirstnameContainingAndIsDelete(String firstname , Boolean isDelete , Pageable pageable);
    Optional<Account> findAccountByEmail( String email);

//    Optional<Account> findAccountByEmailAndWorkspacename(String email, String worksapcename);

    Optional<Account> findByEmail(String email);
    
    Optional<Account> findByEmailAndPassword(String email, String password);
}

package com.fpt.officelink.repository;

import com.fpt.officelink.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRespository extends CrudRepository<Account, Integer> {
    Page<Account> findAllByFirstname(String firstname, Pageable pageable);
    Page<Account> findAllByFirstnameContainingAndIsDeleted(String firstname , Boolean isDeleted , Pageable pageable);
    Optional<Account> findAccountByEmail( String email);

    Optional<Account> findAccountByEmailAndWorkspacename(String email, String worksapcename);

}

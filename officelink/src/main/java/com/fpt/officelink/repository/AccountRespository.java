package com.fpt.officelink.repository;

import com.fpt.officelink.entity.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRespository extends CrudRepository<Account, Integer> {
    Page<Account> findAllByName(String name, Pageable pageable);
    Page<Account> findAllByNameContainingAndIsDelete(String name , Boolean isDelete , Pageable pageable);
    Account  findAllByEmail(String email);


}

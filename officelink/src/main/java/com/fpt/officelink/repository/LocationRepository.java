/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.repository;

import com.fpt.officelink.entity.Location;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Thai Phu Cuong
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {

    //get list location

    Page<Location> findAllByAddressContainingAndIsDeleted(String address , Boolean isDeleted , Pageable pageable);
    Page<Location> findAllByNameContainingAndIsDeleted(String address , Boolean isDeleted , Pageable pageable);



    //check address has existed and be deleted
    List<Location> findByAddressAndIsDeleted(String address, Boolean isDeleted);
    
    Optional<Location> findByNameContainingAndIsDeleted(String name, boolean isDeleted);
    
    Optional<Location> findByAddressContainingAndIsDeleted(String address, boolean isDeleted);



}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Location;

/**
 *
 * @author Thai Phu Cuong
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    
    Optional<Location> findByIdAndWorkplaceId(int id, int workplaceId);
    
    @Query("SELECT t FROM Location t WHERE t.isDeleted = :isDeleted AND t.workplace.id = :workplaceId")
    List<Location> findAllByWorkplaceId(
            @Param("workplaceId") Integer workplaceId,
            @Param("isDeleted") Boolean isDeleted);

    
    @Query("SELECT l FROM Location l "
    		+ "JOIN Account a ON l.id = a.location.id "
    		+ "JOIN a.teams t "
    		+ "WHERE t.department.id = :id")
	Set<Location> findAllByDepartmentId(@Param("id") Integer id);
    
    List<Location> findAllByWorkplaceIdAndIsDeleted(int workplaceId, boolean isDeleted);

    
    @Query("SELECT COUNT(l) FROM Location l WHERE l.workplace.id = :id")
    int countByWorkplaceId(@Param("id") Integer id);
    
    @Query("Select l from Location l where l.name like %:name% and l.isDeleted = :isDeleted and l.workplace.id = :workplaceId ORDER BY l.dateModified DESC")
    Page<Location> findAllByNameContainingAndIsDeletedAndWorkplaceId(
            @Param("name") String name,
            @Param("isDeleted") Boolean isDeleted,
            @Param("workplaceId") Integer workplaceId,
            Pageable pageable);

    @Query("Select l from Location l where l.address like %:address% and l.isDeleted = :isDeleted and l.workplace.id = :workplaceId ORDER BY l.dateModified DESC")
    Page<Location> findAllByAddressContainingAndIsDeletedAndWorkplaceId(
            @Param("address") String address,
            @Param("isDeleted") Boolean isDeleted,
            @Param("workplaceId") Integer workplaceId,
            Pageable pageable);

    @Query("Select l from Location l where l.name like %:name% and l.isDeleted = :isDeleted and l.workplace.id = :workplaceId ORDER BY l.dateModified DESC")
    Optional<Location> findByNameContainingAndIsDeletedAndWorkplaceId(
            @Param("name") String name,
            @Param("isDeleted") Boolean isDeleted,
            @Param("workplaceId") Integer workplaceId);

    @Query("Select l from Location l where l.address like %:address% and l.isDeleted = :isDeleted and l.workplace.id = :workplaceId ORDER BY l.dateModified DESC")
    Optional<Location> findByAddressContainingAndIsDeletedAndWorkplaceId(
            @Param("address") String address,
            @Param("isDeleted") Boolean isDeleted,
            @Param("workplaceId") Integer workplaceId);

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.News;
import java.util.Optional;

/**
 *
 * @author Thai Phu Cuong
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Integer> {

    Optional<News> findByIdAndWorkplaceId(int id, int workplaceId);
    
    @Query("Select n from News n where n.title like %:title% and n.isDeleted = :isDeleted and n.workplace.id = :workplaceId ORDER BY n.dateModified DESC, n.dateCreated DESC")
    Page<News> findAllByTitleContainingAndIsDeleted(
            @Param("title") String title,
            @Param("isDeleted") Boolean isDeleted,
            @Param("workplaceId") Integer workplaceId,
            Pageable pageable);

    List<News> findByIsDeletedAndWorkplaceId(boolean isDeleted, Integer id);

    @Query(value = "FROM News n WHERE n.dateCreated >= ?1 AND n.dateCreated <= ?2 AND n.workplace.id = ?3 ORDER BY n.dateModified DESC, n.dateCreated DESC")
    List<News> findNewstByDateCreated(Date startDate, Date endDate, Integer workplaceId);

    @Query("SELECT n FROM News n WHERE n.workplace.id = :id AND n.isDeleted = false ORDER BY n.dateModified DESC, n.dateCreated DESC")
    Page<News> findLastestNews(@Param("id") Integer workplaceId, Pageable pageable);
}

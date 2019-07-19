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

/**
 *
 * @author Thai Phu Cuong
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Integer>{
    Page<News> findAllByTitleContainingAndIsDeleted(String title, Boolean isDeleted, Pageable page);
    
    List<News> findByIsDeleted(boolean isDeleted);
    
    @Query(value= "FROM News n WHERE n.dateCreated >= ?1 AND n.dateCreated <= ?2 ORDER BY n.dateCreated DESC")
    List<News> findNewstByDateCreated(Date startDate, Date endDate) ;
    
    @Query("SELECT n FROM News n WHERE n.workplace.id = :id AND n.isDeleted = false ORDER BY n.dateCreated DESC")
    Page<News> findLastestNews(@Param("id") Integer workplaceId, Pageable pageable);
}

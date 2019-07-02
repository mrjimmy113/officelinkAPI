/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fpt.officelink.repository;

import com.fpt.officelink.entity.News;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
}

package com.fpt.officelink.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Survey;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer>{
	Page<Survey> findAllByNameContainingAndIsDeleted(String name,boolean isDeleted, Pageable pageable);
	
}

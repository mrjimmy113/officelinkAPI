package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpt.officelink.entity.Configuration;

public interface ConfigurationRepository extends JpaRepository<Configuration, Integer> {
	@Query("SELECT c FROM Configuration c WHERE c.survey.name LIKE %:term% AND c.isDeleted = :isDeleted AND c.workplace.id = :workplaceId")
	Page<Configuration> searchBySurveyName(
			@Param("workplaceId") int workpId,
			@Param("term") String term, 
			@Param("isDeleted") Boolean isDeleted,
			Pageable page);
	
	List<Configuration> findAllByIsDeleted(boolean isDeleted);
}

package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;

@Repository
public interface SurveySendTargetRepository extends JpaRepository<SurveySendTarget, Integer>{

	@Query("SELECT s FROM SurveySendTarget s WHERE s.survey.id = :id AND s.isNeed = true")
	List<SurveySendTarget> findAllBySurveyId(@Param("id") Integer surveyId);
	
	@Modifying
	@Query("DELETE FROM SurveySendTarget s WHERE s.survey.id = :id")
	void deleteBySurveyId(@Param("id") Integer id);
}

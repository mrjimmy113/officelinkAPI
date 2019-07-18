package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;

@Repository
public interface SurveySendTargetRepository extends JpaRepository<SurveySendTarget, Integer>{

	@Query("SELECT s FROM SurveySendTarget s WHERE s.survey.id = : surveyId")
	List<SurveySendTarget> findAllBySurveyId(@Param("surveyId") Integer surveyId);
	
	// retrieve list of team-ids that the given survey is sent to
	@Query("SELECT s.team FROM SurveySendTarget s WHERE s.survey.id = : surveyId")
	List<Team> getSurveyTeams(@Param("surveyId") Integer surveyId);
}

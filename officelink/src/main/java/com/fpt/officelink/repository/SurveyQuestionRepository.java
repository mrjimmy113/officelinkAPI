package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.SurveyQuestion;

@Repository
public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Integer>{
	@Modifying
	@Query("DELETE FROM SurveyQuestion sq WHERE sq.survey.id = :id")
	void deleteBySurveyId(@Param("id") Integer id);
	
	@Query("SELECT sq FROM SurveyQuestion sq WHERE sq.survey.id = :id ORDER BY questionIndex ASC")
	List<SurveyQuestion> findAllBySurveyId(@Param("id") Integer id);
	
	@Query("SELECT a.surveyQuestion FROM Answer a WHERE a.account.location.id = :id AND a.account.workplace.id = :workId AND a.surveyQuestion.survey.id = :surveyId")
	List<SurveyQuestion> findAllByLocationId(@Param("id") Integer id, @Param("workId") Integer workId, @Param("surveyId") Integer surveyId);

	@Query("SELECT a.surveyQuestion FROM Answer a JOIN a.account.teams t WHERE t.id = :id AND a.account.workplace.id = :workId AND a.surveyQuestion.survey.id = :surveyId")
	List<SurveyQuestion> findAllByTeamId(@Param("id") Integer id, @Param("workId") Integer workId, @Param("surveyId") Integer surveyId);
	
	@Query("SELECT a.surveyQuestion FROM Answer a JOIN a.account.teams t WHERE t.department.id = :id AND a.account.workplace.id = :workId AND a.surveyQuestion.survey.id = :surveyId")
	List<SurveyQuestion> findAllByDepartmentId(@Param("id") Integer id, @Param("workId") Integer workId, @Param("surveyId") Integer surveyId);
	
	@Query("SELECT a.surveyQuestion FROM Answer a JOIN a.account.teams t WHERE t.department.id = :depId AND a.account.location.id = :loId AND a.account.workplace.id = :workId AND a.surveyQuestion.survey.id = :surveyId")
	List<SurveyQuestion> findAllByLocationIdAndDepartmentId(@Param("depId") Integer depId, @Param("loId") Integer loId, @Param("workId") Integer workId, @Param("surveyId") Integer surveyId);
	
	@Query("SELECT a.surveyQuestion FROM Answer a JOIN a.account.teams t WHERE t.id = :id AND a.surveyQuestion.survey.id = :surveyId")
	List<SurveyQuestion> findAllByTeamIdOnly(@Param("id") Integer id, @Param("surveyId") Integer surveyId);
}

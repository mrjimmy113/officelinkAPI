package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpt.officelink.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

	@Query("SELECT COUNT(a) FROM Answer a WHERE a.account.id = :id AND a.surveyQuestion.survey.id = :surveyId")
	Integer countAnswerByAccountId(@Param("id") Integer id, @Param("surveyId") Integer surveyId);

	@Query("SELECT a FROM Answer a WHERE a.surveyQuestion.id = :id")
	List<Answer> findAllByIndentity(@Param("id") Integer id);

	@Query("SELECT a FROM Answer a WHERE a.surveyQuestion.id = :id AND a.account.location.id = :locationId")
	List<Answer> findAllByIndentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);

	@Query("SELECT a FROM Answer a JOIN a.account.teams t WHERE a.surveyQuestion.id = :id AND t.department.id = :departmentId")
	List<Answer> findAllByIndentityAndDepartmentId(@Param("id") Integer id, @Param("departmentId") Integer departmentId);

	@Query("SELECT a FROM Answer a JOIN a.account.teams t WHERE a.surveyQuestion.id = :id AND t.id = :teamId")
	List<Answer> findAllByIndentityAndTeamId(@Param("id") Integer id, @Param("teamId") Integer teamId);

	@Query("SELECT a FROM Answer a JOIN a.account.teams t WHERE a.surveyQuestion.id = :id AND a.account.location.id = :locationId AND t.department.id = :departmentId")
	List<Answer> findAllByIndentityAndLocationIdAndDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);
	
	@Query("SELECT a FROM Answer a WHERE a.surveyQuestion.survey.id = :surveyId AND a.surveyQuestion.question.id = :questionId")
	List<Answer> findAllBySurveyIdAndQuestionId(@Param("surveyId") Integer surveyId, @Param("questionId") Integer questionId);

}

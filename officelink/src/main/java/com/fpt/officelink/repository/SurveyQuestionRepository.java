package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;

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
	
	@Query("SELECT sq FROM SurveyQuestion sq WHERE sq.survey.id = :surId AND sq.question.id = :questId")
	Optional<SurveyQuestion> findBySurveyIdAndQuestionId(@Param("surId") Integer surId, @Param("questId") Integer questId);
	
	@Modifying
	@Query("DELETE FROM SurveyQuestion sq WHERE sq.question.id = :id AND sq.question.isDeleted = true AND sq.survey.isSent = false")
	void deleteByQuestionIdAndNotSentSurvey(@Param("id") Integer id);
}

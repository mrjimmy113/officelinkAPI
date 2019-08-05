package com.fpt.officelink.repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.Survey;
import java.util.Set;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {

	@Query("SELECT s FROM Survey s WHERE s.name LIKE %:name% AND s.workplace.id = :id AND s.isDeleted = :isDeleted")
	Page<Survey> findAllByNameContainingAndWorkplaceIdAndIsDeleted(@Param("name") String name, @Param("id") Integer workplaceId, @Param("isDeleted") boolean isDeleted,Pageable pageable);
	
	@Query("SELECT s FROM Survey s WHERE s.isDeleted = :isDeleted AND s.workplace.id = :workplaceId")
	List<Survey> findAllByWorkplaceAndIsDeleted(int workplaceId, boolean isDeleted);

	Page<Survey> findAllByNameContainingAndWorkplaceIdAndIsDeletedAndIsSent(String name, int workplaceId,
			boolean isDeleted, boolean isActive, Pageable pageable);
	
	@Query("SELECT s FROM Survey s WHERE s.dateStop < :date AND s.isActive = :isActive AND s.isDeleted = :isDeleted")
	List<Survey> findAllByDateStopAndIsActiveAndIsDeleted(Date date, boolean isActive, boolean isDeleted);

	@Query("SELECT s FROM Survey s JOIN s.surveyQuestions q WHERE q.question.id = :id AND s.id NOT IN (:notId) AND s.isActive = true")
	List<Survey> findAllByQuestionId(@Param("id") Integer id,@Param("notId") List<Integer> notId);
	
	@Query("SELECT s FROM Survey s WHERE s.workplace.id = :id AND s.dateSendOut != null ORDER BY s.dateSendOut DESC")
	Page<Survey> findTop5LastestSendOutSurvey(@Param("id") Integer workplaceId, Pageable pageable);
	
	@Query("SELECT s FROM Survey s WHERE LOWER(s.name) = LOWER(:name) AND s.workplace.id = :id")
	Optional<Survey> findByNameAndWorkplaceId(@Param("name") String name, @Param("id") Integer workplaceId);
	
	@Query("SELECT s FROM Survey s WHERE s.name LIKE %:term% AND s.workplace.id = :id OR s.isTemplate = true AND s.isDeleted = false")
	Page<Survey> findAllTemplateSurvey(@Param("term") String term, @Param("id") Integer workplaceId, Pageable pageable);
	
	@Query("SELECT sq.survey FROM SurveyQuestion sq LEFT JOIN sq.answers a WHERE sq.survey.name LIKE %:term% AND a.account.email = :email AND sq.survey.workplace.id = :workplaceId AND sq.survey.isSent = true GROUP BY sq.survey.id")
	Page<Survey> findReportableSurvey(@Param("term") String term,@Param("workplaceId") Integer workplaceId,@Param("email") String email,Pageable pageable);
	
	@Query("SELECT DISTINCT s FROM Survey s JOIN s.surveyQuestions q JOIN q.answers a WHERE s.isDeleted = :isDeleted AND s.name like %:name% AND a in :answers")
    Page<Survey> findAllByAnswersAndIsDeleted(
            @Param("isDeleted") boolean isDeleted,
            @Param("name") String name,
            @Param("answers") Set<Answer> answers,
            Pageable pageable);
}

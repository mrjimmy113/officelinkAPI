package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Survey;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {
	Page<Survey> findAllByNameContainingAndIsDeleted(String name, boolean isDeleted, Pageable pageable);

	@Query("SELECT s FROM Survey s WHERE s.isDeleted = :isDeleted AND s.workplace.id = :workplaceId")
	List<Survey> findAllByWorkplaceAndIsDeleted(int workplaceId, boolean isDeleted);

	Page<Survey> findAllByNameContainingAndWorkplaceIdAndIsDeletedAndIsActive(String name, int workplaceId,
			boolean isDeleted, boolean isActive, Pageable pageable);

	@Query("SELECT s FROM Survey s JOIN s.surveyQuestions q WHERE q.question.id = :id")
	List<Survey> findAllByQuestionId(@Param("id") Integer id);
	
}

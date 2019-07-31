package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer>{
	Page<Question> findAllByQuestionContainingAndWorkplaceIdAndIsDeleted(String term,Integer workplaceId,boolean isDeleted, Pageable pageable);
	
	@Query("SELECT q from Question q WHERE q.question LIKE %:term% AND q.type.id = :id AND q.workplace.id = :workplaceId OR q.isTemplate = true AND q.isDeleted = false")
	Page<Question> findAllTemplateWithType(@Param("term") String term,@Param("id") Integer id, @Param("workplaceId") Integer workplaceId,Pageable pageable);
	
	@Query("SELECT q from Question q "
			+ "JOIN SurveyQuestion s "
			+ "ON q.id = s.question.id "
			+ "WHERE s.survey.id = :id")
	List<Question> findAllBySurveyId(@Param("id") Integer id);
	
	@Query("SELECT q FROM Question q WHERE q.question LIKE %:term% AND q.workplace.id = :id OR q.isTemplate = true AND q.isDeleted = false")
	Page<Question> findAllTemplate(@Param("term") String term, @Param("id") Integer id, Pageable pageable);
}

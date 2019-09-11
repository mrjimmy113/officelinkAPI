package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.AnswerReport;

@Repository
public interface SingleChoiceRepository extends JpaRepository<AnswerReport, Integer>{
//	@Query("SELECT NEW AnswerReport(w.content, COUNT(w)) FROM Answer w WHERE w.surveyQuestion.question.type.id = 1 AND w.surveyQuestion.id = :id GROUP BY w.content")
//	List<AnswerReport> findAllByIndentity(@Param("id") Integer id);
//
//	@Query("SELECT NEW AnswerReport(w.content, COUNT(w)) FROM Answer w WHERE w.surveyQuestion.question.type.id = 1 AND w.surveyQuestion.id = :id AND w.account.location.id = :locationId GROUP BY w.content")
//	List<AnswerReport> findAllByIndentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);
//
//	@Query("SELECT NEW AnswerReport(w.content, 1) FROM Answer w JOIN w.account.teams t WHERE w.surveyQuestion.question.type.id = 1 AND w.surveyQuestion.id = :id AND t.department.id = :departmentId GROUP BY w.account.id")
//	List<AnswerReport> findAllByIndentityAndDepartmentId(@Param("id") Integer id,
//			@Param("departmentId") Integer departmentId);
//
//	@Query("SELECT NEW AnswerReport(w.content, COUNT(w)) FROM Answer w JOIN w.account.teams t WHERE w.surveyQuestion.question.type.id = 1 AND w.surveyQuestion.id = :id AND t.id = :teamId GROUP BY w.content")
//	List<AnswerReport> findAllByIndentityAndTeamId(@Param("id") Integer id, @Param("teamId") Integer teamId);
//
//	@Query("SELECT NEW AnswerReport(w.content, 1) FROM Answer w JOIN w.account.teams t WHERE w.surveyQuestion.question.type.id = 1 AND w.surveyQuestion.id = :id AND w.account.location.id = :locationId AND t.department.id = :departmentId GROUP BY w.account.id")
//	List<AnswerReport> findAllByIndentityAndLocationIdAndDepartmentId(@Param("id") Integer id,
//			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);
//
//	@Query("SELECT NEW AnswerReport(w.content, 1) FROM Answer w JOIN w.account.teams t WHERE w.surveyQuestion.question.type.id = 1 AND w.surveyQuestion.id = :id AND (w.account.location.id = :locationId OR t.department.id = :departmentId) GROUP BY w.account.id")
//	List<AnswerReport> findAllByIndentityAndLocationIdOrDepartmentId(@Param("id") Integer id,
//			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);
}

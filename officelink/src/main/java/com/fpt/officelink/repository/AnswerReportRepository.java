package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.AnswerReport;

@Repository
public interface AnswerReportRepository extends JpaRepository<AnswerReport, Integer> {
	@Query("SELECT r.term, SUM(r.weight) FROM AnswerReport r WHERE r.teamQuestionReport.surveyQuestion.id = :id GROUP BY r.term")
	List<AnswerReport> findAllByIdentity(@Param("id") Integer id);

 	@Query("SELECT r.term, SUM(r.weight) FROM AnswerReport r JOIN r.teamQuestionReport.team.accounts a WHERE r.teamQuestionReport.surveyQuestion.id = :id AND a.location.id = :locationId GROUP BY r.term")
	List<AnswerReport> findAllByIdentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);

 	@Query("SELECT r.term, SUM(r.weight) FROM AnswerReport r WHERE r.teamQuestionReport.surveyQuestion.id = :id AND r.teamQuestionReport.team.department.id = :departmentId GROUP BY r.term")
	List<AnswerReport> findAllByIdentityAndDepartmentId(@Param("id") Integer id,@Param("departmentId") Integer departmentId);

 	@Query("SELECT r.term, SUM(r.weight) FROM AnswerReport r JOIN r.teamQuestionReport.team.accounts a WHERE r.teamQuestionReport.surveyQuestion.id = :id AND a.location.id = :locationId OR r.teamQuestionReport.team.department.id = :departmentId GROUP BY r.term")
	List<AnswerReport> findAllByIdentityAndLocationIdOrDepartmentId(@Param("id") Integer id, @Param("locationId") Integer locationId,@Param("departmentId") Integer departmentId);

 	@Query("SELECT r.term, SUM(r.weight) FROM AnswerReport r WHERE r.teamQuestionReport.surveyQuestion.id = :id AND r.teamQuestionReport.team.id = :teamId GROUP BY r.term")
	List<AnswerReport> findAllByIdentityAndTeamId(@Param("id") Integer id,@Param("teamId") Integer teamId);

 	@Query("SELECT r.term, SUM(r.weight) FROM AnswerReport r JOIN r.teamQuestionReport.team.accounts a WHERE r.teamQuestionReport.surveyQuestion.id = :id AND a.location.id = :locationId AND r.teamQuestionReport.team.department.id = :departmentId GROUP BY r.term")
	List<AnswerReport> findAllByIdentityAndLocationIdAndDepartmentId(@Param("id") Integer id, @Param("locationId") Integer locationId,@Param("departmentId") Integer departmentId);
}

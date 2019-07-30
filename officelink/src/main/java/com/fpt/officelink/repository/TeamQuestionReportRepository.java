package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.TeamQuestionReport;

@Repository
public interface TeamQuestionReportRepository extends JpaRepository<TeamQuestionReport, Integer> {
	@Query("SELECT r FROM TeamQuestionReport r WHERE r.surveyQuestion.id = :id")
	List<TeamQuestionReport> findAllByIdentity(@Param("id") Integer id);

 	@Query("SELECT r FROM TeamQuestionReport r JOIN r.team.accounts a WHERE r.surveyQuestion.id = :id AND a.location.id = :locationId")
	List<TeamQuestionReport> findAllByIdentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);

 	@Query("SELECT r FROM TeamQuestionReport r WHERE r.surveyQuestion.id = :id AND r.team.department.id = :departmentId")
	List<TeamQuestionReport> findAllByIdentityAndDepartmentId(@Param("id") Integer id,@Param("departmentId") Integer departmentId);

 	@Query("SELECT r FROM TeamQuestionReport r JOIN r.team.accounts a WHERE r.surveyQuestion.id = :id AND a.location.id = :locationId OR r.team.department.id = :departmentId")
	List<TeamQuestionReport> findAllByIdentityAndLocationIdOrDepartmentId(@Param("id") Integer id, @Param("locationId") Integer locationId,@Param("departmentId") Integer departmentId);

 	@Query("SELECT r FROM TeamQuestionReport r WHERE r.surveyQuestion.id = :id AND r.team.id = :teamId")
	List<TeamQuestionReport> findAllByIdentityAndTeamId(@Param("id") Integer id,@Param("teamId") Integer teamId);

 	@Query("SELECT r FROM TeamQuestionReport r JOIN r.team.accounts a WHERE r.surveyQuestion.id = :id AND a.location.id = :locationId AND r.team.department.id = :departmentId")
	List<TeamQuestionReport> findAllByIdentityAndLocationIdAndDepartmentId(@Param("id") Integer id, @Param("locationId") Integer locationId,@Param("departmentId") Integer departmentId);
}

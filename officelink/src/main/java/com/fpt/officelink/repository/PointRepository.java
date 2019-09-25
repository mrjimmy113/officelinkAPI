package com.fpt.officelink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpt.officelink.entity.Answer;


public interface PointRepository extends JpaRepository<Answer, Integer> {
	@Query("SELECT AVG(w.point) FROM Answer w WHERE  w.surveyQuestion.id = :id")
	Float findAllByIndentity(@Param("id") Integer id);

	@Query("SELECT AVG(w.point) FROM Answer w WHERE  w.surveyQuestion.id = :id AND w.assignmentHistory.location.id = :locationId")
	Float findAllByIndentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);

	@Query(value = "SELECT AVG(f.point)  FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId "
			+ ") as f "
			+ "", nativeQuery = true)
	Float findAllByIndentityAndDepartmentId(@Param("id") Integer id,
			@Param("departmentId") Integer departmentId);


	@Query("SELECT AVG(w.point) FROM Answer w JOIN w.assignmentHistory.teams t WHERE  w.surveyQuestion.id = :id AND t.id = :teamId")
	Float findAllByIndentityAndTeamId(@Param("id") Integer id, @Param("teamId") Integer teamId);

	@Query(value = "SELECT AVG(f.point)  FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId AND h.location_id = :locationId "
			+ ") as f "
			+ "", nativeQuery = true)
	Float findAllByIndentityAndLocationIdAndDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);

	@Query(value = "SELECT AVG(f.point)  FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId OR h.location_id = :locationId "
			+ ") as f "
			+ "", nativeQuery = true)
	Float findAllByIndentityAndLocationIdOrDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);
	
}

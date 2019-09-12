package com.fpt.officelink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpt.officelink.entity.Answer;


public interface PointRepository extends JpaRepository<Answer, Integer> {
	@Query("SELECT AVG(w.point) FROM Answer w WHERE  w.surveyQuestion.id = :id GROUP BY w.point")
	float findAllByIndentity(@Param("id") Integer id);

	@Query("SELECT AVG(w.point) FROM Answer w WHERE  w.surveyQuestion.id = :id AND w.assignmentHistory.location.id = :locationId GROUP BY w.content")
	float findAllByIndentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);

	@Query(value = "SELECT AVG(f.point)  FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId "
			+ ") as f "
			+ "GROUP BY f.point", nativeQuery = true)
	float findAllByIndentityAndDepartmentId(@Param("id") Integer id,
			@Param("departmentId") Integer departmentId);


	@Query("SELECT AVG(w.point) FROM Answer w JOIN w.assignmentHistory.teams t WHERE  w.surveyQuestion.id = :id AND t.id = :teamId GROUP BY w.content")
	float findAllByIndentityAndTeamId(@Param("id") Integer id, @Param("teamId") Integer teamId);

	@Query(value = "SELECT AVG(f.point)  FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId AND as.location_id = :locationId "
			+ ") as f "
			+ "GROUP BY f.point", nativeQuery = true)
	float findAllByIndentityAndLocationIdAndDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);

	@Query(value = "SELECT AVG(f.point)  FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId OR as.location_id = :locationId "
			+ ") as f "
			+ "GROUP BY f.point", nativeQuery = true)
	float findAllByIndentityAndLocationIdOrDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);
	
}

package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.AnswerReport;

@Repository
public interface SingleChoiceRepository extends JpaRepository<AnswerReport, Integer>{
	@Query("SELECT NEW AnswerReport(w.content, COUNT(w)) FROM Answer w WHERE  w.surveyQuestion.id = :id GROUP BY w.content")
	List<AnswerReport> findAllByIndentity(@Param("id") Integer id);

	@Query("SELECT NEW AnswerReport(w.content, COUNT(w)) FROM Answer w WHERE  w.surveyQuestion.id = :id AND w.assignmentHistory.location.id = :locationId GROUP BY w.content")
	List<AnswerReport> findAllByIndentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);

	@Query(value = "SELECT f.content as term, COUNT(f.content) as weight FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId "
			+ ") as f "
			+ "GROUP BY f.content", nativeQuery = true)
	List<TmpReport> findAllByIndentityAndDepartmentId(@Param("id") Integer id,
			@Param("departmentId") Integer departmentId);
	@Query(value = "SELECT f.content as term, COUNT(f.content) as weight FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.survey_question AS sq ON a.question_identity = sq.id "
			+ "JOIN officelink.question as q ON sq.question_id = q.id "
			+ "JOIN officelink.type_question AS tq ON q.type_id = tq.id "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id) as f "
			+ "GROUP BY f.content", nativeQuery = true)
	List<TmpReport> testRun();

	@Query("SELECT DISTINCT NEW AnswerReport(w.content, COUNT(w)) FROM Answer w JOIN w.assignmentHistory.teams t WHERE  w.surveyQuestion.id = :id AND t.id = :teamId GROUP BY w.content")
	List<AnswerReport> findAllByIndentityAndTeamId(@Param("id") Integer id, @Param("teamId") Integer teamId);

	@Query(value = "SELECT f.content as term, COUNT(f.content) as weight FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId AND as.location_id = :locationId "
			+ ") as f "
			+ "GROUP BY f.content", nativeQuery = true)
	List<TmpReport> findAllByIndentityAndLocationIdAndDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);

	@Query(value = "SELECT f.content as term, COUNT(f.content) as weight FROM ("
			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
			+ "JOIN team as t on ht.team_id = t.id "
			+ "WHERE a.question_identity = :id AND t.department_id = :departmentId OR as.location_id = :locationId "
			+ ") as f "
			+ "GROUP BY f.content", nativeQuery = true)
	List<TmpReport> findAllByIndentityAndLocationIdOrDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);
	
	public interface TmpReport {
		  String getTerm();
		  long getWeight();
	}

}

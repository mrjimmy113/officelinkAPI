package com.fpt.officelink.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpt.officelink.entity.AnswerReport;

@Repository
public interface WordCloudRepository extends org.springframework.data.repository.Repository<AnswerReport, Integer> {

	@Query("SELECT NEW AnswerReport(w.word, SUM(w.times)) FROM WordCloud w WHERE w.answer.surveyQuestion.id = :id GROUP BY w.word")
	List<AnswerReport> findAllByIndentity(@Param("id") Integer id);

	@Query("SELECT NEW AnswerReport(w.word, SUM(w.times)) FROM WordCloud w WHERE w.answer.surveyQuestion.id = :id AND w.answer.assignmentHistory.location.id = :locationId GROUP BY w.word")
	List<AnswerReport> findAllByIndentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);

	@Query("SELECT NEW AnswerReport(w.word, w.times) FROM WordCloud w JOIN w.answer.assignmentHistory.teams t WHERE w.answer.surveyQuestion.id = :id AND t.department.id = :departmentId GROUP BY w.id")
	List<AnswerReport> findAllByIndentityAndDepartmentId(@Param("id") Integer id,
			@Param("departmentId") Integer departmentId);

	@Query("SELECT NEW AnswerReport(w.word, SUM(w.times)) FROM WordCloud w JOIN w.answer.assignmentHistory.teams t WHERE w.answer.surveyQuestion.id = :id AND t.id = :teamId GROUP BY w.word")
	List<AnswerReport> findAllByIndentityAndTeamId(@Param("id") Integer id, @Param("teamId") Integer teamId);

	@Query("SELECT NEW AnswerReport(w.word, w.times) FROM WordCloud w JOIN w.answer.assignmentHistory.teams t WHERE w.answer.surveyQuestion.id = :id AND w.answer.assignmentHistory.location.id = :locationId AND t.department.id = :departmentId GROUP BY w.id")
	List<AnswerReport> findAllByIndentityAndLocationIdAndDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);

	@Query("SELECT NEW AnswerReport(w.word, w.times) FROM WordCloud w JOIN w.answer.assignmentHistory.teams t WHERE w.answer.surveyQuestion.id = :id AND (w.answer.assignmentHistory.location.id = :locationId OR t.department.id = :departmentId) GROUP BY w.id")
	List<AnswerReport> findAllByIndentityAndLocationIdOrDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);


//	@Query(value = "SELECT wc.word as term, SUM(wc.times) as weight FROM ("
//			+ "SELECT distinct a.id, a.content, a.point FROM officelink.answer as a "
//			+ "JOIN officelink.assignment_history as h ON a.assign_id = h.id "
//			+ "JOIN officelink.history_team as ht on h.id = ht.history_id "
//			+ "JOIN team as t on ht.team_id = t.id "
//			+ "WHERE sq.id = :id AND t.department_id = :departmentId OR as.location_id = :locationId "
//			+ ") as f "
//			+ "JOIN officelink.word_cloud AS wc ON f.id = wc.answer_id"
//			+ "GROUP BY wc.word", nativeQuery = true)
}

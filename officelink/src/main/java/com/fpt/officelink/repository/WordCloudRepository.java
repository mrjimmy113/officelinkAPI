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

	@Query("SELECT NEW AnswerReport(w.word, SUM(w.times)) FROM WordCloud w WHERE w.answer.surveyQuestion.id = :id AND w.answer.account.location.id = :locationId GROUP BY w.word")
	List<AnswerReport> findAllByIndentityAndLocationId(@Param("id") Integer id, @Param("locationId") Integer locationId);

	@Query("SELECT NEW AnswerReport(w.word, w.times) FROM WordCloud w JOIN w.answer.account.teams t WHERE w.answer.surveyQuestion.id = :id AND t.department.id = :departmentId GROUP BY w.id")
	List<AnswerReport> findAllByIndentityAndDepartmentId(@Param("id") Integer id,
			@Param("departmentId") Integer departmentId);

	@Query("SELECT NEW AnswerReport(w.word, SUM(w.times)) FROM WordCloud w JOIN w.answer.account.teams t WHERE w.answer.surveyQuestion.id = :id AND t.id = :teamId GROUP BY w.word")
	List<AnswerReport> findAllByIndentityAndTeamId(@Param("id") Integer id, @Param("teamId") Integer teamId);

	@Query("SELECT NEW AnswerReport(w.word, w.times) FROM WordCloud w JOIN w.answer.account.teams t WHERE w.answer.surveyQuestion.id = :id AND w.answer.account.location.id = :locationId AND t.department.id = :departmentId GROUP BY w.id")
	List<AnswerReport> findAllByIndentityAndLocationIdAndDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);

	@Query("SELECT NEW AnswerReport(w.word, w.times) FROM WordCloud w JOIN w.answer.account.teams t WHERE w.answer.surveyQuestion.id = :id AND (w.answer.account.location.id = :locationId OR t.department.id = :departmentId) GROUP BY w.id")
	List<AnswerReport> findAllByIndentityAndLocationIdOrDepartmentId(@Param("id") Integer id,
			@Param("locationId") Integer locationId, @Param("departmentId") Integer departmentId);


}

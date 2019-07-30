package com.fpt.officelink.service;

import com.fpt.officelink.dto.AnswerDTO;
import java.sql.Date;
import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.dto.AnswerReportDTO;
import com.fpt.officelink.dto.QuestionDTO;
import com.fpt.officelink.dto.QuestionReportDTO;
import com.fpt.officelink.dto.SurveyAnswerInforDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.SurveySendTarget;
import com.nimbusds.jose.JOSEException;

public interface SurveyService {

	Page<Survey> searchWithPagination(String term, int pageNum);

	void delete(Integer id);

	boolean updateSurvey(Survey survey, List<SurveyQuestion> sqList);

	List<SurveyQuestion> getDetail(Integer id);

	SurveyDTO getTakeSurvey(String token) throws ParseException;

	List<Survey> getWorkplaceSurvey(int workplaceId);

	boolean newSurvey(Survey survey, List<SurveyQuestion> sqList);

	SurveyReportDTO getReport(Integer id);


	String getSurveyToken(Integer surveyId) throws JOSEException;

	Page<Survey> searchReportWithPagination(String term, int pageNum);

	void saveAnswer(SurveyAnswerInforDTO dto);

	void sendRoutineSurvey(int surveyId, int duration) throws JOSEException;

	List<QuestionReportDTO> getFilteredReport(int surveyId, int locationId, int departmentId, int teamId);
	
	Survey updateStatus(Survey survey);
	
	List<Survey> getActiveSurveyByDate(Date date);


	List<Survey> getSurveyByQuestionId(int id, int notId);

	List<AnswerReportDTO> getAnswerReport(int surveyId, int questionId, int locationId, int departmentId, int teamId);

	boolean checkIfUserTakeSurvey(Integer surveyId);

	boolean sendOutSurvey(Integer surveyId, List<SurveySendTarget> targets, int duration, int workplaceId)
			throws JOSEException;
        
        Page<Survey> getHistorySurveyWithPagination(String term, int pageNum);
        
        Date getDateTakenSurvey(int surveyId);
        
        List<QuestionDTO> getTakeSurveyHistory(int id);
        
        List<AnswerDTO> getAnswerBySurveyId(int surveyId);
}

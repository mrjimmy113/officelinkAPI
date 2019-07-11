package com.fpt.officelink.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.dto.SendSurveyDTO;
import com.fpt.officelink.dto.SurveyAnswerInforDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.nimbusds.jose.JOSEException;

public interface SurveyService {

	Page<Survey> searchWithPagination(String term, int pageNum);

	void delete(Integer id);

	void updateSurvey(Survey survey, List<SurveyQuestion> sqList);

	List<SurveyQuestion> getDetail(Integer id);

	SurveyDTO getTakeSurvey(String token) throws ParseException;

	List<Survey> getWorkplaceSurvey(int workplaceId);

	void newSurvey(Survey survey, List<SurveyQuestion> sqList);

	SurveyReportDTO getReport(Integer id);

	void sendOutSurvey(SendSurveyDTO sendInfor) throws JOSEException;

	String getSurveyToken(Integer surveyId) throws JOSEException;

	boolean checkIfUserTakeSurvey();

	Page<Survey> searchReportWithPagination(String term, int pageNum);

	void saveAnswer(SurveyAnswerInforDTO dto);
}

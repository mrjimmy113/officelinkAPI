package com.fpt.officelink.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.dto.AnswerDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.nimbusds.jose.JOSEException;

public interface SurveyService {
	void newSurvey(Survey survey, List<Question> questions);

	Page<Survey> searchWithPagination(String term, int pageNum);

	void delete(Integer id);

	void updateSurvey(Survey survey, List<Question> questions);

	List<Question> getDetail(Integer id);

	String getSurveyToken(String email, Integer surveyId) throws JOSEException;

	SurveyDTO getTakeSurvey(String token) throws ParseException;

	boolean sendOutSurvey(Integer surveyId) throws JOSEException;

	void saveAnswer(List<AnswerDTO> answers);
	
	List<Survey> getWorkplaceSurvey(int workplaceId);
}

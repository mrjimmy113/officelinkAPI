package com.fpt.officelink.service;

import java.sql.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.repository.AnswerOptionRepository;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;

@Service
public class SurveyServiceImpl implements SurveyService {

	@Autowired
	SurveyRepository surveyRep;
	
	@Autowired
	QuestionRepository questionRep;
	
	@Autowired
	SurveyQuestionRepository surQuestRep;
	
	@Autowired
	AnswerOptionRepository optionRep;
	
	@Transactional(rollbackOn = Exception.class)
	@Override
	public void newSurvey(Survey survey, List<Question> questions) {
		Date today = new Date(new java.util.Date().getTime());
		// Set created date
		surveyRep.save(survey);
		questions.forEach(q -> {
			questionRep.saveAndFlush(q);
			SurveyQuestion tmp = new SurveyQuestion();
			tmp.setQuestion(q);
			tmp.setSurvey(survey);
			surQuestRep.save(tmp);
		});
	
	}

}

package com.fpt.officelink.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.repository.AnswerOptionRepository;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;

@Service
public class SurveyServiceImpl implements SurveyService {

	private static final int PAGEMAXSIZE = 9;
	
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
		survey.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
		surveyRep.save(survey);
		questions.forEach(q -> {

			if (q.getId() == null) {
				for (AnswerOption op : q.getOptions()) {
					op.setQuestion(q);
				}
				questionRep.save(q);
			} else {
				Optional<Question> tmp = questionRep.findById(q.getId());
				if (tmp.isPresent()) {
					q = tmp.get();
				}
			}
			SurveyQuestion tmp = new SurveyQuestion();
			tmp.setQuestion(q);
			tmp.setSurvey(survey);
			surQuestRep.save(tmp);

		});

	}
	
	@Transactional(rollbackOn = Exception.class)
	@Override
	public void updateSurvey(Survey survey, List<Question> questions) {
		survey.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
		surveyRep.save(survey);
		surQuestRep.deleteBySurveyId(survey.getId());
		questions.forEach(q -> {

			if (q.getId() == null) {
				questionRep.saveAndFlush(q);
			} else {
				Optional<Question> tmp = questionRep.findById(q.getId());
				if (tmp.isPresent()) {
					q = tmp.get();
				}
			}

			SurveyQuestion tmp = new SurveyQuestion();
			tmp.setQuestion(q);
			tmp.setSurvey(survey);
			surQuestRep.save(tmp);

		});

	}
	
	@Override
	public Page<Survey> searchWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return surveyRep.findAllByNameContainingAndIsDeleted(term, false, pageRequest);
	}
	
	@Override
	public void delete(Integer id) {
		Optional<Survey> opSurvey = surveyRep.findById(id);
		if(opSurvey.isPresent()) {
			Survey tmpSurvey = opSurvey.get();
			tmpSurvey.setDeleted(true);
			surveyRep.save(tmpSurvey);
		}
	}
	
	@Override
	public List<Question> getDetail(Integer id) {
		return questionRep.findAllBySurveyId(id);
	}
	

}

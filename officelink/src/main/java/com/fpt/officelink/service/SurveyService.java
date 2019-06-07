package com.fpt.officelink.service;

import java.util.List;

import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;

public interface SurveyService {
	void newSurvey(Survey survey, List<Question> questions);
}

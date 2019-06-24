package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;

public interface SurveyService {
	void newSurvey(Survey survey, List<Question> questions);

	Page<Survey> searchWithPagination(String term, int pageNum);

	void delete(Integer id);

	void updateSurvey(Survey survey, List<Question> questions);

	List<Question> getDetail(Integer id);
}

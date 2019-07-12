package com.fpt.officelink.dto;

import java.util.List;

public class SurveyAnswerInforDTO {
	private Integer surveyId;
	
	private List<AnswerDTO> answers;

	public Integer getSurveyId() {
		return surveyId;
	}

	public void setSurveyId(Integer surveyId) {
		this.surveyId = surveyId;
	}

	public List<AnswerDTO> getAnswers() {
		return answers;
	}

	public void setAnswers(List<AnswerDTO> answers) {
		this.answers = answers;
	}
	
	
}

package com.fpt.officelink.dto;

import java.util.List;

public class SurveyReportDTO {
	private String name;
	private List<QuestionReportDTO> questions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<QuestionReportDTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionReportDTO> questions) {
		this.questions = questions;
	}
	
	
	
	
}

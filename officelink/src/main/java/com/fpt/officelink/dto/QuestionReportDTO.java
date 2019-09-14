package com.fpt.officelink.dto;

import java.util.List;

public class QuestionReportDTO {
	private QuestionDTO question;
	
	private float avgPoint;
	
	private Integer identity;
	
	private List<AnswerReportDTO> answers;

	public QuestionDTO getQuestion() {
		return question;
	}

	public void setQuestion(QuestionDTO question) {
		this.question = question;
	}

	public List<AnswerReportDTO> getAnswers() {
		return answers;
	}

	public void setAnswers(List<AnswerReportDTO> answers) {
		this.answers = answers;
	}

	public float getAvgPoint() {
		return avgPoint;
	}

	public void setAvgPoint(float avgPoint) {
		this.avgPoint = avgPoint;
	}

	public Integer getIdentity() {
		return identity;
	}

	public void setIdentity(Integer identity) {
		this.identity = identity;
	}
	
	
	
	
	
	
}

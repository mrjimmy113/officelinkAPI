package com.fpt.officelink.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionDTO {
	private Integer id;

	private String question;

	private TypeQuestionDTO type;

	private List<AnswerOptionDTO> options = new ArrayList<AnswerOptionDTO>();

	private boolean isDeleted;
	
	private Integer questionIdentity;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public TypeQuestionDTO getType() {
		return type;
	}

	public void setType(TypeQuestionDTO type) {
		this.type = type;
	}

	public List<AnswerOptionDTO> getOptions() {
		return options;
	}

	public void setOptions(List<AnswerOptionDTO> options) {
		this.options = options;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getQuestionIdentity() {
		return questionIdentity;
	}

	public void setQuestionIdentity(Integer questionIdentity) {
		this.questionIdentity = questionIdentity;
	}
	
	

}

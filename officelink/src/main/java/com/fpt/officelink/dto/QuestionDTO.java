package com.fpt.officelink.dto;

import java.util.ArrayList;
import java.util.List;

public class QuestionDTO {
	private Integer id;
	
	private String question;
	
	private Integer typeId;
	
	private List<AnswerOptionDTO> options = new ArrayList<AnswerOptionDTO>();

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

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public List<AnswerOptionDTO> getOptions() {
		return options;
	}

	public void setOptions(List<AnswerOptionDTO> options) {
		this.options = options;
	}
	
	
	
}

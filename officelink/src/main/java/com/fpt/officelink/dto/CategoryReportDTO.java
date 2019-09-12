package com.fpt.officelink.dto;

import java.util.ArrayList;
import java.util.List;

public class CategoryReportDTO {
	
	private Integer id;
	
	private String name;
	
	private String description;
	
	private List<QuestionReportDTO> questions = new ArrayList<QuestionReportDTO>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<QuestionReportDTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionReportDTO> questions) {
		this.questions = questions;
	}
	
	
}

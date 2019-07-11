package com.fpt.officelink.dto;

public class AnswerReportDTO {
	private String term;
	
	private Integer weight;

	public AnswerReportDTO() {
		super();
	}

	public AnswerReportDTO(String term, Integer weight) {
		super();
		this.term = term;
		this.weight = weight;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	
	
	
}

package com.fpt.officelink.dto;

public class AnswerReportDTO {
	private String term;
	
	private int weight;

	public AnswerReportDTO() {
		super();
	}
	public AnswerReportDTO(String term, int weight) {
		this.term = term;
		this.weight = weight;
	}
	
	public AnswerReportDTO(String term, long weight) {
		this.term = term;
		this.weight = (int)weight;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	public int getWeight() {
		return weight;
	}
	public void setWeight(int weight) {
		this.weight = weight;
	}
	


	
	
	
}

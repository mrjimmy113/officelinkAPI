package com.fpt.officelink.entity;

import javax.persistence.Id;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class AnswerReport implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private int Id;
	
	@Column
	private String term;
	
	@Column
	private Integer weight;
	
	@ManyToOne
	@JoinColumn(name = "questionReport_id")
	private TeamQuestionReport questionReport;
	
	
	public AnswerReport() {
		super();
	}
	public AnswerReport(String term, Integer weight) {
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
	public TeamQuestionReport getQuestionReport() {
		return questionReport;
	}
	public void setQuestionReport(TeamQuestionReport questionReport) {
		this.questionReport = questionReport;
	}

}

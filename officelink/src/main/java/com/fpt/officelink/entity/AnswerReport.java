package com.fpt.officelink.entity;


import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class AnswerReport implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column
	private String term;
	
	@Column
	private Integer weight;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "teamQuestionReport_id")
	private TeamQuestionReport teamQuestionReport;
	
	
	public AnswerReport() {
		super();
	}

	public AnswerReport(String term, Integer weight) {
		this.term = term;
		this.weight = weight;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
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
	public TeamQuestionReport getTeamQuestionReport() {
		return teamQuestionReport;
	}
	public void setTeamQuestionReport(TeamQuestionReport questionReport) {
		this.teamQuestionReport = questionReport;
	}

}

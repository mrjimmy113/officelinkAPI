package com.fpt.officelink.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AnswerReport  {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String term;

	@Column
	private int weight;


	public AnswerReport() {
		super();
	}
	
	public AnswerReport(int id,String term, int weight) {
		this.id = id;
		this.term = term;
		this.weight = (int) weight;
	}

	public AnswerReport(int id,String term, long weight) {
		this.id = id;
		this.term = term;
		this.weight = (int) weight;
	}
	
	public AnswerReport(String term, long weight) {
		this.term = term;
		this.weight = (int) weight;
	}
	public AnswerReport(String term, int weight) {
		this.term = term;
		this.weight = weight;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}


}

package com.fpt.officelink.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class Answer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "date_created")
	private Date dateCreated;
	
	@Column
	private String content;
	
	@OneToOne(mappedBy = "answer")
	private SurveyQuestion surveyQuestions;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public SurveyQuestion getSurveyQuestions() {
		return surveyQuestions;
	}

	public void setSurveyQuestions(SurveyQuestion surveyQuestions) {
		this.surveyQuestions = surveyQuestions;
	}
	
	
	
	

	
}

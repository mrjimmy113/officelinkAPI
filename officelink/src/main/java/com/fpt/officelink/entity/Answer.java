package com.fpt.officelink.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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
	
	@Column(length = 550)
	private String content;
	
	@ManyToOne
	@JoinColumn(name = "question_identity")
	private SurveyQuestion surveyQuestion;
	
	@OneToMany(mappedBy = "answer", cascade = CascadeType.PERSIST)
	private List<WordCloud> wordClouds;
	
	@OneToMany(mappedBy = "answer", cascade = CascadeType.PERSIST)
	private List<MultipleAnswer> multiple;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
	
	@ManyToOne
	@JoinColumn(name = "assign_id")
	private AssignmentHistory assignmentHistory;

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

	public SurveyQuestion getSurveyQuestion() {
		return surveyQuestion;
	}

	public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}



	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<WordCloud> getWordClouds() {
		return wordClouds;
	}

	public void setWordClouds(List<WordCloud> wordClouds) {
		this.wordClouds = wordClouds;
	}

	public List<MultipleAnswer> getMultiple() {
		return multiple;
	}

	public void setMultiple(List<MultipleAnswer> multiple) {
		this.multiple = multiple;
	}

	public AssignmentHistory getAssignmentHistory() {
		return assignmentHistory;
	}

	public void setAssignmentHistory(AssignmentHistory assignmentHistory) {
		this.assignmentHistory = assignmentHistory;
	}

}

package com.fpt.officelink.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class TeamQuestionReport implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "surveyQuestion_id", nullable = false)
	private SurveyQuestion surveyQuestion;
	
	@OneToMany(mappedBy = "questionReport", cascade = CascadeType.ALL)
	private List<AnswerReport> answerReports;

	@ManyToOne
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	//Getter setter
	public List<AnswerReport> getAnswerReports() {
		return answerReports;
	}

	public void setAnswerReports(List<AnswerReport> answers) {
		this.answerReports = answers;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SurveyQuestion getSurveyQuestion() {
		return surveyQuestion;
	}

	public void setSurveyQuestion(SurveyQuestion surveyQuestion) {
		this.surveyQuestion = surveyQuestion;
	}

	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
}

package com.fpt.officelink.entity;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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
	
	@OneToMany(mappedBy = "teamQuestionReport", cascade = CascadeType.PERSIST)
	private Set<AnswerReport> answerReports;

	@ManyToOne
	@JoinColumn(name = "team_id", nullable = false)
	private Team team;

	//Getter setter
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

	public Set<AnswerReport> getAnswerReports() {
		return answerReports;
	}

	public void setAnswerReports(Set<AnswerReport> answers) {
		this.answerReports = answers;
	}
	
	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
	}
	
}

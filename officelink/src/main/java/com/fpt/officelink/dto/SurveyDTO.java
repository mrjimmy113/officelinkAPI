package com.fpt.officelink.dto;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Positive;

public class SurveyDTO {
	@Positive
	private Integer id;
	
	private String name;
	
	private Integer workplaceId;
	
	private boolean isShared;
	
	private boolean isActive;
	
	private boolean isSent;
	
	private Date dateTaken;
	
	private ConfigurationDTO configuration;
	
	private Date dateCreated;
	
	private List<QuestionDTO> questions = new ArrayList<QuestionDTO>();
	

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

	public Integer getWorkplaceId() {
		return workplaceId;
	}

	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public List<QuestionDTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionDTO> questions) {
		this.questions = questions;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public Date getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(Date dateTaken) {
		this.dateTaken = dateTaken;
	}
	
	public ConfigurationDTO getConfiguration() {
		return configuration;
	}

	public void setConfiguration(ConfigurationDTO configurationDTO) {
		this.configuration = configurationDTO;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	
}

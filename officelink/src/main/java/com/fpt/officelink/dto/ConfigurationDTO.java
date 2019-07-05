package com.fpt.officelink.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class ConfigurationDTO {
	@NotNull
	private int id;
	
	@NotNull
	private String scheduleTime;
	
	private int workplaceId;
	
	private boolean isActive;
	
    @NotNull
	private SurveyDTO survey;

	// Getter setter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getScheduleTime() {
		return scheduleTime;
	}

	public void setScheduleTime(String scheduleTime) {
		this.scheduleTime = scheduleTime;
	}

	public int getWorkplaceId() {
		return workplaceId;
	}

	public void setWorkplaceId(int workplaceId) {
		this.workplaceId = workplaceId;
	}
	
	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	
	public SurveyDTO getSurvey() {
		return survey;
	}

	public void setSurvey(SurveyDTO survey) {
		this.survey = survey;
	}
}

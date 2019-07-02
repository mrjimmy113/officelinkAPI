package com.fpt.officelink.dto;

public class ConfigurationDTO {
	private int id;
	
	private String scheduleTime;
	
	private int workplaceId;

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
	
}

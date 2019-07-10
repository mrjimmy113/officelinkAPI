package com.fpt.officelink.dto;

import java.sql.Date;
import java.util.List;

public class SendSurveyDTO {
	private Integer surveyId;
	private List<SendTargetDTO> targetList;
	private Date dateStop;
	
	public Integer getSurveyId() {
		return surveyId;
	}
	public void setSurveyId(Integer surveyId) {
		this.surveyId = surveyId;
	}
	public List<SendTargetDTO> getTargetList() {
		return targetList;
	}
	public void setTargetList(List<SendTargetDTO> targetList) {
		this.targetList = targetList;
	}
	public Date getDateStop() {
		return dateStop;
	}
	public void setDateStop(Date dateStop) {
		this.dateStop = dateStop;
	}
}

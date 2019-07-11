package com.fpt.officelink.dto;

import java.util.List;

public class SendSurveyDTO {
	private Integer surveyId;
	private List<SendTargetDTO> targetList;
	private long expireDate	;
	
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
	public long getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(long expireDate) {
		this.expireDate = expireDate;
	}
	
}

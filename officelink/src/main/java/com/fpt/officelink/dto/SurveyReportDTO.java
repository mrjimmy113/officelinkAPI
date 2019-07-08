package com.fpt.officelink.dto;

import java.sql.Date;
import java.util.List;

public class SurveyReportDTO {
	private String name;
	private Date startDate;
	private Date endDate;
	private int received;
	private int sendOut;
	private List<QuestionReportDTO> questions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<QuestionReportDTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionReportDTO> questions) {
		this.questions = questions;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public int getReceived() {
		return received;
	}

	public void setReceived(int received) {
		this.received = received;
	}

	public int getSendOut() {
		return sendOut;
	}

	public void setSendOut(int sendOut) {
		this.sendOut = sendOut;
	}
	
	
}

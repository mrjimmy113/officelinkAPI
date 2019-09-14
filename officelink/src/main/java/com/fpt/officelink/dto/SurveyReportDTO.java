package com.fpt.officelink.dto;

import java.sql.Date;
import java.util.List;

public class SurveyReportDTO {
	private Integer id;
	private String name;
	private Date dateSendOut;
	private Date dateStop;
	private int receivedAnswer;
	private int sentOut;
	private List<CategoryReportDTO> categories;
	private List<SendTargetDTO> sendTargets;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CategoryReportDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryReportDTO> categories) {
		this.categories = categories;
	}

	public Date getDateSendOut() {
		return dateSendOut;
	}

	public void setDateSendOut(Date dateSendOut) {
		this.dateSendOut = dateSendOut;
	}



	public Date getDateStop() {
		return dateStop;
	}

	public void setDateStop(Date dateStop) {
		this.dateStop = dateStop;
	}

	public int getReceivedAnswer() {
		return receivedAnswer;
	}

	public void setReceivedAnswer(int receivedAnswer) {
		this.receivedAnswer = receivedAnswer;
	}

	public int getSentOut() {
		return sentOut;
	}

	public void setSentOut(int sentOut) {
		this.sentOut = sentOut;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<SendTargetDTO> getSendTargets() {
		return sendTargets;
	}

	public void setSendTargets(List<SendTargetDTO> sendTargets) {
		this.sendTargets = sendTargets;
	}
	
	
	
	
}

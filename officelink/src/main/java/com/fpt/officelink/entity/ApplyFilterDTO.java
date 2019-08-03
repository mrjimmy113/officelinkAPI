package com.fpt.officelink.entity;

import java.util.List;

import com.fpt.officelink.dto.AnswerReportDTO;

public class ApplyFilterDTO {
	private int filterId;
	private List<AnswerReportDTO> answers;
	public int getFilterId() {
		return filterId;
	}
	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}
	public List<AnswerReportDTO> getAnswers() {
		return answers;
	}
	public void setAnswers(List<AnswerReportDTO> answers) {
		this.answers = answers;
	}
	
}

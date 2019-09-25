package com.fpt.officelink.dto;

public class AnswerDTO {
	private Integer id;
	private String content;
	private Integer accountId;
	private Integer questionIdentity;
	private String questionType;
	private Float point;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public Integer getQuestionIdentity() {
		return questionIdentity;
	}
	public void setQuestionIdentity(Integer questionIdentity) {
		this.questionIdentity = questionIdentity;
	}
	public String getQuestionType() {
		return questionType;
	}
	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}
	public Float getPoint() {
		return point;
	}
	public void setPoint(Float point) {
		this.point = point;
	}

	
	
}

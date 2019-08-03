package com.fpt.officelink.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Survey implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String name;
	
	@Column(name = "is_active")
	private boolean isActive;
	
	@Column(name = "is_shared")
	private boolean isShared;
	
	@OneToMany(mappedBy = "survey")
	private Set<SurveyQuestion> surveyQuestions;
	
	@Column
	private Date dateCreated;
	
	@Column
	private Date dateModified;
	
	@Column
	private Date dateSendOut;
	
	@Column
	private Date dateStop;
	
	@Column
	private int receivedAnswer;
	
	@Column
	private int sentOut;
	
	@Column
	private boolean isDeleted;
	
	@OneToMany(mappedBy = "survey", cascade = CascadeType.PERSIST)
	private Set<Configuration> configurations;
	
	@ManyToOne
	@JoinColumn(name = "workplace_id")
	private Workplace workplace;
	
	@OneToMany(mappedBy = "survey")
	private Set<SurveySendTarget> targets;
	
	@Column
	private Integer templateId;
	
	@Column
	private boolean isSent;
	
	@Column
	private boolean isTemplate;

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
		this.name = name.trim();
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isShared() {
		return isShared;
	}

	public void setShared(boolean isShared) {
		this.isShared = isShared;
	}

	public Set<SurveyQuestion> getSurveyQuestions() {
		return surveyQuestions;
	}

	public void setSurveyQuestions(Set<SurveyQuestion> surveyQuestions) {
		this.surveyQuestions = surveyQuestions;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Workplace getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Workplace workplace) {
		this.workplace = workplace;
	}
	
	public Set<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(Set<Configuration> configurations) {
		this.configurations = configurations;
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

	public Set<SurveySendTarget> getTargets() {
		return targets;
	}

	public void setTargets(Set<SurveySendTarget> targets) {
		this.targets = targets;
	}

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public boolean isSent() {
		return isSent;
	}

	public void setSent(boolean isSent) {
		this.isSent = isSent;
	}

	public boolean isTemplate() {
		return isTemplate;
	}

	public void setTemplate(boolean isTemplate) {
		this.isTemplate = isTemplate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Survey other = (Survey) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
	
	
}

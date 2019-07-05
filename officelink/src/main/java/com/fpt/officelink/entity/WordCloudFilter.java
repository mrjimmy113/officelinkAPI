package com.fpt.officelink.entity;

import java.io.Serializable;
import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

@Entity
public class WordCloudFilter implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column
	private String name;

	@Column
	private String language;
	
	@Column
	private boolean isDeleted;

	@ManyToMany
	@JoinTable(name = "filter_word", joinColumns = { @JoinColumn(name = "filterId") }, inverseJoinColumns = {
			@JoinColumn(name = "wordId") })
	private Set<Word> wordList = new HashSet<Word>();
	
	@Column(name = "date_created")
	private Date dateCreated;
	
	@Column(name = "date_modified")
	private Date dateModified;
	
	@ManyToOne
	@JoinColumn(name = "workplace_id")
	private Workplace workplace;

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

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Set<Word> getWordList() {
		return wordList;
	}

	public void setWordList(Set<Word> wordList) {
		this.wordList = wordList;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
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

	public Workplace getWorkplace() {
		return workplace;
	}

	public void setWorkplace(Workplace workplace) {
		this.workplace = workplace;
	}
	
}

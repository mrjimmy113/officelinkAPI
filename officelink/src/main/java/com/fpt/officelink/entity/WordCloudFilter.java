package com.fpt.officelink.entity;

import java.io.Serializable;
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

	@ManyToMany
	@JoinTable(name = "filter_word", joinColumns = { @JoinColumn(name = "filterId") }, inverseJoinColumns = {
			@JoinColumn(name = "wordId") })
	private Set<Word> wordList = new HashSet<Word>();

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

}

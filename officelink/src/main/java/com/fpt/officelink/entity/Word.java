package com.fpt.officelink.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

@Entity
public class Word implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Integer id;
	
	@Column
	private String name;
	
	@ManyToMany(mappedBy = "wordList")
	private Set<WordCloudFilter> filters = new HashSet<WordCloudFilter>();

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

	public Set<WordCloudFilter> getFilters() {
		return filters;
	}

	public void setFilters(Set<WordCloudFilter> filters) {
		this.filters = filters;
	}

}

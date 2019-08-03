package com.fpt.officelink.dto;

import java.util.ArrayList;
import java.util.List;

public class WordCloudFilterDTO {

	private Integer id;

	private String name;
	
	private boolean isExclude;

	private List<WordDTO> wordList = new ArrayList<WordDTO>();

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

	public List<WordDTO> getWordList() {
		return wordList;
	}

	public void setWordList(List<WordDTO> wordList) {
		this.wordList = wordList;
	}

	public boolean isExclude() {
		return isExclude;
	}

	public void setExclude(boolean isExclude) {
		this.isExclude = isExclude;
	}
	
	

}

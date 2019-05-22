package com.fpt.officelink.service;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.WordCloudFilter;

public interface WordCloudFilterService {
	
	Page<WordCloudFilter> searchWithPagination(String term, int pageNum);
	
	void addNewFilter(WordCloudFilter filter);
	
	void modifyFilter(WordCloudFilter filter);
	
	void removeFilter(int id);
}

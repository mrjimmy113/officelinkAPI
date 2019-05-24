package com.fpt.officelink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.WordCloudFilter;
import com.fpt.officelink.repository.WordCloudFilterRepository;
import com.fpt.officelink.repository.WordListRepository;
@Service
public class WordCloudFilterServiceImpl implements WordCloudFilterService {

	private static final int PAGEMAXSIZE = 9;
	
	@Autowired
	WordCloudFilterRepository filterRep;
	
	@Autowired
	WordListRepository wlRep;
	
	@Override
	public Page<WordCloudFilter> searchWithPagination(String term, int pageNum) {
		Pageable pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return filterRep.findAllByNameContaining(term, pageRequest);
	}
	
	@Override
	public void addNewFilter(WordCloudFilter filter) {
		WordCloudFilter saved = filterRep.saveAndFlush(filter);
		saved.getWordList().forEach(element -> {
			element.setFilter(saved);
			wlRep.save(element);
		});
	}

	@Override
	public void modifyFilter(WordCloudFilter filter) {
		filterRep.save(filter);
	}

	@Override
	public void removeFilter(int id) {
		
		
	}

	

}

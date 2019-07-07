package com.fpt.officelink.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Word;
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
	public void modifyFilter(WordCloudFilter filter, List<Word> wordList) {
		Optional<WordCloudFilter> opFilter = filterRep.findById(filter.getId());
		if(opFilter.isPresent()) {
			Set<Word> old = new HashSet<Word>();
			opFilter.get().getWordList().forEach(e -> {
				old.add(e);
			});
			this.filterSave(filter, wordList);
			old.forEach(e -> {
				Optional<Word> opWord = wlRep.findByName(e.getName());
				if(opWord.isPresent()) {
					if(opWord.get().getFilters().isEmpty()) wlRep.delete(opWord.get());
				}
			});
		}
		
	}

	@Override
	public void removeFilter(int id) {

	}

	@Transactional
	@Override
	public void addNewFilter(WordCloudFilter filter, List<Word> wordList) {
		this.filterSave(filter, wordList);
	}
	
	public WordCloudFilter filterSave(WordCloudFilter filter, List<Word> wordList) {
		Set<Word> words = new HashSet<Word>();
		wordList.forEach(e -> {
			Optional<Word> opWord = wlRep.findByName(e.getName().toLowerCase());
			if(opWord.isPresent()) {
				words.add(opWord.get());
			}else {
				words.add(wlRep.save(e));
			}
		});
		filter.setWordList(words);
		return filterRep.save(filter);
	}

}

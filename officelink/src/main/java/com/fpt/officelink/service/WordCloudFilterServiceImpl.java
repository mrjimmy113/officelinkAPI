package com.fpt.officelink.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.Word;
import com.fpt.officelink.entity.WordCloud;
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
		return filterRep.findAllByNameContainingAndIsDeleted(term, false, pageRequest);
	}

	@Override
	public void modifyFilter(WordCloudFilter filter, List<Word> wordList) {
		Optional<WordCloudFilter> opFilter = filterRep.findById(filter.getId());
		if (opFilter.isPresent()) {
			Set<Word> old = new HashSet<Word>();
			opFilter.get().getWordList().forEach(e -> {
				old.add(e);
			});
			Date today = new Date(Calendar.getInstance().getTimeInMillis());
			filter.setDateModified(today);
			this.filterSave(filter, wordList);
			old.forEach(e -> {
				Optional<Word> opWord = wlRep.findByName(e.getName());
				if (opWord.isPresent()) {
					if (opWord.get().getFilters().isEmpty())
						wlRep.delete(opWord.get());
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
		Date today = new Date(Calendar.getInstance().getTimeInMillis());
		filter.setDateCreated(today);
		this.filterSave(filter, wordList);
	}

	public WordCloudFilter filterSave(WordCloudFilter filter, List<Word> wordList) {
		Set<Word> words = new HashSet<Word>();
		wordList.forEach(e -> {
			Optional<Word> opWord = wlRep.findByName(e.getName().toLowerCase());
			if (opWord.isPresent()) {
				words.add(opWord.get());
			} else {
				words.add(wlRep.save(e));
			}
		});
		filter.setWordList(words);
		return filterRep.save(filter);
	}

	@Override
	public void delete(Integer id) {
		Optional<WordCloudFilter> opWCF = filterRep.findById(id);
		WordCloudFilter tmp = opWCF.get();
		tmp.setDeleted(true);
		filterRep.save(tmp);
	}

	@Override
	public boolean isExisted(String name, String language) {
		Optional<WordCloudFilter> opWCF = filterRep.findByNameInIgnoreCaseAndLanguageInIgnoreCase(name, language);
		if (opWCF.isPresent())
			return true;
		return false;
	}
	
	@Override
	public Set<WordCloud> rawTextToWordCloud(String rawText, Integer id, Answer entity) {
		Set<WordCloud> details = new HashSet<WordCloud>();
		Optional<WordCloudFilter> opFilter = filterRep.findById(id);
		rawText = rawText.toLowerCase();
		String[] words = rawText.trim().split(" ");
		if(opFilter.isPresent()) {
			List<Word> filterWordList = new ArrayList<Word>(opFilter.get().getWordList());
			boolean isFound;
			for(int i = 0; i< words.length; i++) {
				if(words[i].trim() == "") {
					continue;
				}
				isFound = false;
				for (WordCloud d : details) {
					if(d.getWord().equalsIgnoreCase(words[i])) {
						d.setTimes(d.getTimes() + 1);
						isFound = true;
						break;
					}
				}
				if(!isFound) {
					boolean isIncludeInFilter = false;
					for (Word word : filterWordList) {
						if(word.getName().equalsIgnoreCase(words[i])) {
							isIncludeInFilter = true;
							break;
						}
					}
					if(!isIncludeInFilter) {
						WordCloud tmp = new WordCloud();
						tmp.setWord(words[i].toLowerCase());
						tmp.setTimes(1);
						tmp.setAnswer(entity);
						details.add(tmp);
					}
				}
			}
		}
		
		return details;
		
	}
	
	
	
	
}

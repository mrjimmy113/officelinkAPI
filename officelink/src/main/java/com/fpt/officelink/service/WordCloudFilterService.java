package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.dto.AnswerReportDTO;
import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.Word;
import com.fpt.officelink.entity.WordCloud;
import com.fpt.officelink.entity.WordCloudFilter;

public interface WordCloudFilterService {
	
	Page<WordCloudFilter> searchWithPagination(String term, int pageNum);
	
	void removeFilter(int id);

	void addNewFilter(WordCloudFilter filter, List<Word> wordList);

	void modifyFilter(WordCloudFilter filter, List<Word> wordList);

	void delete(Integer id);

	List<WordCloud> rawTextToWordCloud(String rawText, Integer id, Answer entity);

	List<WordCloudFilter> getAll();

	List<AnswerReportDTO> applyFilter(List<AnswerReportDTO> answers, Integer filterId);

	boolean isExisted(String name);

	WordCloudFilter getOneFilter(Integer id);
}

package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.TypeQuestion;

public interface QuestionService {

	List<TypeQuestion> getAllType();

	void addNewQuestion(Question q, Integer typeId);

	Page<Question> searchWithPagination(String term, int pageNum);

	void deleteByFlag(Integer id);

	Page<Question> searchWithTermAndType(String term, Integer type, int pageNum);

}

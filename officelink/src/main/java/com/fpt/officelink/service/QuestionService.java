package com.fpt.officelink.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.fpt.officelink.entity.Category;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.TypeQuestion;

public interface QuestionService {

	List<TypeQuestion> getAllType();

	Page<Question> searchWithPagination(String term, int pageNum);

	void deleteByFlag(Integer id);

	Page<Question> searchWithTermAndType(String term, Integer type, int pageNum);

	Page<Question> searchWithPaginationSystemWorkplace(String term, int page);

	List<Category> getAllCategory();

	void addNewQuestion(Question q, Integer typeId, Integer categoryId);

	Page<Question> searchWithCondition(String term, int typeId, int categoryId, int pageNum);

}

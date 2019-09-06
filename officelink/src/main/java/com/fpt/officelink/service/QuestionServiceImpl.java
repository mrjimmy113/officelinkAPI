package com.fpt.officelink.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Category;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.TypeQuestion;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.repository.CategoryRepository;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.TypeQuestionRepository;

@Service
public class QuestionServiceImpl implements QuestionService {

	private static final int PAGEMAXSIZE = 9;

	@Autowired
	QuestionRepository quesRep;

	@Autowired
	TypeQuestionRepository typeRep;
	
	@Autowired
	SurveyQuestionRepository surQuestRep;
	
	@Autowired
	CategoryRepository cateRep;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Override
	public void addNewQuestion(Question q, Integer typeId, Integer categoryId) {
		Optional<TypeQuestion> tmp = typeRep.findById(typeId);
		if (tmp.isPresent()) {
			if(getUserContext().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_system_admin"))) {
				q.setTemplate(true);
			}
			Workplace workplace = new Workplace();
			workplace.setId(getUserContext().getWorkplaceId());
			Date today = new Date(Calendar.getInstance().getTimeInMillis());
			Category category = new Category();
			category.setId(categoryId);
			q.setDateCreated(today);
			q.setWorkplace(workplace);
			q.setType(tmp.get());
			q.setDeleted(false);
			q.setCategory(category);
			q.getOptions().forEach(op -> {
				op.setQuestion(q);
			});
			quesRep.save(q);
		}
	}

	@Override
	public void deleteByFlag(Integer id) {
		Optional<Question> tmp = quesRep.findByIdAndWorkplaceId(id, getUserContext().getWorkplaceId());
		if (tmp.isPresent()) {
			Question q = tmp.get();
			Date today = new Date(Calendar.getInstance().getTimeInMillis());
			q.setDateDeleted(today);
			q.setDeleted(true);
			quesRep.save(q);
			surQuestRep.deleteByQuestionIdAndNotSentSurvey(q.getId());
		}
	}

	// Type
	@Override
	public List<TypeQuestion> getAllType() {
		return typeRep.findAll();
	}

	@Override
	public Page<Question> searchWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return quesRep.findAllByQuestionContainingAndWorkplaceIdAndIsDeletedOrderByDateCreatedDesc(term,getUserContext().getWorkplaceId(), false, pageRequest);
	}

	@Override
	public Page<Question> searchWithTermAndType(String term, Integer id, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return quesRep.findAllTemplateWithType(term, id, getUserContext().getWorkplaceId(), pageRequest);
	}

	@Override
	public Page<Question> searchWithPaginationSystemWorkplace(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return quesRep.findAllTemplate(term, getUserContext().getWorkplaceId(), pageRequest);
	}
	
	@Override
	public List<Category> getAllCategory() {
		return cateRep.findAll();
	}

}

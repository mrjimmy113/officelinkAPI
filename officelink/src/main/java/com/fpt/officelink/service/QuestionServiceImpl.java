package com.fpt.officelink.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.TypeQuestion;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.TypeQuestionRepository;

@Service
public class QuestionServiceImpl implements QuestionService {

	private static final int PAGEMAXSIZE = 9;

	@Autowired
	QuestionRepository quesRep;

	@Autowired
	TypeQuestionRepository typeRep;
	
	@Value("${admin.workplace.id}")
	Integer systemWorkplaceId;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Override
	public void addNewQuestion(Question q, Integer typeId) {
		Optional<TypeQuestion> tmp = typeRep.findById(typeId);
		if (tmp.isPresent()) {
			Workplace workplace = new Workplace();
			workplace.setId(getUserContext().getWorkplaceId());
			q.setWorkplace(workplace);
			q.setType(tmp.get());
			q.setDeleted(false);
			q.getOptions().forEach(op -> {
				op.setQuestion(q);
			});
			quesRep.save(q);
		}
	}

	@Override
	public void deleteByFlag(Integer id) {
		Optional<Question> tmp = quesRep.findById(id);
		if (tmp.isPresent()) {
			Question q = tmp.get();
			q.setDeleted(true);
			quesRep.save(q);
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
		return quesRep.findAllByQuestionContainingAndWorkplaceIdAndIsDeleted(term,getUserContext().getWorkplaceId(), false, pageRequest);
	}

	@Override
	public Page<Question> searchWithTermAndType(String term, Integer id, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return quesRep.findByQuestionAndType(term, id,systemWorkplaceId,getUserContext().getWorkplaceId(),false, pageRequest);
	}
	
	@Override
	public Page<Question> searchWithPaginationSystemWorkplace(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return quesRep.findAllByTwoWorkplaceIdAndIsDeleted(term, systemWorkplaceId, getUserContext().getWorkplaceId(), false, pageRequest);
	}

}

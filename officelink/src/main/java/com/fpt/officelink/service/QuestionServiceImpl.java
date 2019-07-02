package com.fpt.officelink.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.TypeQuestion;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.TypeQuestionRepository;

@Service
public class QuestionServiceImpl implements QuestionService {
	
	private static final int PAGEMAXSIZE = 9;
	
	@Autowired
	QuestionRepository quesRep;
	
	@Autowired
	TypeQuestionRepository typeRep;
	
	//Question
	
	
	@Override
	public void addNewQuestion(Question q, Integer typeId) {
		Optional<TypeQuestion> tmp = typeRep.findById(typeId);
		if(tmp.isPresent()) {
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
		if(tmp.isPresent()) {
			Question q = tmp.get();
			q.setDeleted(true);
			quesRep.save(q);
		}
	}
	
	//Type
	@Override
	public List<TypeQuestion> getAllType() {
		return typeRep.findAll();
	}
	
	@Override
	public Page<Question> searchWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return quesRep.findAllByQuestionContainingAndIsDeleted(term, false, pageRequest);
	}
	
	@Override
	public Page<Question> searchWithTermAndType(String term,Integer id, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return quesRep.findByQuestionAndType(term, id, pageRequest);
	}
	
	
}

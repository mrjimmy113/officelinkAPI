package com.fpt.officelink.service;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.AnswerDTO;
import com.fpt.officelink.dto.AnswerOptionDTO;
import com.fpt.officelink.dto.QuestionDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.TypeQuestionDTO;
import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.mail.service.MailService;
import com.fpt.officelink.repository.AnswerOptionRepository;
import com.fpt.officelink.repository.AnswerRepository;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.nimbusds.jose.JOSEException;

@Service
public class SurveyServiceImpl implements SurveyService {

	private static final int PAGEMAXSIZE = 9;

	@Autowired
	SurveyRepository surveyRep;

	@Autowired
	QuestionRepository questionRep;

	@Autowired
	SurveyQuestionRepository surQuestRep;

	@Autowired
	AnswerOptionRepository optionRep;

	@Autowired
	JwtService jwtSer;

	@Autowired
	MailService mailSer;

	@Autowired
	AnswerRepository answerRep;

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void newSurvey(Survey survey, List<Question> questions) {
		survey.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
		surveyRep.save(survey);
		questions.forEach(q -> {

			if (q.getId() == null) {
				for (AnswerOption op : q.getOptions()) {
					op.setQuestion(q);
				}
				questionRep.save(q);
			} else {
				Optional<Question> tmp = questionRep.findById(q.getId());
				if (tmp.isPresent()) {
					q = tmp.get();
				}
			}
			SurveyQuestion tmp = new SurveyQuestion();
			tmp.setQuestion(q);
			tmp.setSurvey(survey);
			surQuestRep.save(tmp);

		});

	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void updateSurvey(Survey survey, List<Question> questions) {
		survey.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
		surveyRep.save(survey);
		System.out.println("delete");
		surQuestRep.deleteBySurveyId(survey.getId());
		questions.forEach(q -> {

			if (q.getId() == null) {
				questionRep.saveAndFlush(q);
			} else {
				Optional<Question> tmp = questionRep.findById(q.getId());
				if (tmp.isPresent()) {
					q = tmp.get();
				}
			}

			SurveyQuestion tmp = new SurveyQuestion();
			tmp.setQuestion(q);
			tmp.setSurvey(survey);
			surQuestRep.save(tmp);

		});

	}

	@Override
	public Page<Survey> searchWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return surveyRep.findAllByNameContainingAndIsDeleted(term, false, pageRequest);
	}

	@Override
	public void delete(Integer id) {
		Optional<Survey> opSurvey = surveyRep.findById(id);
		if (opSurvey.isPresent()) {
			Survey tmpSurvey = opSurvey.get();
			tmpSurvey.setDeleted(true);
			surveyRep.save(tmpSurvey);
		}
	}

	@Override
	public List<Question> getDetail(Integer id) {
		return questionRep.findAllBySurveyId(id);
	}

	@Override
	public SurveyDTO getTakeSurvey(String token) throws ParseException {
		SurveyDTO result = null;

		if (jwtSer.validateTakeSurveyToken(token)) {
			String email = jwtSer.getEmailFromToken(token);
			Integer id = jwtSer.getSurveyId(token);
			Optional<Survey> survey = surveyRep.findById(id);
			if (survey.isPresent()) {
				result = new SurveyDTO();
				BeanUtils.copyProperties(survey.get(), result);
				List<SurveyQuestion> questions = surQuestRep.findAllBySurveyId(id);
				List<QuestionDTO> qDTOs = new ArrayList<QuestionDTO>();
				questions.forEach(e -> {
					Question q = e.getQuestion();
					QuestionDTO dto = new QuestionDTO();
					BeanUtils.copyProperties(q, dto, "type", "options");
					List<AnswerOptionDTO> opList = new ArrayList<AnswerOptionDTO>();
					q.getOptions().forEach(op -> {
						AnswerOptionDTO opDto = new AnswerOptionDTO();
						BeanUtils.copyProperties(op, opDto);
						opList.add(opDto);
					});
					dto.setOptions(opList);
					TypeQuestionDTO typeDto = new TypeQuestionDTO();
					BeanUtils.copyProperties(q.getType(), typeDto);
					dto.setType(typeDto);
					dto.setQuestionIdentity(e.getId());
					qDTOs.add(dto);
				});
				result.setQuestions(qDTOs);
			}

		}
		return result;
	}

	@Override
	public String getSurveyToken(String email, Integer surveyId) throws JOSEException {
		return jwtSer.createSurveyToken(email, surveyId);
	}

	@Override
	public void sendOutSurvey(Integer surveyId) throws JOSEException {
		Optional<Survey> opSurvey = surveyRep.findById(surveyId);
		Survey survey = opSurvey.get();
		survey.setActive(true);
		surveyRep.save(survey);
		String token = jwtSer.createSurveyToken("quangnguyenvietminh@gmail.com", surveyId);
		List<String> emailList = new ArrayList<String>();
		emailList.add("quangnguyenvietminh@gmail.com");
		Map<String, Object> model = new HashMap<>();
		model.put("link", "http://localhost:4200/take/" + token);
		mailSer.sendMail(emailList.toArray(new String[emailList.size()]), "email-survey.ftl", model);

	}

	@Transactional
	@Override
	public void saveAnswer(List<AnswerDTO> answers) {
		answers.forEach(a -> {
			Answer entity = new Answer();
			entity.setContent(a.getContent());
			entity.setSurveyQuestions(surQuestRep.findById(a.getQuestionIdentity()).get());
			entity.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
			answerRep.save(entity);
		});
	}

}

package com.fpt.officelink.service;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.AnswerDTO;
import com.fpt.officelink.dto.AnswerOptionDTO;
import com.fpt.officelink.dto.QuestionDTO;
import com.fpt.officelink.dto.SurveyAnswerInforDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.TypeQuestionDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.MultipleAnswer;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.enumaration.TypeEnum;
import com.fpt.officelink.mail.service.MailService;
import com.fpt.officelink.repository.AccountRespository;
import com.fpt.officelink.repository.AnswerOptionRepository;
import com.fpt.officelink.repository.AnswerRepository;
import com.fpt.officelink.repository.DepartmentRepository;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.fpt.officelink.repository.TeamQuestionReportRepository;
import com.fpt.officelink.repository.WordCloudRepository;
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

	@Autowired
	WordCloudFilterService filterSer;

	@Autowired
	AccountRespository accRep;

	@Autowired
	SurveySendTargetRepository targetRep;

	@Autowired
	DepartmentRepository depRep;

	@Autowired
	TeamQuestionReportRepository teamQuestReportRep;

	@Autowired
	WordCloudRepository a;

	@Value("${angular.path}")
	private String angularPath;

	@Value("${filter.EN}")
	private int defaultFilterEN;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public boolean newSurvey(Survey survey, List<SurveyQuestion> sqList) {
		Optional<Survey> opSur = surveyRep.findByNameAndWorkplaceId(survey.getName(),
				getUserContext().getWorkplaceId());
		boolean isAdmin = false;
		if (opSur.isPresent())
			return false;
		Date today = new Date(Calendar.getInstance().getTimeInMillis());
		survey.setDateCreated(today);
		survey.setDateModified(today);

		if (getUserContext().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_system_admin"))) {
			isAdmin = true;
		}
		if (isAdmin)
			survey.setTemplate(true);
		Workplace workplace = new Workplace();
		workplace.setId(getUserContext().getWorkplaceId());
		survey.setWorkplace(workplace);
		surveyRep.save(survey);
		for (SurveyQuestion sq : sqList) {
			Question q = sq.getQuestion();
			q.setWorkplace(workplace);
			if (isAdmin)
				q.setTemplate(true);
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
			sq.setQuestion(q);
			sq.setSurvey(survey);
			surQuestRep.save(sq);
		}
		return true;

	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public boolean updateSurvey(Survey survey, List<SurveyQuestion> sqList) {
		Optional<Survey> opSur = surveyRep.findByNameAndWorkplaceId(survey.getName(),
				getUserContext().getWorkplaceId());
		if (opSur.isPresent()) {
			Optional<Survey> tmp = surveyRep.findWorkplaceSurveyById(survey.getId(),getUserContext().getWorkplaceId());
			if (tmp.isPresent()) {
				if (!tmp.get().getName().equalsIgnoreCase(survey.getName())) {
					return false;
				}
			}
		}
		Optional<Survey> opCurSur = surveyRep.findWorkplaceSurveyById(survey.getId(),getUserContext().getWorkplaceId());
		if(opCurSur.isPresent()) {
			Survey curSur = opCurSur.get();
			curSur.setName(survey.getName());
			curSur.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
			surveyRep.save(curSur);
			surQuestRep.deleteBySurveyId(survey.getId());
			sqList.forEach(sq -> {
				Question q = sq.getQuestion();
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
				sq.setQuestion(q);
				sq.setSurvey(curSur);
				surQuestRep.save(sq);
			});
			return true;
		}
		return false;

	}

	@Override
	public Page<Survey> searchWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return surveyRep.findAllByNameContainingAndWorkplaceIdAndIsDeleted(term, getUserContext().getWorkplaceId(),
				false, pageRequest);
	}

	@Override
	public Page<Survey> searchReportWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		if (getUserContext().getAuthorities().contains(new SimpleGrantedAuthority("ROLE_employer"))) {
			return surveyRep.findAllByNameContainingAndWorkplaceIdAndIsDeletedAndIsSentOrderByDateSendOutDesc(term,
					getUserContext().getWorkplaceId(), false, true, pageRequest);
		} else {
			return surveyRep.findReportableSurvey(term, getUserContext().getWorkplaceId(),
					getUserContext().getUsername(), pageRequest);
		}

	}

	@Override
	public void delete(Integer id) {
		Optional<Survey> opSurvey = surveyRep.findWorkplaceSurveyById(id, getUserContext().getWorkplaceId());
		if (opSurvey.isPresent()) {
			Survey tmpSurvey = opSurvey.get();
			tmpSurvey.setDeleted(true);
			tmpSurvey.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
			surveyRep.save(tmpSurvey);
		}
	}

	@Override
	public List<SurveyQuestion> getDetail(Integer id) {
		return surQuestRep.findAllBySurveyId(id);
	}

	@Override
	public SurveyDTO getTakeSurvey(String token) throws ParseException {
		SurveyDTO result = null;
		if (jwtSer.validateTakeSurveyToken(token)) {
			Integer id = jwtSer.getSurveyId(token);
			if (!checkIfUserCanTakeSurvey(id)) {
				return null;
			}
				
			Optional<Survey> survey = surveyRep.findWorkplaceSurveyById(id,getUserContext().getWorkplaceId());
			if (survey.isPresent()) {
				if (checkIfUserTakeSurvey(id) || !survey.get().isActive()) {
					return result;
				}
					
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
					dto.setRequired(e.isRequired());
					qDTOs.add(dto);
				});
				result.setQuestions(qDTOs);
			}

		}
		return result;
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	public boolean sendOutSurvey(Integer surveyId, List<SurveySendTarget> targets, int duration, int workplaceId)
			throws JOSEException {
		Optional<Survey> opSurvey = surveyRep.findWorkplaceSurveyById(surveyId,getUserContext().getWorkplaceId());
		if (opSurvey.isPresent()) {
			Survey survey = opSurvey.get();
			Set<Account> sendList = new HashSet<Account>();
			for (SurveySendTarget target : targets) {
				target.setSurvey(survey);
				if (target.isNeed()) {
					if (target.getDepartment() == null && target.getLocation() == null
							&& target.getTeam() == null) {
						sendList.addAll(accRep.findAllEmail(workplaceId, false));
					} else if (target.getDepartment() != null && target.getLocation() == null
							&& target.getTeam() == null) {
						sendList.addAll(accRep.findAllEmailByDepartmentId(target.getDepartment().getId(),
								workplaceId, false));
					} else if (target.getDepartment() == null && target.getLocation() != null
							&& target.getTeam() == null) {
						sendList.addAll(
								accRep.findAllEmailByLocationId(target.getLocation().getId(), workplaceId, false));
					} else if (target.getDepartment() != null && target.getLocation() != null
							&& target.getTeam() == null) {
						sendList.addAll(
								accRep.findAllEmailByLocationIdAndDepartmentId(target.getDepartment().getId(),
										target.getDepartment().getId(), workplaceId, false));
					} else if (target.getDepartment() != null && target.getTeam() != null) {
						sendList.addAll(accRep.findAllEmailByTeamId(target.getTeam().getId(), workplaceId, false));
					}
				}
			}
			if (sendList.size() == 0) {
				return false;
			}	
			if (!survey.isSent()) {
				survey.setActive(true);
				survey.setSent(true);
				Calendar c = Calendar.getInstance();
				Date date = new Date(c.getTimeInMillis());
				survey.setDateSendOut(date);
				c.add(Calendar.DATE, duration);
				survey.setDateStop(new Date(c.getTimeInMillis()));
				
				survey.setSentOut(sendList.size());
				surveyRep.save(survey);
				targetRep.saveAll(targets);
				List<String> emails = new ArrayList<String>();
				for (Account account : sendList) {
					if (account.getRole().getRole().equals("employer"))
						continue;
					emails.add(account.getEmail());
				}
				Map<String, Object> model = new HashMap<String, Object>();
				model.put("link", angularPath + "/take/" + jwtSer.createSurveyToken(surveyId, duration));
				mailSer.sendMail(emails.toArray(new String[emails.size()]), "email-survey.ftl", model);
			}

		}
		return true;

	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void sendRoutineSurvey(int surveyId, int duration) throws JOSEException {

		Optional<Survey> survey = surveyRep.findById(surveyId);
		if (survey.isPresent()) {
			Survey templateSurvey = survey.get();
			Survey newSurvey = new Survey();
			// Set Information for Duplicate Survey
			Calendar c = Calendar.getInstance();
			Date date = new Date(c.getTimeInMillis());
			newSurvey.setName(templateSurvey.getName() + " " + date.toString());
			newSurvey.setDateCreated(date);
			newSurvey.setDateSendOut(date);
			c.add(Calendar.DATE, duration);
			newSurvey.setDateStop(new Date(c.getTimeInMillis()));
			newSurvey.setDeleted(false);
			newSurvey.setActive(true);
			newSurvey.setSent(true);
			newSurvey.setReceivedAnswer(0);
			newSurvey.setWorkplace(templateSurvey.getWorkplace());
			newSurvey.setTemplateId(templateSurvey.getId());
			List<SurveySendTarget> targets = targetRep.findAllBySurveyIdAndIsNeed(surveyId, true);
			int workplaceId = templateSurvey.getWorkplace().getId();

			// Find Target
			Set<Account> sendList = new HashSet<Account>();
			for (SurveySendTarget target : targets) {
				if (target.getDepartment() == null && target.getLocation() == null && target.getTeam() == null) {
					sendList.addAll(accRep.findAllEmail(workplaceId, false));
				} else if (target.getDepartment() != null && target.getLocation() == null && target.getTeam() == null) {
					sendList.addAll(
							accRep.findAllEmailByDepartmentId(target.getDepartment().getId(), workplaceId, false));
				} else if (target.getDepartment() == null && target.getLocation() != null && target.getTeam() == null) {
					sendList.addAll(accRep.findAllEmailByLocationId(target.getLocation().getId(), workplaceId, false));
				} else if (target.getDepartment() != null && target.getLocation() != null && target.getTeam() == null) {
					sendList.addAll(accRep.findAllEmailByLocationIdAndDepartmentId(target.getDepartment().getId(),
							target.getDepartment().getId(), workplaceId, false));
				} else if (target.getDepartment() != null && target.getTeam() != null) {
					sendList.addAll(accRep.findAllEmailByTeamId(target.getTeam().getId(), workplaceId, false));
				}

			}
			newSurvey.setSentOut(sendList.size());
			surveyRep.save(newSurvey);
			List<SurveyQuestion> surQuests = surQuestRep.findAllBySurveyId(surveyId);
			List<SurveyQuestion> newSurQuests = new ArrayList<SurveyQuestion>();
			surQuests.forEach(e -> {
				SurveyQuestion tmp = new SurveyQuestion();
				tmp.setQuestion(e.getQuestion());
				tmp.setQuestionIndex(e.getQuestionIndex());
				tmp.setSurvey(newSurvey);
				newSurQuests.add(tmp);
			});
			surQuestRep.saveAll(newSurQuests);
			List<String> emails = new ArrayList<String>();
			for (Account account : sendList) {
				if (account.getRole().getRole().equals("employer"))
					continue;
				emails.add(account.getEmail());
			}
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("link", angularPath + "/take/" + jwtSer.createSurveyToken(surveyId, duration));
			mailSer.sendMail(emails.toArray(new String[emails.size()]), "email-survey.ftl", model);

		}

	}

	@Override
	public boolean checkIfUserTakeSurvey(Integer surveyId) {
		boolean isTake = false;
		Account acc = (accRep.findByEmail(getUserContext().getUsername())).get();
		int result = answerRep.countAnswerByAccountId(acc.getId(), surveyId);

		if (result > 0) {
			isTake = true;
		}
		return isTake;
	}

	@Transactional
	@Override
	public void saveAnswer(SurveyAnswerInforDTO dto) {
		List<Answer> savedAnswer = new ArrayList<Answer>();
		Optional<Survey> opSurvey = surveyRep.findWorkplaceSurveyById(dto.getSurveyId(),getUserContext().getWorkplaceId());
		if (opSurvey.isPresent()) {
			Survey sur = opSurvey.get();
			sur.setReceivedAnswer(sur.getReceivedAnswer() + 1);
			surveyRep.save(sur);
			Account acc = accRep.findByEmail(getUserContext().getUsername()).get();
			dto.getAnswers().forEach(a -> {
				Answer entity = new Answer();
				entity.setAccount(acc);
				entity.setContent(a.getContent());
				SurveyQuestion sq = new SurveyQuestion();
				sq.setId(a.getQuestionIdentity());
				entity.setSurveyQuestion(sq);
				entity.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
				if (a.getQuestionType().equals(TypeEnum.TEXT.toString())) {
					entity.setWordClouds(filterSer.rawTextToWordCloud(a.getContent(), defaultFilterEN, entity));
				} else if (a.getQuestionType().equals(TypeEnum.MULTIPLE.toString())) {
					String[] options = a.getContent().split(",");
					List<MultipleAnswer> multipleAnswers = new ArrayList<MultipleAnswer>();
					for (int i = 0; i < options.length; i++) {
						MultipleAnswer multipleAnswer = new MultipleAnswer();
						multipleAnswer.setAnswerOption(options[i]);
						multipleAnswer.setAnswer(entity);
						multipleAnswers.add(multipleAnswer);
					}
					entity.setMultiple(multipleAnswers);
				}
				savedAnswer.add(entity);
			});
			answerRep.saveAll(savedAnswer);
		}
	}

	@Override
	public List<Survey> getWorkplaceSurvey(int workplaceId) {

		return surveyRep.findAllByWorkplaceAndIsDeleted(workplaceId, false);
	}

	@Override
	public List<Survey> getSurveyByQuestionId(int id, int notId) {
		List<Integer> notIdList = new ArrayList<Integer>();
		notIdList.add(notId);
		return surveyRep.findAllByQuestionId(id, notIdList);
	}

	// change active status of a survey to false
	@Override
	public Survey updateStatus(Survey survey) {
		survey.setActive(false);

		return surveyRep.save(survey);
	}

	// get all active survey with end date is today or before
	@Override
	public List<Survey> getActiveSurveyByDate(Date date) {
		List<Survey> result = surveyRep.findAllByDateStopAndIsActiveAndIsDeleted(date, true, false);

		return result;
	}

	@Override
	public Page<Survey> loadTemplateSurvey(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return surveyRep.findAllTemplateSurvey(term, getUserContext().getWorkplaceId(), pageRequest);

	}

	@Override
	public Page<Survey> getHistorySurveyWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		Set<Answer> answerList = answerRep
				.findAllByAccountId(accRep.findAccountByEmail(getUserContext().getUsername()).get().getId());
		return surveyRep.findAllByAnswersAndIsDeleted(false, term, answerList, pageRequest);
	}

	@Override
	public List<QuestionDTO> getTakeSurveyHistory(int id) {
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
		return qDTOs;
	}

	@Override
	public Date getDateTakenSurvey(int surveyId) {
		Survey survey = surveyRep.findById(surveyId).get();
		Set<SurveyQuestion> setSurQues = survey.getSurveyQuestions();
		SurveyQuestion surQues = new SurveyQuestion();
		for (SurveyQuestion tmp : setSurQues) {
			surQues = tmp;
			break;
		}
		Set<Answer> setAnswer = surQues.getAnswers();
		Answer answer = new Answer();
		for (Answer tmp : setAnswer) {
			answer = tmp;
			break;
		}
		return answer.getDateCreated();
	}

	@Override
	public List<AnswerDTO> getAnswerBySurveyId(int surveyId) {
		int accountId = accRep.findAccountByEmail(getUserContext().getUsername()).get().getId();
		List<Answer> answerList = answerRep.findAllByAccountIdAndSurveyId(surveyId, accountId);
		List<AnswerDTO> answerDTO = new ArrayList<>();
		for (Answer tmp : answerList) {
			AnswerDTO dto = new AnswerDTO();
			BeanUtils.copyProperties(tmp, dto);
			answerDTO.add(dto);
		}
		return answerDTO;
	}

	private boolean checkIfUserCanTakeSurvey(Integer surveyId) {
		List<SurveySendTarget> targets = targetRep.findAllBySurveyIdAndIsNeed(surveyId, true);
		Account curAcc = accRep.findAccountByEmail(getUserContext().getUsername()).get();
		for (SurveySendTarget target : targets) {
			Location location = target.getLocation();
			Department dep = target.getDepartment();
			Team team = target.getTeam();
			if (location == null && dep == null && team == null) {
				if (curAcc.getLocation() != null && !curAcc.getTeams().isEmpty())
					return true;
			} else if (location != null && dep == null && team == null) {
				if (curAcc.getLocation().equals(location))
					return true;
			} else if (location == null && dep != null && team == null) {
				for (Team t : curAcc.getTeams()) {
					if (t.getDepartment().equals(dep))
						return true;
				}
			} else if (location != null && dep != null && team == null) {
				boolean hasLocation = false;
				boolean hasDepartment = false;
				if (curAcc.getLocation().equals(location))
					hasLocation = true;
				for (Team t : curAcc.getTeams()) {
					if (t.getDepartment().equals(dep)) {
						hasDepartment = true;
						break;
					}
				}
				if (hasLocation && hasDepartment)
					return true;
			} else if (dep != null && team != null) {
				for (Team t : curAcc.getTeams()) {
					if (t.equals(team))
						return true;
				}
			}

		}
		return false;
	}

	@Override
	public void updateActiveStatus(Integer id, boolean isActive) {
		Optional<Survey> opSurvey = surveyRep.findWorkplaceSurveyById(id, getUserContext().getWorkplaceId());
		if (opSurvey.isPresent()) {
			Survey survey = opSurvey.get();
			survey.setActive(isActive);
			surveyRep.save(survey);
		}
	}
}

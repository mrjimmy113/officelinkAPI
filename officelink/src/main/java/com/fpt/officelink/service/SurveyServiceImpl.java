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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.AnswerOptionDTO;
import com.fpt.officelink.dto.AnswerReportDTO;
import com.fpt.officelink.dto.DepartmentDTO;
import com.fpt.officelink.dto.LocationDTO;
import com.fpt.officelink.dto.QuestionDTO;
import com.fpt.officelink.dto.QuestionReportDTO;
import com.fpt.officelink.dto.SendSurveyDTO;
import com.fpt.officelink.dto.SurveyAnswerInforDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;
import com.fpt.officelink.dto.TeamDTO;
import com.fpt.officelink.dto.TypeQuestionDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.AnswerReport;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.TeamQuestionReport;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.entity.WordCloud;
import com.fpt.officelink.entity.Workplace;
import com.fpt.officelink.mail.service.MailService;
import com.fpt.officelink.repository.AccountRespository;
import com.fpt.officelink.repository.AnswerOptionRepository;
import com.fpt.officelink.repository.AnswerRepository;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.fpt.officelink.repository.TeamQuestionReportRepository;
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
	TeamQuestionReportRepository reportRep;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void newSurvey(Survey survey, List<SurveyQuestion> sqList) {
		survey.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
		Workplace workplace = new Workplace();
		workplace.setId(getUserContext().getWorkplaceId());
		survey.setWorkplace(workplace);
		surveyRep.save(survey);
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
			sq.setSurvey(survey);
			surQuestRep.save(sq);
		});

	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void updateSurvey(Survey survey, List<SurveyQuestion> sqList) {
		survey.setDateModified(new Date(Calendar.getInstance().getTimeInMillis()));
		surveyRep.save(survey);
		System.out.println("delete");
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
			sq.setSurvey(survey);
			surQuestRep.save(sq);
		});

	}

	@Override
	public Page<Survey> searchWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return surveyRep.findAllByNameContainingAndIsDeleted(term, false, pageRequest);
	}

	@Override
	public Page<Survey> searchReportWithPagination(String term, int pageNum) {
		PageRequest pageRequest = PageRequest.of(pageNum, PAGEMAXSIZE);
		return surveyRep.findAllByNameContainingAndWorkplaceIdAndIsDeletedAndIsActive(term,
				getUserContext().getWorkplaceId(), false, true, pageRequest);
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
	public List<SurveyQuestion> getDetail(Integer id) {
		return surQuestRep.findAllBySurveyId(id);
	}

	@Override
	public SurveyDTO getTakeSurvey(String token) throws ParseException {
		SurveyDTO result = null;
		if (jwtSer.validateTakeSurveyToken(token)) {
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
	public String getSurveyToken(Integer surveyId) throws JOSEException {
		return jwtSer.createSurveyToken(surveyId);
	}

	@Override
	@Transactional(rollbackOn = Exception.class)
	@Async
	public void sendOutSurvey(SendSurveyDTO sendInfor, int workplaceId) throws JOSEException {
		Optional<Survey> opSurvey = surveyRep.findById(sendInfor.getSurveyId());
		if (opSurvey.isPresent()) {
			Survey survey = opSurvey.get();
			survey.setActive(true);
			survey.setDateSendOut(new Date(Calendar.getInstance().getTimeInMillis()));
			survey.setDateStop(new Date(sendInfor.getExpireDate()));
			Set<Account> sendList = new HashSet<Account>();
			List<SurveySendTarget> sendTargets = new ArrayList<SurveySendTarget>();
			SurveySendTarget targetEntity = new SurveySendTarget();
			targetEntity.setSurvey(survey);
			sendInfor.getTargetList().forEach(e -> {
				Location location = new Location();
				Department department = new Department();
				Team team = new Team();
				if (e.getDepartmentId().equals(0) && e.getLocationId().equals(0)) {
					sendList.addAll(accRep.findAllByWorkplaceIdAndIsDeleted(workplaceId, false));
				} else if (!e.getDepartmentId().equals(0) && e.getLocationId().equals(0)) {
					sendList.addAll(accRep.findAllEmailByDepartmentId(e.getDepartmentId(), workplaceId, false));
					department.setId(e.getDepartmentId());
				} else if (e.getDepartmentId().equals(0) && !e.getLocationId().equals(0)) {
					sendList.addAll(accRep.findAllEmailByLocationId(e.getLocationId(), workplaceId, false));
					location.setId(e.getLocationId());
				} else if (!e.getDepartmentId().equals(0) && !e.getLocationId().equals(0)) {
					sendList.addAll(accRep.findAllEmailByLocationIdAndDepartmentId(e.getDepartmentId(),
							e.getLocationId(), workplaceId, false));
					department.setId(e.getDepartmentId());
					location.setId(e.getLocationId());
				} else if (!e.getTeamId().equals(0)) {
					sendList.addAll(accRep.findAllEmailByTeamId(e.getTeamId(), workplaceId, false));
					department.setId(e.getDepartmentId());
					location.setId(e.getLocationId());
					team.setId(e.getTeamId());
				}
				targetEntity.setLocation(location);
				targetEntity.setDepartment(department);
				targetEntity.setTeam(team);
				sendTargets.add(targetEntity);
			});

			String token = jwtSer.createSurveyToken(sendInfor.getSurveyId());
			List<String> emailList = new ArrayList<String>();
			sendList.forEach(e -> {
				emailList.add(e.getEmail());
			});
			survey.setSentOut(emailList.size());
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("link", "http://localhost:4200/take/" + token);
			mailSer.sendMail(emailList.toArray(new String[emailList.size()]), "email-survey.ftl", model);
			surveyRep.save(survey);
			targetRep.saveAll(sendTargets);

		}

	}

	@Override
	public boolean checkIfUserTakeSurvey() {
		boolean isTake = false;
		Account acc = (accRep.findByEmail(getUserContext().getUsername())).get();
		int result = answerRep.countAnswerByAccountId(acc.getId());

		if (result > 0) {
			isTake = true;
		}
		System.out.println(isTake);
		return isTake;
	}

	@Transactional
	@Override
	public void saveAnswer(SurveyAnswerInforDTO dto) {
		List<Answer> savedAnswer = new ArrayList<Answer>();
		Optional<Survey> opSurvey = surveyRep.findById(dto.getSurveyId());
		if (opSurvey.isPresent()) {
			Survey sur = opSurvey.get();
			sur.setReceivedAnswer(sur.getReceivedAnswer() + 1);
			surveyRep.save(sur);
			dto.getAnswers().forEach(a -> {
				Answer entity = new Answer();
				Account acc = (accRep.findByEmail(getUserContext().getUsername())).get();
				entity.setAccount(acc);
				entity.setContent(a.getContent());
				entity.setSurveyQuestion(surQuestRep.findById(a.getQuestionIdentity()).get());
				entity.setDateCreated(new Date(Calendar.getInstance().getTimeInMillis()));
				if (a.getContent().matches(".*[a-zA-Z]+.*")) {
					entity.setWordClouds(filterSer.rawTextToWordCloud(a.getContent(), 1, entity));
				}
				savedAnswer.add(entity);
			});
			answerRep.saveAll(savedAnswer);
		}
	}

	@Override
	public SurveyReportDTO getReport(Integer id) {
		SurveyReportDTO result = new SurveyReportDTO();
		Optional<Survey> survey = surveyRep.findById(id);
		if (survey.isPresent()) {
			BeanUtils.copyProperties(survey.get(), result);
			List<SurveyQuestion> sqList = surQuestRep.findAllBySurveyId(id);
			result.setQuestions(getQuestionReports(sqList));
		}

		return result;
	}

	@Override
	public List<Survey> getWorkplaceSurvey(int workplaceId) {

		return surveyRep.findAllByWorkplaceAndIsDeleted(workplaceId, false);
	}

	public SurveySendDetailDTO getSendDetail(int surveyId) {
		SurveySendDetailDTO result = new SurveySendDetailDTO();
		List<SurveySendTarget> targets = targetRep.findAllBySurveyId(surveyId);
		List<LocationDTO> locations = new ArrayList<LocationDTO>();
		List<DepartmentDTO> departments = new ArrayList<DepartmentDTO>();
		List<TeamDTO> teams = new ArrayList<TeamDTO>();
		Optional<Account> currentLogin = accRep.findByEmail(getUserContext().getUsername());

		if (currentLogin.isPresent()) {
			Account currentAcc = currentLogin.get();
			for (SurveySendTarget e : targets) {
				// Check team
				boolean isFound = false;
				if (e.getTeam() != null) {
					for (Team t : currentAcc.getTeams()) {
						if (e.getTeam().getId().equals(t.getId())) {
							TeamDTO teamDTO = new TeamDTO();
							BeanUtils.copyProperties(e.getTeam(), teamDTO);
							teams.add(teamDTO);
							isFound = true;
							break;
						}
						if (e.getDepartment().getId().equals(t.getDepartment().getId())) {
							DepartmentDTO departmentDTO = new DepartmentDTO();
							BeanUtils.copyProperties(e.getDepartment(), departmentDTO);
							departments.add(departmentDTO);
							isFound = true;
							break;
						}
					}
				}
				if (isFound)
					continue;

				if (e.getLocation() != null) {
					if (e.getLocation().getId().equals(currentAcc.getLocation().getId())) {
						LocationDTO locationDTO = new LocationDTO();
						BeanUtils.copyProperties(e.getLocation(), locationDTO);
						locations.add(locationDTO);
						continue;
					}
				}

			}
		}
		return result;
	}

	@Override
	public List<QuestionReportDTO> getFilteredReport(int surveyId, int locationId, int departmentId, int teamId) {
		List<QuestionReportDTO> result = new ArrayList<QuestionReportDTO>();
		int workplaceId = getUserContext().getWorkplaceId();
		if (locationId == 0 && departmentId == 0) {
			result = getQuestionReports(surQuestRep.findAllBySurveyId(surveyId));
		} else if (locationId != 0 && departmentId == 0) {
			result = getQuestionReports(surQuestRep.findAllByLocationId(locationId, workplaceId, surveyId));
		} else if (locationId == 0 && departmentId != 0) {
			result = getQuestionReports(surQuestRep.findAllByDepartmentId(departmentId, workplaceId, surveyId));
		} else if (locationId != 0 && departmentId != 0) {
			result = getQuestionReports(
					surQuestRep.findAllByLocationIdAndDepartmentId(departmentId, locationId, workplaceId, surveyId));
		} else if (teamId != 0) {
			result = getQuestionReports(surQuestRep.findAllByTeamId(teamId, workplaceId, surveyId));
		}
		return result;
	}

	// Report: Output
	private List<QuestionReportDTO> getQuestionReports(List<SurveyQuestion> sqList) {
		List<QuestionReportDTO> qrList = new ArrayList<QuestionReportDTO>();
		sqList.forEach(sq -> {
			QuestionReportDTO qr = new QuestionReportDTO();
			Question e = sq.getQuestion();
			// Change Question to Question DTO
			QuestionDTO dto = new QuestionDTO();
			BeanUtils.copyProperties(e, dto, "type", "options");
			List<AnswerOptionDTO> opList = new ArrayList<AnswerOptionDTO>();
			e.getOptions().forEach(op -> {
				AnswerOptionDTO opDto = new AnswerOptionDTO();
				BeanUtils.copyProperties(op, opDto);
				opList.add(opDto);
			});
			dto.setOptions(opList);
			TypeQuestionDTO typeDto = new TypeQuestionDTO();
			BeanUtils.copyProperties(e.getType(), typeDto);
			dto.setType(typeDto);
			// Set Question
			qr.setQuestion(dto);
			// Get each question report
			List<AnswerReportDTO> arList = new ArrayList<AnswerReportDTO>();
			switch (sq.getQuestion().getType().getType()) {
			case MULTIPLE:
				arList = getMutipleChoiceReport(new ArrayList<Answer>(sq.getAnswers()));
				break;
			case SINGLE:
				arList = getSingleChoiceReport(new ArrayList<Answer>(sq.getAnswers()));
				break;
			case TEXT:
				arList = getFreeTextReport(new ArrayList<Answer>(sq.getAnswers()));
				break;

			}
			qr.setAnswers(arList);
			qrList.add(qr);
		});
		return qrList;
	}

	// Report: Free Text
	private List<AnswerReportDTO> getFreeTextReport(List<Answer> answers) {
		List<AnswerReportDTO> result = new ArrayList<AnswerReportDTO>();
		boolean isFound;
		for (Answer a : answers) {
			for (WordCloud w : a.getWordClouds()) {
				isFound = false;
				for (AnswerReportDTO r : result) {
					if (r.getTerm().equalsIgnoreCase(w.getWord())) {
						r.setWeight(r.getWeight() + w.getTimes());
						isFound = true;
						break;
					}
				}
				if (!isFound) {
					result.add(new AnswerReportDTO(w.getWord(), w.getTimes()));
				}

			}

		}
		return result;
	}

	// Report: Single choice
	private List<AnswerReportDTO> getSingleChoiceReport(List<Answer> answers) {
		List<AnswerReportDTO> result = new ArrayList<AnswerReportDTO>();
		boolean isFound;
		for (Answer answer : answers) {
			isFound = false;
			for (AnswerReportDTO AnswerReportDTO : result) {
				if (answer.getContent().equalsIgnoreCase(AnswerReportDTO.getTerm())) {
					AnswerReportDTO.setWeight(AnswerReportDTO.getWeight() + 1);
					isFound = true;
					break;
				}
			}
			if (!isFound)
				result.add(new AnswerReportDTO(answer.getContent(), 1));
		}
		return result;
	}

	// Report: Mutiple Choice
	private List<AnswerReportDTO> getMutipleChoiceReport(List<Answer> answers) {
		List<AnswerReportDTO> result = new ArrayList<AnswerReportDTO>();
		boolean isFound;
		for (Answer answer : answers) {
			String[] options = answer.getContent().split(",");
			for (int i = 0; i < options.length; i++) {
				isFound = false;
				for (AnswerReportDTO AnswerReportDTO : result) {
					if (options[i].equalsIgnoreCase(AnswerReportDTO.getTerm())) {
						AnswerReportDTO.setWeight(AnswerReportDTO.getWeight() + 1);
						isFound = true;
						break;
					}
				}
				if (!isFound)
					result.add(new AnswerReportDTO(options[i], 1));
			}
		}
		return result;
	}

	// change active status of a survey to false
	@Override
	public void updateStatus(Survey survey) {
		survey.setActive(false);
		surveyRep.save(survey);
	}

	// get all active survey with end date is today or before
	@Override
	public List<Survey> getActiveSurveyByDate(java.util.Date date) {
		List<Survey> result = surveyRep.findAllByDateStopAndIsActiveAndIsDeleted(date, true, false);

		return result;
	}

	@Async
	@Override
	public void generateTeamQuestionReport(int surveyId) {
		// get list of teams that this survey was send to
		List<Team> teams = targetRep.getSurveyTeams(surveyId);

		// Get survey question of each team
		for (Team team : teams) {
			TeamQuestionReport questionReport = new TeamQuestionReport();
			List<SurveyQuestion> teamSurveyQuestions = surQuestRep.findAllByTeamIdOnly(team.getId(), surveyId);

			// set team for report
			for (SurveyQuestion sQuestion : teamSurveyQuestions) {
				questionReport.setTeam(team);
				questionReport.setSurveyQuestion(sQuestion);
				
				// get answer report
				List<AnswerReport> arList = new ArrayList<AnswerReport>();
				switch (sQuestion.getQuestion().getType().getType()) {
				case MULTIPLE:
					arList = generateMutipleChoiceReport(new ArrayList<Answer>(sQuestion.getAnswers()));
					break;
				case SINGLE:
					arList = generateSingleChoiceReport(new ArrayList<Answer>(sQuestion.getAnswers()));
					break;
				case TEXT:
					arList = generateFreeTextReport(new ArrayList<Answer>(sQuestion.getAnswers()));
					break;
				}
				
				questionReport.setAnswerReports(arList);
				reportRep.save(questionReport);
			}
		}
	}

	// Report: Free Text
	private List<AnswerReport> generateFreeTextReport(List<Answer> answers) {
		List<AnswerReport> result = new ArrayList<AnswerReport>();
		boolean isFound;
		for (Answer a : answers) {
			for (WordCloud w : a.getWordClouds()) {
				isFound = false;
				for (AnswerReport r : result) {
					if (r.getTerm().equalsIgnoreCase(w.getWord())) {
						r.setWeight(r.getWeight() + w.getTimes());
						isFound = true;
						break;
					}
				}
				if (!isFound) {
					result.add(new AnswerReport(w.getWord(), w.getTimes()));
				}

			}

		}
		return result;
	}

	// Report: Single choice
	private List<AnswerReport> generateSingleChoiceReport(List<Answer> answers) {
		List<AnswerReport> result = new ArrayList<AnswerReport>();
		boolean isFound;
		for (Answer answer : answers) {
			isFound = false;
			for (AnswerReport ansReport : result) {
				if (answer.getContent().equalsIgnoreCase(ansReport.getTerm())) {
					ansReport.setWeight(ansReport.getWeight() + 1);
					isFound = true;
					break;
				}
			}
			if (!isFound)
				result.add(new AnswerReport(answer.getContent(), 1));
		}
		return result;
	}

	// Report: Mutiple Choice
	private List<AnswerReport> generateMutipleChoiceReport(List<Answer> answers) {
		List<AnswerReport> result = new ArrayList<AnswerReport>();
		boolean isFound;
		for (Answer answer : answers) {
			String[] options = answer.getContent().split(",");
			for (int i = 0; i < options.length; i++) {
				isFound = false;
				for (AnswerReport AnswerReport : result) {
					if (options[i].equalsIgnoreCase(AnswerReport.getTerm())) {
						AnswerReport.setWeight(AnswerReport.getWeight() + 1);
						isFound = true;
						break;
					}
				}
				if (!isFound)
					result.add(new AnswerReport(options[i], 1));
			}
		}
		return result;
	}

}

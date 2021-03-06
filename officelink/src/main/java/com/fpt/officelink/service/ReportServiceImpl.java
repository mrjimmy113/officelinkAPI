package com.fpt.officelink.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.servlet.ServletContext;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.fpt.officelink.dto.AnswerOptionDTO;
import com.fpt.officelink.dto.CategoryDTO;
import com.fpt.officelink.dto.CategoryReportDTO;
import com.fpt.officelink.dto.DashBoardDTO;
import com.fpt.officelink.dto.DepartmentDTO;
import com.fpt.officelink.dto.ImageNewsDTO;
import com.fpt.officelink.dto.LocationDTO;
import com.fpt.officelink.dto.QuestionDTO;
import com.fpt.officelink.dto.QuestionReportDTO;
import com.fpt.officelink.dto.SendTargetDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;
import com.fpt.officelink.dto.TeamDTO;
import com.fpt.officelink.dto.TypeQuestionDTO;
import com.fpt.officelink.entity.Account;
import com.fpt.officelink.entity.AnswerReport;
import com.fpt.officelink.entity.Category;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.News;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.enumaration.TypeEnum;
import com.fpt.officelink.repository.AccountRespository;
import com.fpt.officelink.repository.AnswerReportRepository;
import com.fpt.officelink.repository.DepartmentRepository;
import com.fpt.officelink.repository.LocationRepository;
import com.fpt.officelink.repository.MultipleAnswerRepository;
import com.fpt.officelink.repository.NewsRepository;
import com.fpt.officelink.repository.PointRepository;
import com.fpt.officelink.repository.QuestionRepository;
import com.fpt.officelink.repository.SingleChoiceRepository;
import com.fpt.officelink.repository.SingleChoiceRepository.TmpReport;
import com.fpt.officelink.repository.SurveyQuestionRepository;
import com.fpt.officelink.repository.SurveyRepository;
import com.fpt.officelink.repository.SurveySendTargetRepository;
import com.fpt.officelink.repository.TeamRepository;
import com.fpt.officelink.repository.WordCloudRepository;
import com.nimbusds.jose.JOSEException;

@Service
public class ReportServiceImpl implements ReportService {

	private static final int NUMBEROFACCOUNT = 5;
	
	
	@Autowired
	AccountRespository accRep;

	@Autowired
	SurveySendTargetRepository targetRep;

	@Autowired
	LocationRepository locationRep;

	@Autowired
	DepartmentRepository depRep;

	@Autowired
	TeamRepository teamRep;

	@Autowired
	SurveyRepository surRep;

	@Autowired
	NewsRepository newsRep;

	@Autowired
	ServletContext context;

	@Autowired
	SurveyQuestionRepository surQuestRep;

	@Autowired
	JwtService jwtSer;

	@Autowired
	SingleChoiceRepository singleRep;

	@Autowired
	MultipleAnswerRepository multiRep;

	@Autowired
	WordCloudRepository textRep;

	@Autowired
	SurveyRepository surveyRep;

	@Autowired
	AnswerReportRepository answerReportRep;

	@Autowired
	PointRepository pointRep;

	@Autowired
	QuestionRepository questRep;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Override
	public SurveySendDetailDTO getSendDetail(int surveyId) {
		SurveySendDetailDTO result = new SurveySendDetailDTO();
		Survey survey = surRep.findById(surveyId).get();
		List<SurveySendTarget> targets = null;
		if (survey.getTemplateId() != null) {
			targets = targetRep.findAllBySurveyIdAndIsNeed(survey.getTemplateId(), true);
		} else {
			targets = targetRep.findAllBySurveyIdAndIsNeed(surveyId, true);
		}
		Set<Location> locations = new HashSet<Location>();
		Set<Department> departments = new HashSet<Department>();
		Set<Team> teams = new HashSet<Team>();
		Optional<Account> currentLogin = accRep.findByEmail(getUserContext().getUsername());
		int workplaceId = getUserContext().getWorkplaceId();

		if (currentLogin.isPresent()) {
			Account currentAcc = currentLogin.get();
			boolean isAll = false;
			for (SurveySendTarget target : targets) {
				if (target.getDepartment() == null && targets.get(0).getLocation() == null
						&& targets.get(0).getTeam() == null) {
					isAll = true;
					break;
				}
			}

			if (!isAll) {
				for (SurveySendTarget target : targets) {
					if (target.getDepartment() != null && target.getLocation() == null && target.getTeam() == null) {
						departments.add(target.getDepartment());
						locations.addAll(locationRep.findAllByDepartmentId(target.getDepartment().getId()));
						teams.addAll(teamRep.findAllByDepartmentId(target.getDepartment().getId()));
					} else if (target.getDepartment() == null && target.getLocation() != null
							&& target.getTeam() == null) {
						locations.add(target.getLocation());
						departments.addAll(depRep.findAllByLocationId(target.getLocation().getId()));
						teams.addAll(teamRep.findAllByLocationId(target.getLocation().getId()));
					} else if (target.getDepartment() != null && target.getLocation() != null
							&& target.getTeam() == null) {
						locations.add(target.getLocation());
						departments.add(target.getDepartment());
						teams.addAll(teamRep.findAllByLocationIdAndDepartmentId(target.getLocation().getId(),
								target.getDepartment().getId()));
					} else if (target.getDepartment() != null && target.getTeam() != null) {
						teams.add(target.getTeam());
					}

				}
			}

			switch (currentAcc.getRole().getRole()) {
			case "employer": {
				if (isAll) {
					locations.addAll(this.locationRep.findAllByWorkplaceIdAndIsDeleted(workplaceId, false));
					departments.addAll(this.depRep.findAllByWorkplaceIdAndIsDeleted(workplaceId, false));
					teams.addAll(this.teamRep.findAllByWorkplaceId(workplaceId, false));
				}
				break;
			}

			case "employee": {
				if (isAll) {
					locations.add(currentAcc.getLocation());
					for (Team team : currentAcc.getTeams()) {
						departments.add(team.getDepartment());
					}
					teams.addAll(currentAcc.getTeams());
				} else {
					Set<Location> tmpLocations = new HashSet<Location>();
					Set<Department> tmpDeps = new HashSet<Department>();
					Set<Team> tmpTeams = new HashSet<Team>();
					if (locations.contains(currentAcc.getLocation())) {
						tmpLocations.add(currentAcc.getLocation());
					}
					for (Team team : currentAcc.getTeams()) {
						if (departments.contains(team.getDepartment())) {
							tmpDeps.add(team.getDepartment());
						}
						if (teams.contains(team)) {
							tmpTeams.add(team);
						}
					}
					locations = tmpLocations;
					departments = tmpDeps;
					teams = tmpTeams;
				}
				break;
			}

			}
			List<LocationDTO> locationDTOs = new ArrayList<LocationDTO>();
			List<DepartmentDTO> departmentDTOs = new ArrayList<DepartmentDTO>();
			List<TeamDTO> teamDTOs = new ArrayList<TeamDTO>();
			locations.forEach(e -> {
				LocationDTO dto = new LocationDTO();
				BeanUtils.copyProperties(e, dto);
				locationDTOs.add(dto);
			});
			departments.forEach(e -> {
				DepartmentDTO dto = new DepartmentDTO();
				BeanUtils.copyProperties(e, dto);
				departmentDTOs.add(dto);
			});
			teams.forEach(e -> {
				TeamDTO dto = new TeamDTO();
				BeanUtils.copyProperties(e, dto);
				teamDTOs.add(dto);
			});
			result.setLocations(locationDTOs);
			result.setDepartments(departmentDTOs);
			result.setTeams(teamDTOs);
		}
		return result;
	}

	@Override
	public DashBoardDTO getDashBoard(Integer id) throws IOException {
		DashBoardDTO result = new DashBoardDTO();
		PageRequest top1 = PageRequest.of(0, 1);
		result.setAccount(accRep.countByWorkplaceId(id));
		result.setTeam(teamRep.countByWorkplaceId(id));
		result.setDepartment(depRep.countDepartmentOnWorkplace(id));
		List<LocationDTO> res = new ArrayList<>();
		List<Location> resultLocation = locationRep.findAllByWorkplaceIdAndIsDeleted(id, false);
		resultLocation.forEach(element -> {
			LocationDTO dto = new LocationDTO();
			BeanUtils.copyProperties(element, dto);
			res.add(dto);
		});
		result.setLocation(res);

		List<News> newsList = newsRep.findLastestNews(id, top1).getContent();
		if (!newsList.isEmpty()) {
			ImageNewsDTO dto = new ImageNewsDTO();
			BeanUtils.copyProperties(newsList.get(0), dto);
			String tmp = context.getRealPath("") + "image\\" + dto.getImage();
			Path byteImage = Paths.get(tmp);
			byte[] data = Files.readAllBytes(byteImage);
			dto.setByte_image(data);
			result.setNews(dto);
		}
		if (surRep.countSendOutSurvey(id) > 0) {
			result.setEndTutorial(true);
		}

		return result;
	}

	@Override
	public Optional<SurveyQuestion> getDownloadDetail(String token) throws ParseException {
		if (jwtSer.validateDownLoadToken(token)) {
			Integer surveyId = jwtSer.getSurveyId(token);
			Integer questionId = jwtSer.getQuestionId(token);
			return surQuestRep.findBySurveyIdAndQuestionId(surveyId, questionId);
		}
		return null;
	}

	@Override
	public String getDownLoadToken(Integer surveyId, Integer questionId) throws JOSEException {
		return jwtSer.createDownloadToken(surveyId, questionId);
	}

	@Override
	public SurveyReportDTO getReport(Integer id) {
		SurveyReportDTO result = new SurveyReportDTO();
		Optional<Survey> survey = surveyRep.findById(id);
		if (survey.isPresent()) {
			BeanUtils.copyProperties(survey.get(), result);
			List<SendTargetDTO> sendSurveyDTOs = new ArrayList<SendTargetDTO>();
			for (SurveySendTarget target : survey.get().getTargets()) {
				SendTargetDTO dto = new SendTargetDTO();
				Department tmpDep = target.getDepartment();
				Location tmpLocation = target.getLocation();
				Team tmpTeam = target.getTeam();

				if (tmpDep != null)
					dto.setDepartmentName(tmpDep.getName());
				else
					dto.setDepartmentName("");

				if (tmpLocation != null)
					dto.setLocationName(tmpLocation.getName());
				else
					dto.setLocationName("");

				if (tmpTeam != null)
					dto.setTeamName(tmpTeam.getName());
				else
					dto.setTeamName("");

				sendSurveyDTOs.add(dto);
			}
			result.setSendTargets(sendSurveyDTOs);
		}
		result.setCategories(getFilteredCateReport(id, 0, 0, 0));

		return result;
	}

	@Override
	public List<QuestionReportDTO> getFilteredReport(int surveyId, int locationId, int departmentId, int teamId) {
		Optional<Survey> opSurvey = surveyRep.findById(surveyId);
		List<QuestionReportDTO> result = new ArrayList<QuestionReportDTO>();
		if (opSurvey.isPresent()) {
			List<SurveyQuestion> sqList = surQuestRep.findAllBySurveyId(surveyId);
			for (SurveyQuestion sq : sqList) {
				Function<Integer, Float> method = getPointFunction(locationId, departmentId, teamId);
				result.add(getQuestionDTO(sq.getId(), sq.getQuestion(), method));
			}
		}
		return result;
	}

	@Override
	public List<CategoryReportDTO> getFilteredCateReport(int surveyId, int locationId, int departmentId, int teamId) {
		if(getNumberOfSent(locationId, departmentId, teamId) < NUMBEROFACCOUNT) {
			return null;
		}
		Optional<Survey> opSurvey = surveyRep.findById(surveyId);
		List<CategoryReportDTO> result = new ArrayList<CategoryReportDTO>();
		if (opSurvey.isPresent()) {
			result = getCateReport(surveyId, locationId, departmentId, teamId);
		}
		return result;
	}

	@Override
	public List<AnswerReport> getAnswerReport(int surveyId, int questionId, int locationId, int departmentId,
			int teamId) {
		List<AnswerReport> result = new ArrayList<AnswerReport>();
		SurveyQuestion surveyQuestion = surQuestRep.findBySurveyIdAndQuestionId(surveyId, questionId).get();
		result = getReportFunction(locationId, departmentId, teamId, surveyQuestion.getQuestion().getType().getType())
				.apply(surveyQuestion.getId());
		return result;
	}

	private Function<Integer, List<AnswerReport>> getReportAllFunction(TypeEnum type) {
		Function<Integer, List<AnswerReport>> method = null;
		Account acc = accRep.findByEmail(getUserContext().getUsername()).get();
		switch (acc.getRole().getRole()) {
		case "employer":
			switch (type) {
			case RATE:
			case VAS:
			case SINGLE:
				method = indentity -> singleRep.findAllByIndentity(indentity);
				break;
			case MULTIPLE:
				method = indentity -> multiRep.findAllByIndentity(indentity);
				break;
			case TEXT:
				method = indentity -> textRep.findAllByIndentity(indentity);
				break;
			}
			break;

		case "employee":
			switch (type) {
			case RATE:
			case VAS:
			case SINGLE:
				method = indentity -> tmpToEntity(singleRep.findAllByIndentityAndLocationIdOrDepartmentId(indentity,
						acc.getLocation().getId(), depRep.findByAccountId(acc.getId()).get(0).getId()));
				break;
			case MULTIPLE:
				method = indentity -> multiRep.findAllByIndentityAndLocationIdOrDepartmentId(indentity,
						acc.getLocation().getId(), depRep.findByAccountId(acc.getId()).get(0).getId());
				break;
			case TEXT:
				method = indentity -> textRep.findAllByIndentityAndLocationIdOrDepartmentId(indentity,
						acc.getLocation().getId(), depRep.findByAccountId(acc.getId()).get(0).getId());
				break;
			}
			break;
		}
		return method;
	}

	private QuestionReportDTO getQuestionDTO(Integer identity, Question q, Function<Integer, Float> method) {
		QuestionReportDTO result = new QuestionReportDTO();
		result.setIdentity(identity);
		Float point = method.apply(identity);
		if (point == null) {
			result.setAvgPoint(-1);
		} else {
			result.setAvgPoint(point);
		}
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

		CategoryDTO cateDto = new CategoryDTO();
		BeanUtils.copyProperties(q.getCategory(), cateDto);
		dto.setCategory(cateDto);

		result.setQuestion(dto);
		return result;
	}

	private Function<Integer, List<AnswerReport>> getReportFunction(int locationId, int departmentId, int teamId,
			TypeEnum type) {
		Function<Integer, List<AnswerReport>> method = null;
		if (locationId == 0 && departmentId == 0 && teamId == 0) {
			method = getReportAllFunction(type);
			System.out.println("ALL");
		} else if (locationId != 0 && departmentId == 0 && teamId == 0) {
			switch (type) {
			case RATE:
			case VAS:
			case SINGLE:
				method = indentity -> singleRep.findAllByIndentityAndLocationId(indentity, locationId);
				break;
			case MULTIPLE:
				method = indentity -> multiRep.findAllByIndentityAndLocationId(indentity, locationId);
				break;
			case TEXT:
				method = indentity -> textRep.findAllByIndentityAndLocationId(indentity, locationId);
				break;
			}
			System.out.println("Location");
		} else if (locationId == 0 && departmentId != 0 && teamId == 0) {
			switch (type) {
			case RATE:
			case VAS:
			case SINGLE:
				method = indentity -> tmpToEntity(singleRep.findAllByIndentityAndDepartmentId(indentity, departmentId));
				break;
			case MULTIPLE:
				method = indentity -> multiRep.findAllByIndentityAndDepartmentId(indentity, departmentId);
				break;
			case TEXT:
				method = indentity -> textRep.findAllByIndentityAndDepartmentId(indentity, departmentId);
				break;
			}

			System.out.println("Department");
		} else if (locationId != 0 && departmentId != 0 && teamId == 0) {
			switch (type) {
			case RATE:
			case VAS:
			case SINGLE:
				method = indentity -> tmpToEntity(
						singleRep.findAllByIndentityAndLocationIdAndDepartmentId(indentity, locationId, departmentId));
				break;
			case MULTIPLE:
				method = indentity -> multiRep.findAllByIndentityAndLocationIdAndDepartmentId(indentity, locationId,
						departmentId);
				break;
			case TEXT:
				method = indentity -> textRep.findAllByIndentityAndLocationIdAndDepartmentId(indentity, locationId,
						departmentId);
				break;
			}
			System.out.println("Location - Department");
		} else if (teamId != 0) {
			switch (type) {
			case RATE:
			case VAS:
			case SINGLE:
				method = indentity -> singleRep.findAllByIndentityAndTeamId(indentity, teamId);
				break;
			case MULTIPLE:
				method = indentity -> multiRep.findAllByIndentityAndTeamId(indentity, teamId);
				break;
			case TEXT:
				method = indentity -> textRep.findAllByIndentityAndTeamId(indentity, teamId);
				break;
			}
			System.out.println("Team");
		}
		return method;
	}

	private Function<Integer, Float> getPointFunction(int locationId, int departmentId, int teamId) {
		Function<Integer, Float> method = null;
		if (locationId == 0 && departmentId == 0 && teamId == 0) {
			method = getAllPoint();
			System.out.println("ALL");
		} else if (locationId != 0 && departmentId == 0 && teamId == 0) {
			method = identity -> pointRep.findAllByIndentityAndLocationId(identity, locationId);
			System.out.println("Location");
		} else if (locationId == 0 && departmentId != 0 && teamId == 0) {
			method = identity -> pointRep.findAllByIndentityAndDepartmentId(identity, departmentId);
			System.out.println("Department");
		} else if (locationId != 0 && departmentId != 0 && teamId == 0) {
			method = identity -> pointRep.findAllByIndentityAndLocationIdAndDepartmentId(identity, locationId,
					departmentId);
			System.out.println("Location - Department");
		} else if (teamId != 0) {
			method = identity -> pointRep.findAllByIndentityAndTeamId(identity, teamId);
			System.out.println("Team");
		}
		return method;
	}

	private List<CategoryReportDTO> getCateReport(int surveyId, int locationId, int departmentId, int teamId) {
		List<CategoryReportDTO> result = new ArrayList<CategoryReportDTO>();
		List<SurveyQuestion> questions = surQuestRep.findAllBySurveyId(surveyId);
		for (SurveyQuestion surveyQuestion : questions) {
			boolean newCate = true;
			Category tmp = surveyQuestion.getQuestion().getCategory();
			for (CategoryReportDTO categoryReportDTO : result) {
				if (categoryReportDTO.getId() == tmp.getId()) {
					categoryReportDTO.getQuestions().add(getQuestionDTO(surveyQuestion.getId(),
							surveyQuestion.getQuestion(), getPointFunction(locationId, departmentId, teamId)));
					newCate = false;
					break;
				}
			}
			if (newCate) {
				CategoryReportDTO newOne = new CategoryReportDTO();
				BeanUtils.copyProperties(tmp, newOne);
				newOne.getQuestions().add(getQuestionDTO(surveyQuestion.getId(), surveyQuestion.getQuestion(),
						getPointFunction(locationId, departmentId, teamId)));
				result.add(newOne);
			}
		}

		return result;
	}

	private Function<Integer, Float> getAllPoint() {
		Function<Integer, Float> method = null;
		Account acc = accRep.findByEmail(getUserContext().getUsername()).get();
		switch (acc.getRole().getRole()) {
		case "employer":
			method = indentity -> pointRep.findAllByIndentity(indentity);
			break;
		case "employee":
			method = indentity -> pointRep.findAllByIndentityAndLocationIdOrDepartmentId(indentity,
					acc.getLocation().getId(), depRep.findByAccountId(acc.getId()).get(0).getId());
			break;
		}
		return method;
	}

	private List<AnswerReport> tmpToEntity(List<TmpReport> tmpReport) {
		List<AnswerReport> report = new ArrayList<AnswerReport>();
		for (TmpReport t : tmpReport) {
			AnswerReport r = new AnswerReport();
			r.setTerm(t.getTerm());
			r.setWeight((int) t.getWeight());
			report.add(r);
		}
		return report;
	}

	private int getNumberOfSent(int locationId, int departmentId, int teamId) {

		int numberOfAccount = 0;

		if (locationId == 0 && departmentId == 0 && teamId == 0) {
			numberOfAccount = accRep.findAllEmail(getUserContext().getWorkplaceId(), false).size();
		} else if (locationId != 0 && departmentId == 0 && teamId == 0) {
			numberOfAccount = accRep.findAllEmailByLocationId(locationId, getUserContext().getWorkplaceId(), false).size();
		} else if (locationId == 0 && departmentId != 0 && teamId == 0) {
			numberOfAccount = accRep.findAllEmailByDepartmentId(departmentId, getUserContext().getWorkplaceId(), false).size();
		} else if (locationId != 0 && departmentId != 0 && teamId == 0) {
			numberOfAccount = accRep.findAllEmailByLocationIdAndDepartmentId(departmentId, locationId, getUserContext().getWorkplaceId(), false).size();
		} else if (teamId != 0) {
			numberOfAccount = accRep.findAllEmailByTeamId(teamId, getUserContext().getWorkplaceId(), false).size();
		}

		return numberOfAccount;
	}

}

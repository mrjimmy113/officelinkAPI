package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpt.officelink.dto.AnswerDTO;
import com.fpt.officelink.dto.AnswerOptionDTO;
import com.fpt.officelink.dto.PageSearchDTO;
import com.fpt.officelink.dto.QuestionDTO;
import com.fpt.officelink.dto.SendSurveyDTO;
import com.fpt.officelink.dto.SurveyAnswerInforDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.dto.TypeQuestionDTO;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Department;
import com.fpt.officelink.entity.Location;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.SurveySendTarget;
import com.fpt.officelink.entity.Team;
import com.fpt.officelink.entity.TypeQuestion;
import com.fpt.officelink.service.ConfigurationService;
import com.fpt.officelink.service.SurveyService;

@Controller
@RequestMapping("/survey")
public class SurveyController {

    @Autowired
    SurveyService ser;

	@Autowired
	ConfigurationService configService;

	Logger log = Logger.getLogger(SurveyController.class.getName());

	private CustomUser user;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping("/getWorkplaceSurveys")
	public ResponseEntity<List<SurveyDTO>> getWorkplaceSurveys() {
		this.user = getUserContext();
		HttpStatus status = null;
		List<SurveyDTO> res = new ArrayList<SurveyDTO>();
		try {
			List<Survey> result = ser.getWorkplaceSurvey(user.getWorkplaceId());
    

			result.forEach(s -> {
				SurveyDTO dto = new SurveyDTO();
				BeanUtils.copyProperties(s, dto);
				res.add(dto);
			});

			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<List<SurveyDTO>>(res, status);
	}

	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping
	public ResponseEntity<PageSearchDTO<SurveyDTO>> search(@RequestParam("term") String term,
			@RequestParam("page") int page) {
		HttpStatus status = null;
		PageSearchDTO<SurveyDTO> res = new PageSearchDTO<SurveyDTO>();
		try {
			Page<Survey> result = ser.searchWithPagination(term, page);
			List<SurveyDTO> dtoList = new ArrayList<SurveyDTO>();
			result.getContent().forEach(s -> {
				SurveyDTO dto = new SurveyDTO();
				BeanUtils.copyProperties(s, dto);
				dtoList.add(dto);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(dtoList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<PageSearchDTO<SurveyDTO>>(res, status);
	}

	@GetMapping("/searchReport")
	public ResponseEntity<PageSearchDTO<SurveyReportDTO>> searchReport(@RequestParam("term") String term,
			@RequestParam("page") int page) {
		HttpStatus status = null;
		PageSearchDTO<SurveyReportDTO> res = new PageSearchDTO<SurveyReportDTO>();
		try {
			Page<Survey> result = ser.searchWithPagination(term, page);
			List<SurveyReportDTO> dtoList = new ArrayList<SurveyReportDTO>();
			result.getContent().forEach(s -> {
				SurveyReportDTO dto = new SurveyReportDTO();
				BeanUtils.copyProperties(s, dto);
				dtoList.add(dto);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(dtoList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<PageSearchDTO<SurveyReportDTO>>(res, status);
	}

	@Secured({"ROLE_employer","ROLE_system_admin"})
	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody SurveyDTO dto) {
		HttpStatus status = null;
		try {
			Survey survey = new Survey();
			BeanUtils.copyProperties(dto, survey);
			List<SurveyQuestion> sqList = new ArrayList<SurveyQuestion>();
			dto.getQuestions().forEach(q -> {
				Question tmpQ = new Question();
				SurveyQuestion tmpSQ = new SurveyQuestion();
				tmpSQ.setQuestionIndex(q.getQuestionIndex());
				List<AnswerOption> options = new ArrayList<AnswerOption>();
				if (q.getOptions() != null) {
					q.getOptions().forEach(o -> {
						AnswerOption tmpOp = new AnswerOption();
						BeanUtils.copyProperties(o, tmpOp);
						options.add(tmpOp);
					});
				}
				BeanUtils.copyProperties(q, tmpQ);
				tmpQ.setOptions(options);
				TypeQuestion tmpType = new TypeQuestion();
				BeanUtils.copyProperties(q.getType(), tmpType);
				tmpQ.setType(tmpType);
				tmpSQ.setQuestion(tmpQ);
				tmpSQ.setRequired(q.isRequired());
				sqList.add(tmpSQ);
			});
			boolean success = ser.newSurvey(survey, sqList);
			if (success) status = HttpStatus.OK;
			else status = HttpStatus.CONFLICT;

		} catch (Exception e) {
			log.info(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
     return new ResponseEntity<Integer>(status.value(), status);
	}
		
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@PutMapping
	public ResponseEntity<Integer> update(@RequestBody SurveyDTO dto) {
		HttpStatus status = null;
		try {
			Survey survey = new Survey();
			BeanUtils.copyProperties(dto, survey);
			List<SurveyQuestion> sqList = new ArrayList<SurveyQuestion>();
			dto.getQuestions().forEach(q -> {
				Question tmpQ = new Question();
				SurveyQuestion tmpSQ = new SurveyQuestion();
				tmpSQ.setQuestionIndex(q.getQuestionIndex());
				List<AnswerOption> options = new ArrayList<AnswerOption>();
				if (q.getOptions() != null) {
					q.getOptions().forEach(o -> {
						AnswerOption tmpOp = new AnswerOption();
						BeanUtils.copyProperties(o, tmpOp);
						options.add(tmpOp);
					});
				}
				BeanUtils.copyProperties(q, tmpQ);
				tmpQ.setOptions(options);
				TypeQuestion tmpType = new TypeQuestion();
				BeanUtils.copyProperties(q.getType(), tmpType);
				tmpQ.setType(tmpType);
				tmpSQ.setQuestion(tmpQ);
				tmpSQ.setRequired(q.isRequired());
				sqList.add(tmpSQ);
			});
			boolean success = ser.updateSurvey(survey, sqList);
			if (success) status = HttpStatus.OK;
			else status = HttpStatus.CONFLICT;
			status = HttpStatus.OK;
		} catch (Exception e) {
			log.info(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}

	@Secured({"ROLE_employer","ROLE_system_admin"})
	@DeleteMapping
	public ResponseEntity<Integer> delete(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		try {
			ser.delete(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}

	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping("/detail")
	public ResponseEntity<List<QuestionDTO>> getDetail(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		List<QuestionDTO> res = new ArrayList<QuestionDTO>();
		try {
			List<SurveyQuestion> result = ser.getDetail(id);
			result.forEach(r -> {
				Question e = r.getQuestion();
				QuestionDTO dto = new QuestionDTO();
				dto.setQuestionIdentity(r.getQuestionIndex());
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
				dto.setRequired(r.isRequired());
				res.add(dto);
			});
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<List<QuestionDTO>>(res, status);
	}

	@Secured({"ROLE_employee","ROLE_manager"})
	@GetMapping("/take")
	public ResponseEntity<SurveyDTO> getTakeSurvey(@RequestParam("token") String token) {
		HttpStatus status = null;
		SurveyDTO res = null;
		try {
			res = ser.getTakeSurvey(token);
			if (res != null) {

				status = HttpStatus.OK;
			} else {
				status = HttpStatus.CONFLICT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<SurveyDTO>(res, status);
	}

	@Secured({"ROLE_employer","ROLE_system_admin"})
	@PostMapping("/sendOut")
	public ResponseEntity<Number> sendOutSurvey(@RequestBody SendSurveyDTO target) {
		HttpStatus status = null;
		try {
			List<SurveySendTarget> targets = new ArrayList<SurveySendTarget>();
			target.getTargetList().forEach(e -> {
				SurveySendTarget targetEntity = new SurveySendTarget();

				if (e.getLocationId() != 0) {
					Location location = new Location();
					location.setId(e.getLocationId());
					targetEntity.setLocation(location);
				}

				if (e.getDepartmentId() != 0) {
					Department department = new Department();
					department.setId(e.getDepartmentId());
					targetEntity.setDepartment(department);
				}

				if (e.getTeamId() != 0) {
					Team team = new Team();
					team.setId(e.getTeamId());
					targetEntity.setTeam(team);
				}
				targets.add(targetEntity);

			});
			ser.sendOutSurvey(target.getSurveyId(), configService.filterDuplicate(targets), target.getDuration(),
					getUserContext().getWorkplaceId());

			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Number>(status.value(), status);

	}

	@Secured({"ROLE_employee","ROLE_manager"})
	@PostMapping("/answer")
	public ResponseEntity<Number> answer(@RequestBody SurveyAnswerInforDTO dto) {
		HttpStatus status = null;
		try {
			if(ser.checkIfUserTakeSurvey(dto.getSurveyId())) {
				status = HttpStatus.CONFLICT;
			}else {
				ser.saveAnswer(dto);
				status = HttpStatus.OK;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Number>(status.value(), status);
	}

	@Secured({"ROLE_employer","ROLE_employee","ROLE_manager","ROLE_system_admin"})
	@GetMapping("/report")
	public ResponseEntity<PageSearchDTO<SurveyReportDTO>> reportList(@RequestParam("term") String term,
			@RequestParam("page") int page) {
		HttpStatus status = null;
		PageSearchDTO<SurveyReportDTO> res = new PageSearchDTO<SurveyReportDTO>();
		try {
			Page<Survey> result = ser.searchReportWithPagination(term, page);
			List<SurveyReportDTO> dtoList = new ArrayList<SurveyReportDTO>();
			result.getContent().forEach(s -> {
				SurveyReportDTO dto = new SurveyReportDTO();
				BeanUtils.copyProperties(s, dto);
				dtoList.add(dto);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(dtoList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<PageSearchDTO<SurveyReportDTO>>(res, status);
	}
	
	@Secured({"ROLE_employer","ROLE_system_admin"})
	@GetMapping("/template")
	public ResponseEntity<PageSearchDTO<SurveyDTO>> searchTemplate(@RequestParam("term") String term,
			@RequestParam("page") int page) {
		HttpStatus status = null;
		PageSearchDTO<SurveyDTO> res = new PageSearchDTO<SurveyDTO>();
		try {
			Page<Survey> result = ser.loadTemplateSurvey(term, page);
			List<SurveyDTO> dtoList = new ArrayList<SurveyDTO>();
			result.getContent().forEach(s -> {
				SurveyDTO dto = new SurveyDTO();
				BeanUtils.copyProperties(s, dto);
				dtoList.add(dto);
			});
			res.setMaxPage(result.getTotalPages());
			res.setObjList(dtoList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<PageSearchDTO<SurveyDTO>>(res, status);
	}
	
	@Secured({"ROLE_employee","ROLE_manager"})
	@GetMapping(value = "/history")
    public ResponseEntity<PageSearchDTO<SurveyDTO>> getHistorySurveyWithPagination(@RequestParam("term") String term, @RequestParam("page") int page) {
        HttpStatus status = null;
        PageSearchDTO<SurveyDTO> res = new PageSearchDTO<SurveyDTO>();
        try {
            //Call Service
            Page<Survey> result = ser.getHistorySurveyWithPagination(term, page);
            //Convert to DTO
            List<SurveyDTO> resultList = new ArrayList<SurveyDTO>();
            result.getContent().forEach(element -> {
                SurveyDTO dto = new SurveyDTO();
                BeanUtils.copyProperties(element, dto);
                dto.setDateTaken(ser.getDateTakenSurvey(dto.getId()));
                dto.setQuestions(ser.getTakeSurveyHistory(dto.getId()));
                resultList.add(dto);
            });
            res.setMaxPage(result.getTotalPages());
            res.setObjList(resultList);
            status = HttpStatus.OK;
        } catch (Exception e) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<PageSearchDTO<SurveyDTO>>(res, status);
    }

	@Secured({"ROLE_employee","ROLE_manager"})
    @GetMapping("/history/answer")
    public ResponseEntity<List<AnswerDTO>> getAnswerBySurvey(@RequestParam("id") int id) {
        HttpStatus status = null;
        List<AnswerDTO> dto = new ArrayList<>();
        try {
            dto = ser.getAnswerBySurveyId(id);
            status = HttpStatus.OK;
        } catch (Exception e) {
            e.printStackTrace();
            status = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<List<AnswerDTO>>(dto, status);
    }
}

package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.dto.TypeQuestionDTO;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.entity.TypeQuestion;
import com.fpt.officelink.service.SurveyService;

@Controller
@RequestMapping("/survey")
public class SurveyController {

	@Autowired
	SurveyService ser;

	Logger log = Logger.getLogger(SurveyController.class.getName());
	
	private CustomUser user;

	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
	
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
			
			status =HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<List<SurveyDTO>>(res,status);
	}

	@GetMapping
	public ResponseEntity<PageSearchDTO<SurveyDTO>> search(@RequestParam("term") String term,@RequestParam("page") int page) {
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
			status =HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<PageSearchDTO<SurveyDTO>>(res,status);
	}
	
	@GetMapping("/searchReport")
	public ResponseEntity<PageSearchDTO<SurveyReportDTO>> searchReport(@RequestParam("term") String term,@RequestParam("page") int page) {
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
			status =HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<PageSearchDTO<SurveyReportDTO>>(res,status);
	}
	
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
				Set<AnswerOption> options = new HashSet<AnswerOption>();
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
				sqList.add(tmpSQ);
			});
			ser.newSurvey(survey, sqList);
			status = HttpStatus.CREATED;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}
	
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
				Set<AnswerOption> options = new HashSet<AnswerOption>();
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
				sqList.add(tmpSQ);
			});
			ser.updateSurvey(survey, sqList);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<Integer>(status.value(), status);
	}
	
	@DeleteMapping
	public ResponseEntity<Integer> delete(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		try {
			ser.delete(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(),status);
	}
	
	@GetMapping("/detail")
	public ResponseEntity<List<QuestionDTO>> getDetail(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		List<QuestionDTO> res = new ArrayList<QuestionDTO>();
		try {
			List<SurveyQuestion> result = ser.getDetail(id);
			result.forEach(r ->  {
				Question e = r.getQuestion();
				QuestionDTO dto = new QuestionDTO();
				dto.setQuestionIdentity(r.getQuestionIndex());
				BeanUtils.copyProperties(e, dto,"type","options");
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
				res.add(dto);
			});
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<List<QuestionDTO>>(res,status);
	}
	
	@GetMapping("/take")
	public ResponseEntity<SurveyDTO> getTakeSurvey(@RequestParam("token") String token) {
		HttpStatus status = null;
		SurveyDTO res = null;
		try {
			if(!ser.checkIfUserTakeSurvey()) {
				res = ser.getTakeSurvey(token);
				status = HttpStatus.OK;
			}else {
				status = HttpStatus.ACCEPTED;
			}
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<SurveyDTO>(res,status);
	}
	
	@PostMapping("/sendOut")
	public ResponseEntity<Number> sendOutSurvey(@RequestBody SendSurveyDTO target) {
		HttpStatus status = null;
		try {
			
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Number>(status.value(),status);
		
	}
	
	@PostMapping("/answer")
	public ResponseEntity<Number> answer(@RequestBody List<AnswerDTO> answers) {
		HttpStatus status = null;
		try {
			ser.saveAnswer(answers);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<Number>(status.value(),status);
	}
	
	@GetMapping("/report")
	public ResponseEntity<SurveyReportDTO> report(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		SurveyReportDTO res = null;
		try {
			res = ser.getReport(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<SurveyReportDTO>(res,status);
	}
}

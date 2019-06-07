package com.fpt.officelink.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.Question;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.service.SurveyService;

@Controller
@RequestMapping("/survey")
public class SurveyController {
	
	@Autowired
	SurveyService ser;
	
	Logger log = Logger.getLogger(SurveyController.class.getName());
	
	@PostMapping
	public ResponseEntity<Integer> create(@RequestBody SurveyDTO dto) {
		HttpStatus status = null;
		try {
			Survey survey = new Survey();
			BeanUtils.copyProperties(dto, survey);
			List<Question> questions = new ArrayList<Question>();
			dto.getQuestions().forEach(q -> {
				Question tmpQ = new Question();
				Set<AnswerOption> options = new HashSet<AnswerOption>();
				q.getOptions().forEach(o -> {
					AnswerOption tmpOp = new AnswerOption();
					BeanUtils.copyProperties(o, tmpOp);
					options.add(tmpOp);
				});
				BeanUtils.copyProperties(q, tmpQ);
				tmpQ.setOptions(options);
				questions.add(tmpQ);
			});
			ser.newSurvey(survey, questions);
		} catch (Exception e) {
			log.info(e.getMessage());
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<Integer>(status.value(), status);
	}
}

package com.fpt.officelink.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.AnswerReportDTO;
import com.fpt.officelink.dto.DashBoardDTO;
import com.fpt.officelink.dto.QuestionReportDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;
import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.service.ReportService;
import com.fpt.officelink.service.SurveyService;

@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	ReportService reportSer;

	@Autowired
	SurveyService surSer;
	
	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@GetMapping("/target")
	public ResponseEntity<SurveySendDetailDTO> getTarget(@RequestParam("id") int surveyId) {
		SurveySendDetailDTO res = new SurveySendDetailDTO();
		HttpStatus status = null;
		try {
			res = reportSer.getSendDetail(surveyId);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<SurveySendDetailDTO>(res, status);
	}

	@GetMapping
	public ResponseEntity<List<QuestionReportDTO>> getFilterdReport(@RequestParam("surveyId") int surveyId,
			@RequestParam("locationId") int locationId, @RequestParam("departmentId") int departmentId,
			@RequestParam("teamId") int teamId) {

		List<QuestionReportDTO> res = new ArrayList<QuestionReportDTO>();
		HttpStatus status = null;
		try {
			res = surSer.getFilteredReport(surveyId, locationId, departmentId, teamId);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<List<QuestionReportDTO>>(res, status);
	}

	@GetMapping("/getSameSurvey")
	public ResponseEntity<List<SurveyDTO>> getSurveyHasSameQuestion(@RequestParam("id") int id,
			@RequestParam("notId") int notId) {
		List<SurveyDTO> res = new ArrayList<SurveyDTO>();
		HttpStatus status = null;
		try {
			List<Survey> result = surSer.getSurveyByQuestionId(id, notId);

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

	@GetMapping("/getCompareQuestion")
	public ResponseEntity<List<AnswerReportDTO>> getOneQuestionAnswerReportForCompare(
			@RequestParam("surveyId") int surveyId, @RequestParam("questionId") int questionId,
			@RequestParam("locationId") int locationId, @RequestParam("departmentId") int departmentId,
			@RequestParam("teamId") int teamId) {
		List<AnswerReportDTO> res = new ArrayList<AnswerReportDTO>();
		HttpStatus status = null;
		try {
			res = surSer.getAnswerReport(surveyId, questionId, locationId, departmentId, teamId);

			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<List<AnswerReportDTO>>(res, status);
	}
	
	@GetMapping("/dashboard")
	public ResponseEntity<DashBoardDTO> getDashBoard() {
		DashBoardDTO res = new DashBoardDTO();
		HttpStatus status = null;
		try {
			res = reportSer.getDashBoard(getUserContext().getWorkplaceId());
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<DashBoardDTO>(res, status);
	}
	
	@GetMapping("/download")
	public void downloadFile(HttpServletResponse rep,@RequestParam("surveyId") Integer surveyId, @RequestParam("questionId") Integer questionId) throws IOException {
		Optional<SurveyQuestion> opSurQuest = reportSer.getDownloadDetail(surveyId, questionId);
		if(opSurQuest.isPresent()) {
			SurveyQuestion surQuest = opSurQuest.get();
			rep.setHeader("Content-Disposition", "attachment; filename="+surQuest.getSurvey().getName()+".docx");
			PrintWriter pw = rep.getWriter();
			pw.println("Question: " + surQuest.getQuestion().getQuestion());
			List<AnswerOption> options = surQuest.getQuestion().getOptions();
			for (int i = 0; i < options.size(); i++) {
				pw.println((i + 1) + ". " + options.get(i).getAnswerText());
			}
			pw.println("");
			pw.println("Answers");
			List<Answer> answers = new ArrayList<Answer>(surQuest.getAnswers());
			for (int i = 0; i < answers.size(); i++) {
				pw.println((i + 1) + "/ " + answers.get(i).getContent());
			}
			pw.flush();
		}
		
		
		
       
	}
	
}

package com.fpt.officelink.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fpt.officelink.dto.AnswerReportDTO;
import com.fpt.officelink.dto.CategoryReportDTO;
import com.fpt.officelink.dto.DashBoardDTO;
import com.fpt.officelink.dto.SurveyDTO;
import com.fpt.officelink.dto.SurveyReportDTO;
import com.fpt.officelink.dto.SurveySendDetailDTO;
import com.fpt.officelink.entity.Answer;
import com.fpt.officelink.entity.AnswerOption;
import com.fpt.officelink.entity.AnswerReport;
import com.fpt.officelink.entity.ApplyFilterDTO;
import com.fpt.officelink.entity.CustomUser;
import com.fpt.officelink.entity.Survey;
import com.fpt.officelink.entity.SurveyQuestion;
import com.fpt.officelink.service.ReportService;
import com.fpt.officelink.service.SurveyService;
import com.fpt.officelink.service.WordCloudFilterService;

@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	ReportService reportSer;

	@Autowired
	SurveyService surSer;
	
	@Autowired
	WordCloudFilterService filterSer;
	
	private CustomUser getUserContext() {
		return (CustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
	@GetMapping("/target")
	public ResponseEntity<SurveySendDetailDTO> getTarget(@RequestParam("id") int surveyId) {
		SurveySendDetailDTO res = new SurveySendDetailDTO();
		HttpStatus status = null;
		try {
			res = reportSer.getSendDetail(surveyId);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<SurveySendDetailDTO>(res, status);
	}

	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
	@GetMapping
	public ResponseEntity<List<CategoryReportDTO>> getFilterdReport(@RequestParam("surveyId") int surveyId,
			@RequestParam("locationId") int locationId, @RequestParam("departmentId") int departmentId,
			@RequestParam("teamId") int teamId) {

		List<CategoryReportDTO> res = new ArrayList<CategoryReportDTO>();
		HttpStatus status = null;
		try {
			res = reportSer.getFilteredCateReport(surveyId, locationId, departmentId, teamId);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<List<CategoryReportDTO>>(res, status);
	}

	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
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

	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
	@GetMapping("/getCompareQuestion")
	public ResponseEntity<List<AnswerReportDTO>> getOneQuestionAnswerReportForCompare(
			@RequestParam("surveyId") int surveyId, @RequestParam("questionId") int questionId,
			@RequestParam("locationId") int locationId, @RequestParam("departmentId") int departmentId,
			@RequestParam("teamId") int teamId) {
		List<AnswerReportDTO> res = new ArrayList<AnswerReportDTO>();
		HttpStatus status = null;
		try {
			List<AnswerReport> result = new ArrayList<AnswerReport>();
			result = reportSer.getAnswerReport(surveyId, questionId, locationId, departmentId, teamId);
			for (AnswerReport answerReport : result) {
				AnswerReportDTO dto = new AnswerReportDTO();
				BeanUtils.copyProperties(answerReport, dto);
				res.add(dto);
			}

			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}

		return new ResponseEntity<List<AnswerReportDTO>>(res, status);
	}
	
	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
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
	
	@GetMapping(value = "/download")
	public void downloadFile(HttpServletResponse rep,@RequestParam("token") String token) throws IOException, ParseException {
		Optional<SurveyQuestion> opSurQuest = reportSer.getDownloadDetail(token);
		if(opSurQuest.isPresent()) {
			SurveyQuestion surQuest = opSurQuest.get();
			rep.setHeader("Content-Disposition", "attachment; filename="+surQuest.getSurvey().getName()+".txt");
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
			pw.close();
			                    
		}
	}
	
	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
	@GetMapping("/getDownloadToken")
	public ResponseEntity<String> getDownloadToken(@RequestParam("surveyId") Integer surveyId, @RequestParam("questionId") Integer questionId) {
		String token = null;
		HttpStatus status = null;
		try {
			token = reportSer.getDownLoadToken(surveyId, questionId);
			status = HttpStatus.OK;
		} catch (Exception e) {
			status =HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<String>(token,status);
	}
	
	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
	@PostMapping("/applyFilter")
	public ResponseEntity<List<AnswerReportDTO>> applyFilter(@RequestBody ApplyFilterDTO applyInfor) {
		List<AnswerReportDTO> res = new ArrayList<AnswerReportDTO>();
		HttpStatus status = null;
		try {
			res = filterSer.applyFilter(applyInfor.getAnswers(), applyInfor.getFilterId());
			status = HttpStatus.OK;
		} catch (Exception e) {
			status = HttpStatus.BAD_REQUEST;
		}
		
		return new ResponseEntity<List<AnswerReportDTO>>(res,status);
	}
	
	@Secured({"ROLE_employer","ROLE_employee","ROLE_system_admin"})
	@GetMapping("/detail")
	public ResponseEntity<SurveyReportDTO> reportDetail(@RequestParam("id") Integer id) {
		HttpStatus status = null;
		SurveyReportDTO res = null;
		try {
			res = reportSer.getReport(id);
			status = HttpStatus.OK;
		} catch (Exception e) {
			e.printStackTrace();
			status = HttpStatus.BAD_REQUEST;
		}
		return new ResponseEntity<SurveyReportDTO>(res, status);
	}
	
}

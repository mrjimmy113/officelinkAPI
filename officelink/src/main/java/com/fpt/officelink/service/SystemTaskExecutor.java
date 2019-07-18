package com.fpt.officelink.service;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fpt.officelink.controller.SurveyController;
import com.fpt.officelink.entity.Configuration;
import com.fpt.officelink.entity.Survey;

@Service
public class SystemTaskExecutor {

	private static final Logger log = Logger.getLogger(SurveyController.class.getName());

	@Autowired
	private SurveyService surveyService;
	
	@Async
	public void sentRoutineSurvey(Configuration config) {
		try {
			
			surveyService.sendRoutineSurvey(config.getSurvey().getId(), config.getDuration());

			String msg = String.format("Successfully sent scheduled survey for %s, survey name: %s, time sent: %s",
					config.getWorkplace().getName(), config.getSurvey().getName(), new Date().toString());

			log.log(Level.INFO, msg);

		} catch (Exception e) {
			e.printStackTrace();
			String msg = String.format("Fail to sent scheduled survey for %s, survey name: %s, scheduled time: %s",
					config.getWorkplace().getName(), config.getSurvey().getName(), config.getScheduleTime());

			log.log(Level.INFO, msg);
		}
	}
	
	// set surveys active status and generate report for that team
	@Async
	public void setSurveysExpired() {
		// get list of active surveys with end date is to day or before
		List<Survey> surveys = surveyService.getActiveSurveyByDate(new Date());
		for (Survey survey : surveys) {
			surveyService.generateTeamQuestionReport(survey.getId());
			survey.setActive(false);
			surveyService.updateStatus(survey);
		}
	}
	
}

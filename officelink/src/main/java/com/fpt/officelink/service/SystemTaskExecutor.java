package com.fpt.officelink.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
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

	@Autowired
	private TeamReportService teamReportService;

	@Async
	public void sentRoutineSurvey(Configuration config) {
		try {

			surveyService.sendRoutineSurvey(config.getSurvey().getId(), config.getDuration());

			String msg = String.format("Successfully sent scheduled survey for %s, survey name: %s, time sent: %s",
					config.getWorkplace().getName(), config.getSurvey().getName(), new java.util.Date().toString());

			log.log(Level.INFO, msg);

		} catch (Exception e) {
			e.printStackTrace();
			String msg = String.format("Fail to sent scheduled survey for %s, survey name: %s, scheduled time: %s",
					config.getWorkplace().getName(), config.getSurvey().getName(), config.getScheduleTime());

			log.log(Level.INFO, msg);
		}
	}

	/**
	 * set surveys active status and generate report for teams
	 */
	@Async
	public List<Survey> generateReportDaily() {
		// get list of active surveys with end date < today
		List<Survey> surveys = surveyService.getActiveSurveyByDate(new Date(Calendar.getInstance().getTimeInMillis()));
		if (surveys.isEmpty()) {
			return null;
		}

		List<Survey> successSurveys = new ArrayList<Survey>();

		for (Survey survey : surveys) {
			boolean isSuccess = teamReportService.generateTeamQuestionReport(survey.getId());

			// update active status
			if (isSuccess) {
				successSurveys.add(surveyService.updateStatus(survey));
			}
		}

		return successSurveys;
	}

}

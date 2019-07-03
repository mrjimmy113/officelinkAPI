package com.fpt.officelink.service;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fpt.officelink.controller.SurveyController;
import com.fpt.officelink.entity.Configuration;

@Service
public class SystemTaskExecutor {
	
	private static final Logger log = Logger.getLogger(SurveyController.class.getName());
	
	@Autowired
	SurveyService surveyService;
	
	@Async
	public void sentRoutineSurvey(Configuration config) {
		try {
			boolean success = surveyService.sendOutSurvey(config.getSurvey().getId());
			
			if (success) {
				String msg = String.format("Successfully sent scheduled survey for %s, survey name: %s, time sent: %s",
						config.getWorkplace().getName(),
						config.getSurvey().getName(),
						new Date().toString());

				log.log(Level.INFO, msg);
			} else {
				String msg = String.format("Fail to sent scheduled survey for %s, survey name: %s, scheduled time: %s",
						config.getWorkplace().getName(),
						config.getSurvey().getName(),
						config.getScheduleTime());

				log.log(Level.INFO, msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.FINEST, e.toString());
		}
	}
}

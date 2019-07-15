package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.fpt.officelink.controller.SurveyController;
import com.fpt.officelink.entity.Configuration;

@Service
public class SchedulerService implements SchedulingConfigurer {
	
	private ThreadPoolTaskScheduler dailyScheduler; // daily schedule
	
	private List<ScheduledFuture<?>> dailyTaskList; // daily task list

	private List<Configuration> configList; // list of configurations

	private ThreadPoolTaskScheduler configScheduler; // scheduler properties

	private List<ScheduledFuture<?>> scheduleList; // store scheduled schedule
	
	private static final Logger log = Logger.getLogger(SchedulerService.class.getName());

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	SystemTaskExecutor executor;
	
	// constructor
	private SchedulerService() {
		//init value 
		dailyScheduler = dailyPoolScheduler();
		configScheduler = configurationPoolScheduler();
		scheduleList = new ArrayList<ScheduledFuture<?>>();
		dailyTaskList = new ArrayList<ScheduledFuture<?>>();
		
		// start daily Tasks
		this.configureDailyTasks(new ScheduledTaskRegistrar(), "* 1 15 * * *");
	}

	// Thread pool for configuration scheduler
	private ThreadPoolTaskScheduler configurationPoolScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("ConfigurationThreadPool");
		scheduler.setPoolSize(5);
		scheduler.initialize();
		return scheduler;
	}
	
	// Thread pool for daily task scheduler
	private ThreadPoolTaskScheduler dailyPoolScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("DailyThreadPool");
		scheduler.setPoolSize(5);
		scheduler.initialize();
		return scheduler;
	}
	
	// Config daily tasks
	public void configureDailyTasks(ScheduledTaskRegistrar taskRegistrar, String cron) {
		if (!dailyTaskList.isEmpty()) {
			for (ScheduledFuture<?> schedule : dailyTaskList) {
				schedule.cancel(false);
			}
		}
		
		ScheduledFuture<?> newSchedule = dailyScheduler.schedule(new Runnable() {
			@Override
			public void run() {
				// Put task here
				executor.setSurveysExpired();
				executor.exportReportsDaily();
			}
			// schedule time here

		}, new CronTrigger(cron));
		
		dailyTaskList.add(newSchedule);
		
		taskRegistrar.setScheduler(dailyScheduler);
	}

	// task registration
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		
		// cancel any scheduled tasks
		if (!scheduleList.isEmpty()) {
			for (ScheduledFuture<?> schedule : scheduleList) {
				schedule.cancel(false);
			}
		}
		// load configs
		configList = configurationService.getConfigurations();
		//
		int i = 0;
		// set schedules
		for (Configuration config : configList) {
			if (config.getSurvey() != null && !config.getScheduleTime().isEmpty()) {
				ScheduledFuture<?> newSchedule = configScheduler.schedule(new Runnable() {
					@Override
					public void run() {
						// Put task here
						executor.sentRoutineSurvey(config);
					}
					// schedule time here

				}, new CronTrigger(config.getScheduleTime()));

				// store scheduled task
				// why need to store scheduled tasks? Because once a task is scheduled, it is
				// register to system.
				// when configuration change, you cannot simply create a new schedule and the
				// ignore scheduled task nor update the scheduled task
				// insteed the scheduled tasks need to be cancel first, then replaced with the
				// new schedule
				if (i >= scheduleList.size()) {
					// add new schedule to pool
					scheduleList.add(newSchedule);
				} else {
					// replace existed schedule with new one
					scheduleList.set(i, newSchedule);
				}
				i++;
			}
			taskRegistrar.setScheduler(configScheduler);
		}
	}
}

package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Configuration;

/**
 * @author phduo
 *
 */
@Service
public class SchedulerService implements SchedulingConfigurer {
	
	private ThreadPoolTaskScheduler dailyScheduler; // daily schedule
	
	private List<ScheduledFuture<?>> dailyTaskList; // daily task list

	private ThreadPoolTaskScheduler configScheduler; // scheduler properties

	private List<ScheduledFuture<?>> configTaskList; // store scheduled schedule
	
	private List<Configuration> configList; // list of configurations

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	SystemTaskExecutor executor;
	
	// constructor
	private SchedulerService() {
		//init value 
		dailyScheduler = dailyPoolScheduler();
		configScheduler = configurationPoolScheduler();
		configTaskList = new ArrayList<ScheduledFuture<?>>();
		dailyTaskList = new ArrayList<ScheduledFuture<?>>();
		
		// start daily Tasks
		this.configureDailyTasks(new ScheduledTaskRegistrar(), "0 3 11 * * *");
	}

	/**
	 * Thread pool for configuration scheduler
	 * @return a ThreadPoolTaskScheduler for survey routine relate tasks
	 */
	private ThreadPoolTaskScheduler configurationPoolScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("ConfigurationThreadPool");
		scheduler.setPoolSize(5);
		scheduler.initialize();
		return scheduler;
	}
	
	/**
	 * Thread pool for daily task scheduler
	 * @return a ThreadPoolTaskScheduler for daily tasks
	 */
	private ThreadPoolTaskScheduler dailyPoolScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("DailyThreadPool");
		scheduler.setPoolSize(5);
		scheduler.initialize();
		return scheduler;
	}
	
	/**
	 * Configure daily tasks
	 * @param taskRegistrar
	 * @param cron
	 */
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
				executor.generateReportDaily();
			}
			// schedule time here

		}, new CronTrigger(cron));
		
		dailyTaskList.add(newSchedule);
		
		taskRegistrar.setScheduler(dailyScheduler);
	}

	/**
	 * 
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		
		// cancel any scheduled tasks
		if (!configTaskList.isEmpty()) {
			for (ScheduledFuture<?> schedule : configTaskList) {
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
				ScheduledFuture<?> newSchedule = this.getSchedule(config);

				// store scheduled task
				// why need to store scheduled tasks? Because once a task is scheduled, it is
				// register to system.
				// when configuration change, you cannot simply create a new schedule and the
				// ignore scheduled task nor update the scheduled task
				// insteed the scheduled tasks need to be cancel first, then replaced with the
				// new schedule
				if (i >= configTaskList.size()) {
					// add new schedule to pool
					configTaskList.add(newSchedule);
				} else {
					// replace existed schedule with new one
					configTaskList.set(i, newSchedule);
				}
				i++;
			}
			taskRegistrar.setScheduler(configScheduler);
		}
	}
	
	/**
	 * @param config
	 * @return
	 */
	public ScheduledFuture<?> getSchedule(Configuration config) {
		ScheduledFuture<?> newSchedule = configScheduler.schedule(new Runnable() {
			@Override
			public void run() {
				// Put task here
				executor.sentRoutineSurvey(config);
			}
			// schedule time here

		}, new CronTrigger(config.getScheduleTime()));
		
		return newSchedule;
	}
}

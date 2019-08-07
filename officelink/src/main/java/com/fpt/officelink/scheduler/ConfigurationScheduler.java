package com.fpt.officelink.scheduler;

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
import com.fpt.officelink.service.ConfigurationService;

/**
 * @author phduo
 *
 */
@Service
public class ConfigurationScheduler implements SchedulingConfigurer {
	
	private ThreadPoolTaskScheduler configScheduler; // configuration schedule

	private List<ScheduledFuture<?>> configTaskList; // configuration task list
	
	private List<Configuration> configList; // list of configurations

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	SystemTaskExecutor executor;
	
	// constructor
	private ConfigurationScheduler() {
		//init value 
		configScheduler = configurationPoolScheduler();
		configTaskList = new ArrayList<ScheduledFuture<?>>();
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
	 * Configure Configuration tasks
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
				// in steed the scheduled tasks need to be cancel first, then replaced with the
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
	 * get a schedule task from configuration
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

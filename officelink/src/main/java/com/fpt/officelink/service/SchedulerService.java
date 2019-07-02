package com.fpt.officelink.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import com.fpt.officelink.entity.Configuration;

@Service
public class SchedulerService implements SchedulingConfigurer {

	private List<Configuration> configList; // list of configurations

	ThreadPoolTaskScheduler scheduler; // scheduler properties

	private List<ScheduledFuture<?>> scheduleList; // store scheduled schedule

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	SystemBot bot;

	private SchedulerService() {
		scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		scheduler.setPoolSize(5);
		scheduler.initialize();
		scheduleList = new ArrayList<ScheduledFuture<?>>();
	}

	private static SchedulerService instance;

	public static SchedulerService getInstance() {
		if (instance == null) {
			instance = new SchedulerService();
		}

		return instance;
	}

	@Bean
	public TaskScheduler poolScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
		scheduler.setPoolSize(5);
		scheduler.initialize();
		return scheduler;
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
			ScheduledFuture<?> newSchedule = scheduler.schedule(new Runnable() {
				@Override
				public void run() {
					// Put task here
					bot.printWorkplace(config.getWorkplace().getId());
				}
				// schedule time here
			}, new CronTrigger(config.getScheduleTime()));

			// store scheduled task
			// why need to store scheduled tasks? Because once a task is scheduled, it is
			// register to system.
			// when configuration change, you cannot simply create a new schedule and the ignore scheduled task nor update the scheduled task  
			// insteed the scheduled tasks need to be cancel first, then replaced with the new schedule
			if (i >= scheduleList.size()) {
				// add new schedule to pool
				scheduleList.add(newSchedule);
			} else {
				// replace existed schedule with new one
				scheduleList.set(i, newSchedule);
			}
			i++;
		}
		taskRegistrar.setScheduler(scheduler);

// the below codes are the right way to set a taskregistrar but won't work if you need to change any scheduled task on run time
//			for (CronTask cronTask : taskRegistrar.getCronTaskList()) {
//				System.out.println(cronTask.getRunnable().hashCode());
//			}
//			List<CronTask> triggerTaskList = new ArrayList<CronTask>();
//			
//			for (Configuration config : configList) {
//				CronTask task = new CronTask(new Runnable() {
//					@Override
//					public void run() {
//						// Put task here
//						bot.printWorkplace(config.getWorkplace().getId());
//					}}, new CronTrigger(config.getScheduleTime()));
//				triggerTaskList.add(task);
//			}
//			
//			taskRegistrar.setCronTasksList(triggerTaskList);
//			
//			for (CronTask cronTask : taskRegistrar.getCronTaskList()) {
//				System.out.println(cronTask.getRunnable().hashCode());
//			}
//			System.out.println();
	}
}

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

/**
 * @author phduo
 *
 */
@Service
public class SystemScheduler implements SchedulingConfigurer {

	private ThreadPoolTaskScheduler dailyScheduler; // daily schedule

	private List<ScheduledFuture<?>> dailyTaskList; // daily task list

	@Autowired
	SystemTaskExecutor executor;

	// constructor
	private SystemScheduler() {
		// init value
		dailyScheduler = dailyPoolScheduler();
		dailyTaskList = new ArrayList<ScheduledFuture<?>>();
	}

	/**
	 * Thread pool for daily task scheduler
	 * 
	 * @return a ThreadPoolTaskScheduler for daily tasks
	 */
	private ThreadPoolTaskScheduler dailyPoolScheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		scheduler.setThreadNamePrefix("DailyThreadPool");
		scheduler.setPoolSize(1);
		scheduler.initialize();
		return scheduler;
	}

	/**
	 * Configure daily tasks
	 */
	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
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
		}, new CronTrigger("0 5 0 * * *"));// start daily Tasks (at 0:5:0 every day)

		dailyTaskList.add(newSchedule);

		taskRegistrar.setScheduler(dailyScheduler);
	}

}

package com.fpt.officelink.service;

import java.util.List;

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
public class SchedulerService implements SchedulingConfigurer{
	
	private List<Configuration> configList;
	
	@Autowired
	ConfigurationService configurationService;
	
	@Autowired
	SystemBot bot;
	
	@Bean
	public TaskScheduler poolScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(5);
        scheduler.initialize();
        return scheduler;
    }
	
	@Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(poolScheduler());
        configList = configurationService.getConfigurations();
        for (Configuration config : configList) {
        	taskRegistrar.addTriggerTask(new Runnable() {
    			
    			@Override
    			public void run() {
    				bot.printWorkplace(
    						config.getWorkplace()
    						.getId());
    			}
    		}, new CronTrigger(configurationService
    				.getConfigByWorkplaceId(
    						config.getWorkplace()
    						.getId())
    				.getScheduleTime()));
		}
    }
}

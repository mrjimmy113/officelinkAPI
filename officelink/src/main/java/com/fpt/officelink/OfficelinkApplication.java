package com.fpt.officelink;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fpt.officelink.scheduler.SystemTaskExecutor;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class OfficelinkApplication {
	
	@Autowired
	SystemTaskExecutor executor;
	
	public static void main(String[] args) {
		SpringApplication.run(OfficelinkApplication.class, args);
	}
	
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
            }
        };
    }
	
	@EventListener(ApplicationReadyEvent.class)
	public void onStartup() {
		
	}	

}

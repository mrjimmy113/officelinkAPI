package com.fpt.officelink.security;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	SecurityFilter filter;

	@Autowired
	DinedHandler accessDined;

	@Autowired
	AuthEntryPoint entryPoint;

	@Autowired
	UserDetailsService detailSer;
	
	@Resource(name="authService")
	private UserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	    auth.userDetailsService(userDetailsService);
	}
	
	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
	    return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
		.exceptionHandling()
		.authenticationEntryPoint(entryPoint).accessDeniedHandler(accessDined);

	}

}

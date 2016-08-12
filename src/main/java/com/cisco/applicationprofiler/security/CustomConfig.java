/**
 * Copyright @maplelabs
 */
package com.cisco.applicationprofiler.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * @author Mahesh
 *
 */

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class CustomConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

	@Autowired
	private AuthenticationTokenProcessingFilter authenticationTokenProcessingFilter;

	/**
	 * Override the configuration of spring security to disable authentication
	 * for the static UI components.
	 * 
	 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.web.builders.HttpSecurity)
	 * 
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
			.antMatchers("/index*").permitAll()
			.antMatchers("/").permitAll()
			.antMatchers("/scripts/**").permitAll()
			.antMatchers("/app/**").permitAll()
			.antMatchers("/bower_components/**").permitAll()
			.antMatchers("/styles/**").permitAll()
			.antMatchers("/images/**").permitAll()
			.antMatchers("/header/**").permitAll()
			.antMatchers("/fonts/**").permitAll()
			.antMatchers("/about").permitAll()
			.antMatchers("/profiler/login").permitAll()
			.antMatchers("/profiler/").permitAll()
			.antMatchers("/views/**").permitAll()
			.antMatchers("/acisizer/").permitAll()
			.antMatchers("/api-docs","/api-docs/default/project-controllers","/api-docs/default/user-controller",
				"/api-docs/default/device-controller").permitAll()
			.antMatchers("/restapi*","/lib/*","/fonts/*","/lang/*","/swagger*").permitAll()
			.and()
			.authorizeRequests()
			.antMatchers("/admin/**").hasRole("ADMIN").anyRequest()
			.authenticated().and().httpBasic().disable()
			.addFilterBefore(this.authenticationTokenProcessingFilter, BasicAuthenticationFilter.class);
		http.csrf().disable();
		http.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);

	}
}

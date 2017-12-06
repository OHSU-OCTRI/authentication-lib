package org.octri.authentication.controller;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * "Used to suppress default (BasicErrorController) functionality: By default Spring Boot adds the error path to the
 * list of paths, ignored by Spring Security."
 * 
 * @see https://github.com/olle/no-auth-for-you/blob/master/spring-boot-1.5.0/src/main/java/com/studiomediatech/bugs/web/ErrorController.java
 * @see https://github.com/spring-projects/spring-boot/issues/1048
 */
@Controller
public class ErrorController extends BasicErrorController {

	/**
	 * This Bean declaration provides the Spring Security Context to 4xx responses. This is reported as
	 * fixed in Spring Boot 2.0.0:
	 * 
	 * https://github.com/spring-projects/spring-boot/issues/1048
	 * 
	 * @param springSecurityFilterChain
	 * @return
	 */
	@Bean
	public FilterRegistrationBean getSpringSecurityFilterChainBindedToError(
	                @Qualifier("springSecurityFilterChain") Filter springSecurityFilterChain) {

	        FilterRegistrationBean registration = new FilterRegistrationBean();
	        registration.setFilter(springSecurityFilterChain);
	        registration.setDispatcherTypes(EnumSet.allOf(DispatcherType.class));
	        return registration;
	}

	@Autowired
	public ErrorController(ErrorAttributes errorAttributes) {
		super(errorAttributes, new ErrorProperties());
	}

	@RequestMapping(value = "/error")
	public String error(Model model) {
		return "/error";
	}

	@Override
	public String getErrorPath() {
		return "/__dummyErrorPath";
	}

}
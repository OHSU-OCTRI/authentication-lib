package org.octri.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.BasicErrorController;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
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
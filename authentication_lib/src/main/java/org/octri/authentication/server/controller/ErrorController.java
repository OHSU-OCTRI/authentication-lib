package org.octri.authentication.server.controller;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Used to suppress default (BasicErrorController) functionality: By default Spring Boot adds the error path to the list
 * of paths, ignored by Spring Security.
 *
 * TODO: Determine whether this is still needed.
 *
 * @see <a href=
 *      "https://github.com/olle/no-auth-for-you/blob/master/spring-boot-1.5.0/src/main/java/com/studiomediatech/bugs/web/ErrorController.java">https://github.com/olle/no-auth-for-you/blob/master/spring-boot-1.5.0/src/main/java/com/studiomediatech/bugs/web/ErrorController.java</a>
 * @see <a href=
 *      "https://github.com/spring-projects/spring-boot/issues/1048">https://github.com/spring-projects/spring-boot/issues/1048</a>
 */
@Controller
public class ErrorController extends BasicErrorController {

	/**
	 * Constructor
	 * 
	 * @param errorAttributes
	 *            base error attributes
	 */
	public ErrorController(ErrorAttributes errorAttributes) {
		super(errorAttributes, new ErrorProperties());
	}

	/**
	 * Renders the error page.
	 * 
	 * @param model
	 *            template model attributes
	 * @return the error template
	 */
	@RequestMapping(value = "/error")
	public String error(Model model) {
		return "/error";
	}

}
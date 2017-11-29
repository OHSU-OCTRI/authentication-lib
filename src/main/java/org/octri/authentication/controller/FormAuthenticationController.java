package org.octri.authentication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling form authentication.
 * 
 * @author sams
 */
@Controller
public class FormAuthenticationController {

	@GetMapping("login")
	public String login() {
		return "login";
	}

}

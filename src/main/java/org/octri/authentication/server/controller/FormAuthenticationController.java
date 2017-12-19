package org.octri.authentication.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Controller for handling form authentication.
 * 
 * @author sams
 */
@Controller
public class FormAuthenticationController {

	@GetMapping("login")
	public String login(Model model, @ModelAttribute("passwordChanged") String passwordChanged) {
		model.addAttribute("passwordChanged", Boolean.valueOf(passwordChanged));
		return "login";
	}

}

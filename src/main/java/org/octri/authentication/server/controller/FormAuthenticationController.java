package org.octri.authentication.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for handling form authentication.
 * 
 * @author sams
 */
@Controller
public class FormAuthenticationController {
	
	@Autowired
	Boolean tableBasedEnabled;

	@GetMapping("login")
	public String login(Model model, @ModelAttribute("passwordChanged") String passwordChanged,
			@ModelAttribute("passwordReset") String passwordReset,
			@RequestParam(required = false, name = "error") String error, HttpServletRequest request) {
		model.addAttribute("passwordChanged", Boolean.valueOf(passwordChanged));
		model.addAttribute("passwordReset", Boolean.valueOf(passwordReset));
		model.addAttribute("tableBasedEnabled", tableBasedEnabled);
		model.addAttribute("error", error);
		return "login";
	}

}

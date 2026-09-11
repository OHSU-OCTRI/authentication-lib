package org.octri.authentication.server.controller;

import java.time.LocalDateTime;
import java.time.Period;

import org.octri.authentication.server.security.service.LoginAttemptService;
import org.octri.common.view.ViewUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for viewing login attempts during the last month.
 *
 * NOTE: The login attempt table can be too large to handle on the front end with DataTables. The period of one month
 * was chosen to limit the number of records while providing enough context to debug login issues. A better approach
 * would be to use DataTables with server-side paging, search, and sorting.
 */
@Controller
public class LoginAttemptController {

	private final LoginAttemptService loginAttemptService;

	public LoginAttemptController(LoginAttemptService loginAttemptService) {
		this.loginAttemptService = loginAttemptService;
	}

	@GetMapping("admin/login_attempts")
	public ModelAndView listLoginAttempts(ModelMap model) {
		var oneMonthAgo = LocalDateTime.now().minus(Period.ofMonths(1));
		var loginAttempts = loginAttemptService.findLoginAttemptsSince(oneMonthAgo);

		// listView attribute adds DataTables assets to the page
		model.addAttribute("listView", true);
		model.addAttribute("loginAttempts", loginAttempts);
		ViewUtils.addPageScript(model, "table-sorting.js");

		return new ModelAndView("admin/login_attempt/list", model);
	}

}

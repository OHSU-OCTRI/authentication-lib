package org.octri.authentication.server.controller;

import javax.servlet.http.HttpServletRequest;

import org.octri.authentication.server.security.SecurityHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Provides model attributes that are used globally in page templates.
 */
@ControllerAdvice
public class TemplateAdvice {

	@Value("${app.name}")
	private String appName;

	@Value("${app.version}")
	private String appVersion;

	@Value("${app.displayName}")
	private String displayName;

	private SecurityHelper securityHelper;

	@ModelAttribute
	public void addDefaultAttributes(HttpServletRequest request, Model model) {
		this.securityHelper = new SecurityHelper(SecurityContextHolder.getContext());

		model.addAttribute("appName", appName);
		model.addAttribute("appVersion", appVersion);
		model.addAttribute("displayName", displayName);

		model.addAttribute("isLoggedIn", securityHelper.isLoggedIn());
		model.addAttribute("username", securityHelper.username());
		model.addAttribute("isAdminOrSuper", securityHelper.isAdminOrSuper());
	}
}
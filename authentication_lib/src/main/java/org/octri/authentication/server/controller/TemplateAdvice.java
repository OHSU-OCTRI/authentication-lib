package org.octri.authentication.server.controller;

import java.time.Duration;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.config.SamlProperties;
import org.octri.authentication.server.security.SecurityHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Provides model attributes that are used globally in page templates.
 */
@Component("auth_template_advice")
@ControllerAdvice
public class TemplateAdvice {

	private static final Log log = LogFactory.getLog(TemplateAdvice.class);

	@Value("${app.name}")
	private String appName;

	@Value("${app.version}")
	private String appVersion;

	@Value("${app.displayName}")
	private String displayName;

	private SecurityHelper securityHelper;

	@Autowired
	private Environment env;

	@Autowired
	private OctriAuthenticationProperties authenticationProperties;

	@Autowired(required = false)
	private SamlProperties samlProperties;

	/**
	 * Adds attributes used to render authentication templates to the model used to render the template.
	 *
	 * @param request
	 *            the current servlet request
	 * @param model
	 *            model used to render the template
	 */
	@ModelAttribute
	public void addDefaultAttributes(HttpServletRequest request, Model model) {
		this.securityHelper = new SecurityHelper(SecurityContextHolder.getContext());
		boolean samlEnabled = samlProperties != null && Boolean.TRUE.equals(samlProperties.getEnabled());

		model.addAttribute("appName", appName);
		model.addAttribute("appVersion", appVersion);
		model.addAttribute("displayName", displayName);
		model.addAttribute("currentYear", Calendar.getInstance().get(Calendar.YEAR));

		model.addAttribute("tableBasedEnabled", authenticationProperties.getEnableTableBased());
		model.addAttribute("samlEnabled", samlEnabled);
		model.addAttribute("samlRegistrationId", samlEnabled ? samlProperties.getRegistrationId() : "");
		model.addAttribute("isLoggedIn", securityHelper.isLoggedIn());
		model.addAttribute("username", securityHelper.username());
		model.addAttribute("isAdminOrSuper", securityHelper.isAdminOrSuper());
		model.addAttribute("emailRequired", authenticationProperties.getEmailRequired());
		model.addAttribute("sessionTimeoutSeconds", sessionTimeoutSeconds());
	}

	/**
	 * Gets the time before the session times out due to inactivity, in seconds.
	 *
	 * @return session timeout value in seconds
	 */
	public long sessionTimeoutSeconds() {
		try {
			var timeoutValue = env.getProperty("server.servlet.session.timeout");
			var sessionDuration = DurationStyle.detectAndParse(timeoutValue);
			return sessionDuration.toSeconds();
		} catch (Exception e) {
			log.error("Unexpected exception when parsing session duration", e);
			log.error("Defaulting duration to 20 minutes");
			return Duration.ofMinutes(20l).toSeconds();
		}
	}
}
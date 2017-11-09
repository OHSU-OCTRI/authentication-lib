/**
 * 
 */
package org.octri.authentication.server.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.RequestUtils;
import org.octri.authentication.server.security.entity.LoginAttempt;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.service.LoginAttemptService;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Success handler suitable for use with JSON APIs. Sends a JSON response with status OK instead of a redirect.
 * 
 * TODO: extract code related to recording login attempt into a superclass for use here and with conventional form login
 * that redirects
 * 
 * @author harrelst
 *
 */
@Component
public class JsonResponseAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Log log = LogFactory.getLog(JsonResponseAuthenticationSuccessHandler.class);

	private final ObjectMapper mapper;

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private UserService userService;

	@Autowired
	public JsonResponseAuthenticationSuccessHandler(MappingJackson2HttpMessageConverter messageConverter) {
		this.mapper = messageConverter.getObjectMapper();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
			throws IOException, ServletException {
		recordLoginSuccess(auth, request);
		resetUserFailedAttempts(auth);

		AuthenticationUserDetails userDetails = (AuthenticationUserDetails) auth.getPrincipal();

		log.info("Login: " + userDetails.getUsername());

		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter writer = response.getWriter();
		mapper.writeValue(writer, userDetails);
		writer.flush();
	}

	private void recordLoginSuccess(Authentication auth, HttpServletRequest request) {
		LoginAttempt attempt = new LoginAttempt();
		attempt.setUsername(auth.getName());
		attempt.setAttemptedAt(new Date());
		attempt.setSuccessful(true);
		attempt.setIpAddress(RequestUtils.getClientIpAddr(request));
		loginAttemptService.save(attempt);
	}

	private void resetUserFailedAttempts(Authentication auth) {
		User user = userService.findByUsername(auth.getName());
		if (user == null || !(user.getConsecutiveLoginFailures() > 0)) {
			return;
		}

		user.setConsecutiveLoginFailures(0);
		userService.save(user);
	}

}

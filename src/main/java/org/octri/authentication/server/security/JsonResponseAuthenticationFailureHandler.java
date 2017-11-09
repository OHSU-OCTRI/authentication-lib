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

import org.octri.authentication.RequestUtils;
import org.octri.authentication.server.security.entity.LoginAttempt;
import org.octri.authentication.server.security.service.LoginAttemptService;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * AuthenticationFailureHandler suitable for use with JSON APIs. This handler records login failures and increments user
 * failed login attempts (locking the account if the maximum is exceeded).
 * 
 * The number of failed attempts prior to locking can be configured using the octri.security.max-login-attempts
 * property, which can be configured using the OCTRI_SECURITY_MAX_LOGIN_ATTEMPTS environment variable.
 * 
 * Adapted from ChimeraAuthenticationFailureHandler.
 * 
 * @author lawhead
 * @author harrelst
 *
 */
@Component
public class JsonResponseAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String username = request.getParameter("username");

		recordLoginFailure(username, exception.getMessage(), request);

		if (exception.getClass() == BadCredentialsException.class && userService.findByUsername(username) != null) {
			userService.incrementFailedAttempts(username);
		}

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		PrintWriter writer = response.getWriter();
		writer.write(exception.getMessage());
		writer.flush();
	}

	/**
	 * Creates a new {@link LoginAttempt} record for an unsuccessful login
	 * 
	 * @param username
	 * @param error
	 */
	private void recordLoginFailure(String username, String error, HttpServletRequest request) {
		LoginAttempt attempt = new LoginAttempt();
		attempt.setUsername(username);
		attempt.setIpAddress(RequestUtils.getClientIpAddr(request));
		attempt.setAttemptedAt(new Date());
		attempt.setSuccessful(false);
		attempt.setErrorType(error);
		loginAttemptService.save(attempt);
	}
}

package org.octri.authentication.server.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

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
 * This abstract failure handler can be extended to provide auditing of the login and increment the failed attempts flag, 
 * locking the account if the maximum is exceeded.
 * 
 * The number of failed attempts prior to locking can be configured using the property: 
 * 	octri.authentication.max-login-attempts
 * 
 * See {@link UserService}
 * 
 * Adapted from ChimeraAuthenticationFailureHandler.

 * @author yateam
 */
@Component
public class AuditLoginAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private UserService userService;

	/**
	 * Creates a new {@link LoginAttempt} record for an unsuccessful login
	 * 
	 * @param username
	 * @param error
	 * @param request
	 */
	protected void recordLoginFailure(String username, String error, HttpServletRequest request) {
		LoginAttempt attempt = new LoginAttempt();
		attempt.setUsername(username);
		attempt.setIpAddress(RequestUtils.getClientIpAddr(request));
		attempt.setAttemptedAt(new Date());
		attempt.setSuccessful(false);
		attempt.setErrorType(error);
		loginAttemptService.save(attempt);
	}

	/**
	 * Increments the failed attempts counter
	 * @param username
	 * @param exception
	 */
	protected void recordUserFailedAttempts(String username, AuthenticationException exception) {
		if (exception.getClass() == BadCredentialsException.class && userService.findByUsername(username) != null) {
			userService.incrementFailedAttempts(username);
		}

	}

}

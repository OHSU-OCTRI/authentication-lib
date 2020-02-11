package org.octri.authentication.server.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.octri.authentication.RequestUtils;
import org.octri.authentication.server.security.entity.LoginAttempt;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.DuplicateEmailException;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.service.LoginAttemptService;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * This abstract success handler can be extended to provide auditing of the login and reset the failed attempts flag.
 * 
 * @author yateam
 */
@Component
public abstract class AuditLoginAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Autowired
	private UserService userService;

	protected void recordLoginSuccess(Authentication auth, HttpServletRequest request) {
		LoginAttempt attempt = new LoginAttempt();
		attempt.setUsername(auth.getName());
		attempt.setAttemptedAt(new Date());
		attempt.setSuccessful(true);
		attempt.setIpAddress(RequestUtils.getClientIpAddr(request));
		loginAttemptService.save(attempt);
	}

	protected void resetUserFailedAttempts(Authentication auth) throws InvalidLdapUserDetailsException, DuplicateEmailException {
		User user = userService.findByUsername(auth.getName());
		if (user == null || !(user.getConsecutiveLoginFailures() > 0)) {
			return;
		}

		user.setConsecutiveLoginFailures(0);
		userService.save(user);
	}

}

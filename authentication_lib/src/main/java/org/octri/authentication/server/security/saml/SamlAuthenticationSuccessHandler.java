package org.octri.authentication.server.security.saml;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.RequestUtils;
import org.octri.authentication.server.security.entity.LoginAttempt;
import org.octri.authentication.server.security.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Records successful SAML login and redirects using the standard logic
 * in @link{SavedRequestAwareAuthenticationSuccessHandler}.
 */
@Component
public class SamlAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private static final Log log = LogFactory.getLog(SamlAuthenticationSuccessHandler.class);

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		log.info("Login: " + authentication.getName());
		recordLoginSuccess(authentication, request);
		super.onAuthenticationSuccess(request, response, authentication);

	}

	private void recordLoginSuccess(Authentication auth, HttpServletRequest request) {
		LoginAttempt attempt = new LoginAttempt();
		attempt.setUsername(auth.getName());
		attempt.setAttemptedAt(new Date());
		attempt.setSuccessful(true);
		attempt.setIpAddress(RequestUtils.getClientIpAddr(request));
		loginAttemptService.save(attempt);
	}

}

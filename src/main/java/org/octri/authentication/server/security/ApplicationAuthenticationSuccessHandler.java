package org.octri.authentication.server.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * This success handler can be used by applications to record the login event and redirect using the
 * standard logic in {@link SavedRequestAwareAuthenticationSuccessHandler}
 * 
 * @author yateam
 *
 */
@Component
public class ApplicationAuthenticationSuccessHandler extends AuditLoginAuthenticationSuccessHandler {

	private static final Log log = LogFactory.getLog(ApplicationAuthenticationSuccessHandler.class);

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
			throws IOException, ServletException {
		recordLoginSuccess(auth, request);
		resetUserFailedAttempts(auth);

		AuthenticationUserDetails userDetails = (AuthenticationUserDetails) auth.getPrincipal();

		log.info("Login: " + userDetails.getUsername());
		
		super.onAuthenticationSuccess(request, response, auth);
	}


}

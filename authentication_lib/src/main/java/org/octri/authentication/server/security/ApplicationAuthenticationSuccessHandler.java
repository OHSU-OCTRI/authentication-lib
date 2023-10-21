package org.octri.authentication.server.security;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.entity.SessionEvent.EventType;
import org.octri.authentication.server.security.service.SessionEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

	@Autowired
	private SessionEventService sessionEventService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
			throws IOException, ServletException {
		SecurityHelper securityHelper = new SecurityHelper(SecurityContextHolder.getContext());
		Boolean impersonating = securityHelper.hasRoleName(SwitchUserFilter.ROLE_PREVIOUS_ADMINISTRATOR);

		if (impersonating) {
			sessionEventService.logEvent(EventType.IMPERSONATION);
		} else {
			sessionEventService.logEvent(EventType.LOGIN);
			recordLoginSuccess(auth, request);
			try {
				resetUserFailedAttempts(auth);
			} catch (Exception ex) {
				throw new ServletException(ex);
			}
		}

		AuthenticationUserDetails userDetails = (AuthenticationUserDetails) auth.getPrincipal();
		log.debug("Login: " + userDetails.getUsername());

		super.onAuthenticationSuccess(request, response, auth);
	}

}

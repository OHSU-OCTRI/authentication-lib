/**
 * 
 */
package org.octri.authentication.server.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

/**
 * This failure handler can be used by applications to record the login event and redirect using the
 * standard logic in {@link SimpleUrlAuthenticationFailureHandler}
 * 
 * @author yateam
 *
 */
@Component
public class ApplicationAuthenticationFailureHandler extends AuditLoginAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String username = request.getParameter("username");

		recordLoginFailure(username, exception.getMessage(), request);
		recordUserFailedAttempts(username, exception);
		
		super.onAuthenticationFailure(request, response, exception);
	}

}

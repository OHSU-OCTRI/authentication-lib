package org.octri.authentication.server.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * This failure handler can be used by applications to record the login event and send a JSON response with 
 * status UNAUTHORIZED instead of a redirect.
 * 
 * @author yateam
 *
 */
@Component
public class JsonResponseAuthenticationFailureHandler extends AuditLoginAuthenticationFailureHandler {

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String username = request.getParameter("username");

		recordLoginFailure(username, exception.getMessage(), request);
		recordUserFailedAttempts(username, exception);

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		PrintWriter writer = response.getWriter();
		writer.write(exception.getMessage());
		writer.flush();
	}

}

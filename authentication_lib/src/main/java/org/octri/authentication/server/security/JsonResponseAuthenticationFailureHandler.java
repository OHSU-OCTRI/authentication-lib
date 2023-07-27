package org.octri.authentication.server.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

package org.octri.authentication.server.security;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * An authentication entrypoint suitable for JSON APIs that only returns the 401 unauthorized status code, rather than
 * redirecting.
 *
 * @author harrelst
 *
 */
@Component
public class StatusOnlyAuthenticationEntryPoint implements AuthenticationEntryPoint {

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.security.web.AuthenticationEntryPoint#commence(jakarta.servlet.http.HttpServletRequest,
	 * jakarta.servlet.http.HttpServletResponse, org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

	}

}

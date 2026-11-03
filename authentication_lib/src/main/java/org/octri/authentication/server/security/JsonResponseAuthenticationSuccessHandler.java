package org.octri.authentication.server.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

/**
 * This success handler can be used with JSON APIs to record the login event and send a JSON response with
 * status OK instead of a redirect.
 *
 * @author yateam
 *
 */
@Component
public class JsonResponseAuthenticationSuccessHandler extends AuditLoginAuthenticationSuccessHandler {

	private static final Log log = LogFactory.getLog(JsonResponseAuthenticationSuccessHandler.class);

	private final JsonMapper mapper;

	/**
	 * Constructor.
	 * 
	 * @param mapper
	 *            the application's auto-configured JSON mapper
	 */
	public JsonResponseAuthenticationSuccessHandler(JsonMapper mapper) {
		this.mapper = mapper;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
			throws IOException, ServletException {
		recordLoginSuccess(auth, request);
		try {
			resetUserFailedAttempts(auth);
		} catch (Exception ex) {
			throw new ServletException(ex);
		}

		AuthenticationUserDetails userDetails = (AuthenticationUserDetails) auth.getPrincipal();

		log.info("Login: " + userDetails.getUsername());

		response.setStatus(HttpServletResponse.SC_OK);
		PrintWriter writer = response.getWriter();
		mapper.writeValue(writer, userDetails);
		writer.flush();
	}

}

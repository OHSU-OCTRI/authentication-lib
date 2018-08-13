package org.octri.authentication.server.security;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

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

	private final ObjectMapper mapper;

	@Autowired
	public JsonResponseAuthenticationSuccessHandler(
			MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
		this.mapper = mappingJackson2HttpMessageConverter.getObjectMapper();
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
			throws IOException, ServletException {
		recordLoginSuccess(auth, request);
		try {
			resetUserFailedAttempts(auth);
		} catch (InvalidLdapUserDetailsException ex) {
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

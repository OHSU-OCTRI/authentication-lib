package org.octri.authentication.server.security.firewall;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Custom {@link RequestRejectedHandler} that logs a warning and returns a 400 Bad Request response. Prevents alerts
 * triggered by security scans.
 */
public class LoggingRequestRejectedHandler implements RequestRejectedHandler {

	private static final Log log = LogFactory.getLog(LoggingRequestRejectedHandler.class);

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			RequestRejectedException requestRejectedException) throws IOException, ServletException {
		log.warn(LogMessage.format("Request rejected: %s", requestRejectedException.getMessage()));
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
	}

}

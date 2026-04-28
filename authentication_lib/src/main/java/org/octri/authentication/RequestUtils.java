package org.octri.authentication;

import java.util.List;

import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Common methods to interact with a HttpServletRequest
 *
 * TODO Refactor to a common utilities library
 *
 */
public final class RequestUtils {

	private static final String UNKNOWN = "unknown";

	private RequestUtils() {
		// utility class
	}

	/**
	 * If any proxy or load balancer exists between the client and the server {@link HttpServletRequest#getRemoteAddr()}
	 * will return localhost or the address of the middle machine.
	 *
	 * @param request
	 *            a {@link HttpServletRequest}
	 * @return The remote address of the client accessing the server.
	 */
	public static String getClientIpAddr(HttpServletRequest request) {
		Assert.notNull(request, "request may not be null");
		List<String> headerNames = List.of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP",
				"HTTP_X_FORWARDED_FOR");
		String ip = null;

		// check common proxy headers
		for (String headerName : headerNames) {
			ip = request.getHeader(headerName);
			if (ip != null && ip.length() > 0 && !UNKNOWN.equalsIgnoreCase(ip)) {
				break;
			}
		}

		// IP not found in headers; default to getRemoteAddr()
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		// extract the client IP when the header contains a chain of IPs
		if (ip.indexOf(",") != -1) {
			ip = ip.substring(0, ip.indexOf(",")).trim();
		}

		return ip;
	}
}

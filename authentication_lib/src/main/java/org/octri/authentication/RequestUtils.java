package org.octri.authentication;

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

		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}

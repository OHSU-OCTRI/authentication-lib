package org.octri.authentication.server.security;

/**
 * Helper object that constructs authentication-related URLs.
 */
public class AuthenticationUrlHelper {

	private String baseUrl;
	private String contextPath;

	public AuthenticationUrlHelper(String baseUrl, String contextPath) {
		this.baseUrl = baseUrl;
		this.contextPath = contextPath;
	}

	/**
	 * Get the application's base URL prefix, including the hostname, port, and scheme.
	 *
	 * @return
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Set the application's base URL prefix, including the hostname, port, and scheme.
	 *
	 * @param baseUrl
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Get the application's context path.
	 *
	 * @return
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * Set the application's context path.
	 *
	 * @param contextPath
	 */
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * The full application URL including context path. No trailing slash.
	 *
	 * @return Full application URL with context path.
	 */
	public String getAppUrl() {
		return (baseUrl + contextPath).replaceAll("\\/*$", "");
	}

	/**
	 * The full URL for the login page.
	 *
	 * @return The URL for the login page
	 */
	public String getLoginUrl() {
		return getAppUrl() + "/login";
	}

	/**
	 * The full URL for resetting a password.
	 *
	 * @param resetToken
	 * @return The URL for resetting user's password, including token.
	 */
	public String getPasswordResetUrl(final String resetToken) {
		return getAppUrl() + "/user/password/reset?token=" + resetToken;
	}

}

package org.octri.authentication.server.security;

/**
 * Helper object that constructs authentication-related URLs.
 */
public class AuthenticationUrlHelper {

	private String baseUrl;
	private String contextPath;

	/**
	 * Constructor
	 * 
	 * @param baseUrl
	 *            the application's base URL, including the hostname, port, and scheme.
	 * @param contextPath
	 *            the application context path (initial path segment after the base URL)
	 */
	public AuthenticationUrlHelper(String baseUrl, String contextPath) {
		this.baseUrl = baseUrl;
		this.contextPath = contextPath;
	}

	/**
	 * Gets the application's base URL, including the hostname, port, and scheme.
	 *
	 * @return the configured base URL
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Sets the application's base URL, including the hostname, port, and scheme.
	 *
	 * @param baseUrl
	 *            the base URL to set
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Gets the application's context path (initial path segment after the base URL).
	 *
	 * @return the configured context path
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * Sets the application's context path.
	 *
	 * @param contextPath
	 *            the context path to set
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
	 *            a single-use password reset token string
	 * @return The URL for resetting user's password, including token.
	 */
	public String getPasswordResetUrl(final String resetToken) {
		return getAppUrl() + "/user/password/reset?token=" + resetToken;
	}

}

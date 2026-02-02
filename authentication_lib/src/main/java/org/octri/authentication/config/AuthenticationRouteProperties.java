package org.octri.authentication.config;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for routes used in authentication.
 */
@ConfigurationProperties(prefix = "octri.authentication.routes")
public class AuthenticationRouteProperties {

	/**
	 * Default list of routes that do not require authentication.
	 *
	 * @see #getPublicRoutesWithDefaults
	 */
	public static final String[] DEFAULT_PUBLIC_ROUTES = new String[] {
			"/",
			"/actuator/health",
			"/actuator/health/liveness",
			"/actuator/health/readiness",
			"/assets/**",
			"/css/*",
			"/error",
			"/index.html",
			"/js/*",
			"/login*",
			"/login/**",
			"/login*/**",
			"/user/password/**",
			"/webjars/**" };

	/**
	 * The route of the authentication entry point. Defaults to "/login".
	 */
	private String loginUrl = "/login";

	/**
	 * The route the user will be sent to after login unless a target URL has been set in their session. Defaults to
	 * "/admin/user/list".
	 */
	private String defaultLoginSuccessUrl = "/admin/user/list";

	/**
	 * The route the user will be sent to if their login fails. Defaults to "/login?error".
	 */
	private String loginFailureRedirectUrl = "/login?error";

	/**
	 * The route that logs the user out. Defaults to "/logout".
	 */
	private String logoutUrl = "/logout";

	/**
	 * The route that the user will be sent to after logging out. Defaults to "/login".
	 */
	private String logoutSuccessUrl = "/login";

	/**
	 * Application routes that do not require authentication. Defaults to an empty array.
	 *
	 * @see DEFAULT_PUBLIC_ROUTES
	 * @see getPublicRoutesWithDefaults
	 */
	private String[] customPublicRoutes = new String[] {};

	/**
	 * Gets the configured {@link #loginUrl}.
	 *
	 * @return the configured login URL
	 */
	public String getLoginUrl() {
		return loginUrl;
	}

	/**
	 * Sets the URL of the login page.
	 *
	 * @param loginUrl
	 *            login form path
	 */
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	/**
	 * Gets the configured {@link #defaultLoginSuccessUrl}.
	 *
	 * @return the configured login success URL
	 */
	public String getDefaultLoginSuccessUrl() {
		return defaultLoginSuccessUrl;
	}

	/**
	 * Sets the default URL the user will be sent to after login.
	 *
	 * @param defaultLoginSuccessUrl
	 *            default post-login URL path
	 */
	public void setDefaultLoginSuccessUrl(String defaultLoginSuccessUrl) {
		this.defaultLoginSuccessUrl = defaultLoginSuccessUrl;
	}

	/**
	 * Gets the configured {@link #loginFailureRedirectUrl}.
	 *
	 * @return the configured login failure redirect URL
	 */
	public String getLoginFailureRedirectUrl() {
		return loginFailureRedirectUrl;
	}

	/**
	 * Sets the URL the user will be sent to if their login is unsuccessful.
	 *
	 * @param loginFailureRedirectUrl
	 *            the URL after unsuccessful login
	 */
	public void setLoginFailureRedirectUrl(String loginFailureRedirectUrl) {
		this.loginFailureRedirectUrl = loginFailureRedirectUrl;
	}

	/**
	 * Gets the configured {@link #logoutUrl}.
	 *
	 * @return the configured logout URL
	 */
	public String getLogoutUrl() {
		return logoutUrl;
	}

	/**
	 * Sets the URL of the logout form.
	 *
	 * @param logoutUrl
	 *            logout URL path
	 */
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	/**
	 * Gets the configured {@link #logoutSuccessUrl}.
	 *
	 * @return the configured logout success URL
	 */
	public String getLogoutSuccessUrl() {
		return logoutSuccessUrl;
	}

	/**
	 * Sets the URL displayed after the user logs out.
	 *
	 * @param logoutSuccessUrl
	 *            post-logout URL path
	 */
	public void setLogoutSuccessUrl(String logoutSuccessUrl) {
		this.logoutSuccessUrl = logoutSuccessUrl;
	}

	/**
	 * Gets the list of custom routes that do not require authentication.
	 *
	 * @return array of route pattern strings
	 */
	public String[] getCustomPublicRoutes() {
		return customPublicRoutes;
	}

	/**
	 * Sets the list of custom routes that will do not require authentication.
	 *
	 * @param customPublicRoutes
	 *            array of route pattern strings
	 */
	public void setCustomPublicRoutes(String[] customPublicRoutes) {
		this.customPublicRoutes = customPublicRoutes;
	}

	/**
	 * Convenience method that gets the configured public application routes concatenated with the default public
	 * routes.
	 *
	 * @return array containing the default public routes, plus any custom public routes
	 */
	public String[] getPublicRoutesWithDefaults() {
		ArrayList<String> allPublicRoutes = new ArrayList<>(Arrays.asList(DEFAULT_PUBLIC_ROUTES));
		allPublicRoutes.addAll(Arrays.asList(customPublicRoutes));
		return allPublicRoutes.toArray(new String[0]);
	}

}

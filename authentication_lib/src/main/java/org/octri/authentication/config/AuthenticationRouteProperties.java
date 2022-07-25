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
	 * @see publicRoutes
	 * @see getPublicRoutesWithDefaults
	 */
	public static final String[] DEFAULT_PUBLIC_ROUTES = new String[] {
			"/",
			"/actuator/health",
			"/actuator/health/liveness",
			"/actuator/health/readiness",
			"/actuator/prometheus",
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

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getDefaultLoginSuccessUrl() {
		return defaultLoginSuccessUrl;
	}

	public void setDefaultLoginSuccessUrl(String defaultLoginSuccessUrl) {
		this.defaultLoginSuccessUrl = defaultLoginSuccessUrl;
	}

	public String getLoginFailureRedirectUrl() {
		return loginFailureRedirectUrl;
	}

	public void setLoginFailureRedirectUrl(String loginFailureRedirectUrl) {
		this.loginFailureRedirectUrl = loginFailureRedirectUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getLogoutSuccessUrl() {
		return logoutSuccessUrl;
	}

	public void setLogoutSuccessUrl(String logoutSuccessUrl) {
		this.logoutSuccessUrl = logoutSuccessUrl;
	}

	public String[] getCustomPublicRoutes() {
		return customPublicRoutes;
	}

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

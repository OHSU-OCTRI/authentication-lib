package org.octri.authentication.server.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AuthenticationUrlHelperTest {

	private static final String BASE_URL = "http://localhost:8080";
	private static final String CONTEXT_PATH = "/app";
	private static final String TOKEN = "9465565b-7150-4f95-9855-7997a2f6124a";
	private static final String APP_URL = BASE_URL + CONTEXT_PATH;
	private static final String RESET_PASSWORD_URL = APP_URL + "/user/password/reset?token=" + TOKEN;
	private static final String LOGIN_URL = APP_URL + "/login";

	private AuthenticationUrlHelper urlHelper;

	@BeforeEach
	public void setup() {
		urlHelper = new AuthenticationUrlHelper(BASE_URL, CONTEXT_PATH);
	}

	@Test
	public void testBuildAppUrl() {
		final String appUrl = urlHelper.getAppUrl();
		assertEquals(APP_URL, appUrl, "Builds correct app URL");
	}

	@Test
	public void testBuildAppUrlWithEmptyContextPath() {
		urlHelper.setContextPath("");
		final String appUrl = urlHelper.getAppUrl();
		assertEquals(BASE_URL, appUrl, "Can handle empty context path");
	}

	@Test
	public void testBuildAppUrlStripsTrailingSlash() {
		urlHelper.setContextPath("/");
		final String appUrl = urlHelper.getAppUrl();
		assertEquals(BASE_URL, appUrl, "Trims trailing slash from the URL");
	}

	@Test
	public void testBuildResetPasswordUrl() {
		final String resetPasswordUrl = urlHelper.getPasswordResetUrl(TOKEN);
		assertEquals(RESET_PASSWORD_URL, resetPasswordUrl, "Builds correct reset password URL");
	}

	@Test
	public void testBuildLoginUrl() {
		final String loginUrl = urlHelper.getLoginUrl();
		assertEquals(LOGIN_URL, loginUrl, "Builds correct login URL");
	}

}
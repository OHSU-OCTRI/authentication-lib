package org.octri.authentication.server.security.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.octri.authentication.EmailConfiguration;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Spy
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Mock
	private UserRepository userRepository;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private HttpServletRequest request;

	@Mock
	private JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

	@Mock
	private EmailConfiguration emailConfig;

	@Mock
	private PasswordResetTokenService passwordResetTokenService;

	@Mock
	private PasswordResetToken passwordResetToken;

	private User user;
	private static final String USERNAME = "foo";
	private static final String CURRENT_PASSWORD = "currentPassword";
	private static final String VALID_PASSWORD = "Abcdefg.1";
	private static final String INVALID_PASSWORD_WITH_USERNAME = "Abcdefg." + USERNAME;

	private static final String APP_URL = "http://localhost:8080/app";
	private static final String RESET_PASSWORD_URL = APP_URL + "/user/password/reset?token=secret-token";
	private static final String LOGIN_URL = APP_URL + "/login";

	@Before
	public void beforeEach() {
		user = new User(USERNAME, "Foo", "Bar", "OHSU", "foo@example.com");
		user.setPassword(passwordEncoder.encode(CURRENT_PASSWORD));
		when(userService.save(user)).thenReturn(user);
		when(request.getScheme()).thenReturn("http");
		when(request.getServerName()).thenReturn("localhost");
		when(request.getServerPort()).thenReturn(8080);
		when(request.getRequestURI()).thenReturn("/app");
		when(request.getContextPath()).thenReturn("/app");
		when(request.getQueryString()).thenReturn("");
		when(userService.buildAppUrl(request)).thenReturn(APP_URL);
		when(emailConfig.getFrom()).thenReturn("foo@example.com");
		when(userService.save(any(User.class)))
				.then(i -> i.getArgumentAt(0, User.class));
		// This is the trick that causes save() to return whatever is passed to it inside userService.
		// generatePasswordResetToken() creates a PasswordResetToken and sets a UUID on the token property.
		// generatePasswordResetToken() returns save(PasswordResetToken) which is why this techinque is used.
		when(passwordResetTokenService.save(any(PasswordResetToken.class)))
				.then(i -> i.getArgumentAt(0, PasswordResetToken.class));
		doNothing().when(mailSender).send(any(SimpleMailMessage.class));
	}

	@Test
	public void testSuccessfulPasswordChange() throws InvalidPasswordException {
		User saved = userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, VALID_PASSWORD);
		assertNotNull("User must not be null", saved);
		assertTrue("newPassword set correctly on User", passwordEncoder.matches(VALID_PASSWORD,
				saved.getPassword()));
	}

	@Test
	public void testNewAndConfirmPasswordsMustMatchOnChange() throws InvalidPasswordException {
		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("New and confirm new password values do not match");
		userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, "invalid_confirm_password");
	}

	@Test
	public void testCurrentPasswordMustMatchExistingOnChange() throws InvalidPasswordException {
		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("Current password doesn't match existing password");
		userService.changePassword(user, "not_current_password", CURRENT_PASSWORD, CURRENT_PASSWORD);
	}

	@Test
	public void testNewPasswordMustNotContainUsernameOnChange() throws InvalidPasswordException {
		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("Password must not include username");
		userService.changePassword(user, CURRENT_PASSWORD, INVALID_PASSWORD_WITH_USERNAME,
				INVALID_PASSWORD_WITH_USERNAME);
	}

	@Test
	public void testNewPasswordMustNotBePreviousPasswordOnChange() throws InvalidPasswordException {
		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("Must not use current password");
		userService.changePassword(user, CURRENT_PASSWORD, CURRENT_PASSWORD, CURRENT_PASSWORD);
	}

	@Test
	public void testResetPassword() throws InvalidPasswordException {
		final String password = "Abcdefg.1";
		final String token = "9465565b-7150-4f95-9855-7997a2f6124a";
		UserService spyUserService = spy(userService);

		PasswordResetToken passwordResetToken = new PasswordResetToken();
		passwordResetToken.setUser(user);
		passwordResetToken.setToken(token);
		passwordResetToken.setExpiryDate(new Date());

		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(passwordResetToken);

		User saved = spyUserService.resetPassword(password, password, token);

		verify(passwordResetTokenService).save(any(PasswordResetToken.class));
		assertTrue("User's password should match the hashed password on the User record",
				passwordEncoder.matches(password, saved.getPassword()));
	}

	@Test
	public void testSendPasswordResetTokenEmail() {
		userService.sendPasswordResetTokenEmail(user, "mock token", request, false);
		verify(mailSender).send(any(SimpleMailMessage.class));
	}

	@Test
	public void testSendPasswordResetEmailConfirmation() {
		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(passwordResetToken);
		when(passwordResetToken.getUser()).thenReturn(user);
		userService.sendPasswordResetEmailConfirmation("mock token", request, false);
		verify(mailSender).send(any(SimpleMailMessage.class));
	}

	@Test
	public void testBuildAppUrl() {
		final String appUrl = userService.buildAppUrl(request);
		assertEquals("Builds correct app URL", APP_URL, appUrl);
	}

	@Test
	public void testBuildResetPasswordUrl() {
		final String resetPasswordUrl = userService.buildResetPasswordUrl("secret-token", request);
		assertEquals("Builds correct reset password URL", RESET_PASSWORD_URL, resetPasswordUrl);
	}

	@Test
	public void testBuildLoginUrl() {
		final String loginUrl = userService.buildLoginUrl(request);
		assertEquals("Builds correct login URL", LOGIN_URL, loginUrl);
	}
}

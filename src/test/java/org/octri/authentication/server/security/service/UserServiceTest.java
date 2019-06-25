package org.octri.authentication.server.security.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.octri.authentication.EmailConfiguration;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

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
	private JavaMailSender mailSender;

	@Mock
	private EmailConfiguration emailConfig;

	@Mock
	private PasswordResetTokenService passwordResetTokenService;

	@Mock
	private PasswordResetToken passwordResetToken;

	@Mock
	private DirContextOperations ldapUser;

	@Mock
	private FilterBasedLdapUserSearch ldapSearch;

	private User user;
	private static final String USERNAME = "foo";
	private static final String CURRENT_PASSWORD = "currentPassword";
	private static final String VALID_PASSWORD = "Abcdefg.1";
	private static final String INVALID_PASSWORD_WITH_USERNAME = "Abcdefg." + USERNAME;

	private static final String BASE_URL = "http://localhost:8080";
	private static final String CONTEXT_PATH = "/app";
	private static final String APP_URL = BASE_URL + CONTEXT_PATH;
	private static final String RESET_PASSWORD_URL = APP_URL + "/user/password/reset?token=secret-token";
	private static final String LOGIN_URL = APP_URL + "/login";
	private static final String TOKEN = "9465565b-7150-4f95-9855-7997a2f6124a";

	@Before
	public void beforeEach() throws InvalidLdapUserDetailsException {
		user = new User(USERNAME, "Foo", "Bar", "OHSU", "foo@example.com");
		user.setPassword(passwordEncoder.encode(CURRENT_PASSWORD));
		user.setEmail("foo@example.com");
		userService.setTableBasedEnabled(true);

		userService.setBaseUrl(BASE_URL);
		userService.setContextPath(CONTEXT_PATH);

		when(emailConfig.getFrom()).thenReturn("foo@example.com");
		// This is the trick that causes save() to return whatever is passed to it inside userService.
		// generatePasswordResetToken() creates a PasswordResetToken and sets a UUID on the token property.
		// generatePasswordResetToken() returns save(PasswordResetToken) which is why this technique is used.
		when(userService.save(user)).thenReturn(user);
		doNothing().when(mailSender).send(any(SimpleMailMessage.class));
	}

	@Test
	public void testSuccessfulPasswordChange() throws InvalidPasswordException, InvalidLdapUserDetailsException {
		User saved = userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, VALID_PASSWORD);
		assertNotNull("User must not be null", saved);
		assertTrue("newPassword set correctly on User", passwordEncoder.matches(VALID_PASSWORD,
				saved.getPassword()));
	}

	@Test
	public void testNewAndConfirmPasswordsMustMatchOnChange()
			throws InvalidPasswordException, InvalidLdapUserDetailsException {
		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("New and confirm new password values do not match");
		userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, "invalid_confirm_password");
	}

	@Test
	public void testCurrentPasswordMustMatchExistingOnChange()
			throws InvalidPasswordException, InvalidLdapUserDetailsException {
		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("Current password doesn't match existing password");
		userService.changePassword(user, "not_current_password", CURRENT_PASSWORD, CURRENT_PASSWORD);
	}

	@Test
	public void testNewPasswordMustNotContainUsernameOnChange()
			throws InvalidPasswordException, InvalidLdapUserDetailsException {
		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("Password must not include username");
		userService.changePassword(user, CURRENT_PASSWORD, INVALID_PASSWORD_WITH_USERNAME,
				INVALID_PASSWORD_WITH_USERNAME);
	}

	@Test
	public void testNewPasswordMustNotBePreviousPasswordOnChange()
			throws InvalidPasswordException, InvalidLdapUserDetailsException {
		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("Must not use current password");
		userService.changePassword(user, CURRENT_PASSWORD, CURRENT_PASSWORD, CURRENT_PASSWORD);
	}

	@Test
	public void testResetPassword() throws InvalidPasswordException, InvalidLdapUserDetailsException {
		final String password = "Abcdefg.1";
		UserService spyUserService = spy(userService);

		PasswordResetToken passwordResetToken = new PasswordResetToken(user);

		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(passwordResetToken);

		User saved = spyUserService.resetPassword(password, password, TOKEN);

		verify(passwordResetTokenService).expireToken(any(PasswordResetToken.class));
		assertTrue("User's password should match the hashed password on the User record",
				passwordEncoder.matches(password, saved.getPassword()));
		assertTrue("Returned user should have encoded password",
				passwordEncoder.matches(password, saved.getPassword()));
	}

	@Test
	public void testResetPasswordWithInvalidConfirmPassword()
			throws InvalidPasswordException, InvalidLdapUserDetailsException {
		final String password = "Abcdefg.1";
		final String confirmPassword = "Abcdefg.2";
		UserService spyUserService = spy(userService);

		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(new PasswordResetToken(user));

		expectedException.expect(InvalidPasswordException.class);
		expectedException.expectMessage("New and confirm new password values do not match");
		spyUserService.resetPassword(password, confirmPassword, TOKEN);
	}

	@Test
	public void testResetPasswordWithInvalidToken() throws InvalidPasswordException, InvalidLdapUserDetailsException {
		final String password = "Abcdefg.1";
		UserService spyUserService = spy(userService);

		// Ensures the token will not be found.
		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(null);

		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage("Could not find existing token");
		spyUserService.resetPassword(password, password, TOKEN);
	}

	@Test
	public void testTokenIsExpiredOnFirstUse() throws InvalidPasswordException, InvalidLdapUserDetailsException {
		final String password = "Abcdefg.1";
		UserService spyUserService = spy(userService);

		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(new PasswordResetToken(user));

		spyUserService.resetPassword(password, password, TOKEN);
		verify(passwordResetTokenService).expireToken(any(PasswordResetToken.class));
	}

	@Test
	public void testSendPasswordResetTokenEmail() {
		userService.sendPasswordResetTokenEmail(new PasswordResetToken(user), request, false, false);
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
		final String appUrl = userService.buildAppUrl();
		assertEquals("Builds correct app URL", APP_URL, appUrl);
	}

	@Test
	public void testBuildResetPasswordUrl() {
		final String resetPasswordUrl = userService.buildResetPasswordUrl("secret-token");
		assertEquals("Builds correct reset password URL", RESET_PASSWORD_URL, resetPasswordUrl);
	}

	@Test
	public void testBuildLoginUrl() {
		final String loginUrl = userService.buildLoginUrl();
		assertEquals("Builds correct login URL", LOGIN_URL, loginUrl);
	}

	@Test
	public void testSuccessfulSaveWithLdapOnlyEnabled() throws InvalidLdapUserDetailsException {
		userService.setTableBasedEnabled(false);
		when(ldapSearch.searchForUser(any())).thenReturn(ldapUser);
		when(ldapUser.getStringAttribute("mail")).thenReturn("foo@example.com");

		User user = new User("foo", "Foo", "Bar", "OHSU", "foo@example.com");
		userService.save(user);

		verify(userRepository).save(user);
	}

	@Test
	public void testExceptionOnSaveWithLdapOnlyEnabled() throws InvalidLdapUserDetailsException {
		userService.setTableBasedEnabled(false);
		when(ldapSearch.searchForUser(any())).thenReturn(ldapUser);
		when(ldapUser.getStringAttribute("mail")).thenReturn("foo@example.com");

		User user = new User("foo", "Foo", "Bar", "OHSU", "foobar@example.com");

		expectedException.expect(InvalidLdapUserDetailsException.class);
		userService.save(user);
	}
}

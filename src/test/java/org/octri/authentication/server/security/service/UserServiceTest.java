package org.octri.authentication.server.security.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.ImmutablePair;
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
import org.octri.authentication.server.security.password.Messages;
import org.octri.authentication.server.security.repository.UserRepository;
import org.octri.authentication.utils.ProfileUtils;
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

	@Mock
	private ProfileUtils profileUtils;

	private User user;
	private static final String USERNAME = "foo";
	private static final String CURRENT_PASSWORD = "currentPassword1";
	private static final String VALID_PASSWORD = "Abcdefg.1";
	private static final String INVALID_PASSWORD_WITH_USERNAME = "Abcdefg.1" + USERNAME;

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
	public void testSuccessfulPasswordChange() throws InvalidLdapUserDetailsException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, VALID_PASSWORD);
		final User saved = result.left;
		assertNotNull("User must not be null", saved);
		assertTrue("newPassword set correctly on User", passwordEncoder.matches(VALID_PASSWORD, saved.getPassword()));
	}

	@Test
	public void testPasswordChangeResetsCredentialMetadata() throws InvalidLdapUserDetailsException {
		user.setCredentialsExpired(true);
		user.setCredentialsExpirationDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
		user.setConsecutiveLoginFailures(7);

		Date now = Date.from(Instant.now());
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, VALID_PASSWORD);
		final User saved = result.left;
		assertFalse("Credentials expired flag should be false", saved.getCredentialsExpired());
		assertTrue("New expiration date is in the future", saved.getCredentialsExpirationDate().after(now));
		assertTrue("Consecutive login failures should be 0", saved.getConsecutiveLoginFailures() == 0);
	}

	@Test
	public void testNewAndConfirmPasswordsMustMatchOnChange() throws InvalidLdapUserDetailsException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, "invalid_confirm_password");
		assertFalse("Should return errors", result.right.isEmpty());
		assertEquals("Should return 1 error", 1, result.right.size());
		assertTrue("Should return the correct error message", result.right.contains(Messages.NEW_AND_CONFIRM_PASSWORDS_MISMATCH));
	}

	@Test
	public void testCurrentPasswordMustMatchExistingOnChange() throws InvalidLdapUserDetailsException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, "not_current_password", VALID_PASSWORD, VALID_PASSWORD);
		assertFalse("Should return errors", result.right.isEmpty());
		assertEquals("Should return 1 error", 1, result.right.size());
		assertTrue("Should return the correct error message", result.right.contains(Messages.CURRENT_PASSWORD_INCORRECT));
	}

	@Test
	public void testNewPasswordMustNotContainUsernameOnChange() throws InvalidLdapUserDetailsException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, INVALID_PASSWORD_WITH_USERNAME, INVALID_PASSWORD_WITH_USERNAME);
		assertFalse("Should return errors", result.right.isEmpty());
		assertEquals("Should return 1 error", 1, result.right.size());
		assertTrue("Should return the correct error message", result.right.contains(Messages.PASSWORDS_MUST_NOT_INCLUDE_USERNAME));
	}

	@Test
	public void testNewPasswordMustNotBeCurrentPasswordOnChange() throws InvalidLdapUserDetailsException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, CURRENT_PASSWORD, CURRENT_PASSWORD);
		assertFalse("Should return errors", result.right.isEmpty());
		assertEquals("Should return 1 error", 1, result.right.size());
		assertTrue("Should return the correct error message", result.right.contains(Messages.MUST_NOT_USE_CURRENT_PASSWORD));
	}

	@Test
	public void testResetPassword() throws InvalidLdapUserDetailsException {
		final String password = "Abcdefg.1";
		UserService spyUserService = spy(userService);

		PasswordResetToken passwordResetToken = new PasswordResetToken(user);

		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(passwordResetToken);

		ImmutablePair<User, List<String>> result = spyUserService.resetPassword(password, password, TOKEN);

		verify(passwordResetTokenService).expireToken(any(PasswordResetToken.class));
		assertTrue("User's password should match the hashed password on the User record",
				passwordEncoder.matches(password, result.left.getPassword()));
		assertTrue("Returned user should have encoded password",
				passwordEncoder.matches(password, result.left.getPassword()));
	}

	@Test
	public void testPasswordResetResetsCredentialMetadata() throws InvalidLdapUserDetailsException {
		user.setCredentialsExpired(true);
		user.setCredentialsExpirationDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
		user.setConsecutiveLoginFailures(7);

		PasswordResetToken passwordResetToken = new PasswordResetToken(user);
		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(passwordResetToken);

		Date now = Date.from(Instant.now());
		ImmutablePair<User, List<String>> result = userService.resetPassword(VALID_PASSWORD, VALID_PASSWORD, passwordResetToken.getToken());
		User saved = result.left;
		assertFalse("Credentials expired flag should be false", saved.getCredentialsExpired());
		assertTrue("New expiration date is in the future", saved.getCredentialsExpirationDate().after(now));
		assertTrue("Consecutive login failures should be 0", saved.getConsecutiveLoginFailures() == 0);
	}

	@Test
	public void testResetPasswordWithInvalidConfirmPassword() throws InvalidLdapUserDetailsException {
		final String password = "Abcdefg.1";
		final String confirmPassword = "Abcdefg.2";
		UserService spyUserService = spy(userService);

		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(new PasswordResetToken(user));

		ImmutablePair<User, List<String>> result = spyUserService.resetPassword(password, confirmPassword, TOKEN);
		assertFalse("Should return errors", result.right.isEmpty());
		assertEquals("Should return 1 error", 1, result.right.size());
		assertEquals("Should return the correct error message", Messages.NEW_AND_CONFIRM_PASSWORDS_MISMATCH, result.right.get(0));
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

	@Test
	public void testCurrentPasswordShouldMatchDatabaseValue() {
		List<String> reasons = new ArrayList<>();

		reasons = userService.validatePassword(user, "not-current-password", VALID_PASSWORD, VALID_PASSWORD);
		assertEquals("Should return 1 error", 1, reasons.size());
		assertEquals("Current does not match value in database", Messages.CURRENT_PASSWORD_INCORRECT, reasons.get(0));
	}

	@Test
	public void testNewPasswordShouldMatchConfirmValue() {
		List<String> reasons = new ArrayList<>();

		reasons = userService.validatePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, "confirm-password-wrong");
		assertEquals("Should return 1 error", 1, reasons.size());
		assertEquals("New and confirm passwords should be the same.", Messages.NEW_AND_CONFIRM_PASSWORDS_MISMATCH, reasons.get(0));
	}

	@Test
	public void testPasswordDoesNotContainUsername() {
		List<String> reasons = new ArrayList<>();

		reasons = userService.validatePassword(user, CURRENT_PASSWORD, INVALID_PASSWORD_WITH_USERNAME, INVALID_PASSWORD_WITH_USERNAME);
		assertEquals("Should return 1 error", 1, reasons.size());
		assertEquals("Password should not contain username", Messages.PASSWORDS_MUST_NOT_INCLUDE_USERNAME, reasons.get(0));
	}

	@Test
	public void testPreventUsingCurrentPassword() {
		List<String> reasons = new ArrayList<>();

		reasons = userService.validatePassword(user, CURRENT_PASSWORD, CURRENT_PASSWORD, CURRENT_PASSWORD);
		assertEquals("Should return 1 error", 1, reasons.size());
		assertEquals("Password should not be the current", Messages.MUST_NOT_USE_CURRENT_PASSWORD, reasons.get(0));
	}

	@Test
	public void testPasswordShouldContainCaptialLetterOrSymbol() {
		List<String> reasons = new ArrayList<>();

		final String invalidPassword = "asdf1asdf";

		reasons = userService.validatePassword(user, CURRENT_PASSWORD, invalidPassword, invalidPassword);
		assertEquals("Should return 1 error", 1, reasons.size());
		assertEquals("Password should contain either a captial letter or symbol", Messages.PASSWORD_INSUFFICIENT_CHARACTERISTICS, reasons.get(0));

		final String withCapital = invalidPassword + "L";
		reasons = userService.validatePassword(user, CURRENT_PASSWORD, withCapital, withCapital);
		assertTrue("Should return 0 errors", reasons.isEmpty());

		final String withSymbol = invalidPassword + "-";
		reasons = userService.validatePassword(user, CURRENT_PASSWORD, withSymbol, withSymbol);
		assertTrue("Should return 0 errors", reasons.isEmpty());

		final String withCapitalAndSymbol = invalidPassword + "M/";
		reasons = userService.validatePassword(user, CURRENT_PASSWORD, withCapitalAndSymbol, withCapitalAndSymbol);
		assertTrue("Should return 0 errors", reasons.isEmpty());
	}
}

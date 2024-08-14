package org.octri.authentication.server.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.exception.UserManagementException;
import org.octri.authentication.server.security.password.Messages;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Spy
	private OctriAuthenticationProperties authenticationProperties = new OctriAuthenticationProperties();

	@Spy
	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Mock
	private UserRepository userRepository;

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
	private static final String CURRENT_PASSWORD = "currentPassword1";
	private static final String VALID_PASSWORD = "Abcdefg.1";
	private static final String INVALID_PASSWORD_WITH_USERNAME = "Abcdefg.1" + USERNAME;

	private static final String TOKEN = "9465565b-7150-4f95-9855-7997a2f6124a";

	@BeforeEach
	public void beforeEach() throws UserManagementException {
		user = new User(USERNAME, "Foo", "Bar", "OHSU", "foo@example.com");
		user.setPassword(passwordEncoder.encode(CURRENT_PASSWORD));
		user.setEmail("foo@example.com");
		userService.setTableBasedEnabled(true);
	}

	@Test
	public void testSuccessfulPasswordChange() throws UserManagementException {
		when(userService.save(user)).thenReturn(user);
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD,
				VALID_PASSWORD);
		final User saved = result.left;
		assertNotNull(saved, "User must not be null");
		assertTrue(passwordEncoder.matches(VALID_PASSWORD, saved.getPassword()), "newPassword set correctly on User");
	}

	@Test
	public void testPasswordChangeResetsCredentialMetadata() throws UserManagementException {
		user.setCredentialsExpired(true);
		user.setCredentialsExpirationDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
		user.setConsecutiveLoginFailures(7);

		when(userService.save(user)).thenReturn(user);

		Date now = Date.from(Instant.now());
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD,
				VALID_PASSWORD);
		final User saved = result.left;
		assertFalse(saved.getCredentialsExpired(), "Credentials expired flag should be false");
		assertTrue(saved.getCredentialsExpirationDate().after(now), "New expiration date is in the future");
		assertTrue(saved.getConsecutiveLoginFailures() == 0, "Consecutive login failures should be 0");
	}

	@Test
	public void testNewAndConfirmPasswordsMustMatchOnChange() throws UserManagementException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, VALID_PASSWORD,
				"invalid_confirm_password");
		assertFalse(result.right.isEmpty(), "Should return errors");
		assertEquals(1, result.right.size(), "Should return 1 error");
		assertTrue(result.right.contains(Messages.NEW_AND_CONFIRM_PASSWORDS_MISMATCH),
				"Should return the correct error message");
	}

	@Test
	public void testCurrentPasswordMustMatchExistingOnChange() throws UserManagementException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, "not_current_password",
				VALID_PASSWORD, VALID_PASSWORD);
		assertFalse(result.right.isEmpty(), "Should return errors");
		assertEquals(1, result.right.size(), "Should return 1 error");
		assertTrue(result.right.contains(Messages.CURRENT_PASSWORD_INCORRECT),
				"Should return the correct error message");
	}

	@Test
	public void testNewPasswordMustNotContainUsernameOnChange() throws UserManagementException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD,
				INVALID_PASSWORD_WITH_USERNAME, INVALID_PASSWORD_WITH_USERNAME);
		assertFalse(result.right.isEmpty(), "Should return errors");
		assertEquals(1, result.right.size(), "Should return 1 error");
		assertTrue(result.right.contains(Messages.PASSWORDS_MUST_NOT_INCLUDE_USERNAME),
				"Should return the correct error message");
	}

	@Test
	public void testNewPasswordMustNotBeCurrentPasswordOnChange() throws UserManagementException {
		ImmutablePair<User, List<String>> result = userService.changePassword(user, CURRENT_PASSWORD, CURRENT_PASSWORD,
				CURRENT_PASSWORD);
		assertFalse(result.right.isEmpty(), "Should return errors");
		assertEquals(1, result.right.size(), "Should return 1 error");
		assertTrue(result.right.contains(Messages.MUST_NOT_USE_CURRENT_PASSWORD),
				"Should return the correct error message");
	}

	@Test
	public void testResetPassword() throws UserManagementException {
		final String password = "Abcdefg.1";
		UserService spyUserService = spy(userService);

		PasswordResetToken passwordResetToken = new PasswordResetToken(user);

		when(userService.save(user)).thenReturn(user);
		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(passwordResetToken);

		ImmutablePair<User, List<String>> result = spyUserService.resetPassword(password, password, TOKEN);

		verify(passwordResetTokenService).expireToken(any(PasswordResetToken.class));
		assertTrue(passwordEncoder.matches(password, result.left.getPassword()),
				"User's password should match the hashed password on the User record");
		assertTrue(passwordEncoder.matches(password, result.left.getPassword()),
				"Returned user should have encoded password");
	}

	@Test
	public void testPasswordResetResetsCredentialMetadata() throws UserManagementException {
		user.setCredentialsExpired(true);
		user.setCredentialsExpirationDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
		user.setConsecutiveLoginFailures(7);

		PasswordResetToken passwordResetToken = new PasswordResetToken(user);
		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(passwordResetToken);
		when(userService.save(user)).thenReturn(user);

		Date now = Date.from(Instant.now());
		ImmutablePair<User, List<String>> result = userService.resetPassword(VALID_PASSWORD, VALID_PASSWORD,
				passwordResetToken.getToken());
		User saved = result.left;
		assertFalse(saved.getCredentialsExpired(), "Credentials expired flag should be false");
		assertTrue(saved.getCredentialsExpirationDate().after(now), "New expiration date is in the future");
		assertTrue(saved.getConsecutiveLoginFailures() == 0, "Consecutive login failures should be 0");
	}

	@Test
	public void testResetPasswordWithInvalidConfirmPassword() throws UserManagementException {
		final String password = "Abcdefg.1";
		final String confirmPassword = "Abcdefg.2";
		UserService spyUserService = spy(userService);

		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(new PasswordResetToken(user));

		ImmutablePair<User, List<String>> result = spyUserService.resetPassword(password, confirmPassword, TOKEN);
		assertFalse(result.right.isEmpty(), "Should return errors");
		assertEquals(1, result.right.size(), "Should return 1 error");
		assertEquals(Messages.NEW_AND_CONFIRM_PASSWORDS_MISMATCH, result.right.get(0),
				"Should return the correct error message");
	}

	@Test
	public void testResetPasswordWithInvalidToken() throws InvalidPasswordException, UserManagementException {
		final String password = "Abcdefg.1";
		UserService spyUserService = spy(userService);

		// Ensures the token will not be found.
		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(null);

		Throwable exceptionThrown = assertThrows(IllegalArgumentException.class, () -> {
			spyUserService.resetPassword(password, password, TOKEN);
		});
		assertEquals("Could not find existing token", exceptionThrown.getMessage());
	}

	@Test
	public void testTokenIsExpiredOnFirstUse() throws InvalidPasswordException, UserManagementException {
		final String password = "Abcdefg.1";
		UserService spyUserService = spy(userService);

		when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(new PasswordResetToken(user));
		when(userService.save(user)).thenReturn(user);

		spyUserService.resetPassword(password, password, TOKEN);
		verify(passwordResetTokenService).expireToken(any(PasswordResetToken.class));
	}

	@Test
	public void testSuccessfulSaveWithLdapOnlyEnabled() throws UserManagementException {
		userService.setTableBasedEnabled(false);
		when(ldapSearch.searchForUser(any())).thenReturn(ldapUser);
		when(ldapUser.getStringAttribute("mail")).thenReturn("foo@example.com");
		when(userService.save(user)).thenReturn(user);

		User user = new User("foo", "Foo", "Bar", "OHSU", "foo@example.com");
		userService.save(user);

		verify(userRepository).save(user);
	}

	@Test
	public void testExceptionOnSaveWithLdapOnlyEnabled() throws UserManagementException {
		userService.setTableBasedEnabled(false);
		when(ldapSearch.searchForUser(any())).thenReturn(ldapUser);
		when(ldapUser.getStringAttribute("mail")).thenReturn("foo@example.com");

		User user = new User("foo", "Foo", "Bar", "OHSU", "foobar@example.com");

		assertThrows(InvalidLdapUserDetailsException.class, () -> {
			userService.save(user);
		});
	}

	@Test
	public void testCurrentPasswordShouldMatchDatabaseValue() {
		List<String> reasons = new ArrayList<>();

		reasons = userService.validatePassword(user, "not-current-password", VALID_PASSWORD, VALID_PASSWORD);
		assertEquals(1, reasons.size(), "Should return 1 error");
		assertEquals(Messages.CURRENT_PASSWORD_INCORRECT, reasons.get(0), "Current does not match value in database");
	}

	@Test
	public void testNewPasswordShouldMatchConfirmValue() {
		List<String> reasons = new ArrayList<>();

		reasons = userService.validatePassword(user, CURRENT_PASSWORD, VALID_PASSWORD, "confirm-password-wrong");
		assertEquals(1, reasons.size(), "Should return 1 error");
		assertEquals(Messages.NEW_AND_CONFIRM_PASSWORDS_MISMATCH, reasons.get(0),
				"New and confirm passwords should be the same.");
	}

	@Test
	public void testPasswordDoesNotContainUsername() {
		List<String> reasons = new ArrayList<>();

		reasons = userService.validatePassword(user, CURRENT_PASSWORD, INVALID_PASSWORD_WITH_USERNAME,
				INVALID_PASSWORD_WITH_USERNAME);
		assertEquals(1, reasons.size(), "Should return 1 error");
		assertEquals(Messages.PASSWORDS_MUST_NOT_INCLUDE_USERNAME, reasons.get(0),
				"Password should not contain username");
	}

	@Test
	public void testPreventUsingCurrentPassword() {
		List<String> reasons = new ArrayList<>();

		reasons = userService.validatePassword(user, CURRENT_PASSWORD, CURRENT_PASSWORD, CURRENT_PASSWORD);
		assertEquals(1, reasons.size(), "Should return 1 error");
		assertEquals(Messages.MUST_NOT_USE_CURRENT_PASSWORD, reasons.get(0), "Password should not be the current");
	}

	@Test
	public void testPasswordShouldContainCaptialLetterOrSymbol() {
		List<String> reasons = new ArrayList<>();

		final String invalidPassword = "asdf1asdf";

		reasons = userService.validatePassword(user, CURRENT_PASSWORD, invalidPassword, invalidPassword);
		assertEquals(1, reasons.size(), "Should return 1 error");
		assertEquals(Messages.PASSWORD_INSUFFICIENT_CHARACTERISTICS, reasons.get(0),
				"Password should contain either a captial letter or symbol");

		final String withCapital = invalidPassword + "L";
		reasons = userService.validatePassword(user, CURRENT_PASSWORD, withCapital, withCapital);
		assertTrue(reasons.isEmpty(), "Should return 0 errors");

		final String withSymbol = invalidPassword + "-";
		reasons = userService.validatePassword(user, CURRENT_PASSWORD, withSymbol, withSymbol);
		assertTrue(reasons.isEmpty(), "Should return 0 errors");

		final String withCapitalAndSymbol = invalidPassword + "M/";
		reasons = userService.validatePassword(user, CURRENT_PASSWORD, withCapitalAndSymbol, withCapitalAndSymbol);
		assertTrue(reasons.isEmpty(), "Should return 0 errors");
	}
}

package org.octri.authentication.server.security.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
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

	private User user;
	private static final String USERNAME = "foo";
	private static final String CURRENT_PASSWORD = "currentPassword";
	private static final String VALID_PASSWORD = "Abcdefg.";
	private static final String INVALID_PASSWORD_WITH_USERNAME = "Abcdefg." + USERNAME;

	@Before
	public void beforeEach() {
		user = new User(USERNAME, "Foo", "Bar", "OHSU", "foo@example.com");
		user.setPassword(passwordEncoder.encode(CURRENT_PASSWORD));
		Mockito.when(userService.save(user)).thenReturn(user);
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

}

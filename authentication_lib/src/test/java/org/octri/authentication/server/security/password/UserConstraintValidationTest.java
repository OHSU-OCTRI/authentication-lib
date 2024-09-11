package org.octri.authentication.server.security.password;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.octri.authentication.server.security.entity.AuthenticationMethod;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.validation.Emailable;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.groups.Default;

/**
 * Tests {@link User} constraint validations.<br>
 * <br>
 * Note, there isn't a "UserConstraintValidation" class - this class tests the
 * integration between {@link User} and {@link PasswordContraintValidator}.
 *
 * @author sams
 */
public class UserConstraintValidationTest {

	private static Validator validator;
	PasswordConstraintValidator passwordConstraintValidator = new PasswordConstraintValidator();

	private static String LAST_NAME_REQUIRED = "Last name is required";
	private static String INVALID_EMAIL = "Please provide a valid email address";
	private static String EMAIL_REQUIRED = "Email is required";
	private static String USERNAME_REQUIRED = "Username is required";
	private static String FIRSTNAME_REQUIRED = "First name is required";

	private User testUser;
	private List<String> messages;

	private static final List<String> VALID_PASSWORDS = Arrays.asList(
			"8chaRacs",
			"8cha.acs",
			"8cha$acs");

	private static final List<String> INVALID_PASSWORDS = Arrays.asList(
			"foo",
			"8characs42",
			"8characs",
			"manycharactersl0ngpassw0rdbutn0special0rcaps");

	private static final List<String> VALID_EMAILS = Arrays.asList(
			"foo@example.com",
			"foo.bar@example.com",
			"foo.bar.42@example.com",
			"foo.bar@www.example.com",
			"joe+foo@example.com",
			"12345@example.com",
			"joe_foo@example.com",
			"joe-foo@example.com");

	private static final List<String> INVALID_EMAILS = Arrays.asList(
			"foo",
			"example.com",
			"foo.example.com",
			"joe@");

	@BeforeAll
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@BeforeEach
	public void beforeEach() {
		testUser = new User();
		testUser.setUsername("foo");
		testUser.setFirstName("Foo");
		testUser.setLastName("Bar");
		testUser.setEmail("foo.bar@example.com");
		testUser.setPassword(null);
		testUser.setAuthenticationMethod(AuthenticationMethod.TABLE_BASED);
	}

	@Test
	public void testSaveWithNulls() {
		User user = new User();
		messages = getMessages(user, Emailable.class);
		assertTrue(messages.contains(USERNAME_REQUIRED), USERNAME_REQUIRED);
		assertTrue(messages.contains(EMAIL_REQUIRED), EMAIL_REQUIRED);
		assertTrue(messages.contains(FIRSTNAME_REQUIRED), FIRSTNAME_REQUIRED);
		assertTrue(messages.contains(LAST_NAME_REQUIRED), LAST_NAME_REQUIRED);
	}

	@Test
	public void testForValidPasswords() {
		for (String password : VALID_PASSWORDS) {
			assertTrue(passwordConstraintValidator.validate(password, null).isEmpty(),
					password + " should be valid");
		}
	}

	@Test
	public void testForInvalidPasswords() {
		for (String password : INVALID_PASSWORDS) {
			assertFalse(passwordConstraintValidator.validate(password, null).isEmpty(),
					password + " should not be valid");
		}
	}

	@Test
	public void testForValidEmail() {
		for (String email : VALID_EMAILS) {
			testUser.setEmail(email);
			assertValid(testUser, Emailable.class);
		}
	}

	@Test
	public void testForInvalidEmail() {
		for (String email : INVALID_EMAILS) {
			testUser.setEmail(email);
			assertInvalid(testUser, INVALID_EMAIL, Emailable.class);
		}
	}

	@Test
	public void testNoEmailMode() {
		for (String email : INVALID_EMAILS) {
			testUser.setEmail(email);
			assertValid(testUser, Default.class);
		}
	}

	/**
	 * Helper for ensuring a {@link User} passes validation. Throws an assertion
	 * error if there are validation errors.
	 *
	 * @param user
	 */
	public void assertValid(final User user, Class<?> validationGroup) {
		messages = getMessages(user, validationGroup);
		assertTrue(messages.size() == 0);
	}

	/**
	 * Helper for ensuring a {@link User} fails validation. Throws an assertion
	 * error if there are validation errors.
	 *
	 * @param user
	 */
	public void assertInvalid(final User user, final String expectedMessage, Class<?> validationGroup) {
		messages = getMessages(user, validationGroup);
		assertTrue(messages.size() > 0);
		assertTrue(messages.contains(expectedMessage));
	}

	/**
	 * Helper for retrieving {@link ConstraintViolation} messages.
	 *
	 * @param user
	 * @return List of constraint violation messages.
	 */
	public List<String> getMessages(User user, Class<?> validationGroup) {
		Set<ConstraintViolation<User>> violations = validator.validate(user, validationGroup);
		return violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
	}

}

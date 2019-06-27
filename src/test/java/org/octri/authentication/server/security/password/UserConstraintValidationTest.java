package org.octri.authentication.server.security.password;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.validation.Emailable;

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
	private static String INSTITUTION_REQUIRED = "Institution is required";
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
			"joe@example");

	@BeforeClass
	public static void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@Before
	public void beforeEach() {
		testUser = new User();
		testUser.setUsername("foo");
		testUser.setFirstName("Foo");
		testUser.setLastName("Bar");
		testUser.setInstitution("OHSU");
		testUser.setEmail("foo.bar@example.com");
		testUser.setPassword(null);
	}

	@Test
	public void testSaveWithNulls() {
		User user = new User();
		messages = getMessages(user, Emailable.class);
		assertTrue(USERNAME_REQUIRED, messages.contains(USERNAME_REQUIRED));
		assertTrue(EMAIL_REQUIRED, messages.contains(EMAIL_REQUIRED));
		assertTrue(FIRSTNAME_REQUIRED, messages.contains(FIRSTNAME_REQUIRED));
		assertTrue(LAST_NAME_REQUIRED, messages.contains(LAST_NAME_REQUIRED));
		assertTrue(INSTITUTION_REQUIRED, messages.contains(INSTITUTION_REQUIRED));
	}

	@Test
	public void testForValidPasswords() {
		for (String password : VALID_PASSWORDS) {
			assertTrue(password + " should be valid", passwordConstraintValidator.isValid(password, null));
		}
	}

	@Test
	public void testForInvalidPasswords() {
		for (String password : INVALID_PASSWORDS) {
			assertFalse(password + " should not be valid", passwordConstraintValidator.isValid(password, null));
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
	 * Helper for ensuring a {@link User} passes validation. Throws an assertion error if there are validation errors.
	 *
	 * @param user
	 */
	public void assertValid(final User user, Class validationGroup) {
		messages = getMessages(user, validationGroup);
		assertTrue(messages.size() == 0);
	}

	/**
	 * Helper for ensuring a {@link User} fails validation. Throws an assertion error if there are validation errors.
	 *
	 * @param user
	 */
	public void assertInvalid(final User user, final String expectedMessage, Class validationGroup) {
		messages = getMessages(user, validationGroup);
		assertTrue(messages.size() > 0);
		assertTrue(messages.contains(expectedMessage));
	}

	/**
	 * Helper for retrieving {@link ContraintViolation} messages.
	 *
	 * @param user
	 * @return List of contraint violation messages.
	 */
	public List<String> getMessages(User user, Class validationGroup) {
		Set<ConstraintViolation<User>> violations = validator.validate(user, validationGroup);
		return violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
	}

}

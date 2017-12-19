package org.octri.authentication.server.security.password;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.octri.authentication.server.security.entity.User;

public class PasswordConstraintValidatorTest {

	private static Validator validator;

	private static String LAST_NAME_REQUIRED = "Last name is required";
	private static String INSTITUTION_REQUIRED = "Institution is required";
	private static String INVALID_PASSWORD = "This password does not meet all of the requirements";
	private static String EMAIL_REQUIRED = "Email is required";
	private static String USERNAME_REQUIRED = "Username is required";
	private static String FIRSTNAME_REQUIRED = "First name is required";

	private User testUser;
	private List<String> messages;

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
		testUser.setPassword("foo42");
	}

	@Test
	public void testSaveWithNulls() {
		User user = new User();
		user.setPassword("foo42");
		messages = getMessages(user);
		assertTrue(USERNAME_REQUIRED, messages.contains(USERNAME_REQUIRED));
		assertTrue(EMAIL_REQUIRED, messages.contains(EMAIL_REQUIRED));
		assertTrue(FIRSTNAME_REQUIRED, messages.contains(FIRSTNAME_REQUIRED));
		assertTrue(LAST_NAME_REQUIRED, messages.contains(LAST_NAME_REQUIRED));
		assertTrue(INSTITUTION_REQUIRED, messages.contains(INSTITUTION_REQUIRED));
		assertTrue(INVALID_PASSWORD, messages.contains(INVALID_PASSWORD));
	}

	@Test
	public void testForInvalidPasswords() {
		testForInvalidPassword("foo");
		testForInvalidPassword("8characs42");
		testForInvalidPassword("8characs");
		testForInvalidPassword("manycharactersl0ngpassw0rdbutn0special0rcaps");
	}

	@Test
	public void testForValidPasswords() {
		testForValidPassword("8chaRacs");
		testForValidPassword("8cha.acs");
		testForValidPassword("8cha$acs");
	}

	public void testForInvalidPassword(String password) {
		testUser.setPassword(password);
		messages = getMessages(testUser);
		assertTrue(messages.size() == 1);
		assertTrue(messages.contains(INVALID_PASSWORD));
	}

	public void testForValidPassword(String password) {
		testUser.setPassword(password);
		messages = getMessages(testUser);
		assertTrue(messages.size() == 0);
	}

	public List<String> getMessages(User user) {
		Set<ConstraintViolation<User>> violations = validator.validate(user);
		return violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
	}

}

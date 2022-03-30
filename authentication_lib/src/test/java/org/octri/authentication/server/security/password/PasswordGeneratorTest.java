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

public class PasswordGeneratorTest {

	private static Validator validator;

	private User testUser;

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
	public void testGenerate() {
		String password = PasswordGenerator.generate();
		testUser.setPassword(password);

		Set<ConstraintViolation<User>> violations = validator.validate(testUser);
		List<String> messages = violations.stream().map(v -> v.getMessage()).collect(Collectors.toList());
		assertTrue(messages.size() == 0);
	}

}

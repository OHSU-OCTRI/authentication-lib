package org.octri.authentication.server.security.password;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.octri.authentication.server.security.entity.User;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

public class PasswordGeneratorTest {

	private static Validator validator;

	private User testUser;

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
		assertTrue(messages.size() == 0, "Generated passwords are valid");
	}

}

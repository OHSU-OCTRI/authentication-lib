package org.octri.authentication.server.security.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.repository.PasswordResetTokenRepository;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class PasswordResetTokenServiceTest {

	@InjectMocks
	private PasswordResetTokenService passwordResetTokenService;

	@Mock
	private UserService userService;

	@Mock
	private PasswordResetTokenRepository passwordResetTokenRepository;

	private User user;
	private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";
	private static final String EMAIL = "foo@example.com";

	@Before
	public void beforeEach() {
		user = new User("foo", "Foo", "Bar", "OHSU", "foo@example.com");
		when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
				.then(i -> i.getArgumentAt(0, PasswordResetToken.class));
		when(userService.save(any(User.class)))
				.then(i -> i.getArgumentAt(0, User.class));
	}

	@Test
	public void testGeneratePasswordResetToken() {
		when(userService.findByEmail(EMAIL)).thenReturn(user);
		PasswordResetToken token = passwordResetTokenService.generatePasswordResetToken(EMAIL);
		assertTrue("Token matches expected scheme", token.getToken().matches(UUID_REGEX));
		assertEquals("Linked with correct User", user, token.getUser());
		assertNotNull("There should be an expiration date", token.getExpiryDate());
		assertTrue("The expiration date should be far enough in the future for this test to pass",
				token.getExpiryDate().after(new Date()));
	}

	@Test
	public void testForValidPasswordResetToken() {
		Instant now = Instant.now();
		PasswordResetToken validToken = new PasswordResetToken();
		// Set a date far in the future for this test to pass.
		validToken.setExpiryDate(Date.from(now.plus(1000, ChronoUnit.MINUTES)));

		when(passwordResetTokenService.findByToken("secret-token")).thenReturn(validToken);
		assertTrue("Token must be valid", passwordResetTokenService.isValidPasswordResetToken("secret-token"));
	}

	@Test
	public void testForInvalidPasswordResetTokenWhenExpired() {
		Instant now = Instant.now();
		PasswordResetToken invalidToken = new PasswordResetToken();
		// Set a date far in the past for this test to pass.
		invalidToken.setExpiryDate(Date.from(now.minus(1000, ChronoUnit.MINUTES)));

		when(passwordResetTokenService.findByToken("secret-token")).thenReturn(invalidToken);
		assertFalse("Token must not be expired", passwordResetTokenService.isValidPasswordResetToken("secret-token"));
	}

	@Test
	public void testForInvalidPasswordResetTokenIfDoesNotExist() {
		when(passwordResetTokenService.findByToken("secret-token")).thenReturn(null);
		assertFalse("Token must exist in the database",
				passwordResetTokenService.isValidPasswordResetToken("secret-token"));
	}
}

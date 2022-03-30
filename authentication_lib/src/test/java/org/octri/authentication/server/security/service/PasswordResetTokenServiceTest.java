package org.octri.authentication.server.security.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
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

	@Before
	public void beforeEach() throws InvalidLdapUserDetailsException {
		user = new User("foo", "Foo", "Bar", "OHSU", "foo@example.com");
		when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
				.then(i -> (PasswordResetToken) i.getArgument(0));
	}

	@Test
	public void testGeneratePasswordResetToken() {
		PasswordResetToken token = passwordResetTokenService.generatePasswordResetToken(user);
		assertTrue("Token matches expected scheme", token.getToken().matches(UUID_REGEX));
		assertEquals("Linked with correct User", user, token.getUser());
		assertNotNull("There should be an expiration date", token.getExpiryDate());
		assertTrue("The expiration date should be far enough in the future for this test to pass",
				token.getExpiryDate().after(new Date()));
	}

	@Test
	public void testForValidPasswordResetToken() {
		PasswordResetToken validToken = new PasswordResetToken(user);
		when(passwordResetTokenService.findByToken("secret-token")).thenReturn(validToken);
		assertTrue("Token must be valid", passwordResetTokenService.isValidPasswordResetToken("secret-token"));
	}

	@Test
	public void testForInvalidPasswordResetTokenWhenExpired() {
		Instant now = Instant.now();
		PasswordResetToken invalidToken = new PasswordResetToken(user);
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

	@Test
	public void testExpireToken() throws InterruptedException {
		// 'now' is the time this test started
		Date now = new Date();

		PasswordResetToken token = new PasswordResetToken(user);
		assertTrue("Tokens are generated with an expiration date in the future - should be greater than 'now'",
				token.getExpiryDate().after(now));

		passwordResetTokenService.expireToken(token);
		assertTrue("A token is expired when the expiryDate is before 'now'", token.getExpiryDate().before(now));
	}
}

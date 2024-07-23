package org.octri.authentication.server.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.repository.PasswordResetTokenRepository;

@ExtendWith(MockitoExtension.class)
public class PasswordResetTokenServiceTest {

	@InjectMocks
	private PasswordResetTokenService passwordResetTokenService;

	@Mock
	private OctriAuthenticationProperties authenticationProperties;

	@Mock
	private UserService userService;

	@Mock
	private PasswordResetTokenRepository passwordResetTokenRepository;

	private User user;
	private static final String UUID_REGEX = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}";

	@BeforeEach
	public void beforeEach() throws InvalidLdapUserDetailsException {
		user = new User("foo", "Foo", "Bar", "OHSU", "foo@example.com");
	}

	@Test
	public void testGeneratePasswordResetToken() {
		var expectedDuration = Duration.ofMinutes(30);
		when(authenticationProperties.getPasswordTokenValidFor()).thenReturn(expectedDuration);
		when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
				.then(i -> (PasswordResetToken) i.getArgument(0));

		var token = passwordResetTokenService.generatePasswordResetToken(user);
		assertTrue(token.getToken().matches(UUID_REGEX), "Token matches expected scheme");
		assertEquals(user, token.getUser(), "Linked with correct User");
		assertNotNull(token.getExpiryDate(), "There should be an expiration date");

		var expectedExpiration = Instant.now().plus(expectedDuration);
		var tokenExpiration = token.getExpiryDate().toInstant();
		var difference = ChronoUnit.MINUTES.between(tokenExpiration, expectedExpiration);
		assertTrue(difference >= 0 && difference <= 1, "The token should be valid for the expected amount of time");
	}

	@Test
	public void testExpireToken() throws InterruptedException {
		// 'now' is the time this test started
		Date now = new Date();

		PasswordResetToken token = new PasswordResetToken(user);
		assertTrue(token.getExpiryDate().after(now),
				"Tokens are generated with an expiration date in the future - should be greater than 'now'");

		when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
				.then(i -> (PasswordResetToken) i.getArgument(0));

		passwordResetTokenService.expireToken(token);
		assertTrue(token.getExpiryDate().before(now), "A token is expired when the expiryDate is before 'now'");
	}
}

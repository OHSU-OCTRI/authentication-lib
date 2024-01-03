package org.octri.authentication.server.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.repository.PasswordResetTokenRepository;

@ExtendWith(MockitoExtension.class)
public class PasswordResetTokenServiceTest {

	@InjectMocks
	private PasswordResetTokenService passwordResetTokenService;

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
		when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
				.then(i -> (PasswordResetToken) i.getArgument(0));

		PasswordResetToken token = passwordResetTokenService.generatePasswordResetToken(user);
		assertTrue(token.getToken().matches(UUID_REGEX), "Token matches expected scheme");
		assertEquals(user, token.getUser(), "Linked with correct User");
		assertNotNull(token.getExpiryDate(), "There should be an expiration date");
		assertTrue(token.getExpiryDate().after(new Date()),
				"The expiration date should be far enough in the future for this test to pass");
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

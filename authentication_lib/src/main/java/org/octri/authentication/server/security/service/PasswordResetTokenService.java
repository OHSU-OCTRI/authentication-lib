package org.octri.authentication.server.security.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.AuthenticationUrlHelper;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;

/**
 * Service wrapper for the {@link PasswordResetTokenRepository}.
 *
 * @author sams
 */
@Service
public class PasswordResetTokenService {

	@Autowired
	private OctriAuthenticationProperties authenticationProperties;

	@Autowired
	private AuthenticationUrlHelper urlHelper;

	@Resource
	private PasswordResetTokenRepository passwordResetTokenRepository;

	/**
	 * Finds the reset token record with the given ID.
	 *
	 * @param id
	 *            the ID to search by
	 * @return the reset token wih the given ID
	 */
	@Transactional(readOnly = true)
	public PasswordResetToken find(Long id) {
		return passwordResetTokenRepository.findById(id).get();
	}

	/**
	 * Finds a reset token record with the given token string.
	 *
	 * @param token
	 *            the token string to search by
	 * @return the reset token with the given token string
	 */
	@Transactional(readOnly = true)
	public PasswordResetToken findByToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	/**
	 * Saves a reset token record to the database.
	 *
	 * @param passwordResetToken
	 *            a reset token object
	 * @return the persisted reset token
	 */
	@Transactional
	public PasswordResetToken save(PasswordResetToken passwordResetToken) {
		return passwordResetTokenRepository.save(passwordResetToken);
	}

	/**
	 * Gets all of the reset tokens.
	 *
	 * @return a list of all reset tokens
	 */
	@Transactional(readOnly = true)
	public List<PasswordResetToken> findAll() {
		return (List<PasswordResetToken>) passwordResetTokenRepository.findAll();
	}

	/**
	 * Deletes the reset token record with the given ID.
	 *
	 * @param id
	 *            ID of the record to delete
	 */
	@Transactional
	public void delete(Long id) {
		PasswordResetToken passwordResetToken = passwordResetTokenRepository.findById(id).orElse(null);
		if (passwordResetToken != null) {
			passwordResetTokenRepository.deleteById(id);
		}
	}

	/**
	 * Finds the reset token with the latest expiration date associated with the given user ID.
	 *
	 * @param userId
	 *            user ID to search by
	 * @return latest reset token associated with the user ID, empty if none exist
	 */
	public Optional<PasswordResetToken> findLatest(Long userId) {
		return passwordResetTokenRepository.findFirstByUserIdOrderByExpiryDateDesc(userId);
	}

	/**
	 * Expires token by setting an expiry date in the past.
	 *
	 * @param token
	 *            The token to expire.
	 * @return Persisted token
	 */
	@Transactional
	public PasswordResetToken expireToken(final PasswordResetToken token) {
		token.setExpiryDate(Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
		return save(token);
	}

	/**
	 * Generates a password reset token for a user. The token will be valid for the duration configured in
	 * {@link OctriAuthenticationProperties}. Tokens are persisted in the database.
	 *
	 * @param user
	 *            user account
	 * @return Returns a new {@link PasswordResetToken} for the given user.
	 */
	public PasswordResetToken generatePasswordResetToken(final User user) {
		Duration tokenDuration = authenticationProperties.getPasswordTokenValidFor();
		Assert.notNull(user, "User cannot be null");
		PasswordResetToken token = new PasswordResetToken();
		token.setUser(user);
		token.setExpiryDate(Date.from(Instant.now().plus(tokenDuration)));
		return save(token);
	}

	/**
	 * Generates a password reset token for a user that will be valid for the specified duration. Tokens are persisted
	 * in the database.
	 *
	 * @param user
	 *            user account
	 * @param tokenDuration
	 *            how long the token should be valid
	 * @return Returns a new {@link PasswordResetToken} for the given user.
	 */
	public PasswordResetToken generatePasswordResetToken(final User user, Duration tokenDuration) {
		Assert.notNull(user, "User cannot be null");
		PasswordResetToken token = new PasswordResetToken();
		token.setUser(user);
		token.setExpiryDate(Date.from(Instant.now().plus(tokenDuration)));
		return save(token);
	}

	/**
	 * Find all active tokens. The transient field 'tokenUrl' is set for use in mustache templates.
	 *
	 * @return list of active tokens, with populated token URLs
	 */
	public List<PasswordResetToken> findAllActiveTokens() {
		List<PasswordResetToken> tokens = passwordResetTokenRepository
				.findByExpiryDateGreaterThanOrderByExpiryDateDesc(new Date());
		tokens.forEach(token -> token.setTokenUrl(urlHelper.getPasswordResetUrl(token.getToken())));
		return tokens;
	}

}
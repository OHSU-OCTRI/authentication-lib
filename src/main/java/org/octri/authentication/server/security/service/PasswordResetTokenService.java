package org.octri.authentication.server.security.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Service wrapper for the {@link PasswordResetTokenRepository}.
 *
 * @author sams
 */
@Service
public class PasswordResetTokenService {

	@Autowired
	private UserService userService;

	@Resource
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Transactional(readOnly = true)
	public PasswordResetToken find(Long id) {
		return passwordResetTokenRepository.findById(id).get();
	}

	@Transactional(readOnly = true)
	public PasswordResetToken findByToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	@Transactional
	public PasswordResetToken save(PasswordResetToken passwordResetToken) {
		return passwordResetTokenRepository.save(passwordResetToken);
	}

	@Transactional(readOnly = true)
	public List<PasswordResetToken> findAll() {
		return (List<PasswordResetToken>) passwordResetTokenRepository.findAll();
	}

	@Transactional
	public void delete(Long id) {
		PasswordResetToken passwordResetToken = passwordResetTokenRepository.findById(id).orElse(null);
		if (passwordResetToken != null) {
			passwordResetTokenRepository.deleteById(id);
		}
	}

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
	 * Generates a password reset token for a user. Tokens are persisted in the database.
	 *
	 * @param email
	 * @return Returns a new {@link PasswordResetToken} for the given email address.
	 */
	public PasswordResetToken generatePasswordResetToken(final User user) {
		Assert.notNull(user, "User cannot be null");
		return save(new PasswordResetToken(user));
	}

	/**
	 * Generates a password reset token for a user. Tokens are persisted in the database.
	 *
	 * @param email
	 * @return Returns a new {@link PasswordResetToken} for the given email address.
	 */
	public PasswordResetToken generatePasswordResetToken(final User user, Integer expireInMinutes) {
		Assert.notNull(user, "User cannot be null");
		return save(new PasswordResetToken(user, expireInMinutes));
	}

	/**
	 * Checks to ensure a token exists and has not expired.
	 *
	 * @param token
	 * @return true if the password reset token is valid, false otherwise.
	 */
	public boolean isValidPasswordResetToken(final String token) {
		Assert.notNull(token, "Must provide a token");
		PasswordResetToken existing = findByToken(token);
		if (existing == null) {
			return false;
		}
		Date now = new Date();
		// Valid if: Now is not after the expiration date.
		return !now.after(existing.getExpiryDate());
	}

	/**
	 * Find all active tokens. The transient field 'tokenUrl' is set for use in mustache templates.
	 *
	 * @return
	 */
	public List<PasswordResetToken> findAllActiveTokens() {
		List<PasswordResetToken> tokens = passwordResetTokenRepository.findByExpiryDateGreaterThanOrderByExpiryDateDesc(new Date());
		tokens.forEach(token -> token.setTokenUrl(userService.buildResetPasswordUrl(token.getToken())));
		return tokens;
	}

}
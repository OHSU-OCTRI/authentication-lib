package org.octri.authentication.server.security.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
		return passwordResetTokenRepository.findOne(id);
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
		PasswordResetToken passwordResetToken = passwordResetTokenRepository.findOne(id);
		if (passwordResetToken != null) {
			passwordResetTokenRepository.delete(id);
		}
	}

	/**
	 * Generates a password reset token for a user. Tokens are persisted in the database.
	 * 
	 * @param email
	 * @return Returns a new {@link PasswordResetToken} for the given email address.
	 */
	public PasswordResetToken generatePasswordResetToken(final String email) {
		Assert.notNull(email, "Email address cannot be null");
		User user = userService.findByEmail(email);
		Assert.notNull(user, "Could not find user for password reset for email " + email);
		final String uuid = UUID.randomUUID().toString();
		Instant now = Instant.now();
		PasswordResetToken token = new PasswordResetToken();
		token.setToken(uuid);
		token.setExpiryDate(Date.from(now.plus(PasswordResetToken.EXPIRE_IN_MINUTES, ChronoUnit.MINUTES)));
		token.setUser(user);
		return save(token);
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

}

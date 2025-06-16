package org.octri.authentication.server.security.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * {@link JpaRepository} for manipulating {@link PasswordResetToken} entities.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	/**
	 * Finds a password reset token by its token string.
	 *
	 * @param token
	 *            token string to search by
	 * @return the record with the given token string, or null
	 */
	public PasswordResetToken findByToken(@Param("token") String token);

	/**
	 * Finds the reset token with the latest expiration date associated with the given user ID.
	 *
	 * @param userId
	 *            user ID to search by
	 * @return the latest reset token for the given user ID if there is one, empty otherwise
	 */
	public Optional<PasswordResetToken> findFirstByUserIdOrderByExpiryDateDesc(Long userId);

	/**
	 * Finds reset tokens that expire after the given date.
	 *
	 * @param expiryDate
	 *            the date tokens should expire after
	 * @return all tokens that expire after the given date, in descending order of expiration date
	 */
	public List<PasswordResetToken> findByExpiryDateGreaterThanOrderByExpiryDateDesc(Date expiryDate);
}
package org.octri.authentication.server.security.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.octri.authentication.server.security.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link JpaRepository} for manipulating {@link LoginAttempt} entities.
 *
 * @author yateam
 *
 */
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

	/**
	 * Finds the most recent successful login by the user with the given username.
	 *
	 * @param username
	 *            the username to check
	 * @return the user's most recent successful login, or null if the user has never logged in successfully
	 */
	public LoginAttempt findFirstByUsernameAndSuccessfulIsTrueOrderByAttemptedAtDesc(String username);

	/**
	 * Finds the most recent failed login of the given type.
	 *
	 * @param errorType
	 *            the type of login error desired
	 * @return the most recent login failure of the given type, or null if no login failure of the type has occurred
	 */
	public LoginAttempt findFirstByErrorTypeAndSuccessfulIsFalseOrderByAttemptedAtDesc(String errorType);

	/**
	 * Finds login attempts since the given timestamp, returned in reverse chronological order.
	 *
	 * @param sinceTimestamp
	 *            get login attempts since this timestamp
	 * @return login attempts since the given timestamp, sorted in reverse order by when the login was attempted
	 */
	public List<LoginAttempt> findByAttemptedAtGreaterThanOrderByAttemptedAtDesc(LocalDateTime sinceTimestamp);

	/**
	 * Finds login attempts for the given username since the provided timestamp, returned in reverse chronological
	 * order.
	 *
	 * @param username
	 *            username to search by (case insensitive)
	 * @param sinceTimestamp
	 *            get login attempts since this timestamp
	 * @return login attempts for the username since the given timestamp, sorted in revers chronological order.
	 */
	public List<LoginAttempt> findByUsernameIgnoreCaseAndAttemptedAtGreaterThanOrderByAttemptedAtDesc(String username,
			LocalDateTime sinceTimestamp);

}

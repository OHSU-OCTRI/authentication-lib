package org.octri.authentication.server.security.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.octri.authentication.server.security.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * {@link JpaRepository} for manipulating {@link LoginAttempt} entities.
 *
 * @author yateam
 *
 */
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

	public static final String FIND_BY_USERNAME_SINCE_QUERY = """
			SELECT la FROM LoginAttempt la
			WHERE LOWER(la.username) = LOWER(:username) AND la.attemptedAt > :sinceTimestamp
			ORDER BY attemptedAt DESC
			""";

	public static final String FIND_BY_USERNAME_OR_EMAIL_SINCE_QUERY = """
			SELECT la FROM LoginAttempt la
			WHERE (
			     LOWER(la.username) = LOWER(:username)
			     OR LOWER(la.username) = LOWER(:email)
			   )
			   AND la.attemptedAt > :sinceTimestamp
			ORDER BY attemptedAt DESC
			""";

	/**
	 * Finds the most recent login by the user with the given username and success status
	 *
	 * @param username
	 *            the username to check
	 * @param successful
	 *            the requirement of either successful or failed login
	 * @return the user's most recent login, or null if the no record matches the username and successful requirement
	 */
	public LoginAttempt findFirstByUsernameIgnoreCaseAndSuccessfulOrderByAttemptedAtDesc(String username,
			Boolean successful);

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

	/**
	 * Finds login attempts for the given username since the provided timestamp, returned in reverse chronological
	 * order.
	 *
	 * @param username
	 *            username to search by (case insensitive)
	 * @param sinceTimestamp
	 *            get login attempts since this timestamp
	 * @return login attempts for the username since the given timestamp, sorted in reverse chronological order
	 */
	@Query(FIND_BY_USERNAME_SINCE_QUERY)
	public List<LoginAttempt> findByUsernameSince(@Param("username") String username,
			@Param("sinceTimestamp") LocalDateTime sinceTimestamp);

	/**
	 * Finds login attempts for the given username or email address since the provided timestamp, returned in reverse
	 * chronological order.
	 *
	 * @param username
	 *            username to search by (case insensitive)
	 * @param email
	 *            email to search by (case insensitive)
	 * @param sinceTimestamp
	 *            get login attempts since this timestamp
	 * @return login matching the given username or email address since the given timestamp, sorted in reverse
	 *         chronological order
	 */
	@Query(FIND_BY_USERNAME_OR_EMAIL_SINCE_QUERY)
	public List<LoginAttempt> findByUsernameOrEmailSince(@Param("username") String username,
			@Param("email") String email, @Param("sinceTimestamp") LocalDateTime sinceTimestamp);

}

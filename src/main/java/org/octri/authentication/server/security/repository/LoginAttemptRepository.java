/**
 * 
 */
package org.octri.authentication.server.security.repository;

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
}

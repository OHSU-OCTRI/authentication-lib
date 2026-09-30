package org.octri.authentication.server.security.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.entity.LoginAttempt;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.repository.LoginAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * A service wrapper for the {@link LoginAttemptRepository}.
 *
 * @author yateam
 *
 */
@Service
public class LoginAttemptService {

	private final OctriAuthenticationProperties authenticationProperties;
	private final LoginAttemptRepository loginAttemptRepository;

	/**
	 * Constructor.
	 *
	 * @param loginAttemptRepository
	 *            login attempt repository
	 * @param authenticationProperties
	 *            library configuration properties
	 */
	public LoginAttemptService(LoginAttemptRepository loginAttemptRepository,
			OctriAuthenticationProperties authenticationProperties) {
		this.loginAttemptRepository = loginAttemptRepository;
		this.authenticationProperties = authenticationProperties;
	}

	/**
	 * Gets the login attempt with the given ID.
	 *
	 * @param id
	 *            the ID of the login attempt to get
	 * @return the login attempt with the given ID if it exists, otherwise null
	 */
	@Transactional(readOnly = true)
	public LoginAttempt find(Long id) {
		return loginAttemptRepository.findById(id).get();
	}

	/**
	 * Saves the given login attempt.
	 *
	 * @param loginAttempt
	 *            the login attempt to save
	 * @return the saved login attempt
	 */
	@Transactional
	public LoginAttempt save(LoginAttempt loginAttempt) {
		return loginAttemptRepository.save(loginAttempt);
	}

	/**
	 * Gets a list of all existing login attempts.
	 *
	 * @return a list of existing login attempts
	 */
	@Transactional(readOnly = true)
	public List<LoginAttempt> findAll() {
		return (List<LoginAttempt>) loginAttemptRepository.findAll();
	}

	/**
	 * Deletes the login attempt with the given ID.
	 *
	 * @param id
	 *            the ID of the login attempt to delete
	 */
	@Transactional
	public void delete(Long id) {
		loginAttemptRepository.deleteById(id);
	}

	/**
	 * Deletes all existing login attempts.
	 */
	@Transactional
	public void deleteAll() {
		List<LoginAttempt> allAttempts = loginAttemptRepository.findAll();
		for (LoginAttempt attempt : allAttempts) {
			loginAttemptRepository.delete(attempt);
		}
	}

	/**
	 * Finds the most recent successful login attempt for the given username.
	 *
	 * @param username
	 *            the username to search for
	 * @return the most recent successful login attempt for the username, or null if they have never logged in
	 *         successfully
	 */
	@Transactional(readOnly = true)
	public LoginAttempt findLastSuccess(String username) {
		return loginAttemptRepository.findFirstByUsernameIgnoreCaseAndSuccessfulOrderByAttemptedAtDesc(username, true);
	}

	/**
	 * Finds the most recent failed login attempt for the given username.
	 *
	 * @param username
	 *            the username to search for
	 * @return the most recent failed login attempt for the username, or null if they have never failed to log in
	 */
	@Transactional(readOnly = true)
	public LoginAttempt findLastFailure(String username) {
		return loginAttemptRepository.findFirstByUsernameIgnoreCaseAndSuccessfulOrderByAttemptedAtDesc(username, false);
	}

	/**
	 * Finds the most recent failed login attempt for the given user.
	 *
	 * @param user
	 *            the user to search for
	 * @return the most recent failed login attempt for the user, or null if they have never failed to log in
	 */
	@Transactional(readOnly = true)
	public LoginAttempt findLastFailure(User user) {
		if (authenticationProperties.getEnableLoginByEmail()) {
			var byUsername = loginAttemptRepository
					.findFirstByUsernameIgnoreCaseAndSuccessfulOrderByAttemptedAtDesc(user.getUsername(), false);
			var byEmail = loginAttemptRepository.findFirstByUsernameIgnoreCaseAndSuccessfulOrderByAttemptedAtDesc(
					user.getEmail(),
					false);
			var optFailure = Arrays.asList(byUsername, byEmail)
					.stream()
					.filter(Objects::nonNull)
					.sorted(Comparator.comparing(LoginAttempt::getAttemptedAt).reversed())
					.findFirst();
			return optFailure.orElse(null);
		} else {
			return loginAttemptRepository.findFirstByUsernameIgnoreCaseAndSuccessfulOrderByAttemptedAtDesc(
					user.getUsername(),
					false);
		}
	}

	/**
	 * Finds the most recent login attempt with the given error type (e.g. "Bad credentials", "User is disabled", etc.).
	 *
	 * @param errorType
	 *            the text of the error type to find
	 * @return the most recent error of the given type, or null if it has never occurred
	 */
	@Transactional(readOnly = true)
	public LoginAttempt findLatestByError(String errorType) {
		return loginAttemptRepository.findFirstByErrorTypeAndSuccessfulIsFalseOrderByAttemptedAtDesc(errorType);
	}

	/**
	 * Finds login attempts since the given timestamp, returned in reverse chronological order.
	 *
	 * @param sinceTimestamp
	 *            find login attempts since this timestamp
	 * @return login attempts since the given timestamp, sorted in reverse order by when the login was attempted
	 */
	public List<LoginAttempt> findLoginAttemptsSince(LocalDateTime sinceTimestamp) {
		return loginAttemptRepository.findByAttemptedAtGreaterThanOrderByAttemptedAtDesc(sinceTimestamp);
	}

	/**
	 * Finds login attempts for the provided username since the given timestamp, returned in reverse chronological
	 * order.
	 *
	 * @param username
	 *            username to search by (case insensitive)
	 * @param sinceTimestamp
	 *            find login attempts since this timestamp
	 * @return login attempts for the username since the given timestamp, sorted in reverse chronological order.
	 */
	public List<LoginAttempt> findLoginAttemptsForUsernameSince(String username, LocalDateTime sinceTimestamp) {
		return loginAttemptRepository.findByUsernameSince(username, sinceTimestamp);
	}

	/**
	 * Finds login attempts for the provided user since the given timestamp, returned in reverse chronological order. If
	 * login by email address is enabled, searches by username or email address. Otherwise only login attempts matching
	 * the user's username are returned.
	 *
	 * @param user
	 *            user entity
	 * @param sinceTimestamp
	 *            find login attempts since this timestamp
	 * @return login attempts for the user since the given timestamp, sorted in reverse chronological order
	 */
	public List<LoginAttempt> findLoginAttemptsForUserSince(User user, LocalDateTime sinceTimestamp) {
		if (authenticationProperties.getEnableLoginByEmail()) {
			return loginAttemptRepository.findByUsernameOrEmailSince(user.getUsername(), user.getEmail(),
					sinceTimestamp);
		} else {
			return loginAttemptRepository.findByUsernameSince(user.getUsername(), sinceTimestamp);
		}
	}

}

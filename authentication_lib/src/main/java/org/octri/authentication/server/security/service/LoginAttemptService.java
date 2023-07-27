package org.octri.authentication.server.security.service;

import java.util.List;

import org.octri.authentication.server.security.entity.LoginAttempt;
import org.octri.authentication.server.security.repository.LoginAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * A service wrapper for the {@link LoginAttemptRepository}.
 *
 * @author yateam
 *
 */
@Service
public class LoginAttemptService {

	@Resource
	LoginAttemptRepository loginAttemptRepository;

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
		return loginAttemptRepository.findFirstByUsernameAndSuccessfulIsTrueOrderByAttemptedAtDesc(username);
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

}

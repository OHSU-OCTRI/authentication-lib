/**
 * 
 */
package org.octri.authentication.server.security.service;

import java.util.List;

import javax.annotation.Resource;

import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * A service wrapper for the {@link UserRepository}.
 * 
 * @author yateam
 *
 */
@Service
public class UserService {

	//TODO: Document and make optional with default
	@Value("${octri.authentication.max-login-attempts}")
	private int maxLoginAttempts;

	@Resource
	private UserRepository userRepository;

	/**
	 * Gets the maximum number of failed login attempts allowed before the user's account will be locked.
	 *
	 * @return the maximum number of attempts allowed
	 */
	public int getMaxLoginAttempts() {
		return maxLoginAttempts;
	}

	/**
	 * Sets the maximum number of failed login attempts allowed before the user's account will be locked.
	 *
	 * @param maxLoginAttempts
	 *            the number of attempts to allow
	 */
	public void setMaxLoginAttempts(int maxLoginAttempts) {
		this.maxLoginAttempts = maxLoginAttempts;
	}

	/**
	 * Get the user account with the given ID.
	 *
	 * @param id
	 *            the ID of the user to find
	 * @return the user with the given ID if it exists, otherwise null
	 */
	@Transactional(readOnly = true)
	public User find(Long id) {
		return userRepository.findOne(id);
	}

	/**
	 * Get the user account with the given username.
	 *
	 * @param username
	 *            the username of the user to find
	 * @return the user with the given username if it exists, otherwise null
	 */
	@Transactional(readOnly = true)
	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	/**
	 * Saves the given user account to the database.
	 *
	 * @param user
	 *            the user model to save
	 * @return the saved user model
	 */
	@Transactional
	public User save(User user) {
		return userRepository.save(user);
	}

	/**
	 * Gets a list of all known user accounts.
	 *
	 * @return a list of user models
	 */
	@Transactional(readOnly = true)
	public List<User> findAll() {
		return (List<User>) userRepository.findAll();
	}

	/**
	 * Deletes the user account with the given ID.
	 *
	 * @param id
	 *            the ID of the user model to delete
	 */
	@Transactional
	public void delete(Long id) {
		User user = userRepository.findOne(id);
		if (user != null) {
			userRepository.delete(id);
		}
	}

	/**
	 * Increments the number of failed login attempts for the user account with the given username.
	 *
	 * @param username
	 *            the username of the account that will have its failed login attempts incremented
	 * @return the user account, with increased failed login count
	 */
	@Transactional
	public User incrementFailedAttempts(String username) {
		Assert.hasText(username, "Username is required");
		User user = userRepository.findByUsername(username);
		Assert.notNull(user, "User " + username + " not found when attempting to increment failed attempts");

		return incrementFailedAttempts(user);
	}

	/**
	 * Increments the number of failed login attempts for the specified user account.
	 *
	 * @param user
	 *            the user account that should have its failed login attempts incremented
	 * @return the user account, with increased failed login count
	 */
	@Transactional
	public User incrementFailedAttempts(User user) {
		Assert.notNull(user, "User may not be null");
		user.setConsecutiveLoginFailures(user.getConsecutiveLoginFailures() + 1);
		if (user.getConsecutiveLoginFailures() >= this.maxLoginAttempts) {
			user.setAccountLocked(true);
		}

		return userRepository.save(user);
	}

}

package org.octri.authentication.server.security.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.DuplicateEmailException;
import org.octri.authentication.server.security.exception.UserManagementException;
import org.octri.authentication.server.security.password.Messages;
import org.octri.authentication.server.security.password.PasswordConstraintValidator;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

	@Autowired
	private OctriAuthenticationProperties authenticationProperties;

	@Autowired
	private UserRepository userRepository;

	@Autowired(required = false)
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	/**
	 * Get the user account with the given ID.
	 *
	 * @param id
	 *            the ID of the user to find
	 * @return the user with the given ID if it exists, otherwise null
	 */
	@Transactional(readOnly = true)
	public User find(Long id) {
		return userRepository.findById(id).get();
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
	 * Get the user account with the given email address.
	 *
	 * @param email
	 *            the email of the user to find
	 * @return the user with the given email address if it exists, otherwise null
	 */
	@Transactional(readOnly = true)
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	/**
	 * Saves the given user account to the database. Handles logic for throwing an exception if the email already exists
	 * Also handles not expiring passwords when a password changes.
	 *
	 * This method is not Transactional. The checked exceptions don't work properly within a single transaction.
	 *
	 * @param user
	 *            the user model to save
	 * @return the saved user model
	 * @throws UserManagementException
	 *             if an error occurs when saving the user
	 */
	public User save(User user) throws UserManagementException {
		Assert.notNull(user, "Must provide a user");

		final boolean newUser = user.getId() == null;

		// Some applications make email optional, but if it exists it can't match another in the database.
		if (StringUtils.isNotBlank(user.getEmail())) {
			User existing = findByEmail(user.getEmail());
			if (existing != null && (newUser || !existing.getId().equals(user.getId()))) {
				throw new DuplicateEmailException();
			}
		}

		// Don't clobber existing passwords when editing a user.
		if (!newUser) {
			User existing = find(user.getId());
			if (existing.getPassword() != null) {
				user.setPassword(existing.getPassword());
			}
		}

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
		User user = userRepository.findById(id).orElse(null);
		if (user != null) {
			userRepository.deleteById(id);
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
		if (user.getConsecutiveLoginFailures() >= authenticationProperties.getMaxLoginAttempts()) {
			user.setAccountLocked(true);
		}

		return userRepository.save(user);
	}

	/**
	 * Saves user with newPassword and updates {@link User#credentialsExpirationDate}. If validation fails,
	 * the User is returned paired with a list of errors.
	 *
	 * @param user
	 *            user account
	 * @param currentPassword
	 *            the user's current password
	 * @param newPassword
	 *            the user's new password
	 * @param confirmPassword
	 *            password confirmation; must match newPassword
	 * @return Updated user
	 * @throws UserManagementException
	 *             if there is an error saving the user
	 */
	public ImmutablePair<User, List<String>> changePassword(final User user, final String currentPassword,
			final String newPassword, final String confirmPassword)
			throws UserManagementException {
		List<String> reasons = validatePassword(user, currentPassword, newPassword, confirmPassword);
		if (!reasons.isEmpty()) {
			return ImmutablePair.of(user, reasons);
		}

		user.setPassword(passwordEncoder.encode(newPassword));
		resetCredentialMetadata(user);
		User saved = this.save(user);

		// If a user has successfully changed their password reset tokens are no longer needed.
		Optional<PasswordResetToken> existingToken = passwordResetTokenService.findLatest(user.getId());
		if (existingToken.isPresent()) {
			passwordResetTokenService.expireToken(existingToken.get());
		}

		return ImmutablePair.of(saved, new ArrayList<String>());
	}

	/**
	 * Set the password for the given user to an encoded value.
	 *
	 * @param user
	 *            user account
	 * @param newPassword
	 *            the password to encode
	 */
	public void setEncodedPassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
	}

	/**
	 * Validates a password using the {@link PasswordConstraintValidator} as well as some other checks.
	 *
	 * @param user
	 *            user account
	 * @param currentPassword
	 *            null or the current password. If the currentPassword is null, this indicates the user is resetting
	 *            their password and this validation step should be skipped.
	 * @param newPassword
	 *            The new password.
	 * @param confirmPassword
	 *            Confirm the new password.
	 * @return List of error messages, or an empty list if the password passes validation.
	 */
	public List<String> validatePassword(final User user, final String currentPassword, final String newPassword,
			final String confirmPassword) {
		List<String> reasons = new ArrayList<>();

		// Current password must match existing password in the database if set.
		if (currentPassword != null && !passwordEncoder.matches(currentPassword, user.getPassword())) {
			reasons.add(Messages.CURRENT_PASSWORD_INCORRECT);
		}

		// New password must equal password confirmation
		if (!newPassword.equals(confirmPassword)) {
			reasons.add(Messages.NEW_AND_CONFIRM_PASSWORDS_MISMATCH);
		}

		// Rule: Prevents a password from containing username.
		// TODO: Look into validating with http://www.passay.org/javadocs/org/passay/UsernameRule.html
		if (newPassword.contains(user.getUsername())) {
			reasons.add(Messages.PASSWORDS_MUST_NOT_INCLUDE_USERNAME);
		}

		// Rule: Prevents using a previous password.
		// TODO: Look into validating with http://www.passay.org/javadocs/org/passay/DigestHistoryRule.html
		if (passwordEncoder.matches(newPassword, user.getPassword())) {
			reasons.add(Messages.MUST_NOT_USE_CURRENT_PASSWORD);
		}

		// Manually validate the password instead of using the @ValidPassword annotation.
		// This will allow us to set a null password in order to distinguish LDAP users.
		PasswordConstraintValidator validator = new PasswordConstraintValidator();
		reasons.addAll(validator.validate(newPassword, null));

		return reasons;
	}

	/**
	 * Update a user's password per reset request.
	 *
	 * @param newPassword
	 *            the user's new password
	 * @param confirmPassword
	 *            the password confirmation, must match newPassword
	 * @param token
	 *            single-use password reset token value
	 * @throws UserManagementException
	 *             if there is an error saving the user
	 * @return ImmutablePair with the first entry the saved User and the second a list of validation error messages.
	 */
	public ImmutablePair<User, List<String>> resetPassword(final String newPassword,
			final String confirmPassword, final String token)
			throws UserManagementException {
		Assert.notNull(newPassword, "Password is required");
		Assert.notNull(confirmPassword, "Password confirmation is required");
		Assert.notNull(token, "Password reset token is required");
		PasswordResetToken existingToken = passwordResetTokenService.findByToken(token);
		Assert.notNull(existingToken, "Could not find existing token");
		User user = existingToken.getUser();

		List<String> reasons = validatePassword(user, null, newPassword, confirmPassword);
		if (!reasons.isEmpty()) {
			return ImmutablePair.of(user, reasons);
		}

		user.setPassword(passwordEncoder.encode(newPassword));

		resetCredentialMetadata(user);

		User saved = this.save(user);
		passwordResetTokenService.expireToken(existingToken);

		return ImmutablePair.of(saved, new ArrayList<String>());
	}

	/**
	 * Resets credential expiration and consecutive login failures.
	 *
	 * @param user
	 *            account to reset
	 */
	private void resetCredentialMetadata(User user) {
		Integer credentialsExpirationPeriod = authenticationProperties.getCredentialsExpirationPeriod();
		Instant now = Instant.now();
		user.setCredentialsExpirationDate(Date.from(now.plus(credentialsExpirationPeriod, ChronoUnit.DAYS)));
		user.setConsecutiveLoginFailures(0);
	}

	/**
	 * Saves user with newPassword and updates {@link User#credentialsExpirationDate}. If validation fails,
	 * the User is returned paired with a list of errors.
	 *
	 * @param user
	 *            user account
	 * @param currentPassword
	 *            the user's current password
	 * @param newPassword
	 *            the user's new password
	 * @param confirmPassword
	 *            password confirmation; must match newPassword
	 * @param map
	 *            ignored
	 * @return the user account, with a list of errors if the password failed validation
	 * @throws UserManagementException
	 *             if there is an error saving the user
	 */
	public ImmutablePair<User, List<String>> changePassword(User user, String currentPassword, String newPassword,
			String confirmPassword, Map<String, String[]> map)
			throws UserManagementException {
		return this.changePassword(user, currentPassword, newPassword, confirmPassword);
	}

	/**
	 * Updates a user's password via reset request.
	 *
	 * @param user
	 *            user account
	 * @param newPassword
	 *            the user's new password
	 * @param confirmPassword
	 *            password confirmation; must match newPassword
	 * @param token
	 *            single-use password reset token value
	 * @param parameterMap
	 *            ignored
	 * @return the user account, with a list of errors if the password failed validation
	 * @throws UserManagementException
	 *             if there is an error saving the user
	 */
	public ImmutablePair<User, List<String>> resetPassword(User user, String newPassword, String confirmPassword,
			String token, Map<String, String[]> parameterMap)
			throws UserManagementException {
		return this.resetPassword(newPassword, confirmPassword, token);
	}

	/**
	 * Determines whether the user can reset their password. They must be table based and not be disabled in any way.
	 *
	 * @param user
	 *            the user to test
	 * @return true if the user can reset their password
	 */
	public boolean canResetPassword(User user) {
		return user.isTableBasedUser() && user.getEnabled() && !user.getAccountLocked() && !user.getAccountExpired();
	}

}

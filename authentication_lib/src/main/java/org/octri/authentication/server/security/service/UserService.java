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
import org.octri.authentication.config.LdapContextProperties;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.SecurityHelper;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.DuplicateEmailException;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.exception.UserDirectorySearchException;
import org.octri.authentication.server.security.exception.UserManagementException;
import org.octri.authentication.server.security.password.Messages;
import org.octri.authentication.server.security.password.PasswordConstraintValidator;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
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

	@Autowired(required = false)
	private LdapContextProperties ldapContextProperties;

	@Autowired(required = false)
	private FilterBasedLdapUserSearch ldapSearch;

	@Autowired
	private Boolean tableBasedEnabled;

	@Autowired
	private Boolean ldapEnabled;

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
	 * or if the
	 * LDAP user doesn't match the form input. Also handles not expiring passwords when a password changes.
	 *
	 * This method is not Transactional. The checked exceptions don't work properly within a single transaction.
	 *
	 * @param user
	 *            the user model to save
	 * @return the saved user model
	 * @throws UserManagementException
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

		if (!tableBasedEnabled) {
			try {
				DirContextOperations ldapUser = ldapSearch.searchForUser(user.getUsername());
				final String ldapEmail = ldapUser.getStringAttribute("mail");
				final boolean emailsMatch = ldapEmail.equalsIgnoreCase(user.getEmail());
				if (!emailsMatch) {
					throw new InvalidLdapUserDetailsException(
							InvalidLdapUserDetailsException.INVALID_USER_DETAILS_MESSAGE);
				}
			} catch (UsernameNotFoundException ex) {
				throw new UserDirectorySearchException(ex);
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
	 * @param currentPassword
	 * @param newPassword
	 * @param confirmPassword
	 * @return Updated user
	 * @throws UserManagementException
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
	 * @param newPassword
	 */
	public void setEncodedPassword(User user, String newPassword) {
		user.setPassword(passwordEncoder.encode(newPassword));
	}

	/**
	 * Validates a password using the {@link PasswordConstraintValidator} as well as some other checks.
	 *
	 * @param user
	 * @param currentPassword
	 *            null or the current password. If the currentPassword is null, this indicates the user is resetting
	 *            their password and this validation step should be skipped.
	 * @param newPassword
	 *            The new pasword.
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
	 * @param confirmPassword
	 * @param token
	 * @throws InvalidPasswordException
	 * @throws UserManagementException
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
	 */
	private void resetCredentialMetadata(User user) {
		Integer credentialsExpirationPeriod = authenticationProperties.getCredentialsExpirationPeriod();
		Instant now = Instant.now();
		user.setCredentialsExpirationDate(Date.from(now.plus(credentialsExpirationPeriod, ChronoUnit.DAYS)));
		user.setConsecutiveLoginFailures(0);
	}

	public void setTableBasedEnabled(Boolean tableBasedEnabled) {
		this.tableBasedEnabled = tableBasedEnabled;
	}

	public ImmutablePair<User, List<String>> changePassword(User user, String currentPassword, String newPassword,
			String confirmPassword, Map<String, String[]> map)
			throws UserManagementException {
		return this.changePassword(user, currentPassword, newPassword, confirmPassword);
	}

	public ImmutablePair<User, List<String>> resetPassword(User user, String newPassword, String confirmPassword,
			String token, Map<String, String[]> parameterMap)
			throws UserManagementException {
		return this.resetPassword(newPassword, confirmPassword, token);
	}

	/**
	 * Determines whether or not the user is an LDAP user. This is based off of the email address domain. If the user's
	 * email address domain matches the domain configured in the LDAP properties, then the user is an LDAP user.
	 *
	 * TODO: Consider adding a persisted flag on the `user` record. AUTHLIB-73
	 *
	 * @return true if the user is an LDAP user.
	 */
	public boolean isLdapUser(User user) {
		return ldapEnabled && SecurityHelper.hasEmailDomain(user, ldapContextProperties.getEmailDomain());
	}

	/**
	 * Determines whether the user can reset a password. They must be table based and not be disabled in any way.
	 *
	 * @param user
	 * @return
	 */
	public boolean canResetPassword(User user) {
		return !isLdapUser(user) && user.getEnabled() && !user.getAccountLocked() && !user.getAccountExpired();
	}

}

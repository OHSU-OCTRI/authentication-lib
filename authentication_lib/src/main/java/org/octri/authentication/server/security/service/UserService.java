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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.EmailConfiguration;
import org.octri.authentication.config.LdapContextProperties;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.AuthenticationUrlHelper;
import org.octri.authentication.server.security.SecurityHelper;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.DuplicateEmailException;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.password.Messages;
import org.octri.authentication.server.security.password.PasswordConstraintValidator;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;

/**
 * A service wrapper for the {@link UserRepository}.
 *
 * @author yateam
 *
 */
@Service
public class UserService {

	private static final Log log = LogFactory.getLog(UserService.class);

	@Value("${app.displayName}")
	private String displayName;

	@Autowired
	private OctriAuthenticationProperties authenticationProperties;

	@Autowired
	private AuthenticationUrlHelper urlHelper;

	@Autowired
	private UserRepository userRepository;

	@Autowired(required = false)
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private EmailConfiguration emailConfig;

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
	 * @throws InvalidLdapUserDetailsException
	 * @throws DuplicateEmailException
	 */
	public User save(User user) throws InvalidLdapUserDetailsException, DuplicateEmailException {
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
			DirContextOperations ldapUser = ldapSearch.searchForUser(user.getUsername());
			final String ldapEmail = ldapUser.getStringAttribute("mail");
			final boolean emailsMatch = ldapEmail.equalsIgnoreCase(user.getEmail());
			if (!emailsMatch) {
				throw new InvalidLdapUserDetailsException(InvalidLdapUserDetailsException.INVALID_USER_DETAILS_MESSAGE);
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
	 * @throws InvalidLdapUserDetailsException
	 * @throws DuplicateEmailException
	 */
	public ImmutablePair<User, List<String>> changePassword(final User user, final String currentPassword,
			final String newPassword, final String confirmPassword)
			throws InvalidLdapUserDetailsException, DuplicateEmailException {
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
	 * Send email confirmation to user. If the user is new, a welcome email is sent. Otherwise a password
	 * reset is sent.
	 *
	 * @param token
	 *            the password reset token
	 * @param request
	 *            the request so the application url can be constructed
	 * @param isNewUser
	 *            whether the user is new and should receive a welcome email instead of a password reset
	 * @param dryRun
	 *            Will only print email contents to console if true
	 */
	public void sendPasswordResetTokenEmail(final PasswordResetToken token, final HttpServletRequest request,
			final boolean isNewUser, final boolean dryRun) {

		User user = token.getUser();
		final String resetPath = urlHelper.getPasswordResetUrl(token.getToken());

		SimpleMailMessage email = new SimpleMailMessage();
		String body;
		if (isNewUser) {
			email.setSubject("Welcome to " + displayName);
			body = "Hello " + user.getFirstName() + ",\n\nAn account has been created for you with username "
					+ user.getUsername() + ". To set your password, please follow this link: " + resetPath;

		} else {
			email.setSubject("Password reset request for " + displayName);
			body = "Hello " + user.getFirstName() + ",\n\nTo reset your password please follow this link: "
					+ resetPath + "\n\nIf you did not initiate this request, please contact your system administrator.";
		}
		email.setText(body);
		email.setTo(user.getEmail());
		email.setFrom(emailConfig.getFrom());
		if (emailDryRun(dryRun, user.getEmail())) {
			logDryRunEmail(email);
		} else {
			mailSender.send(email);
			log.info("Password reset confirmation email sent to " + user.getEmail());
		}
	}

	/**
	 * Update a user's password per reset request.
	 *
	 * @param password
	 * @param token
	 * @param sendEmail
	 * @throws InvalidPasswordException
	 * @throws InvalidLdapUserDetailsException
	 * @throws DuplicateEmailException
	 * @return ImmutablePair with the first entry the saved User and the second a list of validation error messages.
	 */
	public ImmutablePair<User, List<String>> resetPassword(final String newPassword,
			final String confirmPassword, final String token)
			throws InvalidLdapUserDetailsException, DuplicateEmailException {
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
		user.setCredentialsExpired(false);
		Instant now = Instant.now();
		user.setCredentialsExpirationDate(Date.from(now.plus(credentialsExpirationPeriod, ChronoUnit.DAYS)));
		user.setConsecutiveLoginFailures(0);
	}

	/**
	 * Send email confirmation to user.
	 *
	 * @param user
	 * @param request
	 * @param dryRun
	 *            Will only print email contents to console if true
	 */
	public void sendPasswordResetEmailConfirmation(final String token, final HttpServletRequest request,
			final boolean dryRun) {
		Assert.notNull(token, "Must provide a token");
		Assert.notNull(request, "Must provide an HttpServletRequest");

		PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
		Assert.notNull(passwordResetToken, "Could not find a user for the provided token");

		final String userEmail = passwordResetToken.getUser().getEmail();

		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject("Your " + displayName + " password was reset");
		final String body = "Your password has been reset. You may now log into the application.\n\nUsername: "
				+ passwordResetToken.getUser().getUsername()
				+ "\nLink: " + urlHelper.getLoginUrl();
		email.setText(body);
		email.setTo(userEmail);
		email.setFrom(emailConfig.getFrom());

		if (emailDryRun(dryRun, userEmail)) {
			logDryRunEmail(email);
		} else {
			mailSender.send(email);
			log.info("Password reset confirmation email sent to " + userEmail);
		}
	}

	private boolean emailDryRun(final boolean dryRun, final String toEmail) {
		return dryRun || StringUtils.isBlank(toEmail);
	}

	private void logDryRunEmail(SimpleMailMessage message) {
		final String format = "DRY RUN, would have sent email to %s from %s with subject \"%s\" and contents \"%s\"";
		log.info(String.format(format, String.join(", ", message.getTo()), message.getFrom(), message.getSubject(),
				message.getText()));
	}

	public void setTableBasedEnabled(Boolean tableBasedEnabled) {
		this.tableBasedEnabled = tableBasedEnabled;
	}

	public ImmutablePair<User, List<String>> changePassword(User user, String currentPassword, String newPassword,
			String confirmPassword, Map<String, String[]> map)
			throws InvalidLdapUserDetailsException, DuplicateEmailException {
		return this.changePassword(user, currentPassword, newPassword, confirmPassword);
	}

	public ImmutablePair<User, List<String>> resetPassword(User user, String newPassword, String confirmPassword,
			String token, Map<String, String[]> parameterMap)
			throws InvalidLdapUserDetailsException, DuplicateEmailException {
		return this.resetPassword(newPassword, confirmPassword, token);
	}

	/**
	 * Send a notification to the original/current email address letting the user know their email address has been
	 * changed.
	 *
	 * NOTE: This method is deprecated and will be removed in AuthLib 2.0.0. Applications should send their own
	 * notifications for use cases that aren't fully supported by this library.
	 *
	 * @param user
	 * @param currentEmail
	 * @deprecated
	 */
	@Deprecated(forRemoval = true, since = "1.3.0")
	public void sendNotificationEmail(final User user, final String currentEmail) {
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject("Your " + displayName + " email was changed");
		final String body = "Hello " + user.getFirstName()
				+ ",\n\nWe are writing to let you know that your email address was changed on the " + displayName
				+ " site. If this was you, no action is needed.\n\nIf this was not you please contact your system administrator.";
		email.setText(body);
		email.setTo(currentEmail);
		email.setFrom(emailConfig.getFrom());
		mailSender.send(email);
		log.info("Email changed for user id " + user.getId() + ". Email confirmation sent.");
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

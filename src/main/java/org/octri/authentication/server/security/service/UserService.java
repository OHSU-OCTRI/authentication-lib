package org.octri.authentication.server.security.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.EmailConfiguration;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.password.PasswordConstraintValidator;
import org.octri.authentication.server.security.repository.UserRepository;
import org.octri.authentication.utils.ProfileUtils;
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

/**
 * A service wrapper for the {@link UserRepository}.
 *
 * @author yateam
 *
 */
@Service
public class UserService {

	private static final Log log = LogFactory.getLog(UserService.class);

	@Value("${server.servlet.context-path:/}")
	private String contextPath;

	@Value("${octri.authentication.base-url}")
	private String baseUrl;

	@Value("${octri.authentication.max-login-attempts:7}")
	private int maxLoginAttempts;

	@Value("${octri.authentication.credentials-expiration-period:180}")
	private int credentialsExpirationPeriod;

	@Resource
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private EmailConfiguration emailConfig;

	@Autowired(required = false)
	private FilterBasedLdapUserSearch ldapSearch;

	@Autowired
	private Boolean tableBasedEnabled;

	@Value("${app.displayName}")
	private String displayName;

	@Autowired
	private ProfileUtils profileUtils;

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
	 * Saves the given user account to the database. Handles logic for throwing an exception if the LDAP user doesn't
	 * match the form input. Also handles not expiring passwords when a password changes.
	 *
	 * @param user
	 *            the user model to save
	 * @return the saved user model
	 * @throws InvalidLdapUserDetailsException
	 */
	@Transactional
	public User save(User user) throws InvalidLdapUserDetailsException {
		Assert.notNull(user, "Must provide a user");
		if (!tableBasedEnabled) {
			DirContextOperations ldapUser = ldapSearch.searchForUser(user.getUsername());
			final String ldapEmail = ldapUser.getStringAttribute("mail");
			final boolean emailsMatch = ldapEmail.equalsIgnoreCase(user.getEmail());
			if (!emailsMatch) {
				throw new InvalidLdapUserDetailsException(InvalidLdapUserDetailsException.INVALID_USER_DETAILS_MESSAGE);
			}
		}

		final boolean newUser = user.getId() == null;

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
		if (user.getConsecutiveLoginFailures() >= this.maxLoginAttempts) {
			user.setAccountLocked(true);
		}

		return userRepository.save(user);
	}

	/**
	 * Saves user with newPassword and updates {@link User#credentialsExpirationDate}, otherwise throws an
	 * {@link InvalidPasswordException}.
	 *
	 * @param user
	 * @param currentPassword
	 * @param newPassword
	 * @param confirmPassword
	 * @return Updated user
	 * @throws InvalidPasswordException
	 */
	public User changePassword(final User user, final String currentPassword, final String newPassword,
			final String confirmPassword) throws InvalidPasswordException, InvalidLdapUserDetailsException {
		validatePassword(user, currentPassword, newPassword, confirmPassword);

		user.setPassword(passwordEncoder.encode(newPassword));

		resetCredentialsExpired(user);

		return this.save(user);
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
	 * @throws InvalidPasswordException
	 */
	private void validatePassword(final User user, final String currentPassword, final String newPassword,
			final String confirmPassword) throws InvalidPasswordException {
		// Current password must match existing password in the database if set.
		if (currentPassword != null && !passwordEncoder.matches(currentPassword, user.getPassword())) {
			throw new InvalidPasswordException("Current password doesn't match existing password");
		}

		// New password must equal password confirmation
		if (!newPassword.equals(confirmPassword)) {
			throw new InvalidPasswordException("New and confirm new password values do not match");
		}

		// Rule: Prevents a password from containing username.
		// TODO: Look into validating with http://www.passay.org/javadocs/org/passay/UsernameRule.html
		if (newPassword.contains(user.getUsername())) {
			throw new InvalidPasswordException("Password must not include username");
		}

		// Rule: Prevents using a previous password.
		// TODO: Look into validating with http://www.passay.org/javadocs/org/passay/DigestHistoryRule.html
		if (passwordEncoder.matches(newPassword, user.getPassword())) {
			throw new InvalidPasswordException("Must not use current password");
		}

		// Manually validate the password instead of using the @ValidPassword annotation.
		// This will allow us to set a null password in order to distinguish LDAP users.
		PasswordConstraintValidator validator = new PasswordConstraintValidator();
		if (!validator.isValid(newPassword, null)) {
			throw new InvalidPasswordException("This password does not meet all of the requirements");
		}
	}

	/**
	 * This is maintained for backward compatibility.
	 * Use {@link #sendPasswordResetTokenEmail(PasswordResetToken, HttpServletRequest, boolean, boolean)}
	 *
	 * @param token
	 *            the password reset token
	 * @param request
	 *            the request so the application url can be constructed
	 * @param dryRun
	 *            Will only print email contents to console if true
	 */
	@Deprecated
	public void sendPasswordResetTokenEmail(final PasswordResetToken token, final HttpServletRequest request,
			final boolean dryRun) {
		sendPasswordResetTokenEmail(token, request, false, dryRun);
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
		final String resetPath = buildResetPasswordUrl(token.getToken());

		SimpleMailMessage email = new SimpleMailMessage();
		String body;
		if (isNewUser) {
			email.setSubject("Welcome to " + displayName);
			body = "Hello " + user.getFirstName() + ",\n\nAn account has been created for you with username "
					+ user.getUsername() + ". To set your password, please follow this link: " + resetPath;

		} else {
			email.setSubject("Reset your password request");
			body = "Hello " + user.getFirstName() + ",\n\nTo reset your password please follow this link: "
					+ resetPath;
		}
		email.setText(body);
		email.setTo(user.getEmail());
		email.setFrom(emailConfig.getFrom());
		if (emailDryRun(dryRun, user.getEmail())) {
			log.info("DRY RUN, would have sent email to " + user.getEmail() + " from " + emailConfig.getFrom()
					+ " about " + body);
		} else {
			mailSender.send(email);
			log.info("Password reset confirmation email sent to " + user.getEmail());
		}
	}

	/**
	 * Builds a full URL for resetting a password.
	 *
	 * @param token
	 * @param request
	 * @return URL for resetting password including token.
	 */
	public String buildResetPasswordUrl(final String token) {
		return buildAppUrl() + "/user/password/reset?token=" + token;
	}

	/**
	 * Builds a full URL for the login page.
	 *
	 * @param token
	 * @param request
	 * @return URL for resetting password including token.
	 */
	protected String buildLoginUrl() {
		return buildAppUrl() + "/login";
	}

	/**
	 * Builds full app URL including context path. No trailing slash.
	 *
	 * @param request
	 * @return Full application URL with context path.
	 */
	protected String buildAppUrl() {
		return (baseUrl + contextPath).replaceAll("\\/*$", "");
	}

	/**
	 * Update a user's password per reset request.
	 *
	 * @param password
	 * @param token
	 * @param sendEmail
	 * @return User with updated password.
	 * @throws InvalidPasswordException
	 * @throws InvalidLdapUserDetailsException
	 */
	public User resetPassword(final String newPassword, final String confirmPassword, final String token)
			throws InvalidPasswordException, InvalidLdapUserDetailsException {
		Assert.notNull(newPassword, "Password is required");
		Assert.notNull(confirmPassword, "Password confirmation is required");
		Assert.notNull(token, "Password reset token is required");
		PasswordResetToken existingToken = passwordResetTokenService.findByToken(token);
		Assert.notNull(existingToken, "Could not find existing token");
		User user = existingToken.getUser();
		validatePassword(user, null, newPassword, confirmPassword);
		user.setPassword(passwordEncoder.encode(newPassword));

		resetCredentialsExpired(user);

		User saved = this.save(user);
		passwordResetTokenService.expireToken(existingToken);
		return saved;
	}

	/**
	 * Sets credentials expired to false and sets a new credentials expiration date in the future.
	 *
	 * @param user
	 */
	private void resetCredentialsExpired(User user) {
		user.setCredentialsExpired(false);
		Instant now = Instant.now();
		user.setCredentialsExpirationDate(Date.from(now.plus(credentialsExpirationPeriod, ChronoUnit.DAYS)));
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
		email.setSubject("Your password was reset");
		final String body = "Your password has been reset. You may now log into the application.\n\nUsername: "
				+ passwordResetToken.getUser().getUsername()
				+ "\nLink: " + buildLoginUrl();
		email.setText(body);
		email.setTo(userEmail);
		email.setFrom(emailConfig.getFrom());

		if (emailDryRun(dryRun, userEmail)) {
			log.info("DRY RUN, would have sent email to " + userEmail + " from " + emailConfig.getFrom()
					+ " about "
					+ body);
		} else {
			mailSender.send(email);
			log.info("Password reset confirmation email sent to " + userEmail);
		}
	}

	private boolean emailDryRun(final boolean dryRun, final String toEmail) {
		return dryRun || StringUtils.isBlank(toEmail) || profileUtils.isActive(ProfileUtils.AuthProfile.noemail.toString());
	}

	public void setTableBasedEnabled(Boolean tableBasedEnabled) {
		this.tableBasedEnabled = tableBasedEnabled;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

}
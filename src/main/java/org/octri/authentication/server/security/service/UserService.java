package org.octri.authentication.server.security.service;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.EmailConfiguration;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.password.PasswordConstraintValidator;
import org.octri.authentication.server.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.UrlUtils;
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

	@Value("${octri.authentication.max-login-attempts:7}")
	private int maxLoginAttempts;

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
			final String confirmPassword) throws InvalidPasswordException {
		validatePassword(user, currentPassword, newPassword, confirmPassword);

		user.setPassword(passwordEncoder.encode(newPassword));

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
	 * Send email confirmation to user.
	 * 
	 * @param user
	 * @param token
	 * @param request
	 * @param dryRun
	 *            Will only print email contents to console if true
	 */
	public void sendPasswordResetTokenEmail(final PasswordResetToken token, final HttpServletRequest request,
			final boolean dryRun) {
		
		User user = token.getUser();
		SimpleMailMessage email = new SimpleMailMessage();
		email.setSubject("Reset your password request");
		final String resetPath = buildResetPasswordUrl(token.getToken(), request);
		final String body = "Hello " + user.getFirstName() + ", to reset your password please follow this link: "
				+ resetPath;
		email.setText(body);
		email.setTo(user.getEmail());
		email.setFrom(emailConfig.getFrom());
		if (dryRun) {
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
	protected String buildResetPasswordUrl(final String token, final HttpServletRequest request) {
		final String appUrl = buildAppUrl(request);
		return appUrl + "/user/password/reset?token=" + token;
	}

	/**
	 * Builds a full URL for the login page.
	 * 
	 * @param token
	 * @param request
	 * @return URL for resetting password including token.
	 */
	protected String buildLoginUrl(final HttpServletRequest request) {
		final String appUrl = buildAppUrl(request);
		return appUrl + "/login";
	}

	/**
	 * Builds full app URL including context path. No trailing slash.
	 * 
	 * @param request
	 * @return Full application URL with context path.
	 */
	protected String buildAppUrl(final HttpServletRequest request) {
		final String fullUrl = UrlUtils.buildFullRequestUrl(request);
		final String urlPath = UrlUtils.buildRequestUrl(request);
		return fullUrl.substring(0, fullUrl.indexOf(urlPath));
	}

	/**
	 * Update a user's password per reset request.
	 * 
	 * @param password
	 * @param token
	 * @param sendEmail
	 * @return User with updated password.
	 * @throws InvalidPasswordException
	 */
	public User resetPassword(final String newPassword, final String confirmPassword, final String token)
			throws InvalidPasswordException {
		Assert.notNull(newPassword, "Password is required");
		Assert.notNull(confirmPassword, "Password confirmation is required");
		Assert.notNull(token, "Password reset token is required");
		PasswordResetToken existingToken = passwordResetTokenService.findByToken(token);
		Assert.notNull(existingToken, "Could not find existing token");
		User user = existingToken.getUser();
		validatePassword(user, null, newPassword, confirmPassword);
		user.setPassword(passwordEncoder.encode(newPassword));
		User saved = this.save(user);
		passwordResetTokenService.expireToken(existingToken);
		return saved;
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
		final String body = "You may now log into the application, your password has been reset. "
				+ buildLoginUrl(request);
		email.setText(body);
		email.setTo(userEmail);
		email.setFrom(emailConfig.getFrom());

		if (dryRun) {
			log.info("DRY RUN, would have sent email to " + userEmail + " from " + emailConfig.getFrom()
					+ " about "
					+ body);
		} else {
			mailSender.send(email);
			log.info("Password reset confirmation email sent to " + userEmail);
		}
	}

}

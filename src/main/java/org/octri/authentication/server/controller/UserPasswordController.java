package org.octri.authentication.server.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.MethodSecurityExpressions;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.password.Messages;
import org.octri.authentication.server.security.service.PasswordResetTokenService;
import org.octri.authentication.server.security.service.UserService;
import org.octri.authentication.utils.ProfileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for all the reset password functionality
 *
 * @author yateam
 *
 */
@Controller
@Scope("session")
public class UserPasswordController {

	private static final Log log = LogFactory.getLog(UserPasswordController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	@Autowired
	private ProfileUtils profileUtils;

	/**
	 * Present a form for changing a password when credentials are expired.
	 *
	 * @return change template
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@GetMapping("user/password/change")
	public String changePassword(ModelMap model) {
		model.addAttribute("formTitle", Messages.TITLE_CHANGE_PASSWORD);
		model.addAttribute("formRoute", "/user/password/change");
		return "user/password/form";
	}

	/**
	 * Persist changed password.
	 *
	 * @param currentPassword
	 *            The user's current password
	 * @param newPassword
	 *            The user's new password
	 * @param confirmPassword
	 *            Confirming the user's new password
	 * @param redirectAttributes
	 *            Used to hold attributes for a redirect.
	 * @param request
	 *            The {@link HttpServletRequest}
	 * @param model
	 *            Object holding view data
	 * @return Redirects to /login
	 * @throws InvalidLdapUserDetailsException
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@PostMapping("user/password/change")
	public String changePassword(@ModelAttribute("currentPassword") String currentPassword,
			@ModelAttribute("newPassword") String newPassword,
			@ModelAttribute("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes,
			HttpServletRequest request, ModelMap model) {
		final String username = (String) request.getSession().getAttribute("lastUsername");
		Assert.notNull(username, Messages.COULD_NOT_FIND_USERNAME_IN_SESSION);

		final User user = userService.findByUsername(username);
		Assert.notNull(user, Messages.COULD_NOT_FIND_AN_EXISTING_USER);

		model.addAttribute("formTitle", Messages.TITLE_CHANGE_PASSWORD);
		model.addAttribute("formRoute", "/user/password/change");

		try {
			ImmutablePair<User, List<String>> result = userService.changePassword(user, currentPassword, newPassword, confirmPassword);

			if (result.right.isEmpty()) {
				redirectAttributes.addFlashAttribute("passwordChanged", true);
				model.clear();
				return "redirect:/login";
			} else {
				model.addAttribute("errorMessages", result.right);
				model.addAttribute("currentPasswordIncorrect", result.right.contains(Messages.CURRENT_PASSWORD_INCORRECT));
				model.addAttribute("passwordValidationError", true);
				return "user/password/form";
			}
		} catch (InvalidLdapUserDetailsException ex) {
			log.error("Unexpected LDAP exception while saving " + username, ex);
			model.addAttribute("errorMessage", Messages.DEFAULT_ERROR_MESSAGE);
			return "user/password/form";
		} catch (RuntimeException ex) {
			log.error("Unexpected runtime exception when " + username + " tried to change their password", ex);
			model.addAttribute("errorMessage", Messages.DEFAULT_ERROR_MESSAGE);
			return "user/password/form";
		}
	}

	/**
	 * A form for initiating a password reset.
	 *
	 * @return forgot template
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@GetMapping("user/password/forgot")
	public String forgotPassword() {
		return "user/password/forgot";
	}

	/**
	 * Handles generating and sending a password reset token.
	 *
	 * @param email
	 *            The users email address.
	 * @param model
	 *            Object holding view data
	 * @param request
	 *            The {@link HttpServletRequest}
	 * @return Returns to the same forgot template and presents a confirmation message.
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@PostMapping("user/password/forgot")
	public String forgotPassword(@ModelAttribute("email") String email, ModelMap model, HttpServletRequest request) {
		try {
			User user = userService.findByEmail(email);
			PasswordResetToken token = passwordResetTokenService.generatePasswordResetToken(user);
			userService.sendPasswordResetTokenEmail(token, request, false, false);
			model.addAttribute("confirmation", true);
			model.addAttribute("expire_in_minutes", PasswordResetToken.EXPIRE_IN_MINUTES);
			return "user/password/forgot";
		} catch (Exception ex) {
			log.error("Error while processing password reset request for email address " + email, ex);
			model.addAttribute("error", true);
			return "user/password/forgot";
		}
	}

	/**
	 * A form for resetting a password.
	 *
	 * @param token
	 *            The user's password reset token.
	 * @param model
	 *            Object holding view data
	 * @return Password reset template
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@GetMapping("user/password/reset")
	public String resetPassword(@RequestParam("token") String token, ModelMap model) {
		// Check to see if there is a valid token.
		// A record should exist in the database and be not expired.
		if (!passwordResetTokenService.isValidPasswordResetToken(token)) {
			model.addAttribute("errorMessage", Messages.INVALID_PASSWORD_RESET_TOKEN);
		}
		model.addAttribute("formTitle", Messages.TITLE_RESET_PASSWORD);
		model.addAttribute("formRoute", "/user/password/reset");
		model.addAttribute("token", token);
		return "user/password/form";
	}

	/**
	 * Persist reset password.
	 *
	 * @param password
	 *            The user's new password
	 * @param token
	 *            The user's password reset token.
	 * @param redirectAttributes
	 *            Used to hold attributes for a redirect.
	 * @param request
	 *            The {@link HttpServletRequest}
	 * @param model
	 *            Object holding view data
	 * @return Redirects to /login
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@PostMapping("user/password/reset")
	public String resetPassword(@ModelAttribute("newPassword") String newPassword,
			@ModelAttribute("confirmPassword") String confirmPassword, @ModelAttribute("token") String token,
			RedirectAttributes redirectAttributes, HttpServletRequest request, ModelMap model) {
		model.addAttribute("formTitle", Messages.TITLE_RESET_PASSWORD);
		model.addAttribute("formRoute", "/user/password/reset");
		try {
			ImmutablePair<User, List<String>> result = userService.resetPassword(newPassword, confirmPassword, token);

			if (result.right.isEmpty()) {
				userService.sendPasswordResetEmailConfirmation(token, request, false);
				redirectAttributes.addFlashAttribute("passwordReset", true);
				model.clear();
				return "redirect:/login";
			} else {
				model.addAttribute("errorMessages", result.right);
				model.addAttribute("passwordValidationError", true);
				return "user/password/form";
			}
		} catch (InvalidLdapUserDetailsException ex) {
			PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
			final String username = passwordResetToken != null ? passwordResetToken.getUser().getUsername() : null;
			log.error("Unexpected LDAP exception while saving " + username, ex);
			model.addAttribute("errorMessage", Messages.DEFAULT_ERROR_MESSAGE);
			return "user/password/form";
		} catch (RuntimeException ex) {
			log.error("Unexpected error while saving password", ex);
			model.addAttribute("errorMessage", Messages.DEFAULT_ERROR_MESSAGE);
			return "user/password/form";
		}
	}

	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@PostMapping("admin/password/token/refresh")
	public String passwordTokenRefresh(final ModelMap model, @RequestParam(name = "userId") Long userId,
			RedirectAttributes redirectAttributes) {
		if (profileUtils.isActive(ProfileUtils.AuthProfile.noemail)) {
			final User user = userService.find(userId);
			Assert.notNull(user, "Could not find a user");
			passwordResetTokenService.save(new PasswordResetToken(user, PasswordResetToken.LONG_EXPIRE_IN_MINUTES));
		}
		return "redirect:/admin/user/form?id=" + userId;
	}
}
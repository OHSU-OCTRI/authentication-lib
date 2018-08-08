package org.octri.authentication.server.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.MethodSecurityExpressions;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.service.PasswordResetTokenService;
import org.octri.authentication.server.security.service.UserRoleService;
import org.octri.authentication.server.security.service.UserService;
import org.octri.authentication.server.view.OptionList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller for managing {@link User}.
 * 
 * @author sams
 */
@Controller
@Scope("session")
public class UserController {

	private static final Log log = LogFactory.getLog(UserController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private UserRoleService userRoleService;

	@Autowired
	private PasswordResetTokenService passwordResetTokenService;

	@Autowired
	private Boolean ldapEnabled;

	@Autowired
	private Boolean tableBasedEnabled;

	private static final String NEW_USER_MAPPING = "admin/user/new";
	private static final String EDIT_USER_MAPPING = "admin/user/edit";

	/**
	 * Returns view for displaying a list of all users.
	 * 
	 * @param model
	 *            Object holding view data
	 * @return List view
	 */
	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@GetMapping("admin/user/list")
	public String listUsers(ModelMap model) {
		List<User> users = userService.findAll();
		model.addAttribute("users", users);
		model.addAttribute("userRoles", userRoles());
		return "admin/user/list";
	}

	/**
	 * Returns view for creating a new user.
	 * 
	 * @param model
	 *            Object holding view data
	 * @return New user view.
	 */
	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@GetMapping("admin/user/new")
	public String newUser(ModelMap model) {
		model.addAttribute("user", new User());
		model.addAttribute("userRoles", userRoles());
		model.addAttribute("pageTitle", "New User");
		model.addAttribute("requestMapping", NEW_USER_MAPPING);
		model.addAttribute("newUser", true);
		return "admin/user/form";
	}

	/**
	 * Persists a new user. See {@link #edit(User, ModelMap)} for saving changes to
	 * an existing {@link User}.
	 * 
	 * @param user
	 *            User being created
	 * @param bindingResult
	 *            Binding result for 'user' parameter
	 * @param model
	 *            Object holding view data
	 * @return new user template on error, or redirects to user list
	 */
	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@PostMapping("admin/user/new")
	public String newUser(@Valid @ModelAttribute User user, BindingResult bindingResult, final ModelMap model,
			HttpServletRequest request) {
		Assert.notNull(user, "User must not be null");
		model.addAttribute("user", user);
		model.addAttribute("userRoles", OptionList.multiFromSearch(userRoles(), user.getUserRoles()));
		model.addAttribute("pageTitle", "New User");
		model.addAttribute("requestMapping", NEW_USER_MAPPING);
		model.addAttribute("newUser", true);
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", true);
			model.addAttribute("errors", bindingResult.getAllErrors());
			return "admin/user/form";
		}

		try {
			User newUser = userService.save(user);

			// The new user is LDAP if table-based auth is not enabled or if LDAP was indicated in the form
			Boolean ldapUser = !getTableBasedEnabled() || user.getLdapUser();
			if (!ldapUser) {
				// TODO: In the future this should be more sophisticated - probably a welcome email for the new user.
				PasswordResetToken token = passwordResetTokenService.generatePasswordResetToken(newUser);
				userService.sendPasswordResetTokenEmail(token, request, false);
			}
		} catch (InvalidLdapUserDetailsException ex) {
			log.error("Could not add new user", ex);
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", InvalidLdapUserDetailsException.INVALID_USER_DETAILS_MESSAGE);
			return "admin/user/form";
		} catch (UsernameNotFoundException ex) {
			log.error("User not found", ex);
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", "The username provided could not be found in LDAP");
			return "admin/user/form";
		} catch (RuntimeException ex) {
			log.error("Unexpected runtime exception while adding new user", ex);
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", "Unexpected exception while adding new user");
			return "admin/user/form";
		}

		model.clear();
		return "redirect:/admin/user/list";
	}

	/**
	 * Presents a page for editing a user.
	 * 
	 * @param id
	 *            User id
	 * @param model
	 *            Object holding view data
	 * @return Edit user template
	 */
	@PreAuthorize(MethodSecurityExpressions.EDIT_USER)
	@GetMapping("admin/user/edit/{id}")
	public String editUser(@PathVariable("id") long id, ModelMap model) {
		Assert.notNull(id, "id must not be null");
		User user = userService.find(id);
		Assert.notNull(user, "Could not find a user for id " + id);
		model.addAttribute("user", user);
		model.addAttribute("userRoles", OptionList.multiFromSearch(userRoles(), user.getUserRoles()));
		model.addAttribute("pageTitle", "Edit User");
		model.addAttribute("requestMapping", EDIT_USER_MAPPING);
		model.addAttribute("newUser", false);
		return "admin/user/form";
	}

	/**
	 * Persists changes to {@link User}. See {@link #newUser(User, ModelMap)} for
	 * persisting a new {@link User}.
	 * 
	 * @param id
	 *            User id
	 * @param user
	 *            User being edited
	 * @param bindingResult
	 *            Binding result for user parameter
	 * @param model
	 *            Object holding view data
	 * @return Redirects to list of all users, or returns to current page and displays an error message.
	 */
	@PreAuthorize(MethodSecurityExpressions.EDIT_USER)
	@PostMapping("admin/user/edit")
	public String edit(@Valid @ModelAttribute User user, BindingResult bindingResult,
			final ModelMap model) {
		Assert.notNull(user, "User must not be null");
		model.addAttribute("user", user);
		model.addAttribute("userRoles", OptionList.multiFromSearch(userRoles(), user.getUserRoles()));
		model.addAttribute("pageTitle", "Edit User");
		model.addAttribute("requestMapping", EDIT_USER_MAPPING);
		model.addAttribute("newUser", false);
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", true);
			model.addAttribute("errors", bindingResult.getAllErrors());
			return "admin/user/form";
		}
		try {
			userService.save(user);
		} catch (InvalidLdapUserDetailsException ex) {
			log.error("Could not edit user", ex);
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", InvalidLdapUserDetailsException.INVALID_USER_DETAILS_MESSAGE);
			return "admin/user/form";
		} catch (UsernameNotFoundException ex) {
			log.error("User not found", ex);
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", "The username provided could not be found in LDAP");
			return "admin/user/form";
		} catch (RuntimeException ex) {
			log.error("Unexpected runtime exception while editing user", ex);
			model.addAttribute("error", true);
			model.addAttribute("errorMessage", "Unexpected exception while adding new user");
			return "admin/user/form";
		}
		model.clear();
		return "redirect:/admin/user/list";
	}

	/**
	 * Present a form for changing a password when credentials are expired.
	 * 
	 * @return change template
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@GetMapping("user/password/change")
	public String changePassword() {
		return "user/password/change";
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
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@PostMapping("user/password/change")
	public String changePassword(@ModelAttribute("currentPassword") String currentPassword,
			@ModelAttribute("newPassword") String newPassword,
			@ModelAttribute("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes,
			HttpServletRequest request, ModelMap model) {
		final String username = (String) request.getSession().getAttribute("lastUsername");
		Assert.notNull(username, "Could not find username in session");

		final User user = userService.findByUsername(username);
		Assert.notNull(user, "Could not find an existing user");

		try {
			userService.changePassword(user, currentPassword, newPassword, confirmPassword);
			redirectAttributes.addFlashAttribute("passwordChanged", true);
			model.clear();
			return "redirect:/login";
		} catch (InvalidPasswordException ex) {
			log.error(username + " submitted an invalid password", ex);
			model.addAttribute("error", true);
			return "user/password/change";
		} catch (InvalidLdapUserDetailsException ex) {
			log.error("Could not change password", ex);
			model.addAttribute("error", true);
			return "user/password/change";
		} catch (RuntimeException ex) {
			log.error("Unexpected runtime exception when " + username + " tried to change their password", ex);
			model.addAttribute("error", true);
			return "user/password/change";
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
			userService.sendPasswordResetTokenEmail(token, request, false);
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
			model.addAttribute("invalidToken", true);
		}
		model.addAttribute("token", token);
		return "user/password/reset";
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
		try {
			userService.resetPassword(newPassword, confirmPassword, token);
			userService.sendPasswordResetEmailConfirmation(token, request, false);
			redirectAttributes.addFlashAttribute("passwordReset", true);
			model.clear();
			return "redirect:/login";
		} catch (InvalidPasswordException ex) {
			log.error("Validation error while saving password", ex);
			model.addAttribute("invalidPassword", true);
			return "user/password/reset";
		} catch (Exception ex) {
			log.error("Unexpected error while saving password", ex);
			model.addAttribute("error", true);
			return "user/password/reset";
		}
	}

	/**
	 * Model attribute that can be used in templates.
	 * 
	 * @return Returns a list of all {@link UserRole}.
	 */
	@ModelAttribute("allUserRoles")
	public List<UserRole> userRoles() {
		return userRoleService.findAll();
	}

	/**
	 * Model attribute that can be used in templates.
	 * 
	 * @return Returns a list of all {@link User}.
	 */
	@ModelAttribute("allUsers")
	public List<User> users() {
		return userService.findAll();
	}

	/**
	 * @return Boolean indicating LDAP is enabled or disabled.
	 */
	@ModelAttribute("ldapEnabled")
	public Boolean getLdapEnabled() {
		return ldapEnabled;
	}

	/**
	 * @return Boolean indicating table-based authentication is enabled or disabled.
	 */
	@ModelAttribute("tableBasedEnabled")
	public Boolean getTableBasedEnabled() {
		return tableBasedEnabled;
	}

}

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
import org.octri.authentication.server.security.exception.InvalidPasswordException;
import org.octri.authentication.server.security.password.PasswordGenerator;
import org.octri.authentication.server.security.service.UserRoleService;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	private Boolean ldapEnabled;

	@Autowired
	private Boolean tableBasedEnabled;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Returns view for displaying a list of all users.
	 * 
	 * @param model
	 * @return List view
	 */
	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@GetMapping("admin/user/list")
	public String listUsers(Model model) {
		List<User> users = userService.findAll();
		model.addAttribute("users", users);
		return "admin/user/list";
	}

	/**
	 * Returns view for creating a new user.
	 * 
	 * @param model
	 * @return New user view.
	 */
	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@GetMapping("admin/user/new")
	public String newUser(Model model) {
		model.addAttribute("user", new User());
		return "admin/user/new";
	}

	/**
	 * Persists a new user. See {@link #edit(User, ModelMap)} for saving changes to
	 * an existing {@link User}.
	 * 
	 * @param user
	 * @param model
	 * @return
	 */
	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@PostMapping("admin/user/new")
	public String newUser(@Valid @ModelAttribute User user, BindingResult bindingResult, final ModelMap modelMap,
			final Model model) {
		Assert.notNull(user, "User must not be null");
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", true);
			return "admin/user/new";
		}
		// Set a random password, user will be forced to reset their password
		// TODO: We could expose the password in the UI for providing to new users since they will be forced to change
		// their passwords on the next login.
		final String password = PasswordGenerator.generate();
		user.setPassword(passwordEncoder.encode(password));
		userService.save(user);
		modelMap.clear();
		return "redirect:/admin/user/list";
	}

	@PreAuthorize(MethodSecurityExpressions.EDIT_USER)
	@GetMapping("admin/user/edit/{id}")
	public String editUser(@PathVariable("id") long id, Model model) {
		Assert.notNull(id, "id must not be null");
		User user = userService.find(id);
		Assert.notNull(user, "Could not find a user for id " + id);
		model.addAttribute("user", user);
		return "admin/user/edit";
	}

	/**
	 * Persists changes to {@link User}. See {@link #newUser(User, ModelMap)} for
	 * persisting a new {@link User}.
	 * 
	 * @param user
	 * @param model
	 * @return Redirects to list of all users.
	 */
	@PreAuthorize(MethodSecurityExpressions.EDIT_USER)
	@PostMapping("admin/user/edit/{id}")
	public String edit(@PathVariable("id") long id, @Valid @ModelAttribute User user, BindingResult bindingResult,
			final ModelMap model) {
		Assert.notNull(id, "User id must not be null");
		Assert.notNull(user, "User must not be null");
		if (bindingResult.hasErrors()) {
			model.addAttribute("error", true);
			return "admin/user/edit";
		}
		// Must retrieve existing password otherwise it is overwritten.
		// Cannot edit password with the edit form, however the field is bound and will
		// be persisted as null.
		User existing = userService.find(id);
		if (existing.getPassword() != null) {
			user.setPassword(existing.getPassword());
		}
		userService.save(user);
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
	 * @return Redirects to /login
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@PostMapping("user/password/change")
	public String changePassword(@ModelAttribute("currentPassword") String currentPassword,
			@ModelAttribute("newPassword") String newPassword, @ModelAttribute("password") String password,
			@ModelAttribute("confirmPassword") String confirmPassword, RedirectAttributes redirectAttributes,
			HttpServletRequest request, Model model, ModelMap modelMap) {
		final String username = (String) request.getSession().getAttribute("lastUsername");
		Assert.notNull(username, "Could not find username in session");

		final User user = userService.findByUsername(username);
		Assert.notNull(user, "Could not find an existing user");

		try {
			userService.changePassword(user, currentPassword, newPassword, confirmPassword);
			redirectAttributes.addFlashAttribute("passwordChanged", true);
			modelMap.clear();
			return "redirect:/login";
		} catch (InvalidPasswordException ex) {
			log.error(username + " submitted an invalid password", ex);
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

	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@PostMapping("user/password/forgot")
	public String forgotPassword(@ModelAttribute("email") String email, Model model, HttpServletRequest request) {
		try {
			PasswordResetToken token = userService.generatePasswordResetToken(email);
			userService.sendPasswordResetTokenEmail(token.getUser(), token.getToken(), request, false);
		} catch (Exception ex) {
			log.error("Error while processing password reset request for email address " + email, ex);
		}
		model.addAttribute("confirmation", true);
		return "user/password/forgot";
	}

	/**
	 * A form for reseting a password.
	 * 
	 * @return forgot template
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@GetMapping("user/password/reset")
	public String resetPassword(@RequestParam("token") String token, Model model) {
		// Check to see if there is a valid token.
		// A record should exist in the database and be not expired.
		if (!userService.isValidPasswordResetToken(token)) {
			model.addAttribute("invalidToken", true);
		}
		model.addAttribute("token", token);
		return "user/password/reset";
	}

	/**
	 * Persist reset password.
	 * 
	 * @return Redirects to /login
	 */
	@PreAuthorize(MethodSecurityExpressions.ANONYMOUS)
	@PostMapping("user/password/reset")
	public String resetPassword(@ModelAttribute("password") String password, @ModelAttribute("token") String token,
			RedirectAttributes redirectAttributes, HttpServletRequest request, Model model, ModelMap modelMap) {
		try {
			userService.resetPassword(password, token);
			userService.sendPasswordResetEmailConfirmation(token, request, false);
			redirectAttributes.addFlashAttribute("passwordReset", true);
			modelMap.clear();
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
	@ModelAttribute("ldap")
	public Boolean getLdapEnabled() {
		return ldapEnabled;
	}

	@ModelAttribute("tableBased")
	public Boolean getTableBasedEnabled() {
		return tableBasedEnabled;
	}

}

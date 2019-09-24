package org.octri.authentication.server.controller;

import static org.octri.authentication.server.security.entity.PasswordResetToken.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.MethodSecurityExpressions;
import org.octri.authentication.server.security.SecurityHelper;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.authentication.server.security.password.PasswordGenConfig;
import org.octri.authentication.server.security.service.PasswordResetTokenService;
import org.octri.authentication.server.security.service.UserRoleService;
import org.octri.authentication.server.security.service.UserService;
import org.octri.authentication.server.view.OptionList;
import org.octri.authentication.utils.ProfileUtils;
import org.octri.authentication.utils.ValidationUtils;
import org.octri.authentication.validation.Emailable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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

	@Autowired
	private Validator validator;

	@Autowired
	private ProfileUtils profileUtils;

	@Autowired
	private ValidationUtils<User> validationUtils;

	@Autowired
	private PasswordGenConfig passwordGeneration;

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
		model.addAttribute("users", users());
		model.addAttribute("userRoles", userRoles());
		model.addAttribute("listView", true);
		return "admin/user/list";
	}

	/**
	 * User create/edit form.
	 *
	 * @param request
	 *            The {@link HttpServletRequest}
	 * @param model
	 *            Object holding view data
	 * @return User form template
	 */
	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@GetMapping("admin/user/form")
	public String userForm(HttpServletRequest request, ModelMap model) {
		String id = request.getParameter("id");
		if (id == null) {
			model.addAttribute("user", new User());
			model.addAttribute("userRoles", userRoles());
			model.addAttribute("pageTitle", "New User");
			model.addAttribute("newUser", true);
			model.addAttribute("formView", true);
		} else {
			SecurityHelper securityHelper = new SecurityHelper(SecurityContextHolder.getContext());
			if (securityHelper.canEditUser(Long.valueOf(id))) {
				User user = userService.find(Long.valueOf(id));
				Assert.notNull(user, "Could not find a user for id " + id);
				model.addAttribute("user", user);
				model.addAttribute("userRoles", OptionList.multiFromSearch(userRoles(), user.getUserRoles()));
				model.addAttribute("pageTitle", "Edit User");
				model.addAttribute("newUser", false);
				model.addAttribute("formView", true);

				if (profileUtils.isActive(ProfileUtils.AuthProfile.noemail)) {
					Optional<PasswordResetToken> latestToken = passwordResetTokenService.findLatest(user.getId());
					if (latestToken.isPresent()
							&& passwordResetTokenService.isValidPasswordResetToken(latestToken.get().getToken())) {
						final String url = userService.buildResetPasswordUrl(latestToken.get().getToken());
						model.addAttribute("passwordResetUrl", url);
					} else {
						model.addAttribute("showNewTokenButton", true);
					}
				}
				if (passwordGeneration.getEnabled() && getTableBasedEnabled() && !user.getLdapUser()) {
					model.addAttribute("allowPasswordGeneration", true);
				}
			} else {
				log.error(securityHelper.username() + " does not have access to edit user " + id);
				model.addAttribute("status", 403);
				model.addAttribute("error", "Access Denied");
				model.addAttribute("message", "You may not edit yourself.");
				model.addAttribute("timestamp", new Date());
				return "error";
			}
		}
		return "admin/user/form";
	}

	/**
	 * Persists a new or existing user.
	 *
	 * @param user
	 *            User being created
	 * @param bindingResult
	 *            Binding result for 'user' parameter
	 * @param model
	 *            Object holding view data
	 * @param request
	 *            The {@link HttpServletRequest} for the request
	 * @return new user template on error, or redirects to user list
	 */
	@PreAuthorize(MethodSecurityExpressions.ADMIN_OR_SUPER)
	@PostMapping("admin/user/form")
	public String userForm(@ModelAttribute User user, BindingResult bindingResult, final ModelMap model,
			HttpServletRequest request) {
		Assert.notNull(user, "User must not be null");

		final boolean newUser = user.getId() == null;

		if (newUser) {
			model.addAttribute("pageTitle", "New User");
		} else {
			model.addAttribute("pageTitle", "Edit User");
		}

		model.addAttribute("newUser", newUser);
		model.addAttribute("user", user);
		model.addAttribute("userRoles", OptionList.multiFromSearch(userRoles(), user.getUserRoles()));
		model.addAttribute("formView", true);

		Set<ConstraintViolation<User>> validationResult = profileUtils.isActive(ProfileUtils.AuthProfile.noemail)
				? validator.validate(user, Default.class)
				: validator.validate(user, Emailable.class);

		if (validationResult.size() > 0) {
			model.addAttribute("error", true);
			model.addAttribute("errors", validationUtils.getErrors(user, validationResult));
			return "admin/user/form";
		}

		try {
			User savedUser = userService.save(user);

			if (newUser) {
				// The new user is LDAP if table-based auth is not enabled or if LDAP was indicated in the form
				Boolean ldapUser = !getTableBasedEnabled() || user.getLdapUser();
				if (!ldapUser) {
					if (profileUtils.isActive(ProfileUtils.AuthProfile.noemail)) {
						passwordResetTokenService.save(new PasswordResetToken(savedUser, LONG_EXPIRE_IN_MINUTES));
					} else {
						PasswordResetToken token = passwordResetTokenService.generatePasswordResetToken(savedUser);
						userService.sendPasswordResetTokenEmail(token, request, true, false);
					}

				}
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

		return "redirect:/admin/user/list";
	}

	/**
	 *
	 * @return Returns a list of all {@link UserRole}.
	 */
	private List<UserRole> userRoles() {
		return userRoleService.findAll();
	}

	/**
	 *
	 * @return Returns a list of all {@link User}.
	 */
	private List<User> users() {
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

	/**
	 * Ensure that empty strings are saved as NULL values.
	 * 
	 * @param binder
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
	}

}

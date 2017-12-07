package org.octri.authentication.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.octri.authentication.MethodSecurityExpressions;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.service.UserRoleService;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller for managing {@link User}.
 * 
 * @author sams
 */
@Controller
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRoleService userRoleService;

	private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	@Autowired
	private Boolean ldapEnabled;

	@Autowired
	private Boolean tableBasedEnabled;
	
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
	public String newUser(Model model, HttpServletRequest request) {
		model.addAttribute("user", new User());
		model.addAttribute("ldap", ldapEnabled);
		model.addAttribute("tableBased", tableBasedEnabled);
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
	public String newUser(@ModelAttribute User user, final ModelMap model) {
		Assert.notNull(user, "User must not be null");
		if (user.getPassword() != null) {
			user.setPassword(encoder.encode(user.getPassword()));
		}
		userService.save(user);
		model.clear();
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
	public String edit(@PathVariable("id") long id, @ModelAttribute User user, final ModelMap model) {
		Assert.notNull(id, "User id must not be null");
		Assert.notNull(user, "User must not be null");
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

}

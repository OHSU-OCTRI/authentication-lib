package org.octri.authentication.server.security.wrapper;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.octri.authentication.server.security.entity.User;

/**
 * This is a wrapper model class for a User. It contains additional information from
 * the form that indicates whether the User will authenticate through LDAP.
 * 
 * @author yateam
 *
 */
public class UserForm {
	
	@NotNull(message = "User must exist.")
	@Valid
	private User user;
	
	private Boolean ldapUser = false;
	
	/**
	 * This constructor creates a new User by default
	 */
	public UserForm() {
		user = new User();
	}
	
	public UserForm(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Boolean getLdapUser() {
		return ldapUser;
	}
	public void setLdapUser(Boolean ldapUser) {
		this.ldapUser = ldapUser;
	}

}

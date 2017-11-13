package org.octri.authentication.server.security.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.springframework.util.Assert;

/**
 * Join model representing a {@link User} that has been assigned a particular {@link UserRole}.
 * 
 * @author yateam
 *
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_role", "user" }) })
public class UserUserRole extends AbstractEntity {

	@NotNull
	@ManyToOne
	private UserRole userRole;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	public UserUserRole() {
	}

	public UserUserRole(User user, UserRole userRole) {
		Assert.notNull(user, "user may not be null");
		Assert.notNull(userRole, "userRole may not be null");

		this.user = user;
		this.userRole = userRole;
	}

	public UserRole getUserRole() {
		return this.userRole;
	}

	public void setUserRole(UserRole userRole) {
		this.userRole = userRole;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}

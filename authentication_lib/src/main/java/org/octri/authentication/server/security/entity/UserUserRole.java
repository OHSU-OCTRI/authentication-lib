package org.octri.authentication.server.security.entity;

import org.springframework.util.Assert;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

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

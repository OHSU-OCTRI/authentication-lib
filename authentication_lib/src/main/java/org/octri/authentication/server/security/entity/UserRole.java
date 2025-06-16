package org.octri.authentication.server.security.entity;

import java.util.List;

import org.octri.authentication.server.view.Labelled;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Represents a type of user / grouping of abilities in the system.
 *
 * @author yateam
 *
 */
@Entity
public class UserRole extends AbstractEntity implements Labelled, Comparable<UserRole> {

	/**
	 * The name of the role. Must be unique.
	 */
	@NotNull
	@Column(unique = true)
	private String roleName;

	/**
	 * A description of the role.
	 */
	@NotNull
	@Size(max = 50)
	private String description;

	/**
	 * Users who have been granted the role.
	 */
	@ManyToMany(mappedBy = "userRoles")
	private List<User> users;

	/**
	 * Gets the name of the role.
	 * 
	 * @return the role name
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * Sets the name of the role.
	 * 
	 * @param roleName
	 *            the role name
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * Gets the description of the role.
	 * 
	 * @return description of the role
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of the role.
	 * 
	 * @param description
	 *            description of the role
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the list of users who have been granted the role.
	 * 
	 * @return list of users
	 */
	public List<User> getUsers() {
		return users;
	}

	/**
	 * Sets the list of users who have been granted the role.
	 * 
	 * @param users
	 *            list of users
	 */
	public void setUsers(List<User> users) {
		this.users = users;
	}

	@Override
	public int compareTo(UserRole role) {
		return this.getRoleName().compareTo(role.getRoleName());
	}

	@Override
	public String getLabel() {
		return this.getDescription();
	}

}

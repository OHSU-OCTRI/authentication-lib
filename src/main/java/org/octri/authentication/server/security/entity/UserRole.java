package org.octri.authentication.server.security.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Size;

import org.octri.authentication.server.view.Labelled;

/**
 * Represents a type of user / grouping of abilities in the system.
 * 
 * @author yateam
 *
 */
@Entity
public class UserRole extends AbstractEntity implements Labelled, Comparable<UserRole> {

	@Column(unique = true)
	private String roleName;

	@Size(max = 50)
	private String description;
	
	@ManyToMany(mappedBy = "userRoles")
	private List<User> users;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<User> getUsers() {
		return users;
	}

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

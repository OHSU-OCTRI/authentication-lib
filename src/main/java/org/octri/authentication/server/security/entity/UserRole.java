package org.octri.authentication.server.security.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

/**
 * Represents a type of user / grouping of abilities in the system.
 * 
 * @author yateam
 *
 */
@Entity
public class UserRole extends AbstractEntity{

	@Column(unique = true)
	private String roleName;

	@Size(max = 50)
	private String description;

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
}

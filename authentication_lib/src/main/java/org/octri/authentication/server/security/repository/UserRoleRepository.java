package org.octri.authentication.server.security.repository;

import org.octri.authentication.server.security.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * {@link JpaRepository} for manipulating {@link UserRole} entities.
 *
 * @author yateam
 *
 */
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

	/**
	 * Finds a role by its name.
	 * 
	 * @param roleName
	 *            role name
	 * @return the role with the given name, or null if no role matches
	 */
	public UserRole findByRoleName(@Param("roleName") String roleName);
}

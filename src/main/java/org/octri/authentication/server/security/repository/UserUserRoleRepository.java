/**
 * 
 */
package org.octri.authentication.server.security.repository;

import java.util.List;

import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.entity.UserUserRole;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * {@link JpaRepository} for manipulating {@link UserUserRole} entities.
 * 
 * @author yateam
 *
 */
public interface UserUserRoleRepository extends JpaRepository<UserUserRole, Long> {

	public List<UserUserRole> findByUser(User user);
}

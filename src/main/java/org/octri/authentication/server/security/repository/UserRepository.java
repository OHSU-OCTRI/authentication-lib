package org.octri.authentication.server.security.repository;

import org.octri.authentication.server.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * {@link JpaRepository} for manipulating {@link User} entities.
 * 
 * @author harrelst
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

	public User findByUsername(@Param("username") String username);
}

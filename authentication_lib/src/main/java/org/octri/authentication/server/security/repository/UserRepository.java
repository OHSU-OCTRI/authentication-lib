package org.octri.authentication.server.security.repository;

import java.util.List;

import org.octri.authentication.server.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * {@link JpaRepository} for manipulating {@link User} entities.
 *
 * @author harrelst
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {

	/**
	 * Finds a user by their username.
	 *
	 * @param username
	 *            username to search by
	 * @return the user with the given username, or null if not found
	 */
	public User findByUsername(@Param("username") String username);

	/**
	 * Finds a user by their username (case insensitive).
	 *
	 * @param username
	 *            username to search by
	 * @return the user with the given username, or null if not found
	 */
	public User findByUsernameIgnoreCase(@Param("username") String username);

	/**
	 * Finds a user by their email address.
	 *
	 * @param email
	 *            email address to search by
	 * @return the user with the given email address, or null if not found
	 */
	public User findByEmail(@Param("email") String email);

	/**
	 * Finds a user by their email address (case insensitive).
	 *
	 * @param email
	 *            email address to search by
	 * @return the user with the given email address, or null if not found
	 */
	public User findByEmailIgnoreCase(@Param("email") String email);

	/**
	 * Finds user accounts based on whether or not they are locked/enabled
	 *
	 * @param accountLocked
	 *            locked state to search by
	 * @param enabled
	 *            enabled state to search by
	 * @return users who match the accountLocked and enabled criteria
	 */
	public List<User> findByAccountLockedAndEnabled(Boolean accountLocked, Boolean enabled);

	/**
	 * Finds a user with username or email matching the given identifier.
	 *
	 * @param identifier
	 *            string to match; expected to be a username or email address
	 * @return matching user, or null if not found
	 */
	@Query("SELECT u from User u WHERE LOWER(u.username) = LOWER(:identifier) OR LOWER(u.email) = LOWER(:identifier)")
	public User findByUsernameOrEmailEquals(@Param("identifier") String identifier);
}

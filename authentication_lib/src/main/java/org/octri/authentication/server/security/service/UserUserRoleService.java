package org.octri.authentication.server.security.service;

import java.util.List;
import java.util.stream.Collectors;

import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.entity.UserUserRole;
import org.octri.authentication.server.security.repository.UserUserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * Service wrapper for the {@link UserUserRoleRepository}.
 *
 * @author yateam
 *
 */
@Service
public class UserUserRoleService {

	@Resource
	private UserUserRoleRepository userUserRoleRepository;

	/**
	 * Finds a user role grant by ID.
	 * 
	 * @param id
	 *            the ID to search by
	 * @return user role grant
	 */
	@Transactional(readOnly = true)
	public UserUserRole find(Long id) {
		return userUserRoleRepository.findById(id).get();
	}

	/**
	 * Finds all the user's grants.
	 * 
	 * @param user
	 *            user account
	 * @return the user's grants
	 */
	@Transactional(readOnly = true)
	public List<UserUserRole> findByUser(User user) {
		return userUserRoleRepository.findByUser(user);
	}

	/**
	 * Finds all the roles granted to the given user.
	 * 
	 * @param user
	 *            user account
	 * @return the user roles granted to the user
	 */
	@Transactional(readOnly = true)
	public List<UserRole> findUserRolesByUser(User user) {
		return findByUser(user).stream().map(uur -> uur.getUserRole()).collect(Collectors.toList());
	}

	/**
	 * Saves a user role grant.
	 * 
	 * @param userUserRole
	 *            the grant to save
	 * @return the saved grant
	 */
	@Transactional
	public UserUserRole save(UserUserRole userUserRole) {
		return userUserRoleRepository.save(userUserRole);
	}

	/**
	 * Finds all user role grants.
	 * 
	 * @return all role grants
	 */
	@Transactional(readOnly = true)
	public List<UserUserRole> findAll() {
		return (List<UserUserRole>) userUserRoleRepository.findAll();
	}

	/**
	 * Deletes a user role grant by ID.
	 * 
	 * @param id
	 *            the ID of the grant to delete
	 */
	@Transactional
	public void delete(Long id) {
		UserUserRole userUserRole = userUserRoleRepository.findById(id).orElse(null);
		if (userUserRole != null) {
			userUserRoleRepository.deleteById(id);
		}
	}

}

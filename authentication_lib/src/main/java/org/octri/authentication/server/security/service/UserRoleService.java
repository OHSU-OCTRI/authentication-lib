package org.octri.authentication.server.security.service;

import java.util.List;

import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * Service wrapper for the {@link UserRoleRepository}.
 *
 * @author sams
 */
@Service
public class UserRoleService {

	@Resource
	private UserRoleRepository userRoleRepository;

	/**
	 * Finds a role by ID.
	 * 
	 * @param id
	 *            role ID
	 * @return the role with the given ID
	 */
	@Transactional(readOnly = true)
	public UserRole find(Long id) {
		return userRoleRepository.findById(id).get();
	}

	/**
	 * Finds a role by name.
	 * 
	 * @param roleName
	 *            role name
	 * @return the role with the given name
	 */
	@Transactional(readOnly = true)
	public UserRole findByRoleName(String roleName) {
		return userRoleRepository.findByRoleName(roleName);
	}

	/**
	 * Save a role record.
	 * 
	 * @param userRole
	 *            role
	 * @return the saved role
	 */
	@Transactional
	public UserRole save(UserRole userRole) {
		return userRoleRepository.save(userRole);
	}

	/**
	 * Finds all user roles.
	 * 
	 * @return list of roles
	 */
	@Transactional(readOnly = true)
	public List<UserRole> findAll() {
		return (List<UserRole>) userRoleRepository.findAll();
	}

	/**
	 * Deletes the role with the given ID.
	 * 
	 * @param id
	 *            role ID
	 */
	@Transactional
	public void delete(Long id) {
		UserRole userRole = userRoleRepository.findById(id).orElse(null);
		if (userRole != null) {
			userRoleRepository.deleteById(id);
		}
	}

}

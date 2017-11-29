package org.octri.authentication.server.security.service;

import java.util.List;

import javax.annotation.Resource;

import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service wrapper for the {@link UserRoleRepository}.
 * 
 * @author sams
 */
@Service
public class UserRoleService {

	@Resource
	private UserRoleRepository userRoleRepository;

	@Transactional(readOnly = true)
	public UserRole find(Long id) {
		return userRoleRepository.findOne(id);
	}

	@Transactional(readOnly = true)
	public UserRole findByRoleName(String roleName) {
		return userRoleRepository.findByRoleName(roleName);
	}

	@Transactional
	public UserRole save(UserRole userRole) {
		return userRoleRepository.save(userRole);
	}

	@Transactional(readOnly = true)
	public List<UserRole> findAll() {
		return (List<UserRole>) userRoleRepository.findAll();
	}

	@Transactional
	public void delete(Long id) {
		UserRole userRole = userRoleRepository.findOne(id);
		if (userRole != null) {
			userRoleRepository.delete(id);
		}
	}

}

/**
 * 
 */
package org.octri.authentication.server.security.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.entity.UserRole;
import org.octri.authentication.server.security.entity.UserUserRole;
import org.octri.authentication.server.security.repository.UserUserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Transactional(readOnly = true)
	public UserUserRole find(Long id) {
		return userUserRoleRepository.findOne(id);
	}

	@Transactional(readOnly = true)
	public List<UserUserRole> findByUser(User user) {
		return userUserRoleRepository.findByUser(user);
	}

	@Transactional(readOnly = true)
	public List<UserRole> findUserRolesByUser(User user) {
		return findByUser(user).stream().map(uur -> uur.getUserRole()).collect(Collectors.toList());
	}

	@Transactional
	public UserUserRole save(UserUserRole userUserRole) {
		return userUserRoleRepository.save(userUserRole);
	}

	@Transactional(readOnly = true)
	public List<UserUserRole> findAll() {
		return (List<UserUserRole>) userUserRoleRepository.findAll();
	}

	@Transactional
	public void delete(Long id) {
		UserUserRole userUserRole = userUserRoleRepository.findOne(id);
		if (userUserRole != null) {
			userUserRoleRepository.delete(id);
		}
	}

}

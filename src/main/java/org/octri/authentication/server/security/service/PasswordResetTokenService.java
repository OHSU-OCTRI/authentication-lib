package org.octri.authentication.server.security.service;

import java.util.List;

import javax.annotation.Resource;

import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service wrapper for the {@link PasswordResetTokenRepository}.
 *
 * @author sams
 */
@Service
public class PasswordResetTokenService {

	@Resource
	private PasswordResetTokenRepository passwordResetTokenRepository;

	@Transactional(readOnly = true)
	public PasswordResetToken find(Long id) {
		return passwordResetTokenRepository.findOne(id);
	}

	@Transactional(readOnly = true)
	public PasswordResetToken findByToken(String token) {
		return passwordResetTokenRepository.findByToken(token);
	}

	@Transactional
	public PasswordResetToken save(PasswordResetToken passwordResetToken) {
		return passwordResetTokenRepository.save(passwordResetToken);
	}

	@Transactional(readOnly = true)
	public List<PasswordResetToken> findAll() {
		return (List<PasswordResetToken>) passwordResetTokenRepository.findAll();
	}

	@Transactional
	public void delete(Long id) {
		PasswordResetToken passwordResetToken = passwordResetTokenRepository.findOne(id);
		if (passwordResetToken != null) {
			passwordResetTokenRepository.delete(id);
		}
	}

}

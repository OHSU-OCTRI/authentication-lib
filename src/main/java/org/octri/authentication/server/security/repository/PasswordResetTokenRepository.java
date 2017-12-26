package org.octri.authentication.server.security.repository;

import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

/**
 * {@link JpaRepository} for manipulating {@link PasswordResetToken} entities.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

	public PasswordResetToken findByToken(@Param("token") String token);

}

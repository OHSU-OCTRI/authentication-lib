package org.octri.authentication.server.customizer;

import org.octri.authentication.server.security.service.PasswordResetTokenService;

/**
 * Default user management workflow.
 */
public class DefaultUserManagementCustomizer implements UserManagementCustomizer {

    PasswordResetTokenService passwordResetTokenService;

    /**
     * The passwordResetTokenService is unused, but demonstrates how we can clean up the Controller
     * by moving logic out that generates a password reset token after creation.
     *
     * @param passwordResetTokenService
     */
    public DefaultUserManagementCustomizer(PasswordResetTokenService passwordResetTokenService) {
        this.passwordResetTokenService = passwordResetTokenService;
    }

}

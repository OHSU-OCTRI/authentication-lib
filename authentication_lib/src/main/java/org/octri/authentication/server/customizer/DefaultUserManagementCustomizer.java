package org.octri.authentication.server.customizer;

import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.service.PasswordResetTokenService;

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

    @Override
    public String postCreateAction(User user) {
        return "redirect:/admin/user/list";
    }

    @Override
    public String postUpdateAction(User user) {
        return "redirect:/admin/user/list";
    }

}

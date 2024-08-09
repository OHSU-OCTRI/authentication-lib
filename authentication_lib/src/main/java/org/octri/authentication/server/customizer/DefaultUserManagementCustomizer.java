package org.octri.authentication.server.customizer;

import java.util.Optional;

import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.service.PasswordResetTokenService;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

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
    public Optional<ModelAndView> beforeSaveAction(User user, ModelMap model,
            HttpServletRequest request) {
        return Optional.empty();
    }

    @Override
    public ModelAndView postCreateAction(User user, ModelMap model,
            HttpServletRequest request) {
        return new ModelAndView("redirect:/admin/user/list");
    }

    @Override
    public ModelAndView postUpdateAction(User user, ModelMap model,
            HttpServletRequest request) {
        return new ModelAndView("redirect:/admin/user/list");
    }

}

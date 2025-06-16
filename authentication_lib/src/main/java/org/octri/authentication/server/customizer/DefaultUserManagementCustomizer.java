package org.octri.authentication.server.customizer;

import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.service.EmailNotificationService;
import org.octri.authentication.server.security.service.PasswordResetTokenService;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Default user management workflow.
 */
public class DefaultUserManagementCustomizer implements UserManagementCustomizer {

    private OctriAuthenticationProperties authenticationProperties;

    private UserService userService;

    private PasswordResetTokenService passwordResetTokenService;

    private EmailNotificationService emailNotificationService;

    /**
     * Constructor
     * 
     * @param authenticationProperties
     *            configuration properties
     * @param userService
     *            service used to manipulate user records
     * @param passwordResetTokenService
     *            service used to manage password reset token strings
     * @param emailNotificationService
     *            service used to send email notifications
     */
    public DefaultUserManagementCustomizer(OctriAuthenticationProperties authenticationProperties,
            UserService userService, PasswordResetTokenService passwordResetTokenService,
            EmailNotificationService emailNotificationService) {
        this.authenticationProperties = authenticationProperties;
        this.userService = userService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.emailNotificationService = emailNotificationService;
    }

    /**
     * Default post-create behavior. Sends account creation emails for new table-based user accounts, then redirects to
     * the user list page.
     *
     * @param user
     *            the newly-created user account
     * @param model
     *            data to use when rendering the view
     * @param request
     *            user form data
     * @return a {@link ModelAndView} that redirects to the user list page
     */
    @Override
    public ModelAndView postCreateAction(User user, ModelMap model, HttpServletRequest request) {
        Boolean tableBasedUser = authenticationProperties.getEnableTableBased() && user.isTableBasedUser();
        Boolean emailRequired = authenticationProperties.getEmailRequired();

        if (tableBasedUser) {
            PasswordResetToken token = passwordResetTokenService.generatePasswordResetToken(user);
            if (emailRequired) {
                emailNotificationService.sendPasswordResetTokenEmail(token, request, true);
            }
        }

        return new ModelAndView(UserManagementCustomizer.DEFAULT_REDIRECT);
    }

}

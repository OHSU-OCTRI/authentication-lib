package org.octri.authentication.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.server.customizer.DefaultUserManagementCustomizer;
import org.octri.authentication.server.customizer.UserManagementCustomizer;
import org.octri.authentication.server.security.service.EmailNotificationService;
import org.octri.authentication.server.security.service.PasswordResetTokenService;
import org.octri.authentication.server.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * A configuration class that provides a {@link UserManagementCustomizer} with default behavior, if the application has
 * not supplied a custom bean.
 */
@Configuration
public class UserManagementCustomizerConfiguration {

    private static final Log log = LogFactory.getLog(UserManagementCustomizerConfiguration.class);

    @Autowired
    private OctriAuthenticationProperties authenticationProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Bean
    @ConditionalOnMissingBean(UserManagementCustomizer.class)
    public UserManagementCustomizer defaultUserManagementCustomizer() {
        log.debug("Creating UserManagementCustomizer for default user management workflow");
        return new DefaultUserManagementCustomizer(authenticationProperties, userService, passwordResetTokenService,
                emailNotificationService);
    }
}
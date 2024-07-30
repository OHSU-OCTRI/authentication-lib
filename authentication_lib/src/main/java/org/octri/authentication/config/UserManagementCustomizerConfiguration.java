package org.octri.authentication.config;

import org.octri.authentication.server.customizer.DefaultUserManagementCustomizer;
import org.octri.authentication.server.customizer.UserManagementCustomizer;
import org.octri.authentication.server.security.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserManagementCustomizerConfiguration {

    @Autowired
    PasswordResetTokenService passwordResetTokenService;

    @Bean
    @ConditionalOnMissingBean(UserManagementCustomizer.class)
    public UserManagementCustomizer defaultUserManagementCustomizer() {
        return new DefaultUserManagementCustomizer(passwordResetTokenService);
    }
}
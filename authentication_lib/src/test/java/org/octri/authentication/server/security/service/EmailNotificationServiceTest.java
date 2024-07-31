package org.octri.authentication.server.security.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.authentication.config.EmailConfiguration;
import org.octri.authentication.server.security.AuthenticationUrlHelper;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.DuplicateEmailException;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    @InjectMocks
    private EmailNotificationService emailNotificationService;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Spy
    private AuthenticationUrlHelper urlHelper = new AuthenticationUrlHelper(BASE_URL, CONTEXT_PATH);

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailConfiguration emailConfig;

    @Mock
    private HttpServletRequest request;

    private static final String BASE_URL = "http://localhost:8080";
    private static final String CONTEXT_PATH = "/app";
    private User user;
    private PasswordResetToken passwordResetToken;

    @BeforeEach
    public void beforeEach() throws InvalidLdapUserDetailsException, DuplicateEmailException {
        user = user();
        passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        ReflectionTestUtils.setField(emailNotificationService, "dryRun", false);
    }

    private User user() {
        user = new User();
        user.setUsername("foo");
        user.setEmail("foo@example.com");
        return user;
    }

    @Test
    public void testSendPasswordResetTokenEmail() {
        when(emailConfig.getFrom()).thenReturn("foo@example.com");
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        emailNotificationService.sendPasswordResetTokenEmail(passwordResetToken, request, false);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    public void testSendPasswordResetEmailConfirmation() {
        when(emailConfig.getFrom()).thenReturn("foo@example.com");
        when(passwordResetTokenService.findByToken(any(String.class))).thenReturn(passwordResetToken);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));
        emailNotificationService.sendPasswordResetEmailConfirmation("mock token", request);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

}

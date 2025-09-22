package org.octri.authentication.server.security.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.AuthenticationUrlHelper;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.authentication.server.security.exception.DuplicateEmailException;
import org.octri.authentication.server.security.exception.InvalidLdapUserDetailsException;
import org.octri.messaging.service.MessageDeliveryService;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class EmailNotificationServiceTest {

    private EmailNotificationService emailNotificationService;

    @Mock
    private PasswordResetTokenService passwordResetTokenService;

    @Mock
    private OctriAuthenticationProperties authenticationProperties;

    @Spy
    private AuthenticationUrlHelper urlHelper = new AuthenticationUrlHelper(BASE_URL, CONTEXT_PATH);

    @Mock
    private MessageDeliveryService messageDeliveryService;

    @Mock
    private HttpServletRequest request;

    private static final String DISPLAY_NAME = "ExampleApp";
    private static final String SENDER_EMAIL = "exampleapp@example.com";
    private static final String USER_EMAIL = "foo@example.com";
    private static final String BASE_URL = "http://localhost:8080";
    private static final String CONTEXT_PATH = "/app";
    private static final String RESET_TOKEN = "6fd30a7e-00f0-44a6-a29c-29f4a0c3aef6";
    private static final String RESET_URL = BASE_URL + CONTEXT_PATH + "/user/password/reset?token=" + RESET_TOKEN;

    private User user;
    private PasswordResetToken passwordResetToken;

    @BeforeEach
    public void beforeEach() throws InvalidLdapUserDetailsException, DuplicateEmailException {
        user = user();
        passwordResetToken = new PasswordResetToken();
        passwordResetToken.setUser(user);
        passwordResetToken.setToken(RESET_TOKEN);
        when(authenticationProperties.getEmailDryRun()).thenReturn(false);
        emailNotificationService = new EmailNotificationService(DISPLAY_NAME, SENDER_EMAIL,
                authenticationProperties, urlHelper, messageDeliveryService, passwordResetTokenService);
    }

    private User user() {
        user = new User();
        user.setUsername("foo");
        user.setEmail(USER_EMAIL);
        return user;
    }

    @Test
    public void testSendPasswordResetTokenEmail() {
        var fromEmail = ArgumentCaptor.forClass(String.class);
        var toEmail = ArgumentCaptor.forClass(String.class);
        var messageSubject = ArgumentCaptor.forClass(String.class);
        var messageBody = ArgumentCaptor.forClass(String.class);

        when(messageDeliveryService.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        emailNotificationService.sendPasswordResetTokenEmail(passwordResetToken, request, false);

        verify(messageDeliveryService).sendEmail(fromEmail.capture(), toEmail.capture(), messageSubject.capture(),
                messageBody.capture());
        assertEquals(SENDER_EMAIL, fromEmail.getValue(), "The expected sender address should be used.");
        assertEquals(USER_EMAIL, toEmail.getValue(), "The message should be sent to the user's email address.");
        assertTrue(messageSubject.getValue().startsWith("Password reset request"),
                "The message has the expected subject.");
        assertTrue(messageBody.getValue().contains("To reset your password"),
                "The message body includes messaging for existing account.");
        assertTrue(messageBody.getValue().contains(RESET_URL), "The message includes the password reset URL.");
    }

    @Test
    public void testSendPasswordResetTokenEmailNewUser() {
        var fromEmail = ArgumentCaptor.forClass(String.class);
        var toEmail = ArgumentCaptor.forClass(String.class);
        var messageSubject = ArgumentCaptor.forClass(String.class);
        var messageBody = ArgumentCaptor.forClass(String.class);

        when(messageDeliveryService.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        emailNotificationService.sendPasswordResetTokenEmail(passwordResetToken, request, true);

        verify(messageDeliveryService).sendEmail(fromEmail.capture(), toEmail.capture(), messageSubject.capture(),
                messageBody.capture());
        assertEquals(SENDER_EMAIL, fromEmail.getValue(), "The expected sender address should be used.");
        assertEquals(USER_EMAIL, toEmail.getValue(), "The message should be sent to the user's email address.");
        assertTrue(messageSubject.getValue().startsWith("Welcome to " + DISPLAY_NAME),
                "The message has the welcome message subject.");
        assertTrue(messageBody.getValue().contains("An account has been created for you"),
                "The message body includes new account messaging.");
        assertTrue(messageBody.getValue().contains(RESET_URL), "The message includes the password reset URL.");
    }

    @Test
    public void testSendPasswordResetEmailConfirmation() {
        var fromEmail = ArgumentCaptor.forClass(String.class);
        var toEmail = ArgumentCaptor.forClass(String.class);
        var messageSubject = ArgumentCaptor.forClass(String.class);
        var messageBody = ArgumentCaptor.forClass(String.class);

        when(passwordResetTokenService.findByToken(RESET_TOKEN)).thenReturn(passwordResetToken);
        when(messageDeliveryService.sendEmail(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Optional.empty());

        emailNotificationService.sendPasswordResetEmailConfirmation(RESET_TOKEN, request);

        verify(messageDeliveryService).sendEmail(fromEmail.capture(), toEmail.capture(), messageSubject.capture(),
                messageBody.capture());
        assertEquals(SENDER_EMAIL, fromEmail.getValue(), "The expected sender address should be used.");
        assertEquals(USER_EMAIL, toEmail.getValue(),
                "The message should be sent to the expected user's email address.");
        assertTrue(messageSubject.getValue().endsWith("password was reset"),
                "The message has the expected subject.");
    }

}

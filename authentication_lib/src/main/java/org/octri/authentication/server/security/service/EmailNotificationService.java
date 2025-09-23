package org.octri.authentication.server.security.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.AuthenticationUrlHelper;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.octri.messaging.service.MessageDeliveryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This service is responsible for constructing emails to send to table-based users for account management
 */
@Service
public class EmailNotificationService {

    private static final Log log = LogFactory.getLog(EmailNotificationService.class);

    private final String displayName;
    private final String fromAddress;
    private final OctriAuthenticationProperties authenticationProperties;
    private final AuthenticationUrlHelper urlHelper;
    private final MessageDeliveryService messageDeliveryService;
    private final PasswordResetTokenService passwordResetTokenService;

    /**
     * Constructor.
     *
     * @param displayName
     *            application display name (injected into messages)
     * @param authenticationProperties
     *            configuration properties
     * @param urlHelper
     *            authentication URL helper
     * @param messageDeliveryService
     *            service used to deliver email messages
     * @param passwordResetTokenService
     *            password reset token service
     */
    public EmailNotificationService(@Value("${app.displayName}") String displayName,
            @Value("${spring.mail.from}") String fromAddress, OctriAuthenticationProperties authenticationProperties,
            AuthenticationUrlHelper urlHelper, MessageDeliveryService messageDeliveryService,
            PasswordResetTokenService passwordResetTokenService) {
        this.displayName = displayName;
        this.fromAddress = fromAddress;
        this.authenticationProperties = authenticationProperties;
        this.urlHelper = urlHelper;
        this.messageDeliveryService = messageDeliveryService;
        this.passwordResetTokenService = passwordResetTokenService;
    }

    /**
     * Send email confirmation to user. If the user is new, a welcome email is sent. Otherwise a password
     * reset is sent.
     *
     * @param token
     *            the password reset token
     * @param request
     *            the request so the application url can be constructed
     * @param isNewUser
     *            whether the user is new and should receive a welcome email instead of a password reset
     */
    public void sendPasswordResetTokenEmail(final PasswordResetToken token, final HttpServletRequest request,
            final boolean isNewUser) {

        final User user = token.getUser();
        final String resetPath = urlHelper.getPasswordResetUrl(token.getToken());
        String subject;
        String body;

        if (isNewUser) {
            subject = "Welcome to " + displayName;
            body = "Hello " + user.getFirstName() + ",\n\nAn account has been created for you with username "
                    + user.getUsername() + ". To set your password, please follow this link: " + resetPath;
        } else {
            subject = "Password reset request for " + displayName;
            body = "Hello " + user.getFirstName() + ",\n\nTo reset your password please follow this link: "
                    + resetPath + "\n\nIf you did not initiate this request, please contact your system administrator.";
        }

        checkAndSendEmail(fromAddress, user.getEmail(), subject, body, "password reset token");
    }

    /**
     * Send email confirmation to user.
     *
     * @param token
     *            the password reset token
     * @param request
     *            servlet request
     */
    public void sendPasswordResetEmailConfirmation(final String token, final HttpServletRequest request) {
        Assert.notNull(token, "Must provide a token");
        Assert.notNull(request, "Must provide an HttpServletRequest");

        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);
        Assert.notNull(passwordResetToken, "Could not find a user for the provided token");

        final String userEmail = passwordResetToken.getUser().getEmail();

        final String subject = "Your " + displayName + " password was reset";
        final String body = "Your password has been reset. You may now log into the application.\n\nUsername: "
                + passwordResetToken.getUser().getUsername()
                + "\nLink: " + urlHelper.getLoginUrl();

        checkAndSendEmail(fromAddress, userEmail, subject, body, "password reset confirmation");
    }

    private void checkAndSendEmail(final String fromAddress, final String toAddress, final String subject,
            final String body, final String messageType) {
        if (Boolean.TRUE.equals(authenticationProperties.getEmailDryRun())) {
            logDryRunEmail(fromAddress, toAddress, subject, body);
        } else if (StringUtils.isBlank(toAddress)) {
            if (Boolean.TRUE.equals(authenticationProperties.getEmailRequired())) {
                throw new IllegalArgumentException("Users must have an email address, but the user's email is blank.");
            }
        } else {
            messageDeliveryService.sendEmail(fromAddress, toAddress, subject, body);
            log.debug("Sent " + messageType + " email to " + toAddress);
        }
    }

    private static void logDryRunEmail(String fromAddress, String toAddress, String subject, String body) {
        final String deprecationMessage = "Setting octri.authentication.email-dry-run=true is deprecated. Use octri.messaging.email-delivery-strategy=LOG instead.";
        final String format = "DRY RUN, would have sent email to %s from %s with subject \"%s\" and contents \"%s\"";
        log.warn(deprecationMessage);
        log.info(String.format(format, String.join(", ", toAddress), fromAddress, subject,
                body));
    }

}

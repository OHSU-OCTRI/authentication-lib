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

        if (isEmailDryRunOrBlank(authenticationProperties.getEmailDryRun(), user.getEmail())) {
            logDryRunEmail(fromAddress, user.getEmail(), subject, body);
        } else {
            messageDeliveryService.sendEmail(fromAddress, user.getEmail(), subject, body);
            log.info("Password reset confirmation email sent to " + user.getEmail());
        }
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

        if (isEmailDryRunOrBlank(authenticationProperties.getEmailDryRun(), userEmail)) {
            logDryRunEmail(fromAddress, userEmail, subject, body);
        } else {
            messageDeliveryService.sendEmail(fromAddress, userEmail, subject, body);
            log.info("Password reset confirmation email sent to " + userEmail);
        }
    }

    private static boolean isEmailDryRunOrBlank(final boolean dryRun, final String toEmail) {
        if (dryRun) {
            logDeprecationWarning();
        }
        return dryRun || StringUtils.isBlank(toEmail);
    }

    private static void logDryRunEmail(String fromAddress, String toAddress, String subject, String body) {
        final String format = "DRY RUN, would have sent email to %s from %s with subject \"%s\" and contents \"%s\"";
        logDeprecationWarning();
        log.info(String.format(format, String.join(", ", toAddress), fromAddress, subject,
                body));
    }

    private static void logDeprecationWarning() {
        var message = "Setting octri.authentication.email-dry-run=true is deprecated. Use octri.messaging.email-delivery-strategy=LOG instead.";
        log.warn(message);
    }

}

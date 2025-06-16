package org.octri.authentication.server.security.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.octri.authentication.config.EmailConfiguration;
import org.octri.authentication.config.OctriAuthenticationProperties;
import org.octri.authentication.server.security.AuthenticationUrlHelper;
import org.octri.authentication.server.security.entity.PasswordResetToken;
import org.octri.authentication.server.security.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import jakarta.servlet.http.HttpServletRequest;

/**
 * This service is responsible for constructing emails to send to table-based users for account management
 */
@Service
public class EmailNotificationService {

    private static final Log log = LogFactory.getLog(EmailNotificationService.class);

    @Value("${app.displayName}")
    private String displayName;

    @Autowired
    OctriAuthenticationProperties authenticationProperties;

    @Autowired
    private AuthenticationUrlHelper urlHelper;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailConfiguration emailConfig;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

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

        User user = token.getUser();
        final String resetPath = urlHelper.getPasswordResetUrl(token.getToken());

        SimpleMailMessage email = new SimpleMailMessage();
        String body;
        if (isNewUser) {
            email.setSubject("Welcome to " + displayName);
            body = "Hello " + user.getFirstName() + ",\n\nAn account has been created for you with username "
                    + user.getUsername() + ". To set your password, please follow this link: " + resetPath;

        } else {
            email.setSubject("Password reset request for " + displayName);
            body = "Hello " + user.getFirstName() + ",\n\nTo reset your password please follow this link: "
                    + resetPath + "\n\nIf you did not initiate this request, please contact your system administrator.";
        }
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(emailConfig.getFrom());
        if (isEmailDryRunOrBlank(authenticationProperties.getEmailDryRun(), user.getEmail())) {
            logDryRunEmail(email);
        } else {
            mailSender.send(email);
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

        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Your " + displayName + " password was reset");
        final String body = "Your password has been reset. You may now log into the application.\n\nUsername: "
                + passwordResetToken.getUser().getUsername()
                + "\nLink: " + urlHelper.getLoginUrl();
        email.setText(body);
        email.setTo(userEmail);
        email.setFrom(emailConfig.getFrom());

        if (isEmailDryRunOrBlank(authenticationProperties.getEmailDryRun(), userEmail)) {
            logDryRunEmail(email);
        } else {
            mailSender.send(email);
            log.info("Password reset confirmation email sent to " + userEmail);
        }
    }

    private static boolean isEmailDryRunOrBlank(final boolean dryRun, final String toEmail) {
        return dryRun || StringUtils.isBlank(toEmail);
    }

    private static void logDryRunEmail(SimpleMailMessage message) {
        final String format = "DRY RUN, would have sent email to %s from %s with subject \"%s\" and contents \"%s\"";
        log.info(String.format(format, String.join(", ", message.getTo()), message.getFrom(), message.getSubject(),
                message.getText()));
    }

}

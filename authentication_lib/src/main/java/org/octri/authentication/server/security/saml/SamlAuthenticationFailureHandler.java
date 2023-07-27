package org.octri.authentication.server.security.saml;

import java.io.IOException;
import java.util.Date;

import org.octri.authentication.RequestUtils;
import org.octri.authentication.server.security.entity.LoginAttempt;
import org.octri.authentication.server.security.service.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.saml2.core.Saml2Error;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Records failed SAML login attempts. Because invalid credentials are detected by the SAML IdP, only login failures
 * that occur after the SAML response is received are recorded.
 */
@Component
public class SamlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private LoginAttemptService loginAttemptService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		recordLoginFailure(request, exception);
		super.onAuthenticationFailure(request, response, exception);
	}

	private void recordLoginFailure(HttpServletRequest request, AuthenticationException exception) {
		LoginAttempt attempt = new LoginAttempt();

		// username isn't available here, but it is included in the error message
		attempt.setUsername("");
		attempt.setAttemptedAt(new Date());
		attempt.setIpAddress(RequestUtils.getClientIpAddr(request));
		attempt.setSuccessful(false);

		if (exception instanceof Saml2AuthenticationException) {
			Saml2Error error = ((Saml2AuthenticationException) exception).getSaml2Error();
			attempt.setErrorType(error.getErrorCode());
			attempt.setErrorMessage(error.getDescription());
		} else {
			attempt.setErrorType(exception.getMessage());
		}

		loginAttemptService.save(attempt);
	}

}

package org.octri.authentication.config;

/**
 * Holds reusable deprecation messages.
 */
public class DeprecationMessages {

	/**
	 * Warns about the deprecated <code>octri.authentication.email-dry-run</code> property.
	 */
	public static final String EMAIL_DRY_RUN = "Setting octri.authentication.email-dry-run=true is deprecated. Use octri.messaging.email-delivery-strategy=LOG instead.";

	/**
	 * Warns about the deprecated <code>spring.mail.from</code> property.
	 */
	public static final String SPRING_MAIL_FROM = "Setting spring.mail.from is deprecated. Use octri.authentication.account-message-email instead.";

}

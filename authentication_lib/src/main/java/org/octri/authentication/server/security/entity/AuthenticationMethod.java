package org.octri.authentication.server.security.entity;

import org.octri.authentication.server.view.Labelled;

/**
 * Enumeration of methods that may be used to authenticate a user.
 */
public enum AuthenticationMethod implements Labelled {

	LDAP("LDAP"), SAML("SAML"), TABLE_BASED("Table Based");

	private String label;

	AuthenticationMethod(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

}

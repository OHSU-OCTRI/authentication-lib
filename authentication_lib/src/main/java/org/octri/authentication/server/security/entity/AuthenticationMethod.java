package org.octri.authentication.server.security.entity;

import org.octri.authentication.server.view.Labelled;

/**
 * Enumeration of methods that may be used to authenticate a user.
 */
public enum AuthenticationMethod implements Labelled {

	/**
	 * Lightweight Directory Access Protocol (LDAP) authentication
	 */
	LDAP("LDAP"),

	/**
	 * Security Assertion Markup Language (SAML) 2.0 authentication
	 */
	SAML("SAML"),

	/**
	 * Table-based authentication - password hash is stored in the database
	 */
	TABLE_BASED("Table Based");

	private String label;

	AuthenticationMethod(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return this.label;
	}

}

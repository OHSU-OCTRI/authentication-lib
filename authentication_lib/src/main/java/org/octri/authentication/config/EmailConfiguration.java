package org.octri.authentication.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Email configuration properties.
 *
 * @author sams
 */
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfiguration {

	private Boolean enabled;
	private String from;
	private String defaultEncoding;
	private String host;
	private Integer port;
	private String protocol;
	private String testConnection;
	private String username;
	private String password;

	/**
	 * Gets whether email support is enabled.
	 * 
	 * @return true if email is enabled
	 */
	public Boolean getEnabled() {
		return enabled;
	}

	/**
	 * Sets whether to enable email support
	 *
	 * @param enabled
	 *            true to enable email support, false to disable it
	 */
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Gets the configured from email address.
	 * 
	 * @return the configured from email address
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * Sets the from email address that messages will come from.
	 * 
	 * @param from
	 *            the from email address
	 */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * Gets the configured default MimeMessage character encoding.
	 * 
	 * @return the configured MimeMessage character encoding
	 */
	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	/**
	 * Sets the default MimeMessage encoding.
	 * 
	 * @param defaultEncoding
	 *            the default MimeMessage character encoding
	 */
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	/**
	 * Gets the configured mail server host.
	 * 
	 * @return the configured mail server host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Sets the mail server host.
	 * 
	 * @param host
	 *            mail server host
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Gets the configured mail server port.
	 * 
	 * @return the configure mail port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * Sets the mail server port.
	 * 
	 * @param port
	 *            the mail server port
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * Gets the configured protocol used by the mail server.
	 * 
	 * @return the configured mail server protocol
	 */
	public String getProtocol() {
		return protocol;
	}

	/**
	 * Sets the protocol used by the mail server.
	 * 
	 * @param protocol
	 *            the mail server protocol.
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	/**
	 * Gets whether connection to the mail server is checked at startup.
	 * 
	 * @return true if the connection is tested
	 */
	public String getTestConnection() {
		return testConnection;
	}

	/**
	 * Sets whether to test the connection to the mail server at startup.
	 *
	 * @param testConnection
	 *            true if the connection should be tested, false if not
	 */
	public void setTestConnection(String testConnection) {
		this.testConnection = testConnection;
	}

	/**
	 * Gets the username used when connecting to the mail server.
	 * 
	 * @return mail server username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username used when connecting to the mail server.
	 * 
	 * @param username
	 *            mail server username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password used when connecting to the mail server.
	 * 
	 * @return the mail server password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password to use when connecting to the mail server.
	 * 
	 * @param password
	 *            the mail server password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "EmailConfiguration [enabled=" + enabled + ", from=" + from + ", defaultEncoding=" + defaultEncoding
				+ ", host=" + host + ", port=" + port + ", protocol=" + protocol + ", testConnection=" + testConnection
				+ ", username=" + username + ", password=" + password + "]";
	}

}

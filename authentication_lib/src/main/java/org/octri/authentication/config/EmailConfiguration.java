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

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getDefaultEncoding() {
		return defaultEncoding;
	}

	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getTestConnection() {
		return testConnection;
	}

	public void setTestConnection(String testConnection) {
		this.testConnection = testConnection;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

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

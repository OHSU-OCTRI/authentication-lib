package org.octri.authentication.server.security.saml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.octri.authentication.config.SamlProperties;
import org.octri.authentication.server.security.SecurityHelper.Role;
import org.octri.authentication.server.security.entity.User;
import org.opensaml.saml.saml2.core.NameID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

@ExtendWith(MockitoExtension.class)
public class ApplicationSaml2AuthenticatedPrincipalTest {

	private SamlProperties samlProperties = new SamlProperties();

	@Mock
	NameID nameId;

	@Test
	public void testConstructorCopiesUserProperties() {
		User user = buildTestUser();
		Collection<? extends GrantedAuthority> authorities = buildTestAuthorities();
		Map<String, List<Object>> attributes = buildTestAttributes();

		ApplicationSaml2AuthenticatedPrincipal principal = new ApplicationSaml2AuthenticatedPrincipal(user, authorities,
				nameId, attributes);

		assertEquals(user.getId(), principal.getUserId());
		assertEquals(user.getUsername(), principal.getUsername());
		assertEquals(user.getFirstName(), principal.getFirstName());
		assertEquals(user.getLastName(), principal.getLastName());
		assertEquals(user.getEmail(), principal.getEmail());
	}

	private User buildTestUser() {
		User testUser = new User();
		testUser.setId(42L);
		testUser.setUsername("example");
		testUser.setFirstName("Example");
		testUser.setLastName("User");
		testUser.setEmail("example@example.com");
		testUser.setInstitution("example");

		return testUser;
	}

	private Collection<? extends GrantedAuthority> buildTestAuthorities() {
		return AuthorityUtils.createAuthorityList(Role.ROLE_USER.name());
	}

	private Map<String, List<Object>> buildTestAttributes() {
		Map<String, List<Object>> testAttributes = new HashMap<>();

		testAttributes.put(samlProperties.getUseridAttribute(), List.of("example"));
		testAttributes.put(samlProperties.getEmailAttribute(), List.of("example@example.com"));
		testAttributes.put(samlProperties.getFirstNameAttribute(), List.of("Example"));
		testAttributes.put(samlProperties.getLastNameAttribute(), List.of("User"));
		testAttributes.put(samlProperties.getGroupAttribute(), List.of("Example_LDAP_Group"));

		return testAttributes;
	}

}

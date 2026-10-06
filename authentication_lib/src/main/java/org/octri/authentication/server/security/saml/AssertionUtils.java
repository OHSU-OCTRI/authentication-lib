package org.octri.authentication.server.security.saml;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.XSURI;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Utility methods for working with SAML assertions.
 */
public class AssertionUtils {

	/**
	 * Extracts the first value of the given attribute from the SAML assertion's attribute map.
	 *
	 * @param attributes
	 *            - assertion attributes as returned by getAssertionAttributes
	 * @param attributeKey
	 *            - attribute key
	 * @return the attribute value
	 */
	public static String getAttributeValue(Map<String, List<Object>> attributes, String attributeKey) {
		return (String) CollectionUtils.firstElement(attributes.get(attributeKey));
	}

	/**
	 * Extracted from Spring Security's {@code BaseOpenSamlAuthenticationProvider}.
	 *
	 * @see <a href=
	 *      "https://github.com/spring-projects/spring-security/blob/7.0.x/saml2/saml2-service-provider/src/main/java/org/springframework/security/saml2/provider/service/authentication/BaseOpenSamlAuthenticationProvider.java">BaseOpenSamlAuthenticationProvider
	 *      code</a>
	 * @param assertion
	 *            SAML assertion
	 * @return a map containing the assertion's attributes
	 */
	public static Map<String, List<Object>> getAssertionAttributes(Assertion assertion) {
		MultiValueMap<String, Object> attributeMap = new LinkedMultiValueMap<>();
		for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
			for (Attribute attribute : attributeStatement.getAttributes()) {
				List<Object> attributeValues = new ArrayList<>();
				for (XMLObject xmlObject : attribute.getAttributeValues()) {
					Object attributeValue = getXmlObjectValue(xmlObject);
					if (attributeValue != null) {
						attributeValues.add(attributeValue);
					}
				}
				attributeMap.addAll(attribute.getName(), attributeValues);
			}
		}
		return new LinkedHashMap<>(attributeMap);
	}

	/**
	 * Extracted from Spring Security's {@code BaseOpenSamlAuthenticationProvider}.
	 *
	 * @see <a href=
	 *      "https://github.com/spring-projects/spring-security/blob/7.0.x/saml2/saml2-service-provider/src/main/java/org/springframework/security/saml2/provider/service/authentication/BaseOpenSamlAuthenticationProvider.java">BaseOpenSamlAuthenticationProvider
	 *      code</a>
	 * @param xmlObject
	 *            XML node
	 * @return the node's value
	 */
	private static Object getXmlObjectValue(XMLObject xmlObject) {
		if (xmlObject instanceof XSAny any) {
			return any.getTextContent();
		}
		if (xmlObject instanceof XSString string) {
			return string.getValue();
		}
		if (xmlObject instanceof XSInteger integer) {
			return integer.getValue();
		}
		if (xmlObject instanceof XSURI rI) {
			return rI.getURI();
		}
		if (xmlObject instanceof XSBoolean boolean1) {
			XSBooleanValue xsBooleanValue = boolean1.getValue();
			return (xsBooleanValue != null) ? xsBooleanValue.getValue() : null;
		}
		if (xmlObject instanceof XSDateTime time) {
			return time.getValue();
		}
		return xmlObject;
	}
}

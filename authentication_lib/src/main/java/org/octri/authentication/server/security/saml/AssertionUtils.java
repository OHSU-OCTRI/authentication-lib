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
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.util.CollectionUtils;

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
	 * @return
	 */
	public static String getAttributeValue(Map<String, List<Object>> attributes, String attributeKey) {
		return (String) CollectionUtils.firstElement(attributes.get(attributeKey));
	}

	/**
	 * Extracted from Spring Security's {@link OpenSaml4AuthenticationProvider}.
	 *
	 * @see https://github.com/spring-projects/spring-security/blob/5.6.x/saml2/saml2-service-provider/src/opensaml4Main/java/org/springframework/security/saml2/provider/service/authentication/OpenSaml4AuthenticationProvider.java
	 * @param assertion
	 * @return
	 */
	public static Map<String, List<Object>> getAssertionAttributes(Assertion assertion) {
		Map<String, List<Object>> attributeMap = new LinkedHashMap<>();
		for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
			for (Attribute attribute : attributeStatement.getAttributes()) {
				List<Object> attributeValues = new ArrayList<>();
				for (XMLObject xmlObject : attribute.getAttributeValues()) {
					Object attributeValue = getXmlObjectValue(xmlObject);
					if (attributeValue != null) {
						attributeValues.add(attributeValue);
					}
				}
				attributeMap.put(attribute.getName(), attributeValues);
			}
		}
		return attributeMap;
	}

	/**
	 * Extracted from Spring Security's {@link OpenSaml4AuthenticationProvider}.
	 *
	 * @see https://github.com/spring-projects/spring-security/blob/5.6.x/saml2/saml2-service-provider/src/opensaml4Main/java/org/springframework/security/saml2/provider/service/authentication/OpenSaml4AuthenticationProvider.java
	 * @param xmlObject
	 * @return
	 */
	private static Object getXmlObjectValue(XMLObject xmlObject) {
		if (xmlObject instanceof XSAny) {
			return ((XSAny) xmlObject).getTextContent();
		}
		if (xmlObject instanceof XSString) {
			return ((XSString) xmlObject).getValue();
		}
		if (xmlObject instanceof XSInteger) {
			return ((XSInteger) xmlObject).getValue();
		}
		if (xmlObject instanceof XSURI) {
			return ((XSURI) xmlObject).getURI();
		}
		if (xmlObject instanceof XSBoolean) {
			XSBooleanValue xsBooleanValue = ((XSBoolean) xmlObject).getValue();
			return (xsBooleanValue != null) ? xsBooleanValue.getValue() : null;
		}
		if (xmlObject instanceof XSDateTime) {
			return ((XSDateTime) xmlObject).getValue();
		}
		return null;
	}
}

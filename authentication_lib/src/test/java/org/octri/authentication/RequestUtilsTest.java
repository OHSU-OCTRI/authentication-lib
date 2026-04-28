package org.octri.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class RequestUtilsTest {

	private static final String IP = "192.168.1.100";

	@Mock
	private HttpServletRequest mockRequest;

	@Test
	public void testNullRequestThrowsException() {
		assertThrows(IllegalArgumentException.class, () -> RequestUtils.getClientIpAddr(null),
				"Null request should throw IllegalArgumentException");
	}

	@Test
	public void testReturnsXForwardedForHeader() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest), "Should return X-Forwarded-For header value");
	}

	@Test
	public void testReturnsProxyClientIpHeader() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
		when(mockRequest.getHeader("Proxy-Client-IP")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest), "Should return Proxy-Client-IP header value");
	}

	@Test
	public void testReturnsWlProxyClientIpHeader() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
		when(mockRequest.getHeader("Proxy-Client-IP")).thenReturn(null);
		when(mockRequest.getHeader("WL-Proxy-Client-IP")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest), "Should return WL-Proxy-Client-IP header value");
	}

	@Test
	public void testReturnsHttpClientIpHeader() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
		when(mockRequest.getHeader("Proxy-Client-IP")).thenReturn(null);
		when(mockRequest.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
		when(mockRequest.getHeader("HTTP_CLIENT_IP")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest), "Should return HTTP_CLIENT_IP header value");
	}

	@Test
	public void testReturnsHttpXForwardedForHeader() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
		when(mockRequest.getHeader("Proxy-Client-IP")).thenReturn(null);
		when(mockRequest.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
		when(mockRequest.getHeader("HTTP_CLIENT_IP")).thenReturn(null);
		when(mockRequest.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest), "Should return HTTP_X_FORWARDED_FOR header value");
	}

	@Test
	public void testFallsBackToRemoteAddr() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(null);
		when(mockRequest.getHeader("Proxy-Client-IP")).thenReturn(null);
		when(mockRequest.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
		when(mockRequest.getHeader("HTTP_CLIENT_IP")).thenReturn(null);
		when(mockRequest.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn(null);
		when(mockRequest.getRemoteAddr()).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest),
				"Should fall back to getRemoteAddr() when no headers are set");
	}

	@Test
	public void testUnknownXForwardedForFallsThrough() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn("unknown");
		when(mockRequest.getHeader("Proxy-Client-IP")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest),
				"'unknown' X-Forwarded-For should fall through to Proxy-Client-IP");
	}

	@Test
	public void testEmptyXForwardedForFallsThrough() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn("");
		when(mockRequest.getHeader("Proxy-Client-IP")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest),
				"Empty X-Forwarded-For should fall through to Proxy-Client-IP");
	}

	@Test
	public void testUnknownCaseInsensitive() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn("UNKNOWN");
		when(mockRequest.getHeader("Proxy-Client-IP")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest),
				"'unknown' check should be case-insensitive");
	}

	@Test
	public void testXForwardedForTakesPriorityOverOthers() {
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(IP);
		assertEquals(IP, RequestUtils.getClientIpAddr(mockRequest),
				"X-Forwarded-For should take priority over all other headers");
	}

	@Test
	public void testXForwardedForIpListReturnsFirstIpAddress() {
		var ipV4List = "203.0.113.10, 10.0.0.5, 172.16.0.2";
		var expectedIpV4 = "203.0.113.10";
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(ipV4List);
		assertEquals(expectedIpV4, RequestUtils.getClientIpAddr(mockRequest),
				"Should return the first IP address in a comma-separated list of addresses");

		var mixedIpList = "2603:3004:88c:0:c00a:6668:b90e:c1,139.60.24.135";
		var expectedIpV6 = "2603:3004:88c:0:c00a:6668:b90e:c1";
		when(mockRequest.getHeader("X-Forwarded-For")).thenReturn(mixedIpList);
		assertEquals(expectedIpV6, RequestUtils.getClientIpAddr(mockRequest),
				"Should return the first IP address in a list of mixed IPv4 and IPv6 addresses");
	}

}

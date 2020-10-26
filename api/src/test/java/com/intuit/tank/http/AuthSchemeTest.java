package com.intuit.tank.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Purpose: Test AuthScheme
 *
 * @author : atayal
 **/
public class AuthSchemeTest {

	@Test
	void testGetScheme() {
		AuthScheme scheme = AuthScheme.getScheme("BASIC");
		Assertions.assertNotNull(scheme);
		assert scheme == AuthScheme.Basic;
		assertEquals("BASIC", scheme.getRepresentation());
	}
}

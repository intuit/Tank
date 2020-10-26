package com.intuit.tank.logging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Purpose: Test Enum SourceType
 *
 * @author : atayal
 **/
public class SourceTypeTest {
	@Test
	void testSourceTypeEnum() {
		SourceType type = SourceType.valueOf("controller");
		Assertions.assertNotNull(type);
		assert SourceType.controller == type;
	}
}

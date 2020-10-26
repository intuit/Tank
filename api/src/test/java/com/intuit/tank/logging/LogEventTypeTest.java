package com.intuit.tank.logging;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Purpose: Test LogEventType Enum
 *
 * @author : atayal
 **/
public class LogEventTypeTest {

	@Test
	void testLogEventTypeEnum() {
		LogEventType type = LogEventType.valueOf("System");
		Assertions.assertNotNull(type);
		assert LogEventType.System == type;
	}
}

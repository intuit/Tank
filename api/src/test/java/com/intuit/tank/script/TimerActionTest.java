package com.intuit.tank.script;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Purpose: Test RequestDataType Enum
 *
 * @author : atayal
 **/
public class TimerActionTest {

	@Test
	void testTimerActionEnum() {
		TimerAction type = TimerAction.valueOf("START");
		Assertions.assertNotNull(type);
		assert TimerAction.START == type;
	}
}

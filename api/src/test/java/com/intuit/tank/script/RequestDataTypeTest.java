package com.intuit.tank.script;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Purpose: Test RequestDataType Enum
 *
 * @author : atayal
 **/
public class RequestDataTypeTest {

	@Test
	void testRequestDataTypeEnum() {
		RequestDataType type = RequestDataType.valueOf("responseContent");
		Assertions.assertNotNull(type);
		assert RequestDataType.responseContent == type;
	}
}

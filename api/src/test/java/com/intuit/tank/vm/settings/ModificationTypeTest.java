package com.intuit.tank.vm.settings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Purpose: Test ModificationType Enum
 *
 * @author : atayal
 **/
public class ModificationTypeTest {

	@Test
	void testModificationTypeEnum() {
		ModificationType type = ModificationType.valueOf("ADD");
		Assertions.assertNotNull(type);
		assert ModificationType.ADD == type;
	}
}

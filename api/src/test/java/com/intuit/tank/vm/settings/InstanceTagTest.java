package com.intuit.tank.vm.settings;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Purpose: test InstanceTag class
 *
 * @author : atayal
 **/
public class InstanceTagTest {
	@Test
	void testInstanceTag() {
		InstanceTag tag = new InstanceTag("tag1", "val1");
		InstanceTag tag2 = new InstanceTag("tag2", "val2");
		Assertions.assertEquals("tag1", tag.getName());
		Assertions.assertEquals("val1", tag.getValue());
		Map<InstanceTag, Integer> tags = new LinkedHashMap<>();
		tags.put(tag, 1);
		tags.put(tag2, 2);
		assert tag != tag2;
		Assertions.assertNotEquals(tag, tag2);
		Assertions.assertNotEquals(tag, "");
		Assertions.assertEquals("tag1 = val1", tag.toString());

	}
}

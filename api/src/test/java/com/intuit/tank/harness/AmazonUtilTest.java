package com.intuit.tank.harness;

import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Purpose: Test AmazonUtil class utility methods
 * @author : atayal
 **/
public class AmazonUtilTest {

	@Inject
	private AmazonUtil amazonUtil;

	@Test
	void testGetVMRegion() {
		VMRegion vmRegion = AmazonUtil.getVMRegion();
		Assertions.assertNotNull(vmRegion);
		assertEquals("Standalone Agent", vmRegion.getDescription());
		assertEquals("standalone-agent", vmRegion.getRegion());
	}

	@Test
	void testIsInAmazon() {
		boolean inAmazon = AmazonUtil.isInAmazon();
		assertFalse(inAmazon);
	}

	@Test
	void testGetZone() {
		String zone = AmazonUtil.getZone();
		Assertions.assertNotNull(zone);
		assertEquals("unknown", zone);
	}

	@Test
	void testGetInstanceId() {
		String instanceId = AmazonUtil.getInstanceId();
		Assertions.assertNotNull(instanceId);
		assertEquals("", instanceId);
	}

	@Test
	void testGetLoggingProfile() {
		LoggingProfile loggingProfile = AmazonUtil.getLoggingProfile();
		Assertions.assertNotNull(loggingProfile);
		assertEquals("Standard", loggingProfile.getDisplayName());
		assertEquals("Logs common fields.", loggingProfile.getDescription());
	}

	@Test
	void testGetCapacity() {
		int capacity = AmazonUtil.getCapacity();
		assertEquals(4000, capacity);
	}

	@Test
	void testGetStopBehavior() {
		StopBehavior stopBehavior = AmazonUtil.getStopBehavior();
		Assertions.assertNotNull(stopBehavior);
		assertEquals("Script Group", stopBehavior.getDisplay());
		assertEquals("Stop after current script group completes.", stopBehavior.getDescription());
	}

	@Test
	void testUsingEip() {
		boolean usingEip = AmazonUtil.usingEip();
		assertFalse(usingEip);
	}

	@Test
	void testGetControllerBaseUrl() {
		String url = AmazonUtil.getControllerBaseUrl();
		Assertions.assertNotNull(url);
		assertEquals("http://localhost:8080/", url);
	}

	@Test
	void testGetUserDataAsMap() {
		Object o = AmazonUtil.getUserDataAsMap();
		Assertions.assertNotNull(o);
	}

}

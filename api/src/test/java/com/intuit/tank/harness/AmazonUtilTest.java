package com.intuit.tank.harness;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Purpose: Test AmazonUtil class utility methods
 * @author : atayal
 **/
public class AmazonUtilTest {

	private WireMockServer wireMockServer;

	@BeforeEach
	public void setup() {
		wireMockServer = new WireMockServer(WireMockConfiguration.options().dynamicPort());
		wireMockServer.start();
		wireMockServer.resetAll();
		configureFor("localhost", wireMockServer.port());
		AmazonUtil.BASE = wireMockServer.baseUrl();
	}

	@AfterEach
	public void teardown() {
		wireMockServer.stop();
	}

	@Test
	void testGetVMRegion_standalone() {
		VMRegion vmRegion = AmazonUtil.getVMRegion();
		assertNotNull(vmRegion);
		assertEquals("Standalone Agent", vmRegion.getDescription());
		assertEquals("standalone-agent", vmRegion.getRegion());
	}

	@Test
	void testIsInAmazon_standalone() {
		boolean inAmazon = AmazonUtil.isInAmazon();
		assertFalse(inAmazon);
	}

	@Test
	void testIsInAmazon() {
		stubFor(get(AmazonUtil.META_DATA + "/placement/availability-zone").willReturn(ok("us-east-2")));
		boolean inAmazon = AmazonUtil.isInAmazon();
		assertTrue(inAmazon);
	}

	@Test
	void testGetZone_standalone() {
		String zone = AmazonUtil.getZone();
		assertNotNull(zone);
		assertEquals("unknown", zone);
	}

	@Test
	void testGetZone() {
		stubFor(get(AmazonUtil.META_DATA + "/placement/availability-zone").willReturn(ok("us-east-2")));
		String zone = AmazonUtil.getZone();
		assertNotNull(zone);
		assertEquals("us-east-2", zone);
	}

	@Test
	void testGetInstanceId_standalone() {
		String instanceId = AmazonUtil.getInstanceId();
		assertNotNull(instanceId);
		assertEquals("", instanceId);
	}

	@Test
	void testGetInstanceId() {
		stubFor(get(AmazonUtil.META_DATA + "/instance-id").willReturn(ok("i-123456789")));
		String instanceId = AmazonUtil.getInstanceId();
		assertNotNull(instanceId);
		assertEquals("i-123456789", instanceId);
	}

	@Test
	void testGetLoggingProfile_standalone() {
		LoggingProfile loggingProfile = AmazonUtil.getLoggingProfile();
		assertNotNull(loggingProfile);
		assertEquals("Standard", loggingProfile.getDisplayName());
		assertEquals("Logs common fields.", loggingProfile.getDescription());
	}

	@Test
	void testGetCapacity_standalone() {
		int capacity = AmazonUtil.getCapacity();
		assertEquals(4000, capacity);
	}

	@Test
	void testGetStopBehavior_standalone() {
		StopBehavior stopBehavior = AmazonUtil.getStopBehavior();
		assertNotNull(stopBehavior);
		assertEquals("Script Group", stopBehavior.getDisplay());
		assertEquals("Stop after current script group completes.", stopBehavior.getDescription());
	}

	@Test
	void testUsingEip_standalone() {
		boolean usingEip = AmazonUtil.usingEip();
		assertFalse(usingEip);
	}

	@Test
	void testGetControllerBaseUrl_standalone() {
		String url = AmazonUtil.getControllerBaseUrl();
		assertNotNull(url);
		assertEquals("http://localhost:8080/", url);
	}

	@Test
	void testGetControllerBaseUrl() {
		stubFor(get(AmazonUtil.USER_DATA).willReturn(okJson("{\"controllerUrl\": \"https://tank.intuit.com:8080/\"}")));
		String url = AmazonUtil.getControllerBaseUrl();
		assertNotNull(url);
		assertEquals("https://tank.intuit.com:8080/", url);
	}

	@Test
	void testGetUserDataAsMap_standalone() {
		Map<String, String> map = AmazonUtil.getUserDataAsMap();
		assertNotNull(map);
		assertTrue(map.isEmpty());
	}

	@Test
	void testGetUserDataAsMap() {
		stubFor(get(AmazonUtil.USER_DATA).willReturn(okJson("{\"jobId\": \"123456\", \"projectName\": \"TestProject\", \"controllerUrl\": \"https://tank.intuit.com:8080/\"}")));
		Map<String, String> map = AmazonUtil.getUserDataAsMap();
		assertNotNull(map);
		assertEquals(3, map.size());
	}

	@Test
	void testGetJobId_standalone() {
		String jobId = AmazonUtil.getJobId();
		assertNotNull(jobId);
		assertEquals("unknown", jobId);
	}

	@Test
	void testGetJobId() {
		stubFor(get(AmazonUtil.USER_DATA).willReturn(okJson("{\"jobId\": \"123456\"}")));
		String jobId = AmazonUtil.getJobId();
		assertNotNull(jobId);
		assertEquals("123456", jobId);
	}

	@Test
	void testGetProjectName_standalone() {
		String project = AmazonUtil.getProjectName();
		assertNotNull(project);
		assertEquals("unknown", project);
	}

	@Test
	void testGetProjectName() {
		stubFor(get(AmazonUtil.USER_DATA).willReturn(okJson("{\"projectName\": \"TestProject\"}")));
		String project = AmazonUtil.getProjectName();
		assertNotNull(project);
		assertEquals("TestProject", project);
	}
}

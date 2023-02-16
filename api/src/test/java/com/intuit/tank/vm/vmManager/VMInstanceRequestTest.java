package com.intuit.tank.vm.vmManager;

import com.intuit.tank.vm.api.enumerated.*;
import com.intuit.tank.vm.settings.InstanceDescription;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class VMInstanceRequestTest {

    private VMInstanceRequest EmptyInstanceRequest = null;
    private VMInstanceRequest InstanceRequest = null;

    private final VMProvider provider = VMProvider.Amazon;
    private final VMRegion region = VMRegion.US_WEST_2;
    private final VMImageType image = VMImageType.AGENT;
    private final InstanceDescription description = new InstanceDescription(null, null);

    @BeforeEach
    public void init() {
        EmptyInstanceRequest = new VMInstanceRequest();
        InstanceRequest = new VMInstanceRequest(provider, region, "testType",
                image, 23, false, "testZone", description);
    }

    @Test
    public void testBuild() {
        assertNotNull(EmptyInstanceRequest);
        assertNotNull(InstanceRequest);
    }

    @Test
    public void testIsUseEips() {
        assertFalse(EmptyInstanceRequest.isUseEips());
        assertFalse(InstanceRequest.isUseEips());
        InstanceRequest.setUserEips(true);
        assertTrue(InstanceRequest.isUseEips());
    }

    @Test
    public void testInstanceDescription() {
        assertNull(EmptyInstanceRequest.getInstanceDescription());
        assertEquals(description, InstanceRequest.getInstanceDescription());
        InstanceDescription newDescription = new InstanceDescription(null, null);
        InstanceRequest.setInstanceDescription(newDescription);
        assertEquals(newDescription, InstanceRequest.getInstanceDescription());
    }

    @Test
    public void testSubnetId() {
        assertNull(EmptyInstanceRequest.getSubnetId());
        EmptyInstanceRequest.setSubnetId("testSubnet");
        assertEquals("testSubnet", EmptyInstanceRequest.getSubnetId());
        assertNull(InstanceRequest.getSubnetId());
        InstanceRequest.setSubnetId("testSubnet");
        assertEquals("testSubnet", InstanceRequest.getSubnetId());
    }

    @Test
    public void testReuseStoppedInstance() {
        assertFalse(EmptyInstanceRequest.getReuseStoppedInstance());
        EmptyInstanceRequest.setReuseStoppedInstance(true);
        assertTrue(EmptyInstanceRequest.getReuseStoppedInstance());

        assertFalse(InstanceRequest.getReuseStoppedInstance());
        InstanceRequest.setReuseStoppedInstance(true);
        assertTrue(InstanceRequest.getReuseStoppedInstance());
    }

    @Test
    public void testZone() {
        assertNull(EmptyInstanceRequest.getZone());
        EmptyInstanceRequest.setZone("testZone");
        assertEquals("testZone", EmptyInstanceRequest.getZone());

        assertEquals("testZone", InstanceRequest.getZone());
        InstanceRequest.setZone("newZone");
        assertEquals("newZone", InstanceRequest.getZone());
    }


    @Test
    public void testAssociatedIps() {
        Set<String> expected =  new HashSet<String>();
        assertEquals(expected, EmptyInstanceRequest.getAssociatedIps());
        EmptyInstanceRequest.addAssociatedIp("ipAddress");
        expected.add("ipAddress");
        assertEquals(expected, EmptyInstanceRequest.getAssociatedIps());

        Set<String> expected2 =  new HashSet<String>();
        assertEquals(expected2, InstanceRequest.getAssociatedIps());
        InstanceRequest.addAssociatedIp("ipAddress");
        expected2.add("ipAddress");
        assertEquals(expected2, InstanceRequest.getAssociatedIps());
    }

    @Test
    public void testUserData() {
        Map<String, String> expected = new HashMap<>();
        assertEquals(expected, EmptyInstanceRequest.getUserData());
        expected.put("testKey", "testValue");
        EmptyInstanceRequest.addUserData("testKey", "testValue");
        assertEquals(expected, EmptyInstanceRequest.getUserData());

        Map<String, String> expected2 = new HashMap<>();
        assertEquals(expected2, InstanceRequest.getUserData());
        expected2.put("testKey", "testValue");
        InstanceRequest.addUserData("testKey", "testValue");
        assertEquals(expected2, InstanceRequest.getUserData());
    }

    @Test
    public void testJobId() {
        assertNull(EmptyInstanceRequest.getJobId());
        EmptyInstanceRequest.setJobId("testId");
        assertEquals("testId", EmptyInstanceRequest.getJobId());

        assertNull(InstanceRequest.getJobId());
        InstanceRequest.setJobId("testId");
        assertEquals("testId", InstanceRequest.getJobId());
    }

    @Test
    public void testLoggingProfile() {
        assertNull(EmptyInstanceRequest.getLoggingProfile());
        EmptyInstanceRequest.setLoggingProfile("testProfile");
        assertEquals("testProfile", EmptyInstanceRequest.getLoggingProfile());

        assertNull(InstanceRequest.getLoggingProfile());
        InstanceRequest.setLoggingProfile("testProfile");
        assertEquals("testProfile", InstanceRequest.getLoggingProfile());
    }

    @Test
    public void testStopBehavior() {
        assertNull(EmptyInstanceRequest.getStopBehavior());
        EmptyInstanceRequest.setStopBehavior("testBehavior");
        assertEquals("testBehavior", EmptyInstanceRequest.getStopBehavior());

        assertNull(InstanceRequest.getStopBehavior());
        InstanceRequest.setStopBehavior("testBehavior");
        assertEquals("testBehavior", InstanceRequest.getStopBehavior());
    }

    @Test
    public void testSize() {
        assertNull(EmptyInstanceRequest.getSize());
        EmptyInstanceRequest.setSize("testSize");
        assertEquals("testSize", EmptyInstanceRequest.getSize());

        assertEquals("testType", InstanceRequest.getSize());
        InstanceRequest.setSize("testSize");
        assertEquals("testSize", InstanceRequest.getSize());
    }

    @Test
    public void testRegion() {
        assertNull(EmptyInstanceRequest.getRegion());
        EmptyInstanceRequest.setRegion(VMRegion.US_WEST_2);
        assertEquals(VMRegion.US_WEST_2, EmptyInstanceRequest.getRegion());

        assertEquals(VMRegion.US_WEST_2, InstanceRequest.getRegion());
        InstanceRequest.setRegion(VMRegion.US_EAST);
        assertEquals(VMRegion.US_EAST, InstanceRequest.getRegion());
    }

    @Test
    public void testNumUsersPerAgent() {
        assertEquals(0, EmptyInstanceRequest.getNumUsersPerAgent());
        EmptyInstanceRequest.setNumUsersPerAgent(18);
        assertEquals(18, EmptyInstanceRequest.getNumUsersPerAgent());

        assertEquals(0, InstanceRequest.getNumUsersPerAgent());
        InstanceRequest.setNumUsersPerAgent(19);
        assertEquals(19, InstanceRequest.getNumUsersPerAgent());
    }

    @Test
    public void testImage() {
        assertNull(EmptyInstanceRequest.getImage());
        EmptyInstanceRequest.setImage(VMImageType.AGENT);
        assertEquals(VMImageType.AGENT, EmptyInstanceRequest.getImage());

        assertEquals(VMImageType.AGENT, InstanceRequest.getImage());
        InstanceRequest.setImage(VMImageType.SUPPORT);
        assertEquals(VMImageType.SUPPORT, InstanceRequest.getImage());
    }

    @Test
    public void testNumberOfInstances() {
        assertEquals(-1, EmptyInstanceRequest.getNumberOfInstances());
        EmptyInstanceRequest.setNumberOfInstances(14);
        assertEquals(14, EmptyInstanceRequest.getNumberOfInstances());

        assertEquals(23, InstanceRequest.getNumberOfInstances());
        InstanceRequest.setNumberOfInstances(11);
        assertEquals(11, InstanceRequest.getNumberOfInstances());
    }

    @Test
    public void testReportingMode() {
        assertNull(EmptyInstanceRequest.getReportingMode());
        EmptyInstanceRequest.setReportingMode("testReport");
        assertEquals("testReport", EmptyInstanceRequest.getReportingMode());

        assertEquals("no_results", InstanceRequest.getReportingMode());
        InstanceRequest.setReportingMode("testReport");
        assertEquals("testReport", InstanceRequest.getReportingMode());
    }

    @Test
    public void testToString() {
        assertEquals(ToStringBuilder.reflectionToString(EmptyInstanceRequest), EmptyInstanceRequest.toString());
        assertEquals(ToStringBuilder.reflectionToString(InstanceRequest), InstanceRequest.toString());
    }
}

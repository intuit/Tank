package com.intuit.tank.vm.vmManager;

import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


public class VMJobRequestTest {
    VMJobRequest InstanceRequest = null;

    @BeforeEach
    public void init() {
        InstanceRequest = new VMJobRequest("jobId", "no_results", "defaultProfile", 23, VMRegion.US_EAST, "testBehavior", "testInstance", 83);
    }

    @Test
    public void testBuild() {
        assertNotNull(InstanceRequest);
    }

    @Test
    public void testIsUseEips() {
        assertFalse(InstanceRequest.isUseEips());
        InstanceRequest.setUserEips(true);
        assertTrue(InstanceRequest.isUseEips());
    }

    @Test
    public void testJobId() {
        assertEquals("jobId", InstanceRequest.getJobId());
        InstanceRequest.setJobId("testId");
        assertEquals("testId", InstanceRequest.getJobId());
    }

    @Test
    public void testStopBehavior() {
        assertEquals("testBehavior", InstanceRequest.getStopBehavior());
        InstanceRequest.setStopBehavior("testBehavior");
        assertEquals("testBehavior", InstanceRequest.getStopBehavior());
    }

    @Test
    public void testImage() {
        assertNull(InstanceRequest.getImage());
        InstanceRequest.setImage(VMImageType.SUPPORT);
        assertEquals(VMImageType.SUPPORT, InstanceRequest.getImage());
    }

    @Test
    public void testNumberOfUsers() {
        assertEquals(23, InstanceRequest.getNumberOfUsers());
        InstanceRequest.setNumberOfUsers(19);
        assertEquals(19, InstanceRequest.getNumberOfUsers());
    }

    @Test
    public void testRegion() {
        assertEquals(VMRegion.US_EAST, InstanceRequest.getRegion());
        InstanceRequest.setRegion(VMRegion.US_WEST_2);
        assertEquals(VMRegion.US_WEST_2, InstanceRequest.getRegion());
    }

    @Test
    public void testLoggingProfile() {
        assertEquals("defaultProfile", InstanceRequest.getLoggingProfile());
        InstanceRequest.setLoggingProfile("testProfile");
        assertEquals("testProfile", InstanceRequest.getLoggingProfile());
    }

    @Test
    public void testReportingMode() {
        assertEquals("no_results", InstanceRequest.getReportingMode());
        InstanceRequest.setReportingMode("testReport");
        assertEquals("testReport", InstanceRequest.getReportingMode());
    }

    @Test
    public void testVmInstanceType() {
        assertEquals("testInstance", InstanceRequest.getVmInstanceType());
        InstanceRequest.setVmInstanceType("testInstanceType");
        assertEquals("testInstanceType", InstanceRequest.getVmInstanceType());
    }

    @Test
    public void testNumUsersPerAgent() {
        assertEquals(83, InstanceRequest.getNumUsersPerAgent());
        InstanceRequest.setNumUsersPerAgent(19);
        assertEquals(19, InstanceRequest.getNumUsersPerAgent());
    }
}

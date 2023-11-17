package com.intuit.tank.vm.vmManager;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashSet;
import java.util.Set;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.common.TankConstants;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;

/**
 * The class <code>JobRequestImplCpTest</code> contains tests for the class <code>{@link JobRequestImpl}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class JobRequestImplCpTest {

    private JobRequest request = null;
    private JobRequestImpl.Builder builder = null;

    @BeforeEach
    public void init() {
        JobRequestImpl.Builder result = JobRequestImpl.builder();
        request = result.getInstance();
        builder = JobRequestImpl.builder();
    }

    /**
     * Run the JobRequestImpl.Builder builder() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testBuilder_1()
            throws Exception {
        JobRequestImpl.Builder build = JobRequestImpl.builder();
        assertNotNull(build);
    }

    @Test
    public void testGetLoggingProfile() {
        assertEquals("STANDARD", request.getLoggingProfile());
    }

    @Test
    public void testIsUseEips() {
        assertFalse(request.isUseEips());
    }

    @Test
    public void testGetStopBehavior() {
        assertEquals("END_OF_SCRIPT_GROUP", request.getStopBehavior());
    }

    @Test
    public void testGetId() {
        assertNull(request.getId());
    }

    @Test
    public void testGetIncrementStrategy() {
        assertEquals(IncrementStrategy.increasing, request.getIncrementStrategy());
    }

    @Test
    public void testGetLocation() {
        assertNull(request.getLocation());
    }

    @Test
    public void testGetTerminationPolicy() {
        assertEquals(TerminationPolicy.time, request.getTerminationPolicy());
    }

    @Test
    public void testGetRampTime() {
        assertEquals( 0, request.getRampTime());
    }

    @Test
    public void testGetBaselineVirtualUsers() {
        assertEquals(0, request.getBaselineVirtualUsers());
    }

    @Test
    public void testGetSimulationTime() {
        assertEquals(0L, request.getSimulationTime());
    }

    @Test
    public void testGetUserIntervalIncrement() {
        assertEquals(1, request.getUserIntervalIncrement());
    }

    @Test
    public void testGetReportingMode() {
        assertEquals(TankConstants.RESULTS_NONE, request.getReportingMode());
    }

    @Test
    public void testGetScriptsXmlUrl() {
        assertNull(request.getScriptsXmlUrl());
    }

    @Test
    public void testGetVmInstanceType() { assertNull(request.getVmInstanceType()); }

    @Test
    public void testGetNumUsersPerAgent() {
        assertEquals(0, request.getNumUsersPerAgent());
    }

    @Test
    public void testGetTotalVirtualUsers() {
        assertEquals(0, request.getTotalVirtualUsers());
    }

    @Test
    public void testGetStatus() {
        assertEquals(JobQueueStatus.Created, request.getStatus());
    }

    @Test
    public void testGetRegions() {
        Set<? extends RegionRequest> regions = new HashSet<RegionRequest>();
        assertEquals(regions, request.getRegions());
    }

    @Test
    public void testGetNotifications() {
        Set<? extends Notification> notifications = new HashSet<Notification>();
        assertEquals(notifications, request.getNotifications());
    }

    @Test
    public void testGetDataFileIds() {
        Set<Integer> dataFileIds = new HashSet<Integer>();
        assertEquals(dataFileIds, request.getDataFileIds());
    }

    @Test
    public void testToString() {
        String object = new ToStringBuilder(request)
                .append("id", request.getId())
                .append("location", request.getLocation())
                .append("terminationPolicy", request.getTerminationPolicy())
                .append("rampTime", request.getRampTime())
                .append("loggingProfile", LoggingProfile.fromString(request.getLoggingProfile()).getDisplayName())
                .append("stopBehavior", StopBehavior.fromString(request.getStopBehavior()).getDisplay())
                .append("simulationTime", request.getSimulationTime())
                .append("useEips", request.isUseEips())
                .append("baselineVirtualUsers", request.getBaselineVirtualUsers())
                .append("userIntervalIncrement", "0")
                .append("endRate", "0.0")
                .append("reportingMode", request.getReportingMode())
                .append("regions", request.getRegions())
                .toString();
        assertEquals(object, request.toString());
    }

    @Test
    public void testEquals() {
        JobRequestImpl.Builder newResult = JobRequestImpl.builder();
        JobRequest newRequest = newResult.getInstance();
        assertTrue(request.equals(newRequest));
    }

    @Test
    public void testNotEquals() {
        String test = "test";
        assertFalse(request.equals(test));
    }

    @Test
    public void testBuild() {
        JobRequest instance = builder.build();
        assertNotNull(instance);
    }

    @Test
    public void testWithId() {
        builder.withId("10");
        assertEquals("10", builder.getInstance().getId());
    }

    @Test
    public void testWithStopBehavior() {
        builder.withStopBehavior("Script Group");
        assertEquals("Script Group", builder.getInstance().getStopBehavior());
    }

    @Test
    public void testWithVmInstanceType() {
        builder.withVmInstanceType("test");
        assertEquals("test", builder.getInstance().getVmInstanceType());
    }

    @Test
    public void testWithnumUsersPerAgent() {
        builder.withnumUsersPerAgent(9);
        assertEquals(9, builder.getInstance().getNumUsersPerAgent());
    }

    @Test
    public void testWithUseEips() {
        builder.withUseEips(false);
        assertFalse(builder.getInstance().isUseEips());
    }

    @Test
    public void testWithLoggingProfile() {
        builder.withLoggingProfile("STANDARD");
        assertEquals("STANDARD", builder.getInstance().getLoggingProfile());
    }

    @Test
    public void testWithIncrementStrategy() {
        builder.withIncrementStrategy(IncrementStrategy.increasing);
        assertEquals(IncrementStrategy.increasing, builder.getInstance().getIncrementStrategy());
    }

    @Test
    public void testWithLocation() {
        builder.withLocation("test_here");
        assertEquals("test_here", builder.getInstance().getLocation());
    }

    @Test
    public void testWithTerminationPolicy() {
        builder.withTerminationPolicy(TerminationPolicy.time);
        assertEquals(TerminationPolicy.time, builder.getInstance().getTerminationPolicy());
    }

    @Test
    public void testWithRampTime() {
        builder.withRampTime(2L);
        assertEquals(2L, builder.getInstance().getRampTime());
    }

    @Test
    public void testWthBaselineVirtualUsers() {
        builder.withBaselineVirtualUsers(1);
        assertEquals(1, builder.getInstance().getBaselineVirtualUsers());
    }

    @Test
    public void testWithSimulationTime() {
        builder.withSimulationTime(5);
        assertEquals(5, builder.getInstance().getSimulationTime());
    }

    @Test
    public void testWithUserIntervalIncrement() {
        builder.withUserIntervalIncrement(8);
        assertEquals(8, builder.getInstance().getUserIntervalIncrement());
    }

    @Test
    public void testWithReportingMode() {
        builder.withReportingMode(TankConstants.RESULTS_NONE);
        assertEquals(TankConstants.RESULTS_NONE, builder.getInstance().getReportingMode());
    }

    @Test
    public void testWithStatus() {
        builder.withStatus(JobQueueStatus.Created);
        assertEquals(JobQueueStatus.Created, builder.getInstance().getStatus());
    }

    @Test
    public void testWithRegions() {
        Set<? extends RegionRequest> regions = new HashSet<RegionRequest>();
        builder.withRegions(regions);
        assertEquals(regions, builder.getInstance().getRegions());
    }

    @Test
    public void testWithNofitications() {
        Set<? extends Notification> notifications = new HashSet<Notification>();
        builder.withNofitications(notifications);
        assertEquals(notifications, builder.getInstance().getNotifications());
    }

    @Test
    public void testWithDataFileIds() {
        Set<Integer> dataFileIds = new HashSet<Integer>();
        builder.withDataFileIds(dataFileIds);
        assertEquals(dataFileIds, builder.getInstance().getDataFileIds());
    }

    @Test
    public void testWithScriptXmlUrl() {
        builder.withScriptXmlUrl("test_url");
        assertEquals("test_url", builder.getInstance().getScriptsXmlUrl());
    }
}

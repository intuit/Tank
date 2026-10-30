package com.intuit.tank.perfManager.workLoads;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.ArgumentCaptor;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.perfManager.RequestAgents;
import com.intuit.tank.vm.scheduleManager.AgentDispatcher;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.RegionRequest;
import com.intuit.tank.vm.vmManager.VMChannel;

import java.util.Set;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>IncreasingWorkLoadTest</code> contains tests for the class <code>{@link IncreasingWorkLoad}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class IncreasingWorkLoadTest {
    /**
     * Run the IncreasingWorkLoad(VMChannel,AgentDispatcher,JobRequest) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testIncreasingWorkLoad_1()
            throws Exception {
        VMChannel channel = null;
        AgentDispatcher agentDispatcher = null;
        JobRequest job = null;

        IncreasingWorkLoad result = new IncreasingWorkLoad(channel, agentDispatcher, job);

        assertNotNull(result);
        assertNull(result.getJob());
    }

    /**
     * Run the JobRequest getJob() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testGetJob_1()
            throws Exception {
        IncreasingWorkLoad fixture = new IncreasingWorkLoad((VMChannel) null, (AgentDispatcher) null, (JobRequest) null);

        JobRequest result = fixture.getJob();

        assertNull(result);
    }

    @Test
    public void testRun_requestsRegionsConcurrently() {
        RegionRequest east = mockRegion(VMRegion.US_EAST_2);
        RegionRequest west = mockRegion(VMRegion.US_WEST_2);
        JobRequest job = mock(JobRequest.class);
        when(job.getId()).thenReturn("1");
        when(job.getIncrementStrategy()).thenReturn(IncrementStrategy.increasing);
        doReturn(Set.of(east, west)).when(job).getRegions();

        // Each region's request blocks until the other arrives; run serially, the barrier times out
        CyclicBarrier bothRegions = new CyclicBarrier(2);
        AgentDispatcher agentDispatcher = mock(AgentDispatcher.class);
        doAnswer(invocation -> bothRegions.await(5, TimeUnit.SECONDS))
                .when(agentDispatcher).processAgentsMessage(any(RequestAgents.class));

        try (Segment ignored = AWSXRay.beginSegment("TEST")) {
            new IncreasingWorkLoad(null, agentDispatcher, job).run();
        }

        assertFalse(bothRegions.isBroken());
        verify(agentDispatcher, times(2)).processAgentsMessage(any(RequestAgents.class));
    }

    @Test
    public void testRun_unsampledRequestRestoresItsSegment() {
        JobRequest job = mock(JobRequest.class);
        when(job.getId()).thenReturn("1");
        doReturn(Set.of()).when(job).getRegions();

        Segment unsampled = AWSXRay.getGlobalRecorder().beginNoOpSegment();
        try {
            new IncreasingWorkLoad(null, mock(AgentDispatcher.class), job).run();

            // A no-op subsegment's close() is empty, so run() must end it explicitly
            assertSame(unsampled, AWSXRay.getTraceEntity());
        } finally {
            AWSXRay.endSegment();
        }
    }

    @Test
    public void testRun_nonlinearRequestsTheAgentsAllocatedToEachRegion() {
        RegionRequest east = mockRegion(VMRegion.US_EAST_2);
        when(east.getPercentage()).thenReturn("100");
        RegionRequest west = mockRegion(VMRegion.US_WEST_2);
        when(west.getPercentage()).thenReturn("0");
        JobRequest job = mock(JobRequest.class);
        when(job.getId()).thenReturn("1");
        when(job.getIncrementStrategy()).thenReturn(IncrementStrategy.standard);
        when(job.getNumAgents()).thenReturn(3);
        doReturn(Set.of(east, west)).when(job).getRegions();
        AgentDispatcher agentDispatcher = mock(AgentDispatcher.class);

        try (Segment ignored = AWSXRay.beginSegment("TEST")) {
            new IncreasingWorkLoad(null, agentDispatcher, job).run();
        }

        // only the region with a share of the agents is requested, sized by its agent count
        ArgumentCaptor<RequestAgents> request = ArgumentCaptor.forClass(RequestAgents.class);
        verify(agentDispatcher).processAgentsMessage(request.capture());
        assertEquals(VMRegion.US_EAST_2, request.getValue().getRegion());
        assertEquals(3, request.getValue().getNumberOfUsers());
    }

    @Test
    public void testRun_linearRegionWithoutUsersIsNotRequested() {
        RegionRequest east = mockRegion(VMRegion.US_EAST_2);
        when(east.getUsers()).thenReturn("0");
        JobRequest job = mock(JobRequest.class);
        when(job.getId()).thenReturn("1");
        when(job.getIncrementStrategy()).thenReturn(IncrementStrategy.increasing);
        doReturn(Set.of(east)).when(job).getRegions();
        AgentDispatcher agentDispatcher = mock(AgentDispatcher.class);

        try (Segment ignored = AWSXRay.beginSegment("TEST")) {
            new IncreasingWorkLoad(null, agentDispatcher, job).run();
        }

        verifyNoInteractions(agentDispatcher);
    }

    @Test
    public void testRun_failureIsLoggedAndNotRethrown() {
        JobRequest job = mock(JobRequest.class);
        when(job.getId()).thenReturn("1");
        when(job.getRegions()).thenThrow(new IllegalStateException("regions unavailable"));

        try (Segment ignored = AWSXRay.beginSegment("TEST")) {
            assertDoesNotThrow(() -> new IncreasingWorkLoad(null, mock(AgentDispatcher.class), job).run());
        }
    }

    private RegionRequest mockRegion(VMRegion region) {
        RegionRequest regionRequest = mock(RegionRequest.class);
        when(regionRequest.getRegion()).thenReturn(region);
        when(regionRequest.getUsers()).thenReturn("10");
        return regionRequest;
    }

}

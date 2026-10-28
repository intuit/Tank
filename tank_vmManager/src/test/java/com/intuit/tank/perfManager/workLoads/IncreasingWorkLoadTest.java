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

import com.intuit.tank.perfManager.workLoads.IncreasingWorkLoad;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.perfManager.RequestAgents;
import com.intuit.tank.vm.scheduleManager.AgentDispatcher;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.RegionRequest;
import com.intuit.tank.vm.vmManager.VMChannel;
import com.intuit.tank.tracing.Tracing;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.testing.junit5.OpenTelemetryExtension;
import io.opentelemetry.sdk.trace.data.SpanData;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * The class <code>IncreasingWorkLoadTest</code> contains tests for the class <code>{@link IncreasingWorkLoad}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class IncreasingWorkLoadTest {

    @RegisterExtension
    static final OpenTelemetryExtension otel = OpenTelemetryExtension.create();
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
        assertEquals(null, result.getJob());
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

        assertEquals(null, result);
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
        List<String> regionSpanIds = new CopyOnWriteArrayList<>();
        doAnswer(invocation -> {
            regionSpanIds.add(Span.current().getSpanContext().getSpanId());
            return bothRegions.await(5, TimeUnit.SECONDS);
        }).when(agentDispatcher).processAgentsMessage(any(RequestAgents.class));

        Tracing.inSpan("TEST", () -> new IncreasingWorkLoad(null, agentDispatcher, job).run());

        assertFalse(bothRegions.isBroken());
        verify(agentDispatcher, times(2)).processAgentsMessage(any(RequestAgents.class));
        // the region threads stay inside the Request.Agents span, which is a child of the caller's span
        SpanData request = findSpan("Request.Agents.JobId.1");
        assertEquals(findSpan("TEST").getSpanId(), request.getParentSpanId());
        assertEquals(List.of(request.getSpanId(), request.getSpanId()), regionSpanIds);
    }

    @Test
    public void testRun_restoresTheCallersSpan() {
        JobRequest job = mock(JobRequest.class);
        when(job.getId()).thenReturn("1");
        doReturn(Set.of()).when(job).getRegions();

        Span caller = Tracing.startSpan("TEST");
        try (Scope ignored = caller.makeCurrent()) {
            new IncreasingWorkLoad(null, mock(AgentDispatcher.class), job).run();

            assertSame(caller, Span.current());
        } finally {
            caller.end();
        }
        assertNotNull(findSpan("Request.Agents.JobId.1"));
    }

    private static SpanData findSpan(String name) {
        return otel.getSpans().stream().filter(span -> span.getName().equals(name)).findFirst()
                .orElseThrow(() -> new AssertionError("No span named " + name));
    }

    private RegionRequest mockRegion(VMRegion region) {
        RegionRequest regionRequest = mock(RegionRequest.class);
        when(regionRequest.getRegion()).thenReturn(region);
        when(regionRequest.getUsers()).thenReturn("10");
        return regionRequest;
    }

}

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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.enterprise.inject.Instance;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.logging.ControllerLoggingConfig;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.util.DataFileUtil;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.perfManager.StandaloneAgentTracker;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmManagerConfig;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.RegionRequest;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

/**
 * Covers the job lifecycle in <code>{@link JobManager}</code>: starting a job, agent registration, the start
 * sequence sent to registered agents, and HTTP/WS command delivery. HTTP is served by a mocked
 * {@link HttpClient} and the 30 second start delay is skipped by interrupting the thread that runs it.
 */
public class JobManagerLifecycleTest {

    private static final String JOB_ID = "42";

    private JobManager fixture;
    private HttpClient httpClient;
    private VMTracker vmTracker;
    private TankConfig tankConfig;
    private AgentConfig agentConfig;
    private MockedStatic<ControllerLoggingConfig> loggingConfig;

    @BeforeEach
    void setUp() throws Exception {
        AgentWsCommandSender.setStaticInstance(null);
        loggingConfig = mockStatic(ControllerLoggingConfig.class);
        fixture = new JobManager();
        httpClient = mock(HttpClient.class);
        setField("client", httpClient);
        vmTracker = mock(VMTracker.class);
        setField("vmTracker", vmTracker);
        agentConfig = mock(AgentConfig.class);
        when(agentConfig.getAgentPort()).thenReturn(8090);
        tankConfig = mock(TankConfig.class);
        when(tankConfig.getAgentConfig()).thenReturn(agentConfig);
        setField("tankConfig", tankConfig);
    }

    @AfterEach
    void tearDown() {
        loggingConfig.close();
        AgentWsCommandSender.setStaticInstance(null);
    }

    // ---------------------------------------------------------------- startJob

    @Test
    void startJobRunsTheWorkloadOnASegmentContextExecutor() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.US_EAST, "1", "100"));
        IncreasingWorkLoad workLoad = stubWorkLoadFactory(jobRequest);
        when(tankConfig.getStandalone()).thenReturn(false);

        try (Segment ignored = AWSXRay.beginSegment("TEST")) {
            fixture.startJob(Integer.parseInt(JOB_ID));
        }

        verify(workLoad, timeout(5000)).run();
        assertTrue(jobInfoCache().containsKey(JOB_ID));
    }

    @Test
    void startJobStandaloneAbortsWhenNoAgentsAreAvailable() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.US_EAST, "1", "100"));
        stubWorkLoadFactory(jobRequest);
        when(tankConfig.getStandalone()).thenReturn(true);
        StandaloneAgentTracker tracker = mock(StandaloneAgentTracker.class);
        when(tracker.getAgents(anyInt())).thenReturn(null);
        setField("standaloneTracker", tracker);
        JobInstance jobInstance = mock(JobInstance.class);

        try (MockedConstruction<JobInstanceDao> dao = mockConstruction(JobInstanceDao.class,
                (mock, context) -> when(mock.findById(42)).thenReturn(jobInstance))) {
            fixture.startJob(42);

            verify(jobInstance).setStatus(JobQueueStatus.Aborted);
            verify(dao.constructed().get(0)).saveOrUpdate(jobInstance);
        }
    }

    @Test
    void startJobStandaloneSendsStartRequestToEachAgent() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.US_EAST, "1", "100"));
        when(jobRequest.getStopBehavior()).thenReturn("END_OF_TEST");
        stubWorkLoadFactory(jobRequest);
        when(tankConfig.getStandalone()).thenReturn(true);
        StandaloneAgentTracker tracker = mock(StandaloneAgentTracker.class);
        when(tracker.getAgents(anyInt())).thenReturn(List.of(new AgentAvailability("i-1", "http://agent-1:8090", 10, null)));
        setField("standaloneTracker", tracker);
        HttpResponse<Object> ok = response(200);
        doReturn(ok).when(httpClient).send(any(HttpRequest.class), any());
        JobInstance jobInstance = mock(JobInstance.class);

        try (MockedConstruction<JobInstanceDao> ignored = mockConstruction(JobInstanceDao.class,
                (mock, context) -> when(mock.findById(42)).thenReturn(jobInstance))) {
            fixture.startJob(42);
        }

        verify(httpClient).send(argThat(request -> request.uri().toString()
                .equals("http://agent-1:8090" + AgentCommand.request.getPath())), any());
        verify(jobInstance, never()).setStatus(any());
    }

    @Test
    void startJobStandaloneAbortsWhenAnAgentRejectsTheStartRequest() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.US_EAST, "1", "100"));
        stubWorkLoadFactory(jobRequest);
        when(tankConfig.getStandalone()).thenReturn(true);
        StandaloneAgentTracker tracker = mock(StandaloneAgentTracker.class);
        when(tracker.getAgents(anyInt())).thenReturn(List.of(new AgentAvailability("i-1", "http://agent-1:8090", 10, null)));
        setField("standaloneTracker", tracker);
        HttpResponse<Object> rejected = response(500);
        doReturn(rejected).when(httpClient).send(any(HttpRequest.class), any());
        JobInstance jobInstance = mock(JobInstance.class);

        try (MockedConstruction<JobInstanceDao> ignored = mockConstruction(JobInstanceDao.class,
                (mock, context) -> when(mock.findById(42)).thenReturn(jobInstance))) {
            fixture.startJob(42);
        }

        verify(jobInstance).setStatus(JobQueueStatus.Aborted);
    }

    @Test
    void startJobStandaloneLogsAndContinuesWhenTheStartRequestCannotBeSent() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.US_EAST, "1", "100"));
        stubWorkLoadFactory(jobRequest);
        when(tankConfig.getStandalone()).thenReturn(true);
        StandaloneAgentTracker tracker = mock(StandaloneAgentTracker.class);
        when(tracker.getAgents(anyInt())).thenReturn(List.of(new AgentAvailability("i-1", "http://agent-1:8090", 10, null)));
        setField("standaloneTracker", tracker);
        when(httpClient.send(any(HttpRequest.class), any())).thenThrow(new IOException("connection refused"));
        JobInstance jobInstance = mock(JobInstance.class);

        try (MockedConstruction<JobInstanceDao> ignored = mockConstruction(JobInstanceDao.class,
                (mock, context) -> when(mock.findById(42)).thenReturn(jobInstance))) {
            fixture.startJob(42);
        }

        // IOException is swallowed by sendRequest, so the job is not aborted
        verify(jobInstance, never()).setStatus(any());
    }

    // ---------------------------------------------------------------- registerAgentForJob

    @Test
    void registerAgentForJobAllocatesUsersAndStartsTheTestWhenAllAgentsHaveRegistered() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.US_EAST, "2", "100"));
        when(jobRequest.getDataFileIds()).thenReturn(new LinkedHashSet<>(List.of(7)));
        when(jobRequest.getRampTime()).thenReturn(60L);
        when(jobRequest.getSimulationTime()).thenReturn(120L);
        when(jobRequest.getBaselineVirtualUsers()).thenReturn(1);
        Object jobInfo = newJobInfo(jobRequest);
        jobInfoCache().put(JOB_ID, jobInfo);
        CloudVmStatus status = mock(CloudVmStatus.class);
        when(vmTracker.getStatus("i-1")).thenReturn(status);
        // the start thread this triggers sends over WS, so every agent is immediately ready
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);
        AgentWsCommandSender wsSender = readySender();
        AgentWsCommandSender.setStaticInstance(wsSender);
        DataFile dataFile = mock(DataFile.class);
        when(dataFile.getId()).thenReturn(7);
        when(dataFile.getPath()).thenReturn("users.csv");

        AgentTestStartData first;
        AgentTestStartData duplicate;
        AgentTestStartData second;
        try (MockedConstruction<DataFileDao> ignored = mockConstruction(DataFileDao.class,
                (mock, context) -> when(mock.findById(7)).thenReturn(dataFile));
             MockedStatic<DataFileUtil> dataFileUtil = mockStatic(DataFileUtil.class)) {
            dataFileUtil.when(() -> DataFileUtil.getNumLines(dataFile)).thenReturn(10);
            dataFileUtil.when(() -> DataFileUtil.getDataFileServiceUrl(anyInt(), anyInt(), anyInt()))
                    .thenAnswer(inv -> "http://controller/data?offset=" + inv.getArgument(1) + "&lines=" + inv.getArgument(2));

            first = fixture.registerAgentForJob(new AgentData(JOB_ID, "i-1", "http://agent-1:8090", 1, VMRegion.US_EAST, "zone"));
            duplicate = fixture.registerAgentForJob(new AgentData(JOB_ID, "i-1", "http://agent-1:8090", 1, VMRegion.US_EAST, "zone"));
            second = fixture.registerAgentForJob(new AgentData(JOB_ID, "i-2", "http://agent-2:8090", 1, VMRegion.US_EAST, "zone"));

            // the data file line count is looked up once and cached
            dataFileUtil.verify(() -> DataFileUtil.getNumLines(dataFile), times(1));
        }

        assertEquals(1, first.getConcurrentUsers());
        assertEquals(0, first.getAgentInstanceNum());
        assertEquals(2, first.getTotalAgents());
        assertEquals(JOB_ID, first.getJobId());
        assertEquals(60L, first.getRampTime());
        assertEquals(120L, first.getSimulationTime());
        assertEquals(1, first.getStartUsers());
        assertEquals("script.xml", first.getScriptUrl());
        assertEquals(1, first.getDataFiles().length);
        assertEquals("users.csv", first.getDataFiles()[0].getFileName());
        assertTrue(first.getDataFiles()[0].isDefaultDataFile());
        assertEquals("http://controller/data?offset=0&lines=5", first.getDataFiles()[0].getFileUrl());
        // a duplicate registration reuses the existing allocation instead of taking more users
        assertEquals(1, duplicate.getConcurrentUsers());
        assertEquals(1, duplicate.getAgentInstanceNum());
        assertEquals(1, second.getConcurrentUsers());
        assertEquals(1, second.getAgentInstanceNum());
        assertEquals("http://controller/data?offset=5&lines=5", second.getDataFiles()[0].getFileUrl());
        verify(status, times(2)).setVmStatus(VMStatus.pending);

        // the last registration starts the test on its own thread, which first sleeps 30s
        Thread startThread = awaitStartTestThread();
        startThread.interrupt();
        startThread.join(TimeUnit.SECONDS.toMillis(10));
        assertFalse(startThread.isAlive());
        verify(wsSender).sendCommand(eq("i-1"), eq(JOB_ID), eq("start"), anyLong());
        verify(wsSender).sendCommand(eq("i-2"), eq(JOB_ID), eq("start"), anyLong());
    }

    @Test
    void registerAgentForJobSkipsMissingDataFiles() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.US_EAST, "2", "100"));
        when(jobRequest.getDataFileIds()).thenReturn(new LinkedHashSet<>(List.of(8, 9)));
        jobInfoCache().put(JOB_ID, newJobInfo(jobRequest));

        AgentTestStartData result;
        try (MockedConstruction<DataFileDao> ignored = mockConstruction(DataFileDao.class)) {
            result = fixture.registerAgentForJob(new AgentData(JOB_ID, "i-1", "http://agent-1:8090", 1, VMRegion.US_EAST, "zone"));
        }

        assertEquals(0, result.getDataFiles().length);
        assertEquals(1, result.getConcurrentUsers());
    }

    // ---------------------------------------------------------------- buildAgentDataForWsHello

    @Test
    void buildAgentDataForWsHelloUsesTheTrackedRegionAndGivenUrl() {
        CloudVmStatus status = mock(CloudVmStatus.class);
        when(status.getVmRegion()).thenReturn(VMRegion.US_WEST_2);
        when(vmTracker.getStatus("i-1")).thenReturn(status);

        AgentData result = fixture.buildAgentDataForWsHello("1", "i-1", "http://agent:9000", 25);

        assertEquals("1", result.getJobId());
        assertEquals("i-1", result.getInstanceId());
        assertEquals("http://agent:9000", result.getInstanceUrl());
        assertEquals(25, result.getCapacity());
        assertEquals(VMRegion.US_WEST_2, result.getRegion());
    }

    @Test
    void buildAgentDataForWsHelloFallsBackToTheJobRegionAndConfiguredPort() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.ASIA_1, "2", "100"));
        jobInfoCache().put(JOB_ID, newJobInfo(jobRequest));
        when(agentConfig.getAgentPort()).thenReturn(8123);

        AgentData result = fixture.buildAgentDataForWsHello(JOB_ID, "i-1", " ", null);

        assertEquals(VMRegion.ASIA_1, result.getRegion());
        assertEquals("http://localhost:8123", result.getInstanceUrl());
        assertEquals(1, result.getCapacity());
    }

    @Test
    void buildAgentDataForWsHelloDefaultsWhenNothingIsKnown() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID);
        jobInfoCache().put(JOB_ID, newJobInfo(jobRequest));
        setField("tankConfig", null);
        setField("vmTracker", null);

        AgentData result = fixture.buildAgentDataForWsHello(JOB_ID, "i-1", null, 0);

        assertEquals(VMRegion.US_EAST, result.getRegion());
        assertEquals("http://localhost:8090", result.getInstanceUrl());
        assertEquals(1, result.getCapacity());
    }

    @Test
    void buildAgentDataForWsHelloDefaultsRegionWhenJobHasNoRegions() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID);
        doReturn(null).when(jobRequest).getRegions();
        Object jobInfo = newJobInfo(increasingJob(JOB_ID));
        Field jobRequestField = jobInfo.getClass().getDeclaredField("jobRequest");
        jobRequestField.setAccessible(true);
        jobRequestField.set(jobInfo, jobRequest);
        jobInfoCache().put(JOB_ID, jobInfo);

        AgentData result = fixture.buildAgentDataForWsHello(JOB_ID, "i-1", null, 3);

        assertEquals(VMRegion.US_EAST, result.getRegion());
        assertEquals(3, result.getCapacity());
    }

    // ---------------------------------------------------------------- startTest

    @Test
    void startTestTwoStepMarksAgentsReadyAndWaitsForStartAgents() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID, region(VMRegion.US_EAST, "2", "100"));
        when(jobRequest.isUseTwoStep()).thenReturn(true);
        Object jobInfo = newJobInfo(jobRequest);
        addAgent(jobInfo, "i-1", "http://agent-1:8090");
        addAgent(jobInfo, "i-2", "http://agent-2:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        CloudVmStatus status = mock(CloudVmStatus.class);
        when(vmTracker.getStatus("i-1")).thenReturn(status);
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);
        AgentWsCommandSender wsSender = readySender();
        AgentWsCommandSender.setStaticInstance(wsSender);

        // release the latch up front; a second call is a no-op once started
        fixture.startAgents(JOB_ID);
        fixture.startAgents(JOB_ID);
        invokeStartTest(jobInfo);

        verify(status).setVmStatus(VMStatus.ready);
        verify(vmTracker).setStatus(status);
        verify(wsSender, times(2)).sendCommand(anyString(), eq(JOB_ID), eq("start"), anyLong());
    }

    @Test
    void startTestTwoStepSendsStartWhenInterruptedWhileWaitingForStartAgents() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID);
        when(jobRequest.isUseTwoStep()).thenReturn(true);
        Object jobInfo = newJobInfo(jobRequest);
        addAgent(jobInfo, "i-1", "http://agent-1:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);
        AgentWsCommandSender wsSender = readySender();
        AgentWsCommandSender.setStaticInstance(wsSender);
        AtomicReference<Exception> failure = new AtomicReference<>();
        Thread runner = new Thread(() -> {
            try {
                invokeStartTest(jobInfo);
            } catch (Exception e) {
                failure.set(e);
            }
        });

        runner.start();
        awaitBlockedOnLatch(runner);
        runner.interrupt();
        runner.join(TimeUnit.SECONDS.toMillis(10));

        assertFalse(runner.isAlive());
        assertNull(failure.get());
        verify(wsSender).sendCommand(eq("i-1"), eq(JOB_ID), eq("start"), anyLong());
    }

    @Test
    void startTestOverWsSkipsAgentsThatAreNotReadyAndReportsFailedAcks() throws Exception {
        JobRequest jobRequest = increasingJob(JOB_ID);
        Object jobInfo = newJobInfo(jobRequest);
        addAgent(jobInfo, "i-acked", "http://a:8090");
        addAgent(jobInfo, "i-nack", "http://b:8090");
        addAgent(jobInfo, "i-nosession", "http://c:8090");
        addAgent(jobInfo, "i-notransfer", "http://d:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);

        AgentWsCommandSender wsSender = mock(AgentWsCommandSender.class);
        when(wsSender.hasSession(anyString())).thenReturn(true);
        when(wsSender.isFileTransferReady(anyString())).thenReturn(true);
        // ready while waiting, then gone (or not transferred) when re-checked after the wait loop
        when(wsSender.hasSession("i-nosession")).thenReturn(true, false);
        when(wsSender.isFileTransferReady("i-notransfer")).thenReturn(true, false);
        when(wsSender.sendCommand(eq("i-acked"), anyString(), anyString(), anyLong())).thenReturn(true);
        when(wsSender.sendCommand(eq("i-nack"), anyString(), anyString(), anyLong())).thenReturn(false);
        setWsSenderInstance(wsSender);

        invokeStartTest(jobInfo);

        verify(wsSender).sendCommand(eq("i-acked"), eq(JOB_ID), eq("start"), anyLong());
        verify(wsSender).sendCommand(eq("i-nack"), eq(JOB_ID), eq("start"), anyLong());
        verify(wsSender, never()).sendCommand(eq("i-nosession"), anyString(), anyString(), anyLong());
        verify(wsSender, never()).sendCommand(eq("i-notransfer"), anyString(), anyString(), anyLong());
    }

    @Test
    void startTestOverWsSkipsEveryAgentWhenNoSenderIsAvailable() throws Exception {
        Object jobInfo = newJobInfo(increasingJob(JOB_ID));
        addAgent(jobInfo, "i-1", "http://a:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);

        assertDoesNotThrow(() -> invokeStartTest(jobInfo));
        verifyNoInteractions(httpClient);
    }

    @Test
    void startTestOverHttpSendsStartToEachAgentAndHandlesEveryResponse() throws Exception {
        Object jobInfo = newJobInfo(increasingJob(JOB_ID));
        addAgent(jobInfo, "i-ok", "http://ok:8090");
        addAgent(jobInfo, "i-accepted", "http://accepted:8090");
        addAgent(jobInfo, "i-error", "http://error:8090");
        addAgent(jobInfo, "i-null", "http://null:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        when(agentConfig.isCommandWsEnabled()).thenReturn(false);
        Map<String, Integer> statusByHost = Map.of("ok", 200, "accepted", 202, "error", 500);
        doAnswer(inv -> {
            HttpRequest request = inv.getArgument(0);
            Integer code = statusByHost.get(request.uri().getHost());
            return CompletableFuture.completedFuture(code == null ? null : response(code, request.uri()));
        }).when(httpClient).sendAsync(any(HttpRequest.class), any());

        invokeStartTest(jobInfo);

        verify(httpClient, times(4)).sendAsync(any(HttpRequest.class), any());
        verify(httpClient).sendAsync(argThat(r -> r.uri().toString().equals("http://ok:8090" + AgentCommand.start.getPath())), any());
    }

    @Test
    void startTestWithoutTankConfigFallsBackToHttp() throws Exception {
        setField("tankConfig", null);
        Object jobInfo = newJobInfo(increasingJob(JOB_ID));
        addAgent(jobInfo, "i-ok", "http://ok:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        HttpResponse<Object> ok = response(200);
        doReturn(CompletableFuture.completedFuture(ok)).when(httpClient).sendAsync(any(HttpRequest.class), any());

        invokeStartTest(jobInfo);

        verify(httpClient).sendAsync(any(HttpRequest.class), any());
    }

    // ---------------------------------------------------------------- sendCommand

    @Test
    void sendCommandOverHttpResolvesUrlsFromRegisteredAgents() throws Exception {
        Object jobInfo = newJobInfo(increasingJob(JOB_ID));
        addAgent(jobInfo, "i-1", "http://agent-1:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        HttpResponse<Object> ok = response(200);
        doReturn(CompletableFuture.completedFuture(ok)).when(httpClient).sendAsync(any(HttpRequest.class), any());

        List<CompletableFuture<?>> result = fixture.sendCommand(List.of("i-1"), AgentCommand.stop);

        assertEquals(1, result.size());
        assertSame(ok, result.get(0).join());
        verify(httpClient).sendAsync(argThat(r -> r.uri().toString().equals("http://agent-1:8090" + AgentCommand.stop.getPath())), any());
    }

    @Test
    void sendCommandOverHttpLogsNoRouteToHostFailures() throws Exception {
        Object jobInfo = newJobInfo(increasingJob(JOB_ID));
        addAgent(jobInfo, "i-1", "http://agent-1:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        IOException noRoute = new IOException("unreachable", new NoRouteToHostException("no route"));
        doReturn(CompletableFuture.failedFuture(noRoute)).when(httpClient).sendAsync(any(HttpRequest.class), any());

        List<CompletableFuture<?>> result = fixture.sendCommand(List.of("i-1"), AgentCommand.kill);

        assertNull(result.get(0).join());
    }

    @Test
    void sendCommandOverHttpLogsOtherFailures() throws Exception {
        Object jobInfo = newJobInfo(increasingJob(JOB_ID));
        addAgent(jobInfo, "i-1", "http://agent-1:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        doReturn(CompletableFuture.failedFuture(new IOException("reset"))).when(httpClient).sendAsync(any(HttpRequest.class), any());

        List<CompletableFuture<?>> result = fixture.sendCommand(List.of("i-1"), AgentCommand.pause);

        assertNull(result.get(0).join());
    }

    @Test
    void sendCommandOverWsHandlesMissingSenderJobAndFailedAck() throws Exception {
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);
        Object jobInfo = newJobInfo(increasingJob(JOB_ID));
        addAgent(jobInfo, "i-ready", "http://a:8090");
        addAgent(jobInfo, "i-nack", "http://b:8090");
        jobInfoCache().put(JOB_ID, jobInfo);
        AgentWsCommandSender wsSender = mock(AgentWsCommandSender.class);
        when(wsSender.hasSession(anyString())).thenReturn(true);
        when(wsSender.isFileTransferReady(anyString())).thenReturn(true);
        when(wsSender.sendCommand(eq("i-ready"), anyString(), anyString(), anyLong())).thenReturn(true);
        when(wsSender.sendCommand(eq("i-nack"), anyString(), anyString(), anyLong())).thenReturn(false);
        setWsSenderInstance(wsSender);

        List<CompletableFuture<?>> result = fixture.sendCommand(List.of("i-ready", "i-nack", "i-unknown"), AgentCommand.start);

        assertEquals(3, result.size());
        verify(wsSender).sendCommand(eq("i-ready"), eq(JOB_ID), eq("start"), anyLong());
        verify(wsSender).sendCommand(eq("i-nack"), eq(JOB_ID), eq("start"), anyLong());
        // i-unknown is in no job, so it is skipped before a session lookup
        verify(wsSender, never()).sendCommand(eq("i-unknown"), anyString(), anyString(), anyLong());
    }

    @Test
    void sendCommandOverWsWithoutSenderCompletesEmpty() throws Exception {
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);

        List<CompletableFuture<?>> result = fixture.sendCommand(List.of("i-1", "i-2"), AgentCommand.stop);

        assertEquals(2, result.size());
        result.forEach(future -> assertNull(future.join()));
        verifyNoInteractions(httpClient);
    }

    @Test
    void sendCommandPrefersTheControllerInitiatedWsClient() throws Exception {
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);
        ControllerInitiatedAgentWsClient wsClient = fixture.getControllerInitiatedAgentWsClient();
        assertSame(wsClient, AgentWsCommandSender.getStaticInstance());
        @SuppressWarnings("unchecked")
        Instance<AgentWsCommandSender> cdiSender = mock(Instance.class);
        setField("wsCommandSenderInstance", cdiSender);

        List<CompletableFuture<?>> result = fixture.sendCommand(List.of("i-1"), AgentCommand.stop);

        assertEquals(1, result.size());
        // the static controller-initiated client wins, so CDI is never consulted
        verifyNoInteractions(cdiSender);
    }

    @Test
    void sendCommandFallsBackToTheStaticSenderWhenCdiFails() throws Exception {
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);
        fixture.adoptAgent("i-1", JOB_ID);
        AgentWsCommandSender staticSender = readySender();
        AgentWsCommandSender.setStaticInstance(staticSender);
        @SuppressWarnings("unchecked")
        Instance<AgentWsCommandSender> cdiSender = mock(Instance.class);
        when(cdiSender.isResolvable()).thenThrow(new IllegalStateException("no container"));
        setField("wsCommandSenderInstance", cdiSender);

        fixture.sendCommand(List.of("i-1"), AgentCommand.stop);

        verify(staticSender).sendCommand(eq("i-1"), eq(JOB_ID), eq("stop"), anyLong());
    }

    @Test
    void sendCommandFallsBackToTheStaticSenderWhenCdiReturnsNull() throws Exception {
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);
        fixture.adoptAgent("i-1", JOB_ID);
        AgentWsCommandSender staticSender = readySender();
        AgentWsCommandSender.setStaticInstance(staticSender);
        @SuppressWarnings("unchecked")
        Instance<AgentWsCommandSender> cdiSender = mock(Instance.class);
        when(cdiSender.isResolvable()).thenReturn(true);
        when(cdiSender.get()).thenReturn(null);
        setField("wsCommandSenderInstance", cdiSender);

        fixture.sendCommand(List.of("i-1"), AgentCommand.stop);

        verify(staticSender).sendCommand(eq("i-1"), eq(JOB_ID), eq("stop"), anyLong());
    }

    @Test
    void sendCommandFallsBackToTheStaticSenderWhenCdiIsUnresolvable() throws Exception {
        when(agentConfig.isCommandWsEnabled()).thenReturn(true);
        fixture.adoptAgent("i-1", JOB_ID);
        AgentWsCommandSender staticSender = readySender();
        AgentWsCommandSender.setStaticInstance(staticSender);
        @SuppressWarnings("unchecked")
        Instance<AgentWsCommandSender> cdiSender = mock(Instance.class);
        when(cdiSender.isResolvable()).thenReturn(false);
        setField("wsCommandSenderInstance", cdiSender);

        fixture.sendCommand(List.of("i-1"), AgentCommand.stop);

        verify(staticSender).sendCommand(eq("i-1"), eq(JOB_ID), eq("stop"), anyLong());
    }

    // ---------------------------------------------------------------- getInstanceUrl / findAgent

    @Test
    void getInstanceUrlSearchesAwsForAgentsNotInTheLocalCache() {
        stubRegions(VMRegion.US_EAST, VMRegion.US_WEST_2);

        List<String> result;
        try (MockedConstruction<AmazonInstance> ignored = mockConstruction(AmazonInstance.class,
                (mock, context) -> when(mock.findDNSName("i-aws")).thenReturn(
                        context.arguments().get(0) == VMRegion.US_WEST_2 ? Optional.of("ec2-host") : Optional.empty()))) {
            result = fixture.getInstanceUrl(List.of("i-aws"));
        }

        assertEquals(List.of("http://ec2-host:8090"), result);
    }

    @Test
    void getInstanceUrlRunsTheAwsLookupInsideTheCallersTraceSegment() {
        stubRegions(VMRegion.US_EAST);

        List<String> result;
        try (Segment ignored = AWSXRay.beginSegment("TEST");
             MockedConstruction<AmazonInstance> amazon = mockConstruction(AmazonInstance.class,
                     (mock, context) -> when(mock.findDNSName("i-aws")).thenReturn(Optional.empty()))) {
            result = fixture.getInstanceUrl(List.of("i-aws"));
        }

        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------------------------- JobInfo

    @Test
    void jobInfoForNonlinearJobsAllocatesAgentsByRegionPercentage() throws Exception {
        RegionRequest east = region(VMRegion.US_EAST, "0", "100");
        RegionRequest west = region(VMRegion.US_WEST_2, "0", "0");
        JobRequest jobRequest = increasingJob(JOB_ID, east, west);
        when(jobRequest.getIncrementStrategy()).thenReturn(IncrementStrategy.standard);
        when(jobRequest.getNumAgents()).thenReturn(2);
        Object jobInfo = newJobInfo(jobRequest);

        assertEquals(2, getJobInfoField(jobInfo, "numberOfMachines"));
        assertFalse(isFilled(jobInfo));
        Method getUsers = jobInfo.getClass().getDeclaredMethod("getUsers", AgentData.class);
        getUsers.setAccessible(true);
        // nonlinear agents take no users of their own, they only count down the region's agents
        assertEquals(0, getUsers.invoke(jobInfo, new AgentData(JOB_ID, "i-1", "u", 5, VMRegion.US_EAST, "z")));
        assertEquals(0, getUsers.invoke(jobInfo, new AgentData(JOB_ID, "i-2", "u", 5, VMRegion.US_WEST_2, "z")));
        assertFalse(isFilled(jobInfo));
        assertEquals(0, getUsers.invoke(jobInfo, new AgentData(JOB_ID, "i-3", "u", 5, VMRegion.US_EAST, "z")));
        assertTrue(isFilled(jobInfo));
    }

    @Test
    void jobInfoForIncreasingJobsSkipsRegionsWithoutUsers() throws Exception {
        RegionRequest east = region(VMRegion.US_EAST, "3", "0");
        RegionRequest west = region(VMRegion.US_WEST_2, "0", "0");
        JobRequest jobRequest = increasingJob(JOB_ID, east, west);
        when(jobRequest.getNumUsersPerAgent()).thenReturn(2);
        Object jobInfo = newJobInfo(jobRequest);

        assertEquals(2, getJobInfoField(jobInfo, "numberOfMachines"));
        Method getUsers = jobInfo.getClass().getDeclaredMethod("getUsers", AgentData.class);
        getUsers.setAccessible(true);
        assertEquals(0, getUsers.invoke(jobInfo, new AgentData(JOB_ID, "i-w", "u", 2, VMRegion.US_WEST_2, "z")));
        assertEquals(2, getUsers.invoke(jobInfo, new AgentData(JOB_ID, "i-1", "u", 2, VMRegion.US_EAST, "z")));
        assertEquals(1, getUsers.invoke(jobInfo, new AgentData(JOB_ID, "i-2", "u", 2, VMRegion.US_EAST, "z")));
        assertTrue(isFilled(jobInfo));
    }

    // ---------------------------------------------------------------- helpers

    private IncreasingWorkLoad stubWorkLoadFactory(JobRequest jobRequest) throws Exception {
        IncreasingWorkLoad workLoad = mock(IncreasingWorkLoad.class);
        when(workLoad.getJob()).thenReturn(jobRequest);
        WorkLoadFactory factory = mock(WorkLoadFactory.class);
        when(factory.getModelRunner(Integer.parseInt(JOB_ID))).thenReturn(workLoad);
        @SuppressWarnings("unchecked")
        Instance<WorkLoadFactory> factoryInstance = mock(Instance.class);
        when(factoryInstance.get()).thenReturn(factory);
        setField("workLoadFactoryInstance", factoryInstance);
        return workLoad;
    }

    private JobRequest increasingJob(String jobId, RegionRequest... regions) {
        JobRequest jobRequest = mock(JobRequest.class);
        when(jobRequest.getId()).thenReturn(jobId);
        when(jobRequest.getIncrementStrategy()).thenReturn(IncrementStrategy.increasing);
        doReturn(new LinkedHashSet<>(List.of(regions))).when(jobRequest).getRegions();
        when(jobRequest.getScriptsXmlUrl()).thenReturn("script.xml");
        when(jobRequest.getNumUsersPerAgent()).thenReturn(1);
        when(jobRequest.getTotalVirtualUsers()).thenReturn(1);
        when(jobRequest.getDataFileIds()).thenReturn(Collections.emptySet());
        return jobRequest;
    }

    private RegionRequest region(VMRegion vmRegion, String users, String percentage) {
        RegionRequest region = mock(RegionRequest.class);
        when(region.getRegion()).thenReturn(vmRegion);
        when(region.getUsers()).thenReturn(users);
        when(region.getPercentage()).thenReturn(percentage);
        return region;
    }

    private AgentWsCommandSender readySender() {
        AgentWsCommandSender wsSender = mock(AgentWsCommandSender.class);
        when(wsSender.hasSession(anyString())).thenReturn(true);
        when(wsSender.isFileTransferReady(anyString())).thenReturn(true);
        when(wsSender.sendCommand(anyString(), anyString(), anyString(), anyLong())).thenReturn(true);
        return wsSender;
    }

    private void setWsSenderInstance(AgentWsCommandSender wsSender) throws Exception {
        @SuppressWarnings("unchecked")
        Instance<AgentWsCommandSender> wsSenderInstance = mock(Instance.class);
        when(wsSenderInstance.isResolvable()).thenReturn(true);
        when(wsSenderInstance.get()).thenReturn(wsSender);
        setField("wsCommandSenderInstance", wsSenderInstance);
    }

    private void stubRegions(VMRegion... regions) {
        VmManagerConfig vmManagerConfig = mock(VmManagerConfig.class);
        when(vmManagerConfig.getRegions()).thenReturn(new LinkedHashSet<>(List.of(regions)));
        when(tankConfig.getVmManagerConfig()).thenReturn(vmManagerConfig);
    }

    @SuppressWarnings("unchecked")
    private static HttpResponse<Object> response(int statusCode) {
        return response(statusCode, URI.create("http://agent:8090"));
    }

    @SuppressWarnings("unchecked")
    private static HttpResponse<Object> response(int statusCode, URI uri) {
        HttpResponse<Object> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(statusCode);
        when(response.uri()).thenReturn(uri);
        return response;
    }

    private Object newJobInfo(JobRequest jobRequest) throws Exception {
        Class<?> jobInfoClass = Class.forName("com.intuit.tank.perfManager.workLoads.JobManager$JobInfo");
        Constructor<?> ctor = jobInfoClass.getDeclaredConstructor(JobRequest.class);
        ctor.setAccessible(true);
        return ctor.newInstance(jobRequest);
    }

    @SuppressWarnings("unchecked")
    private void addAgent(Object jobInfo, String instanceId, String instanceUrl) throws Exception {
        Set<AgentData> agentData = (Set<AgentData>) getJobInfoField(jobInfo, "agentData");
        agentData.add(new AgentData(JOB_ID, instanceId, instanceUrl, 1, VMRegion.US_EAST, "zone"));
    }

    private Object getJobInfoField(Object jobInfo, String name) throws Exception {
        Field field = jobInfo.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(jobInfo);
    }

    private boolean isFilled(Object jobInfo) throws Exception {
        Method isFilled = jobInfo.getClass().getDeclaredMethod("isFilled");
        isFilled.setAccessible(true);
        return (boolean) isFilled.invoke(jobInfo);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> jobInfoCache() throws Exception {
        Field field = JobManager.class.getDeclaredField("jobInfoMapLocalCache");
        field.setAccessible(true);
        return (Map<String, Object>) field.get(fixture);
    }

    /**
     * Runs startTest on this thread with the interrupt flag set, so its 30 second sleep returns at once.
     */
    private void invokeStartTest(Object jobInfo) throws Exception {
        Method startTest = JobManager.class.getDeclaredMethod("startTest", jobInfo.getClass());
        startTest.setAccessible(true);
        Thread.currentThread().interrupt();
        try {
            startTest.invoke(fixture, jobInfo);
        } catch (InvocationTargetException e) {
            throw (Exception) e.getCause();
        } finally {
            Thread.interrupted(); // never leak the flag into later tests
        }
    }

    private static Thread awaitStartTestThread() throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < deadline) {
            for (Map.Entry<Thread, StackTraceElement[]> entry : Thread.getAllStackTraces().entrySet()) {
                for (StackTraceElement frame : entry.getValue()) {
                    if (JobManager.class.getName().equals(frame.getClassName()) && "startTest".equals(frame.getMethodName())) {
                        return entry.getKey();
                    }
                }
            }
            Thread.sleep(10);
        }
        throw new AssertionError("startTest thread was never started");
    }

    private static void awaitBlockedOnLatch(Thread thread) throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < deadline) {
            if (thread.getState() == Thread.State.WAITING) {
                for (StackTraceElement frame : thread.getStackTrace()) {
                    if (CountDownLatch.class.getName().equals(frame.getClassName()) && "await".equals(frame.getMethodName())) {
                        return;
                    }
                }
            }
            Thread.sleep(10);
        }
        throw new AssertionError("startTest never waited for the start agents command");
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field field = JobManager.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(fixture, value);
    }
}

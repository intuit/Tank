package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.MockFlowController;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.reporting.api.DummyResultsReporter;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.message.ObjectMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class APITestHarnessTest {

    private static final String TEST_PLAN_FILE = "src/test/resources/TEST_H.xml";
    private static final String TEST_PLAN_NAME = " project Apache 5 min (id186)";
    private static final String[] DEBUG_LOGGERS = {"com.intuit.tank.http", "com.intuit.tank"};

    private APITestHarness instance;
    private final Map<String, LoggerConfig> originalLoggers = new HashMap<>();

    @BeforeEach
    public void init() {
        instance = APITestHarness.getInstance();
        // -d and -t rewrite the log4j config; remember it so cleanup can put it back
        Configuration config = ((LoggerContext) LogManager.getContext(false)).getConfiguration();
        for (String name : DEBUG_LOGGERS) {
            originalLoggers.put(name, config.getLoggers().get(name));
        }
    }

    @AfterEach
    public void cleanup() {
        APITestHarness.destroyCurrentInstance();
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();
        for (String name : DEBUG_LOGGERS) {
            LoggerConfig original = originalLoggers.get(name);
            if (config.getLoggers().get(name) != original) {
                config.removeLogger(name);
                if (original != null) {
                    config.addLogger(name, original);
                }
            }
        }
        ctx.updateLoggers(config);
    }

    /**
     * Stubs the AmazonUtil calls main() makes to fill the logging ThreadContext.
     */
    private static MockedStatic<AmazonUtil> mockAmazonUtilForMain() {
        MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class);
        amazonUtil.when(AmazonUtil::getJobId).thenReturn("123456");
        amazonUtil.when(AmazonUtil::getProjectName).thenReturn("Test Project");
        amazonUtil.when(AmazonUtil::getZone).thenReturn("c");
        amazonUtil.when(AmazonUtil::getControllerBaseUrl).thenReturn("https://localhost");
        amazonUtil.when(AmazonUtil::getLoggingProfile).thenReturn(LoggingProfile.STANDARD);
        amazonUtil.when(AmazonUtil::usingEip).thenReturn(Boolean.FALSE);
        amazonUtil.when(AmazonUtil::getStopBehavior).thenReturn(StopBehavior.END_OF_SCRIPT);
        amazonUtil.when(() -> AmazonUtil.getMetaData(Mockito.any(CloudMetaDataType.class)))
                .thenThrow(new IOException("not in amazon"));
        return amazonUtil;
    }

    private static MockHDWorkload emptyWorkload() {
        MockHDWorkload workload = new MockHDWorkload();
        workload.setVariables(new HDTestVariables());
        return workload;
    }

    /**
     * Starts a thread that blocks until it is interrupted, completing the returned future when it is.
     */
    private static Thread startBlockedThread(CompletableFuture<Boolean> interrupted) {
        Thread thread = new Thread(() -> {
            try {
                new CountDownLatch(1).await();
            } catch (InterruptedException e) {
                interrupted.complete(true);
            }
        });
        thread.setDaemon(true);
        thread.start();
        return thread;
    }

    @Test
    public void testSingletonPattern() {
        assertNotNull(instance);

        APITestHarness copyInstance = APITestHarness.getInstance();
        assertEquals(instance, copyInstance);

        APITestHarness.destroyCurrentInstance();

        APITestHarness newInstance = APITestHarness.getInstance();
        assertNotEquals(instance, newInstance);
    }

    @Test
    public void testInitializeFromMainArgs() throws IOException {
        try (MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class)) {
            amazonUtil.when(AmazonUtil::getJobId).thenReturn("123456");
            amazonUtil.when(AmazonUtil::getProjectName).thenReturn("Test Project");
            amazonUtil.when(AmazonUtil::getZone).thenReturn("c");
            amazonUtil.when(AmazonUtil::getControllerBaseUrl).thenReturn("https://localhost");
            amazonUtil.when(AmazonUtil::getLoggingProfile).thenReturn(LoggingProfile.STANDARD);
            amazonUtil.when(AmazonUtil::usingEip).thenReturn(Boolean.FALSE);
            amazonUtil.when(AmazonUtil::getStopBehavior).thenReturn(StopBehavior.END_OF_SCRIPT);
            APITestHarness instance = APITestHarness.getInstance();

            instance.setDebug(true);
            instance.setFlowControllerTemplate(new MockFlowController());

            String tankHttpClientClass = "com.intuit.tank.httpclient4.TankHttpClient4";

            String[] args = {"-instanceId=testInstance", "-logging=Standard", "-users=10", "-capacity=100", "-start=5",
                    "-jobId=2345", "-stopBehavior=Script Group", "-time=5", "-client=" + tankHttpClientClass};

            APITestHarness.main(args);

            assertEquals("testInstance", instance.getInstanceId());
            assertEquals("Standard", instance.getAgentRunData().getActiveProfile().getDisplayName());
            assertEquals(10, instance.getAgentRunData().getNumUsers());
            assertEquals(5, instance.getAgentRunData().getNumStartUsers());
            assertEquals("2345", instance.getAgentRunData().getJobId());
            assertEquals("Script Group", instance.getAgentRunData().getStopBehavior().getDisplay());
            assertEquals(300000L, instance.getAgentRunData().getSimulationTimeMillis());
            assertEquals(tankHttpClientClass, instance.getTankHttpClientClass());
        }
    }

    @Test
    public void testUsageMain() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            APITestHarness.main(new String[]{});
            String output = baos.toString();
            assertTrue(output.contains("API Test Harness Usage:"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testAddValidations(){
        APITestHarness instance = APITestHarness.getInstance();
        IntStream.range(0, 7).forEach(i -> instance.addKill());
        IntStream.range(0, 2).forEach(i -> instance.addAbort());
        IntStream.range(0, 6).forEach(i -> instance.addGoto());
        IntStream.range(0, 3).forEach(i -> instance.addSkip());
        IntStream.range(0, 1).forEach(i -> instance.addSkipGroup());
        IntStream.range(0, 9).forEach(i ->  instance.addRestart());
        WatsAgentStatusResponse response = instance.getStatus();

        assertEquals(7, response.getKills());
        assertEquals(2, response.getAborts());
        assertEquals(6, response.getGotos());
        assertEquals(3, response.getSkips());
        assertEquals(1, response.getSkipGroups());
        assertEquals(9, response.getRestarts());
    }

    @Test
    public void testRunConcurrentTestPlans(){
        try (MockedStatic<LogUtil> logUtilMockedStatic = Mockito.mockStatic(LogUtil.class)) {
            logUtilMockedStatic.when(() -> LogUtil.getLogMessage(Mockito.anyString())).thenReturn(new ObjectMessage("test"));
            APITestHarness instance = APITestHarness.getInstance();
            MockHDWorkload workload = new MockHDWorkload();
            HDTestPlan testPlan = new HDTestPlan();
            testPlan.setUserPercentage(5);
            testPlan.setTestPlanName("testPlan");
            workload.setVariables(new HDTestVariables());
            workload.addPlan(testPlan);
            TestPlanSingleton.getInstance().setTestPlan(workload);
            String tankHttpClientClass = "com.intuit.tank.httpclient4.TankHttpClient4";
            instance.getAgentRunData().setSimulationTimeMillis(1L);
            instance.getAgentRunData().setIncrementStrategy(IncrementStrategy.increasing);
            instance.setFlowControllerTemplate(new MockFlowController());
            instance.setDebug(true);

            assertFalse(instance.isStarted());
            assertEquals(0, instance.getCurrentUsers());
            instance.runConcurrentTestPlans();
            instance.threadStarted(new Thread());
            assertEquals(1, instance.getCurrentUsers());
            assertTrue(instance.isStarted());
            assertEquals(tankHttpClientClass, instance.getTankHttpClientClass());
            assertEquals(tankHttpClientClass, instance.getAgentRunData().getTankhttpClientClass());
            assertNotEquals(0, instance.getStartTime());
            assertNotNull(instance.getTPSMonitor());
            assertEquals(new TPSMonitor(30).getTPSInfo().getPeriod(),
                    instance.getTPSMonitor().getTPSInfo().getPeriod());
            instance.threadComplete();
            assertEquals(0, instance.getCurrentUsers());
        }
    }

    @Test
    public void testGetInitialStatus() {
        APITestHarness instance = APITestHarness.getInstance();
        CloudVmStatus status = instance.getInitialStatus();
        assertNotNull(status);
        assertEquals("0", status.getJobId());
        assertEquals("Unknown", status.getJobStatus().name());
        assertEquals("AGENT", status.getRole().name());
        assertEquals("running", status.getVmStatus().name());
        assertEquals(0, status.getTotalUsers());
        assertEquals(0, status.getCurrentUsers());
        assertEquals(0, status.getTotalTps());
        assertNotEquals(0, status.getStartTime().getTime());
    }

    @Test
    public void testDebug() {
        APITestHarness instance = APITestHarness.getInstance();
        instance.setDebug(true);
        assertTrue(instance.isDebug());
    }


    // Nonlinear Workflow

    @Test
    public void testConfigureNonlinearAgentRunData() {
        APITestHarness instance = APITestHarness.getInstance();
        instance.getAgentRunData().setIncrementStrategy(IncrementStrategy.standard);
        MockHDWorkload workload = new MockHDWorkload();
        HDTestPlan testPlan = new HDTestPlan();
        testPlan.setUserPercentage(5);
        testPlan.setTestPlanName("testPlan");
        workload.setVariables(new HDTestVariables());
        workload.addPlan(testPlan);
        TestPlanSingleton.getInstance().setTestPlan(workload);
        instance.setDebug(true);

        MockFlowController controller = new MockFlowController();
        instance.setFlowControllerTemplate(controller);

        instance.getAgentRunData().setTargetRampRate(0.7);
        instance.getAgentRunData().setRampTimeMillis(800);

        instance.runConcurrentTestPlans(); // Run nonlinear workflow

        assertEquals(0.7, instance.getAgentRunData().getTargetRampRate());
    }

    @Test
    public void testMainWithoutControllerRunsLocalPlanAndFlushesResults() {
        List<List<?>> monitorArgs = new ArrayList<>();
        try (MockedStatic<AmazonUtil> ignored = mockAmazonUtilForMain();
             MockedConstruction<DummyResultsReporter> reporters = Mockito.mockConstruction(DummyResultsReporter.class);
             MockedConstruction<APIMonitor> monitors = Mockito.mockConstruction(APIMonitor.class,
                     (monitor, context) -> monitorArgs.add(context.arguments()));
             MockedStatic<APIMonitor> apiMonitor = Mockito.mockStatic(APIMonitor.class)) {
            instance.setFlowControllerTemplate(new MockFlowController());
            // let the clock pass the send deadline set at construction, so the first result triggers
            // an (empty) flush and pushes the deadline out; both results then wait for the end of the test
            long constructed = System.currentTimeMillis();
            while (System.currentTimeMillis() <= constructed) {
                Thread.onSpinWait();
            }
            instance.queueTimingResult(new TankResult());
            instance.queueTimingResult(new TankResult());

            APITestHarness.main(new String[]{"-tp=" + TEST_PLAN_FILE, "-users=0",
                    "-instanceId=local-agent", "-jobId=local-job"});

            assertTrue(instance.isStarted());
            assertTrue(instance.isLocal());
            assertEquals(TEST_PLAN_NAME, instance.getAgentRunData().getProjectName());
            assertEquals("local-agent", instance.getAgentRunData().getInstanceId());
            assertEquals("local-agent", instance.getAgentRunData().getMachineName());
            assertNotEquals(0, instance.getStartTime());

            // the queued results are flushed to the local reporter when the test completes
            assertEquals(1, reporters.constructed().size());
            DummyResultsReporter reporter = reporters.constructed().get(0);
            assertSame(reporter, instance.getResultsReporter());
            verify(reporter).sendTimingResults(eq("local-job"), eq("local-agent"),
                    argThat(results -> results.size() == 2), eq(false));

            // a local monitor is started, then told the job completed
            assertEquals(1, monitors.constructed().size());
            assertEquals(Boolean.TRUE, monitorArgs.get(0).get(0));
            CloudVmStatus status = (CloudVmStatus) monitorArgs.get(0).get(1);
            assertEquals(VMRegion.STANDALONE, status.getVmRegion());
            assertEquals("local-job", status.getJobId());
            apiMonitor.verify(() -> APIMonitor.setJobStatus(JobStatus.Completed));
            apiMonitor.verify(() -> APIMonitor.setDoMonitor(false));
        }
    }

    @Test
    public void testMainFallsBackToLocalInstanceId() {
        String expected;
        try {
            expected = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            expected = "local-instance";
        }
        try (MockedStatic<AmazonUtil> amazonUtil = mockAmazonUtilForMain();
             MockedConstruction<DummyResultsReporter> ignoredReporters = Mockito.mockConstruction(DummyResultsReporter.class);
             MockedConstruction<APIMonitor> ignoredMonitors = Mockito.mockConstruction(APIMonitor.class);
             MockedStatic<APIMonitor> ignoredApiMonitor = Mockito.mockStatic(APIMonitor.class)) {
            // the metadata lookup finds no instance id
            amazonUtil.when(AmazonUtil::getInstanceId).thenReturn("");
            instance.setFlowControllerTemplate(new MockFlowController());

            APITestHarness.main(new String[]{"-tp=" + TEST_PLAN_FILE, "-users=0"});
            assertEquals(expected, instance.getInstanceId());
            assertEquals(expected, instance.getAgentRunData().getMachineName());

            // main() reads the instance id for logging first; the lookup for the agent itself then fails
            amazonUtil.when(AmazonUtil::getInstanceId).thenReturn("i-logged")
                    .thenThrow(new IllegalStateException("no metadata"));
            APITestHarness.destroyCurrentInstance();
            APITestHarness second = APITestHarness.getInstance();
            second.setFlowControllerTemplate(new MockFlowController());
            APITestHarness.main(new String[]{"-tp=" + TEST_PLAN_FILE, "-users=0"});
            assertEquals(expected, second.getInstanceId());
            assertTrue(second.isLocal());
        }
    }

    @Test
    public void testMainParsesFlagsThenStopsOnMissingTestPlan() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try (MockedStatic<AmazonUtil> ignored = mockAmazonUtilForMain()) {
            APITestHarness.main(new String[]{"-ramp=2", "-local", "-capacity=7", "-d", "-tp=does-not-exist.xml"});

            assertTrue(baos.toString().contains("API Test Harness Usage:"));
            assertEquals(120000L, instance.getAgentRunData().getRampTimeMillis());
            assertTrue(instance.isDebug());
            assertTrue(instance.isLocal());
            assertInstanceOf(DebugFlowController.class, instance.getFlowControllerTemplate());
            assertEquals(LoggingProfile.VERBOSE, instance.getAgentRunData().getActiveProfile());
            // parsing stopped at the bad test plan, before the instance id was resolved or a test started
            assertNull(instance.getInstanceId());
            assertFalse(instance.isStarted());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testMainTraceFlagSelectsTraceFlowController() {
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
        try (MockedStatic<AmazonUtil> ignored = mockAmazonUtilForMain()) {
            APITestHarness.main(new String[]{"-t", "-tp=does-not-exist.xml"});

            assertTrue(instance.isDebug());
            assertInstanceOf(TraceFlowController.class, instance.getFlowControllerTemplate());
            assertFalse(instance.isStarted());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testRunConcurrentTestPlansIgnoresSecondStart() {
        FlowController flowController = mock(FlowController.class);
        instance.setFlowControllerTemplate(flowController);
        instance.setDebug(true);
        TestPlanSingleton.getInstance().setTestPlan(emptyWorkload());

        // without a job id the run falls back to the one in the user data
        instance.getAgentRunData().setJobId(null);
        try (MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class)) {
            amazonUtil.when(AmazonUtil::getJobId).thenReturn("from-user-data");

            instance.runConcurrentTestPlans();
            long startTime = instance.getStartTime();
            instance.runConcurrentTestPlans();

            assertTrue(instance.isStarted());
            assertEquals("from-user-data", instance.getAgentRunData().getJobId());
            assertEquals(startTime, instance.getStartTime());
            verify(flowController, times(1)).endTest();
            amazonUtil.verify(AmazonUtil::getJobId, times(1));
        }
    }

    @Test
    public void testRunConcurrentTestPlansEndsWhenInterrupted() {
        FlowController flowController = mock(FlowController.class);
        instance.setFlowControllerTemplate(flowController);
        instance.setDebug(true);
        instance.getAgentRunData().setNumUsers(2);
        TestPlanSingleton.getInstance().setTestPlan(emptyWorkload());
        try {
            // an interrupt while waiting for the users to finish stops the run
            Thread.currentThread().interrupt();
            instance.runConcurrentTestPlans();

            assertFalse(Thread.currentThread().isInterrupted());
            assertTrue(instance.isStarted());
            verify(flowController).endTest();
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    public void testRunConcurrentTestPlansSurvivesBadHttpClientClass() {
        FlowController flowController = mock(FlowController.class);
        instance.setFlowControllerTemplate(flowController);
        instance.setDebug(true);
        instance.setTankHttpClientClass("com.intuit.tank.DoesNotExist");
        TestPlanSingleton.getInstance().setTestPlan(emptyWorkload());

        instance.runConcurrentTestPlans();

        assertTrue(instance.isStarted());
        assertEquals("com.intuit.tank.DoesNotExist", instance.getAgentRunData().getTankhttpClientClass());
        // the class lookup failed before any users started, but the test still ends cleanly
        assertEquals(0, instance.getStartTime());
        verify(flowController).endTest();
    }

    @Test
    public void testCheckAgentThreadsInterruptsUsersAfterSimulationTime() throws Exception {
        instance.setFlowControllerTemplate(new MockFlowController());
        instance.setDebug(true);
        instance.getAgentRunData().setIncrementStrategy(IncrementStrategy.increasing);
        instance.getAgentRunData().setNumUsers(2);
        instance.getAgentRunData().setSimulationTimeMillis(1L);
        TestPlanSingleton.getInstance().setTestPlan(emptyWorkload());
        instance.runConcurrentTestPlans();
        assertEquals(instance.getStartTime() + 1L, instance.getSimulationEndTimeMillis());

        CompletableFuture<Boolean> interrupted = new CompletableFuture<>();
        Thread user = startBlockedThread(interrupted);
        instance.threadStarted(user);

        long deadline = System.currentTimeMillis() + 5000;
        while (!instance.hasMetSimulationTime() && System.currentTimeMillis() < deadline) {
            Thread.onSpinWait();
        }
        assertTrue(instance.hasMetSimulationTime());
        instance.checkSimulationTime();

        instance.checkAgentThreads();

        assertTrue(interrupted.get(5, TimeUnit.SECONDS));
        user.join(5000);
        assertFalse(user.isAlive());
        // a second check finds nothing left to interrupt and prunes the finished thread
        instance.checkAgentThreads();
    }

    @Test
    public void testCheckAgentThreadsLeavesUsersRunningBeforeSimulationTime() {
        CompletableFuture<Boolean> interrupted = new CompletableFuture<>();
        Thread user = startBlockedThread(interrupted);
        instance.threadStarted(user);
        try {
            // no simulation time is configured, so it is never met
            assertFalse(instance.hasMetSimulationTime());
            instance.checkAgentThreads();
            assertTrue(user.isAlive());
            assertFalse(interrupted.isDone());
        } finally {
            user.interrupt();
        }
    }

    @Test
    public void testThreadCompleteReleasesNonlinearPermit() {
        instance.setFlowControllerTemplate(new MockFlowController());
        instance.setDebug(true);
        instance.getAgentRunData().setIncrementStrategy(IncrementStrategy.standard);
        TestPlanSingleton.getInstance().setTestPlan(emptyWorkload());
        instance.runConcurrentTestPlans();

        instance.threadStarted(new Thread());
        instance.threadStarted(new Thread());
        assertEquals(2, instance.getCurrentUsers());
        instance.threadComplete();
        instance.threadComplete();
        assertEquals(0, instance.getCurrentUsers());
    }

    @Test
    public void testGetStatusReportsRemainingRampTime() {
        instance.getAgentRunData().setNumUsers(10);
        instance.getAgentRunData().setNumStartUsers(2);
        instance.getAgentRunData().setRampTimeMillis(8000);
        IntStream.range(0, 4).forEach(i -> instance.threadStarted(new Thread()));
        assertNotNull(instance.getUserTracker());

        WatsAgentStatusResponse response = instance.getStatus();

        // 4 of the 8 ramped users have started, so half the ramp time is left
        assertEquals(4000, response.getRampTimeLeft());
        assertEquals(4, response.getCurrentNumberUsers());
        assertEquals(10, response.getMaxVirtualUsers());
    }

    @Test
    public void testMaxSimulationEndAddsOverSimulationTime() {
        instance.getAgentRunData().setSimulationTimeMillis(60000);
        long overTime = instance.getTankConfig().getAgentConfig().getOverSimulationMaxTime();

        assertEquals(60000L, instance.getSimulationEndTimeMillis());
        assertEquals(60000L + overTime, instance.getMaxSimulationEndTimeMillis());
    }

    @Test
    public void testSetCommandUpdatesJobStatusUntilStopped() throws Exception {
        CompletableFuture<Boolean> interrupted = new CompletableFuture<>();
        Thread user = startBlockedThread(interrupted);
        instance.threadStarted(user);
        try (MockedStatic<APIMonitor> apiMonitor = Mockito.mockStatic(APIMonitor.class)) {
            instance.setCommand(AgentCommand.pause);
            apiMonitor.verify(() -> APIMonitor.setJobStatus(JobStatus.Paused));
            // pausing interrupts the user threads so they notice the new command
            assertTrue(interrupted.get(5, TimeUnit.SECONDS));

            instance.setCommand(AgentCommand.pause_ramp);
            apiMonitor.verify(() -> APIMonitor.setJobStatus(JobStatus.RampPaused));
            instance.setCommand(AgentCommand.resume_ramp);
            apiMonitor.verify(() -> APIMonitor.setJobStatus(JobStatus.Running));
            instance.setCommand(AgentCommand.stop);
            apiMonitor.verify(() -> APIMonitor.setJobStatus(JobStatus.Stopped));

            // once stopped, later commands are ignored
            instance.setCommand(AgentCommand.run);
            assertEquals(AgentCommand.stop, instance.getCmd());
            apiMonitor.verify(() -> APIMonitor.setJobStatus(JobStatus.Running), times(1));
        }
    }

    @Test
    public void testGetInitialStatusInAmazon() {
        try (MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class)) {
            amazonUtil.when(() -> AmazonUtil.getMetaData(CloudMetaDataType.zone)).thenReturn("us-east-1a");
            amazonUtil.when(() -> AmazonUtil.getMetaData(CloudMetaDataType.security_groups))
                    .thenReturn("sg-first\nsg-second");

            CloudVmStatus status = instance.getInitialStatus();

            assertEquals(VMRegion.US_EAST, status.getVmRegion());
            assertEquals("us-east-1a", status.getZone());
            // only the first security group is reported
            assertEquals("sg-first", status.getSecurityGroup());
            assertEquals("0", status.getJobId());
        }
    }

    @Test
    public void testGetInitialStatusWithUnreadableSecurityGroups() {
        try (MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class)) {
            amazonUtil.when(() -> AmazonUtil.getMetaData(CloudMetaDataType.zone)).thenReturn("us-east-2b");
            amazonUtil.when(() -> AmazonUtil.getMetaData(CloudMetaDataType.security_groups))
                    .thenThrow(new IOException("forbidden"));

            CloudVmStatus status = instance.getInitialStatus();

            assertEquals(VMRegion.US_EAST_2, status.getVmRegion());
            assertEquals("unknown", status.getSecurityGroup());
        }
    }

    @Test
    public void testGetInitialStatusOutsideAmazon() {
        try (MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class)) {
            amazonUtil.when(() -> AmazonUtil.getMetaData(CloudMetaDataType.zone)).thenThrow(new IOException("timeout"));

            CloudVmStatus status = instance.getInitialStatus();

            assertEquals(VMRegion.STANDALONE, status.getVmRegion());
            assertNull(status.getZone());
            assertEquals("unknown", status.getSecurityGroup());
            amazonUtil.verify(() -> AmazonUtil.getMetaData(CloudMetaDataType.security_groups), never());
        }
    }

    @Test
    public void testGetInitialStatusFallsBackOnError() {
        try (MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class)) {
            // a null zone cannot be mapped to a region
            amazonUtil.when(() -> AmazonUtil.getMetaData(CloudMetaDataType.zone)).thenReturn(null);

            CloudVmStatus status = instance.getInitialStatus();

            assertEquals("unknown", status.getJobId());
            assertEquals("wats-dev", status.getSecurityGroup());
            assertEquals(VMRegion.US_EAST, status.getVmRegion());
            assertEquals(JobStatus.Unknown, status.getJobStatus());
        }
    }

}

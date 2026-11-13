package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClientBuilder;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataResponse;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

public class TestPlanStarterTest {

    private Object _httpClientMock;
    private HDTestPlan _hdTestPlanMock;
    private AgentRunData _agentRunData;
    private APITestHarness _harnessMock;
    private APITestHarness _originalHarness;
    private CloudWatchAsyncClient _cloudWatchClientMock;
    private TPSMonitor _tpsMonitorMock;

    private final String _tankHttpClientClassStub = "com.intuit.tank.httpclient4.TankHttpClient4";
    private final String _threadGroupNameStub = "Test Plan Runner Group: stub";
    private final int _threadCountStub = 10;

    // the harness command, so the mocked harness behaves like the real one between getCmd and setCommand
    private final AtomicReference<AgentCommand> _cmd = new AtomicReference<>(AgentCommand.run);
    private final AtomicInteger _threadsReported = new AtomicInteger();
    // the harness issues a stop once this many user threads have started
    private final AtomicInteger _stopAfterThreads = new AtomicInteger(Integer.MAX_VALUE);
    // holds the stubbed user sessions open so they count as active until the test ends
    private final CountDownLatch _releaseRunners = new CountDownLatch(1);

    private TestPlanStarter _sut;

    private MockedStatic<CloudWatchAsyncClient> mock_CloudWatchAsyncClient;
    private MockedStatic<AmazonUtil> mock_AmazonUtil;

    @BeforeEach
    public void SetUp() throws Exception {
        _httpClientMock = mock(Object.class);
        _hdTestPlanMock = mock(HDTestPlan.class);
        _agentRunData = mock(AgentRunData.class);
        mock_CloudWatchAsyncClient = mockStatic(CloudWatchAsyncClient.class);
        CloudWatchAsyncClientBuilder builder = mock(CloudWatchAsyncClientBuilder.class, RETURNS_SELF);
        _cloudWatchClientMock = mock(CloudWatchAsyncClient.class);
        when(builder.build()).thenReturn(_cloudWatchClientMock);
        when(CloudWatchAsyncClient.builder()).thenReturn(builder);
        when(_cloudWatchClientMock.putMetricData(any(PutMetricDataRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(PutMetricDataResponse.builder().build()));
        mock_AmazonUtil = mockStatic(AmazonUtil.class);
        when(AmazonUtil.getInstanceId()).thenReturn("i-test");
        when(AmazonUtil.getJobId()).thenReturn("42");
        when(_agentRunData.getIncrementStrategy()).thenReturn(IncrementStrategy.increasing);
        when(_hdTestPlanMock.getTestPlanName()).thenReturn("plan");

        _harnessMock = mock(APITestHarness.class);
        // what a log event reads from the harness, should one be built on a thread new to logging
        when(_harnessMock.getAgentRunData()).thenReturn(new AgentRunData());
        when(_harnessMock.getTankConfig()).thenReturn(mock(TankConfig.class));
        when(_harnessMock.isLocal()).thenReturn(true);
        _tpsMonitorMock = mock(TPSMonitor.class);
        when(_harnessMock.getTPSMonitor()).thenReturn(_tpsMonitorMock);
        when(_harnessMock.getCmd()).thenAnswer(invocation -> _cmd.get());
        doAnswer(invocation -> {
            _cmd.set(invocation.getArgument(0));
            return null;
        }).when(_harnessMock).setCommand(any());
        doAnswer(invocation -> {
            if (_threadsReported.incrementAndGet() >= _stopAfterThreads.get()) {
                _cmd.set(AgentCommand.stop);
            }
            return null;
        }).when(_harnessMock).threadStarted(any());
        _originalHarness = swapHarness(null);
        LogUtil.getLogEvent(); // build this thread's log event from a real harness, as other tests would
        swapHarness(_harnessMock);
    }

    @AfterEach
    public void clearStaticMock() throws Exception {
        Thread.interrupted(); // clear any wake-up a pause test left unused
        _releaseRunners.countDown();
        swapHarness(_originalHarness);
        mock_AmazonUtil.close();
        mock_CloudWatchAsyncClient.close();
    }

    @Test
    public void getPlan_Returns_Initialized_HDTestPlan() {
        // Arrange + Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);

        // Assert
        assertEquals(_hdTestPlanMock, _sut.getPlan(), "TestPlan assigned to TestPlanStarter");
    }

    @Test
    public void constructor_Without_AWS_Region_Skips_CloudWatch() {
        // Arrange: a standalone agent off EC2 has no region for the client
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        when(CloudWatchAsyncClient.builder().build())
                .thenThrow(SdkClientException.create("Unable to load region from any of the providers"));

        // Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, _threadCountStub, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);

        // Assert: the plan still runs, without the CloudWatch dimensions
        assertEquals(_threadCountStub, _sut.getNumThreads());
        mock_AmazonUtil.verify(AmazonUtil::getInstanceId, never());
    }

    @Test
    public void getNumThreads_Returns_Initialized_Number_Of_Threads() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);

        // Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, _threadCountStub, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);

        // Assert
        assertEquals(_threadCountStub, _sut.getNumThreads());
    }

    @Test
    public void getThreadStarted_Given_TestPlanStarter_Not_Run_Returns_Zero() {
        // Arrange + Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);

        // Assert
        assertEquals(0, _sut.getThreadsStarted());
    }

    @Test
    public void isDone_Given_TestPlanStarter_Not_Run_Returns_False() {
        // Arrange + Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);

        // Assert
        assertFalse(_sut.isDone());
    }

    @Test
    public void getNumThreads_Given_User_Percentage_Scales_Threads_With_Minimum_Of_One() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(25);

        // Act
        TestPlanStarter quarter = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, _threadCountStub, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
        TestPlanStarter tiny = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);

        // Assert
        assertEquals(2, quarter.getNumThreads());
        assertEquals(1, tiny.getNumThreads());
    }

    @Test
    public void constructor_Given_Single_Linear_User_Runs_Standalone_Without_CloudWatch() {
        // Arrange
        linearWorkload(1);
        stopAfterThreads(1);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
            sendMetricsOnNextCheck(_sut);
            _sut.run();
        }

        // Assert
        mock_CloudWatchAsyncClient.verify(CloudWatchAsyncClient::builder, never());
        verifyNoInteractions(_cloudWatchClientMock);
        assertEquals(1, _sut.getThreadsStarted());
    }

    @Test
    public void run_Linear_Starts_Initial_Users_Then_Ramps_To_Thread_Count_Until_Stopped() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        linearWorkload(1);
        stopAfterThreads(3);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
            sendMetricsOnNextCheck(_sut);
            _sut.run();

            // Assert
            assertEquals(3, runners.constructed().size());
            for (TestPlanRunner runner : runners.constructed()) {
                verify(runner).setUniqueName(startsWith(_threadGroupNameStub + "-"));
            }
        }
        assertTrue(_sut.isDone());
        assertEquals(3, _sut.getThreadsStarted());
        verify(_harnessMock, times(3)).threadStarted(any(Thread.class));
    }

    @Test
    public void run_Linear_Sends_Thread_Metrics_To_CloudWatch() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        linearWorkload(1);
        stopAfterThreads(2);
        TPSInfoContainer tpsInfo = mock(TPSInfoContainer.class);
        when(tpsInfo.getTotalTps()).thenReturn(7);
        when(_tpsMonitorMock.getTPSInfo()).thenReturn(tpsInfo);
        // a failed push is logged and does not interrupt the ramp
        when(_cloudWatchClientMock.putMetricData(any(PutMetricDataRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new IllegalStateException("cloudwatch down")));

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
            sendMetricsOnNextCheck(_sut);
            _sut.run();
        }

        // Assert
        ArgumentCaptor<PutMetricDataRequest> request = ArgumentCaptor.forClass(PutMetricDataRequest.class);
        verify(_cloudWatchClientMock).putMetricData(request.capture());
        assertEquals("Intuit/Tank", request.getValue().namespace());
        Map<String, Double> metrics = request.getValue().metricData().stream()
                .collect(Collectors.toMap(MetricDatum::metricName, MetricDatum::value));
        assertEquals(2.0, metrics.get("startedThreads"));
        assertEquals(1.0, metrics.get("activeThreads"));
        assertEquals(1.0, metrics.get("sessionStarts"));
        assertEquals(7.0, metrics.get("totalTps"));
        assertEquals(0.0, metrics.get("userRampRate"));
        assertEquals(3, request.getValue().metricData().get(0).dimensions().size());
        assertTrue(_sut.isDone());
    }

    @Test
    public void run_Linear_Given_More_Start_Users_Than_Threads_Starts_Only_Thread_Count() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        linearWorkload(5);
        when(_agentRunData.getRampTimeMillis()).thenReturn(1000L);
        stopAfterThreads(2);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 2, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
            _sut.run();
        }

        // Assert
        assertEquals(2, _sut.getThreadsStarted());
        assertTrue(_sut.isDone());
    }

    @Test
    public void run_Linear_Given_Run_Until_Loops_Completed_Stops_Once_Ramp_Time_Passed() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        linearWorkload(1);
        when(_agentRunData.getSimulationTimeMillis()).thenReturn(0L);
        when(_harnessMock.getStartTime()).thenReturn(System.currentTimeMillis() - 60_000);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
            _sut.run();
        }

        // Assert
        assertTrue(_sut.isDone());
        assertEquals(1, _sut.getThreadsStarted(), "only the initial user starts");
    }

    @Test
    public void run_Linear_Given_Debug_Stops_After_Initial_Users() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        linearWorkload(1);
        when(_harnessMock.isDebug()).thenReturn(true);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
            _sut.run();
        }

        // Assert
        assertTrue(_sut.isDone());
        assertEquals(1, _sut.getThreadsStarted());
    }

    @Test
    public void run_Linear_Given_Pause_And_Simulation_Time_Met_Stops_The_Test() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        linearWorkload(0);
        _cmd.set(AgentCommand.pause);
        when(_harnessMock.hasMetSimulationTime()).thenReturn(true);

        // Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 2, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
        _sut.run();

        // Assert
        assertTrue(_sut.isDone());
        assertEquals(AgentCommand.stop, _cmd.get());
        assertEquals(0, _sut.getThreadsStarted());
    }

    @Test
    public void run_Linear_Given_Pause_Ramp_Holds_Users_Until_Resumed() throws Exception {
        // Arrange
        linearWorkload(0);
        _cmd.set(AgentCommand.pause_ramp);
        stopAfterThreads(1);
        CountDownLatch paused = signalWhilePaused(1);
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 1, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
        PauseObserver observer = applyWhenPaused(paused, AgentCommand.resume_ramp);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut.run();
        }
        observer.thread.join(5000);

        // Assert
        assertTrue(observer.reachedPause, "starter waits in the pause loop");
        assertEquals(0, observer.threadsStartedWhilePaused, "no users start while the ramp is paused");
        assertTrue(_sut.isDone());
        assertEquals(1, _sut.getThreadsStarted(), "the ramp continues once resumed");
    }

    @Test
    public void run_Linear_Propagates_Unexpected_Errors() {
        // Arrange
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        linearWorkload(0);
        doThrow(new IllegalStateException("boom")).when(_harnessMock).checkSimulationTime();
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 2, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);

        // Act + Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, _sut::run);
        assertEquals("boom", thrown.getMessage());
        assertFalse(_sut.isDone());
    }

    @Test
    public void run_Nonlinear_Ramps_Up_Then_Keeps_Adding_Users_Until_Stopped() {
        // Arrange
        nonlinearWorkload(1, 300, 20);
        stopAfterThreads(5);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
            sendMetricsOnNextCheck(_sut);
            _sut.run();

            // Assert
            assertEquals(_sut.getThreadsStarted(), runners.constructed().size());
        }
        assertTrue(_sut.isDone());
        assertTrue(_sut.getThreadsStarted() >= 5, "nonlinear ramp is not capped by the thread count");
        verify(_cloudWatchClientMock, atLeastOnce()).putMetricData(any(PutMetricDataRequest.class));
    }

    @Test
    public void run_Nonlinear_Given_Stop_During_Initial_Ramp_Starts_Only_Initial_Users() {
        // Arrange
        nonlinearWorkload(2, 10_000, 5);
        _cmd.set(AgentCommand.stop);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
            _sut.run();
        }

        // Assert
        assertTrue(_sut.isDone());
        assertEquals(2, _sut.getThreadsStarted());
    }

    @Test
    public void run_Nonlinear_Given_Pause_During_Initial_Ramp_And_Simulation_Time_Met_Stops_The_Test() {
        // Arrange
        nonlinearWorkload(0, 10_000, 5);
        _cmd.set(AgentCommand.pause);
        when(_harnessMock.hasMetSimulationTime()).thenReturn(true);

        // Act
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
        _sut.run();

        // Assert
        assertTrue(_sut.isDone());
        assertEquals(AgentCommand.stop, _cmd.get());
        assertEquals(0, _sut.getThreadsStarted());
    }

    @Test
    public void run_Nonlinear_Given_Pause_During_Initial_Ramp_Waits_Until_Stopped() throws Exception {
        // Arrange
        nonlinearWorkload(0, 10_000, 5);
        _cmd.set(AgentCommand.pause);
        // the first check while paused leaves the ramp step, the second is inside the pause wait
        CountDownLatch paused = signalWhilePaused(2);
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
        PauseObserver observer = applyWhenPaused(paused, AgentCommand.stop);

        // Act
        _sut.run();
        observer.thread.join(5000);

        // Assert
        assertTrue(observer.reachedPause, "starter waits in the initial ramp pause loop");
        assertFalse(observer.doneWhilePaused);
        assertTrue(_sut.isDone());
        assertEquals(0, _sut.getThreadsStarted());
    }

    @Test
    public void run_Nonlinear_Given_Pause_After_Ramp_Resumes_Adding_Users() throws Exception {
        // Arrange
        nonlinearWorkload(0, 0, 20);
        _cmd.set(AgentCommand.pause);
        stopAfterThreads(1);
        CountDownLatch paused = signalWhilePaused(1);
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);
        PauseObserver observer = applyWhenPaused(paused, AgentCommand.resume_ramp);

        // Act
        try (MockedConstruction<TestPlanRunner> runners = mockRunners()) {
            _sut.run();
        }
        observer.thread.join(5000);

        // Assert
        assertTrue(observer.reachedPause, "starter waits in the pause loop");
        assertEquals(0, observer.threadsStartedWhilePaused, "no users start while paused");
        assertTrue(_sut.isDone());
        assertEquals(1, _sut.getThreadsStarted(), "users are added again once resumed");
    }

    @Test
    public void run_Nonlinear_Propagates_Unexpected_Errors() {
        // Arrange
        nonlinearWorkload(0, 0, 5);
        doThrow(new IllegalStateException("boom")).when(_harnessMock).checkSimulationTime();
        _sut = new TestPlanStarter(_httpClientMock, _hdTestPlanMock, 3, _tankHttpClientClassStub, _threadGroupNameStub, _agentRunData);

        // Act + Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, _sut::run);
        assertEquals("boom", thrown.getMessage());
        assertFalse(_sut.isDone());
    }

    private void linearWorkload(int numStartUsers) {
        when(_agentRunData.getNumStartUsers()).thenReturn(numStartUsers);
        when(_agentRunData.getUserInterval()).thenReturn(1);
        when(_agentRunData.getSimulationTimeMillis()).thenReturn(60_000L);
    }

    private void nonlinearWorkload(int numStartUsers, long rampTimeMillis, double targetRampRate) {
        when(_hdTestPlanMock.getUserPercentage()).thenReturn(100);
        when(_agentRunData.getIncrementStrategy()).thenReturn(IncrementStrategy.standard);
        when(_agentRunData.getNumStartUsers()).thenReturn(numStartUsers);
        when(_agentRunData.getRampTimeMillis()).thenReturn(rampTimeMillis);
        when(_agentRunData.getTargetRampRate()).thenReturn(targetRampRate);
        when(_agentRunData.getSimulationTimeMillis()).thenReturn(60_000L);
    }

    /** Has the harness issue a stop once the given number of user threads have started. */
    private void stopAfterThreads(int count) {
        _stopAfterThreads.set(count);
    }

    /** Counts down once the harness has been asked about simulation time the given number of times while paused. */
    private CountDownLatch signalWhilePaused(int checks) {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger pausedChecks = new AtomicInteger();
        when(_harnessMock.hasMetSimulationTime()).thenAnswer(invocation -> {
            AgentCommand cmd = _cmd.get();
            if ((cmd == AgentCommand.pause || cmd == AgentCommand.pause_ramp) && pausedChecks.incrementAndGet() >= checks) {
                latch.countDown();
            }
            return false;
        });
        return latch;
    }

    /** Stubs each user session so it stays running until the test ends. */
    private MockedConstruction<TestPlanRunner> mockRunners() {
        return mockConstruction(TestPlanRunner.class, (runner, context) -> doAnswer(invocation -> {
            _releaseRunners.await(10, TimeUnit.SECONDS);
            return null;
        }).when(runner).run());
    }

    /** What a pause test saw while the starter was paused. */
    private static final class PauseObserver {
        private volatile boolean reachedPause;
        private volatile boolean doneWhilePaused;
        private volatile int threadsStartedWhilePaused;
        private Thread thread;
    }

    /**
     * Once the starter running on this thread waits in a pause loop, applies the command and wakes it from its
     * poll sleep. Gives up after a timeout so a starter that never pauses still finishes.
     */
    private PauseObserver applyWhenPaused(CountDownLatch paused, AgentCommand command) {
        PauseObserver observer = new PauseObserver();
        Thread starterThread = Thread.currentThread();
        observer.thread = new Thread(() -> {
            try {
                observer.reachedPause = paused.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
            observer.doneWhilePaused = _sut.isDone();
            observer.threadsStartedWhilePaused = _sut.getThreadsStarted();
            _cmd.set(command);
            starterThread.interrupt();
        }, "pause-observer");
        observer.thread.setDaemon(true);
        observer.thread.start();
        return observer;
    }

    /** Makes the next metrics check due, rather than relying on the clock moving past construction time. */
    private static void sendMetricsOnNextCheck(TestPlanStarter starter) {
        try {
            Field send = TestPlanStarter.class.getDeclaredField("send");
            send.setAccessible(true);
            send.set(starter, new Date(0));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private static APITestHarness swapHarness(APITestHarness replacement) throws ReflectiveOperationException {
        Field instance = APITestHarness.class.getDeclaredField("instance");
        instance.setAccessible(true);
        APITestHarness previous = (APITestHarness) instance.get(null);
        instance.set(null, replacement);
        return previous;
    }
}

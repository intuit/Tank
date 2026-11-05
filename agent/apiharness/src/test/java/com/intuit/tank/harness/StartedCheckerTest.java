package com.intuit.tank.harness;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.vm.settings.TankConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StartedCheckerTest {

    private APITestHarness harness;
    private final AtomicBoolean exited = new AtomicBoolean();

    @BeforeEach
    public void setUp() {
        harness = mock(APITestHarness.class);
        // what a log event reads from the harness, should one be built on a thread new to logging
        when(harness.getAgentRunData()).thenReturn(new AgentRunData());
        when(harness.getTankConfig()).thenReturn(mock(TankConfig.class));
        when(harness.isLocal()).thenReturn(true);
        // build this thread's log event from the real harness, so the mock never ends up cached in it
        LogUtil.getLogEvent();
    }

    @Test
    public void testRunReturnsWhenAgentHasStarted() {
        when(harness.isStarted()).thenReturn(true);
        try (MockedStatic<APITestHarness> apiTestHarness = Mockito.mockStatic(APITestHarness.class)) {
            apiTestHarness.when(APITestHarness::getInstance).thenReturn(harness);

            new StartedChecker(0, () -> exited.set(true)).run();

            // the agent had started, so the checker asked and returned instead of exiting
            verify(harness).isStarted();
            assertFalse(exited.get());
        }
    }

    @Test
    public void testRunExitsWhenAgentNeverStarted() {
        try (MockedStatic<APITestHarness> apiTestHarness = Mockito.mockStatic(APITestHarness.class)) {
            apiTestHarness.when(APITestHarness::getInstance).thenReturn(harness);

            new StartedChecker(0, () -> exited.set(true)).run();

            assertTrue(exited.get());
        }
    }

    @Test
    public void testInterruptCancelsTheCheck() {
        try (MockedStatic<APITestHarness> apiTestHarness = Mockito.mockStatic(APITestHarness.class)) {
            apiTestHarness.when(APITestHarness::getInstance).thenReturn(harness);
            Thread.currentThread().interrupt();

            new StartedChecker(60_000, () -> exited.set(true)).run();

            // the agent had not started, but an interrupted checker neither checks nor exits
            verify(harness, never()).isStarted();
            assertFalse(exited.get());
            assertTrue(Thread.currentThread().isInterrupted(), "the interrupt is kept for the caller");
        } finally {
            Thread.interrupted();
        }
    }
}

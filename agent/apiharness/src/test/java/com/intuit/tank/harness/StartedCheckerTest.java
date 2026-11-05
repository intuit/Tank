package com.intuit.tank.harness;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StartedCheckerTest {

    @Test
    public void testRunReturnsWhenAgentHasStarted() {
        APITestHarness harness = mock(APITestHarness.class);
        when(harness.isStarted()).thenReturn(true);
        try (MockedStatic<APITestHarness> apiTestHarness = Mockito.mockStatic(APITestHarness.class)) {
            apiTestHarness.when(APITestHarness::getInstance).thenReturn(harness);
            // an interrupted thread cuts the 10 minute wait short
            Thread.currentThread().interrupt();

            new StartedChecker().run();

            // the agent had started, so the checker asked and returned instead of exiting
            verify(harness).isStarted();
            assertFalse(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }
}

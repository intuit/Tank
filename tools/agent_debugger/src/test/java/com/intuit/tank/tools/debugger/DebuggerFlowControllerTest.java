package com.intuit.tank.tools.debugger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.runner.TestStepContext;

/**
 * Drives the controller against a mocked frame, so no window is created.
 */
public class DebuggerFlowControllerTest {

    private AgentDebuggerFrame frame;
    private DebuggerFlowController controller;

    @BeforeEach
    public void createController() {
        frame = mock(AgentDebuggerFrame.class);
        controller = new DebuggerFlowController(frame);
    }

    @Test
    public void requestStepIsExecuted() {
        assertTrue(controller.shouldExecute(contextFor(request(0))));
        verify(frame, never()).setNextStep(any());
    }

    @Test
    public void sleepStepsRunOnlyWhenTheFrameAllowsThem() {
        SleepTimeStep sleep = new SleepTimeStep();
        sleep.setStepIndex(1);
        TestStepContext context = contextFor(sleep);

        when(frame.runSleepSteps()).thenReturn(false);
        assertFalse(controller.shouldExecute(context));
        when(frame.runSleepSteps()).thenReturn(true);
        assertTrue(controller.shouldExecute(context));
    }

    @Test
    public void thinkStepsRunOnlyWhenTheFrameAllowsThem() {
        ThinkTimeStep think = new ThinkTimeStep();
        think.setStepIndex(2);
        TestStepContext context = contextFor(think);

        when(frame.runThinkSteps()).thenReturn(false);
        assertFalse(controller.shouldExecute(context));
        when(frame.runThinkSteps()).thenReturn(true);
        assertTrue(controller.shouldExecute(context));
    }

    @Test
    public void skippedStepMovesTheCursorWithoutExecuting() {
        TestStepContext context = contextFor(request(3));
        controller.skip(3);

        assertFalse(controller.shouldExecute(context));
        verify(frame).moveCursor(context);
        verify(frame).setNextStep(context);

        controller.removeSkip(3);
        assertTrue(controller.shouldExecute(context));
    }

    @Test
    public void shouldExecuteUsesTheFramesCopyOfTheStep() {
        TestStep original = request(4);
        TestStep edited = request(4);
        when(frame.getStep(4)).thenReturn(edited);
        TestStepContext context = newContext(original);

        controller.shouldExecute(context);

        verify(context).setTestStep(edited);
    }

    @Test
    public void firstNextStepStartsTheTestAndPausesUntilNext() {
        TestStepContext context = contextFor(request(0));

        // doNext before nextStep so the wait loop exits at once
        controller.doNext();
        controller.nextStep(context);
        controller.doNext();
        controller.nextStep(context);

        verify(frame, times(1)).testStarted();
        InOrder order = inOrder(frame);
        order.verify(frame).moveCursor(context);
        order.verify(frame).pause();
        order.verify(frame).setNextStep(context);
        verify(frame, times(2)).pause();
    }

    @Test
    public void skippingRunsPastStepsWithoutBreakpoints() {
        TestStepContext context = contextFor(request(6));
        when(frame.hasBreakPoint(6)).thenReturn(false);
        controller.setSkipping(true);

        controller.nextStep(context);

        verify(frame, never()).pause();
        verify(frame).setNextStep(context);
        assertTrue(controller.isSkipping());
    }

    @Test
    public void breakpointStopsSkipping() {
        TestStepContext context = contextFor(request(7));
        when(frame.hasBreakPoint(7)).thenReturn(true);
        controller.setSkipping(true);
        controller.doNext();

        controller.nextStep(context);

        verify(frame).pause();
        assertFalse(controller.isSkipping());
    }

    @Test
    public void interruptWhileWaitingForNextDoesNotAdvance() {
        TestStepContext context = contextFor(request(8));

        Thread.currentThread().interrupt();
        try {
            controller.nextStep(context);
        } finally {
            // the sleep consumed the interrupt; clear it in case it did not
            Thread.interrupted();
        }

        verify(frame).pause();
        verify(frame, never()).setNextStep(context);
    }

    @Test
    public void skipListAndSkipToState() {
        assertFalse(controller.isSkipping());
        controller.skip(1);
        controller.skip(9);
        assertEquals(Set.of(1, 9), controller.getSkipList());

        controller.skipTo(5);
        assertTrue(controller.isSkipping());
        controller.endTest();
        assertFalse(controller.isSkipping());
        verify(frame).testFinished();
    }

    @Test
    public void startAndEndStepAreForwardedToTheFrame() {
        TestStepContext context = contextFor(request(0));

        controller.startStep(context);
        controller.endStep(context);

        verify(frame).stepStarted(context);
        verify(frame).stepFinished(context);
        assertSame(controller, controller.cloneController());
    }

    private TestStepContext contextFor(TestStep step) {
        when(frame.getStep(step.getStepIndex())).thenReturn(step);
        return newContext(step);
    }

    /** A mock, because a real context loads the agent's TankConfig, which writes a settings.xml into the working directory */
    private static TestStepContext newContext(TestStep step) {
        TestStepContext context = mock(TestStepContext.class);
        when(context.getTestStep()).thenReturn(step);
        return context;
    }

    private static TestStep request(int index) {
        HDRequest request = new HDRequest();
        request.setMethod("GET");
        request.setLabel("/step" + index);
        RequestStep step = new RequestStep();
        step.setRequest(request);
        step.setStepIndex(index);
        return step;
    }
}

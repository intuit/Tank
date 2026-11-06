package com.intuit.tank.tools.headless;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDRequest;
import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.runner.TestStepContext;

public class AutomationFlowControllerTest {

    private HeadlessDebuggerSetup setup;
    private AutomationFlowController controller;

    @BeforeEach
    public void createController() {
        setup = mock(HeadlessDebuggerSetup.class);
        controller = new AutomationFlowController(setup);
    }

    @Test
    public void requestStepIsExecuted() {
        TestStepContext context = contextFor(request(0, "#{host}/start"));

        assertTrue(controller.shouldExecute(context));
        verify(setup, never()).stepExecuted();
        verify(setup, never()).setNextStep(any());
    }

    @Test
    public void sleepStepIsExecuted() {
        SleepTimeStep sleep = new SleepTimeStep();
        sleep.setStepIndex(1);
        sleep.setValue("10");

        assertTrue(controller.shouldExecute(contextFor(sleep)));
    }

    @Test
    public void thinkTimeStepIsSkippedAndRecorded() {
        ThinkTimeStep think = new ThinkTimeStep();
        think.setStepIndex(2);
        TestStepContext context = contextFor(think);

        assertFalse(controller.shouldExecute(context));
        verify(setup).stepExecuted();
        verify(setup).recordSkippedStep(think);
        verify(setup).setNextStep(context);
    }

    @Test
    public void efeLogicStepIsSkippedAndRecorded() {
        LogicStep logic = new LogicStep();
        logic.setStepIndex(3);
        logic.setName("EFE Logic Step");
        TestStepContext context = contextFor(logic);

        assertFalse(controller.shouldExecute(context));
        verify(setup).stepExecuted();
        verify(setup).recordSkippedStep(logic);
    }

    @Test
    public void otherLogicStepsAreExecuted() {
        LogicStep logic = new LogicStep();
        logic.setStepIndex(3);
        logic.setName("set variables");

        assertTrue(controller.shouldExecute(contextFor(logic)));
    }

    @Test
    public void stepOnTheSkipListIsNotExecutedOrRecorded() {
        TestStep step = request(5, "#{host}/skip");
        TestStepContext context = contextFor(step);
        controller.skip(5);

        assertFalse(controller.shouldExecute(context));
        verify(setup, never()).stepExecuted();
        verify(setup, never()).recordSkippedStep(any());
        verify(setup).setNextStep(context);

        // once removed from the skip list the step runs again
        controller.removeSkip(5);
        assertTrue(controller.shouldExecute(context));
    }

    @Test
    public void skipListAndSkippingState() {
        assertFalse(controller.isSkipping());
        controller.skip(1);
        controller.skip(2);
        assertEquals(Set.of(1, 2), controller.getSkipList());

        controller.setSkipping(true);
        assertTrue(controller.isSkipping());
        controller.setSkipping(false);
        assertFalse(controller.isSkipping());

        controller.skipTo(4);
        assertTrue(controller.isSkipping());
        // ending the test resets skipTo
        controller.endTest();
        assertFalse(controller.isSkipping());
        verify(setup).testFinished();
    }

    @Test
    public void lifecycleCallsAreForwardedToTheSetup() {
        TestStepContext context = contextFor(request(0, "/a"));

        controller.startStep(context);
        controller.endStep(context);
        controller.doNext();
        controller.nextStep(context);
        controller.nextStep(context);

        verify(setup).stepStarted(context);
        verify(setup).stepFinished(context);
        verify(setup, times(2)).setNextStep(context);
    }

    @Test
    public void cloneReturnsTheSameController() {
        assertSame(controller, controller.cloneController());
    }

    /**
     * The controller swaps in the setup's copy of the step, so serve the same step back. The context is a mock
     * because a real one loads the agent's TankConfig, which writes a settings.xml into the working directory.
     */
    private TestStepContext contextFor(TestStep step) {
        when(setup.getStep(step.getStepIndex())).thenReturn(step);
        TestStepContext context = mock(TestStepContext.class);
        when(context.getTestStep()).thenReturn(step);
        return context;
    }

    private static TestStep request(int index, String label) {
        HDRequest request = new HDRequest();
        request.setLabel(label);
        request.setMethod("GET");
        RequestStep step = new RequestStep();
        step.setRequest(request);
        step.setStepIndex(index);
        return step;
    }
}

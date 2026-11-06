package com.intuit.tank.runner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.harness.*;
import com.intuit.tank.harness.data.*;
import com.intuit.tank.harness.test.MockFlowController;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.common.TankConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * The class <code>TestPlanRunnerTest</code> contains tests for the class <code>{@link TestPlanRunner}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
// a runner that never sees its kill or stop command loops forever, and its pause loop swallows interrupts,
// so the timeout has to run the test in another thread to fail it
@Timeout(value = 30, threadMode = Timeout.ThreadMode.SEPARATE_THREAD)
public class TestPlanRunnerTest {
    private Object httpClient;
    private MockHDTestPlan testPlan;

    private String httpClientClass;

    private MockStep step;

    @BeforeEach
    public void init() {
        httpClient = new Object();
        testPlan = new MockHDTestPlan();
        MockHDScriptGroup group = new MockHDScriptGroup();
        HDTestVariables variables = new HDTestVariables();
        MockHDScript script = new MockHDScript();
        MockHDScriptUseCase useCase = new MockHDScriptUseCase();
        step = new MockStep();
        httpClientClass = "com.intuit.tank.httpclient4.TankHttpClient4";
        variables.addVariable("testKey", "testValue");
        script.setLoop(2);
        useCase.addScriptSteps(step);
        // in the failing step's script group, so SKIP skips it
        useCase.addScriptSteps(requestStep(""));
        script.addUseCase(useCase);
        group.addGroupStep(script);
        group.setLoop(2);
        testPlan.addGroup(group);
        testPlan.setVariables(variables);

        APITestHarness instance = APITestHarness.getInstance();
        FlowController mockFlowController = new MockFlowController();
        instance.setFlowControllerTemplate(mockFlowController);
        instance.runConcurrentTestPlans();
    }

    @AfterEach
    public void cleanup() {
        APITestHarness.destroyCurrentInstance();
    }

    @Test
    public void testPlanRunnerFailureSkipCase() {
        step.setOnFail(TankConstants.HTTP_CASE_SKIP);
        TestPlanRunner runner = new TestPlanRunner(httpClient, testPlan, 3, httpClientClass);
        runner.setUniqueName("testUniqueName");
        runner.run();
        assertNotNull(runner);
    }

    @Test
    public void testPlanRunnerFailureKillCase() {
        step.setOnFail(TankConstants.HTTP_CASE_KILL);
        TestPlanRunner runner = new TestPlanRunner(httpClient, testPlan, 3, httpClientClass);
        runner.setUniqueName("testUniqueName");
        runner.run();
        assertNotNull(runner);
    }

    @Test
    public void testPlanRunnerFailureSkipGroupCase() {
        step.setOnFail(TankConstants.HTTP_CASE_SKIPGROUP);
        TestPlanRunner runner = new TestPlanRunner(httpClient, testPlan, 3, httpClientClass);
        runner.setUniqueName("testUniqueName");
        runner.run();
        assertNotNull(runner);
    }

    @Test
    public void testPlanRunnerFailureAbortCase() {
        step.setOnFail(TankConstants.HTTP_CASE_ABORT);
        TestPlanRunner runner = new TestPlanRunner(httpClient, testPlan, 3, httpClientClass);
        runner.setUniqueName("testUniqueName");
        runner.run();
        assertNotNull(runner);
    }

    @Test
    public void testPlanRunnerFailureGoToCase() {
        // a group no use case has; a goto back into the failing step's own group would retry until killed
        step.setOnFail(ScriptConstants.ACTION_GOTO_PREFIX + "missing");
        TestPlanRunner runner = new TestPlanRunner(httpClient, testPlan, 3, httpClientClass);
        runner.setUniqueName("testUniqueName");
        runner.run();
        assertNotNull(runner);
    }

    @Test
    public void testRunsEveryScriptGroupWithPlanVariables() {
        VariableStep first = variableStep("first");
        VariableStep second = variableStep("second");
        MockHDTestPlan plan = new MockHDTestPlan();
        plan.addGroup(group(1, script(1, useCase(first))));
        plan.addGroup(group(1, script(1, useCase(second))));
        HDTestVariables planVariables = new HDTestVariables();
        planVariables.addVariable("planKey", "planValue");
        plan.setVariables(planVariables);
        List<String> seen = new ArrayList<>();
        RecordingFlowController flow = useRecordingFlowController();
        flow.afterStep = context -> seen.add(context.getVariables().getVariable("planKey") + "/"
                + context.getVariables().getVariable("THREAD_ID"));

        run(plan);

        assertEquals(List.of(first, second), flow.executed);
        assertEquals(List.of("planValue/3", "planValue/3"), seen);
    }

    @Test
    public void testKillCommandStopsBeforeFirstGroup() {
        RecordingFlowController flow = useRecordingFlowController();
        APITestHarness.getInstance().setCommand(AgentCommand.kill);

        run(plan(1, 1, useCase(variableStep("first"))));

        assertTrue(flow.executed.isEmpty());
    }

    @Test
    public void testKillCommandStopsAtNextStep() {
        VariableStep first = variableStep("first");
        RecordingFlowController flow = useRecordingFlowController();
        flow.afterStep = context -> APITestHarness.getInstance().setCommand(AgentCommand.kill);

        run(plan(1, 1, useCase(first, variableStep("second"))));

        assertEquals(List.of(first), flow.executed);
    }

    @Test
    public void testStepsTheFlowControllerSkipsAreNotExecuted() {
        VariableStep first = variableStep("first");
        VariableStep skipped = variableStep("skipped");
        VariableStep last = variableStep("last");
        RecordingFlowController flow = useRecordingFlowController();
        flow.skip = step -> step == skipped;

        run(plan(1, 1, useCase(first, skipped, last)));

        assertEquals(List.of(first, last), flow.executed);
    }

    @Test
    public void testRestartPlanRunsThePlanAgain() {
        MockStep restart = new MockStep();
        restart.setOnFail(TankConstants.HTTP_CASE_RESTART);
        RecordingFlowController flow = useRecordingFlowController();
        // the restart would loop forever, so kill the user on the second run
        flow.afterStep = context -> {
            if (flow.executed.size() == 2) {
                APITestHarness.getInstance().setCommand(AgentCommand.kill);
            }
        };

        run(plan(1, 1, useCase(restart)));

        assertEquals(List.of(restart, restart), flow.executed);
        assertEquals(2, APITestHarness.getInstance().getStatus().getRestarts());
    }

    @Test
    public void testRestartPlanAfterSimulationTimeKillsTheUser() {
        meetSimulationTime();
        MockStep restart = new MockStep();
        restart.setOnFail(TankConstants.HTTP_CASE_RESTART);
        RecordingFlowController flow = useRecordingFlowController();

        run(plan(1, 1, useCase(restart, variableStep("after"))));

        assertEquals(List.of(restart), flow.executed);
        WatsAgentStatusResponse status = APITestHarness.getInstance().getStatus();
        assertEquals(1, status.getKills());
        assertEquals(0, status.getRestarts());
    }

    @Test
    public void testSkipOnLastGroupOfUseCaseEndsUseCase() {
        MockStep failing = new MockStep();
        failing.setOnFail(TankConstants.HTTP_CASE_SKIP);
        // the request step is in the same (empty) script group, so the rest of the use case is skipped
        RecordingFlowController flow = useRecordingFlowController();

        run(plan(1, 1, useCase(failing, requestStep(""))));

        assertEquals(List.of(failing), flow.executed);
        assertEquals(1, APITestHarness.getInstance().getStatus().getSkips());
    }

    @Test
    public void testSkipResumesAtTheFirstStepAfterTheGroup() {
        MockStep failing = new MockStep();
        failing.setOnFail(TankConstants.HTTP_CASE_SKIP);
        VariableStep next = variableStep("next");
        VariableStep last = variableStep("last");
        RecordingFlowController flow = useRecordingFlowController();
        // the request step only marks the failing step's (empty) script group
        flow.skip = step -> step instanceof RequestStep;

        run(plan(1, 1, useCase(failing, requestStep(""), next, last)));

        assertEquals(List.of(failing, next, last), flow.executed);
        assertEquals(1, APITestHarness.getInstance().getStatus().getSkips());
    }

    @Test
    public void testGotoCanTargetTheFirstUseCase() {
        VariableStep target = variableStep("target");
        MockStep failing = new MockStep();
        failing.setOnFail(ScriptConstants.ACTION_GOTO_PREFIX + "target");
        RecordingFlowController flow = useRecordingFlowController();
        flow.skip = step -> step instanceof RequestStep;
        // the goto would loop forever, so kill the user once the target has run again
        flow.afterStep = context -> {
            if (flow.executed.size() == 3) {
                APITestHarness.getInstance().setCommand(AgentCommand.kill);
            }
        };

        run(plan(1, 1, useCase(requestStep("target"), target), useCase(failing)));

        assertEquals(List.of(target, failing, target), flow.executed);
        assertEquals(1, APITestHarness.getInstance().getStatus().getGotos());
    }

    @Test
    public void testGotoRunsTheUseCaseOfTheTargetGroup() {
        VariableStep start = variableStep("start");
        MockStep failing = new MockStep();
        failing.setOnFail(ScriptConstants.ACTION_GOTO_PREFIX + "target");
        VariableStep skipped = variableStep("skipped");
        VariableStep target = variableStep("target");
        RecordingFlowController flow = useRecordingFlowController();
        // the request steps only mark the script group of their use case
        flow.skip = step -> step instanceof RequestStep;

        run(plan(1, 1, useCase(start, failing), useCase(requestStep("other"), skipped),
                useCase(requestStep("target"), target)));

        assertEquals(List.of(start, failing, target), flow.executed);
        assertEquals(1, APITestHarness.getInstance().getStatus().getGotos());
    }

    @Test
    public void testStopAtEndOfStep() {
        VariableStep first = variableStep("first");
        RecordingFlowController flow = useRecordingFlowController();
        APITestHarness.getInstance().getAgentRunData().setStopBehavior(StopBehavior.END_OF_STEP);
        APITestHarness.getInstance().setCommand(AgentCommand.stop);

        run(plan(1, 1, useCase(first, variableStep("second"))));

        assertEquals(List.of(first), flow.executed);
    }

    @Test
    public void testStopAtEndOfScript() {
        VariableStep first = variableStep("first");
        RecordingFlowController flow = useRecordingFlowController();
        APITestHarness.getInstance().getAgentRunData().setStopBehavior(StopBehavior.END_OF_SCRIPT);
        APITestHarness.getInstance().setCommand(AgentCommand.stop);

        MockHDTestPlan plan = new MockHDTestPlan();
        plan.addGroup(group(1, script(2, useCase(first)), script(1, useCase(variableStep("second")))));
        run(plan);

        // the first script finishes its loops, the second script never starts
        assertEquals(List.of(first, first), flow.executed);
    }

    @Test
    public void testStopAtEndOfScriptGroup() {
        VariableStep first = variableStep("first");
        RecordingFlowController flow = useRecordingFlowController();
        APITestHarness.getInstance().getAgentRunData().setStopBehavior(StopBehavior.END_OF_SCRIPT_GROUP);
        APITestHarness.getInstance().setCommand(AgentCommand.stop);

        MockHDTestPlan plan = new MockHDTestPlan();
        plan.addGroup(group(3, script(1, useCase(first))));
        plan.addGroup(group(1, script(1, useCase(variableStep("second")))));
        run(plan);

        assertEquals(List.of(first), flow.executed);
    }

    @Test
    public void testPauseAfterSimulationTimeStops() {
        meetSimulationTime();
        VariableStep first = variableStep("first");
        RecordingFlowController flow = useRecordingFlowController();
        APITestHarness.getInstance().setCommand(AgentCommand.pause);

        run(plan(2, 1, useCase(first)));

        // the paused user is stopped instead of waiting, and stops at the end of the script group
        assertEquals(AgentCommand.stop, APITestHarness.getInstance().getCmd());
        assertEquals(List.of(first), flow.executed);
    }

    @Test
    public void testUnexpectedExceptionEndsTheUser() {
        // a request step whose response has no validation fails with a NullPointerException
        RequestStep broken = requestStep("group");
        RecordingFlowController flow = useRecordingFlowController();

        run(plan(1, 1, useCase(broken, variableStep("after"))));

        assertEquals(List.of(broken), flow.executed);
    }

    /**
     * Records the steps that ran. It can skip steps and act after a step, e.g. to send the agent a command.
     */
    private static class RecordingFlowController implements FlowController {
        private final List<TestStep> executed = new ArrayList<>();
        private Predicate<TestStep> skip = step -> false;
        private Consumer<TestStepContext> afterStep = context -> {
        };

        @Override
        public void startStep(TestStepContext context) {
        }

        @Override
        public void endStep(TestStepContext context) {
            executed.add(context.getTestStep());
            afterStep.accept(context);
        }

        @Override
        public boolean shouldExecute(TestStepContext context) {
            return !skip.test(context.getTestStep());
        }

        @Override
        public void nextStep(TestStepContext context) {
        }

        @Override
        public void endTest() {
        }

        @Override
        public FlowController cloneController() {
            return this;
        }
    }

    private static RecordingFlowController useRecordingFlowController() {
        RecordingFlowController flow = new RecordingFlowController();
        APITestHarness.getInstance().setFlowControllerTemplate(flow);
        return flow;
    }

    private static void meetSimulationTime() {
        // the agent has not started a simulation, so a one millisecond simulation time has already passed
        APITestHarness.getInstance().getAgentRunData().setSimulationTimeMillis(1);
        assertTrue(APITestHarness.getInstance().hasMetSimulationTime());
    }

    private static void run(HDTestPlan plan) {
        TestPlanRunner runner = new TestPlanRunner(new Object(), plan, 3, "com.intuit.tank.runner.TestHttpClient");
        runner.setUniqueName("testUniqueName");
        runner.run();
    }

    private static MockHDTestPlan plan(int groupLoop, int scriptLoop, MockHDScriptUseCase... useCases) {
        MockHDTestPlan plan = new MockHDTestPlan();
        plan.addGroup(group(groupLoop, script(scriptLoop, useCases)));
        return plan;
    }

    private static MockHDScriptGroup group(int loop, MockHDScript... scripts) {
        MockHDScriptGroup group = new MockHDScriptGroup();
        group.setLoop(loop);
        for (MockHDScript script : scripts) {
            group.addGroupStep(script);
        }
        return group;
    }

    private static MockHDScript script(int loop, MockHDScriptUseCase... useCases) {
        MockHDScript script = new MockHDScript();
        script.setLoop(loop);
        for (MockHDScriptUseCase useCase : useCases) {
            script.addUseCase(useCase);
        }
        return script;
    }

    private static MockHDScriptUseCase useCase(TestStep... steps) {
        MockHDScriptUseCase useCase = new MockHDScriptUseCase();
        for (TestStep step : steps) {
            useCase.addScriptSteps(step);
        }
        return useCase;
    }

    private static VariableStep variableStep(String key) {
        VariableStep step = new VariableStep();
        step.setKey(key);
        step.setValue("value");
        return step;
    }

    private static RequestStep requestStep(String scriptGroupName) {
        RequestStep step = new RequestStep();
        // logging the step reads its request and response
        step.setRequest(new HDRequest());
        step.setResponse(new HDResponse());
        step.setScriptGroupName(scriptGroupName);
        return step;
    }
}

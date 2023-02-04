package com.intuit.tank.runner;

import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import com.intuit.tank.vm.common.TankConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The class <code>TestPlanRunnerTest</code> contains tests for the class <code>{@link TestPlanRunner}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class TestPlanRunnerTest {
    public Object httpClient;
    public MockHDTestPlan testPlan;

    public MockHDScriptGroup group;

    public String httpClientClass;

    public HDTestVariables variables;

    public MockHDScript script;

    public MockStep step;

    public MockHDScriptUseCase useCase;

    public APITestHarness instance;

    @BeforeEach
    public void init() {
        httpClient = new Object();
        testPlan = new MockHDTestPlan();
        group = new MockHDScriptGroup();
        variables = new HDTestVariables();
        script = new MockHDScript();
        useCase = new MockHDScriptUseCase();
        step = new MockStep();
        httpClientClass = "com.intuit.tank.httpclient4.TankHttpClient4";
        variables.addVariable("testKey", "testValue");
        script.setLoop(2);
        useCase.addScriptSteps(step);
        useCase.addScriptSteps(new RequestStep());
        script.addUseCase(useCase);
        group.addGroupStep(script);
        group.setLoop(2);
        testPlan.addGroup(group);
        testPlan.setVariables(variables);

        instance = APITestHarness.getInstance();
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
        step.setOnFail(ScriptConstants.ACTION_GOTO_PREFIX);
        TestPlanRunner runner = new TestPlanRunner(httpClient, testPlan, 3, httpClientClass);
        runner.setUniqueName("testUniqueName");
        runner.run();
        assertNotNull(runner);
    }
}

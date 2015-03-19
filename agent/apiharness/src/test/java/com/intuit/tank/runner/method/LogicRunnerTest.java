package com.intuit.tank.runner.method;

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

import org.apache.commons.httpclient.HttpClient;
import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.harness.data.TestStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.runner.method.LogicRunner;
import com.intuit.tank.runner.method.TimerMap;

/**
 * The class <code>LogicRunnerTest</code> contains tests for the class <code>{@link LogicRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class LogicRunnerTest {
    /**
     * Run the LogicRunner(TestStepContext) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testLogicRunner_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1);
        testPlanRunner.setHttpClient(new HttpClient());
        TestStepContext tsc = new TestStepContext(new LogicStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        LogicRunner result = new LogicRunner(tsc);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.Logger.getLogger(Logger.java:117)
        //       at com.intuit.tank.runner.TestPlanRunner.<clinit>(TestPlanRunner.java:44)
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1);
        testPlanRunner.setHttpClient(new HttpClient());
        TestStepContext testStepContext = new TestStepContext(new LogicStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        LogicRunner fixture = new LogicRunner(testStepContext);

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_2()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1);
        testPlanRunner.setHttpClient(new HttpClient());
        TestStepContext testStepContext = new TestStepContext(new LogicStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        LogicRunner fixture = new LogicRunner(testStepContext);

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_3()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1);
        testPlanRunner.setHttpClient(new HttpClient());
        TestStepContext testStepContext = new TestStepContext(new LogicStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        LogicRunner fixture = new LogicRunner(testStepContext);

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }
}
package com.intuit.tank.runner.method;

import static org.junit.Assert.assertNotNull;

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
import org.junit.Test;

import com.intuit.tank.harness.data.ClearCookiesStep;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestHttpClient;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>ClearCookiesRunnerTest</code> contains tests for the class <code>{@link ClearCookiesRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class ClearCookiesRunnerTest {
    /**
     * Run the ClearCookiesRunner(TestStepContext) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testClearCookiesRunner_1()
        throws Exception {
        TestStepContext tsc = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient()));

        ClearCookiesRunner result = new ClearCookiesRunner(tsc);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        //       at com.intuit.tank.harness.test.data.Variables.<clinit>(Variables.java:36)
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
        ClearCookiesRunner fixture = new ClearCookiesRunner(new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient())));

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.harness.test.data.Variables
        assertNotNull(result);
    }
}
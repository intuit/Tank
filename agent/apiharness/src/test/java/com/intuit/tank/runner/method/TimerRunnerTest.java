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
 * The class <code>TimerRunnerTest</code> contains tests for the class <code>{@link TimerRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class TimerRunnerTest {
    /**
     * Run the TimerRunner(TestStepContext) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testTimerRunner_1()
        throws Exception {
        TestStepContext tsc = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient()));

        TimerRunner result = new TimerRunner(tsc);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.Logger.getLogger(Logger.java:117)
        //       at com.intuit.tank.harness.test.data.Variables.<clinit>(Variables.java:36)
        assertNotNull(result);
    }

  
}
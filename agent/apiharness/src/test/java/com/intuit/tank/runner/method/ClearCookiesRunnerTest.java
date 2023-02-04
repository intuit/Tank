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
import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.httpclient4.TankHttpClient4;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.ClearCookiesStep;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * The class <code>ClearCookiesRunnerTest</code> contains tests for the class <code>{@link ClearCookiesRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class ClearCookiesRunnerTest {

    @Test
    public void testClearCookiesRunner_1() {
        TestStepContext tsc = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient"));

        ClearCookiesRunner result = new ClearCookiesRunner(tsc);
        assertNotNull(result);
    }

    @Test
    public void testExecute_1() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(new TankHttpClient4());
        TestStepContext testStepContext = new TestStepContext(new LogicStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        ClearCookiesRunner fixture = new ClearCookiesRunner(testStepContext);

        String result = fixture.execute();
        assertNotNull(result);
    }
}

package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.*;

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
import com.intuit.tank.harness.data.TimerStep;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>TimerRunnerTest</code> contains tests for the class <code>{@link TimerRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class TimerRunnerTest {

    @Test
    public void testTimerRunner_1() {
        TestStepContext tsc = new TestStepContext(new TimerStep(), new Variables(), "", "", new TimerMap(), new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient"));
        TimerRunner result = new TimerRunner(tsc);
        assertNotNull(result);
        String actual = result.execute();
        assertEquals("PASS", actual);
    }

  
}
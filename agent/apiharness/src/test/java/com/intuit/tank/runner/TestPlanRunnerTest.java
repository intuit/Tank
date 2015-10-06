package com.intuit.tank.runner;

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

import java.util.Map;

import org.junit.Test;

import com.intuit.tank.harness.data.HDTestPlan;

/**
 * The class <code>TestPlanRunnerTest</code> contains tests for the class <code>{@link TestPlanRunner}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class TestPlanRunnerTest {
    /**
     * Run the TestPlanRunner(HDTestPlan,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testTestPlanRunner_1()
            throws Exception {
        HDTestPlan testPlan = new HDTestPlan();
        int threadNumber = 1;

        TestPlanRunner result = new TestPlanRunner(testPlan, threadNumber);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.ExceptionInInitializerError
        // at org.apache.log4j.Logger.getLogger(Logger.java:117)
        // at com.intuit.tank.runner.TestPlanRunner.<clinit>(TestPlanRunner.java:44)
        assertNotNull(result);
    }

    /**
     * Run the Map<String, String> getHeaderMap() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testGetHeaderMap_1()
            throws Exception {
        TestPlanRunner fixture = new TestPlanRunner(new HDTestPlan(), 1);
        fixture.setHttpClient(null);
        fixture.setUniqueName("");
        fixture.sslTimeout = 1L;
        fixture.lastSslHandshake = 1L;

        Map<String, String> result = fixture.getHeaderMap();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    

    /**
     * Run the void setUniqueName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSetUniqueName_1()
            throws Exception {
        TestPlanRunner fixture = new TestPlanRunner(new HDTestPlan(), 1);
        fixture.setHttpClient(null);
        fixture.setUniqueName("");
        fixture.sslTimeout = 1L;
        fixture.lastSslHandshake = 1L;
        String name = "";

        fixture.setUniqueName(name);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
    }
}
package com.intuit.tank.monitor;

/*
 * #%L
 * Common
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import com.intuit.tank.monitor.GlobalPercentCompleteMonitor;

import static org.junit.Assert.*;

/**
 * The class <code>GlobalPercentCompleteMonitorTest</code> contains tests for the class
 * <code>{@link GlobalPercentCompleteMonitor}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:35 AM
 */
public class GlobalPercentCompleteMonitorTest {
    /**
     * Run the GlobalPercentCompleteMonitor() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testGlobalPercentCompleteMonitor_1()
            throws Exception {
        GlobalPercentCompleteMonitor result = new GlobalPercentCompleteMonitor();
        assertNotNull(result);
    }

    /**
     * Run the int getPerctComplete(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testGetPerctComplete_1()
            throws Exception {
        GlobalPercentCompleteMonitor fixture = new GlobalPercentCompleteMonitor();
        int id = 1;

        int result = fixture.getPerctComplete(id);

        assertEquals(0, result);
    }

    /**
     * Run the int getPerctComplete(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testGetPerctComplete_2()
            throws Exception {
        GlobalPercentCompleteMonitor fixture = new GlobalPercentCompleteMonitor();
        int id = 1;

        int result = fixture.getPerctComplete(id);

        assertEquals(0, result);
    }

    /**
     * Run the void setError(int,int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testSetError_1()
            throws Exception {
        GlobalPercentCompleteMonitor fixture = new GlobalPercentCompleteMonitor();
        int id = 1;
        int errorCode = 1;

        fixture.setError(id, errorCode);

    }

    /**
     * Run the void setProcessingComplete(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testSetProcessingComplete_1()
            throws Exception {
        GlobalPercentCompleteMonitor fixture = new GlobalPercentCompleteMonitor();
        int id = 1;

        fixture.setProcessingComplete(id);

    }

    /**
     * Run the void setSavingStarted(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testSetSavingStarted_1()
            throws Exception {
        GlobalPercentCompleteMonitor fixture = new GlobalPercentCompleteMonitor();
        int id = 1;

        fixture.setSavingStarted(id);

    }
}
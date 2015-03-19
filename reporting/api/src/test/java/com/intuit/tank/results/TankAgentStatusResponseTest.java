package com.intuit.tank.results;

/*
 * #%L
 * Reporting API
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

import com.intuit.tank.results.TankAgentStatusResponse;

import static org.junit.Assert.*;

/**
 * The class <code>TankAgentStatusResponseTest</code> contains tests for the class
 * <code>{@link TankAgentStatusResponse}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:31 AM
 */
public class TankAgentStatusResponseTest {
    /**
     * Run the TankAgentStatusResponse(long,int,int,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testTankAgentStatusResponse_1()
            throws Exception {
        long time = 1L;
        int num = 1;
        int max = 1;
        int ramp = 1;

        TankAgentStatusResponse result = new TankAgentStatusResponse(time, num, max, ramp);

        assertNotNull(result);
        assertEquals(1, result.getMaxVirtualUsers());
        assertEquals(1L, result.getRunTime());
        assertEquals(1, result.getCurrentNumberUsers());
        assertEquals(1, result.getRampTimeLeft());
    }

    /**
     * Run the int getCurrentNumberUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetCurrentNumberUsers_1()
            throws Exception {
        TankAgentStatusResponse fixture = new TankAgentStatusResponse(1L, 1, 1, 1);

        int result = fixture.getCurrentNumberUsers();

        assertEquals(1, result);
    }

    /**
     * Run the int getMaxVirtualUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetMaxVirtualUsers_1()
            throws Exception {
        TankAgentStatusResponse fixture = new TankAgentStatusResponse(1L, 1, 1, 1);

        int result = fixture.getMaxVirtualUsers();

        assertEquals(1, result);
    }

    /**
     * Run the int getRampTimeLeft() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetRampTimeLeft_1()
            throws Exception {
        TankAgentStatusResponse fixture = new TankAgentStatusResponse(1L, 1, 1, 1);

        int result = fixture.getRampTimeLeft();

        assertEquals(1, result);
    }

    /**
     * Run the long getRunTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:31 AM
     */
    @Test
    public void testGetRunTime_1()
            throws Exception {
        TankAgentStatusResponse fixture = new TankAgentStatusResponse(1L, 1, 1, 1);

        long result = fixture.getRunTime();

        assertEquals(1L, result);
    }
}
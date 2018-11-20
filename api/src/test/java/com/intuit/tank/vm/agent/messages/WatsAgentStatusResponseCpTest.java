package com.intuit.tank.vm.agent.messages;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>WatsAgentStatusResponseCpTest</code> contains tests for the class
 * <code>{@link WatsAgentStatusResponse}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class WatsAgentStatusResponseCpTest {
    /**
     * Run the WatsAgentStatusResponse(long,int,int,int,int,int,int,int,int,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testWatsAgentStatusResponse_1()
            throws Exception {
        long time = 1L;
        int kills = 1;
        int aborts = 1;
        int gotos = 1;
        int skips = 1;
        int skipGroups = 1;
        int restarts = 1;
        int numVirtualUsers = 1;
        int max = 1;
        int ramp = 1;

        WatsAgentStatusResponse result = new WatsAgentStatusResponse(time, kills, aborts, gotos, skips, skipGroups,
                restarts, numVirtualUsers, max, ramp);

        assertNotNull(result);
        assertEquals(1, result.getRampTimeLeft());
        assertEquals(1, result.getKills());
        assertEquals(1, result.getCurrentNumberUsers());
        assertEquals(1, result.getSkipGroups());
        assertEquals(1, result.getRestarts());
        assertEquals(1, result.getGotos());
        assertEquals(1L, result.getRunTime());
        assertEquals(1, result.getAborts());
        assertEquals(1, result.getSkips());
        assertEquals(1, result.getMaxVirtualUsers());
    }

    /**
     * Run the int getAborts() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetAborts_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getAborts();

        assertEquals(1, result);
    }

    /**
     * Run the int getCurrentNumberUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetCurrentNumberUsers_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getCurrentNumberUsers();

        assertEquals(1, result);
    }

    /**
     * Run the int getGotos() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetGotos_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getGotos();

        assertEquals(1, result);
    }

    /**
     * Run the int getKills() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetKills_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getKills();

        assertEquals(1, result);
    }

    /**
     * Run the int getMaxVirtualUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetMaxVirtualUsers_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getMaxVirtualUsers();

        assertEquals(1, result);
    }

    /**
     * Run the int getRampTimeLeft() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetRampTimeLeft_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getRampTimeLeft();

        assertEquals(1, result);
    }

    /**
     * Run the int getRestarts() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetRestarts_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getRestarts();

        assertEquals(1, result);
    }

    /**
     * Run the long getRunTime() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetRunTime_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        long result = fixture.getRunTime();

        assertEquals(1L, result);
    }

    /**
     * Run the int getSkipGroups() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetSkipGroups_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getSkipGroups();

        assertEquals(1, result);
    }

    /**
     * Run the int getSkips() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetSkips_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        int result = fixture.getSkips();

        assertEquals(1, result);
    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testToString_1()
            throws Exception {
        WatsAgentStatusResponse fixture = new WatsAgentStatusResponse(1L, 1, 1, 1, 1, 1, 1, 1, 1, 1);

        String result = fixture.toString();

        assertNotNull(result);
    }
}
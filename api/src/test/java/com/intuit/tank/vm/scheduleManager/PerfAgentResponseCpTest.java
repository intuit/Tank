package com.intuit.tank.vm.scheduleManager;

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

import com.intuit.tank.vm.scheduleManager.PerfAgentResponse;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>PerfAgentResponseCpTest</code> contains tests for the class <code>{@link PerfAgentResponse}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class PerfAgentResponseCpTest {
    /**
     * Run the PerfAgentResponse(String,String,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testPerfAgentResponse_1()
            throws Exception {
        String host = "";
        String qName = "";
        int users = 1;

        PerfAgentResponse result = new PerfAgentResponse(host, qName, users);

        assertNotNull(result);
        assertEquals("", result.getHost());
        assertEquals("", result.getQueue());
        assertEquals(1, result.getNumberUsers());
    }

    /**
     * Run the String getHost() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetHost_1()
            throws Exception {
        PerfAgentResponse fixture = new PerfAgentResponse("", "", 1);

        String result = fixture.getHost();

        assertEquals("", result);
    }

    /**
     * Run the int getNumberUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetNumberUsers_1()
            throws Exception {
        PerfAgentResponse fixture = new PerfAgentResponse("", "", 1);

        int result = fixture.getNumberUsers();

        assertEquals(1, result);
    }

    /**
     * Run the String getQueue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetQueue_1()
            throws Exception {
        PerfAgentResponse fixture = new PerfAgentResponse("", "", 1);

        String result = fixture.getQueue();

        assertEquals("", result);
    }
}
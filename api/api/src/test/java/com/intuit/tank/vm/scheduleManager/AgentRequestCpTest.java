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

import java.util.Hashtable;

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.scheduleManager.AgentRequest;

/**
 * The class <code>AgentRequestCpTest</code> contains tests for the class <code>{@link AgentRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class AgentRequestCpTest {
    /**
     * Run the AgentRequest(int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testAgentRequest_1()
            throws Exception {
        int id = 1;

        AgentRequest result = new AgentRequest(id);

        assertNotNull(result);
        assertEquals(1, result.getRequestorId());
        assertEquals(0, result.getNumberUsers());
    }

    /**
     * Run the AgentRequest(int,int) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testAgentRequest_2()
            throws Exception {
        int id = 1;
        int users = 1;

        AgentRequest result = new AgentRequest(id, users);

        assertNotNull(result);
        assertEquals(1, result.getRequestorId());
        assertEquals(1, result.getNumberUsers());
    }

    /**
     * Run the void addProvider(VMProvider,int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testAddProvider_1()
            throws Exception {
        AgentRequest fixture = new AgentRequest(1, 1);
        VMProvider provider = VMProvider.Amazon;
        int numUsers = 1;

        fixture.addProvider(provider, numUsers);

    }

    /**
     * Run the int getNumberUsers() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetNumberUsers_1()
            throws Exception {
        AgentRequest fixture = new AgentRequest(1, 1);

        int result = fixture.getNumberUsers();

        assertEquals(1, result);
    }

    /**
     * Run the Hashtable<VMProvider, Integer> getProviders() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetProviders_1()
            throws Exception {
        AgentRequest fixture = new AgentRequest(1, 1);

        Hashtable<VMProvider, Integer> result = fixture.getProviders();

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * Run the int getRequestorId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetRequestorId_1()
            throws Exception {
        AgentRequest fixture = new AgentRequest(1, 1);

        int result = fixture.getRequestorId();

        assertEquals(1, result);
    }
}
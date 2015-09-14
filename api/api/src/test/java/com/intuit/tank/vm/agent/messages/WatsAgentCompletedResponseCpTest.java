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

import org.junit.*;

import static org.junit.Assert.*;

import com.intuit.tank.vm.agent.messages.WatsAgentCompletedResponse;
import com.intuit.tank.vm.api.enumerated.VMProvider;

/**
 * The class <code>WatsAgentCompletedResponseCpTest</code> contains tests for the class
 * <code>{@link WatsAgentCompletedResponse}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class WatsAgentCompletedResponseCpTest {
    /**
     * Run the WatsAgentCompletedResponse(VMProvider,String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testWatsAgentCompletedResponse_1()
            throws Exception {
        VMProvider provider = VMProvider.Amazon;
        String instanceId = "";

        WatsAgentCompletedResponse result = new WatsAgentCompletedResponse(provider, instanceId);

        assertNotNull(result);
        assertEquals("", result.getInstanceId());
    }

    /**
     * Run the String getInstanceId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetInstanceId_1()
            throws Exception {
        WatsAgentCompletedResponse fixture = new WatsAgentCompletedResponse(VMProvider.Amazon, "");

        String result = fixture.getInstanceId();

        assertEquals("", result);
    }

    /**
     * Run the VMProvider getProvider() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetProvider_1()
            throws Exception {
        WatsAgentCompletedResponse fixture = new WatsAgentCompletedResponse(VMProvider.Amazon, "");

        VMProvider result = fixture.getProvider();

        assertNotNull(result);
        assertEquals("Amazon", result.name());
        assertEquals("Amazon", result.toString());
        assertEquals(0, result.ordinal());
    }
}
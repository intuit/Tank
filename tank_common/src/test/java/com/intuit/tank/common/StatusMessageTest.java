package com.intuit.tank.common;

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

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.common.StatusMessage;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;

/**
 * The class <code>StatusMessageTest</code> contains tests for the class <code>{@link StatusMessage}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:35 AM
 */
public class StatusMessageTest {
    /**
     * Run the StatusMessage(String) constructor test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testStatusMessage_1()
            throws Exception {
        String replyQueue = "";

        StatusMessage result = new StatusMessage(replyQueue);

        assertNotNull(result);
        assertEquals(null, result.getBody());
        assertEquals("", result.getReplyQueue());
    }

    /**
     * Run the String getBody() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testGetBody_1()
            throws Exception {
        StatusMessage fixture = new StatusMessage("");
        fixture.setStatus(WatsAgentCommand.kill);
        fixture.setBody("");

        String result = fixture.getBody();

        assertEquals("", result);
    }

    /**
     * Run the String getReplyQueue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testGetReplyQueue_1()
            throws Exception {
        StatusMessage fixture = new StatusMessage("");
        fixture.setStatus(WatsAgentCommand.kill);
        fixture.setBody("");

        String result = fixture.getReplyQueue();

        assertEquals("", result);
    }

    /**
     * Run the WatsAgentCommand getStatus() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testGetStatus_1()
            throws Exception {
        StatusMessage fixture = new StatusMessage("");
        fixture.setStatus(WatsAgentCommand.kill);
        fixture.setBody("");

        WatsAgentCommand result = fixture.getStatus();

        assertNotNull(result);
        assertEquals("/kill", result.getPath());
        assertEquals("kill", result.name());
        assertEquals("kill", result.toString());
        assertEquals(5, result.ordinal());
    }

    /**
     * Run the void setBody(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testSetBody_1()
            throws Exception {
        StatusMessage fixture = new StatusMessage("");
        fixture.setStatus(WatsAgentCommand.kill);
        fixture.setBody("");
        String body = "";

        fixture.setBody(body);

    }

    /**
     * Run the void setReplyQueue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testSetReplyQueue_1()
            throws Exception {
        StatusMessage fixture = new StatusMessage("");
        fixture.setStatus(WatsAgentCommand.kill);
        fixture.setBody("");
        String replyQueue = "";

        fixture.setReplyQueue(replyQueue);

    }

    /**
     * Run the void setStatus(WatsAgentCommand) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:35 AM
     */
    @Test
    public void testSetStatus_1()
            throws Exception {
        StatusMessage fixture = new StatusMessage("");
        fixture.setStatus(WatsAgentCommand.kill);
        fixture.setBody("");
        WatsAgentCommand status = WatsAgentCommand.kill;

        fixture.setStatus(status);

    }
}
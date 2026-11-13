/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.standalone.agent;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.vm.agent.messages.StandaloneAgentRequest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CommandListenerTest {

    @Test
    public void testReadsTheRequestTheControllerSends() {
        StandaloneAgentRequest sent = new StandaloneAgentRequest("42", "localhost", 25);
        sent.setStopBehavior(StopBehavior.END_OF_TEST.name());
        // as JobManager.sendRequest writes it
        byte[] body = JsonMapper.builder().build().writeValueAsBytes(sent);

        StandaloneAgentRequest received = CommandListener.getRequest(new ByteArrayInputStream(body));

        assertEquals("42", received.getJobId());
        assertEquals("localhost", received.getInstanceId());
        assertEquals(25, received.getUsers());
        assertEquals(StopBehavior.END_OF_TEST.name(), received.getStopBehavior());
    }

    @Test
    public void testUnreadableRequest() {
        assertNull(CommandListener.getRequest(
                new ByteArrayInputStream("<standalone-agent-request/>".getBytes(StandardCharsets.UTF_8))));
    }
}

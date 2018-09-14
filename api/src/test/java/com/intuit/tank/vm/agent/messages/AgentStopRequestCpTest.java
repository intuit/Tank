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

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.vm.agent.messages.AgentStopRequest;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;

/**
 * The class <code>AgentStopRequestCpTest</code> contains tests for the class <code>{@link AgentStopRequest}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:41 PM
 */
public class AgentStopRequestCpTest {
    /**
     * Run the AgentStopRequest() constructor test.
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testAgentStopRequest_1()
            throws Exception {
        AgentStopRequest result = new AgentStopRequest();
        assertNotNull(result);
    }

    /**
     * Run the WatsAgentCommand getCommand() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetCommand_1()
            throws Exception {
        AgentStopRequest fixture = new AgentStopRequest();
        fixture.setCommand(WatsAgentCommand.kill);
        fixture.setId(1);

        WatsAgentCommand result = fixture.getCommand();

        assertNotNull(result);
        assertEquals("/kill", result.getPath());
        assertEquals("kill", result.name());
        assertEquals("kill", result.toString());
        assertEquals(5, result.ordinal());
    }

    /**
     * Run the int getId() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testGetId_1()
            throws Exception {
        AgentStopRequest fixture = new AgentStopRequest();
        fixture.setCommand(WatsAgentCommand.kill);
        fixture.setId(1);

        int result = fixture.getId();

        assertEquals(1, result);
    }

    /**
     * Run the void setCommand(WatsAgentCommand) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetCommand_1()
            throws Exception {
        AgentStopRequest fixture = new AgentStopRequest();
        fixture.setCommand(WatsAgentCommand.kill);
        fixture.setId(1);
        WatsAgentCommand command = WatsAgentCommand.kill;

        fixture.setCommand(command);

    }

    /**
     * Run the void setId(int) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:41 PM
     */
    @Test
    public void testSetId_1()
            throws Exception {
        AgentStopRequest fixture = new AgentStopRequest();
        fixture.setCommand(WatsAgentCommand.kill);
        fixture.setId(1);
        int id = 1;

        fixture.setId(id);

    }
}
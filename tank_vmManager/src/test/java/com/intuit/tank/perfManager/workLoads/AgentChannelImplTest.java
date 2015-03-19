package com.intuit.tank.perfManager.workLoads;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.LinkedList;
import java.util.List;

import org.junit.*;

import com.intuit.tank.perfManager.workLoads.AgentChannelImpl;

import static org.junit.Assert.*;

/**
 * The class <code>AgentChannelImplTest</code> contains tests for the class <code>{@link AgentChannelImpl}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 10:40 AM
 */
public class AgentChannelImplTest {
    /**
     * Run the AgentChannelImpl() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testAgentChannelImpl_1()
            throws Exception {
        AgentChannelImpl result = new AgentChannelImpl();
        assertNotNull(result);
    }

    /**
     * Run the void killAgents(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testKillAgents_1()
            throws Exception {
        AgentChannelImpl fixture = new AgentChannelImpl();
        List<String> instanceIds = new LinkedList();

        fixture.killAgents(instanceIds);

    }

    /**
     * Run the void pauseAgents(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testPauseAgents_1()
            throws Exception {
        AgentChannelImpl fixture = new AgentChannelImpl();
        List<String> instanceIds = new LinkedList();

        fixture.pauseAgents(instanceIds);

    }

    /**
     * Run the void pauseRamp(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testPauseRamp_1()
            throws Exception {
        AgentChannelImpl fixture = new AgentChannelImpl();
        List<String> instanceIds = new LinkedList();

        fixture.pauseRamp(instanceIds);

    }

    /**
     * Run the void restartAgents(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testRestartAgents_1()
            throws Exception {
        AgentChannelImpl fixture = new AgentChannelImpl();
        List<String> instanceIds = new LinkedList();

        fixture.restartAgents(instanceIds);

    }

    /**
     * Run the void resumeRamp(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testResumeRamp_1()
            throws Exception {
        AgentChannelImpl fixture = new AgentChannelImpl();
        List<String> instanceIds = new LinkedList();

        fixture.resumeRamp(instanceIds);

    }

    /**
     * Run the void stopAgents(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 10:40 AM
     */
    @Test
    public void testStopAgents_1()
            throws Exception {
        AgentChannelImpl fixture = new AgentChannelImpl();
        List<String> instanceIds = new LinkedList();

        fixture.stopAgents(instanceIds);

    }
}
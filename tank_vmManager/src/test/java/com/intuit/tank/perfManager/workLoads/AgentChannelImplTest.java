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

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

import com.intuit.tank.vm.api.enumerated.AgentCommand;

/**
 * The class <code>AgentChannelImplTest</code> contains tests for the class <code>{@link AgentChannelImpl}</code>.
 */
public class AgentChannelImplTest {

    private static final List<String> INSTANCES = List.of("i-1", "i-2");

    private JobManager jobManager;
    private AgentChannelImpl fixture;

    @BeforeEach
    void setUp() throws Exception {
        jobManager = mock(JobManager.class);
        fixture = new AgentChannelImpl();
        Field field = AgentChannelImpl.class.getDeclaredField("jobManager");
        field.setAccessible(true);
        field.set(fixture, jobManager);
    }

    @Test
    void stopAgentsSendsStop() {
        fixture.stopAgents(INSTANCES);
        verify(jobManager).sendCommand(INSTANCES, AgentCommand.stop);
    }

    @Test
    void pauseAgentsSendsPause() {
        fixture.pauseAgents(INSTANCES);
        verify(jobManager).sendCommand(INSTANCES, AgentCommand.pause);
    }

    @Test
    void pauseRampSendsPauseRamp() {
        fixture.pauseRamp(INSTANCES);
        verify(jobManager).sendCommand(INSTANCES, AgentCommand.pause_ramp);
    }

    @Test
    void resumeRampSendsResumeRamp() {
        fixture.resumeRamp(INSTANCES);
        verify(jobManager).sendCommand(INSTANCES, AgentCommand.resume_ramp);
    }

    @Test
    void killAgentsSendsKill() {
        fixture.killAgents(INSTANCES);
        verify(jobManager).sendCommand(INSTANCES, AgentCommand.kill);
    }

    @Test
    void restartAgentsResumesTheRamp() {
        fixture.restartAgents(INSTANCES);
        verify(jobManager).sendCommand(INSTANCES, AgentCommand.resume_ramp);
    }
}

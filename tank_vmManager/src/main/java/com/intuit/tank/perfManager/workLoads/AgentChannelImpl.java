/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.util.List;

import jakarta.inject.Inject;

import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.perfManager.AgentChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * AgentChannelImpl communication channel for agents.
 * 
 * @author dangleton
 * 
 */

public class AgentChannelImpl implements AgentChannel {

    private static final Logger LOG = LogManager.getLogger(AgentChannelImpl.class);


    @Inject
    private JobManager jobManager;

    public void stopAgents(List<String> instanceIds) {
        LOG.info("AgentChannelImpl: stopping agents: {}", String.join(",", instanceIds));
        jobManager.sendCommand(instanceIds, AgentCommand.stop);
    }

    public void pauseAgents(List<String> instanceIds) {
        LOG.info("AgentChannelImpl: pausing agents: {}", String.join(",", instanceIds));
        jobManager.sendCommand(instanceIds, AgentCommand.pause);
    }

    public void pauseRamp(List<String> instanceIds) {
        LOG.info("AgentChannelImpl: pausing ramp: {}", String.join(",", instanceIds));
        jobManager.sendCommand(instanceIds, AgentCommand.pause_ramp);
    }

    public void resumeRamp(List<String> instanceIds) {
        LOG.info("AgentChannelImpl: resuming ramp: {}", String.join(",", instanceIds));
        jobManager.sendCommand(instanceIds, AgentCommand.resume_ramp);
    }

    public void killAgents(List<String> instanceIds) {
        LOG.info("AgentChannelImpl: killing agents: {}", String.join(",", instanceIds));
        jobManager.sendCommand(instanceIds, AgentCommand.kill);
    }

    public void restartAgents(List<String> instanceIds) {
        LOG.info("AgentChannelImpl: restarting agents: {}", String.join(",", instanceIds));
        jobManager.sendCommand(instanceIds, AgentCommand.resume_ramp);
    }
}

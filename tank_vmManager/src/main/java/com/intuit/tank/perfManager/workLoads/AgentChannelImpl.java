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

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;
import com.intuit.tank.vm.perfManager.AgentChannel;

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

    public void stopAgents(List<String> instanceIds) { sendCmd(instanceIds, WatsAgentCommand.stop); }

    public void pauseAgents(List<String> instanceIds) { sendCmd(instanceIds, WatsAgentCommand.pause); }

    public void pauseRamp(List<String> instanceIds) { sendCmd(instanceIds, WatsAgentCommand.pause_ramp); }

    public void resumeRamp(List<String> instanceIds) {
        sendCmd(instanceIds, WatsAgentCommand.resume_ramp);
    }

    public void killAgents(List<String> instanceIds) {
        sendCmd(instanceIds, WatsAgentCommand.kill);
    }

    public void restartAgents(List<String> instanceIds) { sendCmd(instanceIds, WatsAgentCommand.resume_ramp); }

    private void sendCmd(List<String> ids, WatsAgentCommand cmd) {
        for (String instanceId : ids) {
            LOG.info("sending message " + cmd + " to instance with id " + instanceId);
            jobManager.sendCommand(instanceId, cmd);
        }
    }

}

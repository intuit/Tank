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

import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.perfManager.AgentChannel;

/**
 * AgentChannelImpl communication channel for agents.
 * 
 * @author dangleton
 * 
 */

public class AgentChannelImpl implements AgentChannel {

    @Inject
    private JobManager jobManager;

    public void stopAgents(List<String> instanceIds) { jobManager.sendCommand(instanceIds, AgentCommand.stop); }

    public void pauseAgents(List<String> instanceIds) { jobManager.sendCommand(instanceIds, AgentCommand.pause); }

    public void pauseRamp(List<String> instanceIds) { jobManager.sendCommand(instanceIds, AgentCommand.pause_ramp); }

    public void resumeRamp(List<String> instanceIds) { jobManager.sendCommand(instanceIds, AgentCommand.resume_ramp); }

    public void killAgents(List<String> instanceIds) { jobManager.sendCommand(instanceIds, AgentCommand.kill); }

    public void restartAgents(List<String> instanceIds) { jobManager.sendCommand(instanceIds, AgentCommand.resume_ramp); }
}

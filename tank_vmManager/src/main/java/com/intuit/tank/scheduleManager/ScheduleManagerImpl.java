package com.intuit.tank.scheduleManager;

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest;
import com.intuit.tank.vm.agent.messages.AgentMngrAPIResponse;
import com.intuit.tank.vm.scheduleManager.JobRequestData;
import com.intuit.tank.vm.scheduleManager.ScheduleManager;

public class ScheduleManagerImpl implements ScheduleManager {

    private static Logger logger = LogManager.getLogger(ScheduleManagerImpl.class);

    private static Map<String, JobRequestData> elements = new ConcurrentHashMap<String, JobRequestData>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addJob(AgentMngrAPIRequest request) {
        logger.debug("adding job for job id " + request.getJobId());
        JobRequestData data = new JobRequestData(request);
        elements.put(request.getJobId(), data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAgent(AgentMngrAPIResponse agent) {
        JobRequestData data = elements.get(agent.getJobId());
        if (data != null) {
            logger.debug("Received agent job Id " + agent.getJobId());
            data.addAgent(agent);
        } else {
            logger.warn("Received agent for unknown job | Id sent is " + agent.getJobId());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JobRequestData getJob(String jobId) {
        logger.debug("getting job  id " + jobId);
        return elements.get(jobId);
    }
}

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

import java.util.ArrayList;
import java.util.Map;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.vmManager.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.perfManager.RequestAgents;
import com.intuit.tank.vm.scheduleManager.AgentDispatcher;
import com.intuit.tank.vm.vmManager.JobRequest;
import com.intuit.tank.vm.vmManager.JobUtil;
import com.intuit.tank.vm.vmManager.RegionRequest;
import com.intuit.tank.vm.vmManager.VMChannel;

public class IncreasingWorkLoad implements Runnable {

    private static Logger LOG = LogManager.getLogger(IncreasingWorkLoad.class);
    private JobRequest job;
    private VMChannel channel;
    private AgentDispatcher agentDispatcher;
    public IncreasingWorkLoad(VMChannel channel, AgentDispatcher agentDispatcher, JobRequest job) {
        this.job = job;
        this.agentDispatcher = agentDispatcher;
        this.channel = channel;
        LOG.info("Job requested with values: " + job);
    }

    @Override
    public void run() {
        AWSXRay.beginSubsegment("Ask.For.Agents.JobId." + job.getId());
        try {
            askForAgents(new JobInstanceAgentModel(job));
        } catch (Exception th) {
            LOG.error("Error starting agents: " + th.getMessage(), th);
        } finally {
            AWSXRay.endSubsegment();
        }
    }

    public JobRequest getJob() {
        return job;
    }

    private void askForAgents(JobInstanceAgentModel model) {

        LOG.debug("asking for agents...");

        // Calculate number of agents per region split for nonlinear workloads
        Map<RegionRequest, Integer> agentMapping = null;
        if (job.getIncrementStrategy().equals(IncrementStrategy.standard)){
            agentMapping = JobVmCalculator.getMachinesForAgentByUserPercentage(job.getNumAgents(), job.getRegions());
        }

        // start the non region dependent reporting resources if needed
        int totalUsers = 0;
        ArrayList<AgentMngrAPIRequest.UserRequest> urList = new ArrayList<AgentMngrAPIRequest.UserRequest>();
        for (RegionRequest jobRegion : job.getRegions()) {
            int users;
            if(job.getIncrementStrategy().equals(IncrementStrategy.increasing)) {
                users = JobUtil.parseUserString(jobRegion.getUsers());
                LOG.info("Starting " + users + " users in region " + jobRegion.getRegion().getDescription() + " for job "
                        + job.getId());
            } else {
                users = JobUtil.parseUserString(jobRegion.getPercentage());
                LOG.info("Nonlinear - Starting " + users + "% of users in region " + jobRegion.getRegion().getDescription()
                        + " with " + agentMapping.get(jobRegion) + " allocated agents for job " + job.getId());
                users = agentMapping.get(jobRegion); // reassign users to the number of agents allocated for this region - nonlinear
            }
            totalUsers += users;
            if (users > 0) {
                VMRegion region = jobRegion.getRegion();
                urList.add(new AgentMngrAPIRequest.UserRequest(region, users));
                // AgentRunner.setRequestedAgents(model);
                RequestAgents request = new RequestAgents(job.getId(), job.getReportingMode(), job.getLoggingProfile(),
                        region, job.getIncrementStrategy(), users, job.getStopBehavior());
                request.setVmInstanceType(job.getVmInstanceType());
                request.setUserEips(job.isUseEips());
                request.setNumUsersPerAgent(job.getNumUsersPerAgent());
                agentDispatcher.processAgentsMessage(request);
            }
        }
        if (totalUsers <= 0 && job.getIncrementStrategy().equals(IncrementStrategy.increasing)) {
            LOG.warn("Attempt to start a job with no users.");
        }
    }

}

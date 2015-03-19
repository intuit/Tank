package com.intuit.tank.scheduleManager.jms;

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

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.project.VMInstance;
import com.intuit.tank.vm.agent.messages.WatsAgentCompletedResponse;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.perfManager.RequestAgents;
import com.intuit.tank.vm.scheduleManager.AgentDispatcher;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vm.vmManager.VMKillRequest;
import com.intuit.tank.vm.vmManager.VMRequest;
import com.intuit.tank.vm.vmManager.VmMessageProcessor;

public class AgentDispatcherImpl implements AgentDispatcher {

    static Logger logger = Logger.getLogger(AgentDispatcherImpl.class);

    @Inject
    private VmMessageProcessor vmProcessor;

    /**
     * @{inheritDoc
     */
    @Override
    public void vmManagerRequest(VMRequest vmInfo) {
        logger.debug("received vm request");
        vmProcessor.handleVMRequest(vmInfo);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void processAgentsMessage(RequestAgents agentRequest) {
        try {
            logger.debug("received agent request");
            // Request virtual machines
            VMRequest vmRequest = new VMJobRequest(agentRequest.getJobId(), agentRequest.getReportingMode(),
                    agentRequest.getLoggingProfile(),
                    agentRequest.getNumberOfUsers(),
                    agentRequest.getRegion(), agentRequest.getStopBehavior(), agentRequest.getVmInstanceType(),
                    agentRequest.getNumUsersPerAgent());
            ((VMJobRequest)vmRequest).setUserEips(agentRequest.isUseEips());
            vmManagerRequest(vmRequest);

        } catch (Exception ex) {
            logger.error("Error creating vmRequest: " + ex, ex);
        }
    }

    // lookup VMImage, and update its status
    private void updateDatabaseWithStatus(WatsAgentCompletedResponse moObject, JobStatus status) {
        String instanceId = moObject.getInstanceId();
        VMProvider provider = moObject.getProvider();

        if (instanceId != null && provider != null) {
            // Find the entry matching the instanceId and add the job ID
            VMImageDao dao = new VMImageDao();
            VMInstance image = dao.getImageByInstanceId(instanceId);
            image.setStatus(status.toString());
            dao.saveOrUpdate(image);
            logger.debug("Updated VMImage with instance id " + instanceId + " with status: " + status.toString());
        } else {
            if (instanceId == null) {
                logger.error("Agent Completed response contained null instanceId");
            }
            if (provider == null) {
                logger.error("Agent Completed response contained null provider");
            }
        }
    }

    /**
     * @{inheritDoc
     */
    @Override
    public void processAgentCompletedResponse(WatsAgentCompletedResponse moObject) {
        // update DB
        updateDatabaseWithStatus(moObject, JobStatus.Completed);

        // send kill to VMManager
        VMRequest vmRequest = new VMKillRequest(moObject.getProvider(), moObject.getInstanceId());
        vmManagerRequest(vmRequest);
    }
}

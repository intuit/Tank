package com.intuit.tank.vmManager.environment;

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
import java.util.List;

import org.apache.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.vmManager.JobVmCalculator;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vmManager.AgentWatchdog;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

public class JobRequest implements Runnable {

    static Logger logger = Logger.getLogger(JobRequest.class);

    private VMJobRequest request = null;
    private VMTracker tracker;

    public JobRequest(VMJobRequest request, VMTracker tracker) {
        this.request = request;
        this.tracker = tracker;
    }

    @Override
    public void run() {
        List<VMInformation> response = new ArrayList<VMInformation>();
        try {
            IEnvironmentInstance instance = null;
            VMInstanceRequest instanceRequest = this.populateAmazonRequest();
            int machines = JobVmCalculator.getMachinesForAgent(request.getNumberOfUsers(),
                    request.getNumUsersPerAgent());
            instanceRequest.setNumberOfInstances(machines);
            instanceRequest.setUserEips(request.isUseEips());
            instanceRequest.setNumUsersPerAgent(JobVmCalculator.getOptimalUsersPerAgent(request.getNumberOfUsers(),
                    machines));
            instanceRequest.setSize(request.getVmInstanceType());
            instance = this.getEnvironment(instanceRequest);
            response.addAll(instance.create());
            persistInstances(instanceRequest, response);
        } catch (Exception ex) {
            logger.error("Error : " + ex, ex);
        }
    }

    /**
     * 
     * @param instanceRequest
     * @param vmInfo
     */
    private void persistInstances(VMInstanceRequest instanceRequest, List<VMInformation> vmInfo) {
        logger.info("Created " + vmInfo.size() + " Amazon instances.");
        VMImageDao dao = new VMImageDao();
        // create a watchdog to monitor these instances
        AgentWatchdog watchDog = new AgentWatchdog(instanceRequest, vmInfo, tracker);
        // persist the VMImages to database:
        for (VMInformation info : vmInfo) {
            try {
                tracker.setStatus(createCloudStatus(instanceRequest, info));
                dao.addImageFromInfo(request.getJobId(), info, request.getRegion());
                logger.info("Added image (" + info.getInstanceId() + ") to VMImage table");
            } catch (Exception e) {
                logger.warn("Error persisting VM Image: " + e, e);
            }
        }
        Thread thread = new Thread(watchDog);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * @param request2
     * @param info
     * @return
     */
    private CloudVmStatus createCloudStatus(VMInstanceRequest req, VMInformation info) {
        logger.info("request = " + req + " : info = " + info);
        CloudVmStatus ret = new CloudVmStatus(info.getInstanceId(), req.getJobId(), "unknown", JobStatus.Starting,
                VMImageType.AGENT, req.getRegion(), VMStatus.pending, new ValidationStatus(), 0, 0, null, null);
        return ret;
    }

    /**
     * Populate the amazon instance request
     * 
     * @return The amazon request
     */
    private VMInstanceRequest populateAmazonRequest() {
        VMInstanceRequest output = new VMInstanceRequest();
        try {
            output.setImage(VMImageType.AGENT);
            output.setJobId(this.request.getJobId());
            output.setLoggingProfile(this.request.getLoggingProfile());
            output.setStopBehavior(this.request.getStopBehavior());
            output.setProvider(VMProvider.Amazon);
            output.setRegion(this.request.getRegion());
            output.setReportingMode(this.request.getReportingMode());
            return output;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Get the appropriate environment
     * 
     * @return The appropriate environment object
     */
    private IEnvironmentInstance getEnvironment(VMInstanceRequest instanceRequest) {
        // TODO: Implement logic to handle multiple environments
        try {
            IEnvironmentInstance environment = null;
            if (this.request.getProvider() != null) {
                switch (this.request.getProvider()) {
                case Amazon:
                    environment = new AmazonInstance(instanceRequest, request.getRegion());
                    break;
                case Pharos:
                    break;
                }
            } else {
                // TODO: Implement logic to determine which environment to use
                environment = new AmazonInstance(instanceRequest, request.getRegion());
            }
            return environment;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }
}

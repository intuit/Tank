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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.logging.ControllerLoggingConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.dao.VMImageDao;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMProvider;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.perfManager.workLoads.ControllerInitiatedAgentWsClient;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.vm.vmManager.JobVmCalculator;
import com.intuit.tank.vm.vmManager.VMInformation;
import com.intuit.tank.vm.vmManager.VMInstanceRequest;
import com.intuit.tank.vm.vmManager.VMJobRequest;
import com.intuit.tank.vmManager.AgentWatchdog;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;
import org.apache.logging.log4j.message.ObjectMessage;

public class JobRequest implements Runnable {

    static Logger logger = LogManager.getLogger(JobRequest.class);

    private VMJobRequest request = null;
    private VMTracker vmTracker;
    private ControllerInitiatedAgentWsClient controllerInitiatedAgentWsClient;
    private JobManager jobManager;
    private TankConfig tankConfig;

    public JobRequest(VMJobRequest request, VMTracker tracker) {
        this(request, tracker, null, null, new TankConfig());
    }

    public JobRequest(VMJobRequest request,
                      VMTracker tracker,
                      ControllerInitiatedAgentWsClient controllerInitiatedAgentWsClient,
                      JobManager jobManager,
                      TankConfig tankConfig) {
        this.request = request;
        this.vmTracker = tracker;
        this.controllerInitiatedAgentWsClient = controllerInitiatedAgentWsClient;
        this.jobManager = jobManager;
        this.tankConfig = tankConfig;
    }

    @Override
    public void run() {
        try {
            ControllerLoggingConfig.setupThreadContext();
            VMInstanceRequest instanceRequest = this.populateAmazonRequest();
            int machines = 0;
            if(request.getIncrementStrategy().equals(IncrementStrategy.increasing)) {
                machines = JobVmCalculator.getMachinesForAgent(request.getNumberOfUsers(),
                        request.getNumUsersPerAgent());
                instanceRequest.setNumUsersPerAgent(JobVmCalculator.getOptimalUsersPerAgent(request.getNumberOfUsers(),
                        machines));
            } else {
                machines = request.getNumberOfUsers(); // non-linear: for simplicity we pass the number of machines as the number of users
            }
            instanceRequest.setNumberOfInstances(machines);
            instanceRequest.setUserEips(request.isUseEips());
            instanceRequest.setSize(request.getVmInstanceType());
            List<VMInformation> response = this.getEnvironment().create(instanceRequest);
            persistInstances(instanceRequest, response);
        } catch (Exception ex) {
            logger.error(new ObjectMessage(Map.of("Message", "Error : " + ex)), ex);
        }
    }

    /**
     * 
     * @param instanceRequest
     * @param vmInfo
     */
    private void persistInstances(VMInstanceRequest instanceRequest, List<VMInformation> vmInfo) {
        ControllerLoggingConfig.setupThreadContext();
        logger.info(new ObjectMessage(Map.of("Message","Created " + vmInfo.size() + " Amazon instances.")));
        VMImageDao dao = new VMImageDao();
        // create a watchdog to monitor these instances
        AgentWatchdog watchDog = new AgentWatchdog(instanceRequest, vmInfo, vmTracker);
        // persist the VMImages to database:
        for (VMInformation info : vmInfo) {
            try {
                vmTracker.setStatus(createCloudStatus(instanceRequest, info));
                dao.addImageFromInfo(request.getJobId(), info, request.getRegion());
                logger.info(new ObjectMessage(Map.of(
                    "Message", "Added image to VMImage table",
                    "instanceId", info.getInstanceId(),
                    "publicIp", info.getPublicIp() != null ? info.getPublicIp() : "N/A",
                    "privateIp", info.getPrivateIp() != null ? info.getPrivateIp() : "N/A"
                )));
                registerReadyViaControllerInitiatedWs(instanceRequest, info);
            } catch (Exception e) {
                logger.warn(new ObjectMessage(Map.of("Message", "Error persisting VM Image: " + e)), e);
            }
        }
        Thread thread = new Thread(watchDog);
        thread.setDaemon(true);
        thread.start();
    }

    private void registerReadyViaControllerInitiatedWs(VMInstanceRequest instanceRequest, VMInformation info) {
        if (controllerInitiatedAgentWsClient == null || jobManager == null || tankConfig == null) {
            return;
        }
        if (!tankConfig.getAgentConfig().isControllerInitiatedWsEnabled()) {
            return;
        }

        String wsUrl = buildWsUrl(instanceRequest, info);
        if (StringUtils.isBlank(wsUrl)) {
            logger.warn("Unable to build controller-initiated WS URL for {}", info.getInstanceId());
            return;
        }

        Optional<AgentWsEnvelope> hello = Optional.empty();
        int attempts = 0;
        while (hello.isEmpty() && attempts < 8) {
            attempts++;
            hello = controllerInitiatedAgentWsClient.connect(
                    info.getInstanceId(),
                    wsUrl,
                    tankConfig.getAgentConfig().getAgentToken(),
                    10000L);
            if (hello.isPresent()) {
                break;
            }
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (hello.isEmpty()) {
            logger.warn("Controller-initiated WS hello not received for {}", info.getInstanceId());
            return;
        }

        int capacity = hello.get().getCapacity() != null ? hello.get().getCapacity() : instanceRequest.getNumUsersPerAgent();
        String instanceUrl = buildHttpUrl(instanceRequest, info);
        AgentData agentData = new AgentData(instanceRequest.getJobId(), info.getInstanceId(), instanceUrl,
                capacity, instanceRequest.getRegion(), "unknown");
        jobManager.registerAgentForJob(agentData);
    }

    private String buildWsUrl(VMInstanceRequest instanceRequest, VMInformation info) {
        String host = selectHostForRegion(instanceRequest.getRegion(), info);
        if (StringUtils.isBlank(host)) {
            return null;
        }
        String wsPath = tankConfig.getAgentConfig().getCommandWsPath();
        if (!wsPath.startsWith("/")) {
            wsPath = "/" + wsPath;
        }
        return "ws://" + host + ":" + tankConfig.getAgentConfig().getAgentPort() + wsPath;
    }

    private String buildHttpUrl(VMInstanceRequest instanceRequest, VMInformation info) {
        String host = selectHostForRegion(instanceRequest.getRegion(), info);
        if (StringUtils.isBlank(host)) {
            host = "localhost";
        }
        return "http://" + host + ":" + tankConfig.getAgentConfig().getAgentPort();
    }

    private String selectHostForRegion(VMRegion region, VMInformation info) {
        if (region == VMRegion.US_EAST || region == VMRegion.US_EAST_2) {
            return firstNonBlank(info.getPrivateIp(), info.getPrivateDNS(), info.getPublicIp(), info.getPublicDNS());
        }
        return firstNonBlank(info.getPublicDNS(), info.getPublicIp(), info.getPrivateIp(), info.getPrivateDNS());
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    /**
     * @param req
     * @param info
     * @return
     */
    private CloudVmStatus createCloudStatus(VMInstanceRequest req, VMInformation info) {
        logger.trace("request = " + req + " : info = " + info);
        return new CloudVmStatus(info.getInstanceId(), req.getJobId(), "unknown", JobStatus.Starting,
                VMImageType.AGENT, req.getRegion(), VMStatus.starting, new ValidationStatus(), 0, 0, null, null);
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
    private IEnvironmentInstance getEnvironment() {
        // TODO: Implement logic to handle multiple environments
        try {
            IEnvironmentInstance environment = null;
            if (this.request.getProvider() != null) {
                switch (this.request.getProvider()) {
                case Amazon:
                    environment = new AmazonInstance(request.getRegion());
                    break;
                case Pharos:
                    break;
                }
            } else {
                // TODO: Implement logic to determine which environment to use
                environment = new AmazonInstance(request.getRegion());
            }
            return environment;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }
}

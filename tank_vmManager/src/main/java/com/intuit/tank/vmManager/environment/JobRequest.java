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

import com.amazonaws.xray.AWSXRay;
import com.amazonaws.xray.entities.Segment;

import com.intuit.tank.perfManager.workLoads.ControllerInitiatedAgentWsClient;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.logging.ControllerLoggingConfig;
import com.intuit.tank.vm.settings.AgentConfig;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
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
import org.apache.logging.log4j.message.ObjectMessage;

public class JobRequest implements Runnable {

    static Logger logger = LogManager.getLogger(JobRequest.class);

    private VMJobRequest request = null;
    private VMTracker vmTracker;
    private JobManager jobManager;

    public JobRequest(VMJobRequest request, VMTracker tracker) {
        this(request, tracker, null);
    }

    public JobRequest(VMJobRequest request, VMTracker tracker, JobManager jobManager) {
        this.request = request;
        this.vmTracker = tracker;
        this.jobManager = jobManager;
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
            startControllerInitiatedWs(instanceRequest, response);
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
        AgentWatchdog watchDog = new AgentWatchdog(instanceRequest, vmInfo, vmTracker,
                (req, vms) -> startControllerInitiatedWs(req, vms));
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
            } catch (Exception e) {
                logger.warn(new ObjectMessage(Map.of("Message", "Error persisting VM Image: " + e)), e);
            }
        }
        Thread thread = new Thread(watchDog);
        thread.setDaemon(true);
        thread.start();
    }

    private void startControllerInitiatedWs(VMInstanceRequest instanceRequest, List<VMInformation> vmInfo) {
        AgentConfig agentConfig = new TankConfig().getAgentConfig();
        if (!agentConfig.isCommandWsEnabled() || jobManager == null || vmInfo == null || vmInfo.isEmpty()) {
            return;
        }

        ControllerInitiatedAgentWsClient wsClient = jobManager.getControllerInitiatedAgentWsClient();
        String token = agentConfig.getAgentToken();
        int port = agentConfig.getAgentPort();
        long helloTimeoutMillis = Math.max(30_000L, agentConfig.getMaxAgentResponseTime());
        long transferTimeoutMillis = Math.max(120_000L, agentConfig.getMaxAgentWaitTime());
        long maxConnectWaitMillis = Math.max(transferTimeoutMillis, 600_000L);

        for (VMInformation info : vmInfo) {
            Thread thread = new Thread(() -> {
                ControllerLoggingConfig.setupThreadContext();
                // Retries for up to maxConnectWaitMillis, well past the job-start request's segment, so it gets its own
                Segment segment = AWSXRay.beginSegmentWithSampling("Tank.AgentWsBootstrap");
                try {
                    segment.putAnnotation("jobId", String.valueOf(instanceRequest.getJobId()));
                    segment.putAnnotation("instanceId", info.getInstanceId());
                    connectAndBootstrapAgent(wsClient, instanceRequest, info, token, port,
                            helloTimeoutMillis, transferTimeoutMillis, maxConnectWaitMillis);
                } finally {
                    AWSXRay.endSegment();
                }
            });
            thread.setName("ControllerInitiatedWsBootstrap-" + info.getInstanceId());
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void connectAndBootstrapAgent(ControllerInitiatedAgentWsClient wsClient, VMInstanceRequest instanceRequest,
                                          VMInformation info, String token, int port, long helloTimeoutMillis,
                                          long transferTimeoutMillis, long maxConnectWaitMillis) {
        IEnvironmentInstance environment = getEnvironment();
        long start = System.currentTimeMillis();
        int attempt = 1;
        while (System.currentTimeMillis() - start < maxConnectWaitMillis) {
            // The launch response has no public IP yet; pick it up here rather than holding up the launch
            info = refreshPublicAddress(info, environment);
            String host = resolveWsHost(request.getRegion(), info);
            String wsUrl = "ws://" + host + ":" + port + "/ws/control";
            try {
                if (host == null) {
                    throw new IllegalStateException("No reachable host found");
                }
                String instanceUrl = "http://" + host + ":" + port;
                boolean complete = wsClient.connectAndBootstrap(jobManager, info.getInstanceId(), instanceUrl, wsUrl,
                        token, helloTimeoutMillis, transferTimeoutMillis);
                if (complete) {
                    return;
                }
                logger.info(new ObjectMessage(Map.of("Message",
                        "[WS] Controller initiated connect attempt " + attempt + " incomplete for "
                                + info.getInstanceId() + " at " + wsUrl + " — will retry")));
            } catch (Exception e) {
                logger.warn(new ObjectMessage(Map.of("Message",
                        "[WS] Controller initiated connect attempt " + attempt + " failed for "
                                + info.getInstanceId() + " at " + wsUrl + ": " + e.getMessage())));
            }
            try {
                Thread.sleep(Math.min(10_000L, 2_000L * attempt));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            attempt++;
        }
        logger.error(new ObjectMessage(Map.of("Message",
                "[WS] Timed out connecting controller-initiated WS to " + info.getInstanceId()
                        + " for job " + instanceRequest.getJobId())));
    }

    /**
     * Re-describe an agent that has no public address yet; whether it gets one depends on its subnet, not its
     * region. Stops once the instance is running: a public IP is assigned by then if it is getting one, so a
     * private-only agent keeps its private IP without further lookups.
     */
    static VMInformation refreshPublicAddress(VMInformation info, IEnvironmentInstance environment) {
        if (environment == null
                || isNotBlank(info.getPublicIp()) || isNotBlank(info.getPublicDNS())
                || "running".equalsIgnoreCase(info.getState())) {
            return info;
        }
        List<VMInformation> described = environment.describeInstances(List.of(info.getInstanceId()));
        return described.isEmpty() ? info : described.getFirst();
    }

    public static String resolveWsHost(VMRegion region, VMInformation info) {
        if (isControllerRegion(region) && isNotBlank(info.getPrivateIp())) {
            return info.getPrivateIp();
        }
        if (isNotBlank(info.getPublicIp())) {
            return info.getPublicIp();
        }
        if (isNotBlank(info.getPublicDNS())) {
            return info.getPublicDNS();
        }
        if (isNotBlank(info.getPrivateIp())) {
            return info.getPrivateIp();
        }
        if (isNotBlank(info.getPrivateDNS())) {
            return info.getPrivateDNS();
        }
        return null;
    }

    /**
     * Agents in these regions are reached on their private IP; elsewhere they need a public address.
     */
    private static boolean isControllerRegion(VMRegion region) {
        return region == VMRegion.US_EAST || region == VMRegion.US_EAST_2;
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
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

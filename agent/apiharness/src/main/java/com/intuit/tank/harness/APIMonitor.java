package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.AgentCommand;

public class APIMonitor implements Runnable {

    /**
     * 
     */
    private static final int MIN_REPORT_TIME = 15000;
    private static final Logger LOG = LogManager.getLogger(APIMonitor.class);
    private static final ObjectWriter objectWriter = new ObjectMapper().writerFor(CloudVmStatus.class).withDefaultPrettyPrinter();
    private static boolean doMonitor = true;
    private static final HttpClient client = HttpClient.newHttpClient();
    private static CloudVmStatus status;
    private long reportInterval = APIMonitor.MIN_REPORT_TIME;
    private boolean isLocal;

    public APIMonitor(Boolean isLocal, CloudVmStatus vmStatus) {
        this.isLocal = isLocal;
        status = vmStatus;
        try {
            reportInterval = Math.max(APITestHarness.getInstance().getTankConfig().getAgentConfig()
                    .getStatusReportIntervalMilis(reportInterval), MIN_REPORT_TIME);
        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage("Error initializing monitor: " + e.getMessage()), e);
        }
    }

    @Override
    public void run() {
        LOG.debug(LogUtil.getLogMessage("APIMonitor thread started."));
        while (doMonitor) {
            updateInstanceStatus();
            try {
                Thread.sleep(reportInterval);
            } catch ( InterruptedException ie) { /*Ignore*/ }
        }
        updateInstanceStatus();
    }

    private void updateInstanceStatus() {
        try {
            CloudVmStatus newStatus = createStatus(APITestHarness.getInstance().getStatus());
            newStatus.setUserDetails(APITestHarness.getInstance().getUserTracker().getSnapshot());
            TPSInfoContainer tpsInfo = APITestHarness.getInstance().getTPSMonitor().getTPSInfo();
            if (tpsInfo != null) {
                newStatus.setTotalTps(tpsInfo.getTotalTps());
                sendTps(tpsInfo);
            }
            
            if (!isLocal) {
                setInstanceStatus(newStatus.getInstanceId(), newStatus);
            }
            APITestHarness.getInstance().checkAgentThreads();
        } catch (Exception t) {
            LOG.error(LogUtil.getLogMessage("Unable to send status metrics | " + t.getMessage()), t);
        }
    }

    private void sendTps(final TPSInfoContainer tpsInfo) {
        APITestHarness.getInstance().getResultsReporter()
                .sendTpsResults(APITestHarness.getInstance().getAgentRunData().getJobId(),
                        APITestHarness.getInstance().getAgentRunData().getInstanceId(), tpsInfo, true);

    }

    /**
     * @param agentStatus
     *            for this instance
     * @return
     */
    private CloudVmStatus createStatus(WatsAgentStatusResponse agentStatus) {
        return new CloudVmStatus(status.getInstanceId(), status.getJobId(), status.getSecurityGroup(),
                calculateJobStatus(agentStatus, status.getJobStatus()), status.getRole(), status.getVmRegion(),
                status.getVmStatus(),
                new ValidationStatus(agentStatus.getKills(), agentStatus.getAborts(),
                        agentStatus.getGotos(), agentStatus.getSkips(), agentStatus.getSkipGroups(), agentStatus.getRestarts()),
                agentStatus.getMaxVirtualUsers(),
                agentStatus.getCurrentNumberUsers(), status.getStartTime(), status.getEndTime());
    }

    /**
     * @param agentStatus
     *            for this instance
     * @param currentStatus
     * @return
     */
    private JobStatus calculateJobStatus(WatsAgentStatusResponse agentStatus, JobStatus currentStatus) {
        AgentCommand cmd = APITestHarness.getInstance().getCmd();
        JobStatus newStatus = cmd == AgentCommand.pause ? JobStatus.Paused
                : cmd == AgentCommand.stop ? JobStatus.Stopped
                : cmd == AgentCommand.pause_ramp ? JobStatus.RampPaused
                : currentStatus == JobStatus.Unknown
                    || currentStatus == JobStatus.Starting
                    && agentStatus.getCurrentNumberUsers() > 0 ? JobStatus.Running
                : currentStatus;
        
        if (newStatus != currentStatus) {
            LOG.info(LogUtil.getLogMessage("Agent JobStatus transition: " + currentStatus + " -> " + newStatus +
                " (cmd=" + cmd + ", currentUsers=" + agentStatus.getCurrentNumberUsers() + ")"));
        }
        
        return newStatus;
    }

    public static void setDoMonitor(boolean monitor) {
        LOG.debug(LogUtil.getLogMessage("Setting doMonitor to: " + monitor));
        doMonitor = monitor;
    }

    public static void setJobStatus(JobStatus jobStatus) {
        LOG.debug(LogUtil.getLogMessage("Setting job status to: " + jobStatus));
        // Capture immutable status snapshot inside lock, send outside to avoid
        // blocking the synchronized section on network I/O (terminal retry can take ~6s)
        String instanceId;
        CloudVmStatus statusToSend;
        synchronized (APIMonitor.class) {
            if (status == null || status.getJobStatus() == JobStatus.Completed) {
                return;
            }
            try {
                VMStatus vmStatus = jobStatus.equals(JobStatus.Stopped) ? VMStatus.stopping
                        : jobStatus.equals(JobStatus.RampPaused) ? VMStatus.rampPaused
                        : jobStatus.equals(JobStatus.Running) ? VMStatus.running
                        : jobStatus.equals(JobStatus.Completed) ? VMStatus.terminated : status.getVmStatus();
                WatsAgentStatusResponse stats = APITestHarness.getInstance().getStatus();
                Date endTime = (jobStatus == JobStatus.Completed) ? new Date() : status.getEndTime();
                // Build into local — only assign to static 'status' after full success
                CloudVmStatus newStatus = new CloudVmStatus(status.getInstanceId(), status.getJobId(), status.getSecurityGroup(),
                        jobStatus, status.getRole(), status.getVmRegion(), vmStatus,
                        new ValidationStatus(stats.getKills(), stats.getAborts(),
                                stats.getGotos(), stats.getSkips(), stats.getSkipGroups(), stats.getRestarts()),
                        stats.getMaxVirtualUsers(),
                        stats.getCurrentNumberUsers(), status.getStartTime(), endTime);
                newStatus.setUserDetails(APITestHarness.getInstance().getUserTracker().getSnapshot());
                status = newStatus;
                instanceId = status.getInstanceId();
                statusToSend = status;
            } catch (Exception e) {
                LOG.error("Error building status snapshot in setJobStatus: {}", e.toString(), e);
                return;
            }
        }
        // Network send is outside the lock — terminal retry won't block other threads
        try {
            setInstanceStatus(instanceId, statusToSend);
        } catch (Exception e) {
            LOG.error("Error sending status to controller: {}", e.toString(), e);
        }
    }

    protected static void setInstanceStatus(String instanceId, CloudVmStatus vmStatus) throws URISyntaxException, JsonProcessingException {
        // Force currentUsers=0 on terminal statuses — agent threads may not have fully exited
        boolean isTerminal = vmStatus.getJobStatus() == JobStatus.Completed
            || vmStatus.getJobStatus() == JobStatus.Stopped
            || vmStatus.getVmStatus() == VMStatus.terminated;
        if (isTerminal) {
            vmStatus.setCurrentUsers(0);
        }

        String json = objectWriter.writeValueAsString(vmStatus);
        String token = APITestHarness.getInstance().getTankConfig().getAgentConfig().getAgentToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(APITestHarness.getInstance().getTankConfig().getControllerBase() + "/v2/agent/instance/status/" + instanceId))
                .header(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType())
                .header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType())
                .header(HttpHeaders.AUTHORIZATION, "bearer " + token)
                .timeout(Duration.ofSeconds(10))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        LOG.debug(LogUtil.getLogMessage("Sending instance status update for instance: " + instanceId + ", Status: " + json));

        if (isTerminal) {
            // Terminal statuses use synchronous send with retry — losing the final Completed
            // report causes the controller to show stale "Running" status indefinitely
            for (int attempt = 1; attempt <= 3; attempt++) {
                try {
                    HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                    if (response.statusCode() == 200 || response.statusCode() == 202 || response.statusCode() == 204) {
                        LOG.info(LogUtil.getLogMessage("Terminal status delivered on attempt " + attempt));
                        return;
                    }
                    LOG.warn(LogUtil.getLogMessage("Terminal status delivery got " + response.statusCode() + " on attempt " + attempt));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    LOG.warn(LogUtil.getLogMessage("Terminal status delivery interrupted on attempt " + attempt));
                    return;
                } catch (Exception e) {
                    LOG.warn(LogUtil.getLogMessage("Terminal status delivery failed on attempt " + attempt + ": " + e.getMessage()));
                }
                if (attempt < 3) {
                    try { Thread.sleep(2000); } catch (InterruptedException ignored) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            LOG.error(LogUtil.getLogMessage("Failed to deliver terminal status after 3 attempts for instance " + instanceId));
        } else {
            // Non-terminal statuses remain async (fire-and-forget is fine for periodic updates)
            client.sendAsync(request, HttpResponse.BodyHandlers.discarding());
        }
    }
}

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

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.rest.mvc.rest.clients.AgentClient;
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
    private static Logger LOG = LogManager.getLogger(APIMonitor.class);
    private static boolean doMonitor = true;
    private static AgentClient client;
    private static CloudVmStatus status;
    private long reportInterval = APIMonitor.MIN_REPORT_TIME;
    private boolean isLocal;

    public APIMonitor(Boolean isLocal, CloudVmStatus vmStatus) {
        this.isLocal = isLocal;
        status = vmStatus;
        try {
            client = new AgentClient(APITestHarness.getInstance().getTankConfig().getControllerBase(), null);
            reportInterval = Math.max(APITestHarness.getInstance().getTankConfig().getAgentConfig()
                    .getStatusReportIntervalMilis(reportInterval), MIN_REPORT_TIME);
        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage("Error initializing monitor: " + e.getMessage()), e);
        }
    }

    @Override
    public void run() {

        while (doMonitor) {
            try {
                CloudVmStatus newStatus = createStatus(APITestHarness.getInstance().getStatus());
                newStatus.setUserDetails(APITestHarness.getInstance().getUserTracker().getSnapshot());
                TPSInfoContainer tpsInfo = APITestHarness.getInstance().getTPSMonitor().getTPSInfo();
                if (tpsInfo != null) {
                    newStatus.setTotalTps(tpsInfo.getTotalTps());
                    sendTps(tpsInfo);
                }
                if (!isLocal) client.setInstanceStatus(newStatus.getInstanceId(), newStatus);
                APITestHarness.getInstance().checkAgentThreads();
                Thread.sleep(reportInterval);
            } catch (Exception t) {
                LOG.error(LogUtil.getLogMessage("Unable to send status metrics | " + t.getMessage()), t);
            }
        }
        CloudVmStatus newStatus = createStatus(APITestHarness.getInstance().getStatus());
        client.setInstanceStatus(newStatus.getInstanceId(), newStatus);
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
        return cmd == AgentCommand.pause ? JobStatus.Paused
                : cmd == AgentCommand.stop ? JobStatus.Stopped
                : cmd == AgentCommand.pause_ramp ? JobStatus.RampPaused
                : currentStatus == JobStatus.Unknown
                    || currentStatus == JobStatus.Starting
                    && agentStatus.getCurrentNumberUsers() > 0 ? JobStatus.Running
                : currentStatus;
    }

    public static void setDoMonitor(boolean monitor) {
        doMonitor = monitor;
    }

    public synchronized static void setJobStatus(JobStatus jobStatus) {
        if (status != null && status.getJobStatus() != JobStatus.Completed) {
            try {
            	VMStatus vmStatus =  jobStatus.equals(JobStatus.Stopped) ? VMStatus.stopping
                        : jobStatus.equals(JobStatus.RampPaused) ? VMStatus.rampPaused
                        : jobStatus.equals(JobStatus.Running) ? VMStatus.running
                        : jobStatus.equals(JobStatus.Completed) ? VMStatus.terminated : status.getVmStatus();
                WatsAgentStatusResponse stats = APITestHarness.getInstance().getStatus();
                Date endTime = (jobStatus == JobStatus.Completed) ? new Date() : status
                        .getEndTime();
                status = new CloudVmStatus(status.getInstanceId(), status.getJobId(), status.getSecurityGroup(),
                        jobStatus, status.getRole(), status.getVmRegion(), vmStatus,
                        new ValidationStatus(stats.getKills(), stats.getAborts(),
                                stats.getGotos(), stats.getSkips(), stats.getSkipGroups(), stats.getRestarts()),
                        stats.getMaxVirtualUsers(),
                        stats.getCurrentNumberUsers(), status.getStartTime(), endTime);
                status.setUserDetails(APITestHarness.getInstance().getUserTracker().getSnapshot());
                client.setInstanceStatus(status.getInstanceId(), status);
            } catch (Exception e) {
                LOG.error("Error sending status to controller: " + e.toString(), e);
            }
        }
    }

}

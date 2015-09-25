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

import org.apache.log4j.Logger;

import com.intuit.tank.CloudServiceClient;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.reporting.api.TPSInfoContainer;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;

public class APIMonitor implements Runnable {

    /**
     * 
     */
    private static final int MIN_REPORT_TIME = 15000;
    private static Logger LOG = Logger.getLogger(APIMonitor.class);
    private static boolean doMonitor = true;
    private static CloudServiceClient client;
    private static CloudVmStatus status;
    private long reportInterval = APIMonitor.MIN_REPORT_TIME;

    public APIMonitor(CloudVmStatus vmStatus) {
        status = vmStatus;
        try {
            client = new CloudServiceClient(APITestHarness.getInstance().getTankConfig().getControllerBase());
            reportInterval = Math.max(APITestHarness.getInstance().getTankConfig().getAgentConfig()
                    .getStatusReportIntervalMilis(reportInterval), MIN_REPORT_TIME);
        } catch (Exception e) {
            LOG.error("Error initializing monitor: " + e, e);
        }
    }

    @Override
    public void run() {

        while (doMonitor) {
            try {
                CloudVmStatus newStatus = createStatus(APITestHarness.getInstance().getStats());
                newStatus.setUserDetails(APITestHarness.getInstance().getUserTracker().getSnapshot());
                TPSInfoContainer tpsInfo = APITestHarness.getInstance().getTPMonitor().getTPSInfo();
                if (tpsInfo != null) {
                    newStatus.setTotalTps(tpsInfo.getTotalTps());
                    sendTps(tpsInfo);
                }
                client.setVmStatus(newStatus.getInstanceId(), newStatus);
                APITestHarness.getInstance().checkAgentThreads();
                Thread.sleep(reportInterval);
            } catch (Exception t) {
                LOG.error(LogUtil.getLogMessage("Unable to send status metrics | " + t.getMessage()), t);
            }
        }
        CloudVmStatus newStatus = createStatus(APITestHarness.getInstance().getStats());
        client.setVmStatus(newStatus.getInstanceId(), newStatus);
    }

    private void sendTps(final TPSInfoContainer tpsInfo) {
        APITestHarness.getInstance().getResultsReporter()
                .sendTpsResults(APITestHarness.getInstance().getAgentRunData().getJobId(), APITestHarness
                        .getInstance().getAgentRunData().getInstanceId(), tpsInfo, true);

    }

    /**
     * @param Statistics
     *            for this instance
     * @return
     */
    private CloudVmStatus createStatus(WatsAgentStatusResponse stats) {
        CloudVmStatus ret = new CloudVmStatus(status.getInstanceId(), status.getJobId(), status.getSecurityGroup(),
                calculateJobStatus(stats, status.getJobStatus()), status.getRole(), status.getVmRegion(),
                status.getVmStatus(),
                new ValidationStatus(stats.getKills(), stats.getAborts(),
                        stats.getGotos(), stats.getSkips(), stats.getSkipGroups(), stats.getRestarts()),
                stats.getMaxVirtualUsers(),
                stats.getCurrentNumberUsers(), status.getStartTime(), status.getEndTime());
        return ret;
    }

    /**
     * @param Statistics
     *            for this instance
     * @return
     */
    private JobStatus calculateJobStatus(WatsAgentStatusResponse stats, JobStatus currentStatus) {
        if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.pause) {
            return JobStatus.Paused;
        } else if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.stop) {
            return JobStatus.Stopped;
        } else if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.pause_ramp) {
            return JobStatus.RampPaused;
        } else if ((currentStatus == JobStatus.Unknown || currentStatus == JobStatus.Starting)
                && stats.getCurrentNumberUsers() > 0) {
            return JobStatus.Running;
        }
        return currentStatus;
    }

    public static void setDoMonitor(boolean monitor) {
        doMonitor = monitor;
    }

    public synchronized static void setJobStatus(JobStatus jobStatus) {
        if (status.getJobStatus() != JobStatus.Completed) {
            try {
                WatsAgentStatusResponse stats = APITestHarness.getInstance().getStats();
                Date endTime = (jobStatus == JobStatus.Completed) ? new Date() : status
                        .getEndTime();
                status = new CloudVmStatus(status.getInstanceId(), status.getJobId(), status.getSecurityGroup(),
                        jobStatus, status.getRole(), status.getVmRegion(), status.getVmStatus(),
                        new ValidationStatus(stats.getKills(), stats.getAborts(),
                                stats.getGotos(), stats.getSkips(), stats.getSkipGroups(), stats.getRestarts()),
                        stats.getMaxVirtualUsers(),
                        stats.getCurrentNumberUsers(), status.getStartTime(), endTime);
                status.setUserDetails(APITestHarness.getInstance().getUserTracker().getSnapshot());
                client.setVmStatus(status.getInstanceId(), status);
            } catch (Exception e) {
                LOG.error("Error sending status to controller: " + e.toString(), e);
            }
        }
    }

}

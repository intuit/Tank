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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.intuit.tank.CloudServiceClient;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.TPSInfo;
import com.intuit.tank.api.model.v1.cloud.TPSInfoContainer;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.persistence.databases.DataBaseFactory;
import com.intuit.tank.persistence.databases.DatabaseKeys;
import com.intuit.tank.reporting.databases.Attribute;
import com.intuit.tank.reporting.databases.IDatabase;
import com.intuit.tank.reporting.databases.Item;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;
import com.intuit.tank.vm.common.util.ReportUtil;

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
    private String tpsTableName;

    public APIMonitor(CloudVmStatus vmStatus) {
        status = vmStatus;
        try {
            client = new CloudServiceClient(APITestHarness.getInstance().getTankConfig().getControllerBase());
            reportInterval = Math.max(APITestHarness.getInstance().getTankConfig().getAgentConfig()
                    .getStatusReportIntervalMilis(reportInterval), MIN_REPORT_TIME);
            tpsTableName = APITestHarness.getInstance().getTankConfig().getInstanceName() + "_tps";
            DataBaseFactory.getDatabase().createTable(tpsTableName);
        } catch (Exception e) {
            e.printStackTrace();
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
                }
                sendTps(tpsInfo);
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
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    IDatabase db = DataBaseFactory.getDatabase();
                    List<Item> items = new ArrayList<Item>();
                    for (TPSInfo info : tpsInfo.getTpsInfos()) {
                        Item item = createItem(info);
                        items.add(item);
                    }
                    LOG.info("Sending " + items.size() + " to TPS Table " + tpsTableName);
                    db.addItems(tpsTableName, items, false);
                } catch (Exception e) {
                    LOG.error("Error storing TPS: " + e, e);
                }
            }
        });
        t.setDaemon(true);
        // t.setPriority(Thread.NORM_PRIORITY - 2);
        t.start();

    }

    private Item createItem(TPSInfo info) {
        Item item = new Item();
        List<Attribute> attributes = new ArrayList<Attribute>();
        String ts = ReportUtil.getTimestamp(info.getTimestamp());
        addAttribute(attributes, DatabaseKeys.TIMESTAMP_KEY.getShortKey(), ts);
        addAttribute(attributes, DatabaseKeys.JOB_ID_KEY.getShortKey(), APITestHarness.getInstance().getAgentRunData()
                .getJobId());
        addAttribute(attributes, DatabaseKeys.INSTANCE_ID_KEY.getShortKey(), APITestHarness.getInstance()
                .getInstanceId());
        addAttribute(attributes, DatabaseKeys.LOGGING_KEY_KEY.getShortKey(), info.getKey());
        addAttribute(attributes, DatabaseKeys.PERIOD_KEY.getShortKey(), Integer.toString(info.getPeriodInSeconds()));
        addAttribute(attributes, DatabaseKeys.TRANSACTIONS_KEY.getShortKey(), Integer.toString(info.getTransactions()));
        item.setAttributes(attributes);
        String name = APITestHarness.getInstance().getInstanceId()
                + "_" + APITestHarness.getInstance().getAgentRunData().getJobId()
                + "_" + info.getKey()
                + "_" + ts;
        item.setName(name);
        return item;
    }

    public static void addAttribute(List<Attribute> attributes, String key, String value) {
        if (value == null) {
            value = "";
        }
        attributes.add(new Attribute(key, value));
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
                LOG.error("Error sending status to controller: " + e.toString());
            }
        }
    }

}

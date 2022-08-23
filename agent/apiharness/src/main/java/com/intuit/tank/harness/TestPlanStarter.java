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

import com.google.common.collect.ImmutableMap;
import com.intuit.tank.runner.TestPlanRunner;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import org.apache.logging.log4j.message.ObjectMessage;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestPlanStarter implements Runnable {

    private static final Logger LOG = LogManager.getLogger(TestPlanStarter.class);

    private final CloudWatchAsyncClient cloudWatchClient;
    private static final String namespace = "Intuit/Tank";
    private final Dimension instanceId;
    private final Dimension jobId;
    private Date send = new Date();
    private static final int interval = 15; // SECONDS

    private final Object httpClient;
    private final HDTestPlan plan;
    private final int numThreads;
    private final String tankHttpClientClass;
    private final ThreadGroup threadGroup;
    private final AgentRunData agentRunData;
    private int threadsStarted = 0;
    private final long rampDelay;

    private boolean done = false;

    public TestPlanStarter(Object httpClient, HDTestPlan plan, int numThreads, String tankHttpClientClass, ThreadGroup threadGroup, AgentRunData agentRunData) {
        super();
        this.httpClient = httpClient;
        this.plan = plan;
        this.tankHttpClientClass = tankHttpClientClass;
        this.numThreads = (int) Math.max(1, Math.floor(numThreads * (plan.getUserPercentage() / 100D)));
        this.threadGroup = threadGroup;
        this.agentRunData = agentRunData;
        this.rampDelay = calcRampTime();
        this.cloudWatchClient = CloudWatchAsyncClient.builder().build();
        this.instanceId = Dimension.builder()
                .name("InstanceId")
                .value(AmazonUtil.getInstanceId())
                .build();

        this.jobId = Dimension.builder()
                .name("JobId")
                .value(AmazonUtil.getJobId())
                .build();
    }

    public void run() {
        // start initial users
        int numInitialUsers = agentRunData.getNumStartUsers();
        if (threadsStarted < numInitialUsers && threadsStarted < numThreads) {
            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Starting initial " + numInitialUsers + " users for plan "
                    + plan.getTestPlanName() + "...")));
            while (threadsStarted < numInitialUsers && threadsStarted < numThreads) {
                createThread(httpClient, threadsStarted);
            }
        }

        // start rest of users sleeping between each interval
        LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Starting ramp of additional " + (numThreads - threadsStarted)
                + " users for plan " + plan.getTestPlanName() + "...")));
        while (!done) {
            if ((threadsStarted - numInitialUsers) % agentRunData.getUserInterval() == 0) {
                try {
                    Thread.sleep(rampDelay);
                } catch (InterruptedException e) {
                    LOG.error(LogUtil.getLogMessage("Error trying to wait for ramp", LogEventType.System), e);
                }
            }
            // Loop while in pause or pause_ramp state
            while (APITestHarness.getInstance().getCmd() == AgentCommand.pause_ramp
            		|| APITestHarness.getInstance().getCmd() == AgentCommand.pause) {
                if (APITestHarness.getInstance().hasMetSimulationTime()) {
                    APITestHarness.getInstance().setCommand(AgentCommand.stop);
                    break;
                } else {
                    try {
                        Thread.sleep(APITestHarness.POLL_INTERVAL);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
            if ( APITestHarness.getInstance().getCmd() == AgentCommand.stop
            		|| APITestHarness.getInstance().getCmd() == AgentCommand.kill
                    || APITestHarness.getInstance().hasMetSimulationTime()
                    || APITestHarness.getInstance().isDebug()
                    || (agentRunData.getSimulationTimeMillis() == 0 //Run Until: Loops Completed
                        && System.currentTimeMillis() - APITestHarness.getInstance().getStartTime() > agentRunData.getRampTimeMillis())) {
                done = true;
                break;
            }

            if (this.threadsStarted < numThreads) {
                createThread(httpClient, threadsStarted);
            }

            if (send.before(new Date())) { // Send thread metrics every <interval> seconds
                Instant timestamp = new Date().toInstant();
                List<MetricDatum> datumList = new ArrayList<>();
                datumList.add(MetricDatum.builder()
                        .metricName("currentThreads")
                        .unit(StandardUnit.COUNT)
                        .value((double) this.threadsStarted)
                        .timestamp(timestamp)
                        .dimensions(instanceId, jobId)
                        .build());
                datumList.add(MetricDatum.builder()
                        .metricName("targetThreads")
                        .unit(StandardUnit.COUNT)
                        .value((double) numThreads)
                        .timestamp(timestamp)
                        .dimensions(instanceId, jobId)
                        .build());
                PutMetricDataRequest request = PutMetricDataRequest.builder()
                        .namespace(namespace)
                        .metricData(datumList)
                        .build();

                cloudWatchClient.putMetricData(request);
                send = DateUtils.addSeconds(new Date(), interval);
            }
        }
        done = true;
    }

    public int getThreadsStarted() {
        return threadsStarted;
    }

    public HDTestPlan getPlan() {
        return plan;
    }

    public int getNumThreads() {
        return numThreads;
    }

    public boolean isDone() {
        return done;
    }

    private long calcRampTime() {
        int ramp = (numThreads - agentRunData.getNumStartUsers());
        if (ramp > 0) {
            return (agentRunData.getRampTimeMillis() *
                    agentRunData.getUserInterval())
                    / ramp;
        } else if (agentRunData.getRampTimeMillis() > 0) {
            LOG.info(LogUtil.getLogMessage("No Ramp - " + rampDelay, LogEventType.System));
        }
        return 1; //Return minimum wait time 1 millisecond
    }

    private void createThread(Object httpClient, int threadNumber) {
        TestPlanRunner session = new TestPlanRunner(httpClient, plan, threadNumber, tankHttpClientClass);
        Thread thread = new Thread(threadGroup, session, "AGENT");
        thread.setDaemon(true);// system won't shut down normally until all user threads stop
        session.setUniqueName(threadGroup.getName() + "-" + thread.getId());
        thread.start();
        APITestHarness.getInstance().threadStarted(thread);
        threadsStarted++;
    }
}

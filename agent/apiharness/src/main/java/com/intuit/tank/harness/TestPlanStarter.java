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
import com.intuit.tank.harness.data.*;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.message.ObjectMessage;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.cloudwatch.model.Dimension;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TestPlanStarter implements Runnable {

    private static final Logger LOG = LogManager.getLogger(TestPlanStarter.class);

    private CloudWatchAsyncClient cloudWatchClient;
    private static final String namespace = "Intuit/Tank";
    private Dimension testPlan;
    private Dimension instanceId;
    private Dimension jobId;
    private boolean standalone;
    private Date send = new Date();
    private static final int interval = 30; // SECONDS

    private final Object httpClient;
    private final HDTestPlan plan;
    private final int numThreads;
    private final String tankHttpClientClass;
    private final ThreadGroup threadGroup;
    private final AgentRunData agentRunData;
    private int threadsStarted = 0;
    private long rampDelay;
    private int currentRampRate;
    private int sessionStarts = 0;
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
        this.standalone = ((this.numThreads == 1) && (this.agentRunData.getIncrementStrategy().equals(IncrementStrategy.increasing)));
        if (!this.standalone) {
            this.cloudWatchClient = CloudWatchAsyncClient.builder().build();
            this.testPlan = Dimension.builder()
                    .name("testPlan")
                    .value(plan.getTestPlanName())
                    .build();
            this.instanceId = Dimension.builder()
                    .name("InstanceId")
                    .value(AmazonUtil.getInstanceId())
                    .build();
            this.jobId = Dimension.builder()
                    .name("JobId")
                    .value(AmazonUtil.getJobId())
                    .build();
        }
    }

    public void run() {

        HostInfo hostInfo = new HostInfo();
        ThreadContext.put("jobId", AmazonUtil.getJobId());
        ThreadContext.put("projectName", AmazonUtil.getProjectName());
        ThreadContext.put("instanceId", AmazonUtil.getInstanceId());
        ThreadContext.put("publicIp", hostInfo.getPublicIp());
        ThreadContext.put("location", AmazonUtil.getZone());
        ThreadContext.put("httpHost", AmazonUtil.getControllerBaseUrl());
        ThreadContext.put("loggingProfile", AmazonUtil.getLoggingProfile().getDisplayName());
        ThreadContext.put("workloadType", agentRunData.getIncrementStrategy().getDisplay());
        ThreadContext.put("order", Integer.toString(agentRunData.getAgentInstanceNum()));
        ThreadContext.put("numTotalAgents", Integer.toString(agentRunData.getTotalAgents()));
        ThreadContext.put("numTotalUsers", Integer.toString(numThreads));
        ThreadContext.put("targetRampRate", Double.toString(agentRunData.getTargetRampRate()));

        IncrementStrategy workloadType = agentRunData.getIncrementStrategy();
        if(workloadType.equals(IncrementStrategy.increasing)) { // Linear Workload
            try {
                // start initial users
                int numInitialUsers = agentRunData.getNumStartUsers();
                if (threadsStarted < numInitialUsers && threadsStarted < numThreads) {
                    LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Linear - Starting initial " + numInitialUsers + " users for plan "
                            + plan.getTestPlanName() + "...")));
                    while (threadsStarted < numInitialUsers && threadsStarted < numThreads) {
                        createThread(httpClient, threadsStarted);
                    }
                }

                // start rest of users sleeping between each interval
                LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Linear - Starting ramp of additional " + (numThreads - threadsStarted)
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
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                    if (APITestHarness.getInstance().getCmd() == AgentCommand.stop
                            || APITestHarness.getInstance().getCmd() == AgentCommand.kill
                            || APITestHarness.getInstance().hasMetSimulationTime()
                            || APITestHarness.getInstance().isDebug()
                            || (agentRunData.getSimulationTimeMillis() == 0 //Run Until: Loops Completed
                            && System.currentTimeMillis() - APITestHarness.getInstance().getStartTime() > agentRunData.getRampTimeMillis())) {
                        done = true;
                        break;
                    }

                    long activeCount = numThreads; //default
                    try {
                        Thread[] list = new Thread[this.threadGroup.activeCount()];
                        this.threadGroup.enumerate(list);
                        activeCount = Arrays.stream(list)
                                .filter(Objects::nonNull)
                                .filter(Thread::isAlive)
                                .filter(thread -> thread.getName() != null && thread.getName().equals("AGENT"))
                                .count();
                    } catch (SecurityException se) {
                        LOG.error(LogUtil.getLogMessage("Failure to count threads:"), se);
                    }

                    if (threadsStarted < numThreads || activeCount < numThreads) {
                        createThread(httpClient, this.threadsStarted);
                    }

                    if (!this.standalone && send.before(new Date())) { // Send thread metrics every <interval> seconds
                        Instant timestamp = new Date().toInstant();
                        List<MetricDatum> datumList = new ArrayList<>();
                        datumList.add(MetricDatum.builder()
                                .metricName("startedThreads")
                                .unit(StandardUnit.COUNT)
                                .value((double) this.threadsStarted)
                                .timestamp(timestamp)
                                .dimensions(testPlan, instanceId, jobId)
                                .build());
                        datumList.add(MetricDatum.builder()
                                .metricName("activeThreads")
                                .unit(StandardUnit.COUNT)
                                .value((double) activeCount)
                                .timestamp(timestamp)
                                .dimensions(testPlan, instanceId, jobId)
                                .build());
                        datumList.add(MetricDatum.builder()
                                .metricName("targetThreads")
                                .unit(StandardUnit.COUNT)
                                .value((double) numThreads)
                                .timestamp(timestamp)
                                .dimensions(testPlan, instanceId, jobId)
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
            } catch (final Throwable t) {
                LOG.error(LogUtil.getLogMessage("Linear - TestPlanStarter Unknown Error:"), t);
                throwUnchecked(t);
            }
        } else { // Nonlinear Workload
            try {
                // start initial users
                int numInitialUsers = agentRunData.getNumStartUsers();
                if (threadsStarted < numInitialUsers) {
                    LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Nonlinear - Starting initial " + numInitialUsers + " users for plan "
                            + plan.getTestPlanName() + "...")));
                    while (threadsStarted < numInitialUsers) {
                        createThread(httpClient, threadsStarted);
                    }
                }

                intialDelay(); // initial delay
                currentRampRate++; // after delay, start ramp rate at 1 user/sec

                long lastRampRateAddition = (long) (System.currentTimeMillis() - (agentRunData.getBaseDelay() * 1000));
                double agentTimer = ((System.currentTimeMillis() - lastRampRateAddition)) / 1000.0; // start agent timer at base delay
                long lastRampIncreaseTime = System.currentTimeMillis();
                long rampRateDelayMillis = (long) (agentRunData.getRampRateDelay() * 1000);

                long totalPauseTime = 0;
                long pauseStartTime = 0;

                double previousTotalUsers = calculateTotalUsers(agentTimer);
                double accumulatedUsers = 0.0;
                double logProgress = agentTimer; // log offset every 10 seconds from start
                double additionalUsers = 0.0;

                // start rest of users sleeping between each interval
                while (!done) {

                    // add additional fractional users to the total users during ramp time
                    double currentUsers = calculateTotalUsers(agentTimer);
                    double expectedUsersToAdd = currentUsers - previousTotalUsers;
                    previousTotalUsers = currentUsers;

                    if(expectedUsersToAdd >= 1 && expectedUsersToAdd < 2) {
                        accumulatedUsers += (expectedUsersToAdd - 1);
                        if (accumulatedUsers >= 1) {
                            createThread(httpClient, this.threadsStarted);
                            additionalUsers++;
                            accumulatedUsers -= 1;
                        }
                    }

                    while (APITestHarness.getInstance().getCmd() == AgentCommand.pause_ramp
                            || APITestHarness.getInstance().getCmd() == AgentCommand.pause) {
                        if(pauseStartTime == 0) {
                            pauseStartTime = System.currentTimeMillis(); // start pause timer
                        }
                        if (APITestHarness.getInstance().hasMetSimulationTime()) {
                            APITestHarness.getInstance().setCommand(AgentCommand.stop);
                            break;
                        } else {
                            try {
                                Thread.sleep(10000); // 10 second pause intervals
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }

                    if(pauseStartTime != 0) {
                        totalPauseTime += System.currentTimeMillis() - pauseStartTime; // add pause duration to total pause duration
                        pauseStartTime = 0;
                    }

                    if (APITestHarness.getInstance().getCmd() == AgentCommand.stop
                            || APITestHarness.getInstance().getCmd() == AgentCommand.kill
                            || APITestHarness.getInstance().hasMetSimulationTime()
                            || APITestHarness.getInstance().isDebug()) {
                        done = true;
                        break;
                    }

                    long activeCount = 0; //default
                    try {
                        Thread[] list = new Thread[this.threadGroup.activeCount()];
                        this.threadGroup.enumerate(list);
                        activeCount = Arrays.stream(list)
                                .filter(Objects::nonNull)
                                .filter(Thread::isAlive)
                                .filter(thread -> thread.getName() != null && thread.getName().equals("AGENT"))
                                .count();
                    } catch (SecurityException se) {
                        LOG.error(LogUtil.getLogMessage("Failure to count threads:"), se);
                    }

                    long currentLastRampIncreaseTime;
                    if(totalPauseTime > 0) { // if paused, add total pause duration to last ramp increase time for next evaluation
                        currentLastRampIncreaseTime = lastRampIncreaseTime + totalPauseTime;
                        lastRampRateAddition += totalPauseTime;
                        totalPauseTime = 0;
                    } else {
                        currentLastRampIncreaseTime = lastRampIncreaseTime;
                    }

                    // check non-linear current concurrent users against target concurrent users
                    agentTimer = (System.currentTimeMillis() - lastRampRateAddition) / 1000.0; // update agent time
                    double offset = calculateConcurrentUsers(agentTimer) - activeCount;

                    if(agentTimer >= logProgress) { // temporary 30 sec log progress to debug any ramp up issues
                        LOG.info("Nonlinear Ramp Adjustment Check:" + "\n"
                                         + "- Total Additional Fractional Users: " + additionalUsers + "\n"
                                         + "- current concurrent users: " + activeCount + "\n"
                                         + "- target concurrent users: " + calculateConcurrentUsers(agentTimer) + "\n"
                                         + "- current ramp rate: " + currentRampRate + "\n"
                                         + "- offset: " + offset);
                        logProgress += 30;
                    }

                    long currentTime = System.currentTimeMillis();

                    if (currentTime - currentLastRampIncreaseTime > rampRateDelayMillis) {
                        if(currentRampRate < agentRunData.getTargetRampRate()) {
                            currentRampRate++;
                            lastRampIncreaseTime = currentTime;
                        }
                    }

                    createThread(httpClient, this.threadsStarted);
                    this.sessionStarts++; // track session starts

                    if (!this.standalone && send.before(new Date())) { // Send thread metrics every <interval> seconds
                        Instant timestamp = new Date().toInstant();
                        List<MetricDatum> datumList = new ArrayList<>();
                        datumList.add(MetricDatum.builder()
                                .metricName("startedThreads")
                                .unit(StandardUnit.COUNT)
                                .value((double) this.threadsStarted)
                                .timestamp(timestamp)
                                .dimensions(testPlan, instanceId, jobId)
                                .build());
                        datumList.add(MetricDatum.builder()
                                .metricName("activeThreads")
                                .unit(StandardUnit.COUNT)
                                .value((double) activeCount)
                                .timestamp(timestamp)
                                .dimensions(testPlan, instanceId, jobId)
                                .build());
                        datumList.add(MetricDatum.builder()
                                .metricName("userRampRate")
                                .unit(StandardUnit.COUNT)
                                .value((double) this.currentRampRate)
                                .timestamp(timestamp)
                                .dimensions(testPlan, instanceId, jobId)
                                .build());
                        datumList.add(MetricDatum.builder()
                                .metricName("sessionStarts")
                                .unit(StandardUnit.COUNT)
                                .value((double) this.sessionStarts)
                                .timestamp(timestamp)
                                .dimensions(testPlan, instanceId, jobId)
                                .build());
                        PutMetricDataRequest request = PutMetricDataRequest.builder()
                                .namespace(namespace)
                                .metricData(datumList)
                                .build();

                        cloudWatchClient.putMetricData(request);
                        send = DateUtils.addSeconds(new Date(), interval / 2); // 15 SECONDS
                        this.sessionStarts = 0; // reset session starts for next interval
                    }
                    try {
                        Thread.sleep(1000 / currentRampRate); // sleep between adding users proportional to current ramp rate
                    } catch (InterruptedException e) {
                        LOG.error(LogUtil.getLogMessage("Error trying to wait for ramp", LogEventType.System), e);
                    }
                }
                done = true;
            } catch (final Throwable t) {
                LOG.error(LogUtil.getLogMessage("Nonlinear - TestPlanStarter Unknown Error:"), t);
                throwUnchecked(t);
            }
        }
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

    private void intialDelay() {
        try {
            long delay = (long) (agentRunData.getInitialDelay() * 1000); // initial delay
            long baseDelay = (long) (agentRunData.getBaseDelay() * 1000); // base delay
            long startTime = System.currentTimeMillis();
            long totalPauseDuration = 0; // keep track of pause duration
            double previousTotalUsers = 0.0;
            double accumulatedUsers = 0.0;
            long initialRampTimeElapsed = 0;
            long initialRampTimeInterval = 0;

            while (System.currentTimeMillis() - startTime < (delay + totalPauseDuration)) {
                try {
                    if (APITestHarness.getInstance().getCmd() == AgentCommand.pause_ramp
                            || APITestHarness.getInstance().getCmd() == AgentCommand.pause) {
                        if (APITestHarness.getInstance().hasMetSimulationTime()) {
                            APITestHarness.getInstance().setCommand(AgentCommand.stop);
                            return;
                        } else {
                            throw new InterruptedException();
                        }
                    }
                    // each agent ramp 0 to 1 user/sec by adding fractional users to the total users over the initial ramp
                    if((delay - baseDelay) <= initialRampTimeElapsed && initialRampTimeElapsed <= delay) { // if in initial ramp time (delay - baseDelay) to delay
                        double currentUsers = calculateTotalUsers((double) initialRampTimeInterval / 1000);
                        double expectedUsersToAdd = currentUsers - previousTotalUsers;
                        previousTotalUsers = currentUsers;
                        accumulatedUsers += expectedUsersToAdd;
                        if (accumulatedUsers >= 1) {
                            int wholeNumberUsers = (int) accumulatedUsers;
                            for (int i = 0; i < wholeNumberUsers; i++) {
                                createThread(httpClient, this.threadsStarted);
                            }
                            accumulatedUsers -= wholeNumberUsers;
                        }
                        initialRampTimeInterval += 500;
                    }
                    initialRampTimeElapsed += 500;
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    long pauseStartTime = System.currentTimeMillis();
                    while (APITestHarness.getInstance().getCmd() == AgentCommand.pause_ramp
                            || APITestHarness.getInstance().getCmd() == AgentCommand.pause) {
                        if (APITestHarness.getInstance().hasMetSimulationTime()) {
                            APITestHarness.getInstance().setCommand(AgentCommand.stop);
                            return;
                        } else {
                            try {
                                Thread.sleep(1000);
                                startTime += 1000; // update startTime to account for pause - "freeze" timer
                            } catch (InterruptedException ignored) {
                            }
                        }
                    }
                    totalPauseDuration += System.currentTimeMillis() - pauseStartTime; // add pause duration to total pause duration
                }
            }
        } catch (Exception e) {
            LOG.info("Nonlinear - Initial Delay Error: " + e);
        }
    }

    private long calcRampTime() {
        int ramp = (numThreads - agentRunData.getNumStartUsers());
        if (ramp > 0) {
            return (agentRunData.getRampTimeMillis() *
                    agentRunData.getUserInterval())
                    / ramp;
        } else if (agentRunData.getRampTimeMillis() > 0) {
            LOG.info(LogUtil.getLogMessage("Linear - No Ramp - " + rampDelay, LogEventType.System));
        }
        return 1; //Return minimum wait time 1 millisecond
    }

    private double calculateTotalUsers(double t) {
        double d = agentRunData.getRampTimeMillis() / 1000.0;
        if(t < d){
            return ((agentRunData.getTargetRampRate()) / (2 * d)) * Math.pow(t, 2);
        } else {
            double rampUpUsers = ((agentRunData.getTargetRampRate()) / (2 * d)) * Math.pow(d, 2);
            return rampUpUsers + (agentRunData.getTargetRampRate() * (t - d));
        }
    }

    private double calculateConcurrentUsers(double t) {
        double d = agentRunData.getRampTimeMillis() / 1000.0; // offset check only works if ramp duration = user duration
        double totalUsersNow = calculateTotalUsers(t);
        double totalUsersBefore = t - d >= 0 ? calculateTotalUsers(t - d) : 0;
        return totalUsersNow - totalUsersBefore;
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

    private static <E extends RuntimeException> void throwUnchecked(Throwable t) {
        throw (E) t;
    }
}

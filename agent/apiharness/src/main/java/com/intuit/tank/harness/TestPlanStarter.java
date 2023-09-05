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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private int u;

    private int currentRampRate;

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
        this.standalone = (this.numThreads == 1);
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
        IncrementStrategy workloadType = agentRunData.getIncrementStrategy();
        if(workloadType.equals(IncrementStrategy.increasing)) {
            try {
                long prevRampDelay = 0;
                int userCount = 0;

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
                        // LOCAL DEBUGGING
//                        System.out.println("Adding 1 user to " + plan.getTestPlanName());
//                        System.out.println("Active Threads: " + activeCount);
//                        System.out.println("RampDelay: " + rampDelay);
//                        prevRampDelay += rampDelay;
//                        userCount += 1;
//                        if(prevRampDelay > 1000) {
//                            System.out.println("Ramp Rate (users/sec): " + userCount);
//                            prevRampDelay = 0;
//                            userCount = 0;
//                        }
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
        } else {
            // FOR DEBUGGING THINK TIME SCRIPTS
            List<HDScriptGroup> groups = plan.getGroup();
            HDScriptGroup group = groups.get(0);
            List<HDScript> steps = group.getGroupSteps();
            HDScript step = steps.get(0);
            List<HDScriptUseCase> useCaseList = step.getUseCase();
            HDScriptUseCase useCase = useCaseList.get(0);
            List<TestStep> testSteps = useCase.getScriptSteps();
            TestStep testStep = testSteps.get(0);
            Matcher matcher = Pattern.compile("\\d+").matcher(testStep.getInfo());
            matcher.find();
            int seconds = Integer.parseInt(matcher.group());
            u = (seconds / 1000);
            // FOR DEBUGGING THINK TIME SCRIPTS
            LOG.info("Nonlinear Ramp\n" +
                    "Initial Delay (seconds): " + agentRunData.getIntialDelay() + "\n"
                    + "Ramp Rate Delay (seconds): " + agentRunData.getRampRateDelay() + "\n"
                    + "Target Ramp Rate (users/sec): " + agentRunData.getTargetRampRate() + "\n"
                    + "User Duration (seconds): " + u + "\n"
                    + "Ramp Duration (seconds): " + agentRunData.getRampTimeMillis() / 1000.0 + "\n"
                    + "Simulation Duration (seconds): " + agentRunData.getSimulationTimeMillis() / 1000.0 + "\n"
                    + "---------------------");

            try {
                long startTime = System.currentTimeMillis();
                Thread.sleep((long) (agentRunData.getIntialDelay() * 1000));
                currentRampRate++; // after delay, start ramp rate at 1 user/sec
                // TODO: start initial users
//                int numInitialUsers = agentRunData.getNumStartUsers();
//                if (threadsStarted < numInitialUsers) {
//                    LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Nonlinear - Starting initial " + numInitialUsers + " users for plan "
//                            + plan.getTestPlanName() + "...")));
//                    while (threadsStarted < numInitialUsers) {
//                        createThread(httpClient, threadsStarted);
//                    }
//                }
                long lastRampIncreaseTime = System.currentTimeMillis();
                long lastRampRateAddition = System.currentTimeMillis();
                long rampRateDelayMillis = (long) (agentRunData.getRampRateDelay() * 1000);

                // start rest of users sleeping between each interval
                while (!done) {
                    // TODO: Logic for pause or pause_ramp state
//                    while (APITestHarness.getInstance().getCmd() == AgentCommand.pause_ramp
//                            || APITestHarness.getInstance().getCmd() == AgentCommand.pause) {
//                        if (APITestHarness.getInstance().hasMetSimulationTime()) {
//                            APITestHarness.getInstance().setCommand(AgentCommand.stop);
//                            break;
//                        } else {
//                            try {
//                                Thread.sleep(APITestHarness.POLL_INTERVAL);
//                            } catch (InterruptedException ignored) {
//                            }
//                        }
//                    }
                    if (APITestHarness.getInstance().getCmd() == AgentCommand.stop
                            || APITestHarness.getInstance().getCmd() == AgentCommand.kill
                            || APITestHarness.getInstance().hasMetSimulationTime()
                            || APITestHarness.getInstance().isDebug()
                            || (agentRunData.getSimulationTimeMillis() == 0 //Run Until: Loops Completed
                            && System.currentTimeMillis() - APITestHarness.getInstance().getStartTime() > agentRunData.getRampTimeMillis())) {
                        done = true;
                        break;
                    }

                     // TODO: local debugging
//                    if (APITestHarness.getInstance().hasMetSimulationTime()
//                            || (agentRunData.getSimulationTimeMillis() == 0 //Run Until: Loops Completed
//                            && System.currentTimeMillis() - APITestHarness.getInstance().getStartTime() > agentRunData.getRampTimeMillis())) {
//                        done = true;
//                        break;
//                    }

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

                    long currentTime = System.currentTimeMillis();

                    if (currentTime - lastRampIncreaseTime > rampRateDelayMillis) {
                        if(currentRampRate < agentRunData.getTargetRampRate()) {
                            long timeInterval = currentTime - lastRampIncreaseTime;
                            currentRampRate++;
                            LOG.info("Ramp Rate (users/sec): " + currentRampRate);
                            LOG.info("Ramp Delay Time Interval: " + timeInterval);
                            lastRampIncreaseTime = currentTime;
                        }
                    }

                    createThread(httpClient, this.threadsStarted);

//                    currentTime = System.currentTimeMillis();
//                    long timeInterval = currentTime - lastRampRateAddition;
//                    lastRampRateAddition = currentTime;
//                    System.out.println("Ramp Rate Addition Time Interval: " + timeInterval);

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
                        PutMetricDataRequest request = PutMetricDataRequest.builder()
                                .namespace(namespace)
                                .metricData(datumList)
                                .build();

                        cloudWatchClient.putMetricData(request);
                        send = DateUtils.addSeconds(new Date(), interval);
                    }

//                    if ((threadsStarted - numInitialUsers) % agentRunData.getUserInterval() == 0) {
                        try {
                            Thread.sleep(1000 / currentRampRate);
                        } catch (InterruptedException e) {
                            LOG.error(LogUtil.getLogMessage("Error trying to wait for ramp", LogEventType.System), e);
                        }
//                    }
                }
                done = true;

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
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

    private long calcRampTime() {
        if(agentRunData.getIncrementStrategy().equals(IncrementStrategy.increasing)) {
            int ramp = (numThreads - agentRunData.getNumStartUsers());
            if (ramp > 0) {
                return (agentRunData.getRampTimeMillis() *
                        agentRunData.getUserInterval())
                        / ramp;
            } else if (agentRunData.getRampTimeMillis() > 0) {
                LOG.info(LogUtil.getLogMessage("No Ramp - " + rampDelay, LogEventType.System));
            }
            return 1; //Return minimum wait time 1 millisecond
        } else {
            // TODO: use ramp rate
//            long timeElapsed = System.currentTimeMillis() - startTime;
//            currentRampRate = (int) (((double) (10) / (agentRunData.getRampTimeMillis() / 1000)) * timeElapsed);
//            return Math.round((1.0 / currentRampRate) * 1000);
            return 1;
        }
    }

    private double calculateTotalUsers(double t) {
        long d = agentRunData.getRampTimeMillis();
        double endRampRate = ((double) agentRunData.getRampTimeMillis() / 1000) / agentRunData.getIntialDelay();
        if(t >= d){
            return d + ((endRampRate) / (2 * d)) * Math.pow(d, 2) + (endRampRate * (t - d));
        }
        return t + ((endRampRate) / (2 * d)) * Math.pow(t, 2);
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

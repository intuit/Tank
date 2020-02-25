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

import com.intuit.tank.runner.TestPlanRunner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;

public class TestPlanStarter implements Runnable {

    private static final Logger LOG = LogManager.getLogger(TestPlanStarter.class);

    private final Object httpClient;
    private final HDTestPlan plan;
    private final int numThreads;
    private final ThreadGroup threadGroup;
    private final String tankHttpClientClass;
    private int threadsStarted = 0;
    private final long rampDelay;

    private boolean done = false;

    public TestPlanStarter(Object httpClient, HDTestPlan plan, int numThreads, String tankHttpClientClass, ThreadGroup threadGroup) {
        super();
        this.httpClient = httpClient;
        this.plan = plan;
        this.threadGroup = threadGroup;
        this.tankHttpClientClass = tankHttpClientClass;
        this.numThreads = (int) Math.max(1, Math.floor(numThreads * (plan.getUserPercentage() / 100D)));
        this.rampDelay = calcRampTime();
    }

    public void run() {
        // start initial users
        int numInitialUsers = APITestHarness.getInstance().getAgentRunData().getNumStartUsers();
        if (threadsStarted < numInitialUsers && threadsStarted < numThreads) {
            LOG.info(LogUtil.getLogMessage("Starting initial " + numInitialUsers + " users for plan "
                    + plan.getTestPlanName() + "..."));
            while (threadsStarted < numInitialUsers && threadsStarted < numThreads) {
                Thread thread = createThread(httpClient, threadsStarted);
                APITestHarness.getInstance().threadStarted(thread);
                thread.start();
                threadsStarted++;
            }
        }

        // start rest of users sleeping between each interval
        LOG.info(LogUtil.getLogMessage("Starting ramp of additional " + (numThreads - threadsStarted)
                + " users for plan " + plan.getTestPlanName() + "..."));
        while (!done) {
            if ((threadsStarted - numInitialUsers) % APITestHarness.getInstance().getAgentRunData().getUserInterval() == 0) {
                try {
                    Thread.sleep(rampDelay);
                } catch (InterruptedException e) {
                    LOG.error(LogUtil.getLogMessage("Error trying to wait for ramp", LogEventType.System), e);
                }
            }
            // Loop while in pause or pause_ramp state
            while (APITestHarness.getInstance().getCmd() == WatsAgentCommand.pause_ramp
            		|| APITestHarness.getInstance().getCmd() == WatsAgentCommand.pause) {
                if (APITestHarness.getInstance().hasMetSimulationTime()) {
                    APITestHarness.getInstance().setCommand(WatsAgentCommand.stop);
                    break;
                } else {
                    try {
                        Thread.sleep(APITestHarness.POLL_INTERVAL);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                }
            }
            if ( APITestHarness.getInstance().getCmd() == WatsAgentCommand.stop
            		|| APITestHarness.getInstance().getCmd() == WatsAgentCommand.kill
                    || APITestHarness.getInstance().hasMetSimulationTime()
                    || APITestHarness.getInstance().isDebug() ) {
                done = true;
                break;
            }
            if (threadGroup.activeCount() < numThreads) {
                Thread thread = createThread(httpClient, threadsStarted);
                thread.start();
                APITestHarness.getInstance().threadStarted(thread);
                threadsStarted++;
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
        int ramp = (numThreads - APITestHarness.getInstance().getAgentRunData().getNumStartUsers());
        if (ramp > 0) {
            return (APITestHarness.getInstance().getAgentRunData().getRampTime() *
                    APITestHarness.getInstance().getAgentRunData().getUserInterval())
                    / ramp;
        } else if (APITestHarness.getInstance().getAgentRunData().getRampTime() > 0) {
            LOG.info(LogUtil.getLogMessage("No Ramp - " + rampDelay, LogEventType.System));
        }
        return 1; //Return minimum wait time 1 millisecond
    }

    private Thread createThread(Object httpClient, int threadNumber) {
        TestPlanRunner session = new TestPlanRunner(httpClient, plan, threadNumber, tankHttpClientClass);
        Thread thread = new Thread(threadGroup, session, "AGENT");
        thread.setDaemon(true);// system won't shut down normally until all user threads stop
        session.setUniqueName(threadGroup.getName() + "-" + thread.getId());
        return thread;
    }
}

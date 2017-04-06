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

import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;

public class TestPlanStarter implements Runnable {

    private static final Logger LOG = LogManager.getLogger(TestPlanStarter.class);

    private HDTestPlan plan;
    private int numThreads;
    private Stack<Thread> runners = new Stack<Thread>();
    private int threadsStarted;

    private boolean done;

    public TestPlanStarter(HDTestPlan plan, int numThreads) {
        super();
        this.plan = plan;
        this.numThreads = (int) Math.floor(numThreads * (plan.getUserPercentage() / 100D));
    }

    public void run() {
        // start initial users
        int numStartUsers = APITestHarness.getInstance().getAgentRunData().getNumStartUsers();
        if (threadsStarted < numStartUsers && threadsStarted < numThreads) {
            LOG.info(LogUtil.getLogMessage("Starting initial " + numStartUsers + " users for plan "
                    + plan.getTestPlanName() + "..."));
            while (threadsStarted < numStartUsers && threadsStarted < numThreads && !runners.isEmpty()) {
                runners.pop().start();
                APITestHarness.getInstance().threadStarted();
                threadsStarted++;
            }
        }

        // start rest of users sleeping between each interval
        LOG.info(LogUtil.getLogMessage("Starting ramp of additional " + (numThreads - threadsStarted)
                + " users for plan " + plan.getTestPlanName() + "..."));
        while (threadsStarted < numThreads && !runners.isEmpty()) {
            if ((threadsStarted - numStartUsers) % APITestHarness.getInstance().getAgentRunData().getUserInterval() == 0) {
                waitForRampTime();
            }
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
            if (APITestHarness.getInstance().getCmd() == WatsAgentCommand.stop
            		|| APITestHarness.getInstance().getCmd() == WatsAgentCommand.kill) {
                break;
            }
            runners.pop().start();
            APITestHarness.getInstance().threadStarted();
            threadsStarted++;
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

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public boolean isDone() {
        return done;
    }

    public void addThread(Thread t) {
        runners.add(t);
    }

    private void waitForRampTime() {

        try {
            long rampDelay = 0;
            int ramp = (numThreads - APITestHarness.getInstance().getAgentRunData().getNumStartUsers());
            if (ramp > 0) {
                rampDelay = (APITestHarness.getInstance().getAgentRunData().getRampTime() * APITestHarness
                        .getInstance().getAgentRunData().getUserInterval())
                        / ramp;
            }
            if (rampDelay > 0) {
                Thread.sleep(rampDelay);
            } else if (APITestHarness.getInstance().getAgentRunData().getRampTime() > 0) {
                LOG.info(LogUtil.getLogMessage("No Ramp - " + rampDelay, LogEventType.System));
            }

        } catch (Exception t) {
            LOG.error(LogUtil.getLogMessage("Error trying to wait for ramp", LogEventType.System), t);
        }
    }

}

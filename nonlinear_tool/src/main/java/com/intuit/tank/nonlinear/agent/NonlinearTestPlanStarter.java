package com.intuit.tank.nonlinear.agent;

import com.google.common.collect.ImmutableMap;
import com.intuit.tank.runner.TestPlanRunner;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.AgentRunData;
import com.intuit.tank.harness.APITestHarness;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import org.apache.logging.log4j.message.ObjectMessage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NonlinearTestPlanStarter implements Runnable {

    private boolean standalone;

    private final HDTestPlan plan;
    private final int numThreads;
    private final ThreadGroup threadGroup;
    private final AgentRunData agentRunData;
    private int threadsStarted = 0;
    private long rampDelay = calcRampDelay();

    private boolean done = false;

    private double startRampRate; // starting ramp rate in users/sec

    private double endRampRate; // ending ramp rate in users/sec

    private double k = 1; // steepness of ramp

    private double t = 0; // time interval

    private int actualUsersAdded = 0;

    public NonlinearTestPlanStarter(HDTestPlan plan, int numThreads, ThreadGroup threadGroup, AgentRunData agentRunData,
                                    double startRampRate, double endRampRate, double k) {
        super();
        this.plan = plan;
        this.numThreads = (int) Math.max(1, Math.floor(numThreads * (plan.getUserPercentage() / 100D)));
        this.threadGroup = threadGroup;
        this.agentRunData = agentRunData;
        this.standalone = (this.numThreads == 1);
        this.startRampRate = startRampRate;
        this.endRampRate = endRampRate;
        this.k = k;
    }

    public void run() {
        try {
            double currentTime = System.currentTimeMillis();
            // start initial users
            int numInitialUsers = agentRunData.getNumStartUsers();
            if (threadsStarted < numInitialUsers && threadsStarted < numThreads) {
                while (threadsStarted < numInitialUsers && threadsStarted < numThreads) {
                    createThread();
                }
            }

            // start rest of users sleeping between each interval
            while (!done) {

                try {
                    Thread.sleep(rampDelay);
                } catch (InterruptedException e) {
                    System.out.println(LogUtil.getLogMessage("Error trying to wait for ramp", LogEventType.System) + e.getMessage());
                }

                // Cumulative User Growth Calculation
                double expectedCumulativeUsers = calculateExpectedUsers(t);

                int numUsersToAdd = (int) Math.round(expectedCumulativeUsers - actualUsersAdded);

                // Add the users
                System.out.println("Adding " + numUsersToAdd + " to total concurrent users");
                for (int i = 0; i < numUsersToAdd; i++) {
                    createThread();
                    System.out.println("Adding 1 user - Total Users: " + threadsStarted);
                }

                // Update the time interval
                t += rampDelay;

                // Stop the loop if current time is greater than the simulation time
                if (System.currentTimeMillis() - currentTime > agentRunData.getSimulationTimeMillis()) {
                    done = true;
                }
            }
            done = true;
        } catch (final Throwable t) {
            System.out.println(LogUtil.getLogMessage("TestPlanStarter Unknown Error:") + t.getMessage());
            throwUnchecked(t);
        }
    }

    private double nonlinearRampRate(double t) {
        double ramp = agentRunData.getRampTimeMillis();
        if (t <= ramp) {
            return ((endRampRate - startRampRate) * (1 / (1 + Math.exp(-k * (t - ramp / 2)))) + startRampRate);
        } else {
            return endRampRate;
        }
    }

    private double calculateExpectedUsers(double t){
        double sum = 0;
        double dt = 0.01;
        for (double ti = 0; ti <= t; ti += dt) {
            sum += nonlinearRampRate(ti) * dt;
        }
        return sum;
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

    private long calcRampDelay() {
        return 500;
    }

    private void createThread() {
        threadsStarted++;
    }

    private static <E extends RuntimeException> void throwUnchecked(Throwable t) {
        throw (E) t;
    }
}
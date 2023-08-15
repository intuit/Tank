package com.intuit.tank.nonlinear.agent;

import com.google.common.collect.ImmutableMap;
import com.intuit.tank.harness.*;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.logging.log4j.message.ObjectMessage;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class NonlinearAPITestHarness {

    private static NonlinearAPITestHarness instance;
    private AgentRunData agentRunData;
    private ArrayList<ThreadGroup> threadGroupArray = new ArrayList<ThreadGroup>();

    private int currentNumThreads = 0;

    private TankConfig tankConfig;
    private boolean started = false;
    private long startTime = 0;

    private CountDownLatch doneSignal;


    public static NonlinearAPITestHarness getInstance() {
        if (instance == null) {
            instance = new NonlinearAPITestHarness();
        }
        return instance;
    }

    public static void main(String[] args) throws InterruptedException {
//        getInstance().runConcurrentTestPlans(); -> full simulation
        HDTestPlan plan = new HDTestPlan();
        plan.setTestPlanName("Main");
        plan.setUserPercentage(100);
        NonlinearAPITestHarness.getInstance().setAgentRunData(30000);
        NonlinearAPITestHarness.getInstance().agentRunData.setSimulationTimeMillis(60000);
        NonlinearTestPlanStarter starter = new NonlinearTestPlanStarter(plan, 3000, null, instance.agentRunData, 0, 10, 1.2);
        Thread t = new Thread(starter);
        t.setDaemon(true);
        t.start();
        t.join();
    }

    public void setAgentRunData(long rampTime) {
        AgentRunData agentRunData = new AgentRunData();
        agentRunData.setNumStartUsers(0);
        agentRunData.setUserInterval(1);
        agentRunData.setRampTimeMillis(rampTime);
        this.agentRunData = agentRunData;
    }

    public void runConcurrentTestPlans() {
        if (started) {
            System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Agent already started. Ignoring start command")));
            return;
        }
        String info = " RAMP_TIME=" + agentRunData.getRampTimeMillis() +
                "; agentRunData.getNumUsers()=" + agentRunData.getNumUsers() +
                "; NUM_START_THREADS=" + agentRunData.getNumStartUsers() +
                "; simulationTime=" + agentRunData.getSimulationTimeMillis();
        System.out.println(new ObjectMessage(ImmutableMap.of("Message", "starting test with " + info)));
        started = true;

        doneSignal = new CountDownLatch(agentRunData.getNumUsers());
        try {
            HDWorkload hdWorkload = TestPlanSingleton.getInstance().getTestPlans().get(0);
            List<NonlinearTestPlanStarter> testPlans = new ArrayList<NonlinearTestPlanStarter>();
            for (HDTestPlan plan : hdWorkload.getPlans()) {
                if (plan.getUserPercentage() > 0) {
                    plan.setVariables(hdWorkload.getVariables());
                    ThreadGroup threadGroup = new ThreadGroup("Test Plan Runner Group: " + plan.getTestPlanName());
                    threadGroupArray.add(threadGroup);
                    NonlinearTestPlanStarter starter = new NonlinearTestPlanStarter(plan, agentRunData.getNumUsers(), threadGroup, agentRunData, 0, 10, 1.2);
                    testPlans.add(starter);
                    System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Users for Test Plan " + plan.getTestPlanName() + " at "
                            + plan.getUserPercentage()
                            + "% = " + starter.getNumThreads())));
                }
            }

            System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Have all testPlan runners configured")));

            System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Starting threads...")));

            // start initial users
            startTime = System.currentTimeMillis();
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
            System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Simulation start: " + df.format(new Date(getStartTime())))));
            if (agentRunData.getSimulationTimeMillis() != 0) {
                System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Scheduled Simulation End : "
                        + df.format(new Date(getSimulationEndTimeMillis())))));
               System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Max Simulation End : "
                        + df.format(new Date(getMaxSimulationEndTimeMillis())))));
            } else {
                System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Ends at script loops completed with no Max Simulation Time.")));
            }
            currentNumThreads = 0;
            if (agentRunData.getNumUsers() > 0) {
                for (NonlinearTestPlanStarter starter : testPlans) {
                    Thread t = new Thread(starter);
                    t.setDaemon(true);
                    t.start();

                }
                while (!testPlans.stream().allMatch(NonlinearTestPlanStarter::isDone)) {
                    Thread.sleep(5000);
                }
                // if we broke early, fix our countdown latch
                int numToCount = testPlans.stream().mapToInt(NonlinearTestPlanStarter::getThreadsStarted).sum();
                while (numToCount < agentRunData.getNumUsers()) {
                    doneSignal.countDown();
                    numToCount++;
                }
                // wait for them to finish
                System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Ramp Complete...")));

                doneSignal.await();
            }
        } catch (InterruptedException e) {
            System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Stopped")));
        } catch (Throwable t) {
            System.out.println(new ObjectMessage(ImmutableMap.of("Message", "error executing..." + t)) + t.getMessage());
        } finally {
            System.out.println(new ObjectMessage(ImmutableMap.of("Message", "Test Complete...")));
        }
    }

    public long getStartTime() {
        return startTime;
    }

    public long getMaxSimulationEndTimeMillis() {
        return getSimulationEndTimeMillis() + tankConfig.getAgentConfig().getOverSimulationMaxTime();
    }

    public long getSimulationEndTimeMillis() {
        return getStartTime() + agentRunData.getSimulationTimeMillis();
    }
}

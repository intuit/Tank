package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.test.MockFlowController;
import com.intuit.tank.results.TankResult;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class APITestHarnessTest {

    public APITestHarness instance;

    @BeforeEach
    public void init() {
        instance = APITestHarness.getInstance();
    }

    @AfterEach
    public void cleanup() {
        APITestHarness.destroyCurrentInstance();
    }

    @Test
    public void testAPITestHarnessTestMain() {
        instance.setDebug(true);
        instance.setFlowControllerTemplate(new MockFlowController());
        String tankHttpClientClass = "com.intuit.tank.httpclient4.TankHttpClient4";
        APITestHarness.main(new String[]{});
        APITestHarness.main(new String[]{"-instanceId=12345", "-users=10", "-capacity=100", "-start=5",
                                         "-jobId=2345", "-stopBehavior=Test", "-time=5", "-client=" + tankHttpClientClass});
        assertEquals("12345", instance.getInstanceId());
        assertEquals(10, instance.getAgentRunData().getNumUsers());
        assertEquals(5, instance.getAgentRunData().getNumStartUsers());
        assertEquals("2345", instance.getAgentRunData().getJobId());
        assertEquals(StopBehavior.END_OF_SCRIPT_GROUP, instance.getAgentRunData().getStopBehavior());
        assertEquals(300000L, instance.getAgentRunData().getSimulationTimeMillis());
        assertEquals(tankHttpClientClass, instance.getTankHttpClientClass());
        APITestHarness.main(new String[]{"-local"});
        APITestHarness.main(new String[]{"-tp=FILENOTFOUND"});
    }

    @Test
    public void testAPITestHarnessTestMainDebugAndTrace() {
        instance.setDebugLogger(new MockFlowController());
    }

    @Test
    public void testAPITestHarnessTestWatsAgentStatusResponse(){
        instance.getAgentRunData().setNumUsers(10);
        instance.getAgentRunData().setNumStartUsers(5);
        instance.addKill();
        instance.addAbort();
        instance.addGoto();
        instance.addSkip();
        instance.addSkipGroup();
        instance.addRestart();
        WatsAgentStatusResponse response = instance.getStatus();
        assertEquals(1, response.getKills());
        assertEquals(1, response.getAborts());
        assertEquals(1, response.getGotos());
        assertEquals(1, response.getSkips());
        assertEquals(1, response.getSkipGroups());
        assertEquals(1, response.getRestarts());
    }

    @Test
    public void testAPITestHarnessTestRunConcurrentTestPlans(){
        MockHDWorkload workload = new MockHDWorkload();
        HDTestPlan testPlan = new HDTestPlan();
        testPlan.setUserPercentage(5);
        testPlan.setTestPlanName("testPlan");
        workload.setVariables(new HDTestVariables());
        workload.addPlan(testPlan);
        TestPlanSingleton.getInstance().setTestPlan(workload);
        instance.getAgentRunData().setSimulationTimeMillis(1L);
        instance.setFlowControllerTemplate(new MockFlowController());
        instance.setDebug(true);
        instance.runConcurrentTestPlans();
        instance.checkAgentThreads();
    }

    @Test
    public void testAPITestHarnessTestSetCommand(){
        instance.setCommand(AgentCommand.run);
        instance.setCommand(AgentCommand.pause_ramp);
        instance.setCommand(AgentCommand.resume_ramp);
        instance.setCommand(AgentCommand.pause);
        instance.setCommand(AgentCommand.stop);
    }

    @Test
    public void testAPITestHarnessTestQueueTimingResult(){
        instance.queueTimingResult(new TankResult());
    }

    @Test
    public void testAPITestHarnessTestRemainingHelperFunctions(){
        assertEquals(DefaultFlowController.class, instance.getFlowControllerTemplate().getClass());
        assertEquals(0, instance.getCurrentUsers());
        assertFalse(instance.isStarted());
        instance.setDebug(true);
        assertTrue(instance.isDebug());
        instance.setTankHttpClientClass("testTankHttpClientClass");
        assertEquals("testTankHttpClientClass", instance.getTankHttpClientClass());
    }
}

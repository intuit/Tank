package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.HDTestVariables;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.MockFlowController;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.vm.agent.messages.WatsAgentStatusResponse;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import org.apache.logging.log4j.message.ObjectMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class APITestHarnessTest {

    private APITestHarness instance;

    @BeforeEach
    public void init() {
        instance = APITestHarness.getInstance();
    }

    @AfterEach
    public void cleanup() {
        APITestHarness.destroyCurrentInstance();
    }

    @Test
    public void testSingletonPattern() {
        assertNotNull(instance);

        APITestHarness copyInstance = APITestHarness.getInstance();
        assertEquals(instance, copyInstance);

        APITestHarness.destroyCurrentInstance();

        APITestHarness newInstance = APITestHarness.getInstance();
        assertNotEquals(instance, newInstance);
    }

    @Test
    public void testInitializeFromMainArgs() throws IOException {
        try (MockedStatic<AmazonUtil> amazonUtil = Mockito.mockStatic(AmazonUtil.class)) {
            amazonUtil.when(AmazonUtil::getJobId).thenReturn("123456");
            amazonUtil.when(AmazonUtil::getProjectName).thenReturn("Test Project");
            amazonUtil.when(AmazonUtil::getZone).thenReturn("c");
            amazonUtil.when(AmazonUtil::getControllerBaseUrl).thenReturn("https://localhost");
            amazonUtil.when(AmazonUtil::getLoggingProfile).thenReturn(LoggingProfile.STANDARD);
            amazonUtil.when(AmazonUtil::usingEip).thenReturn(Boolean.FALSE);
            amazonUtil.when(AmazonUtil::getStopBehavior).thenReturn(StopBehavior.END_OF_SCRIPT);
            APITestHarness instance = APITestHarness.getInstance();

            instance.setDebug(true);
            instance.setFlowControllerTemplate(new MockFlowController());

            String tankHttpClientClass = "com.intuit.tank.httpclient4.TankHttpClient4";

            String[] args = {"-instanceId=testInstance", "-logging=Standard", "-users=10", "-capacity=100", "-start=5",
                    "-jobId=2345", "-stopBehavior=Script Group", "-time=5", "-client=" + tankHttpClientClass};

            APITestHarness.main(args);

            assertEquals("testInstance", instance.getInstanceId());
            assertEquals("Standard", instance.getAgentRunData().getActiveProfile().getDisplayName());
            assertEquals(10, instance.getAgentRunData().getNumUsers());
            assertEquals(5, instance.getAgentRunData().getNumStartUsers());
            assertEquals("2345", instance.getAgentRunData().getJobId());
            assertEquals("Script Group", instance.getAgentRunData().getStopBehavior().getDisplay());
            assertEquals(300000L, instance.getAgentRunData().getSimulationTimeMillis());
            assertEquals(tankHttpClientClass, instance.getTankHttpClientClass());
        }
    }

    @Test
    public void testUsageMain() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        try {
            APITestHarness.main(new String[]{});
            String output = baos.toString();
            assertTrue(output.contains("API Test Harness Usage:"));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testAddValidations(){
        APITestHarness instance = APITestHarness.getInstance();
        IntStream.range(0, 7).forEach(i -> instance.addKill());
        IntStream.range(0, 2).forEach(i -> instance.addAbort());
        IntStream.range(0, 6).forEach(i -> instance.addGoto());
        IntStream.range(0, 3).forEach(i -> instance.addSkip());
        IntStream.range(0, 1).forEach(i -> instance.addSkipGroup());
        IntStream.range(0, 9).forEach(i ->  instance.addRestart());
        WatsAgentStatusResponse response = instance.getStatus();

        assertEquals(7, response.getKills());
        assertEquals(2, response.getAborts());
        assertEquals(6, response.getGotos());
        assertEquals(3, response.getSkips());
        assertEquals(1, response.getSkipGroups());
        assertEquals(9, response.getRestarts());
    }

    @Test
    public void testRunConcurrentTestPlans(){
        try (MockedStatic<LogUtil> logUtilMockedStatic = Mockito.mockStatic(LogUtil.class)) {
            logUtilMockedStatic.when(() -> LogUtil.getLogMessage(Mockito.anyString())).thenReturn(new ObjectMessage("test"));
            APITestHarness instance = APITestHarness.getInstance();
            MockHDWorkload workload = new MockHDWorkload();
            HDTestPlan testPlan = new HDTestPlan();
            testPlan.setUserPercentage(5);
            testPlan.setTestPlanName("testPlan");
            workload.setVariables(new HDTestVariables());
            workload.addPlan(testPlan);
            TestPlanSingleton.getInstance().setTestPlan(workload);
            String tankHttpClientClass = "com.intuit.tank.httpclient4.TankHttpClient4";
            instance.getAgentRunData().setSimulationTimeMillis(1L);
            instance.getAgentRunData().setIncrementStrategy(IncrementStrategy.increasing);
            instance.setFlowControllerTemplate(new MockFlowController());
            instance.setDebug(true);

            assertFalse(instance.isStarted());
            assertEquals(0, instance.getCurrentUsers());
            instance.runConcurrentTestPlans();
            instance.threadStarted(new Thread());
            assertEquals(1, instance.getCurrentUsers());
            assertTrue(instance.isStarted());
            assertEquals(tankHttpClientClass, instance.getTankHttpClientClass());
            assertEquals(tankHttpClientClass, instance.getAgentRunData().getTankhttpClientClass());
            assertNotEquals(0, instance.getStartTime());
            assertNotNull(instance.getTPSMonitor());
            assertEquals(new TPSMonitor(30).getTPSInfo().getPeriod(),
                    instance.getTPSMonitor().getTPSInfo().getPeriod());
            instance.threadComplete();
            assertEquals(0, instance.getCurrentUsers());
        }
    }

    @Test
    public void testGetInitialStatus() {
        APITestHarness instance = APITestHarness.getInstance();
        CloudVmStatus status = instance.getInitialStatus();
        assertNotNull(status);
        assertEquals("0", status.getJobId());
        assertEquals("Unknown", status.getJobStatus().name());
        assertEquals("AGENT", status.getRole().name());
        assertEquals("running", status.getVmStatus().name());
        assertEquals(0, status.getTotalUsers());
        assertEquals(0, status.getCurrentUsers());
        assertEquals(0, status.getTotalTps());
        assertNotEquals(0, status.getStartTime().getTime());
    }

    @Test
    public void testDebug() {
        APITestHarness instance = APITestHarness.getInstance();
        instance.setDebug(true);
        assertTrue(instance.isDebug());
    }


    // Nonlinear Workflow

    @Test
    public void testConfigureNonlinearAgentRunData() {
        APITestHarness instance = APITestHarness.getInstance();
        instance.getAgentRunData().setIncrementStrategy(IncrementStrategy.standard);
        MockHDWorkload workload = new MockHDWorkload();
        HDTestPlan testPlan = new HDTestPlan();
        testPlan.setUserPercentage(5);
        testPlan.setTestPlanName("testPlan");
        workload.setVariables(new HDTestVariables());
        workload.addPlan(testPlan);
        TestPlanSingleton.getInstance().setTestPlan(workload);
        instance.setDebug(true);

        MockFlowController controller = new MockFlowController();
        instance.setFlowControllerTemplate(controller);

        instance.getAgentRunData().setTargetRampRate(0.7);
        instance.getAgentRunData().setRampTimeMillis(800);

        instance.runConcurrentTestPlans(); // Run nonlinear workflow

        assertEquals(0.7, instance.getAgentRunData().getTargetRampRate());
    }

}

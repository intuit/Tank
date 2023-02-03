package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.TimerStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.runner.method.TimerMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TraceFlowControllerTest {
    @Test
    public void testTraceFlowController() {
        TraceFlowController flowController = new TraceFlowController();
        TestPlanRunner tpr = new TestPlanRunner(new HDTestPlan(), 3, "testHttpClientClass");
        TestStepContext tsc = new TestStepContext(new TimerStep(), new Variables(),
                "testPlanName", "testUniqueName",
                new TimerMap(), tpr );

        flowController.nextStep(tsc);
        flowController.startStep(tsc);
        flowController.endStep(tsc);

        assertTrue(flowController.shouldExecute(tsc));
        assertNotNull(flowController.cloneController());
    }
}

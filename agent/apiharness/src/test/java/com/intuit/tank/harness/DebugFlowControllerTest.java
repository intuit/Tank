package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.TimerStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.runner.method.TimerMap;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;


import static org.junit.jupiter.api.Assertions.*;

public class DebugFlowControllerTest {
    @Test
    public void testDebugFlowController() {
        DebugFlowController flowController = new DebugFlowController();
        TestPlanRunner tpr = new TestPlanRunner(new HDTestPlan(), 3, "testHttpClientClass");
        TestStepContext tsc = new TestStepContext(new TimerStep(), new Variables(),
                                      "testPlanName", "testUniqueName",
                                                    new TimerMap(), tpr );
        String userInput = "s";
        ByteArrayInputStream bais = new ByteArrayInputStream(userInput.getBytes());
        System.setIn(bais);

        flowController.nextStep(tsc);
        flowController.startStep(tsc);
        flowController.endStep(tsc);

        assertTrue(flowController.shouldExecute(tsc));
        assertNotNull(flowController.cloneController());
    }
}

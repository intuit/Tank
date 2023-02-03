package com.intuit.tank.runner.method;

import com.intuit.tank.harness.data.AuthenticationStep;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.httpclient4.TankHttpClient4;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationRunnerTest {

    @Test
    public void testAuthenticationRunner_1(){
        AuthenticationStep testStep = new AuthenticationStep();
        testStep.setUserName("testUsername");
        Variables variables = new Variables();
        String testPlanName = "testPlan";
        String uniqueName = "testUniqueName";
        TimerMap timerMap = new TimerMap();
        TestPlanRunner parent = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        parent.setHttpClient(null);

        TestStepContext tsc = new TestStepContext(testStep, variables, testPlanName, uniqueName, timerMap, parent);
        tsc.setHttpClient(new TankHttpClient4());
        AuthenticationRunner authenticationRunner = new AuthenticationRunner(tsc);
        assertNotNull(authenticationRunner);
        String result = authenticationRunner.execute();
        assertEquals("PASS", result);
    }
}

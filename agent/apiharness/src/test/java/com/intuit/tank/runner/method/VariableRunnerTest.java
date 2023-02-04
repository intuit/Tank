package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>VariableRunnerTest</code> contains tests for the class <code>{@link VariableRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class VariableRunnerTest {

    @Test
    public void testVariableRunner_1() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext tsc = new TestStepContext(new VariableStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        VariableRunner result = new VariableRunner(tsc);

        assertNotNull(result);
    }

    @Test
    public void testVariableExecute_1() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        VariableStep variableStep = new VariableStep();
        variableStep.setKey("testKey");
        variableStep.setValue("testValue");
        TestStepContext tsc = new TestStepContext(variableStep, new Variables(), "", "", new TimerMap(), testPlanRunner);

        VariableRunner result = new VariableRunner(tsc);

        assertEquals("PASS", result.execute());
    }
}

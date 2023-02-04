package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.ThinkTimeStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>ThinkTimeRunnerTest</code> contains tests for the class <code>{@link ThinkTimeRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class ThinkTimeRunnerTest {

    @Test
    public void testThinkTimeRunner_1() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext tsc = new TestStepContext(new ThinkTimeStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        ThinkTimeRunner result = new ThinkTimeRunner(tsc);

        assertNotNull(result);
    }

    @Test
    public void testExecute_1() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext testStepContext = new TestStepContext(new ThinkTimeStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        ThinkTimeRunner fixture = new ThinkTimeRunner(testStepContext);

        String result = fixture.execute();

        assertNotNull(result);
    }

    @Test
    public void testExecute_2() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);

        Variables variables = new Variables();
        variables.addVariable("Int1", "-1");
        variables.addVariable("Int2", "-2");

        ThinkTimeStep step = new ThinkTimeStep();
        step.setMinTime("@Int1");
        step.setMaxTime("@Int2");

        TestStepContext testStepContext = new TestStepContext(step, variables, "", "", new TimerMap(), testPlanRunner);
        ThinkTimeRunner fixture = new ThinkTimeRunner(testStepContext);

        String result = fixture.execute();

        assertEquals("PASS", result);
    }

    @Test
    public void testExecute_3() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);

        Variables variables = new Variables();
        variables.addVariable("Int1", "2");
        variables.addVariable("Int2", "4");

        ThinkTimeStep step = new ThinkTimeStep();
        step.setMinTime("@Int1");
        step.setMaxTime("@Int2");

        TestStepContext testStepContext = new TestStepContext(step, variables, "", "", new TimerMap(), testPlanRunner);
        ThinkTimeRunner fixture = new ThinkTimeRunner(testStepContext);

        String result = fixture.execute();

        assertEquals("PASS", result);
    }

    @Test
    public void testExecute_Num_Format_Exceptions() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);

        Variables variables = new Variables();
        variables.addVariable("NotInt1", "test");
        variables.addVariable("NotInt2", "test");

        ThinkTimeStep step = new ThinkTimeStep();
        step.setMinTime("@NotInt1");
        step.setMaxTime("@NotInt2");

        TestStepContext testStepContext = new TestStepContext(step, variables, "", "", new TimerMap(), testPlanRunner);
        ThinkTimeRunner fixture = new ThinkTimeRunner(testStepContext);

        String result = fixture.execute();

        assertEquals("PASS", result);
    }
}

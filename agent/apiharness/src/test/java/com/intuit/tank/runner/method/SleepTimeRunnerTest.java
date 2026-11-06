package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.SleepTimeStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestHttpClient;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

/**
 * The class <code>SleepTimeRunnerTest</code> contains tests for the class <code>{@link SleepTimeRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class SleepTimeRunnerTest {
    /**
     * Run the SleepTimeRunner(TestStepContext) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testSleepTimeRunner_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext tsc = new TestStepContext(new SleepTimeStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        SleepTimeRunner result = new SleepTimeRunner(tsc);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        //       at com.intuit.tank.runner.TestPlanRunner.<clinit>(TestPlanRunner.java:44)
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext testStepContext = new TestStepContext(new SleepTimeStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        SleepTimeRunner fixture = new SleepTimeRunner(testStepContext);

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_2()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext testStepContext = new TestStepContext(new SleepTimeStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        SleepTimeRunner fixture = new SleepTimeRunner(testStepContext);

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    /**
     * Run the String execute() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testExecute_3()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext testStepContext = new TestStepContext(new SleepTimeStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        SleepTimeRunner fixture = new SleepTimeRunner(testStepContext);

        String result = fixture.execute();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: Could not initialize class com.intuit.tank.runner.TestPlanRunner
        assertNotNull(result);
    }

    @Test
    public void testExecuteSleepsForVariableValue() {
        Variables variables = new Variables();
        variables.addVariable("sleepTime", "20");
        assertSleepsAtLeast(20, "@sleepTime", variables);
    }

    @Test
    public void testExecuteSleepsForFunctionValue() {
        assertSleepsAtLeast(15, "#function.string.concat.1.5", new Variables());
    }

    @Test
    public void testExecuteSleepsForLiteralValue() {
        assertSleepsAtLeast(10, "10", new Variables());
    }

    @Test
    public void testExecuteWhenInterrupted() {
        SleepTimeRunner fixture = createRunner("60000", new Variables());
        Thread.currentThread().interrupt();
        try {
            long start = System.nanoTime();
            // the interrupt ends the sleep right away and the step still passes
            assertEquals(TankConstants.HTTP_CASE_PASS, fixture.execute());
            assertTrue(TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start) < 60000);
        } finally {
            Thread.interrupted();
        }
    }

    private static void assertSleepsAtLeast(long expectedMillis, String value, Variables variables) {
        SleepTimeRunner fixture = createRunner(value, variables);
        long start = System.nanoTime();
        assertEquals(TankConstants.HTTP_CASE_PASS, fixture.execute());
        long elapsed = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
        assertTrue(elapsed >= expectedMillis, "slept " + elapsed + "ms, expected at least " + expectedMillis + "ms");
    }

    private static SleepTimeRunner createRunner(String value, Variables variables) {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        SleepTimeStep step = new SleepTimeStep();
        step.setValue(value);
        TestStepContext testStepContext = new TestStepContext(step, variables, "", "", new TimerMap(), testPlanRunner);
        return new SleepTimeRunner(testStepContext);
    }
}
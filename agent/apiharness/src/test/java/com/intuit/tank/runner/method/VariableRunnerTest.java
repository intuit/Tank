package com.intuit.tank.runner.method;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.VariableStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestHttpClient;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>VariableRunnerTest</code> contains tests for the class <code>{@link VariableRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class VariableRunnerTest {
    /**
     * Run the VariableRunner(TestStepContext) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 5:53 PM
     */
    @Test
    public void testVariableRunner_1()
        throws Exception {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, new TestHttpClient());
        testPlanRunner.setHttpClient(null);
        TestStepContext tsc = new TestStepContext(new VariableStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        VariableRunner result = new VariableRunner(tsc);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.ExceptionInInitializerError
        //       at org.apache.log4j.LogManager.getLogger(Logger.java:117)
        //       at com.intuit.tank.runner.TestPlanRunner.<clinit>(TestPlanRunner.java:44)
        assertNotNull(result);
    }
}
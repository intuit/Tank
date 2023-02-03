package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.intuit.tank.tools.script.ScriptIOBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>LogicRunnerTest</code> contains tests for the class <code>{@link LogicRunner}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class LogicRunnerTest {

    @BeforeEach
    public void init(){
        ScriptIOBean ioBean = mock(ScriptIOBean.class);
        when(ioBean.getOutput("action")).thenReturn("action");
    }

    @Test
    public void testLogicRunner_1() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext tsc = new TestStepContext(new LogicStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        LogicRunner result = new LogicRunner(tsc);

        assertNotNull(result);
    }

    @Test
    public void testExecute_1() {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
        TestStepContext testStepContext = new TestStepContext(new LogicStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);
        LogicRunner fixture = new LogicRunner(testStepContext);

        String result = fixture.execute();
        assertNotNull(result);
    }
}

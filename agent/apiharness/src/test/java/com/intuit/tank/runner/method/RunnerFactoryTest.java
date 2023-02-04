package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.intuit.tank.harness.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>RunnerFactoryTest</code> contains tests for the class <code>{@link RunnerFactory}</code>.
 *
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class RunnerFactoryTest {

    TestPlanRunner testPlanRunner;

    @BeforeEach
    public void init(){
        testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        testPlanRunner.setHttpClient(null);
    }
    @Test
    public void testGetRunner_1() {
        TestStepContext tsc = new TestStepContext(new ThinkTimeStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(ThinkTimeRunner.class, result.getClass());
    }

    @Test
    public void testGetRunner_2() {
        TestStepContext tsc = new TestStepContext(new AuthenticationStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(AuthenticationRunner.class, result.getClass());
    }

    @Test
    public void testGetRunner_3() {
        TestStepContext tsc = new TestStepContext(new SleepTimeStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(SleepTimeRunner.class, result.getClass());
    }

    @Test
    public void testGetRunner_4() {
        TestStepContext tsc = new TestStepContext(new VariableStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(VariableRunner.class, result.getClass());
    }

    @Test
    public void testGetRunner_5() {
        TestStepContext tsc = new TestStepContext(new TimerStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(TimerRunner.class, result.getClass());
    }

    @Test
    public void testGetRunner_6() {
        TestStepContext tsc = new TestStepContext(new ClearCookiesStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(ClearCookiesRunner.class, result.getClass());
    }

    @Test
    public void testGetRunner_7() {
        TestStepContext tsc = new TestStepContext(new LogicStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(LogicRunner.class, result.getClass());
    }

    @Test
    public void testGetRunner_8() {
        TestStepContext tsc = new TestStepContext(new CookieStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(SetCookieRunner.class, result.getClass());
    }

    @Test
    public void testGetRunner_9() {
        TestStepContext tsc = new TestStepContext(new MockStep(), new Variables(), "", "", new TimerMap(), testPlanRunner);

        Runner result = RunnerFactory.getRunner(tsc);

        assertEquals(MockRunner.class, result.getClass());
    }
}

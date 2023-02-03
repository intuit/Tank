package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.intuit.tank.harness.data.CookieStep;
import com.intuit.tank.runner.TestHttpClient;
import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;

/**
 * The class <code>SetCookieRunnerTest</code> contains tests for the class <code>{@link SetCookieRunner}</code>.
 * 
 * @generatedBy CodePro at 12/16/14 5:53 PM
 */
public class SetCookieRunnerTest {
    @Test
    public void testSetCookieRunner_1() {
        TestStepContext tsc = new TestStepContext(new CookieStep(), new Variables(), "", "", new TimerMap(),
                new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient"));
        tsc.setHttpClient(new TestHttpClient());
        SetCookieRunner result = new SetCookieRunner(tsc);
        assertNotNull(result);
        String actual = result.execute();
        assertEquals("PASS", actual);
    }
}

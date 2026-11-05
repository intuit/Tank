package com.intuit.tank.runner.method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import com.intuit.tank.tools.script.ScriptIOBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.intuit.tank.harness.MockRequest;
import com.intuit.tank.harness.MockResponse;
import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.LogicStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.script.ScriptConstants;
import com.intuit.tank.vm.common.TankConstants;

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

    static Stream<Arguments> actions() {
        return Stream.of(
                Arguments.of(ScriptConstants.ACTION_TERMINATE_USER, TankConstants.HTTP_CASE_KILL),
                Arguments.of(ScriptConstants.ACTION_ABORT_GROUP, TankConstants.HTTP_CASE_SKIP),
                Arguments.of(ScriptConstants.ACTION_ABORT_SCRIPT, TankConstants.HTTP_CASE_ABORT),
                Arguments.of(ScriptConstants.ACTION_ABORT_SCRIPT_GROUP, TankConstants.HTTP_CASE_SKIPGROUP),
                Arguments.of(ScriptConstants.ACTION_RESTART_PLAN, TankConstants.HTTP_CASE_RESTART),
                Arguments.of(ScriptConstants.ACTION_GOTO_PREFIX + "Login", ScriptConstants.ACTION_GOTO_PREFIX + "Login"));
    }

    /**
     * An action set by the script fails the step and sets the step's onFail to the matching flow change.
     */
    @ParameterizedTest
    @MethodSource("actions")
    public void testExecuteAction(String action, String expectedOnFail) {
        LogicStep step = createStep("ioBean.setOutput('action', '" + action + "');");

        assertEquals(TankConstants.HTTP_CASE_FAIL, new LogicRunner(createContext(step, new Variables())).execute());
        assertEquals(expectedOnFail, step.getOnFail());
    }

    @Test
    public void testExecuteUnknownActionPasses() {
        LogicStep step = createStep("ioBean.setOutput('action', 'dance');");
        step.setOnFail(TankConstants.HTTP_CASE_SKIP);

        assertEquals(TankConstants.HTTP_CASE_PASS, new LogicRunner(createContext(step, new Variables())).execute());
        assertEquals(TankConstants.HTTP_CASE_SKIP, step.getOnFail());
    }

    @Test
    public void testExecuteScriptCanSetVariables() {
        Variables variables = new Variables();
        LogicStep step = createStep("ioBean.getInput('variables').addVariable('fromScript', 'set');");

        assertEquals(TankConstants.HTTP_CASE_PASS, new LogicRunner(createContext(step, variables)).execute());
        assertEquals("set", variables.getVariable("fromScript"));
    }

    @Test
    public void testExecutePassesRequestAndResponseToScript() {
        MockResponse response = new MockResponse();
        response.setHttpCode(503);
        LogicStep step = createStep("if (ioBean.getInput('request') != null && ioBean.getInput('response').getHttpCode() == 503) {"
                + " ioBean.setOutput('action', '" + ScriptConstants.ACTION_TERMINATE_USER + "'); }");
        TestStepContext context = createContext(step, new Variables());
        context.setRequest(new MockRequest());
        context.setResponse(response);

        assertEquals(TankConstants.HTTP_CASE_FAIL, new LogicRunner(context).execute());
        assertEquals(TankConstants.HTTP_CASE_KILL, step.getOnFail());
    }

    @Test
    public void testExecuteScriptErrorAbortsScript() {
        LogicStep step = createStep("this is not javascript");

        assertEquals(TankConstants.HTTP_CASE_FAIL, new LogicRunner(createContext(step, new Variables())).execute());
        assertEquals(TankConstants.HTTP_CASE_ABORT, step.getOnFail());
    }

    private static LogicStep createStep(String script) {
        LogicStep step = new LogicStep();
        step.setName("logic");
        step.setScript(script);
        return step;
    }

    private static TestStepContext createContext(LogicStep step, Variables variables) {
        TestPlanRunner testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "com.intuit.tank.runner.TestHttpClient");
        return new TestStepContext(step, variables, "", "", new TimerMap(), testPlanRunner);
    }
}

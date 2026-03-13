package com.intuit.tank.tools.script;

import com.intuit.tank.script.models.ScriptStepTO;
import com.intuit.tank.script.models.ScriptTO;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ResourceUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ScriptRunner using GraalVM JS engine (JSR-223).
 */
class ScriptRunnerTest {

    private ScriptEngine jsEngine() {
        ScriptEngine engine = JsEngineFactory.createJsEngine();
        assertNotNull(engine, "GraalVM JS ScriptEngine must be present on classpath");
        return engine;
    }

    @Test
    void testEngineIsGraalJS() {
        ScriptEngine engine = jsEngine();
        String engineName = engine.getFactory().getEngineName();
        assertTrue(engineName.contains("Graal") || engineName.contains("GraalVM"),
                "Expected GraalVM engine but got: " + engineName);
    }

    @Test
    void testRunSimpleScript() throws ScriptException {
        ScriptRunner runner = new ScriptRunner();
        String script = "ioBean.setOutput('result', 'hello');";
        Map<String, Object> inputs = new HashMap<>();
        StringOutputLogger logger = new StringOutputLogger();

        ScriptIOBean result = runner.runScript("test", script, jsEngine(), inputs, logger);

        assertEquals("hello", result.getOutput("result"));
    }

    @Test
    void testRunComplexScript() throws ScriptException, IOException {
        ScriptRunner runner = new ScriptRunner();
        String script = IOUtils.toString(Objects.requireNonNull(
                ResourceUtils.class.getResourceAsStream("/scriptFilter.js")), StandardCharsets.UTF_8);
        ScriptTO scriptTO = ScriptTO.builder()
                .withName("test-script")
                .withProductName("test-product")
                .withSteps(List.of(ScriptStepTO.builder().build()))
                .build();
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("script", scriptTO);
        StringOutputLogger logger = new StringOutputLogger();

        runner.runScript("test", script, jsEngine(), inputs, logger);

        assertTrue(logger.getOutput().contains("Number of Steps: 1"));
    }

    @Test
    void testInputsAreAvailableInScript() throws ScriptException {
        ScriptRunner runner = new ScriptRunner();
        String script = "ioBean.setOutput('echo', ioBean.getInput('value'));";
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("value", "tank-graalvm");
        StringOutputLogger logger = new StringOutputLogger();

        ScriptIOBean result = runner.runScript("echo-test", script, jsEngine(), inputs, logger);

        assertEquals("tank-graalvm", result.getOutput("echo"));
    }

    @Test
    void testScriptExceptionPropagated() {
        ScriptRunner runner = new ScriptRunner();
        String badScript = "this is not valid javascript }{";
        Map<String, Object> inputs = new HashMap<>();

        assertThrows(ScriptException.class,
                () -> runner.runScript("bad-script", badScript, jsEngine(), inputs, new NullOutputLogger()));
    }

    @Test
    void testRunScriptWithoutName() throws ScriptException {
        ScriptRunner runner = new ScriptRunner();
        String script = "ioBean.setOutput('x', 42);";
        Map<String, Object> inputs = new HashMap<>();

        ScriptIOBean result = runner.runScript(script, jsEngine(), inputs, new NullOutputLogger());

        assertEquals(42, ((Number) result.getOutput("x")).intValue());
    }

    @Test
    void testDebugOutputCaptured() throws ScriptException {
        ScriptRunner runner = new ScriptRunner();
        String script = "ioBean.setOutput('done', true);";
        Map<String, Object> inputs = new HashMap<>();
        StringOutputLogger logger = new StringOutputLogger();

        runner.runScript("debug-test", script, jsEngine(), inputs, logger);

        String output = logger.getOutput();
        assertFalse(output.isEmpty(), "Output logger should have been invoked");
        assertTrue(output.contains("Starting scriptEngine") || output.contains("Finished scriptEngine"));
    }
}

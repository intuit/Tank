package com.intuit.tank.script;

import com.intuit.tank.rest.mvc.rest.services.scripts.ExternalScriptTester.Run;
import com.intuit.tank.script.models.ScriptStepTO;
import com.intuit.tank.script.models.ScriptTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SandboxedExternalScriptTesterTest {

    private static final long TIME_LIMIT_MS = 2_000;
    private final SandboxedExternalScriptTester tester = new SandboxedExternalScriptTester(TIME_LIMIT_MS);

    private static ScriptTO script() {
        List<ScriptStepTO> steps = new ArrayList<>();
        steps.add(ScriptStepTO.builder().withUuid("a").withType("request").withHostname("qa.example.com").build());
        steps.add(ScriptStepTO.builder().withUuid("b").withType("request").withHostname("cdn.example.com").build());
        return ScriptTO.builder().withName("checkout").withSteps(steps).build();
    }

    @Test
    void theScriptCanEditAndAddSteps() {
        ScriptTO script = script();
        Run run = tester.run("filter.js", """
                var script = ioBean.getInput('script');
                // the script's step list can't be changed in place; set a new one
                var steps = new (Java.type('java.util.ArrayList'))(script.getSteps());
                steps.removeIf(function (s) { return s.getHostname() == 'cdn.example.com'; });
                var Step = Java.type('com.intuit.tank.script.models.ScriptStepTO');
                steps.add(Step.builder().withType('sleep').withName('pause').build());
                script.setSteps(steps);
                ioBean.println('steps now ' + steps.size());
                """, script);
        assertFalse(run.timedOut());
        assertTrue(run.output().contains("steps now 2"), run.output());
        assertEquals(List.of("request", "sleep"), script.getSteps().stream().map(ScriptStepTO::getType).toList());
    }

    @Test
    void otherJavaClassesAreBlocked() {
        String output = tester.run("filter.js",
                "var Runtime = Java.type('java.lang.Runtime'); ioBean.println('escaped');", script()).output();
        assertTrue(output.contains("Access to host class java.lang.Runtime is not allowed"), output);
        assertFalse(output.contains("escaped"), output);
    }

    @Test
    void reflectionThroughGivenObjectsIsBlocked() {
        String output = tester.run("filter.js",
                "var c = ioBean.getInput('script').getClass().forName('java.lang.Runtime'); ioBean.println('escaped ' + c);",
                script()).output();
        assertTrue(output.contains("Exception thrown") && output.contains("forName"), "blocked by the host access rules: " + output);
        assertFalse(output.contains("escaped"), output);
    }

    @Test
    void scriptErrorsAreReportedInTheOutput() {
        Run run = tester.run("filter.js", "this is not javascript", script());
        assertFalse(run.timedOut());
        assertTrue(run.output().contains("Exception thrown"), run.output());
    }

    @Test
    void runawayScriptHitsTheTimeLimit() {
        Run run = tester.run("filter.js", "ioBean.println('before'); while (true) {}", script());
        assertTrue(run.timedOut());
        // the wait and the clock may round differently by a few milliseconds
        assertTrue(run.durationMs() >= TIME_LIMIT_MS - 20, "waited " + run.durationMs() + " ms");
        assertTrue(run.output().contains("before"), "partial output is returned: " + run.output());
        assertTrue(run.output().contains("did not finish"), run.output());
    }
}

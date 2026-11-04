/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script;

import com.intuit.tank.rest.mvc.rest.models.LogicTestRequest;
import com.intuit.tank.rest.mvc.rest.models.LogicTestResult;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SandboxedLogicStepTesterTest {

    private final SandboxedLogicStepTester tester = new SandboxedLogicStepTester();

    private static LogicTestRequest request(String script) {
        return new LogicTestRequest(null, script, Map.of("user", "alice"), "{\"a\":1}", Map.of("X-Req", "1"),
                "{\"token\":\"t-123\"}", Map.of("X-Resp", "2"));
    }

    @Test
    void runsTheScriptWithTheTemplateHelpers() {
        LogicTestResult result = tester.test(request(
                "var token = toJsonObj(getResponseBody()).token;\n"
                        + "setVariable('token', token);\n"
                        + "log('user is ' + getVariable('user'));\n"
                        + "setAction('goto');"));
        assertFalse(result.timedOut());
        assertTrue(result.output().contains("user is alice"), result.output());
        assertTrue(result.output().contains("token = t-123"), "variables after the script are listed: " + result.output());
        assertTrue(result.output().contains("action = goto"), "outputs are listed: " + result.output());
    }

    @Test
    void javaIsNotAvailable() {
        String output = tester.test(request("var Runtime = Java.type('java.lang.Runtime'); log('escaped');")).output();
        assertTrue(output.contains("\"Java\" is not defined"), output);
        assertFalse(output.contains("escaped"), output);
    }

    @Test
    void reflectionThroughGivenObjectsIsBlocked() {
        String output = tester.test(request(
                "var c = getRequest().getClass().forName('java.lang.Runtime'); log('escaped ' + c);")).output();
        assertTrue(output.toLowerCase().contains("reflection"), "blocked by the class filter: " + output);
        assertFalse(output.contains("escaped"), output);
    }

    @Test
    void scriptErrorsAreReportedInTheOutput() {
        LogicTestResult result = tester.test(request("this is not javascript"));
        assertFalse(result.timedOut());
        assertTrue(result.output().contains("Exception thrown"), result.output());
    }

    @Test
    void runawayScriptHitsTheTimeLimit() {
        LogicTestResult result = tester.test(request("log('before'); while (true) {}"));
        assertTrue(result.timedOut());
        // the wait and the clock may round differently by a few milliseconds
        assertTrue(result.durationMs() >= SandboxedLogicStepTester.TIME_LIMIT_MS - 20, "waited " + result.durationMs() + " ms");
        assertTrue(result.output().contains("before"), "partial output is returned: " + result.output());
        assertTrue(result.output().contains("did not finish"), result.output());
    }
}

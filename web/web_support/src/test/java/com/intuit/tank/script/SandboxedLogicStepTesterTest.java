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

    private String blocked(String escape) {
        String output = tester.test(request(escape + "; log('escaped');")).output();
        assertTrue(output.contains("Exception thrown"), output);
        assertFalse(output.contains("escaped"), output);
        return output;
    }

    @Test
    void javaIsNotAvailable() {
        String output = blocked("var Runtime = Java.type('java.lang.Runtime')");
        assertTrue(output.contains("Access to host class java.lang.Runtime is not allowed"), output);
    }

    @Test
    void reflectionThroughGivenObjectsIsBlocked() {
        blocked("getRequest().getClass().forName('java.lang.Runtime')");
        blocked("getRequest().getClass().class.forName('java.lang.Runtime')");
        blocked("ioBean.getClass().getClassLoader().loadClass('java.lang.Runtime')");
        blocked("ioBean.getClass().class.getClassLoader().loadClass('java.lang.Runtime')");
    }

    @Test
    void filesCannotBeRead() {
        blocked("load('/etc/hosts')");
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

    @Test
    void runawayScriptIsCancelledAndFreesItsSlot() {
        for (int i = 0; i <= SandboxedLogicStepTester.MAX_RUNNING; i++) {
            assertTrue(tester.test(request("while (true) {}")).timedOut());
        }
        assertFalse(tester.test(request("log('still accepting tests');")).timedOut());
    }
}

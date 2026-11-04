/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script;

import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;
import com.intuit.tank.rest.mvc.rest.models.LogicTestRequest;
import com.intuit.tank.rest.mvc.rest.models.LogicTestResult;
import com.intuit.tank.rest.mvc.rest.services.scripts.LogicStepTester;
import com.intuit.tank.tools.script.ScriptIOBean;
import com.intuit.tank.tools.script.ScriptRunner;
import com.intuit.tank.tools.script.StringOutputLogger;
import com.intuit.tank.vm.common.LogicScriptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs logic step tests for the REST API in a restricted JavaScript engine:
 * <ul>
 *     <li>no Java access: {@code --no-java} removes {@code Java}, {@code Packages} and friends, and a class
 *     filter that admits no class also blocks reflection through the objects the script is given;</li>
 *     <li>a time limit of {@value #TIME_LIMIT_MS} ms, after which the request returns with what was printed
 *     so far;</li>
 *     <li>at most {@value #MAX_RUNNING} tests at once.</li>
 * </ul>
 *
 * <p>A script that is still running at the time limit cannot be stopped on current JVMs; its thread runs on
 * until the script ends and keeps its slot until then. If scripts that never end use up every slot,
 * further tests are refused until the controller restarts.</p>
 *
 * <p>The inputs and the output format match the web UI's logic step editor.</p>
 */
@ApplicationScoped
public class SandboxedLogicStepTester implements LogicStepTester {

    private static final Logger LOG = LogManager.getLogger(SandboxedLogicStepTester.class);
    static final long TIME_LIMIT_MS = 5_000;
    static final int MAX_RUNNING = 2;
    private static final String DASHES = "-------------";

    private final Semaphore running = new Semaphore(MAX_RUNNING);

    @Override
    public LogicTestResult test(LogicTestRequest request) {
        if (!running.tryAcquire()) {
            throw new IllegalStateException("Too many logic step tests are running; try again shortly");
        }
        StringOutputLogger output = new StringOutputLogger();
        AtomicReference<String> finalOutput = new AtomicReference<>();
        long start = System.nanoTime();
        Thread worker = new Thread(() -> {
            try {
                finalOutput.set(run(request, output));
            } finally {
                running.release();
            }
        }, "logic-step-test");
        worker.setDaemon(true);
        worker.start();
        try {
            worker.join(TIME_LIMIT_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long duration = (System.nanoTime() - start) / 1_000_000;
        if (worker.isAlive()) {
            worker.interrupt();
            LOG.warn("Logic step test did not finish within {} ms; its thread keeps running until the script ends",
                    TIME_LIMIT_MS);
            return new LogicTestResult(snapshot(output) + "\nStopped waiting after " + TIME_LIMIT_MS
                    + " ms: the script did not finish.", true, duration);
        }
        return new LogicTestResult(finalOutput.get(), false, duration);
    }

    private static String run(LogicTestRequest request, StringOutputLogger output) {
        Variables vars = new Variables();
        if (request.variables() != null) {
            request.variables().forEach(vars::addVariable);
        }
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("variables", vars);
        inputs.put("request", createRequest(request));
        inputs.put("response", createResponse(request));
        try {
            String script = new LogicScriptUtil().buildScript(request.script());
            logMap("Variables", vars.getVariableValues(), output);
            output.logLine(DASHES + " script " + DASHES);
            ScriptIOBean ioBean = new ScriptRunner().runScript("logic-test", script, sandboxedEngine(), inputs, output);
            logMap("Outputs", ioBean.getOutputs(), output);
            logMap("Variables", vars.getVariableValues(), output);
        } catch (Exception e) {
            output.logLine("\nException thrown: " + e);
        }
        return snapshot(output);
    }

    static ScriptEngine sandboxedEngine() {
        return new NashornScriptEngineFactory().getScriptEngine(new String[] { "--no-java" },
                SandboxedLogicStepTester.class.getClassLoader(), className -> false);
    }

    private static String snapshot(StringOutputLogger output) {
        synchronized (output) {
            return output.getOutput();
        }
    }

    private static void logMap(String label, Map<String, ?> map, StringOutputLogger output) {
        if (!map.isEmpty()) {
            output.logLine(DASHES + " " + label + " " + DASHES);
            map.forEach((key, value) -> output.logLine(key + " = " + value));
        }
    }

    private static BaseRequest createRequest(LogicTestRequest request) {
        BaseRequest ret = new BaseRequest(null, null) {
            @Override
            public void setNamespace(String name, String value) {
            }

            @Override
            public void setKey(String key, String value) {
            }

            @Override
            public String getKey(String key) {
                return null;
            }
        };
        ret.setBody(request.requestBody());
        ret.addHeader("Test", "LoadTest");
        if (request.requestHeaders() != null) {
            request.requestHeaders().forEach(ret::addHeader);
        }
        return ret;
    }

    private static BaseResponse createResponse(LogicTestRequest request) {
        BaseResponse ret = new BaseResponse() {
            @Override
            public String getValue(String key) {
                return null;
            }
        };
        ret.setResponseBody(request.responseBody());
        if (request.responseHeaders() != null) {
            ret.getHeaders().putAll(request.responseHeaders());
        }
        return ret;
    }
}

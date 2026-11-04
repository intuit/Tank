/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.script;

import com.intuit.tank.rest.mvc.rest.services.scripts.ExternalScriptTester;
import com.intuit.tank.script.models.ScriptTO;
import com.intuit.tank.tools.script.ScriptRunner;
import com.intuit.tank.tools.script.StringOutputLogger;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openjdk.nashorn.api.scripting.ClassFilter;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Runs external script tests for the REST API in a restricted JavaScript engine:
 * <ul>
 *     <li>Java access only to the script model classes ({@code com.intuit.tank.script.models}) and a few
 *     collection classes, so a script can build and edit steps; the class filter also blocks reflection
 *     through the objects the script is given;</li>
 *     <li>a time limit of {@value #TIME_LIMIT_MS} ms, after which the request returns with what was printed
 *     so far;</li>
 *     <li>at most {@value #MAX_RUNNING} tests at once.</li>
 * </ul>
 *
 * <p>As when a filter runs it, the script gets the script as the {@code script} input of {@code ioBean}.</p>
 *
 * <p>A script that is still running at the time limit cannot be stopped on current JVMs; its thread runs on
 * until the script ends and keeps its slot until then. If scripts that never end use up every slot,
 * further tests are refused until the controller restarts.</p>
 */
@ApplicationScoped
public class SandboxedExternalScriptTester implements ExternalScriptTester {

    private static final Logger LOG = LogManager.getLogger(SandboxedExternalScriptTester.class);
    static final long TIME_LIMIT_MS = 60_000;
    static final int MAX_RUNNING = 2;
    private static final String MODELS_PACKAGE = "com.intuit.tank.script.models.";
    private static final Set<String> ALLOWED_CLASSES = Set.of("java.util.ArrayList", "java.util.LinkedList",
            "java.util.HashMap", "java.util.LinkedHashMap", "java.util.TreeMap", "java.util.HashSet",
            "java.util.LinkedHashSet", "java.util.TreeSet", "java.util.UUID", "java.util.Date");
    static final ClassFilter CLASS_FILTER = name -> name.startsWith(MODELS_PACKAGE) || ALLOWED_CLASSES.contains(name);

    private final Semaphore running = new Semaphore(MAX_RUNNING);
    private final long timeLimitMs;

    public SandboxedExternalScriptTester() {
        this(TIME_LIMIT_MS);
    }

    /** For tests, which should not wait a full minute for a script that never ends. */
    SandboxedExternalScriptTester(long timeLimitMs) {
        this.timeLimitMs = timeLimitMs;
    }

    @Override
    public Run run(String name, String code, ScriptTO script) {
        if (!running.tryAcquire()) {
            throw new IllegalStateException("Too many external script tests are running; try again shortly");
        }
        StringOutputLogger output = new StringOutputLogger();
        AtomicReference<String> finalOutput = new AtomicReference<>();
        long start = System.nanoTime();
        Thread worker = new Thread(() -> {
            try {
                finalOutput.set(run(name, code, script, output));
            } finally {
                running.release();
            }
        }, "external-script-test");
        worker.setDaemon(true);
        worker.start();
        try {
            worker.join(timeLimitMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        long duration = (System.nanoTime() - start) / 1_000_000;
        if (worker.isAlive()) {
            worker.interrupt();
            LOG.warn("External script test did not finish within {} ms; its thread keeps running until the script ends",
                    timeLimitMs);
            return new Run(snapshot(output) + "\nStopped waiting after " + timeLimitMs
                    + " ms: the script did not finish.", true, duration);
        }
        return new Run(finalOutput.get(), false, duration);
    }

    private static String run(String name, String code, ScriptTO script, StringOutputLogger output) {
        try {
            new ScriptRunner().runScript(name, code, sandboxedEngine(), Map.of("script", script), output);
        } catch (Exception e) {
            output.logLine("\nException thrown: " + e);
        }
        return snapshot(output);
    }

    static ScriptEngine sandboxedEngine() {
        return new NashornScriptEngineFactory().getScriptEngine(new String[0],
                SandboxedExternalScriptTester.class.getClassLoader(), CLASS_FILTER);
    }

    private static String snapshot(StringOutputLogger output) {
        synchronized (output) {
            return output.getOutput();
        }
    }
}

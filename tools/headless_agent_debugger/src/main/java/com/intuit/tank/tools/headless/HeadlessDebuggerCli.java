package com.intuit.tank.tools.headless;

/*
 * #%L
 * Intuit Tank Headless Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.IVersionProvider;
import picocli.CommandLine.Option;

/**
 * Command line entry point. The exit codes are a stable contract for callers such as Jenkins.
 */
@Command(name = "java -jar Headless-Debugger-all.jar",
        description = "Replays a Tank project and writes the authid and step results to output.log.",
        versionProvider = HeadlessDebuggerCli.BuildVersion.class,
        sortOptions = false,
        exitCodeListHeading = "%nExit codes:%n",
        exitCodeList = {
                "0:The run finished. Check output.log for the result",
                "1:The script failed the hostname replacement check (not filtered for production)",
                "2:Invalid command line arguments",
                "3:Could not get the project or its script from Tank" })
public class HeadlessDebuggerCli implements Callable<Integer> {

    public static final int EXIT_OK = 0;
    public static final int EXIT_HOSTNAME_CHECK_FAILED = 1;
    public static final int EXIT_INVALID_ARGUMENTS = 2;
    public static final int EXIT_TANK_API_ERROR = 3;

    private static final Logger LOG = LogManager.getLogger(HeadlessDebuggerCli.class);

    @Option(names = { "-h", "--host" }, required = true, paramLabel = "<controller_base_url>",
            description = "The url of the Tank controller to get the project from.")
    private String url;

    @Option(names = { "-p", "--project" }, required = true, paramLabel = "<projectId>",
            description = "The id of the Tank project whose script to run.")
    private int projectId;

    @Option(names = { "-t", "--token" }, required = true, paramLabel = "<token>",
            description = "Tank API token used to connect to the controller.")
    private String token;

    @Option(names = "--help", usageHelp = true, description = "Show this help and exit.")
    private boolean help;

    @Option(names = "--version", versionHelp = true, description = "Show the build version and git commit, then exit.")
    private boolean version;

    static CommandLine newCommandLine() {
        return new CommandLine(new HeadlessDebuggerCli()); // picocli exits 2 on invalid input
    }

    static int run(String[] args) {
        return newCommandLine().execute(args);
    }

    @Override
    public Integer call() throws InterruptedException {
        LOG.info("Headless Agent Debugger - STARTING EXECUTION");

        HeadlessDebuggerSetup runner = new HeadlessDebuggerSetup(url, projectId, token);
        if (runner.isHostnameCheckFailed()) {
            return EXIT_HOSTNAME_CHECK_FAILED;
        }
        if (!runner.isReady()) {
            return EXIT_TANK_API_ERROR;
        }
        try {
            runner.start();
            runner.getRunningThread().join();
        } catch (Exception e) {
            LOG.error("Error running headless debugger: " + e.getMessage(), e);
        } finally {
            runner.stop();
            runner.quit();
        }
        return EXIT_OK;
    }

    /** Reads the version and git commit that the build wrote into the jar. */
    public static class BuildVersion implements IVersionProvider {
        @Override
        public String[] getVersion() throws IOException {
            Properties props = new Properties();
            try (InputStream in = HeadlessDebuggerCli.class.getResourceAsStream("/headless-debugger-build.properties")) {
                if (in != null) {
                    props.load(in);
                }
            }
            return new String[] {
                    "Headless Debugger " + props.getProperty("git.build.version", "unknown"),
                    "git commit " + props.getProperty("git.commit.id.abbrev", "unknown")
                            + ("true".equals(props.getProperty("git.dirty")) ? " (dirty)" : "") };
        }
    }
}

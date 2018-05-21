package com.intuit.tank.agent;

/*
 * #%L
 * Agent Startup
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.AmazonUtil;
import com.intuit.tank.vm.common.TankConstants;

public class AgentStartup implements Runnable {

    private static Logger logger = LogManager.getLogger(AgentStartup.class);
    public static final String SERVICE_RELATIVE_PATH = "/rest/v1/agent-service";
    public static final String METHOD_SETTINGS = "/settings";
    private static String API_HARNESS_COMMAND = "./startAgent.sh";
    public static final String METHOD_SUPPORT = "/supportFiles";
    private static final long WAIT_FOR_RESTART_TIME = 00000; // zero minute

    private String controllerBase;

    public void run() {
        logger.info("Starting up...");
        if (AmazonUtil.usingEip()) {
            try {
                logger.info("Using EIP. Sleeping for " + WAIT_FOR_RESTART_TIME + " ms.");
                Thread.sleep(WAIT_FOR_RESTART_TIME);
            } catch (InterruptedException e1) {
                logger.info("Exception waiting.");
                System.exit(0);
            }
        }
        try {
            if (controllerBase == null) {
                controllerBase = AmazonUtil.getControllerBaseUrl();
            }

            logger.info("Starting up: ControllerBaseUrl=" + controllerBase);
            URL url = new URL(controllerBase + SERVICE_RELATIVE_PATH + METHOD_SETTINGS);
            logger.info("Starting up: making call to tank service url to get settings.xml "
                    + url.toExternalForm());
            InputStream settingsStream = url.openStream();
            try {
                String settings = IOUtils.toString(settingsStream, StandardCharsets.UTF_8);
                FileUtils.writeStringToFile(new File("settings.xml"), settings, StandardCharsets.UTF_8);
                logger.info("got settings file...");
            } finally {
                IOUtils.closeQuietly(settingsStream);
            }
            url = new URL(controllerBase + SERVICE_RELATIVE_PATH + METHOD_SUPPORT);
            logger.info("Making call to tank service url to get support files " + url.toExternalForm());
            ZipInputStream zip = new ZipInputStream(url.openStream());
            try {
                ZipEntry entry = zip.getNextEntry();
                while (entry != null) {
                    String name = entry.getName();
                    logger.info("Got file from controller: " + name);
                    File f = new File(name);
                    FileOutputStream fout = FileUtils.openOutputStream(f);
                    try {
                        IOUtils.copy(zip, fout);
                    } finally {
                        IOUtils.closeQuietly(fout);
                    }
                    entry = zip.getNextEntry();
                }
            } finally {
                IOUtils.closeQuietly(zip);
            }
            // now start the harness
            String jvmArgs = AmazonUtil.getUserDataAsMap().get(TankConstants.KEY_JVM_ARGS);
            logger.info("Starting apiharness with command: " + API_HARNESS_COMMAND + " -http=" + controllerBase + " "
                    + jvmArgs);
            Runtime.getRuntime().exec(API_HARNESS_COMMAND + " -http=" + controllerBase + " " + jvmArgs);
        } catch (Exception e) {
            logger.error("Error in AgentStartup " + e, e);
        }
    }

    public static void main(String[] args) {
        AgentStartup agentStartup = new AgentStartup();

        for (String argument : args) {
            String[] values = argument.split("=");

            if (values[0].equalsIgnoreCase("-controller")) {
                if (values.length < 2) {
                    usage();
                    return;
                }
                agentStartup.controllerBase = values[1];
            }

        }
        agentStartup.run();
    }

    /**
     * Display usage error text
     */
    private static void usage() {
        System.out.println("Tank Test Startup Usage:");
        System.out
                .println("java -cp agent-startup-pkg-1.0-all.jar com/intuit/tank/agent/AgentStartup <options>");
        System.out.println("-controller=<controller_base_url>:  The url of the controller to get test info from");
    }

}

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

import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import com.amazonaws.util.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.AmazonUtil;
import com.intuit.tank.vm.common.TankConstants;

public class AgentStartup implements Runnable {
    private static Logger logger = LogManager.getLogger(AgentStartup.class);
    private static final String SERVICE_RELATIVE_PATH = "/rest/v1/agent-service";
    private static final String METHOD_SETTINGS = "/settings";
    private static final String API_HARNESS_COMMAND = "./startAgent.sh";
    private static final String METHOD_SUPPORT = "/supportFiles";
    private static final int[] FIBONACCI = new int[] { 1, 1, 2, 3, 5, 8, 13 };

    private final String controllerBaseUrl;

    public AgentStartup (String controllerBaseUrl ) {
        this.controllerBaseUrl = controllerBaseUrl;
    }

    public void run() {
        logger.info("Starting up...");
        try {
            logger.info("Starting up: ControllerBaseUrl=" + controllerBaseUrl);
            URL url = new URL(controllerBaseUrl + SERVICE_RELATIVE_PATH + METHOD_SETTINGS);
            logger.info("Starting up: making call to tank service url to get settings.xml "
                    + url.toExternalForm());
            try ( InputStream settingsStream = url.openStream() ) {
                String settings = IOUtils.toString(settingsStream, StandardCharsets.UTF_8);
                FileUtils.writeStringToFile(new File("settings.xml"), settings, StandardCharsets.UTF_8);
                logger.info("got settings file...");
            } catch (ConnectException ce) {
                logger.error("Error creating connection to "
                        + controllerBaseUrl + " : this is normal during the bake : " + ce.getMessage());
            }
            // Download Support Files
            url = new URL(controllerBaseUrl + SERVICE_RELATIVE_PATH + METHOD_SUPPORT);
            logger.info("Making call to tank service url to get support files " + url.toExternalForm());
            int retryCount = 0;
            while (true) {
                try (ZipInputStream zip = new ZipInputStream(url.openStream())) {
                    ZipEntry entry = zip.getNextEntry();
                    while (entry != null) {
                        String name = entry.getName();
                        logger.info("Got file from controller: " + name);
                        File f = new File(name);
                        try (FileOutputStream fout = FileUtils.openOutputStream(f)) {
                            IOUtils.copy(zip, fout);
                        }
                        entry = zip.getNextEntry();
                    }
                    break;
                } catch (EOFException | ZipException e) {
                    logger.error("Error unzipping support files : retryCount="
                            + retryCount + " : " + e.getMessage());
                    if (retryCount < FIBONACCI.length) {
                        Thread.sleep( FIBONACCI[++retryCount] * 1000 );
                    } else throw e;
                }
            }
            // now start the harness
            String jvmArgs = AmazonUtil.getUserDataAsMap().get(TankConstants.KEY_JVM_ARGS);
            logger.info("Starting apiharness with command: "
                    + API_HARNESS_COMMAND + " -http=" + controllerBaseUrl + " " + jvmArgs);
            Runtime.getRuntime().exec(API_HARNESS_COMMAND + " -http=" + controllerBaseUrl + " " + jvmArgs);
        } catch (Exception e) {
            logger.error("Error in AgentStartup " + e, e);
        }
    }

    public static void main(String[] args) {
        String controllerBaseUrl = null;
        for (String argument : args) {
            String[] values = argument.split("=");

            if (values[0].equalsIgnoreCase("-controller")) {
                if (values.length < 2) {
                    usage();
                    return;
                }
                controllerBaseUrl = values[1];
            }
        }
        if (StringUtils.isNullOrEmpty(controllerBaseUrl)) {
            controllerBaseUrl = AmazonUtil.getControllerBaseUrl();
        }
        AgentStartup agentStartup = new AgentStartup(controllerBaseUrl);
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

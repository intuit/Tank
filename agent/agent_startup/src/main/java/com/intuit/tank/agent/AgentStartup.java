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

import java.io.*;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.AmazonUtil;
import com.intuit.tank.vm.common.TankConstants;
import software.amazon.awssdk.utils.StringUtils;

public class AgentStartup implements Runnable {
    private static final Logger logger = LogManager.getLogger(AgentStartup.class);
    private static final String SERVICE_RELATIVE_PATH = "/v2/agent";
    private static final String METHOD_SETTINGS = "/settings";
    private static final String API_HARNESS_COMMAND = "./startAgent.sh";
    private static final String METHOD_SUPPORT = "/support-files";
    private static final String TANK_AGENT_DIR = "/opt/tank_agent";
    private static final int[] FIBONACCI = new int[] { 1, 1, 2, 3, 5, 8, 13 };

    private final String controllerBaseUrl;
    private final String token;

    public AgentStartup (String controllerBaseUrl, String token ) {
        this.controllerBaseUrl = controllerBaseUrl;
        this.token = token;
    }

    public void run() {
        logger.info("Starting up...");
        HttpClient client = HttpClient.newHttpClient();
        try {
            logger.info("Starting up: ControllerBaseUrl={}", controllerBaseUrl);
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(
                    controllerBaseUrl + SERVICE_RELATIVE_PATH + METHOD_SETTINGS))
                    .header("Authorization", "bearer "+token).build();
            logger.info("Starting up: making call to tank service url to get settings.xml {} {} {}",
                    controllerBaseUrl, SERVICE_RELATIVE_PATH, METHOD_SUPPORT);
            client.send(request, BodyHandlers.ofFile(Paths.get(TANK_AGENT_DIR, "settings.xml")));
            logger.info("got settings file...");
            // Download Support Files
            request = HttpRequest.newBuilder().uri(URI.create(
                    controllerBaseUrl + SERVICE_RELATIVE_PATH + METHOD_SUPPORT))
                    .header("Authorization", "bearer "+token).build();
            logger.info("Making call to tank service url to get support files {} {} {}",
                    controllerBaseUrl, SERVICE_RELATIVE_PATH, METHOD_SUPPORT);
            int retryCount = 0;
            while (true) {
                try (ZipInputStream zip = new ZipInputStream(
                        client.send(request, BodyHandlers.ofInputStream()).body())) {
                    ZipEntry entry = zip.getNextEntry();
                    while (entry != null) {
                        String name = entry.getName();
                        logger.info("Got file from controller: " + name);
                        File file = new File(TANK_AGENT_DIR, name);
                        if (!file.toPath().normalize().startsWith(TANK_AGENT_DIR)) // Protect "Zip Slip"
                            throw new Exception("Bad zip entry");
                        try (FileOutputStream fout = FileUtils.openOutputStream(file)) {
                            IOUtils.copy(zip, fout);
                        }
                        entry = zip.getNextEntry();
                    }
                    break;
                } catch (EOFException | ZipException e) {
                    logger.error("Error unzipping support files : retryCount={} : {}", retryCount, e.getMessage());
                    if (retryCount < FIBONACCI.length) {
                        Thread.sleep( FIBONACCI[retryCount++] * 1000 );
                    } else throw e;
                }
            }
            // now start the harness
            String controllerArg = " -http=" + controllerBaseUrl;
            String jvmArgs = AmazonUtil.getUserDataAsMap().get(TankConstants.KEY_JVM_ARGS);
            logger.info("Starting apiharness with command: {} {} {}",
                    API_HARNESS_COMMAND, controllerArg, jvmArgs);
            Runtime.getRuntime().exec(API_HARNESS_COMMAND + "\"" + controllerArg + "\" \"" + jvmArgs + "\"");
        } catch (ConnectException ce) {
            logger.error("Error creating connection to {} : this is normal during the bake : {}",
                    controllerBaseUrl, ce.getMessage());
        } catch (IOException e) {
            logger.error("Error Executing API Harness Command: {} : {}", API_HARNESS_COMMAND, e, e);
        } catch (InterruptedException ignored) {
        } catch (Exception e) {
            logger.error("Error in AgentStartup {}", e, e);
        }
    }

    public static void main(String[] args) {
        String controllerBaseUrl = null;
        String token = null;
        for (String argument : args) {
            String[] values = argument.split("=");
            if (values[0].equalsIgnoreCase("-http")) {
                if (values.length < 2) {
                    usage();
                    return;
                }
                controllerBaseUrl = values[1];
            } else if (values[0].equalsIgnoreCase("-token")) {
                token = (values.length > 1 ? values[1] : null);
            }
        }
        controllerBaseUrl = (StringUtils.isEmpty(controllerBaseUrl)) ? AmazonUtil.getControllerBaseUrl() : controllerBaseUrl;
        token = (StringUtils.isEmpty(token)) ? AmazonUtil.getAgentToken() : token;

        AgentStartup agentStartup = new AgentStartup(controllerBaseUrl, token);
        agentStartup.run();
    }

    /**
     * Display usage error text
     */
    private static void usage() {
        System.out.println("Tank Test Startup Usage:");
        System.out.println("java -cp agent-startup-pkg-1.0-all.jar com/intuit/tank/agent/AgentStartup <options>");
        System.out.println("-http=<controller_base_url>:  The url of the controller to get test info from");
        System.out.println("-token=<agent_token>:  The tank agent token assigned by the controller");
        System.out.println("Service Path: " + SERVICE_RELATIVE_PATH);
        System.out.println("Settings Method: " + METHOD_SETTINGS);
        System.out.println("Support Files Method: " + METHOD_SUPPORT);
    }
}

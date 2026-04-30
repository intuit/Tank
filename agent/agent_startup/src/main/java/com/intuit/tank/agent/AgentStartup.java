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
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

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
        HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
        try {
            boolean wsEnabled = Boolean.parseBoolean(
                    AmazonUtil.getUserDataAsMap().getOrDefault(TankConstants.KEY_COMMAND_WS_ENABLED, "false"));
            logger.info("Starting up: ControllerBaseUrl={}", controllerBaseUrl);
            if (!wsEnabled) {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(
                        controllerBaseUrl + SERVICE_RELATIVE_PATH + METHOD_SETTINGS))
                        .header("Authorization", "bearer "+token).build();
                logger.info("Starting up: making call to tank service url to get settings.xml {} {} {}",
                        controllerBaseUrl, SERVICE_RELATIVE_PATH, METHOD_SUPPORT);
                client.send(request, BodyHandlers.ofFile(Paths.get(TANK_AGENT_DIR, "settings.xml")));
                logger.info("got settings file...");
            } else {
                logger.info("Command WS enabled - skipping settings download (will receive over WS).");
            }
            // Always download support files (harness JAR) - required before harness can start
            {
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(
                        controllerBaseUrl + SERVICE_RELATIVE_PATH + METHOD_SUPPORT))
                        .header("Authorization", "bearer "+token).build();
                logger.info("Making call to tank service url to get support files {} {} {}",
                        controllerBaseUrl, SERVICE_RELATIVE_PATH, METHOD_SUPPORT);
                int retryCount = 0;
                while (true) {
                    try {
                        HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
                        if (response.statusCode() < 200 || response.statusCode() >= 300) {
                            throw new IOException("Support files download failed: HTTP " + response.statusCode());
                        }
                        try (ZipInputStream zip = new ZipInputStream(response.body())) {
                            ZipEntry entry = zip.getNextEntry();
                            Path agentDirPath = Paths.get(TANK_AGENT_DIR).toAbsolutePath().normalize();
                            boolean extractedEntry = false;
                            while (entry != null) {
                                String filename = entry.getName();
                                logger.info("Got file from controller: {}", filename);
                                Path targetPath = agentDirPath.resolve(filename).normalize();
                                if (!targetPath.startsWith(agentDirPath)) // Protect "Zip Slip"
                                    throw new ZipException("Bad zip entry");
                                if (!entry.isDirectory()) {
                                    Files.write(targetPath, zip.readAllBytes());
                                    extractedEntry = true;
                                }
                                entry = zip.getNextEntry();
                            }
                            if (!extractedEntry) {
                                throw new ZipException("Support files archive contained no files");
                            }
                            break;
                        }
                    } catch (IOException e) {
                        logger.error("Error downloading/unzipping support files : retryCount={} : {}", retryCount, e.getMessage());
                        if (retryCount < FIBONACCI.length) {
                            Thread.sleep( FIBONACCI[retryCount++] * 1000 );
                        } else throw e;
                    }
                }
                File harnessJar = new File(TANK_AGENT_DIR, "apiharness-1.0-all.jar");
                if (!harnessJar.exists()) {
                    throw new IOException("apiharness-1.0-all.jar not found after support files extraction");
                }
            }
            // now start the harness
            String controllerArg = " -http=" + controllerBaseUrl;
            String jvmArgs = AmazonUtil.getUserDataAsMap().getOrDefault(TankConstants.KEY_JVM_ARGS, "");
            String command = API_HARNESS_COMMAND + controllerArg;
            if (jvmArgs != null && !jvmArgs.isBlank()) {
                command += " " + jvmArgs;
            }
            logger.info("Starting apiharness with command: {}", command);
            Runtime.getRuntime().exec(command);
        } catch (ConnectException ce) {
            logger.error("Error creating connection to {} : this is normal during the bake : {}",
                    controllerBaseUrl, ce.getMessage());
        } catch (IOException e) {
            logger.error("Error in AgentStartup: {} : {}", API_HARNESS_COMMAND, e, e);
            // If harness JAR exists on disk (baked into AMI), try launching anyway
            File fallbackJar = new File(TANK_AGENT_DIR, "apiharness-1.0-all.jar");
            if (fallbackJar.exists()) {
                logger.info("Harness JAR found on disk despite download failure — launching anyway");
                try {
                    String controllerArg = " -http=" + controllerBaseUrl;
                    String jvmArgs = AmazonUtil.getUserDataAsMap().getOrDefault(TankConstants.KEY_JVM_ARGS, "");
                    String command = API_HARNESS_COMMAND + controllerArg;
                    if (jvmArgs != null && !jvmArgs.isBlank()) {
                        command += " " + jvmArgs;
                    }
                    logger.info("Starting apiharness with fallback command: {}", command);
                    Runtime.getRuntime().exec(command);
                } catch (IOException ex) {
                    logger.error("Fallback harness launch also failed: {}", ex.getMessage(), ex);
                }
            }
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

package com.intuit.tank.standalone.agent;

/*
 * #%L
 * Agent Standalone
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.intuit.tank.AgentServiceClient;
import com.intuit.tank.harness.HostInfo;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentAvailabilityStatus;
import com.intuit.tank.vm.agent.messages.StandaloneAgentRequest;

public class StandaloneAgentStartup implements Runnable {

    private static Logger LOG = Logger.getLogger(StandaloneAgentStartup.class);
    public static final String SERVICE_RELATIVE_PATH = "/rest/v1/agent-service";
    private static String API_HARNESS_COMMAND = "./start_wats.sh";
    public static final String METHOD_SETTINGS = "/settings";
    public static final String METHOD_SUPPORT = "/supportFiles";
    private static final long PING_TIME = 1000 * 60 * 5;// five minutes

    private String controllerBase;
    private AgentAvailability currentAvailability;
    private AgentServiceClient agentClient;
    private String instanceId;
    private String hostname;
    private int capacity = 4000;

    @Override
    public void run() {
        CommandListener.startHttpServer(CommandListener.PORT, this);
        agentClient = new AgentServiceClient(controllerBase);
        
        if (hostname != null) {
            instanceId = hostname;
        } else {
            instanceId = new HostInfo().getPublicIp();
        }
        currentAvailability = new AgentAvailability(instanceId, getInstanceUrl(), capacity,
                AgentAvailabilityStatus.AVAILABLE);
        startPinger();
    }

    public void startTest(final StandaloneAgentRequest request) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    currentAvailability.setAvailabilityStatus(AgentAvailabilityStatus.DELEGATING);
                    sendAvailability();
                    LOG.info("Starting up: ControllerBaseUrl=" + controllerBase);
                    URL url = new URL(controllerBase + SERVICE_RELATIVE_PATH + METHOD_SETTINGS);
                    LOG.info("Starting up: making call to tank service url to get settings.xml "
                            + url.toExternalForm());
                    InputStream settingsStream = url.openStream();
                    try {
                        String settings = IOUtils.toString(settingsStream);
                        FileUtils.writeStringToFile(new File("settings.xml"), settings);
                        LOG.info("got settings file...");
                    } finally {
                        IOUtils.closeQuietly(settingsStream);
                    }
                    url = new URL(controllerBase + SERVICE_RELATIVE_PATH + METHOD_SUPPORT);
                    LOG.info("Making call to tank service url to get support files " + url.toExternalForm());
                    ZipInputStream zip = new ZipInputStream(url.openStream());
                    try {
                        ZipEntry entry = zip.getNextEntry();
                        while (entry != null) {
                            String name = entry.getName();
                            LOG.info("Got file from controller: " + name);
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
                    String cmd = API_HARNESS_COMMAND + " -http=" + controllerBase + " -jobId=" + request.getJobId()
                            + " -stopBehavior=" + request.getStopBehavior();
                    LOG.info("Starting apiharness with command: " + cmd);
                    currentAvailability.setAvailabilityStatus(AgentAvailabilityStatus.RUNNING_JOB);
                    sendAvailability();
                    Process exec = Runtime.getRuntime().exec(cmd);
                    exec.waitFor();
                    currentAvailability.setAvailabilityStatus(AgentAvailabilityStatus.AVAILABLE);
                    sendAvailability();
                    //
                } catch (Exception e) {
                    LOG.error("Error in AgentStartup " + e, e);
                    currentAvailability.setAvailabilityStatus(AgentAvailabilityStatus.AVAILABLE);
                    sendAvailability();
                }
            }
        });
        t.start();
    }

    private String getInstanceUrl() {
        if (hostname == null) {
            hostname = new HostInfo().getPublicIp();
            if (hostname.equals(HostInfo.UNKNOWN)) {
                LOG.error("Cannot determine local hostname or ip please specify on the caomandline.");
                System.err.println("Cannot determine local hostname or ip please specify on the caomandline.");
                usage();
                System.exit(1);
            }
        }
        return "http://" + hostname + ":" + CommandListener.PORT;
    }

    private void startPinger() {

        Thread t = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {
                        sendAvailability();
                    } catch (Exception e1) {
                        LOG.warn("Error sending Availability: " + e1, e1);
                    }
                    try {
                        Thread.sleep(PING_TIME);
                    } catch (InterruptedException e) {
                        LOG.warn("Interrupted during sleep.", e);
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private void sendAvailability() {
        // create new availability as a copy of the original
        AgentAvailability availability = new AgentAvailability(currentAvailability.getInstanceId(),
                currentAvailability.getInstanceUrl(), currentAvailability.getCapacity(),
                currentAvailability.getAvailabilityStatus());
        LOG.info("Sending availaability: " + ToStringBuilder.reflectionToString(availability));
        agentClient.standaloneAgentAvailable(availability);
    }

    public static void main(String[] args) {
        StandaloneAgentStartup agentStartup = new StandaloneAgentStartup();

        for (int iter = 0; iter < args.length; ++iter) {
            String argument = args[iter];
            String[] values = argument.split("=");

            if (values[0].equalsIgnoreCase("-controller")) {
                if (values.length < 2) {
                    usage();
                    return;
                }
                agentStartup.controllerBase = values[1];
                continue;
            } else if (values[0].equalsIgnoreCase("-host")) {
                if (values.length < 2) {
                    usage();
                    return;
                }
                agentStartup.hostname = values[1];
                continue;
            } else if (values[0].equalsIgnoreCase("-capacity")) {
                if (values.length < 2) {
                    usage();
                    return;
                }
                try {
                    agentStartup.capacity = Integer.parseInt(values[1]);
                } catch (NumberFormatException e) {
                    LOG.error("Error parsing capacity " + values[1] + " Capacity must be an integer.");
                    System.out.println("Error parsing capacity " + values[1] + " Capacity must be an integer.");
                    usage();
                    return;
                }
                continue;
            }

        }
        if (StringUtils.isBlank(agentStartup.controllerBase)) {
            usage();
            System.exit(1);
        }
        agentStartup.run();
    }

    /**
     * Display usage error text
     */
    private static void usage() {
        System.out.println("TAnk Standalone Agent Startup Usage:");
        System.out
                .println("java -cp standaloneagent-startup-pkg-1.0-all.jar com/intuit/tank/agent/StandaloneAgentStartup <options>");
        System.out.println("-controller=<controller_base_url>:  The url of the controller to get test info from.");
        System.out
                .println("-host=<agent ip or host>:  optional. only need if agent cannot determine correct ip. The ip or dns name of this agent.");
        System.out
                .println("-capacity=<integer>:  optional. The number of users this agent can simulate. Default 4000.");
    }

}

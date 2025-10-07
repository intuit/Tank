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

import com.intuit.tank.harness.AmazonUtil;
import software.amazon.awssdk.utils.StringUtils;

public class AgentStartup {
    private static final String SERVICE_RELATIVE_PATH = "/v2/agent";
    private static final String METHOD_SETTINGS = "/settings";
    private static final String METHOD_SUPPORT = "/support-files";
    protected static final String API_HARNESS_COMMAND = "./startAgent.sh";
    protected static final String TANK_AGENT_DIR = "/opt/tank_agent";

    public static void main(String[] args) {
        String controllerBaseUrl = null;
        String token = null;
        boolean server = false;
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
            } else if (values[0].equalsIgnoreCase("-server")) {
                server = true;
            }
        }
        controllerBaseUrl = (StringUtils.isEmpty(controllerBaseUrl)) ? AmazonUtil.getControllerBaseUrl() : controllerBaseUrl;
        token = (StringUtils.isEmpty(token)) ? AmazonUtil.getAgentToken() : token;
        server = (!server) ? AmazonUtil.getEnableServer() : server;
        if (server) {
            AgentStartupServer agentStartup = new AgentStartupServer(8090,  token);
            agentStartup.run();
        }
        else {
            AgentStartupClient agentStartup = new AgentStartupClient(controllerBaseUrl, token);
            agentStartup.run();
        }
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

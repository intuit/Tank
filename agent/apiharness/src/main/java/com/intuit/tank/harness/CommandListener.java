package com.intuit.tank.harness;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
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
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.google.common.collect.ImmutableMap;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.vm.api.enumerated.AgentCommand;

public class CommandListener {

    private static final Logger LOG = LogManager.getLogger(CommandListener.class);

    private static boolean started = false;

    public static void main(String[] args) {
        APITestHarness.getInstance();
        startHttpServer(8080);
        System.out.println("Listening for requests");
    }

    public synchronized static void startHttpServer(int port) {
        if (!started) {
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                HttpContext context = server.createContext("/");
                context.setHandler(CommandListener::handleRequest);
                server.start();
                System.out.println("Starting httpserver on port " + port);
                LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Starting httpserver on port " + port)));
                started = true;
            } catch (IOException e) {
                LOG.error(LogUtil.getLogMessage("Error starting httpServer: " + e), e);
                throw new RuntimeException(e);
            }
        }
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            String response = "unknown path";
            String path = exchange.getRequestURI().getPath();
            if (path.equals(AgentCommand.start.getPath()) || path.equals(AgentCommand.run.getPath())) {
                response = "Received command " + path + ", Starting Test JobId=" + APITestHarness.getInstance().getAgentRunData().getJobId();
                startTest();
            } else if (path.startsWith(AgentCommand.stop.getPath())) {
                response = "Received command " + path + ", Stopping Test JobId=" + APITestHarness.getInstance().getAgentRunData().getJobId();
                APITestHarness.getInstance().setCommand(AgentCommand.stop);
            } else if (path.startsWith(AgentCommand.kill.getPath())) {
                response = "Received command " + path + ", Killing Test JobId=" + APITestHarness.getInstance().getAgentRunData().getJobId();
                System.exit(0);
            } else if (path.startsWith(AgentCommand.pause.getPath())) {
                response = "Received command " + path + ", Pausing Test JobId=" + APITestHarness.getInstance().getAgentRunData().getJobId();
                APITestHarness.getInstance().setCommand(AgentCommand.pause);
            } else if (path.startsWith(AgentCommand.pause_ramp.getPath())) {
                response = "Received command " + path + ", Pausing Ramp for Test JobId=" + APITestHarness.getInstance().getAgentRunData().getJobId();
                APITestHarness.getInstance().setCommand(AgentCommand.pause_ramp);
            } else if (path.startsWith(AgentCommand.resume_ramp.getPath())) {
                response = "Received command " + path + ", Resume Test JobId=" + APITestHarness.getInstance().getAgentRunData().getJobId();
                APITestHarness.getInstance().setCommand(AgentCommand.resume_ramp);
            } else if (path.startsWith(AgentCommand.status.getPath())) {
                response = APITestHarness.getInstance().getStatus().toString();
                APITestHarness.getInstance().setCommand(AgentCommand.resume_ramp);
            }
            LOG.info(new ObjectMessage(ImmutableMap.of("Message", response )));

            exchange.getResponseHeaders().set(HTTP.CONTENT_TYPE, "text/plain");
            exchange.getResponseHeaders().set(HTTP.SERVER_HEADER,"Intuit Tank Agent/3.0.1");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            exchange.close();
        } catch (IOException e) {
            LOG.info(new ObjectMessage(ImmutableMap.of("Message", "Failed to handle controller command" )), e);
        }
    }

    public static void startTest() {
        Thread thread = new Thread( () -> APITestHarness.getInstance().runConcurrentTestPlans());
        thread.setDaemon(true);
        thread.start();
    }
}

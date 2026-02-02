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
import java.net.BindException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.vm.api.enumerated.AgentCommand;

public class CommandListener {

    private static final Logger LOG = LogManager.getLogger(CommandListener.class);

    private static boolean started = false;

    private static final int MAX_RETRIES = 180;

    private static final long RETRY_SLEEP = 1000; // 1 second

    public static void main(String[] args) {
        APITestHarness.getInstance();
        startHttpServer(8080);
        System.out.println("Listening for requests");
    }

    public synchronized static void startHttpServer(int port) {
        if (!started) {
            int attempt = 1;
            while (attempt <= MAX_RETRIES) {
                try {
                    HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
                    HttpContext context = server.createContext("/");
                    context.setHandler(CommandListener::handleRequest);
                    server.start();
                    LOG.info(LogUtil.getLogMessage("Successful! Starting httpserver on port " + port));
                    started = true;
                    return;
                } catch (BindException e) {
                    LOG.info(LogUtil.getLogMessage("Attempt " + attempt + ": Port " + port + " is currently in use. Waiting..."));
                    try {
                        Thread.sleep(RETRY_SLEEP);
                    } catch (InterruptedException ignored) {}
                } catch (IOException e) {
                    LOG.error(LogUtil.getLogMessage("Attempt " + attempt + ": Error starting httpServer: " + e), e);
                    throw new RuntimeException(e);
                }
                attempt++;
        }
            LOG.error(LogUtil.getLogMessage("Maximum attempts reached. Unable to start server. Terminating instance"));
            throw new RuntimeException("Maximum attempts reached. Unable to start server.");
        }
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            String response = "Not Found";
            String path = exchange.getRequestURI().getPath();
            if (path.equals(AgentCommand.start.getPath()) || path.equals(AgentCommand.run.getPath())) {
                response = "Received command " + path + ", Starting Test JobId=" + APITestHarness.getInstance().getAgentRunData().getJobId();
                LOG.info(LogUtil.getLogMessage("Received START command - launching test threads for job " + 
                    APITestHarness.getInstance().getAgentRunData().getJobId()));
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
            LOG.info(LogUtil.getLogMessage(response));

            exchange.getResponseHeaders().set(HTTP.CONTENT_TYPE, "text/plain");
            exchange.getResponseHeaders().set(HTTP.SERVER_HEADER,"Intuit Tank Agent/4.0.0");
            if (response.equalsIgnoreCase("Not Found")) {
                exchange.sendResponseHeaders(404, response.length());
            } else {
                exchange.sendResponseHeaders(202, response.length());
            }
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            exchange.close();
        } catch (IOException e) {
            LOG.info(LogUtil.getLogMessage("Failed to handle controller command"), e);
        }
    }

    public static void startTest() {
        Thread thread = new Thread( () -> APITestHarness.getInstance().runConcurrentTestPlans());
        thread.setDaemon(true);
        thread.start();
    }
}

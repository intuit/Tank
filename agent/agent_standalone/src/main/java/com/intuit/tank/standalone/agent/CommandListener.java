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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import com.intuit.tank.vm.agent.messages.StandaloneAgentRequest;
import com.intuit.tank.vm.api.enumerated.AgentCommand;

public class CommandListener {

    private static Logger LOG = LogManager.getLogger(CommandListener.class);
    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    public static final int PORT = 1090;
    private static boolean started = false;

    private static StandaloneAgentStartup agentStarter;

    public synchronized static void startHttpServer(int port, StandaloneAgentStartup standaloneAgentStartup) {
        if (!started) {
            agentStarter = standaloneAgentStartup;
            try {
                HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
                HttpContext context = server.createContext("/");
                context.setHandler(CommandListener::handleRequest);
                server.start();
                System.out.println("Starting httpserver on port " + port);
                started = true;
            } catch (IOException e) {
                LOG.error("Error starting httpServer: " + e, e);
                throw new RuntimeException(e);
            }
        }
    }

    private static void handleRequest(HttpExchange exchange) {
        try {
            String response = "unknown path";
            int code = 200;
            String path = exchange.getRequestURI().getPath();
            if (path.equals(AgentCommand.request.getPath())) {
                response = "Requesting users ";
                StandaloneAgentRequest agentRequest = getRequest(exchange.getRequestBody());
                if (agentRequest == null) {
                    response = "Invalid StandaloneAgentRequest.";
                    code = 406;
                } else if (agentRequest.getJobId() != null && agentRequest.getUsers() > 0) {
                    // launch the harness with the specified details.
                    agentStarter.startTest(agentRequest);
                } else {
                    response = "invalid request.";
                    code = 400;
                }
            }
            exchange.getResponseHeaders().set(HTTP.CONTENT_TYPE, "text/plain");
            exchange.getResponseHeaders().set(HTTP.SERVER_HEADER,"Intuit Tank Agent/4.0.0");
            exchange.sendResponseHeaders(code, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            exchange.close();
        } catch (IOException e) {
            LOG.error("error sending response");
        }
    }

    /**
     * @return the job request the controller sent (JobManager.sendRequest posts it as JSON), or null if it can't be read
     */
    static StandaloneAgentRequest getRequest(InputStream inputStream) {
        try {
            return JSON_MAPPER.readValue(inputStream, StandaloneAgentRequest.class);
        } catch (JacksonException e) {
            LOG.error("Error unmarshalling body: {}", e.getMessage());
        }
        return null;
    }

}

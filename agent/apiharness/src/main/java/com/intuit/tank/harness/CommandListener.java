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
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.vm.api.enumerated.WatsAgentCommand;

public class CommandListener implements Container {

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
                Container container = new CommandListener();
                @SuppressWarnings("resource") Connection connection = new SocketConnection(container);
                SocketAddress address = new InetSocketAddress(port);
                System.out.println("Starting httpserver on port " + port);
                LOG.info("Starting httpserver on port " + port);
                connection.connect(address);
                started = true;
            } catch (IOException e) {
                LOG.error(LogUtil.getLogMessage("Error starting httpServer: " + e), e);
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void handle(Request req, Response response) {
        try {
            String msg = "unknown path";
            String path = req.getPath().getPath();
            if (path.equals(WatsAgentCommand.start.getPath()) || path.equals(WatsAgentCommand.run.getPath())) {
                msg = "Starting Test " + APITestHarness.getInstance().getAgentRunData().getJobId();
                startTest();
            } else if (path.startsWith(WatsAgentCommand.stop.getPath())) {
                msg = "Stopping Test " + APITestHarness.getInstance().getAgentRunData().getJobId();
                APITestHarness.getInstance().setCommand(WatsAgentCommand.stop);
            } else if (path.startsWith(WatsAgentCommand.kill.getPath())) {
                msg = "Killing Test " + APITestHarness.getInstance().getAgentRunData().getJobId();
                System.exit(0);
            } else if (path.startsWith(WatsAgentCommand.pause.getPath())) {
                msg = "Pausing Test " + APITestHarness.getInstance().getAgentRunData().getJobId();
                APITestHarness.getInstance().setCommand(WatsAgentCommand.pause);
            } else if (path.startsWith(WatsAgentCommand.pause_ramp.getPath())) {
                msg = "Pausing Ramp for Test " + APITestHarness.getInstance().getAgentRunData().getJobId();
                APITestHarness.getInstance().setCommand(WatsAgentCommand.pause_ramp);
            } else if (path.startsWith(WatsAgentCommand.resume_ramp.getPath())) {
                msg = "Pausing Test " + APITestHarness.getInstance().getAgentRunData().getJobId();
                APITestHarness.getInstance().setCommand(WatsAgentCommand.resume_ramp);
            } else if (path.startsWith(WatsAgentCommand.status.getPath())) {
                msg = APITestHarness.getInstance().getStats().toString();
                APITestHarness.getInstance().setCommand(WatsAgentCommand.resume_ramp);
            }
            LOG.info(msg);
            PrintStream body = response.getPrintStream();

            long time = System.currentTimeMillis();

            response.set("Content-Type", "text/plain");
            response.set("Server", "Intuit Tank Agent/1.0");
            response.setDate("Date", time);
            response.setDate("Last-Modified", time);

            body.println(msg);
            body.close();
        } catch (IOException e) {
            LOG.error("");
            response.setCode(500);
        }
    }

    public void startTest() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                APITestHarness.getInstance().runConcurrentTestPlans();
            }
        });
        t.setDaemon(true);
        t.start();
    }
}

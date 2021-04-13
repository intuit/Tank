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

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.protocol.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.intuit.tank.vm.agent.messages.StandaloneAgentRequest;
import com.intuit.tank.vm.api.enumerated.AgentCommand;

public class CommandListener {

    private static Logger LOG = LogManager.getLogger(CommandListener.class);

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
            exchange.getResponseHeaders().set(HTTP.SERVER_HEADER,"Intuit Tank Agent/3.0.1");
            exchange.sendResponseHeaders(code, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
            exchange.close();
        } catch (SAXException| ParserConfigurationException | IOException e) {
            LOG.error("error sending response");
        }
    }

    private static StandaloneAgentRequest getRequest(InputStream inputStream) throws SAXException, ParserConfigurationException {
        try {
        	//Source: https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Prevention_Cheat_Sheet#Unmarshaller
        	SAXParserFactory spf = SAXParserFactory.newInstance();
        	spf.setNamespaceAware(true);
        	spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        	spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        	spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        	
        	Source xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(inputStream));
        	
            JAXBContext ctx = JAXBContext.newInstance(StandaloneAgentRequest.class.getPackage().getName());
            Object unmarshalled = ctx.createUnmarshaller().unmarshal(xmlSource);
            return (StandaloneAgentRequest) unmarshalled;
        } catch (JAXBException e) {
            LOG.error("Error unmarshalling body.");
        }
        return null;
    }

}

package com.intuit.tank.agent;

import com.intuit.tank.harness.AmazonUtil;
import com.intuit.tank.vm.common.TankConstants;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import static com.intuit.tank.agent.AgentStartup.API_HARNESS_COMMAND;
import static com.intuit.tank.agent.AgentStartup.TANK_AGENT_DIR;

public class AgentStartupServer implements  Runnable {

    private static final Logger logger = LogManager.getLogger(AgentStartupServer.class);
    private final int port;
    private final String token;

    public AgentStartupServer(int port, String token) {
        this.port = port;
        this.token = token;
    }

    @Override
    public void run() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/agentStartup", new AgentStartupHandler());
            server.start();
            logger.info("Server is running on port {}", port);
        } catch (IOException e) {
            logger.error("Error starting the server: {}", e.getMessage());
        }
    }

    // Custom HttpHandler to process requests
    class AgentStartupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "Invalid request method!";
            boolean authenticated = exchange.getRequestHeaders().get("Authentication").stream()
                    .anyMatch(value -> value.equals("Bearer " + token));
            if (exchange.getRequestMethod().equals("PUT") && authenticated) {
                try (ZipInputStream zip = new ZipInputStream(exchange.getRequestBody())) {
                    ZipEntry entry = zip.getNextEntry();
                    while (entry != null) {
                        String filename = entry.getName();
                        logger.info("Got file from controller: {}", filename);
                        Path targetPath = Paths.get(TANK_AGENT_DIR).resolve(filename).toRealPath();
                        if (!targetPath.startsWith(TANK_AGENT_DIR)) // Protect "Zip Slip"
                            throw new ZipException("Bad zip entry");
                        Files.write(targetPath, zip.readAllBytes());
                        entry = zip.getNextEntry();
                    }
                    response = "Controller payload successfully received!";
                } catch (EOFException | ZipException e) {
                    logger.error("Error unzipping support files : {}", e.getMessage());
                    response = "Controller payload failed to receive!";
                }
                // Send response headers (status code 200 OK, and content length)
                exchange.sendResponseHeaders(200, response.length());
                // now start the harness
                String controllerArg = " -http=" + AmazonUtil.getControllerBaseUrl();
                String jvmArgs = AmazonUtil.getUserDataAsMap().get(TankConstants.KEY_JVM_ARGS);
                logger.info("Starting apiharness with command: {} {} {}",
                        API_HARNESS_COMMAND, controllerArg, jvmArgs);
                Runtime.getRuntime().exec(API_HARNESS_COMMAND + controllerArg + " " + jvmArgs);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close(); // Close the output stream
                System.exit(0);
            }
            // handle all other requests
            exchange.sendResponseHeaders(405, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close(); // Close the output stream
        }
    }
}

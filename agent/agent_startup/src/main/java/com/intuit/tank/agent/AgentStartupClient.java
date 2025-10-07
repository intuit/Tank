package com.intuit.tank.agent;

import com.intuit.tank.harness.AmazonUtil;
import com.intuit.tank.vm.common.TankConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.EOFException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import static com.intuit.tank.agent.AgentStartup.API_HARNESS_COMMAND;
import static com.intuit.tank.agent.AgentStartup.TANK_AGENT_DIR;

public class AgentStartupClient implements Runnable {
    private static final Logger logger = LogManager.getLogger(AgentStartupClient.class);
    private static final String SERVICE_RELATIVE_PATH = "/v2/agent";
    private static final String METHOD_SETTINGS = "/settings";
    private static final String METHOD_SUPPORT = "/support-files";
    private static final int[] FIBONACCI = new int[] { 1, 1, 2, 3, 5, 8, 13 };


    private final String controllerBaseUrl;
    private final String token;

    public AgentStartupClient(String controllerBaseUrl, String token ) {
        this.controllerBaseUrl = controllerBaseUrl;
        this.token = token;
    }

    @Override
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
            client.send(request, HttpResponse.BodyHandlers.ofFile(Paths.get(TANK_AGENT_DIR, "settings.xml")));
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
                        client.send(request, HttpResponse.BodyHandlers.ofInputStream()).body())) {
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
            Runtime.getRuntime().exec(API_HARNESS_COMMAND + controllerArg + " " + jvmArgs);
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
}

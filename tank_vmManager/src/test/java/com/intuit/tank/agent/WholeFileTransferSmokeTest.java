package com.intuit.tank.agent;

import com.intuit.tank.perfManager.workLoads.ControllerInitiatedAgentWsClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Real Jetty on both ends: the controller WS client pushes a harness-JAR-sized file over h2c to the agent's
 * StartupWebSocketServer as one message, and the agent must write it out byte for byte.
 */
public class WholeFileTransferSmokeTest {

    @TempDir
    Path tempDir;

    @Test
    void testControllerPushesHarnessSizedJarToRealAgentReceiverAsOneMessage() throws Exception {
        byte[] jar = new byte[41 * 1024 * 1024 + 7];
        new Random(8441).nextBytes(jar);

        Path toolsDir = tempDir.resolve("catalina/webapps/ROOT/tools");
        Files.createDirectories(toolsDir);
        Files.write(toolsDir.resolve("apiharness-1.0-all.jar"), new byte[] { 1 });
        Path agentDir = Files.createDirectories(tempDir.resolve("agent"));

        int port;
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        StartupWebSocketServer agent = new StartupWebSocketServer(port, "i-smoke", "bootstrap", 1, agentDir.toFile());
        agent.start();

        String previousCatalinaHome = System.getProperty("catalina.home");
        System.setProperty("catalina.home", tempDir.resolve("catalina").toString());
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        setField(client, "cachedHarnessJarBytes", jar);
        setField(client, "cachedStartupScript", Optional.empty());

        try {
            client.connectAndBootstrap(null, "i-smoke", "http://localhost:" + port,
                    "ws://localhost:" + port + "/", "token", 10_000, 60_000);

            File received = agent.awaitHarnessJar(60_000);
            assertNotNull(received, "agent never completed the harness JAR");
            assertArrayEquals(jar, Files.readAllBytes(received.toPath()));
        } finally {
            client.disconnect("i-smoke");
            agent.stop(5_000);
            if (previousCatalinaHome == null) {
                System.clearProperty("catalina.home");
            } else {
                System.setProperty("catalina.home", previousCatalinaHome);
            }
        }
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}

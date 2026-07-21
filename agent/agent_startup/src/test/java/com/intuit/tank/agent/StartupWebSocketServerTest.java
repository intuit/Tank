package com.intuit.tank.agent;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StartupWebSocketServerTest {

    @TempDir
    Path agentDir;

    @Test
    void receivesCustomStartupScriptBeforeCompletingHarnessBootstrap() throws Exception {
        StartupWebSocketServer server =
                new StartupWebSocketServer(0, "i-agent", "job-1", 1, agentDir.toFile());
        Session session = sessionThatCompletesCallbacks();
        byte[] script = "#!/bin/sh\necho custom\n".getBytes(StandardCharsets.UTF_8);

        server.handleFileOffer(session, fileOffer(
                "script-id", "startup_script", "startAgent.sh", script.length));
        server.handleFileChunk(session, "script-id", 0, script);

        Path receivedScript = agentDir.resolve("startAgent.sh");
        assertArrayEquals(script, Files.readAllBytes(receivedScript));
        assertTrue(Files.isExecutable(receivedScript));
        assertFalse(isHarnessJarReady(server));

        byte[] harnessJar = new byte[] { 1, 2, 3 };
        server.handleFileOffer(session, fileOffer(
                "jar-id", "support_jar", "apiharness-1.0-all.jar", harnessJar.length));
        server.handleFileChunk(session, "jar-id", 0, harnessJar);

        File receivedHarness = server.awaitHarnessJar(100);
        assertEquals(agentDir.resolve("apiharness-1.0-all.jar").toFile(), receivedHarness);
        assertArrayEquals(harnessJar, Files.readAllBytes(receivedHarness.toPath()));
    }

    @Test
    void rejectsMismatchedStartupFileTypeAndName() {
        StartupWebSocketServer server =
                new StartupWebSocketServer(0, "i-agent", "job-1", 1, agentDir.toFile());
        Session session = mock(Session.class);

        server.handleFileOffer(session, fileOffer(
                "script-id", "support_jar", "startAgent.sh", 10));

        verify(session).sendText(contains("unsupported_startup_file"), any(Callback.class));
    }

    @Test
    void keepsPackagedStartupScriptWhenControllerOnlySendsHarnessJar() throws Exception {
        byte[] packagedScript = "#!/bin/sh\necho packaged\n".getBytes(StandardCharsets.UTF_8);
        Path startupScript = agentDir.resolve("startAgent.sh");
        Files.write(startupScript, packagedScript);
        startupScript.toFile().setExecutable(true, false);
        StartupWebSocketServer server =
                new StartupWebSocketServer(0, "i-agent", "job-1", 1, agentDir.toFile());
        Session session = sessionThatCompletesCallbacks();
        byte[] harnessJar = new byte[] { 1, 2, 3 };

        server.handleFileOffer(session, fileOffer(
                "jar-id", "support_jar", "apiharness-1.0-all.jar", harnessJar.length));
        server.handleFileChunk(session, "jar-id", 0, harnessJar);

        assertArrayEquals(packagedScript, Files.readAllBytes(startupScript));
        assertTrue(Files.isExecutable(startupScript));
        assertEquals(agentDir.resolve("apiharness-1.0-all.jar").toFile(), server.awaitHarnessJar(100));
    }

    private AgentWsEnvelope fileOffer(String fileId, String fileType, String fileName, int totalBytes) {
        return AgentWsEnvelope.fileOffer(
                "i-agent", "bootstrap", fileId, fileType, fileName, totalBytes, 1, totalBytes, false);
    }

    private Session sessionThatCompletesCallbacks() {
        Session session = mock(Session.class);
        doAnswer(invocation -> {
            Callback callback = invocation.getArgument(1);
            callback.succeed();
            return null;
        }).when(session).sendText(anyString(), any(Callback.class));
        return session;
    }

    private boolean isHarnessJarReady(StartupWebSocketServer server) throws Exception {
        Field field = StartupWebSocketServer.class.getDeclaredField("harnessJarFuture");
        field.setAccessible(true);
        return ((CompletableFuture<?>) field.get(server)).isDone();
    }
}

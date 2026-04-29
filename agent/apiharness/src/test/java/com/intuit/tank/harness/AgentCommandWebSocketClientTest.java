package com.intuit.tank.harness;

import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.http.WebSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AgentCommandWebSocketClientTest {

    @TempDir
    Path tempDir;

    @Test
    void testCommandAckAndDedup() throws Exception {
        AgentCommandWebSocketClient client = newClient(false);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);

        try (MockedStatic<CommandListener> commandListener = Mockito.mockStatic(CommandListener.class)) {
            String command = AgentWsEnvelope.command("cmd-1", "i-1", "job-1", "start").toJson();
            client.onText(webSocket, command, true);
            client.onText(webSocket, command, true);

            commandListener.verify(() -> CommandListener.applyCommand("start"), times(1));

            List<AgentWsEnvelope> acks = parseFileAndCommandAcks(sentFrames);
            assertEquals(2, acks.size());
            assertEquals(AgentWsEnvelope.AckStatus.ok, acks.get(0).getStatus());
            assertEquals(AgentWsEnvelope.AckStatus.duplicate, acks.get(1).getStatus());
            assertEquals("cmd-1", acks.get(0).getAckForId());
            assertEquals("cmd-1", acks.get(1).getAckForId());
        } finally {
            client.close();
        }
    }

    @Test
    void testCommandFailureAckFailed() throws Exception {
        AgentCommandWebSocketClient client = newClient(false);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);

        try (MockedStatic<CommandListener> commandListener = Mockito.mockStatic(CommandListener.class)) {
            commandListener.when(() -> CommandListener.applyCommand("start"))
                    .thenThrow(new RuntimeException("boom"));

            client.onText(webSocket, AgentWsEnvelope.command("cmd-fail", "i-1", "job-1", "start").toJson(), true);

            List<AgentWsEnvelope> acks = parseFileAndCommandAcks(sentFrames);
            assertEquals(1, acks.size());
            assertEquals(AgentWsEnvelope.AckStatus.failed, acks.get(0).getStatus());
            assertEquals("cmd-fail", acks.get(0).getAckForId());
        } finally {
            client.close();
        }
    }

    @Test
    void testFragmentedTextFrameHandling() throws Exception {
        AgentCommandWebSocketClient client = newClient(false);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);

        try (MockedStatic<CommandListener> commandListener = Mockito.mockStatic(CommandListener.class)) {
            String json = AgentWsEnvelope.command("cmd-frag", "i-1", "job-1", "pause").toJson();
            int split = json.length() / 2;

            client.onText(webSocket, json.substring(0, split), false);
            client.onText(webSocket, json.substring(split), true);

            commandListener.verify(() -> CommandListener.applyCommand("pause"), times(1));
            List<AgentWsEnvelope> acks = parseFileAndCommandAcks(sentFrames);
            assertEquals(1, acks.size());
            assertEquals(AgentWsEnvelope.AckStatus.ok, acks.get(0).getStatus());
        } finally {
            client.close();
        }
    }

    @Test
    void testPingPongHandling() throws Exception {
        AgentCommandWebSocketClient client = newClient(false);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);

        try {
            client.onText(webSocket, AgentWsEnvelope.ping("ping-1").toJson(), true);

            assertEquals(1, sentFrames.size());
            AgentWsEnvelope pong = AgentWsEnvelope.fromJson(sentFrames.get(0));
            assertEquals(AgentWsEnvelope.Type.pong, pong.getType());
            assertEquals("ping-1", pong.getPingId());
        } finally {
            client.close();
        }
    }

    @Test
    void testFileChunkWithoutOfferSendsFailureAck() throws Exception {
        AgentCommandWebSocketClient client = newClient(true);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);

        try {
            String payload = Base64.getEncoder().encodeToString("hello".getBytes(StandardCharsets.UTF_8));
            client.onText(webSocket, AgentWsEnvelope.fileChunk("i-1", "job-1", "missing-file", 0, payload).toJson(), true);

            assertEquals(1, sentFrames.size());
            AgentWsEnvelope ack = AgentWsEnvelope.fromJson(sentFrames.get(0));
            assertEquals(AgentWsEnvelope.Type.file_ack, ack.getType());
            assertEquals(AgentWsEnvelope.AckStatus.failed, ack.getStatus());
            assertEquals("file_offer_not_found", ack.getError());
        } finally {
            client.close();
        }
    }

    @Test
    void testFileTransferLifecycleCompletes() throws Exception {
        AgentCommandWebSocketClient client = newClient(true);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);
        String fileId = "file-1";
        String filePath = tempDir.resolve("transfer-" + UUID.randomUUID() + ".txt").toString();
        byte[] payloadBytes = "payload-123".getBytes(StandardCharsets.UTF_8);

        try {
            AgentTestStartData startData = buildStartData();
            client.onText(webSocket, AgentWsEnvelope.jobConfig("i-1", "job-1", startData, 1).toJson(), true);
            client.onText(webSocket,
                    AgentWsEnvelope.fileOffer("i-1", "job-1", fileId, "custom", filePath,
                            payloadBytes.length, 1, false).toJson(), true);
            client.onText(webSocket,
                    AgentWsEnvelope.fileChunk("i-1", "job-1", fileId, 0,
                            Base64.getEncoder().encodeToString(payloadBytes)).toJson(), true);

            File target = new File(filePath);
            assertTrue(target.exists());
            assertEquals("payload-123", Files.readString(target.toPath()));

            List<AgentWsEnvelope.AckStatus> statuses = parseFileAckStatuses(sentFrames);
            assertEquals(List.of(
                    AgentWsEnvelope.AckStatus.chunk_received,
                    AgentWsEnvelope.AckStatus.complete,
                    AgentWsEnvelope.AckStatus.all_files_complete
            ), statuses);
        } finally {
            client.close();
        }
    }

    @Test
    void testConnectWithRetryResetsTransferState() throws Exception {
        AgentCommandWebSocketClient client = newClient(true);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);
        String filePath = tempDir.resolve("stale-" + UUID.randomUUID() + ".txt").toString();

        try {
            AgentTestStartData startData = buildStartData();
            client.onText(webSocket, AgentWsEnvelope.jobConfig("i-1", "job-1", startData, 10).toJson(), true);
            client.onText(webSocket,
                    AgentWsEnvelope.fileOffer("i-1", "job-1", "stale-file", "custom", filePath,
                            4, 1, false).toJson(), true);

            CompletableFuture<AgentTestStartData> oldJobConfigFuture = getField(client, "jobConfigFuture");
            CompletableFuture<Void> oldTransferFuture = getField(client, "transferCompleteFuture");
            oldJobConfigFuture.complete(startData);
            oldTransferFuture.complete(null);

            AtomicInteger completedFiles = getField(client, "completedFiles");
            completedFiles.set(3);
            setField(client, "expectedFiles", 99);
            setField(client, "receivedJobConfig", startData);

            AtomicBoolean running = getField(client, "running");
            running.set(false);
            invokeNoArg(client, "connectWithRetry");

            CompletableFuture<AgentTestStartData> newJobConfigFuture = getField(client, "jobConfigFuture");
            CompletableFuture<Void> newTransferFuture = getField(client, "transferCompleteFuture");
            assertNotSame(oldJobConfigFuture, newJobConfigFuture);
            assertNotSame(oldTransferFuture, newTransferFuture);
            assertFalse(newJobConfigFuture.isDone());
            assertFalse(newTransferFuture.isDone());

            ConcurrentHashMap<String, ?> incomingFiles = getField(client, "incomingFiles");
            assertTrue(incomingFiles.isEmpty());
            assertEquals(0, completedFiles.get());
            assertEquals(-1, (int) getField(client, "expectedFiles"));
            assertNull(getField(client, "receivedJobConfig"));
            assertFalse(new File(filePath + ".part").exists());
        } finally {
            client.close();
        }
    }

    @Test
    void testIdleConnectionTriggersAbort() throws Exception {
        AgentCommandWebSocketClient client = newClient(false);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);

        try {
            AtomicReference<WebSocket> ref = getField(client, "webSocketRef");
            ref.set(webSocket);
            setField(client, "lastFrameReceivedAt", System.currentTimeMillis() - 95_000L);

            invokeNoArg(client, "checkIdleConnection");
            verify(webSocket, atLeastOnce()).abort();
            assertNull(ref.get());
        } finally {
            client.close();
        }
    }

    @Test
    void testAwaitInitialTransferDisabled() {
        AgentCommandWebSocketClient client = newClient(false);
        try {
            assertTrue(client.awaitInitialTransfer(10));
        } finally {
            client.close();
        }
    }

    @Test
    void testAwaitInitialTransferSuccess() throws Exception {
        AgentCommandWebSocketClient client = newClient(true);
        List<String> sentFrames = new ArrayList<>();
        WebSocket webSocket = mockWebSocket(sentFrames);

        try {
            AgentTestStartData startData = buildStartData();
            client.onText(webSocket, AgentWsEnvelope.jobConfig("i-1", "job-1", startData, 0).toJson(), true);

            assertTrue(client.awaitInitialTransfer(200));
            assertNotNull(client.getReceivedJobConfig());
        } finally {
            client.close();
        }
    }

    @Test
    void testAwaitInitialTransferTimeout() {
        AgentCommandWebSocketClient client = newClient(true);
        try {
            assertFalse(client.awaitInitialTransfer(10));
        } finally {
            client.close();
        }
    }

    private AgentCommandWebSocketClient newClient(boolean fileTransferEnabled) {
        return new AgentCommandWebSocketClient(
                "http://127.0.0.1:1",
                "/v2/agent/ws/control",
                "token",
                "i-1",
                "job-1",
                5,
                fileTransferEnabled);
    }

    private AgentTestStartData buildStartData() {
        AgentTestStartData startData = new AgentTestStartData("script.xml", 1, 1L);
        startData.setDataFiles(new DataFileRequest[0]);
        return startData;
    }

    private WebSocket mockWebSocket(List<String> sentFrames) {
        WebSocket webSocket = mock(WebSocket.class);
        when(webSocket.sendText(any(CharSequence.class), eq(true))).thenAnswer(invocation -> {
            sentFrames.add(invocation.getArgument(0).toString());
            return CompletableFuture.completedFuture(webSocket);
        });
        when(webSocket.sendClose(anyInt(), anyString())).thenReturn(CompletableFuture.completedFuture(webSocket));
        return webSocket;
    }

    private List<AgentWsEnvelope.AckStatus> parseFileAckStatuses(List<String> sentFrames) throws Exception {
        List<AgentWsEnvelope.AckStatus> statuses = new ArrayList<>();
        for (String frame : sentFrames) {
            AgentWsEnvelope env = AgentWsEnvelope.fromJson(frame);
            if (env.getType() == AgentWsEnvelope.Type.file_ack) {
                statuses.add(env.getStatus());
            }
        }
        return statuses;
    }

    private List<AgentWsEnvelope> parseFileAndCommandAcks(List<String> sentFrames) throws Exception {
        List<AgentWsEnvelope> acks = new ArrayList<>();
        for (String frame : sentFrames) {
            AgentWsEnvelope env = AgentWsEnvelope.fromJson(frame);
            if (env.getType() == AgentWsEnvelope.Type.ack || env.getType() == AgentWsEnvelope.Type.file_ack) {
                acks.add(env);
            }
        }
        return acks;
    }

    private void invokeNoArg(Object target, String methodName) throws Exception {
        Method method = target.getClass().getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(target);
    }

    @SuppressWarnings("unchecked")
    private <T> T getField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testConstructorSetsFields() {
        AgentCommandWebSocketClient client = new AgentCommandWebSocketClient(
                "http://controller.example.com/tank",
                "/v2/agent/ws/control",
                "test-token",
                "i-123",
                "job-1"
        );
        assertNotNull(client);
    }

    @Test
    void testConstructorWithHttpsUrl() {
        AgentCommandWebSocketClient client = new AgentCommandWebSocketClient(
                "https://controller.example.com/tank",
                "/v2/agent/ws/control",
                "test-token",
                "i-456",
                "job-2"
        );
        assertNotNull(client);
    }

    @Test
    void testCloseBeforeConnect() {
        AgentCommandWebSocketClient client = new AgentCommandWebSocketClient(
                "http://localhost:8080/tank",
                "/v2/agent/ws/control",
                "token",
                "i-789",
                "job-3"
        );
        // Should not throw
        client.close();
    }
}

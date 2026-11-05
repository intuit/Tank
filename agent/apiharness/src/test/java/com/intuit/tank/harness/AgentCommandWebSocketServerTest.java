package com.intuit.tank.harness;

import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

public class AgentCommandWebSocketServerTest {

    private static final String TOKEN = "agent-token";
    private static final String NOT_BOOTSTRAP = "not_bootstrap_connection";

    private AgentCommandWebSocketServer server;
    private int port;

    @AfterEach
    public void tearDown() {
        if (server != null) {
            server.closeServer();
        }
    }

    private void startServer(String token, Long silenceTimeoutMs) throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        server = new AgentCommandWebSocketServer(port, "i-test", "42", 100, token);
        if (silenceTimeoutMs != null) {
            server.setSilenceTimeoutMs(silenceTimeoutMs);
        }
        server.start();
    }

    private TestController connect(String node, String authorization) throws Exception {
        TestController controller = new TestController();
        WebSocket.Builder builder = HttpClient.newHttpClient().newWebSocketBuilder();
        if (authorization != null) {
            builder.header("Authorization", authorization);
        }
        String query = node != null ? "?node=" + node : "";
        controller.socket = builder.buildAsync(URI.create("ws://localhost:" + port + "/ws/control" + query), controller)
                .get(5, TimeUnit.SECONDS);
        assertEquals(AgentWsEnvelope.Type.hello, controller.next().getType());
        return controller;
    }

    private TestController connect(String node) throws Exception {
        return connect(node, "bearer " + TOKEN);
    }

    @Test
    public void controllersOnDifferentNodesStayConnectedAndAllGetStatus() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        TestController b = connect("b");

        assertTrue(server.sendStatusUpdate(null));

        assertEquals(AgentWsEnvelope.Type.status_update, a.next().getType());
        assertEquals(AgentWsEnvelope.Type.status_update, b.next().getType());
        assertFalse(a.closed.isDone());
    }

    @Test
    public void reconnectFromSameNodeReplacesOnlyThatNode() throws Exception {
        startServer(TOKEN, null);
        TestController a1 = connect("a");
        TestController b = connect("b");
        TestController a2 = connect("a");

        a1.closed.get(5, TimeUnit.SECONDS);
        assertTrue(server.sendStatusUpdate(null));
        assertEquals(AgentWsEnvelope.Type.status_update, a2.next().getType());
        assertEquals(AgentWsEnvelope.Type.status_update, b.next().getType());
    }

    @Test
    public void rejectsUpgradeWithoutTheAgentToken() throws Exception {
        startServer(TOKEN, null);

        assertThrows(ExecutionException.class, () -> connect("a", null));
        assertThrows(ExecutionException.class, () -> connect("a", "bearer wrong"));
        assertNotNull(connect("a", "Bearer " + TOKEN));
    }

    @Test
    public void acceptsAnyUpgradeWhenNoTokenIsConfigured() throws Exception {
        startServer(null, null);

        assertNotNull(connect(null, null));
    }

    @Test
    public void closesConnectionsThatGoSilent() throws Exception {
        startServer(TOKEN, 600L);
        TestController silent = connect("a");
        TestController pinging = connect("b");

        long deadline = System.currentTimeMillis() + 1_500;
        while (System.currentTimeMillis() < deadline) {
            pinging.send(AgentWsEnvelope.ping("p"));
            assertEquals(AgentWsEnvelope.Type.pong, pinging.next().getType());
            Thread.sleep(100);
        }

        silent.closed.get(5, TimeUnit.SECONDS);
        assertFalse(pinging.closed.isDone());
    }

    @Test
    public void startAndJobConfigComeOnlyFromTheBootstrapConnection() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        TestController b = connect("b");

        // a becomes the bootstrap connection; start then fails only because bootstrap isn't finished.
        a.send(AgentWsEnvelope.command("c1", "i-test", "42", "start"));
        assertEquals("bootstrap_not_ready", a.next().getError());

        b.send(AgentWsEnvelope.command("c2", "i-test", "42", "start"));
        assertEquals("not_bootstrap_connection", b.next().getError());

        b.send(AgentWsEnvelope.jobConfig("i-test", "42", new com.intuit.tank.vm.agent.messages.AgentTestStartData(), 0));
        b.send(AgentWsEnvelope.ping("p"));
        assertEquals(AgentWsEnvelope.Type.pong, b.next().getType());
        assertNull(server.getReceivedJobConfig());

        // Once a closes, b can take the role.
        a.socket.sendClose(WebSocket.NORMAL_CLOSURE, "done").get(5, TimeUnit.SECONDS);
        a.closed.get(5, TimeUnit.SECONDS);
        Thread.sleep(200);
        b.send(AgentWsEnvelope.command("c3", "i-test", "42", "start"));
        assertEquals("bootstrap_not_ready", b.next().getError());
    }

    @Test
    public void commandsOtherThanStartWorkFromAnyConnection() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        TestController b = connect("b");

        a.send(AgentWsEnvelope.command("c1", "i-test", "42", "start"));
        assertEquals("bootstrap_not_ready", a.next().getError());

        // The harness isn't running here, so send a command that reaches CommandListener and fails there
        // rather than one that drives the harness.
        b.send(AgentWsEnvelope.command("c2", "i-test", "42", "unknown"));
        AgentWsEnvelope ack = b.next();
        assertEquals(AgentWsEnvelope.Type.ack, ack.getType());
        assertEquals("Unknown command: unknown", ack.getError());
    }

    @Test
    public void killIsAcknowledgedBeforeTheAgentExits() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        CompletableFuture<Void> killed = new CompletableFuture<>();
        Runnable original = CommandListener.killAction;
        // Stand in for System.exit: tear the server down immediately, as the dying JVM would.
        CommandListener.killAction = () -> {
            server.closeServer();
            killed.complete(null);
        };
        try {
            a.send(AgentWsEnvelope.command("k1", "i-test", "42", "kill"));

            AgentWsEnvelope ack = a.next();
            assertEquals(AgentWsEnvelope.Type.ack, ack.getType());
            assertEquals(AgentWsEnvelope.AckStatus.ok, ack.getStatus());
            killed.get(5, TimeUnit.SECONDS);
        } finally {
            CommandListener.killAction = original;
        }
    }

    @Test
    public void queryParamReadsNamedParameter() {
        assertEquals("node-1", AgentCommandWebSocketServer.queryParam("x=1&node=node-1", "node"));
        assertEquals("a b", AgentCommandWebSocketServer.queryParam("node=a%20b", "node"));
        assertNull(AgentCommandWebSocketServer.queryParam("node=", "node"));
        assertNull(AgentCommandWebSocketServer.queryParam(null, "node"));
    }

    @Test
    public void queryParamIgnoresOtherParameters() {
        assertNull(AgentCommandWebSocketServer.queryParam("x=1&=2&flag", "node"));
    }

    @Test
    public void startFailsWhenThePortIsTaken() throws Exception {
        try (ServerSocket taken = new ServerSocket(0)) {
            server = new AgentCommandWebSocketServer(taken.getLocalPort(), "i-test", "42", 100);
            server.setReuseAddr(false);

            IOException e = assertThrows(IOException.class, () -> server.start());
            assertTrue(e.getMessage().contains(String.valueOf(taken.getLocalPort())));
        }
    }

    @Test
    public void sendStatusUpdateReturnsFalseWithNoConnections() throws Exception {
        startServer(TOKEN, null);

        assertFalse(server.sendStatusUpdate(null));
    }

    @Test
    public void statusUpdateCarriesTheStatusJobId() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        CloudVmStatus status = new CloudVmStatus("i-test", "99", null, JobStatus.Running, null, null, null,
                VMStatus.running, new ValidationStatus(), 10, 5, null, null);

        assertTrue(server.sendStatusUpdate(status));

        AgentWsEnvelope update = a.next();
        assertEquals(AgentWsEnvelope.Type.status_update, update.getType());
        assertEquals("99", update.getJobId());
    }

    @Test
    public void ignoresFramesItCannotUse() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");

        // None of these get a reply; the pong that follows proves the connection is still served.
        a.sendText("not json");
        a.sendText("{}");
        a.send(AgentWsEnvelope.ack("ctrl", "status_update", "s1", AckStatus.ok));
        a.send(AgentWsEnvelope.pong("ctrl", "session", "p0", null));
        a.send(AgentWsEnvelope.ping("p1"));

        AgentWsEnvelope pong = a.next();
        assertEquals(AgentWsEnvelope.Type.pong, pong.getType());
        assertEquals("p1", pong.getPingId());
        assertFalse(a.closed.isDone());
    }

    @Test
    public void closeFrameFromTheControllerClosesTheConnection() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");

        a.send(AgentWsEnvelope.close("ctrl", "controller_shutdown", "bye"));

        a.closed.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void closeServerTellsControllersItIsShuttingDown() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");

        server.closeServer();

        AgentWsEnvelope close = a.next();
        assertEquals(AgentWsEnvelope.Type.close, close.getType());
        assertEquals("agent_shutdown", close.getReasonCode());
        a.closed.get(5, TimeUnit.SECONDS);
    }

    @Test
    public void repeatedCommandIdIsAcknowledgedAsDuplicate() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");

        a.send(AgentWsEnvelope.command("c1", "i-test", "42", "unknown"));
        assertEquals(AckStatus.failed, a.next().getStatus());

        a.send(AgentWsEnvelope.command("c1", "i-test", "42", "unknown"));
        AgentWsEnvelope ack = a.next();
        assertEquals(AckStatus.duplicate, ack.getStatus());
        assertEquals("c1", ack.getAckForId());
    }

    @Test
    public void awaitInitialTransferTimesOutWithoutJobConfig() throws Exception {
        startServer(TOKEN, null);

        assertFalse(server.awaitInitialTransfer(50));
    }

    @Test
    public void jobConfigWithoutPayloadFailsTheInitialTransfer() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");

        a.send(AgentWsEnvelope.jobConfig("i-test", "42", null, 0));

        assertFalse(server.awaitInitialTransfer(5_000));
        assertNull(server.getReceivedJobConfig());
    }

    @Test
    public void jobConfigExpectingNoFilesCompletesTheInitialTransfer() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        AgentTestStartData jobConfig = new AgentTestStartData();
        jobConfig.setJobId("42");

        a.send(AgentWsEnvelope.jobConfig("i-test", "42", jobConfig, 0));

        assertTrue(server.awaitInitialTransfer(5_000));
        assertEquals("42", server.getReceivedJobConfig().getJobId());
    }

    @Test
    public void receivesFilesAndAcksAllFilesCompleteOnceBootstrapIsReady(@TempDir Path dir) throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        // No expectedFiles: the agent expects settings, script and support files, plus any data files.
        a.send(AgentWsEnvelope.jobConfig("i-test", "42", new AgentTestStartData(), null));

        // 1: one text chunk, flagged as the default data file so it is also copied to csv-data.txt.
        File first = dir.resolve("first.csv").toFile();
        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "f1", "other", first.getAbsolutePath(), 5, 1, 5, true));
        assertFileAck(a.next(), "f1", 0, AckStatus.ok);
        a.send(AgentWsEnvelope.fileChunk("i-test", "42", "f1", 0, base64("hello")));
        assertFileAck(a.next(), "f1", 0, AckStatus.chunk_received);
        assertFileAck(a.next(), "f1", 0, AckStatus.complete);
        assertEquals("hello", Files.readString(first.toPath()));
        assertEquals("hello", Files.readString(dir.resolve(TankConstants.DEFAULT_CSV_FILE_NAME)));
        assertFalse(new File(first.getAbsolutePath() + ".part").exists());

        // 2: two binary chunks into a directory that doesn't exist yet.
        File second = dir.resolve("nested/second.bin").toFile();
        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "f2", null, second.getAbsolutePath(), 6, 2, 3, null));
        assertFileAck(a.next(), "f2", 0, AckStatus.ok);
        a.sendBinary(AgentWsEnvelope.binaryFileChunk("f2", 0, new byte[]{1, 2, 3}, 0, 3));
        assertFileAck(a.next(), "f2", 0, AckStatus.chunk_received);
        a.sendBinary(AgentWsEnvelope.binaryFileChunk("f2", 1, new byte[]{4, 5, 6}, 0, 3));
        assertFileAck(a.next(), "f2", 1, AckStatus.chunk_received);
        assertFileAck(a.next(), "f2", 1, AckStatus.complete);
        assertArrayEquals(new byte[]{1, 2, 3, 4, 5, 6}, Files.readAllBytes(second.toPath()));

        // 3: streamed with unknown size and chunk count; an empty chunk ends it.
        File third = dir.resolve("third.txt").toFile();
        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "f3", "other", third.getAbsolutePath(), -1, -1, 3, false));
        assertFileAck(a.next(), "f3", 0, AckStatus.ok);
        a.send(AgentWsEnvelope.fileChunk("i-test", "42", "f3", 0, base64("abc")));
        assertFileAck(a.next(), "f3", 0, AckStatus.chunk_received);
        a.send(AgentWsEnvelope.fileChunk("i-test", "42", "f3", 1, ""));
        assertFileAck(a.next(), "f3", 1, AckStatus.complete);
        assertEquals("abc", Files.readString(third.toPath()));

        assertTrue(server.awaitInitialTransfer(5_000));
        assertNotNull(server.getReceivedJobConfig());

        // all_files_complete waits for the agent to finish bootstrapping, then names the last chunk.
        a.send(AgentWsEnvelope.ping("p1"));
        assertEquals(AgentWsEnvelope.Type.pong, a.next().getType());
        server.markInitialBootstrapReady();
        assertFileAck(a.next(), "f3", 1, AckStatus.all_files_complete);
    }

    @Test
    public void reofferReplacesTheEarlierTransferOfThatFile(@TempDir Path dir) throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        File first = dir.resolve("first.txt").toFile();
        File second = dir.resolve("second.txt").toFile();

        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "f1", "other", first.getAbsolutePath(), 2, 1, 2, false));
        assertFileAck(a.next(), "f1", 0, AckStatus.ok);
        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "f1", "other", second.getAbsolutePath(), 2, 1, 2, false));
        assertFileAck(a.next(), "f1", 0, AckStatus.ok);
        a.send(AgentWsEnvelope.fileChunk("i-test", "42", "f1", 0, base64("ok")));
        assertFileAck(a.next(), "f1", 0, AckStatus.chunk_received);
        assertFileAck(a.next(), "f1", 0, AckStatus.complete);

        assertEquals("ok", Files.readString(second.toPath()));
        assertFalse(first.exists());
    }

    @Test
    public void dataFilesGoToTheAgentDataDirectory() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        String name = "ws-data-" + UUID.randomUUID() + ".csv";
        File expected = new File(new TankConfig().getAgentConfig().getAgentDataFileStorageDir(), name);
        try {
            a.send(AgentWsEnvelope.fileOffer("i-test", "42", "d1", "data", name, 3, 1, 3, false));
            assertFileAck(a.next(), "d1", 0, AckStatus.ok);
            a.send(AgentWsEnvelope.fileChunk("i-test", "42", "d1", 0, base64("a,b")));
            assertFileAck(a.next(), "d1", 0, AckStatus.chunk_received);
            assertFileAck(a.next(), "d1", 0, AckStatus.complete);

            assertEquals("a,b", Files.readString(expected.toPath()));
        } finally {
            Files.deleteIfExists(expected.toPath());
        }
    }

    @Test
    public void offerFailsWhenTheTargetDirectoryCannotBeCreated() throws Exception {
        // Settings and support archives always land in /opt/tank_agent; only run where that can't be created.
        assumeFalse(new File("/opt/tank_agent").exists() || new File("/opt").canWrite());
        startServer(TOKEN, null);
        TestController a = connect("a");

        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "s1", "settings", "settings.xml", 1, 1, 1, false));
        AgentWsEnvelope settingsAck = a.next();
        assertFileAck(settingsAck, "s1", 0, AckStatus.failed);
        assertTrue(settingsAck.getError().startsWith("Unable to create dir"));

        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "z1", "support_archive", " ", 1, 1, 1, false));
        AgentWsEnvelope archiveAck = a.next();
        assertFileAck(archiveAck, "z1", 0, AckStatus.failed);
        assertTrue(archiveAck.getError().startsWith("Unable to create dir"));
    }

    @Test
    public void offerWithoutAFileNameIsIgnored() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");

        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "f1", "other", null, 1, 1, 1, false));
        a.send(AgentWsEnvelope.ping("p1"));

        assertEquals(AgentWsEnvelope.Type.pong, a.next().getType());
    }

    @Test
    public void chunksThatCannotBeWrittenAreRejected(@TempDir Path dir) throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");

        a.send(AgentWsEnvelope.fileChunk("i-test", "42", "missing", 0, base64("x")));
        AgentWsEnvelope notFound = a.next();
        assertFileAck(notFound, "missing", 0, AckStatus.failed);
        assertEquals("file_offer_not_found", notFound.getError());

        a.send(AgentWsEnvelope.fileChunk("i-test", "42", "f1", 0, "not base64!"));
        AgentWsEnvelope badData = a.next();
        assertFileAck(badData, "f1", 0, AckStatus.failed);
        assertNotNull(badData.getError());

        // The offer promised 10 bytes in one chunk, so a 5-byte chunk completes it short.
        File target = dir.resolve("short.txt").toFile();
        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "f2", "other", target.getAbsolutePath(), 10, 1, 10, false));
        assertFileAck(a.next(), "f2", 0, AckStatus.ok);
        a.send(AgentWsEnvelope.fileChunk("i-test", "42", "f2", 0, base64("hello")));
        assertFileAck(a.next(), "f2", 0, AckStatus.chunk_received);
        AgentWsEnvelope mismatch = a.next();
        assertFileAck(mismatch, "f2", 0, AckStatus.failed);
        assertTrue(mismatch.getError().startsWith("file size mismatch"));
        assertFalse(target.exists());

        // The failed transfer is forgotten, so further chunks find no offer.
        a.send(AgentWsEnvelope.fileChunk("i-test", "42", "f2", 1, base64("world")));
        assertEquals("file_offer_not_found", a.next().getError());
    }

    @Test
    public void fileTransferIsRefusedFromANonBootstrapConnection(@TempDir Path dir) throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");
        TestController b = connect("b");
        File target = dir.resolve("file.txt").toFile();
        a.send(AgentWsEnvelope.fileOffer("i-test", "42", "f1", "other", target.getAbsolutePath(), 2, 1, 2, false));
        assertFileAck(a.next(), "f1", 0, AckStatus.ok);

        b.send(AgentWsEnvelope.fileOffer("i-test", "42", "f2", "other", target.getAbsolutePath(), 2, 1, 2, false));
        assertEquals(NOT_BOOTSTRAP, b.next().getError());
        b.send(AgentWsEnvelope.fileChunk("i-test", "42", "f1", 0, base64("hi")));
        assertEquals(NOT_BOOTSTRAP, b.next().getError());
        b.sendBinary(AgentWsEnvelope.binaryFileChunk("f1", 0, "hi".getBytes(StandardCharsets.UTF_8), 0, 2));
        AgentWsEnvelope binaryAck = b.next();
        assertFileAck(binaryAck, "f1", 0, AckStatus.failed);
        assertEquals(NOT_BOOTSTRAP, binaryAck.getError());

        // The bootstrap connection's transfer is untouched by the refused chunks.
        a.sendBinary(AgentWsEnvelope.binaryFileChunk("f1", 0, "ok".getBytes(StandardCharsets.UTF_8), 0, 2));
        assertFileAck(a.next(), "f1", 0, AckStatus.chunk_received);
        assertFileAck(a.next(), "f1", 0, AckStatus.complete);
        assertEquals("ok", Files.readString(target.toPath()));
    }

    @Test
    public void malformedBinaryFrameFailsTheConnection() throws Exception {
        startServer(TOKEN, null);
        TestController a = connect("a");

        a.sendBinary(ByteBuffer.wrap(new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 'x'}));

        a.closed.get(5, TimeUnit.SECONDS);
    }

    private static void assertFileAck(AgentWsEnvelope ack, String fileId, int chunkIndex, AckStatus status) {
        assertEquals(AgentWsEnvelope.Type.file_ack, ack.getType());
        assertEquals(fileId, ack.getFileId());
        assertEquals(chunkIndex, ack.getChunkIndex());
        assertEquals(status, ack.getStatus(), "error: " + ack.getError());
    }

    private static String base64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    private static class TestController implements WebSocket.Listener {
        private final LinkedBlockingQueue<String> messages = new LinkedBlockingQueue<>();
        private final CompletableFuture<Void> closed = new CompletableFuture<>();
        private final StringBuilder partial = new StringBuilder();
        private WebSocket socket;

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            partial.append(data);
            if (last) {
                messages.add(partial.toString());
                partial.setLength(0);
            }
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            closed.complete(null);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            closed.complete(null);
        }

        private AgentWsEnvelope next() throws Exception {
            String message = messages.poll(5, TimeUnit.SECONDS);
            assertNotNull(message, "no message from agent within 5s");
            return AgentWsEnvelope.fromJson(message);
        }

        private void send(AgentWsEnvelope envelope) throws Exception {
            sendText(envelope.toJson());
        }

        private void sendText(String text) throws Exception {
            socket.sendText(text, true).get(5, TimeUnit.SECONDS);
        }

        private void sendBinary(ByteBuffer frame) throws Exception {
            socket.sendBinary(frame, true).get(5, TimeUnit.SECONDS);
        }
    }
}

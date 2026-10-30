package com.intuit.tank.perfManager.workLoads;

import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.vm.vmManager.VMTerminator;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMImageType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.vm.vmManager.models.ValidationStatus;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Drives {@link ControllerInitiatedAgentWsClient} end to end against a mocked Jetty session that plays
 * the agent's side of the protocol (hello, offer acks, chunk acks, command acks).
 */
public class ControllerInitiatedAgentWsClientFlowTest {

    private static final String CLASS_NAME = "com.intuit.tank.perfManager.workLoads.ControllerInitiatedAgentWsClient";
    // Mirrors MAX_BOOTSTRAP_CONNECTION_MS's default.
    private static final long MAX_BOOTSTRAP_CONNECTION_MS = 180_000L;

    @TempDir
    Path tempDir;

    private HttpServer httpServer;
    private String baseUrl;
    private final List<String> authHeaders = new CopyOnWriteArrayList<>();

    @BeforeEach
    void startHttpServer() throws Exception {
        httpServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        httpServer.createContext("/files/script", exchange -> {
            authHeaders.add(String.valueOf(exchange.getRequestHeaders().getFirst("Authorization")));
            respond(exchange, 200, "<script/>".getBytes(StandardCharsets.UTF_8), null);
        });
        httpServer.createContext("/files/data.gz", exchange ->
                respond(exchange, 200, gzip("a,b\n1,2\n".getBytes(StandardCharsets.UTF_8)), "gzip"));
        httpServer.createContext("/files/empty", exchange -> respond(exchange, 200, new byte[0], null));
        httpServer.createContext("/files/broken", exchange -> respond(exchange, 500, new byte[0], null));
        httpServer.start();
        baseUrl = "http://127.0.0.1:" + httpServer.getAddress().getPort();
    }

    @AfterEach
    void stopHttpServer() {
        httpServer.stop(0);
    }

    @Test
    void testConnectAndBootstrapTransfersJobFilesAndRunsCommands() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        AtomicReference<ControllerInitiatedAgentWsClient.Endpoint> endpoint = new AtomicReference<>();
        AtomicReference<URI> connectedUri = new AtomicReference<>();
        WebSocketClient wsClient = stubWsClient(client, session, endpoint, connectedUri,
                AgentWsEnvelope.hello("i-actual", "job-1", "agent-session", null, 10, false));
        FakeAgent agent = new FakeAgent("i-actual", envelope -> endpoint.get().onWebSocketText(envelope.toJson()));
        agent.stub(session);

        AgentTestStartData startData = new AgentTestStartData(baseUrl + "/files/script", 1, 0);
        startData.setJobId("job-1");
        startData.setDataFiles(new DataFileRequest[] {
                new DataFileRequest("data.csv", true, baseUrl + "/files/data.gz"),
                new DataFileRequest("empty.csv", false, baseUrl + "/files/empty")
        });
        JobManager jobManager = mock(JobManager.class);
        AgentData agentData = mock(AgentData.class);
        when(jobManager.buildAgentDataForWsHello("job-1", "i-actual", "http://agent:8090", 10)).thenReturn(agentData);
        when(jobManager.registerAgentForJob(agentData)).thenReturn(startData);

        boolean result = client.connectAndBootstrap(jobManager, "i-expected", "http://agent:8090",
                "ws://agent:8090/ws?v=1", "tok", 1_000L, 5_000L);

        assertTrue(result);
        assertTrue(client.isFileTransferReady("i-actual"));
        assertEquals("ready", client.getWsState("i-actual"));
        assertEquals("3/3 files", client.getTransferProgress("i-actual"));
        assertTrue(client.getLastSeen("i-actual") > 0);
        assertTrue(client.hasSession("i-actual"));
        assertFalse(client.hasSession("i-expected"));
        assertEquals(List.of("bearer tok"), authHeaders);
        assertTrue(connectedUri.get().toString().startsWith("ws://agent:8090/ws?v=1&node="));
        assertEquals(List.of("script.xml", "data.csv", "empty.csv"), agent.offeredFiles);
        assertArrayEquals("a,b\n1,2\n".getBytes(StandardCharsets.UTF_8), agent.receivedBytes.get("data.csv").toByteArray());
        verify(wsClient).connect(any(), any(URI.class), any(ClientUpgradeRequest.class));

        // A second connect reuses the open session rather than dialing again.
        Optional<AgentWsEnvelope> hello = client.connect("i-actual", "ws://agent:8090/ws", "tok", 1_000L);
        assertEquals("i-actual", hello.orElseThrow().getInstanceId());

        assertTrue(client.sendCommand("i-actual", "job-1", "start", 1_000L));
        assertEquals("running", client.getWsState("i-actual"));
        agent.commandStatus = AckStatus.failed;
        assertFalse(client.sendCommand("i-actual", "job-1", "stop", 1_000L));
        agent.commandStatus = null;
        assertFalse(client.sendCommand("i-actual", "job-1", "pause", 20L));
        assertFalse(client.sendCommand("i-unknown", "job-1", "start", 20L));

        // The listener is still keyed by the pre-hello id, so its socket close is not matched to the
        // re-bound session; the agent's close frame (carrying its real id) is.
        endpoint.get().onWebSocketClose(1000, "bye");
        assertTrue(client.hasSession("i-actual"));
        endpoint.get().onWebSocketText(AgentWsEnvelope.close("i-actual", "done", "bye").toJson());
        assertFalse(client.hasSession("i-actual"));
        assertEquals("disconnected", client.getWsState("i-actual"));
        assertFalse(client.isFileTransferReady("i-actual"));
    }

    @Test
    void testConnectAndBootstrapPushesStartupFilesWhenAgentNeedsBootstrap() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        AtomicReference<ControllerInitiatedAgentWsClient.Endpoint> endpoint = new AtomicReference<>();
        stubWsClient(client, session, endpoint, new AtomicReference<>(),
                AgentWsEnvelope.hello("i-agent", null, "agent-session", null, null, true));
        FakeAgent agent = new FakeAgent("i-agent", envelope -> endpoint.get().onWebSocketText(envelope.toJson()));
        agent.stub(session);
        setField(client, "cachedStartupScript", Optional.empty());
        writeHarnessJar(new byte[] { 4, 5 });
        JobManager jobManager = mock(JobManager.class);

        withCatalinaHome(tempDir, () -> assertFalse(client.connectAndBootstrap(jobManager, "i-agent",
                "http://agent:8090", "ws://agent:8090/ws", "tok", 1_000L, 2_000L)));

        // The bootstrap connection is closed so the agent can restart with the harness.
        assertEquals("bootstrap_sent", client.getWsState("i-agent"));
        assertEquals("2/2 files", client.getTransferProgress("i-agent"));
        assertEquals(List.of("settings.xml", "apiharness-1.0-all.jar"), agent.offeredFiles);
        verify(jobManager, never()).registerAgentForJob(any());
    }

    @Test
    void testConnectAndBootstrapClosesWhenJobIsNotCached() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        AtomicReference<ControllerInitiatedAgentWsClient.Endpoint> endpoint = new AtomicReference<>();
        stubWsClient(client, session, endpoint, new AtomicReference<>(),
                AgentWsEnvelope.hello(null, "job-1", "agent-session", null, 5));
        List<AgentWsEnvelope> sent = recordSentText(session);
        JobManager jobManager = mock(JobManager.class);

        boolean result = client.connectAndBootstrap(jobManager, "i-agent", "http://agent:8090",
                "ws://agent:8090/ws", "tok", 1_000L, 1_000L);

        assertFalse(result);
        assertEquals("registered", client.getWsState("i-agent"));
        assertEquals(AgentWsEnvelope.Type.close, sent.get(0).getType());
        assertEquals("bootstrap_failed", sent.get(0).getReasonCode());
        assertFalse(client.hasSession("i-agent"));
        verify(session).close(anyInt(), anyString(), any(Callback.class));
    }

    @Test
    void testConnectAndBootstrapAbortsWhenTransferSourceFails() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        AtomicReference<ControllerInitiatedAgentWsClient.Endpoint> endpoint = new AtomicReference<>();
        stubWsClient(client, session, endpoint, new AtomicReference<>(),
                AgentWsEnvelope.hello("i-agent", "job-1", "agent-session", null, 5));
        AgentTestStartData startData = new AgentTestStartData(baseUrl + "/files/broken", 1, 0);
        JobManager jobManager = mock(JobManager.class);
        when(jobManager.registerAgentForJob(any())).thenReturn(startData);

        boolean result = client.connectAndBootstrap(jobManager, "i-agent", "http://agent:8090",
                "ws://agent:8090/ws", null, 1_000L, 1_000L);

        assertFalse(result);
        assertEquals("disconnected", client.getWsState("i-agent"));
        assertFalse(client.hasSession("i-agent"));
        verify(session).disconnect();
    }

    @Test
    void testConnectAndBootstrapAbortsWhenAgentRejectsOffer() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        AtomicReference<ControllerInitiatedAgentWsClient.Endpoint> endpoint = new AtomicReference<>();
        stubWsClient(client, session, endpoint, new AtomicReference<>(),
                AgentWsEnvelope.hello("i-agent", "job-1", "agent-session", null, 5));
        FakeAgent agent = new FakeAgent("i-agent", envelope -> endpoint.get().onWebSocketText(envelope.toJson()));
        agent.offerResponder = offer -> AgentWsEnvelope.fileAck("i-agent", "job-1", offer.getFileId(), null,
                AckStatus.failed, "disk_full");
        agent.stub(session);
        AgentTestStartData startData = new AgentTestStartData(baseUrl + "/files/script", 1, 0);
        startData.setJobId("job-1");
        JobManager jobManager = mock(JobManager.class);
        when(jobManager.registerAgentForJob(any())).thenReturn(startData);

        boolean result = client.connectAndBootstrap(jobManager, "i-agent", "http://agent:8090",
                "ws://agent:8090/ws", "tok", 1_000L, 1_000L);

        assertFalse(result);
        assertEquals("disconnected", client.getWsState("i-agent"));
    }

    @Test
    void testConnectAndBootstrapReturnsFalseWhenSessionClosedAfterHello() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = mock(Session.class);
        when(session.isOpen()).thenReturn(false);
        stubWsClient(client, session, new AtomicReference<>(), new AtomicReference<>(),
                AgentWsEnvelope.hello("i-agent", "job-1", "agent-session", null, 5));

        assertFalse(client.connectAndBootstrap(mock(JobManager.class), "i-agent", "http://agent:8090",
                "ws://agent:8090/ws", "tok", 1_000L, 1_000L));
    }

    @Test
    void testConnectAndBootstrapReturnsFalseWhenConnectFails() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        WebSocketClient wsClient = mock(WebSocketClient.class);
        when(wsClient.connect(any(), any(URI.class), any(ClientUpgradeRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(new IOException("refused")));
        setField(client, "wsClient", wsClient);

        assertFalse(client.connectAndBootstrap(mock(JobManager.class), "i-agent", "http://agent:8090",
                "ws://agent:8090/ws", "tok", 1_000L, 1_000L));
        assertEquals("disconnected", client.getWsState("i-agent"));
    }

    @Test
    void testConnectTimesOutWaitingForHelloAndClosesSession() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        stubWsClient(client, session, new AtomicReference<>(), new AtomicReference<>(), null);

        Optional<AgentWsEnvelope> hello = client.connect("i-agent", "ws://agent:8090/ws", "tok", 10L);

        assertTrue(hello.isEmpty());
        assertFalse(client.hasSession("i-agent"));
        assertEquals("disconnected", client.getWsState("i-agent"));
        verify(session).close(anyInt(), anyString(), any(Callback.class));
    }

    @Test
    void testConnectReplacesPreviousClosedSession() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session previous = mock(Session.class);
        installSession(client, "i-agent", previous);
        Session session = openSession();
        AtomicReference<ControllerInitiatedAgentWsClient.Endpoint> endpoint = new AtomicReference<>();
        stubWsClient(client, session, endpoint, new AtomicReference<>(),
                AgentWsEnvelope.hello("i-agent", "job-1", "agent-session", null, 5));

        Optional<AgentWsEnvelope> hello = client.connect("i-agent", "ws://agent:8090/ws", "tok", 1_000L);

        assertTrue(hello.isPresent());
        assertTrue(client.hasSession("i-agent"));
        assertEquals("connected", client.getWsState("i-agent"));
        verify(previous).close(anyInt(), anyString(), any(Callback.class));

        // Listener errors are treated as a close of the active session.
        endpoint.get().onWebSocketError(new IOException("reset"));
        assertFalse(client.hasSession("i-agent"));
    }

    @Test
    void testStartupBootstrapReadsHarnessJarAndCompletesAfterAllFilesAcked() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        FakeAgent agent = new FakeAgent("i-agent", envelope -> invokeHandleText(client, "i-agent", session, envelope));
        agent.stub(session);
        writeHarnessJar(new byte[] { 7, 8, 9 });

        withCatalinaHome(tempDir, () -> assertFalse(invokePushStartupBootstrap(client, context)));

        assertEquals("bootstrap_sent", client.getWsState("i-agent"));
        assertFalse(client.hasSession("i-agent"));
        assertTrue(agent.offeredFiles.contains("settings.xml"));
        assertTrue(agent.offeredFiles.contains("apiharness-1.0-all.jar"));
        int files = agent.offeredFiles.size();
        assertEquals(files + "/" + files + " files", client.getTransferProgress("i-agent"));
        assertArrayEquals(new byte[] { 7, 8, 9 }, (byte[]) getField(client, "cachedHarnessJarBytes"));
    }

    @Test
    void testStartupBootstrapFailsWhenHarnessJarIsMissing() throws Exception {
        assumeFalse(new File("/opt/apache-tomcat-11.0.21/webapps/ROOT/tools/apiharness-1.0-all.jar").exists());
        assumeFalse(new File("/etc/tank/jars/apiharness-1.0-all.jar").exists());
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);

        withCatalinaHome(null, () -> assertFalse(invokePushStartupBootstrap(client, context)));

        verify(session).close(anyInt(), anyString(), any(Callback.class));
        verify(session, never()).sendText(anyString(), any(Callback.class));
    }

    @Test
    void testStartupBootstrapStopsAtConnectionBudgetBeforeSendingChunks() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        setField(context, "openedAtMs", System.currentTimeMillis() - MAX_BOOTSTRAP_CONNECTION_MS - 1_000L);
        FakeAgent agent = new FakeAgent("i-agent", envelope -> invokeHandleText(client, "i-agent", session, envelope));
        agent.stub(session);
        setField(client, "cachedStartupScript", Optional.empty());
        writeHarnessJar(new byte[] { 1 });

        withCatalinaHome(tempDir, () -> assertFalse(invokePushStartupBootstrap(client, context)));

        assertEquals("bootstrap_transferring", client.getWsState("i-agent"));
        assertFalse(client.hasSession("i-agent"));
        assertEquals(List.of("settings.xml"), agent.offeredFiles);
        verify(session, never()).sendBinary(any(ByteBuffer.class), any(Callback.class));
        verify(session).close(anyInt(), eq("bootstrap_connection_budget_reached"), any(Callback.class));
    }

    @Test
    void testStartupBootstrapStopsWhenBudgetRunsOutWhileWaitingForChunkAck() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        new TankConfig();
        long deadline = System.currentTimeMillis() + 200L;
        setField(context, "openedAtMs", deadline - MAX_BOOTSTRAP_CONNECTION_MS);
        FakeAgent agent = new FakeAgent("i-agent", envelope -> invokeHandleText(client, "i-agent", session, envelope));
        // The agent never acks chunks, and the send itself outlasts the connection budget.
        agent.chunkAcks = false;
        agent.onBinarySent = () -> {
            while (System.currentTimeMillis() <= deadline) {
                Thread.sleep(5);
            }
        };
        agent.stub(session);
        setField(client, "cachedStartupScript", Optional.empty());
        writeHarnessJar(new byte[] { 1 });

        withCatalinaHome(tempDir, () -> assertFalse(invokePushStartupBootstrap(client, context)));

        assertEquals("bootstrap_transferring", client.getWsState("i-agent"));
        verify(session).close(anyInt(), eq("bootstrap_connection_budget_reached"), any(Callback.class));
    }

    @Test
    void testStartupBootstrapStopsWhenChunkAckWaitTimesOutAtBudget() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        new TankConfig();
        setField(context, "openedAtMs", System.currentTimeMillis() + 300L - MAX_BOOTSTRAP_CONNECTION_MS);
        FakeAgent agent = new FakeAgent("i-agent", envelope -> invokeHandleText(client, "i-agent", session, envelope));
        agent.chunkAcks = false;
        agent.stub(session);
        setField(client, "cachedStartupScript", Optional.empty());
        writeHarnessJar(new byte[] { 1 });

        withCatalinaHome(tempDir, () -> assertFalse(invokePushStartupBootstrap(client, context)));

        assertEquals("bootstrap_transferring", client.getWsState("i-agent"));
        verify(session).sendBinary(any(ByteBuffer.class), any(Callback.class));
    }

    @Test
    void testSendFileResumesFromAgentOffset() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        FakeAgent agent = new FakeAgent("i-agent", envelope -> invokeHandleText(client, "i-agent", session, envelope));
        AtomicInteger offers = new AtomicInteger();
        agent.offerResponder = offer -> {
            int offerNumber = offers.getAndIncrement();
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck("i-agent", "job-1", offer.getFileId(),
                    offerNumber == 0 ? 0 : null, AckStatus.resume, null);
            // The third agent already holds the whole file.
            ack.setResumeOffset(offerNumber == 2 ? 10L : 4L);
            return ack;
        };
        agent.stub(session);
        byte[] content = "0123456789".getBytes(StandardCharsets.UTF_8);

        assertTrue(invokeSendFile(client, context, transferFile("data", "a.csv", content), 4, 0L));
        assertTrue(invokeSendFile(client, context, transferFile("data", "b.csv", content), 4, 0L));
        assertTrue(invokeSendFile(client, context, transferFile("data", "c.csv", content), 4, 0L));

        assertArrayEquals("456789".getBytes(StandardCharsets.UTF_8), agent.receivedBytes.get("a.csv").toByteArray());
        assertArrayEquals("456789".getBytes(StandardCharsets.UTF_8), agent.receivedBytes.get("b.csv").toByteArray());
        assertEquals(List.of(0, 1), agent.chunkIndexes.get("a.csv"));
        assertEquals(List.of(), agent.chunkIndexes.get("c.csv"));
    }

    @Test
    void testSendFileStartsFromBeginningWhenOfferAckIsMissingOrUnusable() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        FakeAgent agent = new FakeAgent("i-agent", envelope -> invokeHandleText(client, "i-agent", session, envelope));
        AtomicInteger offers = new AtomicInteger();
        agent.offerResponder = offer -> {
            switch (offers.getAndIncrement()) {
                case 0 -> pendingAcks(client).get(offer.getFileId()).complete(null);
                case 1 -> pendingAcks(client).get(offer.getFileId())
                        .completeExceptionally(new IllegalStateException("lost"));
                default -> {
                    // resume without an offset is treated as a fresh start
                    return AgentWsEnvelope.fileAck("i-agent", "job-1", offer.getFileId(), null,
                            AckStatus.resume, null);
                }
            }
            return null;
        };
        agent.stub(session);
        byte[] content = "abcdef".getBytes(StandardCharsets.UTF_8);

        for (String name : List.of("a.csv", "b.csv", "c.csv")) {
            assertTrue(invokeSendFile(client, context, transferFile("data", name, content), 4, 0L));
            assertArrayEquals(content, agent.receivedBytes.get(name).toByteArray());
        }
    }

    @Test
    void testSendFileWaitsForWindowCreditWhenAcksLag() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        FakeAgent agent = new FakeAgent("i-agent", envelope -> invokeHandleText(client, "i-agent", session, envelope));
        agent.ackDelayMs = 20L;
        agent.stub(session);
        byte[] content = new byte[64];
        for (int i = 0; i < content.length; i++) {
            content[i] = (byte) i;
        }

        assertTrue(invokeSendFile(client, context, transferFile("data", "big.bin", content), 1, 0L));

        assertArrayEquals(content, agent.receivedBytes.get("big.bin").toByteArray());
    }

    @Test
    void testSendFileFailsWhenAgentReportsChunkFailure() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        FakeAgent agent = new FakeAgent("i-agent", envelope -> invokeHandleText(client, "i-agent", session, envelope));
        agent.chunkStatus = AckStatus.failed;
        agent.stub(session);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class, () ->
                invokeSendFile(client, context, transferFile("data", "a.csv", new byte[] { 1, 2 }), 1, 0L));

        assertInstanceOf(IOException.class, thrown.getCause());
        assertEquals("disk_full", thrown.getCause().getMessage());
        CompletableFuture<?> transferComplete = (CompletableFuture<?>) getField(context, "transferCompleteFuture");
        assertTrue(transferComplete.isCompletedExceptionally());
    }

    @Test
    void testWaitForAckedThroughTranslatesWaitFailures() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();

        // Timeout without a budget: plain IOException, or the window failure when one was recorded.
        assertWaitFails(client, new TimeoutException(), false, "Timed out waiting for WS chunk ack for i-agent");
        assertWaitFails(client, new TimeoutException(), true, "boom");
        // Any other failure of the advance future.
        assertWaitFails(client, new ExecutionException(new IllegalStateException()), false,
                "Timed out waiting for WS chunk ack for i-agent");
        assertWaitFails(client, new ExecutionException(new IllegalStateException()), true, "boom");

        // Interruption is propagated with the interrupt flag restored.
        Object window = newChunkWindow("file-1");
        setField(window, "advance", new ThrowingFuture(window, new InterruptedException(), false));
        try {
            InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
                    () -> invokeWaitForAckedThrough(client, window, 0, 0L));
            assertInstanceOf(InterruptedException.class, thrown.getCause());
            assertTrue(Thread.currentThread().isInterrupted());
        } finally {
            Thread.interrupted();
        }
    }

    @Test
    void testWaitForAckedThroughReportsRecordedFailureImmediately() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Object window = newChunkWindow("file-1");
        invokeWindow(window, "onFailure", new Class<?>[] { String.class }, (Object) null);

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
                () -> invokeWaitForAckedThrough(client, window, 5, 0L));

        assertEquals("chunk transfer failed", thrown.getCause().getMessage());
    }

    @Test
    void testChunkWindowTracksContiguousAcks() throws Exception {
        Object window = newChunkWindow("file-1");
        Class<?>[] intArg = { Integer.class };

        invokeWindow(window, "onAck", intArg, (Object) null);
        invokeWindow(window, "onAck", intArg, -1);
        invokeWindow(window, "onAck", intArg, 1);
        assertNotNull(invokeWindow(window, "awaitAdvanceIfBelow", new Class<?>[] { int.class }, 0));
        invokeWindow(window, "onAck", intArg, 0);
        assertNull(invokeWindow(window, "awaitAdvanceIfBelow", new Class<?>[] { int.class }, 1));
        assertTrue((boolean) invokeWindow(window, "matchesFile", new Class<?>[] { String.class }, "file-1"));
        assertFalse((boolean) invokeWindow(window, "matchesFile", new Class<?>[] { String.class }, "file-2"));
    }

    @Test
    void testFileAckIsIgnoredForUnknownMissingOrStaleSessions() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        AgentWsEnvelope allDone = AgentWsEnvelope.fileAck("i-agent", "job-1", "file-1", 0,
                AckStatus.all_files_complete, null);

        invokeHandleText(client, "i-other", session, AgentWsEnvelope.fileAck("i-other", "job-1", "file-1", 0,
                AckStatus.all_files_complete, null));
        invokeHandleText(client, "i-agent", null, allDone);
        invokeHandleText(client, "i-agent", mock(Session.class), allDone);

        assertFalse(client.isFileTransferReady("i-agent"));
        assertFalse(((CompletableFuture<?>) getField(context, "transferCompleteFuture")).isDone());

        // With no chunk window an all_files_complete still marks the agent ready.
        invokeHandleText(client, "i-agent", session, allDone);
        assertTrue(client.isFileTransferReady("i-agent"));
        assertEquals("ready", client.getWsState("i-agent"));
    }

    @Test
    void testFileAckFailureForOtherFileOnlyFailsTransfer() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        setField(context, "chunkWindow", newChunkWindow("file-other"));

        invokeHandleText(client, "i-agent", session, AgentWsEnvelope.fileAck("i-agent", "job-1", "file-1", 0,
                AckStatus.complete, null));
        invokeHandleText(client, "i-agent", session, AgentWsEnvelope.fileAck("i-agent", "job-1", "file-1", 0,
                AckStatus.failed, "bad"));

        assertEquals("1/0 files", client.getTransferProgress("i-agent"));
        assertTrue(((CompletableFuture<?>) getField(context, "transferCompleteFuture")).isCompletedExceptionally());
        assertNull(invokeWindow(getField(context, "chunkWindow"), "failure", new Class<?>[0]));
    }

    @Test
    void testHandleTextRoutesAcksPongsAndCloses() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        installSession(client, "i-agent", session);
        CompletableFuture<AgentWsEnvelope> pending = new CompletableFuture<>();
        pendingAcks(client).put("cmd-1", pending);

        // Malformed frames and frames without a type are ignored.
        invokeHandleText(client, "i-agent", session, "not json");
        invokeHandleText(client, "i-agent", session, "{}");
        invokeHandleText(client, "i-agent", session, AgentWsEnvelope.ack("i-agent", "command", null, AckStatus.ok));
        invokeHandleText(client, "i-agent", session, AgentWsEnvelope.ack("i-agent", "command", "cmd-x", AckStatus.ok));
        invokeHandleText(client, "i-agent", session, AgentWsEnvelope.ack("i-agent", "command", "cmd-1", AckStatus.ok));
        invokeHandleText(client, "i-agent", session, AgentWsEnvelope.pong("i-agent", "s", "p", null));
        invokeHandleText(client, "i-agent", session, AgentWsEnvelope.ping("p"));
        assertTrue(pending.isDone());
        assertTrue(client.hasSession("i-agent"));

        invokeHandleText(client, "i-agent", session, AgentWsEnvelope.close("i-agent", "done", "bye"));
        assertFalse(client.hasSession("i-agent"));
        assertEquals("disconnected", client.getWsState("i-agent"));
    }

    @Test
    void testCloseIsIgnoredForUnknownMissingStaleOrReplacedSessions() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        Object context = installSession(client, "i-agent", session);
        Object window = newChunkWindow("file-1");
        setField(context, "chunkWindow", window);

        invokeOnClosed(client, "i-other", session);
        invokeOnClosed(client, "i-agent", null);
        invokeOnClosed(client, "i-agent", mock(Session.class));
        assertTrue(client.hasSession("i-agent"));

        invokeOnClosed(client, "i-agent", session);
        assertFalse(client.hasSession("i-agent"));
        assertEquals("WS connection closed for i-agent",
                invokeWindow(window, "failure", new Class<?>[0]));

        // A session replaced between lookup and removal is left alone.
        ControllerInitiatedAgentWsClient racing = new ControllerInitiatedAgentWsClient();
        setField(racing, "sessions", new ConcurrentHashMap<String, Object>() {
            @Override
            public boolean remove(Object key, Object value) {
                return false;
            }
        });
        installSession(racing, "i-agent", session);
        invokeOnClosed(racing, "i-agent", session);
        assertTrue(racing.hasSession("i-agent"));
        assertNull(racing.getWsState("i-agent"));
    }

    @Test
    void testKeepaliveSkipsClosedSessionsAndToleratesPingFailures() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session closed = mock(Session.class);
        installSession(client, "i-closed", closed);
        Session failing = mock(Session.class);
        when(failing.isOpen()).thenReturn(true);
        doAnswer(invocation -> {
            ((Callback) invocation.getArgument(1)).fail(new IOException("broken pipe"));
            return null;
        }).when(failing).sendText(anyString(), any(Callback.class));
        installSession(client, "i-failing", failing);

        invokePrivate(client, "pingOpenSessions", new Class<?>[0]);

        verify(closed, never()).sendText(anyString(), any(Callback.class));
        verify(failing).sendText(anyString(), any(Callback.class));
        assertTrue(client.hasSession("i-failing"));
    }

    @Test
    void testStaleSessionAbortToleratesDisconnectFailure() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        client.setCloseStaleSessions(true);
        Session session = openSession();
        doThrow(new IllegalStateException("gone")).when(session).disconnect();
        Object context = installSession(client, "i-stale", session);
        setField(context, "openedAtMs", System.currentTimeMillis() - 5 * 60_000L);

        invokePrivate(client, "pingOpenSessions", new Class<?>[0]);

        assertFalse(client.hasSession("i-stale"));
        verify(session).disconnect();
    }

    @Test
    void testDisconnectFallsBackToHardDisconnectWhenCloseFails() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Session session = openSession();
        doThrow(new IllegalStateException("close failed")).when(session)
                .close(anyInt(), anyString(), any(Callback.class));
        doThrow(new IllegalStateException("disconnect failed")).when(session).disconnect();
        Object context = installSession(client, "i-agent", session);

        client.disconnect("i-agent");
        // A second close is a no-op once the context is closed.
        invokeSessionContext(context, "close");
        client.disconnect("i-missing");

        verify(session).disconnect();
        assertEquals("disconnected", client.getWsState("i-agent"));
        assertEquals("disconnected", client.getWsState("i-missing"));
    }

    @Test
    void testStatusUpdateIgnoredWithoutTrackerAndToleratesFailures() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        CloudVmStatus status = createStatus(JobStatus.Completed, VMStatus.running);

        // No tracker yet: nothing happens.
        invokeHandleText(client, "i-agent", null, AgentWsEnvelope.statusUpdate("i-agent", "job-1", status));
        // No status payload.
        client.setVmTracker(mock(VMTracker.class));
        invokeHandleText(client, "i-agent", null, AgentWsEnvelope.statusUpdate("i-agent", "job-1", null));

        // Terminal status without a terminator is logged and the tracker is still updated.
        VMTracker tracker = mock(VMTracker.class);
        client.setVmTracker(tracker);
        invokeHandleText(client, "i-agent", null, AgentWsEnvelope.statusUpdate("i-agent", "job-1", status));
        verify(tracker).setStatus(any(CloudVmStatus.class));

        // A terminator failure is retried on the next terminal status.
        VMTerminator terminator = mock(VMTerminator.class);
        doThrow(new IllegalStateException("aws down")).doNothing().when(terminator).terminate("i-agent");
        client.setVmTerminator(terminator);
        invokeHandleText(client, "i-agent", null, AgentWsEnvelope.statusUpdate("i-agent", "job-1", status));
        invokeHandleText(client, "i-agent", null, AgentWsEnvelope.statusUpdate("i-agent", "job-1", status));
        verify(terminator, org.mockito.Mockito.times(2)).terminate("i-agent");

        // Replaced VMs are terminal too; tracker failures are swallowed.
        CloudVmStatus replaced = createStatus(JobStatus.Running, VMStatus.replaced);
        doThrow(new IllegalStateException("db down")).when(tracker).setStatus(any(CloudVmStatus.class));
        invokeHandleText(client, "i-replaced", null, AgentWsEnvelope.statusUpdate("i-replaced", "job-1", replaced));
        verify(terminator).terminate("i-replaced");
    }

    @Test
    void testLocalControllerUriRewritesOnlyControllerApiPaths() throws Exception {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        Class<?>[] stringArg = { String.class };

        assertEquals(URI.create("http://localhost:8080/v2/jobs/1/script?a=1&b=2"),
                invokePrivate(client, "localControllerUri", stringArg, "https://tank.example.com/v2/jobs/1/script?a=1&b=2"));
        assertEquals(URI.create("http://localhost:8080/v2/scripts/5/download"),
                invokePrivate(client, "localControllerUri", stringArg, "https://tank.example.com/v2/scripts/5/download"));
        assertEquals(URI.create("http://localhost:8080/v2/datafiles/9"),
                invokePrivate(client, "localControllerUri", stringArg, "https://tank.example.com/v2/datafiles/9"));
        assertEquals(URI.create("https://s3.example.com/bucket/file"),
                invokePrivate(client, "localControllerUri", stringArg, "https://s3.example.com/bucket/file"));
        assertEquals(URI.create("mailto:ops@example.com"),
                invokePrivate(client, "localControllerUri", stringArg, "mailto:ops@example.com"));
    }

    @Test
    void testWithNodeIdAppendsNodeParameter() {
        assertTrue(ControllerInitiatedAgentWsClient.withNodeId("ws://a:1/ws").startsWith("ws://a:1/ws?node="));
        assertTrue(ControllerInitiatedAgentWsClient.withNodeId("ws://a:1/ws?x=1").startsWith("ws://a:1/ws?x=1&node="));
    }

    @Test
    void testReadStartupScriptHandlesMissingAndUnreadableScripts() throws Exception {
        com.intuit.tank.storage.FileStorage missing = mock(com.intuit.tank.storage.FileStorage.class);
        assertTrue(ControllerInitiatedAgentWsClient.readStartupScript(missing).isEmpty());

        com.intuit.tank.storage.FileStorage broken = mock(com.intuit.tank.storage.FileStorage.class);
        when(broken.exists(any())).thenReturn(true);
        when(broken.readFileData(any())).thenThrow(new IllegalStateException("denied"));
        IOException thrown = assertThrows(IOException.class,
                () -> ControllerInitiatedAgentWsClient.readStartupScript(broken));
        assertInstanceOf(IllegalStateException.class, thrown.getCause());
    }

    // ---------------------------------------------------------------------------------------------
    // Fake agent

    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T value) throws Exception;
    }

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    /** Plays the agent side of the protocol for a mocked session. */
    private static final class FakeAgent {
        private final String agentId;
        private final ThrowingConsumer<AgentWsEnvelope> toController;
        private final List<String> offeredFiles = new CopyOnWriteArrayList<>();
        private final Map<String, ByteArrayOutputStream> receivedBytes = new ConcurrentHashMap<>();
        private final Map<String, List<Integer>> chunkIndexes = new ConcurrentHashMap<>();
        private final Map<String, String> fileNames = new ConcurrentHashMap<>();
        private final Map<String, Integer> totalChunks = new ConcurrentHashMap<>();
        private final AtomicInteger completedFiles = new AtomicInteger();
        private volatile Integer expectedFiles;
        private volatile Function<AgentWsEnvelope, AgentWsEnvelope> offerResponder;
        private volatile AckStatus commandStatus = AckStatus.ok;
        private volatile AckStatus chunkStatus;
        private volatile boolean chunkAcks = true;
        private volatile long ackDelayMs;
        private volatile ThrowingRunnable onBinarySent;
        private ScheduledExecutorService scheduler;

        private FakeAgent(String agentId, ThrowingConsumer<AgentWsEnvelope> toController) {
            this.agentId = agentId;
            this.toController = toController;
        }

        private void stub(Session session) {
            doAnswer(invocation -> {
                AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(invocation.getArgument(0));
                ((Callback) invocation.getArgument(1)).succeed();
                onText(envelope);
                return null;
            }).when(session).sendText(anyString(), any(Callback.class));
            doAnswer(invocation -> {
                AgentWsEnvelope.BinaryFileChunk chunk =
                        AgentWsEnvelope.fromBinaryFileChunk(((ByteBuffer) invocation.getArgument(0)).duplicate());
                ((Callback) invocation.getArgument(1)).succeed();
                onChunk(chunk);
                return null;
            }).when(session).sendBinary(any(ByteBuffer.class), any(Callback.class));
        }

        private void onText(AgentWsEnvelope envelope) throws Exception {
            switch (envelope.getType()) {
                case job_config -> expectedFiles = envelope.getExpectedFiles();
                case file_offer -> {
                    offeredFiles.add(envelope.getFileName());
                    fileNames.put(envelope.getFileId(), envelope.getFileName());
                    totalChunks.put(envelope.getFileId(), envelope.getTotalChunks());
                    receivedBytes.put(envelope.getFileName(), new ByteArrayOutputStream());
                    chunkIndexes.put(envelope.getFileName(), new CopyOnWriteArrayList<>());
                    AgentWsEnvelope ack = offerResponder != null
                            ? offerResponder.apply(envelope)
                            : AgentWsEnvelope.fileAck(agentId, envelope.getJobId(), envelope.getFileId(), null,
                                    AckStatus.ok, null);
                    if (ack != null) {
                        toController.accept(ack);
                    }
                }
                case command -> {
                    if (commandStatus != null) {
                        toController.accept(AgentWsEnvelope.ack(agentId, "command", envelope.getCommandId(),
                                commandStatus));
                    }
                }
                default -> {
                }
            }
        }

        private void onChunk(AgentWsEnvelope.BinaryFileChunk chunk) throws Exception {
            String fileName = fileNames.get(chunk.fileId());
            receivedBytes.get(fileName).write(chunk.payload());
            chunkIndexes.get(fileName).add(chunk.chunkIndex());
            if (onBinarySent != null) {
                onBinarySent.run();
            }
            if (!chunkAcks) {
                return;
            }
            if (chunkStatus == AckStatus.failed) {
                toController.accept(AgentWsEnvelope.fileAck(agentId, null, chunk.fileId(), chunk.chunkIndex(),
                        AckStatus.failed, "disk_full"));
                return;
            }
            boolean last = chunk.chunkIndex() >= totalChunks.get(chunk.fileId()) - 1;
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(agentId, null, chunk.fileId(), chunk.chunkIndex(),
                    last ? AckStatus.complete : AckStatus.chunk_received, null);
            if (ackDelayMs > 0) {
                synchronized (this) {
                    if (scheduler == null) {
                        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                            Thread t = new Thread(r, "fake-agent-acks");
                            t.setDaemon(true);
                            return t;
                        });
                    }
                }
                scheduler.schedule(() -> {
                    toController.accept(ack);
                    return null;
                }, ackDelayMs, TimeUnit.MILLISECONDS);
                return;
            }
            toController.accept(ack);
            if (last && expectedFiles != null && completedFiles.incrementAndGet() == expectedFiles) {
                toController.accept(AgentWsEnvelope.fileAck(agentId, null, chunk.fileId(), chunk.chunkIndex(),
                        AckStatus.all_files_complete, null));
            }
        }
    }

    /** A future that reports "not done" and throws the given failure when awaited. */
    private static final class ThrowingFuture extends CompletableFuture<Void> {
        private final Object window;
        private final Exception failure;
        private final boolean recordWindowFailure;

        private ThrowingFuture(Object window, Exception failure, boolean recordWindowFailure) {
            this.window = window;
            this.failure = failure;
            this.recordWindowFailure = recordWindowFailure;
        }

        @Override
        public boolean isDone() {
            return false;
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            if (recordWindowFailure) {
                try {
                    setField(window, "failure", "boom");
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
            if (failure instanceof InterruptedException e) {
                throw e;
            }
            if (failure instanceof ExecutionException e) {
                throw e;
            }
            throw (TimeoutException) failure;
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Helpers

    private void assertWaitFails(ControllerInitiatedAgentWsClient client, Exception failure,
                                 boolean recordWindowFailure, String expectedMessage) throws Exception {
        Object window = newChunkWindow("file-1");
        setField(window, "advance", new ThrowingFuture(window, failure, recordWindowFailure));

        InvocationTargetException thrown = assertThrows(InvocationTargetException.class,
                () -> invokeWaitForAckedThrough(client, window, 0, 0L));

        assertInstanceOf(IOException.class, thrown.getCause());
        assertEquals(expectedMessage, thrown.getCause().getMessage());
    }

    private static CloudVmStatus createStatus(JobStatus jobStatus, VMStatus vmStatus) {
        return new CloudVmStatus("i-agent", "job-1", "sg-1", jobStatus, VMImageType.AGENT, VMRegion.US_EAST,
                vmStatus, new ValidationStatus(), 5, 1, new Date(), null);
    }

    private WebSocketClient stubWsClient(ControllerInitiatedAgentWsClient client, Session session,
                                         AtomicReference<ControllerInitiatedAgentWsClient.Endpoint> endpoint,
                                         AtomicReference<URI> connectedUri, AgentWsEnvelope hello) throws Exception {
        WebSocketClient wsClient = mock(WebSocketClient.class);
        when(wsClient.connect(any(), any(URI.class), any(ClientUpgradeRequest.class))).thenAnswer(invocation -> {
            ControllerInitiatedAgentWsClient.Endpoint listener = invocation.getArgument(0);
            endpoint.set(listener);
            connectedUri.set(invocation.getArgument(1));
            listener.onWebSocketOpen(session);
            if (hello != null) {
                listener.onWebSocketText(hello.toJson());
            }
            return CompletableFuture.completedFuture(session);
        });
        setField(client, "wsClient", wsClient);
        return wsClient;
    }

    private List<AgentWsEnvelope> recordSentText(Session session) {
        List<AgentWsEnvelope> sent = new CopyOnWriteArrayList<>();
        doAnswer(invocation -> {
            sent.add(AgentWsEnvelope.fromJson(invocation.getArgument(0)));
            ((Callback) invocation.getArgument(1)).succeed();
            return null;
        }).when(session).sendText(anyString(), any(Callback.class));
        return sent;
    }

    private Session openSession() {
        Session session = mock(Session.class);
        when(session.isOpen()).thenReturn(true);
        doAnswer(invocation -> {
            ((Callback) invocation.getArgument(1)).succeed();
            return null;
        }).when(session).sendText(anyString(), any(Callback.class));
        return session;
    }

    private void writeHarnessJar(byte[] content) throws IOException {
        Path toolsDir = tempDir.resolve("webapps/ROOT/tools");
        Files.createDirectories(toolsDir);
        Files.write(toolsDir.resolve("apiharness-1.0-all.jar"), content);
    }

    private void withCatalinaHome(Path catalinaHome, ThrowingRunnable body) throws Exception {
        String previous = System.getProperty("catalina.home");
        if (catalinaHome == null) {
            System.clearProperty("catalina.home");
        } else {
            System.setProperty("catalina.home", catalinaHome.toString());
        }
        try {
            body.run();
        } finally {
            if (previous == null) {
                System.clearProperty("catalina.home");
            } else {
                System.setProperty("catalina.home", previous);
            }
        }
    }

    private static void respond(com.sun.net.httpserver.HttpExchange exchange, int status, byte[] body,
                                String contentEncoding) throws IOException {
        if (contentEncoding != null) {
            exchange.getResponseHeaders().add("Content-Encoding", contentEncoding);
        }
        exchange.sendResponseHeaders(status, body.length == 0 ? -1 : body.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(body);
        }
    }

    private static byte[] gzip(byte[] content) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(content);
        }
        return out.toByteArray();
    }

    private static ControllerInitiatedAgentWsClient.TransferFile transferFile(String type, String name, byte[] content) {
        return new ControllerInitiatedAgentWsClient.TransferFile(type, name, content, false);
    }

    private boolean invokeSendFile(ControllerInitiatedAgentWsClient client, Object context,
                                   ControllerInitiatedAgentWsClient.TransferFile file, int chunkBytes,
                                   long deadlineMs) throws Exception {
        Method method = ControllerInitiatedAgentWsClient.class.getDeclaredMethod("sendFile",
                context.getClass(), String.class, String.class, ControllerInitiatedAgentWsClient.TransferFile.class,
                int.class, long.class);
        method.setAccessible(true);
        return (boolean) method.invoke(client, context, "i-agent", "job-1", file, chunkBytes, deadlineMs);
    }

    private void invokeWaitForAckedThrough(ControllerInitiatedAgentWsClient client, Object window,
                                           int requiredIndex, long deadlineMs) throws Exception {
        Method method = ControllerInitiatedAgentWsClient.class.getDeclaredMethod("waitForAckedThrough",
                window.getClass(), int.class, long.class, String.class);
        method.setAccessible(true);
        method.invoke(client, window, requiredIndex, deadlineMs, "i-agent");
    }

    private boolean invokePushStartupBootstrap(ControllerInitiatedAgentWsClient client, Object context)
            throws Exception {
        return (boolean) invokePrivate(client, "pushStartupBootstrapJar",
                new Class<?>[] { String.class, context.getClass(), long.class }, "i-agent", context, 2_000L);
    }

    private void invokeHandleText(ControllerInitiatedAgentWsClient client, String instanceId, Session session,
                                  AgentWsEnvelope envelope) throws Exception {
        invokeHandleText(client, instanceId, session, envelope.toJson());
    }

    private void invokeHandleText(ControllerInitiatedAgentWsClient client, String instanceId, Session session,
                                  String text) throws Exception {
        invokePrivate(client, "handleText",
                new Class<?>[] { String.class, Session.class, String.class, CompletableFuture.class },
                instanceId, session, text, new CompletableFuture<AgentWsEnvelope>());
    }

    private void invokeOnClosed(ControllerInitiatedAgentWsClient client, String instanceId, Session session)
            throws Exception {
        invokePrivate(client, "onClosed", new Class<?>[] { String.class, Session.class }, instanceId, session);
    }

    private static Object invokePrivate(Object target, String name, Class<?>[] types, Object... args)
            throws Exception {
        Method method = target.getClass().getDeclaredMethod(name, types);
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    private static Object invokeWindow(Object window, String name, Class<?>[] types, Object... args)
            throws Exception {
        return invokePrivate(window, name, types, args);
    }

    private static void invokeSessionContext(Object context, String name) throws Exception {
        invokePrivate(context, name, new Class<?>[0]);
    }

    private static Object newChunkWindow(String fileId) throws Exception {
        Constructor<?> constructor = Class.forName(CLASS_NAME + "$ChunkWindow").getDeclaredConstructor(String.class);
        constructor.setAccessible(true);
        return constructor.newInstance(fileId);
    }

    @SuppressWarnings("unchecked")
    private static ConcurrentHashMap<String, CompletableFuture<AgentWsEnvelope>> pendingAcks(
            ControllerInitiatedAgentWsClient client) {
        try {
            return (ConcurrentHashMap<String, CompletableFuture<AgentWsEnvelope>>) getField(client, "pendingAcks");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static Object installSession(ControllerInitiatedAgentWsClient client, String instanceId,
                                         Session session) throws Exception {
        Class<?> endpointClass = ControllerInitiatedAgentWsClient.Endpoint.class;
        Constructor<?> endpointConstructor = endpointClass.getDeclaredConstructor(
                ControllerInitiatedAgentWsClient.class, String.class, CompletableFuture.class);
        endpointConstructor.setAccessible(true);
        Object endpoint = endpointConstructor.newInstance(client, instanceId, new CompletableFuture<AgentWsEnvelope>());

        Constructor<?> contextConstructor = Class.forName(CLASS_NAME + "$SessionContext").getDeclaredConstructor(
                Session.class, endpointClass, CompletableFuture.class);
        contextConstructor.setAccessible(true);
        Object context = contextConstructor.newInstance(session, endpoint, new CompletableFuture<AgentWsEnvelope>());

        ((Map<String, Object>) getField(client, "sessions")).put(instanceId, context);
        return context;
    }

    private static Object getField(Object target, String name) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        return field.get(target);
    }

    private static void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}

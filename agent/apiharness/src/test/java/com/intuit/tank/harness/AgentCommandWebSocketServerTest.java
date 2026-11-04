package com.intuit.tank.harness;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentCommandWebSocketServerTest {

    private static final String TOKEN = "agent-token";

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
    public void queryParamReadsNamedParameter() {
        assertEquals("node-1", AgentCommandWebSocketServer.queryParam("x=1&node=node-1", "node"));
        assertEquals("a b", AgentCommandWebSocketServer.queryParam("node=a%20b", "node"));
        assertNull(AgentCommandWebSocketServer.queryParam("node=", "node"));
        assertNull(AgentCommandWebSocketServer.queryParam(null, "node"));
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
            socket.sendText(envelope.toJson(), true).get(5, TimeUnit.SECONDS);
        }
    }
}

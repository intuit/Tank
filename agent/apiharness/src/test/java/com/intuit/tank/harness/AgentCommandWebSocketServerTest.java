package com.intuit.tank.harness;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.Type;
import com.intuit.tank.vm.api.enumerated.AgentCommand;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentCommandWebSocketServerTest {

    private static final String INSTANCE_ID = "i-test";
    private static final String JOB_ID = "16340";
    private static final String CONTROLLER_PATH = "/ws/control";
    private static final String EMERGENCY_PATH = "/ws/control?role=emergency";

    private final List<Client> clients = new ArrayList<>();
    private AgentCommandWebSocketServer server;
    private int port;

    @BeforeEach
    void startServer() throws Exception {
        APITestHarness.destroyCurrentInstance();
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        }
        server = new AgentCommandWebSocketServer(port, INSTANCE_ID, JOB_ID, 1);
        server.start();
    }

    @AfterEach
    void stopServer() {
        clients.forEach(client -> client.socket.abort());
        server.closeServer();
        APITestHarness.destroyCurrentInstance();
    }

    @Test
    void emergencyConnectionKeepsTheControllerConnected() throws Exception {
        Client controller = connect(CONTROLLER_PATH);
        assertEquals(Type.hello, controller.next().getType());
        Client emergency = connect(EMERGENCY_PATH);
        assertEquals(Type.hello, emergency.next().getType());

        assertTrue(server.sendStatusUpdate(status()));

        assertEquals(Type.status_update, controller.next().getType());
        assertEquals(Type.status_update, emergency.next().getType());
        assertFalse(controller.closed.isDone());
    }

    @Test
    void emergencyCannotStartRunOrSendStatus() throws Exception {
        Client emergency = connect(EMERGENCY_PATH);
        emergency.next();
        AgentCommand before = APITestHarness.getInstance().getCmd();

        for (String command : List.of("start", "run", "status")) {
            emergency.send(AgentWsEnvelope.command("c-" + command, INSTANCE_ID, JOB_ID, command));
            AgentWsEnvelope ack = emergency.next();
            assertAck(ack, "c-" + command, AckStatus.failed);
            assertEquals("not_allowed_on_emergency_connection", ack.getError());
        }
        assertEquals(before, APITestHarness.getInstance().getCmd());
    }

    @Test
    void statusReachesEmergencyConnectionWithoutController() throws Exception {
        Client emergency = connect(EMERGENCY_PATH);
        emergency.next();

        assertFalse(server.sendStatusUpdate(status()));

        assertEquals(Type.status_update, emergency.next().getType());
    }

    @Test
    void newControllerConnectionStillReplacesThePreviousOne() throws Exception {
        Client first = connect(CONTROLLER_PATH);
        first.next();
        Client second = connect(CONTROLLER_PATH);
        second.next();

        first.closed.get(5, TimeUnit.SECONDS);
    }

    @Test
    void closingEmergencyConnectionKeepsTheController() throws Exception {
        Client controller = connect(CONTROLLER_PATH);
        controller.next();
        Client emergency = connect(EMERGENCY_PATH);
        emergency.next();

        emergency.socket.sendClose(WebSocket.NORMAL_CLOSURE, "done").get(5, TimeUnit.SECONDS);
        emergency.closed.get(5, TimeUnit.SECONDS);

        assertTrue(server.sendStatusUpdate(status()));
        assertEquals(Type.status_update, controller.next().getType());
    }

    private static CloudVmStatus status() {
        return new CloudVmStatus(INSTANCE_ID, JOB_ID, null, null, null, null, null, null, 0, 0, null, null);
    }

    private static void assertAck(AgentWsEnvelope ack, String commandId, AckStatus status) {
        assertEquals(Type.ack, ack.getType());
        assertEquals(commandId, ack.getAckForId());
        assertEquals(status, ack.getStatus());
    }

    private Client connect(String path) throws Exception {
        Client client = new Client();
        client.socket = HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create("ws://127.0.0.1:" + port + path), client)
                .get(5, TimeUnit.SECONDS);
        clients.add(client);
        return client;
    }

    private static final class Client implements WebSocket.Listener {
        private final BlockingQueue<String> messages = new LinkedBlockingQueue<>();
        private final StringBuilder partial = new StringBuilder();
        private final CompletableFuture<Integer> closed = new CompletableFuture<>();
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
            closed.complete(statusCode);
            return null;
        }

        AgentWsEnvelope next() throws Exception {
            String text = messages.poll(5, TimeUnit.SECONDS);
            assertNotNull(text, "no message within 5s");
            return AgentWsEnvelope.fromJson(text);
        }

        void send(AgentWsEnvelope envelope) throws Exception {
            sendRaw(envelope.toJson());
        }

        void sendRaw(String json) throws Exception {
            socket.sendText(json, true).get(5, TimeUnit.SECONDS);
        }
    }
}

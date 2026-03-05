package com.intuit.tank.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WebSocketRelay hardening.
 */
class WebSocketRelayTest {

    @Test
    @DisplayName("Relay should set SO_TIMEOUT on read sockets to prevent infinite blocking")
    void testRelaySetsSoTimeout() throws Exception {
        try (ServerSocket ss1 = new ServerSocket(0);
             ServerSocket ss2 = new ServerSocket(0)) {

            Socket client = new Socket("localhost", ss1.getLocalPort());
            Socket server = new Socket("localhost", ss2.getLocalPort());
            Socket clientAccepted = ss1.accept();
            Socket serverAccepted = ss2.accept();

            WebSocketSession session = new WebSocketSession("ws://localhost/test", Collections.emptyMap());
            WebSocketRelay relay = new WebSocketRelay(client, server, session);

            // Start relay (which should configure SO_TIMEOUT before reading)
            relay.start();

            // Give relay threads a moment to start and configure sockets
            Thread.sleep(200);

            // Both sockets should have a non-zero SO_TIMEOUT
            assertTrue(client.getSoTimeout() > 0,
                "Client socket should have SO_TIMEOUT set, but was: " + client.getSoTimeout());
            assertTrue(server.getSoTimeout() > 0,
                "Server socket should have SO_TIMEOUT set, but was: " + server.getSoTimeout());

            relay.stop();
            clientAccepted.close();
            serverAccepted.close();
        }
    }

    @Test
    @DisplayName("Relay should have a SOCKET_TIMEOUT_MS constant defined")
    void testSocketTimeoutConstantExists() throws Exception {
        Field field = WebSocketRelay.class.getDeclaredField("SOCKET_TIMEOUT_MS");
        field.setAccessible(true);
        int timeout = (int) field.get(null);
        assertTrue(timeout > 0, "SOCKET_TIMEOUT_MS should be positive");
    }
}

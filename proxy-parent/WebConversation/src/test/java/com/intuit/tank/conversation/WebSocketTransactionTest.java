package com.intuit.tank.conversation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD: Test-first for WebSocketTransaction model
 */
public class WebSocketTransactionTest {

    private WebSocketTransaction transaction;

    @BeforeEach
    void setUp() {
        transaction = new WebSocketTransaction("ws://localhost:8080/chat");
    }

    @Test
    void testCreateTransaction() {
        assertNotNull(transaction);
        assertEquals("ws://localhost:8080/chat", transaction.getUrl());
        assertNotNull(transaction.getMessages());
        assertTrue(transaction.getMessages().isEmpty());
    }

    @Test
    void testCreateTransactionWithWss() {
        WebSocketTransaction wssTransaction = new WebSocketTransaction("wss://example.com/ws");
        assertEquals("wss://example.com/ws", wssTransaction.getUrl());
        assertEquals(Protocol.wss, wssTransaction.getProtocol());
    }

    @Test
    void testCreateTransactionWithWs() {
        assertEquals(Protocol.ws, transaction.getProtocol());
    }

    @Test
    void testAddMessage() {
        WebSocketMessage message = new WebSocketMessage(
            true,
            WebSocketMessage.Type.TEXT,
            "Hello".getBytes(),
            System.currentTimeMillis()
        );

        transaction.addMessage(message);

        assertEquals(1, transaction.getMessages().size());
        assertEquals(message, transaction.getMessages().get(0));
    }

    @Test
    void testAddMultipleMessages() {
        WebSocketMessage msg1 = new WebSocketMessage(
            true, WebSocketMessage.Type.TEXT, "Client 1".getBytes(), System.currentTimeMillis()
        );
        WebSocketMessage msg2 = new WebSocketMessage(
            false, WebSocketMessage.Type.TEXT, "Server 1".getBytes(), System.currentTimeMillis()
        );
        WebSocketMessage msg3 = new WebSocketMessage(
            true, WebSocketMessage.Type.TEXT, "Client 2".getBytes(), System.currentTimeMillis()
        );

        transaction.addMessage(msg1);
        transaction.addMessage(msg2);
        transaction.addMessage(msg3);

        assertEquals(3, transaction.getMessages().size());
    }

    @Test
    void testGetClientMessages() {
        transaction.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT, "C1".getBytes(), 1L));
        transaction.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT, "S1".getBytes(), 2L));
        transaction.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT, "C2".getBytes(), 3L));
        transaction.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT, "S2".getBytes(), 4L));

        List<WebSocketMessage> clientMessages = transaction.getClientMessages();
        assertEquals(2, clientMessages.size());
        assertTrue(clientMessages.stream().allMatch(WebSocketMessage::isFromClient));
    }

    @Test
    void testGetServerMessages() {
        transaction.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT, "C1".getBytes(), 1L));
        transaction.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT, "S1".getBytes(), 2L));
        transaction.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT, "C2".getBytes(), 3L));
        transaction.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT, "S2".getBytes(), 4L));

        List<WebSocketMessage> serverMessages = transaction.getServerMessages();
        assertEquals(2, serverMessages.size());
        assertTrue(serverMessages.stream().allMatch(WebSocketMessage::isFromServer));
    }

    @Test
    void testAddHandshakeHeader() {
        transaction.addHandshakeHeader("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==");
        transaction.addHandshakeHeader("Sec-WebSocket-Version", "13");

        assertEquals(2, transaction.getHandshakeHeaders().size());
        assertEquals("dGhlIHNhbXBsZSBub25jZQ==", transaction.getHandshakeHeaders().get("Sec-WebSocket-Key"));
        assertEquals("13", transaction.getHandshakeHeaders().get("Sec-WebSocket-Version"));
    }

    @Test
    void testGenerateConnectionId() {
        String connectionId = transaction.generateConnectionId();
        assertNotNull(connectionId);
        assertTrue(connectionId.startsWith("ws_"));
        assertTrue(connectionId.length() > 3);
    }

    @Test
    void testConnectionIdIsStable() {
        String id1 = transaction.generateConnectionId();
        String id2 = transaction.generateConnectionId();
        assertEquals(id1, id2, "Connection ID should be stable for same transaction");
    }

    @Test
    void testInvalidUrl() {
        assertThrows(IllegalArgumentException.class, () -> {
            new WebSocketTransaction("http://example.com");
        }, "Should reject non-WebSocket URLs");
    }

    @Test
    void testNullUrl() {
        assertThrows(IllegalArgumentException.class, () -> {
            new WebSocketTransaction(null);
        });
    }

    @Test
    void testGetMessageCount() {
        assertEquals(0, transaction.getMessageCount());
        
        transaction.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT, "1".getBytes(), 1L));
        assertEquals(1, transaction.getMessageCount());
        
        transaction.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT, "2".getBytes(), 2L));
        assertEquals(2, transaction.getMessageCount());
    }

    @Test
    void testToString() {
        String str = transaction.toString();
        assertNotNull(str);
        assertTrue(str.contains("ws://localhost:8080/chat"));
        assertTrue(str.contains("messages=0"));
    }
}




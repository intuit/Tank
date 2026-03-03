package com.intuit.tank.handler;

import com.intuit.tank.conversation.WebSocketMessage;
import com.intuit.tank.conversation.WebSocketTransaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WebSocketSession - per-connection state tracking during proxy recording.
 */
class WebSocketSessionTest {

    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Origin", "http://localhost:8090");
        session = new WebSocketSession("ws://localhost:8090/ws", headers);
    }

    @Test
    void testConnectionIdGeneration() {
        // Same URL should produce same connection ID
        WebSocketSession session2 = new WebSocketSession("ws://localhost:8090/ws", null);
        assertEquals(session.getConnectionId(), session2.getConnectionId());

        // Different URL should produce different ID
        WebSocketSession session3 = new WebSocketSession("ws://localhost:8090/other", null);
        assertNotEquals(session.getConnectionId(), session3.getConnectionId());

        // Connection ID format: ws_<8 hex chars>
        assertTrue(session.getConnectionId().matches("ws_[0-9a-f]{8}"));
    }

    @Test
    void testAddTextFrame() {
        WebSocketFrame frame = new WebSocketFrame(
            true,  // FIN
            WebSocketFrame.Opcode.TEXT,
            false,
            "Hello".getBytes(StandardCharsets.UTF_8),
            null
        );

        session.addFrame(frame, true);  // From client

        List<WebSocketSession.CapturedMessage> messages = session.getMessages();
        assertEquals(1, messages.size());

        WebSocketSession.CapturedMessage msg = messages.get(0);
        assertTrue(msg.fromClient);
        assertEquals(WebSocketFrame.Opcode.TEXT, msg.type);
        assertEquals("Hello", msg.getPayloadAsText());
    }

    @Test
    void testAddBinaryFrame() {
        byte[] binaryData = new byte[] {0x01, 0x02, 0x03, 0x04};
        WebSocketFrame frame = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.BINARY,
            false,
            binaryData,
            null
        );

        session.addFrame(frame, false);  // From server

        List<WebSocketSession.CapturedMessage> messages = session.getMessages();
        assertEquals(1, messages.size());

        WebSocketSession.CapturedMessage msg = messages.get(0);
        assertFalse(msg.fromClient);
        assertEquals(WebSocketFrame.Opcode.BINARY, msg.type);
        assertArrayEquals(binaryData, msg.payload);
    }

    @Test
    void testControlFramesIgnored() {
        // PING frame should be ignored
        WebSocketFrame ping = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.PING,
            false,
            "ping".getBytes(),
            null
        );

        // PONG frame should be ignored
        WebSocketFrame pong = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.PONG,
            false,
            "pong".getBytes(),
            null
        );

        // CLOSE frame should be ignored
        WebSocketFrame close = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.CLOSE,
            false,
            new byte[] {0x03, (byte) 0xE8},  // Status 1000
            null
        );

        session.addFrame(ping, true);
        session.addFrame(pong, false);
        session.addFrame(close, true);

        // No messages should be recorded
        assertEquals(0, session.getMessageCount());
    }

    @Test
    void testFragmentedMessage() {
        // First fragment (FIN=0)
        WebSocketFrame frag1 = new WebSocketFrame(
            false,  // Not final
            WebSocketFrame.Opcode.TEXT,
            false,
            "Hel".getBytes(StandardCharsets.UTF_8),
            null
        );

        // Continuation (FIN=0)
        WebSocketFrame frag2 = new WebSocketFrame(
            false,
            WebSocketFrame.Opcode.CONTINUATION,
            false,
            "lo ".getBytes(StandardCharsets.UTF_8),
            null
        );

        // Final continuation (FIN=1)
        WebSocketFrame frag3 = new WebSocketFrame(
            true,  // Final
            WebSocketFrame.Opcode.CONTINUATION,
            false,
            "World".getBytes(StandardCharsets.UTF_8),
            null
        );

        session.addFrame(frag1, true);
        assertEquals(0, session.getMessageCount());  // Not complete yet

        session.addFrame(frag2, true);
        assertEquals(0, session.getMessageCount());  // Still not complete

        session.addFrame(frag3, true);
        assertEquals(1, session.getMessageCount());  // Now complete

        WebSocketSession.CapturedMessage msg = session.getMessages().get(0);
        assertEquals("Hello World", msg.getPayloadAsText());
    }

    @Test
    void testBidirectionalMessages() {
        // Client sends
        WebSocketFrame clientMsg = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.TEXT,
            true,  // Masked (client→server)
            "{\"action\":\"subscribe\"}".getBytes(StandardCharsets.UTF_8),
            null
        );

        // Server responds
        WebSocketFrame serverMsg = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.TEXT,
            false,  // Not masked (server→client)
            "{\"status\":\"ok\"}".getBytes(StandardCharsets.UTF_8),
            null
        );

        session.addFrame(clientMsg, true);
        session.addFrame(serverMsg, false);

        List<WebSocketSession.CapturedMessage> messages = session.getMessages();
        assertEquals(2, messages.size());

        assertTrue(messages.get(0).fromClient);
        assertFalse(messages.get(1).fromClient);
    }

    @Test
    void testClose() {
        assertFalse(session.isClosed());
        assertEquals(0, session.getEndTime());

        session.close();

        assertTrue(session.isClosed());
        assertTrue(session.getEndTime() > 0);

        // Adding frames after close should be ignored
        WebSocketFrame frame = new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.TEXT,
            false,
            "Should be ignored".getBytes(),
            null
        );
        session.addFrame(frame, true);

        assertEquals(0, session.getMessageCount());
    }

    @Test
    void testToTransaction() {
        // Add some messages
        session.addFrame(new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.TEXT,
            true,
            "Hello".getBytes(StandardCharsets.UTF_8),
            null
        ), true);

        session.addFrame(new WebSocketFrame(
            true,
            WebSocketFrame.Opcode.TEXT,
            false,
            "Echo: Hello".getBytes(StandardCharsets.UTF_8),
            null
        ), false);

        session.close();

        // Convert to transaction
        WebSocketTransaction tx = session.toTransaction();

        assertEquals("ws://localhost:8090/ws", tx.getUrl());
        assertEquals(2, tx.getMessageCount());

        List<WebSocketMessage> messages = tx.getMessages();
        
        // First message: client→server
        assertTrue(messages.get(0).isFromClient());
        assertEquals(WebSocketMessage.Type.TEXT, messages.get(0).getType());
        assertEquals("Hello", messages.get(0).getContentAsString());

        // Second message: server→client
        assertFalse(messages.get(1).isFromClient());
        assertEquals("Echo: Hello", messages.get(1).getContentAsString());
    }

    @Test
    void testToString() {
        String str = session.toString();
        assertTrue(str.contains("ws://localhost:8090/ws"));
        assertTrue(str.contains("messages=0"));
        assertTrue(str.contains("closed=false"));
    }
}

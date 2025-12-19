package com.intuit.tank.conversation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD: Test-first for WebSocketMessage model
 */
public class WebSocketMessageTest {

    @Test
    void testCreateClientMessage() {
        long timestamp = System.currentTimeMillis();
        WebSocketMessage message = new WebSocketMessage(
            true,                    // from_client
            WebSocketMessage.Type.TEXT,
            "Hello Server".getBytes(),
            timestamp
        );

        assertTrue(message.isFromClient());
        assertEquals(WebSocketMessage.Type.TEXT, message.getType());
        assertArrayEquals("Hello Server".getBytes(), message.getContent());
        assertEquals(timestamp, message.getTimestamp());
    }

    @Test
    void testCreateServerMessage() {
        long timestamp = System.currentTimeMillis();
        WebSocketMessage message = new WebSocketMessage(
            false,                   // from_server
            WebSocketMessage.Type.TEXT,
            "Hello Client".getBytes(),
            timestamp
        );

        assertFalse(message.isFromClient());
        assertTrue(message.isFromServer());
        assertEquals(WebSocketMessage.Type.TEXT, message.getType());
    }

    @Test
    void testBinaryMessage() {
        byte[] binaryData = new byte[]{0x01, 0x02, 0x03, 0x04};
        WebSocketMessage message = new WebSocketMessage(
            true,
            WebSocketMessage.Type.BINARY,
            binaryData,
            System.currentTimeMillis()
        );

        assertEquals(WebSocketMessage.Type.BINARY, message.getType());
        assertArrayEquals(binaryData, message.getContent());
        assertTrue(message.isBinary());
        assertFalse(message.isText());
    }

    @Test
    void testTextMessage() {
        WebSocketMessage message = new WebSocketMessage(
            true,
            WebSocketMessage.Type.TEXT,
            "Test".getBytes(),
            System.currentTimeMillis()
        );

        assertTrue(message.isText());
        assertFalse(message.isBinary());
    }

    @Test
    void testGetContentAsString() {
        String textContent = "Hello WebSocket";
        WebSocketMessage message = new WebSocketMessage(
            true,
            WebSocketMessage.Type.TEXT,
            textContent.getBytes(),
            System.currentTimeMillis()
        );

        assertEquals(textContent, message.getContentAsString());
    }

    @Test
    void testMessageWithNullContent() {
        assertThrows(IllegalArgumentException.class, () -> {
            new WebSocketMessage(true, WebSocketMessage.Type.TEXT, null, System.currentTimeMillis());
        });
    }

    @Test
    void testMessageTypeEnum() {
        assertEquals(2, WebSocketMessage.Type.values().length);
        assertNotNull(WebSocketMessage.Type.TEXT);
        assertNotNull(WebSocketMessage.Type.BINARY);
    }
}




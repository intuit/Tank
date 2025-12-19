package com.intuit.tank.handler;

import org.junit.jupiter.api.Test;
import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.MutableRequestHeader;
import org.owasp.proxy.http.MutableResponseHeader;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TDD: Test-first for WebSocket upgrade detection
 */
public class WebSocketUpgradeDetectionTest {

    @Test
    void testIsWebSocketUpgradeRequest() throws MessageFormatException {
        MutableRequestHeader request = new MutableRequestHeader.Impl();
        request.setStartLine("GET /chat HTTP/1.1");
        request.setHeader("Host", "localhost:8080");
        request.setHeader("Upgrade", "websocket");
        request.setHeader("Connection", "Upgrade");
        request.setHeader("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==");
        request.setHeader("Sec-WebSocket-Version", "13");

        assertTrue(WebSocketUpgradeDetector.isWebSocketUpgradeRequest(request));
    }

    @Test
    void testIsNotWebSocketUpgradeRequestMissingUpgradeHeader() throws MessageFormatException {
        MutableRequestHeader request = new MutableRequestHeader.Impl();
        request.setStartLine("GET /api/users HTTP/1.1");
        request.setHeader("Host", "localhost:8080");
        request.setHeader("Connection", "keep-alive");

        assertFalse(WebSocketUpgradeDetector.isWebSocketUpgradeRequest(request));
    }

    @Test
    void testIsNotWebSocketUpgradeRequestWrongUpgradeValue() throws MessageFormatException {
        MutableRequestHeader request = new MutableRequestHeader.Impl();
        request.setStartLine("GET /chat HTTP/1.1");
        request.setHeader("Host", "localhost:8080");
        request.setHeader("Upgrade", "h2c");  // HTTP/2 upgrade, not WebSocket
        request.setHeader("Connection", "Upgrade");

        assertFalse(WebSocketUpgradeDetector.isWebSocketUpgradeRequest(request));
    }

    @Test
    void testIsNotWebSocketUpgradeRequestMissingConnectionHeader() throws MessageFormatException {
        MutableRequestHeader request = new MutableRequestHeader.Impl();
        request.setStartLine("GET /chat HTTP/1.1");
        request.setHeader("Host", "localhost:8080");
        request.setHeader("Upgrade", "websocket");

        assertFalse(WebSocketUpgradeDetector.isWebSocketUpgradeRequest(request));
    }

    @Test
    void testIsWebSocketUpgradeResponse() throws MessageFormatException {
        MutableResponseHeader response = new MutableResponseHeader.Impl();
        response.setStartLine("HTTP/1.1 101 Switching Protocols");
        response.setHeader("Upgrade", "websocket");
        response.setHeader("Connection", "Upgrade");
        response.setHeader("Sec-WebSocket-Accept", "s3pPLMBiTxaQ9kYGzzhZRbK+xOo=");

        assertTrue(WebSocketUpgradeDetector.isWebSocketUpgradeResponse(response));
    }

    @Test
    void testIsNotWebSocketUpgradeResponseWrongStatus() throws MessageFormatException {
        MutableResponseHeader response = new MutableResponseHeader.Impl();
        response.setStartLine("HTTP/1.1 200 OK");
        response.setHeader("Upgrade", "websocket");
        response.setHeader("Connection", "Upgrade");

        assertFalse(WebSocketUpgradeDetector.isWebSocketUpgradeResponse(response));
    }

    @Test
    void testIsNotWebSocketUpgradeResponseMissingUpgradeHeader() throws MessageFormatException {
        MutableResponseHeader response = new MutableResponseHeader.Impl();
        response.setStartLine("HTTP/1.1 101 Switching Protocols");
        response.setHeader("Connection", "Upgrade");

        assertFalse(WebSocketUpgradeDetector.isWebSocketUpgradeResponse(response));
    }

    @Test
    void testCaseInsensitiveHeaderMatching() throws MessageFormatException {
        MutableRequestHeader request = new MutableRequestHeader.Impl();
        request.setStartLine("GET /chat HTTP/1.1");
        request.setHeader("Host", "localhost:8080");
        request.setHeader("upgrade", "WebSocket");  // Lowercase header, mixed case value
        request.setHeader("connection", "upgrade");

        assertTrue(WebSocketUpgradeDetector.isWebSocketUpgradeRequest(request),
                  "Should be case-insensitive");
    }

    @Test
    void testExtractWebSocketUrl() throws MessageFormatException {
        MutableRequestHeader request = new MutableRequestHeader.Impl();
        request.setStartLine("GET /chat HTTP/1.1");
        request.setHeader("Host", "localhost:8080");
        request.setSsl(false);

        String url = WebSocketUpgradeDetector.extractWebSocketUrl(request);
        assertEquals("ws://localhost:8080/chat", url);
    }

    @Test
    void testExtractWebSocketUrlWss() throws MessageFormatException {
        MutableRequestHeader request = new MutableRequestHeader.Impl();
        request.setStartLine("GET /notifications HTTP/1.1");
        request.setHeader("Host", "example.com");
        request.setSsl(true);

        String url = WebSocketUpgradeDetector.extractWebSocketUrl(request);
        assertEquals("wss://example.com/notifications", url);
    }

    @Test
    void testExtractWebSocketUrlWithQuery() throws MessageFormatException {
        MutableRequestHeader request = new MutableRequestHeader.Impl();
        request.setStartLine("GET /chat?user=john&room=lobby HTTP/1.1");
        request.setHeader("Host", "localhost:8080");
        request.setSsl(false);

        String url = WebSocketUpgradeDetector.extractWebSocketUrl(request);
        assertEquals("ws://localhost:8080/chat?user=john&room=lobby", url);
    }
}

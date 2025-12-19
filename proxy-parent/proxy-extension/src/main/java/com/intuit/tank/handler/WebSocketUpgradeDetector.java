package com.intuit.tank.handler;

import org.owasp.proxy.http.MessageFormatException;
import org.owasp.proxy.http.RequestHeader;
import org.owasp.proxy.http.ResponseHeader;

/**
 * Detects WebSocket upgrade requests and responses.
 * 
 * A WebSocket connection starts as an HTTP request with specific headers:
 * - GET method
 * - Upgrade: websocket
 * - Connection: Upgrade
 * - Sec-WebSocket-Key: ...
 * - Sec-WebSocket-Version: 13
 * 
 * If the server accepts, it responds with HTTP 101:
 * - HTTP/1.1 101 Switching Protocols
 * - Upgrade: websocket
 * - Connection: Upgrade
 * - Sec-WebSocket-Accept: ...
 * 
 * After this handshake, the connection switches to WebSocket framing protocol.
 */
public class WebSocketUpgradeDetector {

    /**
     * Check if an HTTP request is a WebSocket upgrade request.
     * 
     * @param request HTTP request to check
     * @return true if this is a WebSocket upgrade request
     */
    public static boolean isWebSocketUpgradeRequest(RequestHeader request) {
        try {
            // Must have Upgrade: websocket header
            String upgradeHeader = request.getHeader("Upgrade");
            if (upgradeHeader == null || !upgradeHeader.trim().equalsIgnoreCase("websocket")) {
                return false;
            }

            // Must have Connection: Upgrade header
            String connectionHeader = request.getHeader("Connection");
            if (connectionHeader == null || !connectionHeader.toLowerCase().contains("upgrade")) {
                return false;
            }

            // Should be GET method (per RFC 6455)
            String method = request.getMethod();
            if (method != null && !method.equalsIgnoreCase("GET")) {
                return false;
            }

            return true;
        } catch (MessageFormatException e) {
            return false;
        }
    }

    /**
     * Check if an HTTP response is a WebSocket upgrade response (101).
     * 
     * @param response HTTP response to check
     * @return true if this is a successful WebSocket upgrade response
     */
    public static boolean isWebSocketUpgradeResponse(ResponseHeader response) {
        try {
            // Must be 101 Switching Protocols
            String status = response.getStatus();
            if (status == null || !status.equals("101")) {
                return false;
            }

            // Must have Upgrade: websocket header
            String upgradeHeader = response.getHeader("Upgrade");
            if (upgradeHeader == null || !upgradeHeader.trim().equalsIgnoreCase("websocket")) {
                return false;
            }

            // Must have Connection: Upgrade header
            String connectionHeader = response.getHeader("Connection");
            if (connectionHeader == null || !connectionHeader.toLowerCase().contains("upgrade")) {
                return false;
            }

            return true;
        } catch (MessageFormatException e) {
            return false;
        }
    }

    /**
     * Extract the WebSocket URL from the HTTP upgrade request.
     * Converts http:// to ws:// and https:// to wss://
     * 
     * @param request HTTP upgrade request
     * @return WebSocket URL (ws:// or wss://)
     */
    public static String extractWebSocketUrl(RequestHeader request) {
        try {
            String host = request.getHeader("Host");
            if (host == null) {
                throw new IllegalArgumentException("Missing Host header");
            }

            boolean isSsl = request.isSsl();
            String scheme = isSsl ? "wss" : "ws";

            // Get path from request line (e.g., "GET /chat HTTP/1.1" -> "/chat")
            String startLine = request.getStartLine();
            String[] parts = startLine.split("\\s+");
            String path = parts.length > 1 ? parts[1] : "/";

            return String.format("%s://%s%s", scheme, host, path);
        } catch (MessageFormatException e) {
            throw new IllegalArgumentException("Invalid request", e);
        }
    }
}




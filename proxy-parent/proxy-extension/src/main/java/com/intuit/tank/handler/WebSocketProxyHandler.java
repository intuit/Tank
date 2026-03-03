package com.intuit.tank.handler;

import java.net.Socket;

/**
 * Interface for handlers that can provide server socket for WebSocket handoff.
 * 
 * After an HTTP 101 Switching Protocols response, the connection switches
 * from HTTP to WebSocket framing. This interface allows the proxy to
 * retrieve the server socket and hand off to WebSocket relay.
 */
public interface WebSocketProxyHandler {

    /**
     * Get the server socket for WebSocket relay after 101 upgrade.
     * 
     * @return The server socket, or null if not available
     */
    Socket getServerSocket();

    /**
     * Release ownership of the server socket without closing it.
     * Called after successful handoff to WebSocket relay.
     */
    void releaseServerSocket();
}

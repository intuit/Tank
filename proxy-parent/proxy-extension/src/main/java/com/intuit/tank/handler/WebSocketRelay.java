package com.intuit.tank.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Bidirectional WebSocket relay that forwards frames between client and server
 * while capturing them for recording.
 * 
 * Architecture:
 * - Two threads: client→server and server→client
 * - Each thread reads frames, logs them to session, and forwards unchanged
 * - Relay stops when either side disconnects or sends CLOSE frame
 * 
 * Thread safety: WebSocketSession.addFrame() is thread-safe (uses CopyOnWriteArrayList)
 */
public class WebSocketRelay {

    private static final Logger LOG = LogManager.getLogger(WebSocketRelay.class);
    private static final int SOCKET_TIMEOUT_MS = 30_000; // 30 second read timeout

    private final Socket clientSocket;
    private final Socket serverSocket;
    private final WebSocketSession session;
    private final WebSocketFrameCodec codec;
    private final AtomicBoolean running;
    private final CountDownLatch completionLatch;

    private Thread clientToServerThread;
    private Thread serverToClientThread;

    /**
     * Create a WebSocket relay
     * 
     * @param clientSocket Socket connected to client (browser)
     * @param serverSocket Socket connected to server (origin)
     * @param session Session to record messages to
     */
    public WebSocketRelay(Socket clientSocket, Socket serverSocket, WebSocketSession session) {
        this.clientSocket = clientSocket;
        this.serverSocket = serverSocket;
        this.session = session;
        this.codec = new WebSocketFrameCodec();
        this.running = new AtomicBoolean(true);
        this.completionLatch = new CountDownLatch(2);  // Wait for both threads
    }

    /**
     * Start the relay. This method returns immediately.
     * The relay runs in background threads until stopped.
     */
    public void start() {
        LOG.info("[WebSocket Relay] Starting for {}", session.getUrl());

        clientToServerThread = new Thread(() -> relay(true), "WS-Client→Server-" + session.getConnectionId());
        serverToClientThread = new Thread(() -> relay(false), "WS-Server→Client-" + session.getConnectionId());

        clientToServerThread.setDaemon(true);
        serverToClientThread.setDaemon(true);

        clientToServerThread.start();
        serverToClientThread.start();
    }

    /**
     * Relay loop for one direction
     * 
     * @param clientToServer True for client→server, false for server→client
     */
    private void relay(boolean clientToServer) {
        String direction = clientToServer ? "Client→Server" : "Server→Client";
        Socket readSocket = clientToServer ? clientSocket : serverSocket;
        Socket writeSocket = clientToServer ? serverSocket : clientSocket;

        try {
            // Set SO_TIMEOUT to prevent infinite blocking on read
            readSocket.setSoTimeout(SOCKET_TIMEOUT_MS);

            InputStream in = readSocket.getInputStream();
            OutputStream out = writeSocket.getOutputStream();

            while (running.get() && !readSocket.isClosed() && !writeSocket.isClosed()) {
                try {
                    WebSocketFrame frame = codec.readFrame(in);

                    // Log the frame
                    if (frame.getOpcode() == WebSocketFrame.Opcode.TEXT) {
                        LOG.debug("[WebSocket {}] TEXT: {}", direction, frame.getPayloadAsText());
                    } else if (frame.getOpcode() == WebSocketFrame.Opcode.BINARY) {
                        LOG.debug("[WebSocket {}] BINARY: {} bytes", direction, frame.getPayload().length);
                    } else if (frame.getOpcode() == WebSocketFrame.Opcode.CLOSE) {
                        LOG.info("[WebSocket {}] CLOSE frame received", direction);
                    } else {
                        LOG.trace("[WebSocket {}] Control frame: {}", direction, frame.getOpcode());
                    }

                    // Record to session (skips control frames internally)
                    session.addFrame(frame, clientToServer);

                    // Forward frame unchanged
                    // Client→Server frames stay masked, Server→Client frames stay unmasked
                    codec.writeFramePreserveMask(out, frame);

                    // Stop on CLOSE frame
                    if (frame.getOpcode() == WebSocketFrame.Opcode.CLOSE) {
                        LOG.info("[WebSocket Relay] CLOSE received from {}, stopping relay", direction);
                        stop();
                        break;
                    }

                } catch (SocketTimeoutException e) {
                    // SO_TIMEOUT fired — check if relay is still running and retry
                    if (running.get()) {
                        LOG.trace("[WebSocket {}] Read timeout, retrying...", direction);
                        continue;
                    }
                    break;
                } catch (SocketException e) {
                    // Socket closed - normal termination
                    LOG.debug("[WebSocket {}] Socket closed: {}", direction, e.getMessage());
                    break;
                } catch (IOException e) {
                    if (running.get()) {
                        LOG.warn("[WebSocket {}] Read error: {}", direction, e.getMessage());
                    }
                    break;
                }
            }

        } catch (IOException e) {
            LOG.error("[WebSocket {}] Failed to get streams: {}", direction, e.getMessage());
        } finally {
            completionLatch.countDown();
            LOG.debug("[WebSocket {}] Relay thread exiting", direction);
        }
    }

    /**
     * Stop the relay and close both sockets
     */
    public void stop() {
        if (running.compareAndSet(true, false)) {
            LOG.info("[WebSocket Relay] Stopping relay for {}", session.getUrl());

            session.close();

            // Close sockets to unblock any blocking reads
            closeQuietly(clientSocket);
            closeQuietly(serverSocket);
        }
    }

    /**
     * Wait for the relay to complete (both threads finished)
     */
    public void awaitCompletion() throws InterruptedException {
        completionLatch.await();
    }

    /**
     * Wait for completion with timeout
     * 
     * @param timeoutMs Maximum time to wait in milliseconds
     * @return true if completed, false if timeout
     */
    public boolean awaitCompletion(long timeoutMs) throws InterruptedException {
        return completionLatch.await(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    /**
     * Check if relay is still running
     */
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Get the session (for retrieving recorded messages)
     */
    public WebSocketSession getSession() {
        return session;
    }

    private void closeQuietly(Socket socket) {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                LOG.trace("Error closing socket: {}", e.getMessage());
            }
        }
    }
}

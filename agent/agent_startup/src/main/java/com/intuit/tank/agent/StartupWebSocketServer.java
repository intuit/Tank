package com.intuit.tank.agent;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Agent-side WebSocket server that receives startup files pushed by the controller during bootstrap.
 * Runs over WebSocket-over-HTTP/2 (RFC 8441) using Jetty 12; the same connector also
 * accepts HTTP/1.1 WebSocket upgrades so a controller may negotiate either transport via ALPN/prior
 * knowledge.
 */
public class StartupWebSocketServer {

    private static final Logger logger = LogManager.getLogger(StartupWebSocketServer.class);
    private static final String TANK_AGENT_DIR = "/opt/tank_agent";
    private static final String API_HARNESS_JAR = "apiharness-1.0-all.jar";
    private static final String SUPPORT_JAR_FILE_TYPE = "support_jar";
    private static final String START_AGENT_SCRIPT = "startAgent.sh";
    private static final String STARTUP_SCRIPT_FILE_TYPE = "startup_script";
    // Only small text envelopes are aggregated; file content is streamed frame by frame to disk.
    private static final long MAX_WS_MESSAGE_BYTES = 1024L * 1024;
    // Raise Jetty's 30s default WS idle timeout so a slow/paused bootstrap transfer isn't dropped.
    private static final long WS_IDLE_TIMEOUT_MS =
            Math.max(60_000L, Long.getLong("tank.ws.idleTimeoutMs", 600_000L));

    private final int port;
    private final String instanceId;
    private final String jobId;
    private final int capacity;
    private final File agentDir;
    private final String agentSessionId = UUID.randomUUID().toString();
    private final CompletableFuture<File> harnessJarFuture = new CompletableFuture<>();
    private final CompletableFuture<Void> serverStoppedFuture = new CompletableFuture<>();

    private final Server server;
    private boolean reuseAddr;

    private Session currentFileConnection;
    private FileOutputStream currentFileStream;
    private File currentTempFile;
    private File targetFile;
    private String currentFileName;
    private String currentFileId;
    private long receivedBytes;
    private long expectedBytes;
    private long transferStartedAtNs;

    public StartupWebSocketServer(int port, String instanceId, String jobId, int capacity) {
        this(port, instanceId, jobId, capacity, new File(TANK_AGENT_DIR));
    }

    StartupWebSocketServer(int port, String instanceId, String jobId, int capacity, File agentDir) {
        this.port = port;
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.capacity = capacity;
        this.agentDir = agentDir;
        this.server = new Server();
    }

    public void setReuseAddr(boolean reuseAddr) {
        this.reuseAddr = reuseAddr;
    }

    /** Starts the embedded Jetty server. Mirrors the previous blocking-start contract. */
    public void start() throws IOException {
        HttpConfiguration httpConfig = new HttpConfiguration();
        HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
        // h2c cleartext HTTP/2 — enables the RFC 8441 extended CONNECT upgrade path.
        HTTP2CServerConnectionFactory h2c = new HTTP2CServerConnectionFactory(httpConfig);
        h2c.setConnectProtocolEnabled(true);

        ServerConnector connector = new ServerConnector(server, http11, h2c);
        connector.setPort(port);
        connector.setReuseAddress(reuseAddr);
        connector.setIdleTimeout(WS_IDLE_TIMEOUT_MS);
        server.addConnector(connector);

        WebSocketUpgradeHandler wsHandler = WebSocketUpgradeHandler.from(server, container -> {
            container.setMaxBinaryMessageSize(MAX_WS_MESSAGE_BYTES);
            container.setMaxTextMessageSize(MAX_WS_MESSAGE_BYTES);
            container.setIdleTimeout(java.time.Duration.ofMillis(WS_IDLE_TIMEOUT_MS));
            container.addMapping("/", (upgradeRequest, upgradeResponse, callback) -> new StartupEndpoint(this));
        });
        server.setHandler(wsHandler);

        try {
            server.start();
        } catch (Exception e) {
            throw new IOException("Failed to start startup WS server on port " + port, e);
        }
        logger.info("Startup WS server started on port {}", port);
    }

    /** Stops the embedded server. Signature kept for callers migrating from the old library. */
    public void stop(long timeoutMillis) throws InterruptedException {
        try {
            server.setStopTimeout(timeoutMillis);
            server.stop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw e;
        } catch (Exception e) {
            logger.warn("Error stopping startup WS server: {}", e.getMessage());
        } finally {
            if (!harnessJarFuture.isDone()) {
                resetFileStateForRetry(null);
                logger.warn("Startup WS server stopped before harness JAR was received");
            }
            serverStoppedFuture.complete(null);
        }
    }

    public File awaitHarnessJar(long timeoutMillis) {
        try {
            return harnessJarFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            logger.error("Timed out waiting for harness JAR: {}", e.getMessage());
            return null;
        }
    }

    public File awaitHarnessJarOrServerStop(long timeoutMillis) {
        try {
            Object result = CompletableFuture.anyOf(harnessJarFuture, serverStoppedFuture)
                    .get(timeoutMillis, TimeUnit.MILLISECONDS);
            if (result instanceof File) {
                return (File) result;
            }
            if (serverStoppedFuture.isDone() && !harnessJarFuture.isDone()) {
                logger.warn("Startup WS server stopped before harness JAR was received");
            }
            return null;
        } catch (java.util.concurrent.TimeoutException e) {
            logger.error("Timed out waiting for harness JAR: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            if (serverStoppedFuture.isDone() && !harnessJarFuture.isDone()) {
                logger.warn("Startup WS server stopped before harness JAR was received: {}", e.getMessage());
            } else {
                logger.error("Error waiting for harness JAR: {}", e.getMessage(), e);
            }
            return null;
        }
    }

    public boolean hasServerStopped() {
        return serverStoppedFuture.isDone();
    }

    synchronized void handleFileOffer(Session conn, AgentWsEnvelope envelope) {
        String offeredFileName = envelope.getFileName() != null ? envelope.getFileName() : "";
        String fileName = new File(offeredFileName).getName();
        String fileType = envelope.getFileType();
        boolean harnessJar = API_HARNESS_JAR.equals(fileName)
                && SUPPORT_JAR_FILE_TYPE.equalsIgnoreCase(fileType);
        boolean startupScript = START_AGENT_SCRIPT.equals(fileName)
                && STARTUP_SCRIPT_FILE_TYPE.equalsIgnoreCase(fileType);
        if (!offeredFileName.equals(fileName) || (!harnessJar && !startupScript)) {
            sendFileAck(conn, envelope.getFileId(), AckStatus.failed, "unsupported_startup_file");
            return;
        }

        closeCurrentFileQuietly();
        try {
            if (!agentDir.exists() && !agentDir.mkdirs()) {
                throw new IOException("Unable to create " + agentDir.getAbsolutePath());
            }
            currentFileConnection = conn;
            currentFileName = fileName;
            targetFile = new File(agentDir, fileName);
            currentTempFile = new File(targetFile.getAbsolutePath() + ".part");
            currentFileStream = new FileOutputStream(currentTempFile);
            currentFileId = envelope.getFileId();
            receivedBytes = 0;
            expectedBytes = envelope.getTotalBytes() != null ? envelope.getTotalBytes() : -1L;
            transferStartedAtNs = System.nanoTime();
            sendFileAck(conn, envelope.getFileId(), AckStatus.ok, null);
        } catch (IOException e) {
            closeCurrentFileQuietly();
            sendFileAck(conn, envelope.getFileId(), AckStatus.failed, e.getMessage());
        }
    }

    /** Writes one frame of the current file message; on the final frame finalizes the file and acks. */
    synchronized void handleFileData(Session conn, String fileId, ByteBuffer data, boolean last) {
        if (currentFileStream == null || currentFileId == null || !currentFileId.equals(fileId)) {
            if (last) {
                sendFileAck(conn, fileId, AckStatus.failed, "file_offer_not_found");
            }
            return;
        }

        try {
            receivedBytes += data.remaining();
            currentFileStream.getChannel().write(data);
            if (!last) {
                return;
            }
            boolean harnessJar = API_HARNESS_JAR.equals(currentFileName);
            File completedFile = finalizeStartupFile();
            sendFileAckSync(conn, fileId, AckStatus.complete, null);
            if (harnessJar) {
                harnessJarFuture.complete(completedFile);
            }
        } catch (Exception e) {
            failCurrentFileTransfer(conn, fileId, e);
        }
    }

    private void handlePing(Session conn, AgentWsEnvelope envelope) {
        try {
            AgentWsEnvelope pong = AgentWsEnvelope.pong(instanceId, agentSessionId, envelope.getPingId(), null);
            sendText(conn, pong.toJson());
        } catch (Exception e) {
            logger.warn("Failed to send startup WS pong: {}", e.getMessage());
        }
    }

    private File finalizeStartupFile() throws IOException {
        currentFileStream.close();
        currentFileStream = null;
        if (expectedBytes >= 0 && expectedBytes != receivedBytes) {
            throw new IOException("file size mismatch expected=" + expectedBytes + " actual=" + receivedBytes);
        }
        try {
            Files.move(currentTempFile.toPath(), targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(currentTempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        File completedFile = targetFile;
        if (START_AGENT_SCRIPT.equals(currentFileName) && !completedFile.setExecutable(true, false)) {
            throw new IOException("Unable to make " + completedFile.getAbsolutePath() + " executable");
        }
        long durationMs = transferStartedAtNs > 0L
                ? TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - transferStartedAtNs)
                : 0L;
        double throughputMiBps = durationMs > 0
                ? Math.round(((receivedBytes / (1024.0 * 1024.0)) / (durationMs / 1000.0)) * 100.0) / 100.0
                : 0.0;
        logger.info("Startup WS file received file={} bytes={} durationMs={} throughputMiBps={}",
                currentFileName, receivedBytes, durationMs, throughputMiBps);
        currentTempFile = null;
        targetFile = null;
        currentFileName = null;
        currentFileId = null;
        transferStartedAtNs = 0L;
        return completedFile;
    }

    private void sendFileAck(Session conn, String fileId, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, null, status, error);
            sendText(conn, ack.toJson());
        } catch (Exception e) {
            logger.warn("Failed to send startup WS file ack for {}: {}", fileId, e.getMessage());
        }
    }

    /** Sends a file ack and blocks until the frame is flushed, so it cannot be lost on shutdown. */
    private void sendFileAckSync(Session conn, String fileId, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, null, status, error);
            Callback.Completable callback = new Callback.Completable();
            conn.sendText(ack.toJson(), callback);
            callback.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("Failed to send startup WS file ack (sync) for {}: {}", fileId, e.getMessage());
        }
    }

    private void sendText(Session conn, String text) {
        conn.sendText(text, Callback.NOOP);
    }

    private synchronized void failCurrentFileTransfer(Session conn, String fileId, Exception error) {
        boolean harnessJar = API_HARNESS_JAR.equals(currentFileName);
        closeCurrentFileQuietly();
        currentFileId = null;
        sendFileAck(conn, fileId, AckStatus.failed, error.getMessage());
        if (harnessJar) {
            harnessJarFuture.completeExceptionally(error);
        }
    }

    private synchronized void handleFatalError(Exception ex) {
        closeCurrentFileQuietly();
        currentFileConnection = null;
        currentFileId = null;
        serverStoppedFuture.completeExceptionally(ex);
    }

    private synchronized void closeCurrentFileQuietly() {
        if (currentFileStream != null) {
            try {
                currentFileStream.close();
            } catch (IOException ignored) {
            }
            currentFileStream = null;
        }
    }

    private synchronized void resetFileStateForRetry(Session conn) {
        if (currentFileConnection != null && conn != null && conn != currentFileConnection) {
            logger.info("Ignoring stale startup WS reset from previous connection");
            return;
        }
        closeCurrentFileQuietly();
        currentFileConnection = null;
        currentFileId = null;
        // The next connection re-offers the file from the start and overwrites the .part file.
        logger.info("Startup WS connection state reset");
    }

    /**
     * Per-connection Jetty WebSocket endpoint. {@code AutoDemanding} means Jetty automatically demands
     * the next frame after each callback returns, matching the old library's push-style delivery.
     */
    public static class StartupEndpoint implements Session.Listener.AutoDemanding {

        private final StartupWebSocketServer server;
        private Session session;
        private AgentWsEnvelope.BinaryFileHeaderReader headerReader = new AgentWsEnvelope.BinaryFileHeaderReader();
        private String fileId;

        private StartupEndpoint(StartupWebSocketServer server) {
            this.server = server;
        }

        @Override
        public void onWebSocketOpen(Session session) {
            this.session = session;
            logger.info("Controller connected to startup WS server from {}", session.getRemoteSocketAddress());
            try {
                AgentWsEnvelope hello = AgentWsEnvelope.hello(
                        server.instanceId, server.jobId, server.agentSessionId, null, server.capacity, true);
                server.sendText(session, hello.toJson());
            } catch (Exception e) {
                logger.warn("Failed to send startup WS hello: {}", e.getMessage());
                session.close();
            }
        }

        @Override
        public void onWebSocketText(String message) {
            try {
                AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(message);
                if (envelope.getType() == null) {
                    logger.warn("Startup WS frame missing type");
                    return;
                }
                switch (envelope.getType()) {
                    case file_offer -> server.handleFileOffer(session, envelope);
                    case ping -> server.handlePing(session, envelope);
                    case ack -> logger.info("Startup WS received ack: {}", envelope.getAckForType());
                    case close -> session.close();
                    default -> logger.info("Startup WS ignoring frame type: {}", envelope.getType());
                }
            } catch (Exception e) {
                logger.warn("Failed handling startup WS message: {}", e.getMessage(), e);
            }
        }

        /**
         * Receives a file message frame by frame (Jetty does not aggregate it), so memory use is bounded by
         * the frame size regardless of file size. The first bytes of each message are the file header.
         */
        @Override
        public void onWebSocketPartialBinary(ByteBuffer payload, boolean last, Callback callback) {
            try {
                if (fileId == null) {
                    fileId = headerReader.read(payload);
                }
                if (fileId != null) {
                    server.handleFileData(session, fileId, payload, last);
                } else if (last) {
                    throw new IOException("Binary file message ended before header was complete");
                }
                callback.succeed();
            } catch (Exception e) {
                logger.warn("Failed handling startup WS binary message: {}", e.getMessage(), e);
                callback.fail(e);
            } finally {
                if (last) {
                    headerReader = new AgentWsEnvelope.BinaryFileHeaderReader();
                    fileId = null;
                }
            }
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            logger.info("Startup WS connection closed code={} reason={}", statusCode, reason);
            server.resetFileStateForRetry(session);
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            logger.warn("Startup WS error: {}", cause.getMessage(), cause);
            if (session == null) {
                logger.error("Startup WS server-level error — preserving partial file for restart");
                server.handleFatalError(cause instanceof Exception ? (Exception) cause : new IOException(cause));
                return;
            }
            server.resetFileStateForRetry(session);
        }
    }
}

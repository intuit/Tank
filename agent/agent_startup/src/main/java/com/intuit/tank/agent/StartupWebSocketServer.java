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
 * Agent-side WebSocket server that receives the harness JAR pushed by the controller during startup
 * bootstrap. Runs over WebSocket-over-HTTP/2 (RFC 8441) using Jetty 12; the same connector also
 * accepts HTTP/1.1 WebSocket upgrades so a controller may negotiate either transport via ALPN/prior
 * knowledge.
 */
public class StartupWebSocketServer {

    private static final Logger logger = LogManager.getLogger(StartupWebSocketServer.class);
    private static final String TANK_AGENT_DIR = "/opt/tank_agent";
    private static final String API_HARNESS_JAR = "apiharness-1.0-all.jar";
    private static final String SUPPORT_JAR_FILE_TYPE = "support_jar";
    // 2 MiB chunks * a healthy window can exceed Jetty's default frame/message limits.
    private static final long MAX_WS_MESSAGE_BYTES = 64L * 1024 * 1024;
    // Raise Jetty's 30s default WS idle timeout so a slow/paused bootstrap transfer isn't dropped.
    private static final long WS_IDLE_TIMEOUT_MS =
            Math.max(60_000L, Long.getLong("tank.ws.idleTimeoutMs", 600_000L));

    private final int port;
    private final String instanceId;
    private final String jobId;
    private final int capacity;
    private final String agentSessionId = UUID.randomUUID().toString();
    private final CompletableFuture<File> harnessJarFuture = new CompletableFuture<>();
    private final CompletableFuture<Void> serverStoppedFuture = new CompletableFuture<>();

    private final Server server;
    private boolean reuseAddr;

    private Session currentFileConnection;
    private FileOutputStream currentFileStream;
    private File currentTempFile;
    private File targetFile;
    private String currentFileId;
    private int receivedChunks;
    private int expectedChunks;
    private long receivedBytes;
    private long expectedBytes;
    private long transferStartedAtNs;

    public StartupWebSocketServer(int port, String instanceId, String jobId, int capacity) {
        this.port = port;
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.capacity = capacity;
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

    private synchronized void handleFileOffer(Session conn, AgentWsEnvelope envelope) {
        String fileName = new File(envelope.getFileName() != null ? envelope.getFileName() : "").getName();
        String fileType = envelope.getFileType();
        if (!API_HARNESS_JAR.equals(fileName) && !SUPPORT_JAR_FILE_TYPE.equalsIgnoreCase(fileType)) {
            sendFileAck(conn, envelope.getFileId(), null, AckStatus.failed, "unsupported_startup_file");
            return;
        }

        closeCurrentFileQuietly();
        long offerTotalBytes = envelope.getTotalBytes() != null ? envelope.getTotalBytes() : -1L;
        int offerTotalChunks = envelope.getTotalChunks() != null ? envelope.getTotalChunks() : 0;
        int offerChunkBytes = envelope.getChunkBytes() != null ? envelope.getChunkBytes() : 0;
        try {
            File agentDir = new File(TANK_AGENT_DIR);
            if (!agentDir.exists() && !agentDir.mkdirs()) {
                throw new IOException("Unable to create " + agentDir.getAbsolutePath());
            }
            currentFileConnection = conn;
            targetFile = new File(agentDir, API_HARNESS_JAR);
            currentTempFile = new File(targetFile.getAbsolutePath() + ".part");

            // Check for resumable partial file from a previous failed transfer
            if (currentTempFile.exists() && offerTotalBytes > 0 && offerChunkBytes > 0
                    && currentTempFile.length() > 0 && currentTempFile.length() < offerTotalBytes) {
                long partialBytes = currentTempFile.length();
                // Truncate to safe chunk boundary
                long safeResumeOffset = partialBytes - (partialBytes % offerChunkBytes);
                if (safeResumeOffset != partialBytes) {
                    logger.info("Truncating .part from {} to {} (chunk boundary alignment, chunkBytes={})",
                            partialBytes, safeResumeOffset, offerChunkBytes);
                    try (java.io.RandomAccessFile raf = new java.io.RandomAccessFile(currentTempFile, "rw")) {
                        raf.setLength(safeResumeOffset);
                    }
                    partialBytes = safeResumeOffset;
                }
                int resumeChunk = (int) (partialBytes / offerChunkBytes);
                logger.info("Resumable .part file found: {} bytes={}/{} resumeChunk={} chunkBytes={}",
                        currentTempFile.getAbsolutePath(), partialBytes, offerTotalBytes, resumeChunk, offerChunkBytes);
                currentFileStream = new FileOutputStream(currentTempFile, true); // append
                currentFileId = envelope.getFileId();
                receivedChunks = resumeChunk;
                expectedChunks = offerTotalChunks;
                receivedBytes = partialBytes;
                expectedBytes = offerTotalBytes;
                if (transferStartedAtNs == 0L) {
                    transferStartedAtNs = System.nanoTime();
                }
                sendFileAckWithResume(conn, envelope.getFileId(), AckStatus.resume, partialBytes, resumeChunk);
                return;
            }

            // Fresh transfer — delete stale .part if any
            if (currentTempFile.exists()) {
                currentTempFile.delete();
            }
            currentFileStream = new FileOutputStream(currentTempFile);
            currentFileId = envelope.getFileId();
            receivedChunks = 0;
            expectedChunks = offerTotalChunks;
            receivedBytes = 0;
            expectedBytes = offerTotalBytes;
            transferStartedAtNs = System.nanoTime();
            // Send explicit ok ack so controller doesn't wait 10s
            sendFileAck(conn, envelope.getFileId(), 0, AckStatus.ok, null);
        } catch (IOException e) {
            closeCurrentFileQuietly();
            sendFileAck(conn, envelope.getFileId(), null, AckStatus.failed, e.getMessage());
        }
    }

    private synchronized void handleFileChunk(Session conn, String fileId, Integer chunkIndex, byte[] payload) {
        if (currentFileStream == null || currentFileId == null || !currentFileId.equals(fileId)) {
            sendFileAck(conn, fileId, chunkIndex, AckStatus.failed, "file_offer_not_found");
            return;
        }

        try {
            currentFileStream.write(payload);
            receivedBytes += payload.length;
            receivedChunks++;
            sendFileAck(conn, fileId, chunkIndex, AckStatus.chunk_received, null);

            if (expectedBytes > 0 && receivedBytes >= expectedBytes) {
                File completedFile = finalizeHarnessJar();
                sendFileAckSync(conn, fileId, chunkIndex, AckStatus.complete, null);
                harnessJarFuture.complete(completedFile);
            }
        } catch (Exception e) {
            closeCurrentFileQuietly();
            sendFileAck(conn, fileId, chunkIndex, AckStatus.failed, e.getMessage());
            harnessJarFuture.completeExceptionally(e);
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

    private File finalizeHarnessJar() throws IOException {
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
        long durationMs = transferStartedAtNs > 0L
                ? TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - transferStartedAtNs)
                : 0L;
        double throughputMiBps = durationMs > 0
                ? Math.round(((receivedBytes / (1024.0 * 1024.0)) / (durationMs / 1000.0)) * 100.0) / 100.0
                : 0.0;
        logger.info("Startup WS harness JAR received bytes={} chunks={}/{} durationMs={} throughputMiBps={}",
                receivedBytes, receivedChunks, expectedChunks, durationMs, throughputMiBps);
        currentTempFile = null;
        targetFile = null;
        currentFileId = null;
        transferStartedAtNs = 0L;
        return completedFile;
    }

    private void sendFileAck(Session conn, String fileId, Integer chunkIndex, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, chunkIndex, status, error);
            sendText(conn, ack.toJson());
        } catch (Exception e) {
            logger.warn("Failed to send startup WS file ack for {}: {}", fileId, e.getMessage());
        }
    }

    /** Sends a file ack and blocks until the frame is flushed, so it cannot be lost on shutdown. */
    private void sendFileAckSync(Session conn, String fileId, Integer chunkIndex, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, chunkIndex, status, error);
            Callback.Completable callback = new Callback.Completable();
            conn.sendText(ack.toJson(), callback);
            callback.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            logger.warn("Failed to send startup WS file ack (sync) for {}: {}", fileId, e.getMessage());
        }
    }

    private void sendFileAckWithResume(Session conn, String fileId, AckStatus status, long resumeOffset, int resumeChunk) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, resumeChunk, status, null);
            ack.setResumeOffset(resumeOffset);
            sendText(conn, ack.toJson());
        } catch (Exception e) {
            logger.warn("Failed to send startup WS resume ack for {}: {}", fileId, e.getMessage());
        }
    }

    private void sendText(Session conn, String text) {
        conn.sendText(text, Callback.NOOP);
    }

    private synchronized void handleFatalError(Exception ex) {
        closeCurrentFileQuietly();
        if (currentTempFile != null && currentTempFile.exists()) {
            logger.info("Keeping partial bootstrap file after startup WS server error {} chunks={}/{} bytes={}/{}",
                    currentTempFile.getAbsolutePath(), receivedChunks, expectedChunks, receivedBytes, expectedBytes);
        }
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
        if (currentTempFile != null && currentTempFile.exists()) {
            logger.info("Keeping partial bootstrap file for resume {} chunks={}/{} bytes={}/{}",
                    currentTempFile.getAbsolutePath(), receivedChunks, expectedChunks, receivedBytes, expectedBytes);
        }
        currentFileConnection = null;
        // Keep currentTempFile, targetFile, receivedBytes, expectedBytes for resume
        // Reset only the stream and connection-specific state
        currentFileId = null;
        logger.info("Startup WS connection state reset — .part file preserved for resume");
    }

    /**
     * Per-connection Jetty WebSocket endpoint. {@code AutoDemanding} means Jetty automatically demands
     * the next frame after each callback returns, matching the old library's push-style delivery.
     */
    public static class StartupEndpoint implements Session.Listener.AutoDemanding {

        private final StartupWebSocketServer server;
        private Session session;

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
                    case file_chunk -> handleTextFileChunk(session, envelope);
                    case ping -> server.handlePing(session, envelope);
                    case ack -> logger.info("Startup WS received ack: {}", envelope.getAckForType());
                    case close -> session.close();
                    default -> logger.info("Startup WS ignoring frame type: {}", envelope.getType());
                }
            } catch (Exception e) {
                logger.warn("Failed handling startup WS message: {}", e.getMessage(), e);
            }
        }

        @Override
        public void onWebSocketBinary(ByteBuffer payload, Callback callback) {
            try {
                AgentWsEnvelope.BinaryFileChunk chunk = AgentWsEnvelope.fromBinaryFileChunk(payload);
                server.handleFileChunk(session, chunk.fileId(), chunk.chunkIndex(), chunk.payload());
                callback.succeed();
            } catch (Exception e) {
                logger.warn("Failed handling startup WS binary message: {}", e.getMessage(), e);
                callback.fail(e);
            }
        }

        private void handleTextFileChunk(Session conn, AgentWsEnvelope envelope) {
            try {
                byte[] payload = envelope.getChunkData() == null || envelope.getChunkData().isEmpty()
                        ? new byte[0]
                        : java.util.Base64.getDecoder().decode(envelope.getChunkData());
                server.handleFileChunk(conn, envelope.getFileId(), envelope.getChunkIndex(), payload);
            } catch (Exception e) {
                server.closeCurrentFileQuietly();
                server.sendFileAck(conn, envelope.getFileId(), envelope.getChunkIndex(), AckStatus.failed, e.getMessage());
                server.harnessJarFuture.completeExceptionally(e);
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

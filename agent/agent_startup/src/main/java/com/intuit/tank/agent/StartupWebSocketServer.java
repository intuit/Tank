package com.intuit.tank.agent;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class StartupWebSocketServer extends WebSocketServer {

    private static final Logger logger = LogManager.getLogger(StartupWebSocketServer.class);
    private static final String TANK_AGENT_DIR = "/opt/tank_agent";
    private static final String API_HARNESS_JAR = "apiharness-1.0-all.jar";
    private static final String SUPPORT_JAR_FILE_TYPE = "support_jar";

    private final String instanceId;
    private final String jobId;
    private final int capacity;
    private final String agentSessionId = UUID.randomUUID().toString();
    private final CompletableFuture<File> harnessJarFuture = new CompletableFuture<>();
    private final CompletableFuture<Void> serverStoppedFuture = new CompletableFuture<>();

    private WebSocket currentFileConnection;
    private FileOutputStream currentFileStream;
    private File currentTempFile;
    private File targetFile;
    private String currentFileId;
    private int receivedChunks;
    private int expectedChunks;
    private long receivedBytes;
    private long expectedBytes;

    public StartupWebSocketServer(int port, String instanceId, String jobId, int capacity) {
        super(new InetSocketAddress(port));
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.capacity = capacity;
    }

    @Override
    public void run() {
        try {
            super.run();
        } catch (Throwable t) {
            logger.error("Startup WS server thread crashed", t);
            serverStoppedFuture.completeExceptionally(t);
        } finally {
            if (!harnessJarFuture.isDone()) {
                resetFileStateForRetry(null);
                logger.warn("Startup WS server thread exited before harness JAR was received");
            } else {
                logger.info("Startup WS server thread exited");
            }
            serverStoppedFuture.complete(null);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("Controller connected to startup WS server from {}", conn.getRemoteSocketAddress());
        try {
            AgentWsEnvelope hello = AgentWsEnvelope.hello(instanceId, jobId, agentSessionId, null, capacity, true);
            conn.send(hello.toJson());
        } catch (Exception e) {
            logger.warn("Failed to send startup WS hello: {}", e.getMessage());
            conn.close();
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(message);
            if (envelope.getType() == null) {
                logger.warn("Startup WS frame missing type");
                return;
            }
            switch (envelope.getType()) {
                case file_offer -> handleFileOffer(conn, envelope);
                case file_chunk -> handleFileChunk(conn, envelope);
                case ping -> handlePing(conn, envelope);
                case ack -> logger.info("Startup WS received ack: {}", envelope.getAckForType());
                case close -> conn.close();
                default -> logger.info("Startup WS ignoring frame type: {}", envelope.getType());
            }
        } catch (Exception e) {
            logger.warn("Failed handling startup WS message: {}", e.getMessage(), e);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("Startup WS connection closed code={} reason={} remote={}", code, reason, remote);
        resetFileStateForRetry(conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.warn("Startup WS error: {}", ex.getMessage(), ex);
        if (conn == null) {
            logger.error("Startup WS server-level error — preserving partial file for restart");
            handleFatalError(ex);
            return;
        }
        resetFileStateForRetry(conn);
    }

    @Override
    public void onStart() {
        logger.info("Startup WS server started on {}", getAddress());
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

    private synchronized void handleFileOffer(WebSocket conn, AgentWsEnvelope envelope) {
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
            // Send explicit ok ack so controller doesn't wait 10s
            sendFileAck(conn, envelope.getFileId(), 0, AckStatus.ok, null);
        } catch (IOException e) {
            closeCurrentFileQuietly();
            sendFileAck(conn, envelope.getFileId(), null, AckStatus.failed, e.getMessage());
        }
    }

    private synchronized void handleFileChunk(WebSocket conn, AgentWsEnvelope envelope) {
        if (currentFileStream == null || currentFileId == null || !currentFileId.equals(envelope.getFileId())) {
            sendFileAck(conn, envelope.getFileId(), envelope.getChunkIndex(), AckStatus.failed, "file_offer_not_found");
            return;
        }

        try {
            byte[] payload = envelope.getChunkData() == null || envelope.getChunkData().isEmpty()
                    ? new byte[0]
                    : Base64.getDecoder().decode(envelope.getChunkData());
            currentFileStream.write(payload);
            receivedBytes += payload.length;
            receivedChunks++;
            sendFileAck(conn, envelope.getFileId(), envelope.getChunkIndex(), AckStatus.chunk_received, null);

            if (expectedBytes > 0 && receivedBytes >= expectedBytes) {
                File completedFile = finalizeHarnessJar();
                sendFileAck(conn, envelope.getFileId(), envelope.getChunkIndex(), AckStatus.complete, null);
                harnessJarFuture.complete(completedFile);
            }
        } catch (Exception e) {
            closeCurrentFileQuietly();
            sendFileAck(conn, envelope.getFileId(), envelope.getChunkIndex(), AckStatus.failed, e.getMessage());
            harnessJarFuture.completeExceptionally(e);
        }
    }

    private void handlePing(WebSocket conn, AgentWsEnvelope envelope) {
        try {
            AgentWsEnvelope pong = AgentWsEnvelope.pong(instanceId, agentSessionId, envelope.getPingId(), null);
            conn.send(pong.toJson());
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
        currentTempFile = null;
        targetFile = null;
        currentFileId = null;
        return completedFile;
    }

    private void sendFileAck(WebSocket conn, String fileId, Integer chunkIndex, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, chunkIndex, status, error);
            conn.send(ack.toJson());
        } catch (Exception e) {
            logger.warn("Failed to send startup WS file ack for {}: {}", fileId, e.getMessage());
        }
    }

    private void sendFileAckWithResume(WebSocket conn, String fileId, AckStatus status, long resumeOffset, int resumeChunk) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, resumeChunk, status, null);
            ack.setResumeOffset(resumeOffset);
            conn.send(ack.toJson());
        } catch (Exception e) {
            logger.warn("Failed to send startup WS resume ack for {}: {}", fileId, e.getMessage());
        }
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

    private synchronized void resetFileStateForRetry(WebSocket conn) {
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
}

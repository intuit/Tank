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
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("Controller connected to startup WS server from {}", conn.getRemoteSocketAddress());
        try {
            AgentWsEnvelope hello = AgentWsEnvelope.hello(instanceId, jobId, agentSessionId, null, capacity, true);
            conn.send(hello.toJson());
        } catch (IOException e) {
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
        } catch (IOException e) {
            logger.warn("Failed parsing startup WS message: {}", e.getMessage());
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("Startup WS connection closed code={} reason={} remote={}", code, reason, remote);
        closeCurrentFileQuietly();
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.warn("Startup WS error: {}", ex.getMessage(), ex);
        closeCurrentFileQuietly();
        if (!harnessJarFuture.isDone()) {
            harnessJarFuture.completeExceptionally(ex);
        }
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

    private synchronized void handleFileOffer(WebSocket conn, AgentWsEnvelope envelope) {
        String fileName = new File(envelope.getFileName() != null ? envelope.getFileName() : "").getName();
        String fileType = envelope.getFileType();
        if (!API_HARNESS_JAR.equals(fileName) && !SUPPORT_JAR_FILE_TYPE.equalsIgnoreCase(fileType)) {
            sendFileAck(conn, envelope.getFileId(), null, AckStatus.failed, "unsupported_startup_file");
            return;
        }

        closeCurrentFileQuietly();
        try {
            File agentDir = new File(TANK_AGENT_DIR);
            if (!agentDir.exists() && !agentDir.mkdirs()) {
                throw new IOException("Unable to create " + agentDir.getAbsolutePath());
            }
            targetFile = new File(agentDir, API_HARNESS_JAR);
            currentTempFile = new File(targetFile.getAbsolutePath() + ".part");
            currentFileStream = new FileOutputStream(currentTempFile);
            currentFileId = envelope.getFileId();
            receivedChunks = 0;
            expectedChunks = envelope.getTotalChunks() != null ? envelope.getTotalChunks() : 0;
            receivedBytes = 0;
            expectedBytes = envelope.getTotalBytes() != null ? envelope.getTotalBytes() : -1L;
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

            if (expectedChunks > 0 && receivedChunks >= expectedChunks) {
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
        } catch (IOException e) {
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
        } catch (IOException e) {
            logger.warn("Failed to send startup WS file ack for {}: {}", fileId, e.getMessage());
        }
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
}

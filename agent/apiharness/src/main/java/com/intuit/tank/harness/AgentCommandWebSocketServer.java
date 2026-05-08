package com.intuit.tank.harness;

import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class AgentCommandWebSocketServer extends WebSocketServer {

    private static final Logger LOG = LogManager.getLogger(AgentCommandWebSocketServer.class);
    private static final int MAX_APPLIED_COMMAND_IDS = 10_000;
    private static final String AGENT_HOME_DIR = "/opt/tank_agent";
    private static final String SUPPORT_ARCHIVE_FILE_TYPE = "support_archive";
    private static final String DEFAULT_SUPPORT_ARCHIVE_NAME = "agent-support-files.zip";

    private final String instanceId;
    private final String jobId;
    private final int capacity;
    private final String agentSessionId;

    private final AtomicReference<WebSocket> activeConnection = new AtomicReference<>();
    private final Set<String> appliedCommandIds = ConcurrentHashMap.newKeySet();
    private final ConcurrentHashMap<String, IncomingFileState> incomingFiles = new ConcurrentHashMap<>();
    private final AtomicInteger completedFiles = new AtomicInteger(0);
    private final AtomicBoolean initialBootstrapReady = new AtomicBoolean(false);
    private final AtomicBoolean transferCompleteAckSent = new AtomicBoolean(false);
    private volatile String lastAppliedCommandId;
    private volatile CompletableFuture<AgentTestStartData> jobConfigFuture = new CompletableFuture<>();
    private volatile CompletableFuture<Void> transferCompleteFuture = new CompletableFuture<>();
    private volatile int expectedFiles = -1;
    private volatile AgentTestStartData receivedJobConfig;
    private volatile WebSocket transferCompleteAckWebSocket;
    private volatile String transferCompleteAckFileId;
    private volatile Integer transferCompleteAckChunkIndex;

    public AgentCommandWebSocketServer(int port, String instanceId, String jobId, int capacity) {
        super(new InetSocketAddress(port));
        setReuseAddr(true);
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.capacity = capacity;
        this.agentSessionId = UUID.randomUUID().toString();
    }

    @Override
    public void onOpen(WebSocket connection, ClientHandshake handshake) {
        LOG.info(new ObjectMessage(Map.of("Message",
                "Controller WS connected to agent " + instanceId + " from " + connection.getRemoteSocketAddress())));
        WebSocket previous = activeConnection.getAndSet(connection);
        if (previous != null && previous != connection && previous.isOpen()) {
            previous.close();
        }
        try {
            AgentWsEnvelope hello = AgentWsEnvelope.hello(instanceId, jobId, agentSessionId, lastAppliedCommandId, capacity);
            connection.send(hello.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send WS hello: " + e.getMessage())));
            connection.close();
        }
    }

    @Override
    public void onMessage(WebSocket connection, String message) {
        try {
            AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(message);
            if (envelope.getType() == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "WS frame missing type")));
                return;
            }

            switch (envelope.getType()) {
                case command -> handleCommand(connection, envelope);
                case ping -> handlePing(connection, envelope);
                case job_config -> handleJobConfig(envelope);
                case file_offer -> handleFileOffer(connection, envelope);
                case file_chunk -> handleFileChunk(connection, envelope);
                case ack -> LOG.info(new ObjectMessage(Map.of("Message", "Received WS ack: " + envelope.getAckForType())));
                case close -> {
                    LOG.info(new ObjectMessage(Map.of("Message", "Controller closed WS: " + envelope.getReason())));
                    connection.close();
                }
                default -> LOG.warn(new ObjectMessage(Map.of("Message", "Unexpected WS frame type: " + envelope.getType())));
            }
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed parsing WS message: " + e.getMessage())));
        }
    }

    @Override
    public void onMessage(WebSocket connection, ByteBuffer message) {
        try {
            AgentWsEnvelope.BinaryFileChunk chunk = AgentWsEnvelope.fromBinaryFileChunk(message);
            handleFileChunk(connection, chunk.fileId(), chunk.chunkIndex(), chunk.payload());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed parsing WS binary file chunk: " + e.getMessage())));
        }
    }

    private void handleCommand(WebSocket connection, AgentWsEnvelope envelope) {
        String commandId = envelope.getCommandId();
        String command = envelope.getCommand();

        if (appliedCommandIds.size() > MAX_APPLIED_COMMAND_IDS) {
            appliedCommandIds.clear();
        }

        if (commandId != null && !appliedCommandIds.add(commandId)) {
            sendAck(connection, commandId, AckStatus.duplicate, null);
            return;
        }

        if (isStartCommand(command) && !initialBootstrapReady.get()) {
            sendAck(connection, commandId, AckStatus.failed, "bootstrap_not_ready");
            return;
        }

        try {
            CommandListener.applyCommand(command);
            lastAppliedCommandId = commandId;
            sendAck(connection, commandId, AckStatus.ok, null);
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "Failed to apply command " + command + " for agent " + instanceId + ": " + e.getMessage())));
            sendAck(connection, commandId, AckStatus.failed, e.getMessage());
        }
    }

    private boolean isStartCommand(String command) {
        return "start".equalsIgnoreCase(command) || "run".equalsIgnoreCase(command);
    }

    private void handlePing(WebSocket connection, AgentWsEnvelope envelope) {
        try {
            AgentWsEnvelope pong = AgentWsEnvelope.pong(instanceId, agentSessionId, envelope.getPingId(), lastAppliedCommandId);
            connection.send(pong.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send pong: " + e.getMessage())));
        }
    }

    private void handleJobConfig(AgentWsEnvelope envelope) {
        AgentTestStartData jobConfig = envelope.getJobConfig();
        if (jobConfig == null) {
            jobConfigFuture.completeExceptionally(new IllegalStateException("WS job_config missing payload"));
            return;
        }
        receivedJobConfig = jobConfig;
        expectedFiles = envelope.getExpectedFiles() != null
                ? envelope.getExpectedFiles()
                : 3 + (jobConfig.getDataFiles() != null ? jobConfig.getDataFiles().length : 0);

        CompletableFuture<AgentTestStartData> currentJobConfigFuture = jobConfigFuture;
        if (!currentJobConfigFuture.isDone()) {
            currentJobConfigFuture.complete(jobConfig);
        }
        CompletableFuture<Void> currentTransferCompleteFuture = transferCompleteFuture;
        if (expectedFiles <= 0 && !currentTransferCompleteFuture.isDone()) {
            currentTransferCompleteFuture.complete(null);
        }
    }

    private void handleFileOffer(WebSocket connection, AgentWsEnvelope envelope) {
        String fileId = envelope.getFileId();
        if (fileId == null || envelope.getFileName() == null) {
            return;
        }

        IncomingFileState previous = incomingFiles.remove(fileId);
        if (previous != null) {
            previous.closeQuietly();
        }

        try {
            File targetFile = resolveTargetFile(envelope);
            File parent = targetFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Unable to create dir " + parent.getAbsolutePath());
            }

            File tempFile = new File(targetFile.getAbsolutePath() + ".part");
            IncomingFileState state = new IncomingFileState(
                    envelope.getFileType(),
                    envelope.getFileName(),
                    targetFile,
                    tempFile,
                    envelope.getTotalBytes() != null ? envelope.getTotalBytes() : 0L,
                    envelope.getTotalChunks() != null ? envelope.getTotalChunks() : 0,
                    Boolean.TRUE.equals(envelope.getDefaultDataFile()),
                    new FileOutputStream(tempFile)
            );
            incomingFiles.put(fileId, state);
            sendFileAck(connection, fileId, 0, AckStatus.ok, null);
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed handling file_offer " + envelope.getFileName() + ": " + e.getMessage())));
            sendFileAck(connection, fileId, 0, AckStatus.failed, e.getMessage());
        }
    }

    private void handleFileChunk(WebSocket connection, AgentWsEnvelope envelope) {
        String fileId = envelope.getFileId();
        byte[] payload;
        try {
            payload = envelope.getChunkData() == null || envelope.getChunkData().isEmpty()
                    ? new byte[0]
                    : Base64.getDecoder().decode(envelope.getChunkData());
        } catch (Exception e) {
            sendFileAck(connection, fileId, envelope.getChunkIndex(), AckStatus.failed, e.getMessage());
            return;
        }
        handleFileChunk(connection, fileId, envelope.getChunkIndex(), payload);
    }

    private void handleFileChunk(WebSocket connection, String fileId, Integer chunkIndex, byte[] payload) {
        IncomingFileState state = fileId != null ? incomingFiles.get(fileId) : null;
        if (state == null) {
            sendFileAck(connection, fileId, chunkIndex, AckStatus.failed, "file_offer_not_found");
            return;
        }

        try {
            if (state.totalChunks < 0 && payload.length == 0) {
                finalizeFile(state);
                incomingFiles.remove(fileId);
                sendFileAck(connection, fileId, chunkIndex, AckStatus.complete, null);
                markFileComplete(connection, fileId, chunkIndex);
                return;
            }

            state.outputStream.write(payload);
            state.receivedBytes += payload.length;
            state.receivedChunks++;

            sendFileAck(connection, fileId, chunkIndex, AckStatus.chunk_received, null);

            if (state.totalChunks >= 0 && state.receivedChunks >= state.totalChunks) {
                finalizeFile(state);
                incomingFiles.remove(fileId);
                sendFileAck(connection, fileId, chunkIndex, AckStatus.complete, null);
                markFileComplete(connection, fileId, chunkIndex);
            }
        } catch (Exception e) {
            incomingFiles.remove(fileId);
            state.closeQuietly();
            sendFileAck(connection, fileId, chunkIndex, AckStatus.failed, e.getMessage());
        }
    }

    private void markFileComplete(WebSocket connection, String fileId, Integer chunkIndex) {
        int done = completedFiles.incrementAndGet();
        CompletableFuture<Void> currentTransferCompleteFuture = transferCompleteFuture;
        if (expectedFiles > 0 && done >= expectedFiles && !currentTransferCompleteFuture.isDone()) {
            prepareTransferCompleteAck(connection, fileId, chunkIndex);
            currentTransferCompleteFuture.complete(null);
            sendTransferCompleteAckIfReady();
        }
    }

    private void finalizeFile(IncomingFileState state) throws IOException {
        state.closeQuietly();

        if (state.totalBytes >= 0 && state.totalBytes != state.receivedBytes) {
            throw new IOException("file size mismatch expected=" + state.totalBytes + " actual=" + state.receivedBytes);
        }

        try {
            Files.move(state.tempFile.toPath(), state.targetFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            Files.move(state.tempFile.toPath(), state.targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }

        if (SUPPORT_ARCHIVE_FILE_TYPE.equalsIgnoreCase(state.fileType)) {
            unpackSupportArchive(state.targetFile);
            Files.deleteIfExists(state.targetFile.toPath());
            return;
        }

        if (state.defaultDataFile && !TankConstants.DEFAULT_CSV_FILE_NAME.equals(state.fileName)) {
            File defaultFile = new File(state.targetFile.getParentFile(), TankConstants.DEFAULT_CSV_FILE_NAME);
            FileUtils.copyFile(state.targetFile, defaultFile);
        }
    }

    private void unpackSupportArchive(File archiveFile) throws IOException {
        Path agentDirPath = Paths.get(AGENT_HOME_DIR).toAbsolutePath().normalize();
        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(archiveFile.toPath()))) {
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                Path targetPath = agentDirPath.resolve(entry.getName()).normalize();
                if (!targetPath.startsWith(agentDirPath)) {
                    throw new ZipException("Bad zip entry");
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(targetPath);
                } else {
                    Path parent = targetPath.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                    try (OutputStream out = Files.newOutputStream(targetPath)) {
                        byte[] buf = new byte[8192];
                        int len;
                        while ((len = zip.read(buf)) != -1) {
                            out.write(buf, 0, len);
                        }
                    }
                }
                zip.closeEntry();
                entry = zip.getNextEntry();
            }
        }
    }

    private File resolveTargetFile(AgentWsEnvelope envelope) {
        String fileType = envelope.getFileType();
        if ("settings".equalsIgnoreCase(fileType)) {
            return new File(AGENT_HOME_DIR, "settings.xml");
        }
        if (SUPPORT_ARCHIVE_FILE_TYPE.equalsIgnoreCase(fileType)) {
            String archiveName = envelope.getFileName();
            if (archiveName == null || archiveName.isBlank()) {
                archiveName = DEFAULT_SUPPORT_ARCHIVE_NAME;
            }
            archiveName = new File(archiveName).getName();
            return new File(AGENT_HOME_DIR, archiveName);
        }
        if ("script".equalsIgnoreCase(fileType)) {
            return new File("script.xml");
        }
        if ("data".equalsIgnoreCase(fileType)) {
            String dataDir = new TankConfig().getAgentConfig().getAgentDataFileStorageDir();
            return new File(dataDir, envelope.getFileName());
        }
        return new File(envelope.getFileName());
    }

    private void sendFileAck(WebSocket connection, String fileId, Integer chunkIndex, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, chunkIndex, status, error);
            connection.send(ack.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send file ack for " + fileId + ": " + e.getMessage())));
        }
    }

    private void prepareTransferCompleteAck(WebSocket connection, String fileId, Integer chunkIndex) {
        transferCompleteAckWebSocket = connection;
        transferCompleteAckFileId = fileId;
        transferCompleteAckChunkIndex = chunkIndex;
    }

    private void sendTransferCompleteAckIfReady() {
        WebSocket connection = transferCompleteAckWebSocket;
        if (!initialBootstrapReady.get() || connection == null || !connection.isOpen()) {
            return;
        }
        if (transferCompleteAckSent.compareAndSet(false, true)) {
            sendFileAck(connection, transferCompleteAckFileId, transferCompleteAckChunkIndex, AckStatus.all_files_complete, null);
        }
    }

    private void sendAck(WebSocket connection, String commandId, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.ack(instanceId, "command", commandId, status);
            ack.setError(error);
            connection.send(ack.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send command ack: " + e.getMessage())));
        }
    }

    @Override
    public void onClose(WebSocket connection, int code, String reason, boolean remote) {
        LOG.info(new ObjectMessage(Map.of("Message",
                "Controller WS disconnected from agent " + instanceId + " code=" + code + " reason=" + reason)));
        activeConnection.compareAndSet(connection, null);
    }

    @Override
    public void onError(WebSocket connection, Exception ex) {
        LOG.error(new ObjectMessage(Map.of("Message", "Agent WS server transport error: " + ex.getMessage())), ex);
    }

    @Override
    public void onStart() {
        LOG.info(new ObjectMessage(Map.of("Message", "Agent WS control server started for " + instanceId)));
    }

    public boolean awaitInitialTransfer(long timeoutMillis) {
        try {
            CompletableFuture<AgentTestStartData> currentJobConfigFuture = jobConfigFuture;
            CompletableFuture<Void> currentTransferCompleteFuture = transferCompleteFuture;
            receivedJobConfig = currentJobConfigFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
            currentTransferCompleteFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Timed out waiting for controller-initiated WS transfer: " + e.getMessage())));
            return false;
        }
    }

    public AgentTestStartData getReceivedJobConfig() {
        return receivedJobConfig;
    }

    public void markInitialBootstrapReady() {
        initialBootstrapReady.set(true);
        sendTransferCompleteAckIfReady();
    }

    public boolean sendStatusUpdate(CloudVmStatus instanceStatus) {
        WebSocket connection = activeConnection.get();
        if (connection == null || !connection.isOpen()) {
            return false;
        }
        try {
            String statusJobId = instanceStatus != null && instanceStatus.getJobId() != null
                    ? instanceStatus.getJobId()
                    : jobId;
            AgentWsEnvelope update = AgentWsEnvelope.statusUpdate(instanceId, statusJobId, instanceStatus);
            connection.send(update.toJson());
            return true;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed sending WS status update: " + e.getMessage())));
            return false;
        }
    }

    public void closeServer() {
        WebSocket connection = activeConnection.getAndSet(null);
        if (connection != null && connection.isOpen()) {
            try {
                AgentWsEnvelope closeEnvelope = AgentWsEnvelope.close(instanceId, "agent_shutdown", "Agent shutting down");
                connection.send(closeEnvelope.toJson());
            } catch (IOException ignored) {
            }
            connection.close();
        }
        try {
            stop();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static class IncomingFileState {
        private final String fileType;
        private final String fileName;
        private final File targetFile;
        private final File tempFile;
        private final long totalBytes;
        private final int totalChunks;
        private final boolean defaultDataFile;
        private final OutputStream outputStream;
        private long receivedBytes;
        private int receivedChunks;

        private IncomingFileState(String fileType, String fileName, File targetFile, File tempFile,
                                  long totalBytes, int totalChunks, boolean defaultDataFile,
                                  OutputStream outputStream) {
            this.fileType = fileType;
            this.fileName = fileName;
            this.targetFile = targetFile;
            this.tempFile = tempFile;
            this.totalBytes = totalBytes;
            this.totalChunks = totalChunks;
            this.defaultDataFile = defaultDataFile;
            this.outputStream = outputStream;
        }

        private void closeQuietly() {
            try {
                outputStream.close();
            } catch (IOException ignored) {
            }
        }
    }
}

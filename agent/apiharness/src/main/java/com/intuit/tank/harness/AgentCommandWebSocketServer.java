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
import java.io.OutputStream;
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

/**
 * Agent-side WebSocket server that receives job config, settings/script/data files, and control
 * commands pushed by the controller. Runs over WebSocket-over-HTTP/2 (RFC 8441) using Jetty 12; the
 * same connector also accepts HTTP/1.1 WebSocket upgrades for transition compatibility.
 */
public class AgentCommandWebSocketServer {

    private static final Logger LOG = LogManager.getLogger(AgentCommandWebSocketServer.class);
    private static final int MAX_APPLIED_COMMAND_IDS = 10_000;
    private static final String AGENT_HOME_DIR = "/opt/tank_agent";
    private static final String SUPPORT_ARCHIVE_FILE_TYPE = "support_archive";
    private static final String DEFAULT_SUPPORT_ARCHIVE_NAME = "agent-support-files.zip";
    // 2 MiB chunks * a healthy window can exceed Jetty's default frame/message limits.
    private static final long MAX_WS_MESSAGE_BYTES = 64L * 1024 * 1024;
    // Jetty's default WS idle timeout is 30s. The controller may hold the connection idle while it
    // waits for a whole fleet of agents to become ready before broadcasting START, so raise it well
    // past that window to avoid the connection being closed with "Connection Idle Timeout".
    private static final long WS_IDLE_TIMEOUT_MS =
            Math.max(60_000L, Long.getLong("tank.ws.idleTimeoutMs", 600_000L));

    private final int port;
    private final String instanceId;
    private final String jobId;
    private final int capacity;
    private final String agentSessionId;

    private final Server server;
    private boolean reuseAddr = true;

    private final AtomicReference<Session> activeConnection = new AtomicReference<>();
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
    private volatile Session transferCompleteAckWebSocket;
    private volatile String transferCompleteAckFileId;
    private volatile Integer transferCompleteAckChunkIndex;

    public AgentCommandWebSocketServer(int port, String instanceId, String jobId, int capacity) {
        this.port = port;
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.capacity = capacity;
        this.agentSessionId = UUID.randomUUID().toString();
        this.server = new Server();
    }

    public void setReuseAddr(boolean reuseAddr) {
        this.reuseAddr = reuseAddr;
    }

    public void start() throws IOException {
        HttpConfiguration httpConfig = new HttpConfiguration();
        HttpConnectionFactory http11 = new HttpConnectionFactory(httpConfig);
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
            container.addMapping("/", (upgradeRequest, upgradeResponse, callback) -> new CommandEndpoint(this));
        });
        server.setHandler(wsHandler);

        try {
            server.start();
        } catch (Exception e) {
            throw new IOException("Failed to start agent WS control server on port " + port, e);
        }
        LOG.info(new ObjectMessage(Map.of("Message", "Agent WS control server started for " + instanceId + " on port " + port)));
    }

    private void onConnectionEstablished(Session connection) {
        LOG.info(new ObjectMessage(Map.of("Message",
                "Controller WS connected to agent " + instanceId + " from " + connection.getRemoteSocketAddress())));
        Session previous = activeConnection.getAndSet(connection);
        if (previous != null && previous != connection && previous.isOpen()) {
            previous.close();
        }
        try {
            AgentWsEnvelope hello = AgentWsEnvelope.hello(instanceId, jobId, agentSessionId, lastAppliedCommandId, capacity);
            sendText(connection, hello.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send WS hello: " + e.getMessage())));
            connection.close();
        }
    }

    private void onTextMessage(Session connection, String message) {
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

    private void handleCommand(Session connection, AgentWsEnvelope envelope) {
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

    private void handlePing(Session connection, AgentWsEnvelope envelope) {
        try {
            AgentWsEnvelope pong = AgentWsEnvelope.pong(instanceId, agentSessionId, envelope.getPingId(), lastAppliedCommandId);
            sendText(connection, pong.toJson());
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

    private void handleFileOffer(Session connection, AgentWsEnvelope envelope) {
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

    private void handleFileChunk(Session connection, AgentWsEnvelope envelope) {
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

    private void handleFileChunk(Session connection, String fileId, Integer chunkIndex, byte[] payload) {
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

    private void markFileComplete(Session connection, String fileId, Integer chunkIndex) {
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

        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - state.transferStartedAtNs);
        double throughputMiBps = durationMs > 0
                ? Math.round(((state.receivedBytes / (1024.0 * 1024.0)) / (durationMs / 1000.0)) * 100.0) / 100.0
                : 0.0;
        LOG.info(new ObjectMessage(Map.of("Message",
                "WS file received file=" + state.fileName
                        + " type=" + state.fileType
                        + " bytes=" + state.receivedBytes + "/" + state.totalBytes
                        + " chunks=" + state.receivedChunks + "/" + state.totalChunks
                        + " durationMs=" + durationMs
                        + " throughputMiBps=" + throughputMiBps)));

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

    private void sendFileAck(Session connection, String fileId, Integer chunkIndex, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, chunkIndex, status, error);
            sendText(connection, ack.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send file ack for " + fileId + ": " + e.getMessage())));
        }
    }

    private void prepareTransferCompleteAck(Session connection, String fileId, Integer chunkIndex) {
        transferCompleteAckWebSocket = connection;
        transferCompleteAckFileId = fileId;
        transferCompleteAckChunkIndex = chunkIndex;
    }

    private void sendTransferCompleteAckIfReady() {
        Session connection = transferCompleteAckWebSocket;
        if (!initialBootstrapReady.get() || connection == null || !connection.isOpen()) {
            return;
        }
        if (transferCompleteAckSent.compareAndSet(false, true)) {
            sendFileAck(connection, transferCompleteAckFileId, transferCompleteAckChunkIndex, AckStatus.all_files_complete, null);
        }
    }

    private void sendAck(Session connection, String commandId, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.ack(instanceId, "command", commandId, status);
            ack.setError(error);
            sendText(connection, ack.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send command ack: " + e.getMessage())));
        }
    }

    private void sendText(Session connection, String text) {
        connection.sendText(text, Callback.NOOP);
    }

    private void onConnectionClosed(Session connection, int code, String reason) {
        LOG.info(new ObjectMessage(Map.of("Message",
                "Controller WS disconnected from agent " + instanceId + " code=" + code + " reason=" + reason)));
        activeConnection.compareAndSet(connection, null);
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
        Session connection = activeConnection.get();
        if (connection == null || !connection.isOpen()) {
            return false;
        }
        try {
            String statusJobId = instanceStatus != null && instanceStatus.getJobId() != null
                    ? instanceStatus.getJobId()
                    : jobId;
            AgentWsEnvelope update = AgentWsEnvelope.statusUpdate(instanceId, statusJobId, instanceStatus);
            sendText(connection, update.toJson());
            return true;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed sending WS status update: " + e.getMessage())));
            return false;
        }
    }

    public void closeServer() {
        Session connection = activeConnection.getAndSet(null);
        if (connection != null && connection.isOpen()) {
            try {
                AgentWsEnvelope closeEnvelope = AgentWsEnvelope.close(instanceId, "agent_shutdown", "Agent shutting down");
                sendText(connection, closeEnvelope.toJson());
            } catch (IOException ignored) {
            }
            connection.close();
        }
        try {
            server.stop();
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Error stopping agent WS server: " + e.getMessage())));
        }
    }

    /**
     * Per-connection Jetty WebSocket endpoint. {@code AutoDemanding} means Jetty automatically demands
     * the next frame after each callback returns, matching the old library's push-style delivery.
     */
    public static class CommandEndpoint implements Session.Listener.AutoDemanding {

        private final AgentCommandWebSocketServer owner;
        private Session session;

        private CommandEndpoint(AgentCommandWebSocketServer owner) {
            this.owner = owner;
        }

        @Override
        public void onWebSocketOpen(Session session) {
            this.session = session;
            owner.onConnectionEstablished(session);
        }

        @Override
        public void onWebSocketText(String message) {
            owner.onTextMessage(session, message);
        }

        @Override
        public void onWebSocketBinary(ByteBuffer payload, Callback callback) {
            try {
                AgentWsEnvelope.BinaryFileChunk chunk = AgentWsEnvelope.fromBinaryFileChunk(payload);
                owner.handleFileChunk(session, chunk.fileId(), chunk.chunkIndex(), chunk.payload());
                callback.succeed();
            } catch (IOException e) {
                LOG.warn(new ObjectMessage(Map.of("Message", "Failed parsing WS binary file chunk: " + e.getMessage())));
                callback.fail(e);
            }
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            owner.onConnectionClosed(session, statusCode, reason);
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            LOG.error(new ObjectMessage(Map.of("Message", "Agent WS server transport error: " + cause.getMessage())), cause);
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
        private final long transferStartedAtNs;
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
            this.transferStartedAtNs = System.nanoTime();
        }

        private void closeQuietly() {
            try {
                outputStream.close();
            } catch (IOException ignored) {
            }
        }
    }
}

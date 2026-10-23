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
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
 * <p>
 * Several controllers may be connected at once, and each gets hello and every status update. A
 * controller names itself with {@code /ws/control?node=<id>}; a new connection from the same node
 * replaces that node's previous one. Job config, files and start/run are accepted only from the
 * bootstrap connection: the first connection to send one of them, until it closes. Connections the
 * agent hears nothing from (the controller pings every 10s) are closed as dead.
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
    // A half-open TCP connection still accepts writes, so silence is the only reliable sign it is dead.
    // Controllers ping every tank.ws.keepAlivePingMs (10s); allow several missed pings.
    private static final long WS_SILENCE_TIMEOUT_MS =
            Math.max(30_000L, Long.getLong("tank.ws.silenceTimeoutMs", 45_000L));
    // Status updates are snapshots, so a slow connection skips updates rather than queueing them.
    private static final int MAX_PENDING_STATUS_SENDS = 32;
    private static final String NOT_BOOTSTRAP_CONNECTION = "not_bootstrap_connection";

    private final int port;
    private final String instanceId;
    private final String jobId;
    private final int capacity;
    private final String agentSessionId;
    private final String agentToken;

    private final Server server;
    private boolean reuseAddr = true;
    private long silenceTimeoutMs = WS_SILENCE_TIMEOUT_MS;
    private volatile ScheduledExecutorService silenceMonitor;

    private final Map<Session, ControllerConnection> connections = new ConcurrentHashMap<>();
    private final AtomicReference<Session> bootstrapConnection = new AtomicReference<>();
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
        this(port, instanceId, jobId, capacity, null);
    }

    /**
     * @param agentToken when set, upgrades must carry {@code Authorization: bearer <agentToken>}
     */
    public AgentCommandWebSocketServer(int port, String instanceId, String jobId, int capacity, String agentToken) {
        this.port = port;
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.capacity = capacity;
        this.agentToken = agentToken;
        this.agentSessionId = UUID.randomUUID().toString();
        this.server = new Server();
    }

    public void setReuseAddr(boolean reuseAddr) {
        this.reuseAddr = reuseAddr;
    }

    void setSilenceTimeoutMs(long silenceTimeoutMs) {
        this.silenceTimeoutMs = silenceTimeoutMs;
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
            container.addMapping("/", (upgradeRequest, upgradeResponse, callback) -> {
                if (!isAuthorized(upgradeRequest.getHeaders().get(HttpHeader.AUTHORIZATION))) {
                    LOG.warn(new ObjectMessage(Map.of("Message",
                            "Rejected unauthorized WS upgrade to agent " + instanceId + " from " + Request.getRemoteAddr(upgradeRequest))));
                    Response.writeError(upgradeRequest, upgradeResponse, callback, HttpStatus.UNAUTHORIZED_401);
                    return null;
                }
                return new CommandEndpoint(this, queryParam(upgradeRequest.getHttpURI().getQuery(), "node"));
            });
        });
        server.setHandler(wsHandler);

        try {
            server.start();
        } catch (Exception e) {
            throw new IOException("Failed to start agent WS control server on port " + port, e);
        }
        ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "agent-ws-silence-monitor");
            t.setDaemon(true);
            return t;
        });
        long checkIntervalMs = Math.max(100L, silenceTimeoutMs / 3);
        monitor.scheduleWithFixedDelay(this::closeSilentConnections, checkIntervalMs, checkIntervalMs, TimeUnit.MILLISECONDS);
        silenceMonitor = monitor;
        LOG.info(new ObjectMessage(Map.of("Message", "Agent WS control server started for " + instanceId + " on port " + port)));
    }

    private boolean isAuthorized(String authorization) {
        if (agentToken == null || agentToken.isBlank()) {
            return true;
        }
        String prefix = "bearer ";
        if (authorization == null || !authorization.regionMatches(true, 0, prefix, 0, prefix.length())) {
            return false;
        }
        return MessageDigest.isEqual(
                authorization.substring(prefix.length()).trim().getBytes(StandardCharsets.UTF_8),
                agentToken.getBytes(StandardCharsets.UTF_8));
    }

    static String queryParam(String query, String name) {
        if (query == null) {
            return null;
        }
        for (String param : query.split("&")) {
            int eq = param.indexOf('=');
            if (eq > 0 && name.equals(param.substring(0, eq))) {
                String value = URLDecoder.decode(param.substring(eq + 1), StandardCharsets.UTF_8);
                return value.isBlank() ? null : value;
            }
        }
        return null;
    }

    private void onConnectionEstablished(Session connection, String nodeId) {
        LOG.info(new ObjectMessage(Map.of("Message",
                "Controller WS connected to agent " + instanceId + " from " + connection.getRemoteSocketAddress()
                        + " node=" + nodeId)));
        synchronized (connections) {
            if (nodeId != null) {
                for (Map.Entry<Session, ControllerConnection> entry : connections.entrySet()) {
                    if (nodeId.equals(entry.getValue().nodeId)) {
                        connections.remove(entry.getKey());
                        entry.getKey().close();
                    }
                }
            }
            connections.put(connection, new ControllerConnection(nodeId));
        }
        try {
            AgentWsEnvelope hello = AgentWsEnvelope.hello(instanceId, jobId, agentSessionId, lastAppliedCommandId, capacity);
            sendText(connection, hello.toJson());
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send WS hello: " + e.getMessage())));
            connection.close();
        }
    }

    private void touch(Session connection) {
        ControllerConnection controller = connections.get(connection);
        if (controller != null) {
            controller.lastHeardMs = System.currentTimeMillis();
        }
    }

    /**
     * Makes {@code connection} the bootstrap connection unless another open one already holds the role.
     */
    private boolean claimBootstrap(Session connection) {
        Session holder = bootstrapConnection.get();
        if (holder == connection) {
            return true;
        }
        if (holder != null && holder.isOpen() && connections.containsKey(holder)) {
            return false;
        }
        return bootstrapConnection.compareAndSet(holder, connection);
    }

    private void closeSilentConnections() {
        long now = System.currentTimeMillis();
        for (Map.Entry<Session, ControllerConnection> entry : connections.entrySet()) {
            long silentMs = now - entry.getValue().lastHeardMs;
            if (silentMs > silenceTimeoutMs && connections.remove(entry.getKey(), entry.getValue())) {
                LOG.warn(new ObjectMessage(Map.of("Message", "Closing WS connection to agent " + instanceId
                        + " node=" + entry.getValue().nodeId + " silent for " + silentMs + "ms")));
                bootstrapConnection.compareAndSet(entry.getKey(), null);
                entry.getKey().disconnect();
            }
        }
    }

    private void onTextMessage(Session connection, String message) {
        touch(connection);
        try {
            AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(message);
            if (envelope.getType() == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "WS frame missing type")));
                return;
            }

            switch (envelope.getType()) {
                case command -> handleCommand(connection, envelope);
                case ping -> handlePing(connection, envelope);
                case job_config -> {
                    if (claimBootstrap(connection)) {
                        handleJobConfig(envelope);
                    } else {
                        LOG.warn(new ObjectMessage(Map.of("Message",
                                "Ignoring job_config from a non-bootstrap WS connection to agent " + instanceId)));
                    }
                }
                case file_offer -> {
                    if (claimBootstrap(connection)) {
                        handleFileOffer(connection, envelope);
                    } else {
                        sendFileAck(connection, envelope.getFileId(), 0, AckStatus.failed, NOT_BOOTSTRAP_CONNECTION);
                    }
                }
                case file_chunk -> {
                    if (claimBootstrap(connection)) {
                        handleFileChunk(connection, envelope);
                    } else {
                        sendFileAck(connection, envelope.getFileId(), envelope.getChunkIndex(), AckStatus.failed, NOT_BOOTSTRAP_CONNECTION);
                    }
                }
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

        if (isStartCommand(command) && !claimBootstrap(connection)) {
            sendAck(connection, commandId, AckStatus.failed, NOT_BOOTSTRAP_CONNECTION);
            return;
        }

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

    private void onBinaryFileChunk(Session connection, AgentWsEnvelope.BinaryFileChunk chunk) {
        touch(connection);
        if (claimBootstrap(connection)) {
            handleFileChunk(connection, chunk.fileId(), chunk.chunkIndex(), chunk.payload());
        } else {
            sendFileAck(connection, chunk.fileId(), chunk.chunkIndex(), AckStatus.failed, NOT_BOOTSTRAP_CONNECTION);
        }
    }

    private void onConnectionClosed(Session connection, int code, String reason) {
        LOG.info(new ObjectMessage(Map.of("Message",
                "Controller WS disconnected from agent " + instanceId + " code=" + code + " reason=" + reason)));
        connections.remove(connection);
        bootstrapConnection.compareAndSet(connection, null);
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

    /**
     * Sends the status to every open connection.
     *
     * @return true if at least one connection was sent the update
     */
    public boolean sendStatusUpdate(CloudVmStatus instanceStatus) {
        if (connections.isEmpty()) {
            return false;
        }
        try {
            String statusJobId = instanceStatus != null && instanceStatus.getJobId() != null
                    ? instanceStatus.getJobId()
                    : jobId;
            String update = AgentWsEnvelope.statusUpdate(instanceId, statusJobId, instanceStatus).toJson();
            boolean sent = false;
            for (Map.Entry<Session, ControllerConnection> entry : connections.entrySet()) {
                Session connection = entry.getKey();
                ControllerConnection controller = entry.getValue();
                if (!connection.isOpen()) {
                    continue;
                }
                if (controller.pendingStatusSends.get() >= MAX_PENDING_STATUS_SENDS) {
                    if (!controller.droppingStatus) {
                        controller.droppingStatus = true;
                        LOG.warn(new ObjectMessage(Map.of("Message", "Skipping WS status updates to slow connection node="
                                + controller.nodeId + " on agent " + instanceId)));
                    }
                    continue;
                }
                controller.droppingStatus = false;
                controller.pendingStatusSends.incrementAndGet();
                connection.sendText(update, Callback.from(
                        controller.pendingStatusSends::decrementAndGet,
                        failure -> controller.pendingStatusSends.decrementAndGet()));
                sent = true;
            }
            return sent;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed sending WS status update: " + e.getMessage())));
            return false;
        }
    }

    public void closeServer() {
        ScheduledExecutorService monitor = silenceMonitor;
        if (monitor != null) {
            monitor.shutdownNow();
        }
        for (Session connection : connections.keySet()) {
            connections.remove(connection);
            if (connection.isOpen()) {
                try {
                    AgentWsEnvelope closeEnvelope = AgentWsEnvelope.close(instanceId, "agent_shutdown", "Agent shutting down");
                    sendText(connection, closeEnvelope.toJson());
                } catch (IOException ignored) {
                }
                connection.close();
            }
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
        private final String nodeId;
        private Session session;

        private CommandEndpoint(AgentCommandWebSocketServer owner, String nodeId) {
            this.owner = owner;
            this.nodeId = nodeId;
        }

        @Override
        public void onWebSocketOpen(Session session) {
            this.session = session;
            owner.onConnectionEstablished(session, nodeId);
        }

        @Override
        public void onWebSocketText(String message) {
            owner.onTextMessage(session, message);
        }

        @Override
        public void onWebSocketBinary(ByteBuffer payload, Callback callback) {
            try {
                AgentWsEnvelope.BinaryFileChunk chunk = AgentWsEnvelope.fromBinaryFileChunk(payload);
                owner.onBinaryFileChunk(session, chunk);
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

    private static class ControllerConnection {
        private final String nodeId;
        private final AtomicInteger pendingStatusSends = new AtomicInteger();
        private volatile long lastHeardMs = System.currentTimeMillis();
        private volatile boolean droppingStatus;

        private ControllerConnection(String nodeId) {
            this.nodeId = nodeId;
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

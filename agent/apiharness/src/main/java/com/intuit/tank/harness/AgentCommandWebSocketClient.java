package com.intuit.tank.harness;

import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.common.TankConstants;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
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
import java.util.concurrent.CompletionStage;
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

public class AgentCommandWebSocketClient implements WebSocket.Listener {

    private static final Logger LOG = LogManager.getLogger(AgentCommandWebSocketClient.class);

    private static final int[] BACKOFF_SECONDS = {1, 2, 5, 10, 20, 30};
    private static final String AGENT_HOME_DIR = "/opt/tank_agent";
    private static final String SUPPORT_ARCHIVE_FILE_TYPE = "support_archive";
    private static final String DEFAULT_SUPPORT_ARCHIVE_NAME = "agent-support-files.zip";

    private final String controllerBaseUrl;
    private final String wsPath;
    private final String token;
    private final String instanceId;
    private final String jobId;
    private final String agentSessionId;
    private final Integer helloCapacity;
    private final boolean fileTransferEnabled;
    private final HttpClient httpClient;

    private static final int MAX_APPLIED_COMMAND_IDS = 10_000;

    private final AtomicReference<WebSocket> webSocketRef = new AtomicReference<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    private final Set<String> appliedCommandIds = ConcurrentHashMap.newKeySet();
    private volatile String lastAppliedCommandId = null;
    private volatile long lastFrameReceivedAt = System.currentTimeMillis();

    private final ScheduledExecutorService idleMonitor;

    private volatile CompletableFuture<AgentTestStartData> jobConfigFuture = new CompletableFuture<>();
    private volatile CompletableFuture<Void> transferCompleteFuture = new CompletableFuture<>();
    private final ConcurrentHashMap<String, IncomingFileState> incomingFiles = new ConcurrentHashMap<>();
    private final AtomicInteger completedFiles = new AtomicInteger(0);
    private volatile int expectedFiles = -1;
    private volatile AgentTestStartData receivedJobConfig;

    // Buffer for accumulating partial text frames
    private final StringBuilder messageBuffer = new StringBuilder();

    public AgentCommandWebSocketClient(String controllerBaseUrl, String wsPath, String token,
                                       String instanceId, String jobId) {
        this(controllerBaseUrl, wsPath, token, instanceId, jobId, null, false);
    }

    public AgentCommandWebSocketClient(String controllerBaseUrl, String wsPath, String token,
                                       String instanceId, String jobId,
                                       Integer capacity, boolean fileTransferEnabled) {
        this.controllerBaseUrl = controllerBaseUrl;
        this.wsPath = wsPath;
        this.token = token;
        this.instanceId = instanceId;
        this.jobId = jobId;
        this.agentSessionId = UUID.randomUUID().toString();
        this.helloCapacity = capacity;
        this.fileTransferEnabled = fileTransferEnabled;
        this.httpClient = HttpClient.newHttpClient();
        this.idleMonitor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "WS-IdleMonitor-" + instanceId);
            t.setDaemon(true);
            return t;
        });
        idleMonitor.scheduleAtFixedRate(this::checkIdleConnection, 30L, 30L, TimeUnit.SECONDS);
    }

    public void connect() {
        Thread connectThread = new Thread(this::connectWithRetry, "WS-Connect-" + instanceId);
        connectThread.setDaemon(true);
        connectThread.start();
    }

    private void connectWithRetry() {
        resetTransferState();
        int attempt = 0;
        while (running.get()) {
            try {
                String wsUrl = buildWsUrl();
                LOG.info(new ObjectMessage(Map.of("Message", "Connecting WS to " + wsUrl)));

                WebSocket ws = httpClient.newWebSocketBuilder()
                        .header("Authorization", "bearer " + token)
                        .buildAsync(URI.create(wsUrl), this)
                        .join();

                webSocketRef.set(ws);
                attempt = 0; // reset on successful connect

                // Send hello
                AgentWsEnvelope hello = AgentWsEnvelope.hello(instanceId, jobId, agentSessionId, lastAppliedCommandId, helloCapacity);
                ws.sendText(hello.toJson(), true);
                LOG.info(new ObjectMessage(Map.of("Message", "WS hello sent for agent " + instanceId + " job " + jobId)));

                // Block until connection closes (handled by listener callbacks)
                // The listener methods will handle incoming frames
                return;

            } catch (Exception e) {
                int backoff = BACKOFF_SECONDS[Math.min(attempt, BACKOFF_SECONDS.length - 1)];
                // Add jitter: 0-500ms
                int jitter = (int) (Math.random() * 500);
                LOG.warn(new ObjectMessage(Map.of("Message", "WS connect failed (attempt " + attempt + "): " + e.getMessage()
                        + ", retrying in " + backoff + "s")));
                try {
                    Thread.sleep(backoff * 1000L + jitter);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
                attempt++;
            }
        }
    }

    private synchronized void resetTransferState() {
        incomingFiles.values().forEach(IncomingFileState::closeAndDeleteTempQuietly);
        incomingFiles.clear();
        completedFiles.set(0);
        expectedFiles = -1;
        receivedJobConfig = null;
        jobConfigFuture = new CompletableFuture<>();
        transferCompleteFuture = new CompletableFuture<>();
    }

    private String buildWsUrl() {
        String base = controllerBaseUrl;
        // Convert http(s) to ws(s)
        if (base.startsWith("https://")) {
            base = "wss://" + base.substring("https://".length());
        } else if (base.startsWith("http://")) {
            base = "ws://" + base.substring("http://".length());
        }
        // Remove trailing slash
        if (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        return base + wsPath;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        LOG.info(new ObjectMessage(Map.of("Message", "WS connection opened for agent " + instanceId)));
        lastFrameReceivedAt = System.currentTimeMillis();
        webSocket.request(1);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        lastFrameReceivedAt = System.currentTimeMillis();
        messageBuffer.append(data);
        if (last) {
            String fullMessage = messageBuffer.toString();
            messageBuffer.setLength(0);
            handleMessage(webSocket, fullMessage);
        }
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        lastFrameReceivedAt = System.currentTimeMillis();
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onPong(WebSocket webSocket, ByteBuffer message) {
        lastFrameReceivedAt = System.currentTimeMillis();
        webSocket.request(1);
        return null;
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        LOG.info(new ObjectMessage(Map.of("Message", "WS closed: code=" + statusCode + " reason=" + reason)));
        webSocketRef.set(null);
        lastFrameReceivedAt = System.currentTimeMillis();
        scheduleReconnect();
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        LOG.error(new ObjectMessage(Map.of("Message", "WS error for agent " + instanceId + ": " + error.getMessage())), error);
        webSocketRef.set(null);
        lastFrameReceivedAt = System.currentTimeMillis();
        scheduleReconnect();
    }

    private void scheduleReconnect() {
        if (running.get() && reconnecting.compareAndSet(false, true)) {
            Thread reconnect = new Thread(() -> {
                try {
                    connectWithRetry();
                } finally {
                    reconnecting.set(false);
                }
            }, "WS-Reconnect-" + instanceId);
            reconnect.setDaemon(true);
            reconnect.start();
        }
    }

    private void handleMessage(WebSocket webSocket, String text) {
        try {
            AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(text);
            if (envelope.getType() == null) {
                LOG.warn(new ObjectMessage(Map.of("Message", "Received WS frame with no type")));
                return;
            }

            switch (envelope.getType()) {
                case command -> handleCommand(webSocket, envelope);
                case ping -> handlePing(webSocket, envelope);
                case ack -> LOG.info(new ObjectMessage(Map.of("Message", "Received ack: " + envelope.getAckForType())));
                case job_config -> handleJobConfig(envelope);
                case file_offer -> handleFileOffer(envelope);
                case file_chunk -> handleFileChunk(webSocket, envelope);
                default -> LOG.warn(new ObjectMessage(Map.of("Message", "Unexpected WS frame type: " + envelope.getType())));
            }
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to parse WS frame: " + e.getMessage())));
        }
    }

    private void handleCommand(WebSocket webSocket, AgentWsEnvelope envelope) {
        String commandId = envelope.getCommandId();
        String command = envelope.getCommand();

        // Bound the dedup set to prevent unbounded growth
        if (appliedCommandIds.size() > MAX_APPLIED_COMMAND_IDS) {
            appliedCommandIds.clear();
            LOG.info(new ObjectMessage(Map.of("Message", "Cleared appliedCommandIds set (exceeded " + MAX_APPLIED_COMMAND_IDS + ")")));
        }

        // Deduplicate
        if (commandId != null && !appliedCommandIds.add(commandId)) {
            LOG.info(new ObjectMessage(Map.of("Message", "Duplicate WS command " + commandId + " ignored")));
            sendAck(webSocket, commandId, AckStatus.duplicate);
            return;
        }

        LOG.info(new ObjectMessage(Map.of("Message", "Received WS command: " + command + " (id=" + commandId + ")")));

        try {
            applyCommand(command);
            lastAppliedCommandId = commandId;
            sendAck(webSocket, commandId, AckStatus.ok);
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "Error applying WS command " + command + ": " + e.getMessage())), e);
            sendAck(webSocket, commandId, AckStatus.failed);
        }
    }

    private void applyCommand(String command) {
        CommandListener.applyCommand(command);
    }

    private void handlePing(WebSocket webSocket, AgentWsEnvelope envelope) {
        try {
            AgentWsEnvelope pong = AgentWsEnvelope.pong(instanceId, agentSessionId, envelope.getPingId(), lastAppliedCommandId);
            webSocket.sendText(pong.toJson(), true);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send pong: " + e.getMessage())));
        }
    }

    private void handleJobConfig(AgentWsEnvelope envelope) {
        if (!fileTransferEnabled) {
            return;
        }
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

    private void handleFileOffer(AgentWsEnvelope envelope) {
        if (!fileTransferEnabled) {
            return;
        }

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
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed handling file_offer " + envelope.getFileName() + ": " + e.getMessage())));
        }
    }

    private void handleFileChunk(WebSocket webSocket, AgentWsEnvelope envelope) {
        if (!fileTransferEnabled) {
            return;
        }

        String fileId = envelope.getFileId();
        IncomingFileState state = fileId != null ? incomingFiles.get(fileId) : null;
        if (state == null) {
            sendFileAck(webSocket, fileId, envelope.getChunkIndex(), AckStatus.failed, "file_offer_not_found");
            return;
        }

        try {
            byte[] payload = envelope.getChunkData() == null || envelope.getChunkData().isEmpty()
                    ? new byte[0]
                    : Base64.getDecoder().decode(envelope.getChunkData());

            state.outputStream.write(payload);
            state.receivedBytes += payload.length;
            state.receivedChunks++;

            sendFileAck(webSocket, fileId, envelope.getChunkIndex(), AckStatus.chunk_received, null);

            if (state.receivedChunks >= state.totalChunks) {
                finalizeFile(state);
                incomingFiles.remove(fileId);
                sendFileAck(webSocket, fileId, envelope.getChunkIndex(), AckStatus.complete, null);

                int done = completedFiles.incrementAndGet();
                CompletableFuture<Void> currentTransferCompleteFuture = transferCompleteFuture;
                if (expectedFiles > 0 && done >= expectedFiles && !currentTransferCompleteFuture.isDone()) {
                    currentTransferCompleteFuture.complete(null);
                    sendFileAck(webSocket, fileId, envelope.getChunkIndex(), AckStatus.all_files_complete, null);
                }
            }
        } catch (Exception e) {
            incomingFiles.remove(fileId);
            state.closeQuietly();
            sendFileAck(webSocket, fileId, envelope.getChunkIndex(), AckStatus.failed, e.getMessage());
        }
    }

    private void finalizeFile(IncomingFileState state) throws IOException {
        state.closeQuietly();

        if (state.totalBytes != state.receivedBytes) {
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
                    Files.write(targetPath, zip.readAllBytes());
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

    private void sendFileAck(WebSocket webSocket, String fileId, Integer chunkIndex, AckStatus status, String error) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.fileAck(instanceId, jobId, fileId, chunkIndex, status, error);
            webSocket.sendText(ack.toJson(), true);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send file ack for " + fileId + ": " + e.getMessage())));
        }
    }

    private void sendAck(WebSocket webSocket, String commandId, AckStatus status) {
        try {
            AgentWsEnvelope ack = AgentWsEnvelope.ack(instanceId, "command", commandId, status);
            webSocket.sendText(ack.toJson(), true);
        } catch (IOException e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Failed to send ack for " + commandId + ": " + e.getMessage())));
        }
    }

    public void close() {
        running.set(false);
        idleMonitor.shutdownNow();
        WebSocket ws = webSocketRef.get();
        if (ws != null) {
            try {
                AgentWsEnvelope closeEnvelope = AgentWsEnvelope.close(instanceId, "agent_shutdown", "Agent shutting down");
                ws.sendText(closeEnvelope.toJson(), true);
            } catch (IOException ignored) {}
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "Agent shutting down");
        }
    }

    public boolean awaitInitialTransfer(long timeoutMillis) {
        if (!fileTransferEnabled) {
            return true;
        }
        try {
            CompletableFuture<AgentTestStartData> currentJobConfigFuture = jobConfigFuture;
            CompletableFuture<Void> currentTransferCompleteFuture = transferCompleteFuture;
            receivedJobConfig = currentJobConfigFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
            currentTransferCompleteFuture.get(timeoutMillis, TimeUnit.MILLISECONDS);
            return true;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "Timed out waiting for initial WS file transfer: " + e.getMessage())));
            return false;
        }
    }

    public AgentTestStartData getReceivedJobConfig() {
        return receivedJobConfig;
    }

    private void checkIdleConnection() {
        if (!running.get()) {
            return;
        }

        WebSocket ws = webSocketRef.get();
        if (ws == null) {
            return;
        }

        long idleFor = System.currentTimeMillis() - lastFrameReceivedAt;
        if (idleFor > 90_000L) {
            LOG.warn(new ObjectMessage(Map.of("Message", "No controller frame for " + idleFor + "ms, reconnecting WS")));
            webSocketRef.set(null);
            try {
                ws.abort();
            } catch (Exception ignored) {
            }
            scheduleReconnect();
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
        private final FileOutputStream outputStream;
        private volatile long receivedBytes;
        private volatile int receivedChunks;

        private IncomingFileState(String fileType, String fileName, File targetFile, File tempFile,
                                  long totalBytes, int totalChunks, boolean defaultDataFile,
                                  FileOutputStream outputStream) {
            this.fileType = fileType;
            this.fileName = fileName;
            this.targetFile = targetFile;
            this.tempFile = tempFile;
            this.totalBytes = totalBytes;
            this.totalChunks = totalChunks;
            this.defaultDataFile = defaultDataFile;
            this.outputStream = outputStream;
            this.receivedBytes = 0L;
            this.receivedChunks = 0;
        }

        private void closeQuietly() {
            try {
                outputStream.close();
            } catch (IOException ignored) {
            }
        }

        private void closeAndDeleteTempQuietly() {
            closeQuietly();
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (IOException ignored) {
            }
        }
    }
}

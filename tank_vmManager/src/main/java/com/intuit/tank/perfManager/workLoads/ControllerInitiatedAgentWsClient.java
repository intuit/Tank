package com.intuit.tank.perfManager.workLoads;

import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

@ApplicationScoped
public class ControllerInitiatedAgentWsClient implements AgentWsCommandSender {

    private static final Logger LOG = LogManager.getLogger(ControllerInitiatedAgentWsClient.class);
    private static final String API_HARNESS_JAR = "apiharness-1.0-all.jar";
    private static final String SETTINGS_FILE_NAME = "settings.xml";
    private static final String SCRIPT_FILE_NAME = "script.xml";
    private static final String LOCAL_CONTROLLER_ORIGIN = "http://localhost:8080";
    private static final int DEFAULT_CHUNK_BYTES =
            Math.max(1, Integer.getInteger("tank.ws.chunkBytes", 2 * 1024 * 1024));
    private static final int CHUNK_ACK_WINDOW =
            Math.max(1, Integer.getInteger("tank.ws.chunkAckWindow", 32));
    private static final long MAX_BOOTSTRAP_CONNECTION_MS =
            Long.getLong("tank.ws.bootstrap.maxConnectionMs", 180_000L);

    private final HttpClient httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    private final ConcurrentHashMap<String, SessionContext> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<AgentWsEnvelope>> pendingAcks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, PendingChunkAck> pendingChunkAcks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> fileTransferReady = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> agentLastSeen = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> agentWsState = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> agentTransferProgress = new ConcurrentHashMap<>();
    private volatile byte[] cachedHarnessJarBytes;

    private volatile VMTracker vmTracker;

    public ControllerInitiatedAgentWsClient() {
    }

    public void setVmTracker(VMTracker vmTracker) {
        this.vmTracker = vmTracker;
    }

    public Optional<AgentWsEnvelope> connect(String instanceId, String wsUrl, String token, long helloTimeoutMillis) {
        try {
            SessionContext existing = sessions.get(instanceId);
            if (existing != null && existing.isOpen()) {
                return Optional.ofNullable(existing.hello);
            }

            CompletableFuture<AgentWsEnvelope> helloFuture = new CompletableFuture<>();
            Listener listener = new Listener(instanceId, helloFuture);

            HttpClient wsHttpClient = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .connectTimeout(java.time.Duration.ofSeconds(10))
                    .build();
            WebSocket webSocket = wsHttpClient.newWebSocketBuilder()
                    .header("Authorization", "bearer " + token)
                    .buildAsync(URI.create(wsUrl), listener)
                    .join();

            SessionContext context = new SessionContext(webSocket, helloFuture);
            SessionContext previous = sessions.put(instanceId, context);
            if (previous != null) {
                previous.close();
            }
            agentWsState.put(instanceId, "connected");

            AgentWsEnvelope hello = helloFuture.get(helloTimeoutMillis, TimeUnit.MILLISECONDS);
            context.hello = hello;
            agentLastSeen.put(instanceId, System.currentTimeMillis());
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Controller initiated WS connected to agent " + instanceId + " via " + wsUrl)));
            return Optional.of(hello);
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "[WS] Failed to connect WS to agent " + instanceId + ": " + e.getMessage())));
        }

        SessionContext failed = sessions.remove(instanceId);
        if (failed != null) {
            failed.close();
        }
        agentWsState.put(instanceId, "disconnected");
        return Optional.empty();
    }

    public boolean connectAndBootstrap(JobManager jobManager, String instanceId, String instanceUrl, String wsUrl,
                                       String token, long helloTimeoutMillis, long transferTimeoutMillis) {
        Optional<AgentWsEnvelope> helloOptional = connect(instanceId, wsUrl, token, helloTimeoutMillis);
        if (helloOptional.isEmpty()) {
            return false;
        }

        AgentWsEnvelope hello = helloOptional.get();
        String agentId = hello.getInstanceId() != null ? hello.getInstanceId() : instanceId;
        bindActualAgentId(instanceId, agentId);
        SessionContext context = sessions.get(agentId);
        if (context == null || !context.isOpen()) {
            return false;
        }

        try {
            if (Boolean.TRUE.equals(hello.getNeedsBootstrap())) {
                return pushStartupBootstrapJar(agentId, context, transferTimeoutMillis);
            }

            agentWsState.put(agentId, "registered");
            AgentData agentData = jobManager.buildAgentDataForWsHello(
                    hello.getJobId(), agentId, instanceUrl, hello.getCapacity());
            AgentTestStartData startData = jobManager.registerAgentForJob(agentData);
            if (startData == null) {
                sendEnvelope(context, AgentWsEnvelope.close(agentId, "bootstrap_failed", "Job not found in controller cache"));
                context.close();
                return false;
            }

            List<TransferFile> files = buildTransferFiles(startData, authorizationHeader(token));
            context.jobId = startData.getJobId();
            context.expectedFiles = files.size();
            fileTransferReady.put(agentId, false);
            agentWsState.put(agentId, "transferring");
            agentTransferProgress.put(agentId, "0/" + files.size() + " files");

            sendEnvelope(context, AgentWsEnvelope.jobConfig(agentId, startData.getJobId(), startData, files.size()));
            sendFiles(context, agentId, startData.getJobId(), files, DEFAULT_CHUNK_BYTES);

            context.transferCompleteFuture.get(transferTimeoutMillis, TimeUnit.MILLISECONDS);
            fileTransferReady.put(agentId, true);
            agentWsState.put(agentId, "ready");
            agentTransferProgress.put(agentId, files.size() + "/" + files.size() + " files");
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] Controller initiated bootstrap complete for " + agentId)));
            return true;
        } catch (Exception e) {
            LOG.error(new ObjectMessage(Map.of("Message", "[WS] Controller initiated bootstrap failed for " + agentId + ": " + e.getMessage())), e);
            agentWsState.put(agentId, "disconnected");
            context.abort();
            return false;
        }
    }

    private void bindActualAgentId(String expectedId, String actualId) {
        if (expectedId.equals(actualId)) {
            return;
        }
        SessionContext context = sessions.remove(expectedId);
        if (context != null) {
            context.instanceId = actualId;
            sessions.put(actualId, context);
        }
    }

    @Override
    public boolean hasSession(String instanceId) {
        SessionContext context = sessions.get(instanceId);
        return context != null && context.isOpen();
    }

    @Override
    public boolean sendCommand(String instanceId, String jobId, String command, long ackTimeoutMillis) {
        SessionContext context = sessions.get(instanceId);
        if (context == null || !context.isOpen()) {
            return false;
        }

        String commandId = UUID.randomUUID().toString();
        CompletableFuture<AgentWsEnvelope> ackFuture = new CompletableFuture<>();
        pendingAcks.put(commandId, ackFuture);

        try {
            AgentWsEnvelope cmd = AgentWsEnvelope.command(commandId, instanceId, jobId, command);
            sendEnvelope(context, cmd);

            AgentWsEnvelope ack = ackFuture.get(ackTimeoutMillis, TimeUnit.MILLISECONDS);
            boolean success = ack != null && ack.getStatus() == AckStatus.ok;
            if (success && "start".equalsIgnoreCase(command)) {
                agentWsState.put(instanceId, "running");
            }
            return success;
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "[WS] Command " + command + " to " + instanceId + " failed: " + e.getMessage())));
            return false;
        } finally {
            pendingAcks.remove(commandId);
        }
    }

    @Override
    public boolean isFileTransferReady(String instanceId) {
        return Boolean.TRUE.equals(fileTransferReady.get(instanceId));
    }

    @Override
    public String getWsState(String instanceId) {
        return agentWsState.get(instanceId);
    }

    @Override
    public String getTransferProgress(String instanceId) {
        return agentTransferProgress.get(instanceId);
    }

    @Override
    public Long getLastSeen(String instanceId) {
        return agentLastSeen.get(instanceId);
    }

    private List<TransferFile> buildTransferFiles(AgentTestStartData startData, String authorizationHeader)
            throws IOException, InterruptedException {
        List<TransferFile> files = new ArrayList<>();
        files.add(new TransferFile("settings", SETTINGS_FILE_NAME, loadSettingsFile(), false));
        if (startData != null && startData.getScriptUrl() != null) {
            files.add(new TransferFile("script", SCRIPT_FILE_NAME,
                    readBytesFromUrl(startData.getScriptUrl(), authorizationHeader), false));
        }
        if (startData != null && startData.getDataFiles() != null) {
            for (DataFileRequest dataFileRequest : startData.getDataFiles()) {
                files.add(new TransferFile(
                        "data",
                        dataFileRequest.getFileName(),
                        readBytesFromUrl(dataFileRequest.getFileUrl(), authorizationHeader),
                        dataFileRequest.isDefaultDataFile()));
            }
        }
        return files;
    }

    private boolean pushStartupBootstrapJar(String agentId, SessionContext context, long transferTimeoutMillis)
            throws Exception {
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] Agent " + agentId
                + " needs startup bootstrap — pushing harness JAR"
                + " chunkBytes=" + DEFAULT_CHUNK_BYTES
                + " ackWindow=" + CHUNK_ACK_WINDOW
                + " maxConnectionMs=" + MAX_BOOTSTRAP_CONNECTION_MS)));
        File harnessJar = findHarnessJar();
        if (harnessJar == null || !harnessJar.exists() || !harnessJar.isFile()) {
            LOG.error(new ObjectMessage(Map.of("Message", "[WS] Harness JAR not found on controller for startup bootstrap")));
            context.close();
            return false;
        }

        context.jobId = "bootstrap";
        context.expectedFiles = 1;
        context.bootstrapTransfer = true;
        fileTransferReady.put(agentId, false);
        agentWsState.put(agentId, "bootstrap_transferring");
        agentTransferProgress.put(agentId, "0/1 files");

        byte[] jarBytes = cachedHarnessJarBytes;
        if (jarBytes == null) {
            synchronized (this) {
                jarBytes = cachedHarnessJarBytes;
                if (jarBytes == null) {
                    jarBytes = Files.readAllBytes(harnessJar.toPath());
                    cachedHarnessJarBytes = jarBytes;
                    LOG.info(new ObjectMessage(Map.of("Message",
                            "[WS] Cached harness JAR bytes: " + jarBytes.length + " bytes")));
                }
            }
        }
        List<TransferFile> bootstrapFiles = new ArrayList<>();
        bootstrapFiles.add(new TransferFile("support_jar", API_HARNESS_JAR, jarBytes, false));

        long connectionDeadlineMs = context.openedAtMs + MAX_BOOTSTRAP_CONNECTION_MS;
        boolean sentAllChunks = sendFilesWithBudget(context, agentId, "bootstrap", bootstrapFiles,
                DEFAULT_CHUNK_BYTES, connectionDeadlineMs);

        if (!sentAllChunks) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Bootstrap connection budget reached for " + agentId
                            + " — closing cleanly to resume on next connection")));
            agentWsState.put(agentId, "bootstrap_transferring");
            sessions.remove(agentId, context);
            context.closeGracefully("bootstrap_connection_budget_reached");
            return false;
        }

        context.transferCompleteFuture.get(transferTimeoutMillis, TimeUnit.MILLISECONDS);
        agentTransferProgress.put(agentId, "1/1 files");
        agentWsState.put(agentId, "bootstrap_sent");
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] Bootstrap JAR sent to " + agentId + " — waiting for harness to start")));
        context.close();
        return false;
    }

    private File findHarnessJar() {
        List<String> searchPaths = new ArrayList<>();
        String catalinaHome = System.getProperty("catalina.home");
        if (catalinaHome != null && !catalinaHome.isBlank()) {
            searchPaths.add(catalinaHome + "/webapps/ROOT/tools/" + API_HARNESS_JAR);
        }
        searchPaths.add("/opt/apache-tomcat-11.0.21/webapps/ROOT/tools/" + API_HARNESS_JAR);
        searchPaths.add("/etc/tank/jars/" + API_HARNESS_JAR);

        for (String path : searchPaths) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }

    private byte[] loadSettingsFile() throws IOException {
        File configFile = new TankConfig().getSourceConfigFile();
        File agentConfigFile = new File(configFile.getParentFile(), "agent-settings.xml");
        if (agentConfigFile.exists() && agentConfigFile.isFile()) {
            configFile = agentConfigFile;
        }
        return Files.readAllBytes(configFile.toPath());
    }

    private byte[] readBytesFromUrl(String url, String authorizationHeader) throws IOException, InterruptedException {
        URI uri = localControllerUri(url);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(uri);
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpResponse<byte[]> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Failed reading transfer source " + uri + " status=" + response.statusCode());
        }
        byte[] body = response.body();
        if ("gzip".equalsIgnoreCase(response.headers().firstValue("Content-Encoding").orElse(""))) {
            return ungzip(body);
        }
        return body;
    }

    private URI localControllerUri(String url) throws IOException {
        URI uri = URI.create(url);
        String path = uri.getRawPath();
        if (path == null || !(path.contains("/v2/jobs/") || path.contains("/v2/scripts/")
                || path.contains("/v2/datafiles/"))) {
            return uri;
        }
        StringBuilder local = new StringBuilder(LOCAL_CONTROLLER_ORIGIN).append(path);
        if (uri.getRawQuery() != null) {
            local.append('?').append(uri.getRawQuery());
        }
        try {
            return URI.create(local.toString());
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid local controller URI for " + url, e);
        }
    }

    private byte[] ungzip(byte[] compressed) throws IOException {
        try (InputStream in = new GZIPInputStream(new ByteArrayInputStream(compressed));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            return out.toByteArray();
        }
    }

    private String authorizationHeader(String token) {
        return token != null && !token.isBlank() ? "bearer " + token : null;
    }

    private void sendFiles(SessionContext context, String instanceId, String jobId, List<TransferFile> files, int chunkBytes)
            throws IOException, InterruptedException {
        for (TransferFile file : files) {
            sendFile(context, instanceId, jobId, file, chunkBytes, 0L);
        }
    }

    private boolean sendFilesWithBudget(SessionContext context, String instanceId, String jobId,
                                        List<TransferFile> files, int chunkBytes, long connectionDeadlineMs)
            throws IOException, InterruptedException {
        try {
            for (TransferFile file : files) {
                if (!sendFile(context, instanceId, jobId, file, chunkBytes, connectionDeadlineMs)) {
                    return false;
                }
            }
            return true;
        } catch (BootstrapBudgetExceededException e) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Bootstrap transfer budget reached for " + instanceId
                            + " during ack wait — closing cleanly to resume")));
            pendingChunkAcks.remove(instanceId);
            return false;
        }
    }

    private boolean sendFile(SessionContext context, String instanceId, String jobId, TransferFile file,
                             int chunkBytes, long connectionDeadlineMs)
            throws IOException, InterruptedException {
        byte[] content = file.content();
        long totalBytes = content != null ? content.length : 0L;
        int totalChunks = Math.max(1, (int) Math.ceil((double) totalBytes / chunkBytes));
        String fileId = UUID.randomUUID().toString();
        long transferStartedAtNs = System.nanoTime();

        // Send offer and wait for ack (may include resume offset)
        CompletableFuture<AgentWsEnvelope> offerAckFuture = new CompletableFuture<>();
        pendingAcks.put(fileId, offerAckFuture);
        sendEnvelope(context, AgentWsEnvelope.fileOffer(
                instanceId,
                jobId,
                fileId,
                file.fileType(),
                file.fileName(),
                totalBytes,
                totalChunks,
                chunkBytes,
                file.defaultDataFile()));

        if (content == null || content.length == 0) {
            pendingAcks.remove(fileId);
            sendChunk(context, instanceId, fileId, 0, new byte[0], 0, 0, connectionDeadlineMs);
            return true;
        }

        // Check for offer ack (ok, resume, or failed)
        int startOffset = 0;
        int startChunk = 0;
        try {
            AgentWsEnvelope offerAck = offerAckFuture.get(10, TimeUnit.SECONDS);
            if (offerAck != null) {
                if (offerAck.getStatus() == AckStatus.failed) {
                    throw new IOException("File offer rejected by agent: " + offerAck.getError());
                }
                if (offerAck.getStatus() == AckStatus.resume
                        && offerAck.getResumeOffset() != null && offerAck.getResumeOffset() > 0) {
                    startOffset = offerAck.getResumeOffset().intValue();
                    startChunk = offerAck.getChunkIndex() != null ? offerAck.getChunkIndex() : 0;
                    LOG.info(new ObjectMessage(Map.of("Message",
                            "[WS] Resuming file transfer for " + instanceId + " from offset=" + startOffset
                                    + " chunk=" + startChunk + " (" + startOffset + "/" + totalBytes + " bytes)")));
                }
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            // No ack or timeout — start from beginning (backward compatible)
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] No file offer ack from " + instanceId + " — starting transfer from beginning")));
        } finally {
            pendingAcks.remove(fileId);
        }

        int chunkIndex = startChunk;
        for (int offset = startOffset; offset < content.length; offset += chunkBytes) {
            if (connectionDeadlineMs > 0 && System.currentTimeMillis() >= connectionDeadlineMs) {
                LOG.info(new ObjectMessage(Map.of("Message",
                        "[WS] Bootstrap transfer budget reached for " + instanceId
                                + " at chunk=" + chunkIndex + " offset=" + offset
                                + "/" + totalBytes + " — will reconnect and resume")));
                return false;
            }
            int len = Math.min(chunkBytes, content.length - offset);
            sendChunk(context, instanceId, fileId, chunkIndex, content, offset, len, connectionDeadlineMs);
            chunkIndex++;
        }
        logFileTransferComplete(instanceId, file, totalBytes, totalChunks, chunkBytes,
                startOffset, chunkIndex - startChunk, transferStartedAtNs);
        return true;
    }

    private void logFileTransferComplete(String instanceId, TransferFile file, long totalBytes, int totalChunks,
                                         int chunkBytes, int startOffset, int chunksSent, long transferStartedAtNs) {
        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - transferStartedAtNs);
        long sentBytes = Math.max(0L, totalBytes - startOffset);
        double throughputMiBps = durationMs > 0
                ? Math.round(((sentBytes / (1024.0 * 1024.0)) / (durationMs / 1000.0)) * 100.0) / 100.0
                : 0.0;
        LOG.info(new ObjectMessage(Map.of("Message",
                "[WS] File transfer complete for " + instanceId
                        + " file=" + file.fileName()
                        + " bytes=" + totalBytes
                        + " chunks=" + chunksSent + "/" + totalChunks
                        + " chunkBytes=" + chunkBytes
                        + " resumed=" + (startOffset > 0)
                        + " resumeOffset=" + startOffset
                        + " durationMs=" + durationMs
                        + " throughputMiBps=" + throughputMiBps)));
    }

    private void sendChunk(SessionContext context, String instanceId, String fileId,
                           int chunkIndex, byte[] bytes, int offset, int len, long connectionDeadlineMs)
            throws IOException, InterruptedException {
        CompletableFuture<Void> gate = null;
        if ((chunkIndex + 1) % CHUNK_ACK_WINDOW == 0) {
            gate = new CompletableFuture<>();
            pendingChunkAcks.put(instanceId, new PendingChunkAck(fileId, chunkIndex, gate));
        }
        sendBinaryChunk(context, AgentWsEnvelope.binaryFileChunk(fileId, chunkIndex, bytes, offset, len));
        if (gate != null) {
            try {
                long ackTimeoutMs = 30_000L;
                if (connectionDeadlineMs > 0) {
                    long remainingMs = connectionDeadlineMs - System.currentTimeMillis();
                    if (remainingMs <= 0) {
                        throw new BootstrapBudgetExceededException(
                                "Bootstrap connection budget reached during chunk ack wait");
                    }
                    ackTimeoutMs = Math.min(ackTimeoutMs, remainingMs);
                }
                gate.get(ackTimeoutMs, TimeUnit.MILLISECONDS);
            } catch (BootstrapBudgetExceededException e) {
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } catch (java.util.concurrent.TimeoutException e) {
                if (connectionDeadlineMs > 0 && System.currentTimeMillis() >= connectionDeadlineMs) {
                    throw new BootstrapBudgetExceededException(
                            "Bootstrap connection budget reached while waiting for chunk ack");
                }
                throw new IOException("Timed out waiting for WS chunk ack for " + instanceId, e);
            } catch (Exception e) {
                throw new IOException("Timed out waiting for WS chunk ack for " + instanceId, e);
            }
        }
    }

    private void sendBinaryChunk(SessionContext context, ByteBuffer payload) {
        synchronized (context.webSocket) {
            context.webSocket.sendBinary(payload, true).join();
        }
    }

    private void sendEnvelope(SessionContext context, AgentWsEnvelope envelope) throws IOException {
        synchronized (context.webSocket) {
            context.webSocket.sendText(envelope.toJson(), true).join();
        }
    }

    private void handleText(String instanceId, WebSocket webSocket, String text,
                            CompletableFuture<AgentWsEnvelope> helloFuture) {
        try {
            AgentWsEnvelope envelope = AgentWsEnvelope.fromJson(text);
            if (envelope.getType() == null) {
                return;
            }
            String agentId = envelope.getInstanceId() != null ? envelope.getInstanceId() : instanceId;
            agentLastSeen.put(agentId, System.currentTimeMillis());
            switch (envelope.getType()) {
                case hello -> helloFuture.complete(envelope);
                case ack -> handleAck(envelope);
                case file_ack -> handleFileAck(agentId, webSocket, envelope);
                case status_update -> handleStatusUpdate(agentId, envelope);
                case pong -> LOG.debug(new ObjectMessage(Map.of("Message", "[WS] Pong from " + agentId)));
                case close -> onClosed(agentId, webSocket);
                default -> {
                }
            }
        } catch (IOException ignored) {
        }
    }

    private void handleAck(AgentWsEnvelope envelope) {
        String ackForId = envelope.getAckForId();
        if (ackForId != null) {
            CompletableFuture<AgentWsEnvelope> future = pendingAcks.remove(ackForId);
            if (future != null) {
                future.complete(envelope);
            }
        }
    }

    private void handleFileAck(String instanceId, WebSocket webSocket, AgentWsEnvelope envelope) {
        SessionContext context = sessions.get(instanceId);
        if (context == null) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring file_ack for non-active session " + instanceId)));
            return;
        }
        if (webSocket == null) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring file_ack without WebSocket identity for " + instanceId)));
            return;
        }
        if (context.webSocket != webSocket) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring stale file_ack from previous session for " + instanceId)));
            return;
        }

        // Route offer-level acks (ok, resume, failed) to the pending offer future
        if (envelope.getFileId() != null && (envelope.getStatus() == AckStatus.ok
                || envelope.getStatus() == AckStatus.resume
                || envelope.getStatus() == AckStatus.failed)) {
            CompletableFuture<AgentWsEnvelope> offerFuture = pendingAcks.remove(envelope.getFileId());
            if (offerFuture != null) {
                offerFuture.complete(envelope);
                return;
            }
        }

        if (envelope.getStatus() == AckStatus.all_files_complete) {
            fileTransferReady.put(instanceId, true);
            agentWsState.put(instanceId, "ready");
            PendingChunkAck pending = pendingChunkAcks.get(instanceId);
            if (pending != null && pending.matches(envelope.getFileId(), envelope.getChunkIndex())) {
                pendingChunkAcks.remove(instanceId, pending);
                pending.future.complete(null);
            }
            context.transferCompleteFuture.complete(null);
            return;
        }

        if (envelope.getStatus() == AckStatus.complete) {
            int completed = context.completedFiles.merge(instanceId, 1, Integer::sum);
            agentTransferProgress.put(instanceId, completed + "/" + context.expectedFiles + " files");
            if (context.bootstrapTransfer && completed >= context.expectedFiles) {
                context.transferCompleteFuture.complete(null);
            }
        }

        if (envelope.getStatus() == AckStatus.complete || envelope.getStatus() == AckStatus.chunk_received) {
            PendingChunkAck pending = pendingChunkAcks.get(instanceId);
            if (pending != null && pending.matches(envelope.getFileId(), envelope.getChunkIndex())) {
                pendingChunkAcks.remove(instanceId, pending);
                pending.future.complete(null);
            }
        } else if (envelope.getStatus() == AckStatus.failed) {
            PendingChunkAck pending = pendingChunkAcks.get(instanceId);
            if (pending != null && pending.matches(envelope.getFileId(), envelope.getChunkIndex())) {
                pendingChunkAcks.remove(instanceId, pending);
                pending.future.completeExceptionally(new IOException(envelope.getError()));
            }
            context.transferCompleteFuture.completeExceptionally(new IOException(envelope.getError()));
        }
    }

    private void handleStatusUpdate(String instanceId, AgentWsEnvelope envelope) {
        CloudVmStatus status = envelope.getInstanceStatus();
        if (status == null || vmTracker == null) {
            return;
        }
        try {
            status.setInstanceId(instanceId);
            vmTracker.setStatus(status);
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] Failed status update from " + instanceId + ": " + e.getMessage())));
        }
    }

    private void onClosed(String instanceId, WebSocket webSocket) {
        SessionContext context = sessions.get(instanceId);
        if (context == null) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring close for non-active session " + instanceId)));
            return;
        }
        if (webSocket == null) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring close without WebSocket identity for " + instanceId)));
            return;
        }
        if (context.webSocket != webSocket) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring stale close from previous session for " + instanceId)));
            return;
        }
        if (!sessions.remove(instanceId, context)) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring close for replaced session " + instanceId)));
            return;
        }
        context.markClosed();
        fileTransferReady.remove(instanceId);
        PendingChunkAck pending = pendingChunkAcks.remove(instanceId);
        if (pending != null) {
            pending.future.completeExceptionally(new IOException("WS connection closed for " + instanceId));
        }
        agentWsState.put(instanceId, "disconnected");
    }

    private class Listener implements WebSocket.Listener {
        private final String instanceId;
        private final CompletableFuture<AgentWsEnvelope> helloFuture;
        private final StringBuilder messageBuffer = new StringBuilder();

        private Listener(String instanceId, CompletableFuture<AgentWsEnvelope> helloFuture) {
            this.instanceId = instanceId;
            this.helloFuture = helloFuture;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            messageBuffer.append(data);
            if (last) {
                String fullMessage = messageBuffer.toString();
                messageBuffer.setLength(0);
                handleText(instanceId, webSocket, fullMessage, helloFuture);
            }
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
            webSocket.request(1);
            return null;
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            onClosed(instanceId, webSocket);
            return null;
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "[WS] Controller initiated WS listener error for " + instanceId + ": " + error.getMessage())));
            onClosed(instanceId, webSocket);
        }
    }

    private static class SessionContext {
        private final WebSocket webSocket;
        private final CompletableFuture<AgentWsEnvelope> helloFuture;
        private final CompletableFuture<Void> transferCompleteFuture = new CompletableFuture<>();
        private final ConcurrentHashMap<String, Integer> completedFiles = new ConcurrentHashMap<>();
        private final AtomicBoolean closed = new AtomicBoolean(false);
        private final long openedAtMs;
        private volatile String instanceId;
        private volatile String jobId;
        private volatile int expectedFiles;
        private volatile boolean bootstrapTransfer;
        private volatile AgentWsEnvelope hello;

        private SessionContext(WebSocket webSocket, CompletableFuture<AgentWsEnvelope> helloFuture) {
            this.webSocket = webSocket;
            this.helloFuture = helloFuture;
            this.openedAtMs = System.currentTimeMillis();
        }

        private boolean isOpen() {
            return !closed.get() && !webSocket.isInputClosed() && !webSocket.isOutputClosed();
        }

        private void close() {
            closeGracefully("Closing session");
        }

        private void closeGracefully(String reason) {
            if (closed.compareAndSet(false, true)) {
                try {
                    webSocket.sendClose(WebSocket.NORMAL_CLOSURE, reason)
                            .orTimeout(2, TimeUnit.SECONDS)
                            .exceptionally(t -> {
                                webSocket.abort();
                                return webSocket;
                            })
                            .join();
                } catch (Exception e) {
                    try { webSocket.abort(); } catch (Exception ignored) {}
                }
                helloFuture.completeExceptionally(new IllegalStateException(reason));
                transferCompleteFuture.completeExceptionally(new IllegalStateException(reason));
            }
        }

        private void abort() {
            if (closed.compareAndSet(false, true)) {
                try { webSocket.abort(); } catch (Exception ignored) {}
                helloFuture.completeExceptionally(new IllegalStateException("Aborted"));
                transferCompleteFuture.completeExceptionally(new IllegalStateException("Aborted"));
            }
        }

        private void markClosed() {
            closed.set(true);
            helloFuture.completeExceptionally(new IllegalStateException("Closed"));
            transferCompleteFuture.completeExceptionally(new IllegalStateException("Closed"));
        }
    }

    private record TransferFile(String fileType, String fileName, byte[] content, boolean defaultDataFile) {
    }

    private static class PendingChunkAck {
        private final String fileId;
        private final int chunkIndex;
        private final CompletableFuture<Void> future;

        private PendingChunkAck(String fileId, int chunkIndex, CompletableFuture<Void> future) {
            this.fileId = fileId;
            this.chunkIndex = chunkIndex;
            this.future = future;
        }

        private boolean matches(String fileId, Integer chunkIndex) {
            return this.fileId.equals(fileId) && chunkIndex != null && this.chunkIndex == chunkIndex;
        }
    }

    private static class BootstrapBudgetExceededException extends IOException {
        private BootstrapBudgetExceededException(String message) {
            super(message);
        }
    }
}

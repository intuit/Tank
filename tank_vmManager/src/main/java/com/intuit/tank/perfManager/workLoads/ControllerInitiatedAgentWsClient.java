package com.intuit.tank.perfManager.workLoads;

import com.intuit.tank.storage.FileData;
import com.intuit.tank.storage.FileStorage;
import com.intuit.tank.storage.FileStorageFactory;
import com.intuit.tank.vm.agent.messages.AgentData;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMTerminator;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http2.client.HTTP2Client;
import org.eclipse.jetty.http2.client.transport.HttpClientTransportOverHTTP2;
import org.eclipse.jetty.websocket.api.Callback;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.ClientUpgradeRequest;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;

/**
 * Controller-side client that pushes bootstrap and job files to agents over WebSocket-over-HTTP/2
 * (RFC 8441) using the Jetty 12 WebSocket client. Each file is offered, then sent as one WebSocket
 * message; framing, HTTP/2 flow control and backpressure are left to Jetty.
 */
@ApplicationScoped
public class ControllerInitiatedAgentWsClient implements AgentWsCommandSender {

    private static final Logger LOG = LogManager.getLogger(ControllerInitiatedAgentWsClient.class);
    private static final String API_HARNESS_JAR = "apiharness-1.0-all.jar";
    private static final String START_AGENT_SCRIPT = "startAgent.sh";
    private static final String STARTUP_SCRIPT_FILE_TYPE = "startup_script";
    private static final String SETTINGS_FILE_NAME = "settings.xml";
    private static final String SCRIPT_FILE_NAME = "script.xml";
    private static final String LOCAL_CONTROLLER_ORIGIN = "http://localhost:8080";
    // Agents reject WS messages over 64 MB (StartupWebSocketServer, AgentCommandWebSocketServer), and each
    // file is sent as one message, so no file may exceed this.
    private static final long MAX_WS_MESSAGE_BYTES = 64L * 1024 * 1024;
    // Jetty's default WS idle timeout is 30s. The controller holds connections idle while waiting for
    // a whole fleet of agents to become ready before broadcasting START — raise it well past that.
    private static final long WS_IDLE_TIMEOUT_MS =
            Math.max(60_000L, Long.getLong("tank.ws.idleTimeoutMs", 600_000L));
    // Interval for the application-level keepalive ping that holds idle connections open and detects
    // dead ones early (well under WS_IDLE_TIMEOUT_MS).
    private static final long WS_KEEPALIVE_PING_MS =
            Math.max(5_000L, Long.getLong("tank.ws.keepAlivePingMs", 10_000L));
    // A half-open TCP connection still accepts writes, so silence from the agent is the only reliable
    // sign the session is dead. Closing it lets AgentReadoption reconnect.
    private static final long WS_STALE_SESSION_MS =
            Math.max(3 * WS_KEEPALIVE_PING_MS, Long.getLong("tank.ws.staleSessionMs", 60_000L));

    private final java.net.http.HttpClient httpClient =
            java.net.http.HttpClient.newBuilder().build();
    private final ConcurrentHashMap<String, SessionContext> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<AgentWsEnvelope>> pendingAcks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> fileTransferReady = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> agentLastSeen = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> agentWsState = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> agentTransferProgress = new ConcurrentHashMap<>();
    private final java.util.Set<String> terminationRequestedInstances = ConcurrentHashMap.newKeySet();
    private volatile byte[] cachedHarnessJarBytes;
    private volatile Optional<byte[]> cachedStartupScript;
    private volatile WebSocketClient wsClient;
    private volatile java.util.concurrent.ScheduledExecutorService keepAliveExecutor;

    private volatile VMTracker vmTracker;
    private volatile VMTerminator vmTerminator;
    private volatile boolean closeStaleSessions;

    public ControllerInitiatedAgentWsClient() {
    }

    public void setVmTracker(VMTracker vmTracker) {
        this.vmTracker = vmTracker;
    }

    public void setVmTerminator(VMTerminator vmTerminator) {
        this.vmTerminator = vmTerminator;
    }

    public void setCloseStaleSessions(boolean closeStaleSessions) {
        this.closeStaleSessions = closeStaleSessions;
    }

    private WebSocketClient webSocketClient() throws Exception {
        WebSocketClient client = wsClient;
        if (client == null) {
            synchronized (this) {
                client = wsClient;
                if (client == null) {
                    HTTP2Client http2Client = new HTTP2Client();
                    HttpClient jettyHttpClient = new HttpClient(new HttpClientTransportOverHTTP2(http2Client));
                    client = new WebSocketClient(jettyHttpClient);
                    client.setMaxBinaryMessageSize(MAX_WS_MESSAGE_BYTES);
                    client.setMaxTextMessageSize(MAX_WS_MESSAGE_BYTES);
                    // Match the agents' raised idle timeout: the controller may hold connections idle
                    // while waiting for a whole fleet to become ready before broadcasting START.
                    client.setIdleTimeout(java.time.Duration.ofMillis(WS_IDLE_TIMEOUT_MS));
                    client.start();
                    wsClient = client;
                    startKeepAlive();
                }
            }
        }
        return client;
    }

    /**
     * Periodically pings every open session so a connection held idle (e.g. while the controller waits
     * for a whole fleet to become ready before broadcasting START) is kept warm and not closed by the
     * peer's idle timeout. The agent servers reply with pong; a send failure closes the dead session.
     */
    private void startKeepAlive() {
        java.util.concurrent.ScheduledExecutorService executor =
                java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
                    Thread t = new Thread(r, "ws-keepalive");
                    t.setDaemon(true);
                    return t;
                });
        executor.scheduleWithFixedDelay(this::pingOpenSessions,
                WS_KEEPALIVE_PING_MS, WS_KEEPALIVE_PING_MS, TimeUnit.MILLISECONDS);
        keepAliveExecutor = executor;
    }

    private void pingOpenSessions() {
        for (Map.Entry<String, SessionContext> entry : sessions.entrySet()) {
            SessionContext context = entry.getValue();
            if (context == null || !context.isOpen()) {
                continue;
            }
            long lastSeen = Math.max(context.openedAtMs, agentLastSeen.getOrDefault(entry.getKey(), 0L));
            if (closeStaleSessions && System.currentTimeMillis() - lastSeen > WS_STALE_SESSION_MS) {
                LOG.warn(new ObjectMessage(Map.of("Message",
                        "[WS] No traffic from " + entry.getKey() + " for " + WS_STALE_SESSION_MS + "ms — closing stale session")));
                if (sessions.remove(entry.getKey(), context)) {
                    context.abort();
                    fileTransferReady.remove(entry.getKey());
                    agentWsState.put(entry.getKey(), "disconnected");
                }
                continue;
            }
            try {
                sendEnvelope(context, AgentWsEnvelope.ping(UUID.randomUUID().toString()));
            } catch (Exception e) {
                LOG.debug(new ObjectMessage(Map.of("Message",
                        "[WS] Keepalive ping failed for " + entry.getKey() + ": " + e.getMessage())));
            }
        }
    }

    public Optional<AgentWsEnvelope> connect(String instanceId, String wsUrl, String token, long helloTimeoutMillis) {
        try {
            SessionContext existing = sessions.get(instanceId);
            if (existing != null && existing.isOpen()) {
                return Optional.ofNullable(existing.hello);
            }

            CompletableFuture<AgentWsEnvelope> helloFuture = new CompletableFuture<>();
            Endpoint endpoint = new Endpoint(this, instanceId, helloFuture);

            ClientUpgradeRequest upgradeRequest = new ClientUpgradeRequest();
            upgradeRequest.setHeader("Authorization", "bearer " + token);

            Session session = webSocketClient()
                    .connect(endpoint, URI.create(wsUrl), upgradeRequest)
                    .get(10, TimeUnit.SECONDS);

            SessionContext context = new SessionContext(session, endpoint, helloFuture);
            endpoint.context = context;
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

    public void disconnect(String instanceId) {
        SessionContext context = sessions.remove(instanceId);
        if (context != null) {
            context.close();
        }
        fileTransferReady.remove(instanceId);
        agentWsState.put(instanceId, "disconnected");
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
            sendFiles(context, agentId, startData.getJobId(), files);

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
        File harnessJar = findHarnessJar();
        if (harnessJar == null || !harnessJar.exists() || !harnessJar.isFile()) {
            LOG.error(new ObjectMessage(Map.of("Message", "[WS] Harness JAR not found on controller for startup bootstrap")));
            context.close();
            return false;
        }

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
        Optional<byte[]> startupScript = loadStartupScript();
        List<TransferFile> bootstrapFiles = buildStartupBootstrapFiles(jarBytes, startupScript);
        int bootstrapFileCount = bootstrapFiles.size();
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] Agent " + agentId
                + " needs startup bootstrap — pushing " + bootstrapFileCount + " files"
                + " customStartAgent=" + startupScript.isPresent()
                + " transfer=whole_file")));

        context.jobId = "bootstrap";
        context.expectedFiles = bootstrapFileCount;
        context.bootstrapTransfer = true;
        fileTransferReady.put(agentId, false);
        agentWsState.put(agentId, "bootstrap_transferring");
        agentTransferProgress.put(agentId, "0/" + bootstrapFileCount + " files");

        try {
            sendFiles(context, agentId, "bootstrap", bootstrapFiles);
        } catch (UnsupportedStartupScriptException e) {
            bootstrapFiles = buildStartupBootstrapFiles(jarBytes, Optional.empty());
            bootstrapFileCount = bootstrapFiles.size();
            context.expectedFiles = bootstrapFileCount;
            context.completedFiles.clear();
            agentTransferProgress.put(agentId, "0/" + bootstrapFileCount + " files");
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] Agent " + agentId
                    + " does not support startup_script — retrying bootstrap with harness JAR only")));
            sendFiles(context, agentId, "bootstrap", bootstrapFiles);
        }

        context.transferCompleteFuture.get(transferTimeoutMillis, TimeUnit.MILLISECONDS);
        agentTransferProgress.put(agentId, bootstrapFileCount + "/" + bootstrapFileCount + " files");
        agentWsState.put(agentId, "bootstrap_sent");
        LOG.info(new ObjectMessage(Map.of("Message", "[WS] Startup bootstrap files sent to " + agentId
                + " — waiting for harness to start")));
        sessions.remove(agentId, context);
        fileTransferReady.remove(agentId);
        context.close();
        return false;
    }

    private Optional<byte[]> loadStartupScript() throws IOException {
        Optional<byte[]> startupScript = cachedStartupScript;
        if (startupScript == null) {
            synchronized (this) {
                startupScript = cachedStartupScript;
                if (startupScript == null) {
                    FileStorage fileStorage =
                            FileStorageFactory.getFileStorage(new TankConfig().getJarDir(), false);
                    startupScript = readStartupScript(fileStorage);
                    cachedStartupScript = startupScript;
                }
            }
        }
        return startupScript;
    }

    static Optional<byte[]> readStartupScript(FileStorage fileStorage) throws IOException {
        FileData startupScript = new FileData("", START_AGENT_SCRIPT);
        if (!fileStorage.exists(startupScript)) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "[WS] Custom startAgent.sh not found in configured JAR storage — using packaged default")));
            return Optional.empty();
        }
        try (InputStream input = fileStorage.readFileData(startupScript)) {
            return Optional.of(input.readAllBytes());
        } catch (RuntimeException e) {
            throw new IOException("Failed reading startAgent.sh from configured JAR storage", e);
        }
    }

    static List<TransferFile> buildStartupBootstrapFiles(byte[] jarBytes, Optional<byte[]> startupScript) {
        List<TransferFile> files = new ArrayList<>();
        startupScript.ifPresent(content ->
                files.add(new TransferFile(STARTUP_SCRIPT_FILE_TYPE, START_AGENT_SCRIPT, content, false)));
        files.add(new TransferFile("support_jar", API_HARNESS_JAR, jarBytes, false));
        return files;
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

    private void sendFiles(SessionContext context, String instanceId, String jobId, List<TransferFile> files)
            throws IOException {
        for (TransferFile file : files) {
            sendFile(context, instanceId, jobId, file);
        }
    }

    private void sendFile(SessionContext context, String instanceId, String jobId, TransferFile file)
            throws IOException {
        byte[] content = file.content() != null ? file.content() : new byte[0];
        String fileId = UUID.randomUUID().toString();
        ByteBuffer header = AgentWsEnvelope.binaryFileChunkHeader(fileId, 0);
        if ((long) header.remaining() + content.length > MAX_WS_MESSAGE_BYTES) {
            throw new IOException("File " + file.fileName() + " is " + content.length
                    + " bytes; whole-file WS transfer is limited to " + MAX_WS_MESSAGE_BYTES + " bytes per file");
        }
        long transferStartedAtNs = System.nanoTime();

        CompletableFuture<AgentWsEnvelope> offerAckFuture = new CompletableFuture<>();
        pendingAcks.put(fileId, offerAckFuture);
        // chunkBytes == totalBytes aligns any agent-side resume offset to 0, so the whole file is always sent.
        sendEnvelope(context, AgentWsEnvelope.fileOffer(
                instanceId,
                jobId,
                fileId,
                file.fileType(),
                file.fileName(),
                content.length,
                1,
                Math.max(1, content.length),
                file.defaultDataFile()));

        try {
            AgentWsEnvelope offerAck = offerAckFuture.get(10, TimeUnit.SECONDS);
            if (offerAck != null && offerAck.getStatus() == AckStatus.failed) {
                if (isLegacyStartupScriptRejection(file, offerAck)) {
                    throw new UnsupportedStartupScriptException();
                }
                throw new IOException("File offer rejected by agent: " + offerAck.getError());
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] No file offer ack from " + instanceId + " — sending file anyway")));
        } finally {
            pendingAcks.remove(fileId);
        }

        sendWholeFile(context, header, content);
        logFileSent(instanceId, file, content.length, transferStartedAtNs);
    }

    static boolean isLegacyStartupScriptRejection(TransferFile file, AgentWsEnvelope offerAck) {
        return STARTUP_SCRIPT_FILE_TYPE.equals(file.fileType())
                && START_AGENT_SCRIPT.equals(file.fileName())
                && offerAck != null
                && offerAck.getStatus() == AckStatus.failed
                && "unsupported_startup_file".equals(offerAck.getError());
    }

    private void logFileSent(String instanceId, TransferFile file, long totalBytes, long transferStartedAtNs) {
        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - transferStartedAtNs);
        double throughputMiBps = durationMs > 0
                ? Math.round(((totalBytes / (1024.0 * 1024.0)) / (durationMs / 1000.0)) * 100.0) / 100.0
                : 0.0;
        LOG.info(new ObjectMessage(Map.of("Message",
                "[WS] File sent to " + instanceId
                        + " file=" + file.fileName()
                        + " bytes=" + totalBytes
                        + " transfer=whole_file"
                        + " durationMs=" + durationMs
                        + " throughputMiBps=" + throughputMiBps)));
    }

    /**
     * Sends the file as one WebSocket message: the binary file header, then the whole file. Both fragments
     * are queued under the session lock so no other message can interleave between them.
     */
    private void sendWholeFile(SessionContext context, ByteBuffer header, byte[] content) {
        Callback.Completable headerSent = new Callback.Completable();
        Callback.Completable fileSent = new Callback.Completable();
        synchronized (context.session) {
            context.session.sendPartialBinary(header, false, headerSent);
            headerSent.join();
            // The harness JAR bytes are shared by every concurrent push; read-only keeps Jetty from mutating them.
            context.session.sendPartialBinary(ByteBuffer.wrap(content).asReadOnlyBuffer(), true, fileSent);
        }
        fileSent.join();
    }

    private void sendEnvelope(SessionContext context, AgentWsEnvelope envelope) throws IOException {
        Callback.Completable callback = new Callback.Completable();
        synchronized (context.session) {
            context.session.sendText(envelope.toJson(), callback);
        }
        callback.join();
    }

    private void handleText(String instanceId, Session session, String text,
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
                case file_ack -> handleFileAck(agentId, session, envelope);
                case status_update -> handleStatusUpdate(instanceId, envelope);
                case pong -> LOG.debug(new ObjectMessage(Map.of("Message", "[WS] Pong from " + agentId)));
                case close -> onClosed(agentId, session);
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

    private void handleFileAck(String instanceId, Session session, AgentWsEnvelope envelope) {
        SessionContext context = sessions.get(instanceId);
        if (context == null) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring file_ack for non-active session " + instanceId)));
            return;
        }
        if (session == null) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring file_ack without WebSocket identity for " + instanceId)));
            return;
        }
        if (context.session != session) {
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
            context.transferCompleteFuture.complete(null);
            return;
        }

        if (envelope.getStatus() == AckStatus.complete) {
            int completed = context.completedFiles.merge(instanceId, 1, Integer::sum);
            agentTransferProgress.put(instanceId, completed + "/" + context.expectedFiles + " files");
            if (context.bootstrapTransfer && completed >= context.expectedFiles) {
                context.transferCompleteFuture.complete(null);
            }
        } else if (envelope.getStatus() == AckStatus.failed) {
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
            requestTerminationForTerminalStatus(instanceId, status);
            vmTracker.setStatus(status);
        } catch (Exception e) {
            LOG.warn(new ObjectMessage(Map.of("Message", "[WS] Failed status update from " + instanceId + ": " + e.getMessage())));
        }
    }

    private void requestTerminationForTerminalStatus(String instanceId, CloudVmStatus status) {
        if (!isTerminalStatus(status)) {
            return;
        }
        VMTerminator terminator = vmTerminator;
        if (terminator == null) {
            LOG.error(new ObjectMessage(Map.of("Message", "[WS] Terminal status from " + instanceId
                    + " but VMTerminator is unavailable; instance termination was not scheduled")));
            return;
        }
        if (!terminationRequestedInstances.add(instanceId)) {
            return;
        }
        try {
            LOG.info(new ObjectMessage(Map.of("Message", "[WS] Scheduling VM termination for terminal status from "
                    + instanceId + " job " + status.getJobId())));
            terminator.terminate(instanceId);
        } catch (Exception e) {
            terminationRequestedInstances.remove(instanceId);
            LOG.error(new ObjectMessage(Map.of("Message", "[WS] Failed scheduling VM termination for "
                    + instanceId + ": " + e.getMessage())), e);
        }
    }

    private boolean isTerminalStatus(CloudVmStatus status) {
        return status.getJobStatus() == JobStatus.Completed
                || status.getVmStatus() == VMStatus.terminated
                || status.getVmStatus() == VMStatus.replaced;
    }

    private void onClosed(String instanceId, Session session) {
        SessionContext context = sessions.get(instanceId);
        if (context == null) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring close for non-active session " + instanceId)));
            return;
        }
        if (session == null) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring close without WebSocket identity for " + instanceId)));
            return;
        }
        if (context.session != session) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring stale close from previous session for " + instanceId)));
            return;
        }
        if (!sessions.remove(instanceId, context)) {
            LOG.info(new ObjectMessage(Map.of("Message",
                    "[WS] Ignoring close for replaced session " + instanceId)));
            return;
        }
        terminationRequestedInstances.remove(instanceId);
        context.markClosed();
        fileTransferReady.remove(instanceId);
        agentWsState.put(instanceId, "disconnected");
    }

    /**
     * WebSocket listener. Must be a {@code public static} class: Jetty 12 invokes the callback
     * methods via MethodHandles, which cannot reach a non-public (e.g. private inner) endpoint class.
     */
    public static class Endpoint implements Session.Listener.AutoDemanding {
        private final ControllerInitiatedAgentWsClient client;
        private final String instanceId;
        private final CompletableFuture<AgentWsEnvelope> helloFuture;
        private volatile SessionContext context;
        private Session session;

        private Endpoint(ControllerInitiatedAgentWsClient client, String instanceId,
                         CompletableFuture<AgentWsEnvelope> helloFuture) {
            this.client = client;
            this.instanceId = instanceId;
            this.helloFuture = helloFuture;
        }

        @Override
        public void onWebSocketOpen(Session session) {
            this.session = session;
        }

        @Override
        public void onWebSocketText(String message) {
            client.handleText(instanceId, session, message, helloFuture);
        }

        @Override
        public void onWebSocketClose(int statusCode, String reason) {
            client.onClosed(instanceId, session);
        }

        @Override
        public void onWebSocketError(Throwable cause) {
            LOG.warn(new ObjectMessage(Map.of("Message",
                    "[WS] Controller initiated WS listener error for " + instanceId + ": " + cause.getMessage())));
            client.onClosed(instanceId, session);
        }
    }

    private static class SessionContext {
        private final Session session;
        private final Endpoint endpoint;
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

        private SessionContext(Session session, Endpoint endpoint, CompletableFuture<AgentWsEnvelope> helloFuture) {
            this.session = session;
            this.endpoint = endpoint;
            this.helloFuture = helloFuture;
            this.openedAtMs = System.currentTimeMillis();
        }

        private boolean isOpen() {
            return !closed.get() && session.isOpen();
        }

        private void close() {
            closeGracefully("Closing session");
        }

        private void closeGracefully(String reason) {
            if (closed.compareAndSet(false, true)) {
                try {
                    session.close(org.eclipse.jetty.websocket.api.StatusCode.NORMAL, reason, Callback.NOOP);
                } catch (Exception e) {
                    try { session.disconnect(); } catch (Exception ignored) {}
                }
                helloFuture.completeExceptionally(new IllegalStateException(reason));
                transferCompleteFuture.completeExceptionally(new IllegalStateException(reason));
            }
        }

        private void abort() {
            if (closed.compareAndSet(false, true)) {
                try { session.disconnect(); } catch (Exception ignored) {}
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

    record TransferFile(String fileType, String fileName, byte[] content, boolean defaultDataFile) {
    }

    private static class UnsupportedStartupScriptException extends IOException {
    }
}

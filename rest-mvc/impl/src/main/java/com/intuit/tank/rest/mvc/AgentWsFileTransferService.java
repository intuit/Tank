package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.DataFileRequest;
import com.intuit.tank.vm.settings.TankConfig;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ObjectMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

public class AgentWsFileTransferService {

    private static final Logger LOG = LogManager.getLogger(AgentWsFileTransferService.class);

    private static final String SETTINGS_FILE_NAME = "settings.xml";
    private static final String SUPPORT_ARCHIVE_FILE_TYPE = "support_archive";
    private static final String DEFAULT_SUPPORT_ARCHIVE_NAME = "agent-support-files.zip";
    private static final String SCRIPT_FILE_NAME = "script.xml";
    private static final int CHUNK_ACK_WINDOW = 8;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ConcurrentHashMap<String, byte[]> sharedFileCache = new ConcurrentHashMap<>();

    @FunctionalInterface
    public interface ChunkAckGateFactory {
        CompletableFuture<Void> create(String instanceId, String fileId, int chunkIndex);
    }

    public static class TransferFile {
        private final String fileType;
        private final String fileName;
        private final byte[] content;
        private final String streamUrl;
        private final String authHeader;
        private final long totalBytes;
        private final boolean defaultDataFile;

        public TransferFile(String fileType, String fileName, byte[] content, boolean defaultDataFile) {
            this(fileType, fileName, content, null, null, content != null ? content.length : 0L, defaultDataFile);
        }

        public TransferFile(String fileType, String fileName, byte[] content, String streamUrl,
                            String authHeader, long totalBytes, boolean defaultDataFile) {
            this.fileType = fileType;
            this.fileName = fileName;
            this.content = content;
            this.streamUrl = streamUrl;
            this.authHeader = authHeader;
            this.totalBytes = totalBytes;
            this.defaultDataFile = defaultDataFile;
        }

        public String getFileType() {
            return fileType;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getContent() {
            return content;
        }

        public String getStreamUrl() {
            return streamUrl;
        }

        public String getAuthHeader() {
            return authHeader;
        }

        public long getTotalBytes() {
            return totalBytes;
        }

        public boolean isDefaultDataFile() {
            return defaultDataFile;
        }
    }

    public List<TransferFile> buildTransferFiles(AgentTestStartData startData,
                                                 String authorizationHeader)
            throws IOException, InterruptedException {
        List<TransferFile> files = new ArrayList<>();
        String jobId = startData != null && startData.getJobId() != null ? startData.getJobId() : "unknown";

        // Settings delivered over WS (agent skips HTTP settings download in WS mode)
        files.add(new TransferFile("settings", SETTINGS_FILE_NAME,
                getCachedSharedFile("settings:" + jobId, this::loadSettingsFile), false));

        // Support files (harness JAR) always downloaded via HTTP by AgentStartup — not sent over WS

        if (startData != null && startData.getScriptUrl() != null) {
            files.add(new TransferFile("script", SCRIPT_FILE_NAME,
                    getCachedSharedFile("script:" + jobId,
                            () -> readBytesFromUrl(startData.getScriptUrl(), authorizationHeader)), false));
        }

        if (startData != null && startData.getDataFiles() != null) {
            for (DataFileRequest dataFileRequest : startData.getDataFiles()) {
                files.add(new TransferFile(
                        "data",
                        dataFileRequest.getFileName(),
                        null,
                        dataFileRequest.getFileUrl(),
                        authorizationHeader,
                        readContentLength(dataFileRequest.getFileUrl(), authorizationHeader),
                        dataFileRequest.isDefaultDataFile()
                ));
            }
        }

        return files;
    }

    public int sendFiles(WebSocketSession session,
                         String instanceId,
                         String jobId,
                         List<TransferFile> files,
                         int configuredChunkBytes) throws IOException {
        try {
            return sendFiles(session, instanceId, jobId, files, configuredChunkBytes, null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted during WS file transfer", e);
        }
    }

    public int sendFiles(WebSocketSession session,
                         String instanceId,
                         String jobId,
                         List<TransferFile> files,
                         int configuredChunkBytes,
                         ChunkAckGateFactory chunkAckGateFactory)
            throws IOException, InterruptedException {
        int chunkBytes = configuredChunkBytes > 0 ? configuredChunkBytes : 49152;
        for (TransferFile file : files) {
            sendFile(session, instanceId, jobId, file, chunkBytes, chunkAckGateFactory);
        }
        return files.size();
    }

    private void sendFile(WebSocketSession session,
                          String instanceId,
                          String jobId,
                          TransferFile file,
                          int chunkBytes,
                          ChunkAckGateFactory chunkAckGateFactory)
            throws IOException, InterruptedException {
        byte[] content = file.getContent();
        long totalBytes = content != null ? content.length : file.getTotalBytes();
        int totalChunks = totalBytes >= 0 ? Math.max(1, (int) Math.ceil((double) totalBytes / chunkBytes)) : -1;
        String fileId = UUID.randomUUID().toString();

        AgentWsEnvelope offer = AgentWsEnvelope.fileOffer(
                instanceId,
                jobId,
                fileId,
                file.getFileType(),
                file.getFileName(),
                totalBytes,
                totalChunks,
                chunkBytes,
                file.isDefaultDataFile());
        sendEnvelope(session, offer);

        if (content != null) {
            sendByteArrayFile(session, instanceId, jobId, fileId, file, content, chunkBytes, chunkAckGateFactory);
        } else {
            sendStreamingFile(session, instanceId, jobId, fileId, file, chunkBytes, chunkAckGateFactory);
        }

        LOG.info(new ObjectMessage(Map.of("Message",
                "Sent WS file transfer payload " + file.getFileName() + " chunks=" + totalChunks)));
    }

    private void sendByteArrayFile(WebSocketSession session,
                                   String instanceId,
                                   String jobId,
                                   String fileId,
                                   TransferFile file,
                                   byte[] content,
                                   int chunkBytes,
                                   ChunkAckGateFactory chunkAckGateFactory)
            throws IOException, InterruptedException {
        if (content.length == 0) {
            sendChunk(session, instanceId, jobId, fileId, 0, new byte[0], 0, chunkAckGateFactory);
            return;
        }
        int chunkIndex = 0;
        for (int offset = 0; offset < content.length; offset += chunkBytes) {
            int len = Math.min(chunkBytes, content.length - offset);
            sendChunk(session, instanceId, jobId, fileId, chunkIndex,
                    Arrays.copyOfRange(content, offset, offset + len), len, chunkAckGateFactory);
            chunkIndex++;
        }
    }

    private void sendStreamingFile(WebSocketSession session,
                                   String instanceId,
                                   String jobId,
                                   String fileId,
                                   TransferFile file,
                                   int chunkBytes,
                                   ChunkAckGateFactory chunkAckGateFactory)
            throws IOException, InterruptedException {
        int chunkIndex = 0;
        try (InputStream in = openStream(file)) {
            byte[] buffer = new byte[chunkBytes];
            int len;
            while ((len = in.read(buffer)) != -1) {
                sendChunk(session, instanceId, jobId, fileId, chunkIndex,
                        Arrays.copyOf(buffer, len), len, chunkAckGateFactory);
                chunkIndex++;
            }
        }
        if (chunkIndex == 0 || file.getTotalBytes() < 0) {
            sendChunk(session, instanceId, jobId, fileId, chunkIndex, new byte[0], 0, chunkAckGateFactory);
        }
    }

    private void sendChunk(WebSocketSession session,
                           String instanceId,
                           String jobId,
                           String fileId,
                           int chunkIndex,
                           byte[] bytes,
                           int len,
                           ChunkAckGateFactory chunkAckGateFactory)
            throws IOException, InterruptedException {
        CompletableFuture<Void> gate = null;
        if (chunkAckGateFactory != null && (chunkIndex + 1) % CHUNK_ACK_WINDOW == 0) {
            gate = chunkAckGateFactory.create(instanceId, fileId, chunkIndex);
        }
        String base64 = len == 0 ? "" : Base64.getEncoder().encodeToString(bytes);
        AgentWsEnvelope chunk = AgentWsEnvelope.fileChunk(instanceId, jobId, fileId, chunkIndex, base64);
        sendEnvelope(session, chunk);
        if (gate != null) {
            try {
                gate.get(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } catch (Exception e) {
                throw new IOException("Timed out waiting for WS file chunk ack for " + instanceId, e);
            }
        }
    }

    private void sendEnvelope(WebSocketSession session, AgentWsEnvelope envelope) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(envelope.toJson()));
        }
    }

    public void clearJobCache(String jobId) {
        if (jobId != null) {
            sharedFileCache.remove("settings:" + jobId);
            sharedFileCache.remove("script:" + jobId);
        }
    }

    private byte[] loadSettingsFile() throws IOException {
        File configFile = new TankConfig().getSourceConfigFile();
        File agentConfigFile = new File(configFile.getParentFile(), "agent-settings.xml");
        if (agentConfigFile.exists() && agentConfigFile.isFile()) {
            configFile = agentConfigFile;
        }
        return FileUtils.readFileToByteArray(configFile);
    }

    private byte[] getCachedSharedFile(String key, SharedFileLoader loader) throws IOException, InterruptedException {
        byte[] cached = sharedFileCache.get(key);
        if (cached != null) {
            return cached;
        }
        synchronized (sharedFileCache) {
            cached = sharedFileCache.get(key);
            if (cached == null) {
                cached = loader.load();
                sharedFileCache.put(key, cached);
            }
            return cached;
        }
    }

    private byte[] readBytesFromUrl(String url, String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpRequest request = requestBuilder.build();
        HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Failed reading transfer source " + url + " status=" + response.statusCode());
        }

        byte[] body = response.body();
        if ("gzip".equalsIgnoreCase(response.headers().firstValue("Content-Encoding").orElse(""))) {
            return ungzip(body);
        }
        return body;
    }

    private long readContentLength(String url, String authorizationHeader) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(url)).method("HEAD", HttpRequest.BodyPublishers.noBody());
        if (authorizationHeader != null && !authorizationHeader.isBlank()) {
            requestBuilder.header("Authorization", authorizationHeader);
        }
        HttpResponse<Void> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.discarding());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            return -1L;
        }
        if ("gzip".equalsIgnoreCase(response.headers().firstValue("Content-Encoding").orElse(""))) {
            return -1L;
        }
        return response.headers()
                .firstValue("Content-Length")
                .map(value -> {
                    try {
                        return Long.parseLong(value);
                    } catch (NumberFormatException e) {
                        return -1L;
                    }
                })
                .orElse(-1L);
    }

    private InputStream openStream(TransferFile file) throws IOException, InterruptedException {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder().uri(URI.create(file.getStreamUrl()));
        if (file.getAuthHeader() != null && !file.getAuthHeader().isBlank()) {
            requestBuilder.header("Authorization", file.getAuthHeader());
        }
        HttpResponse<InputStream> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Failed streaming transfer source " + file.getStreamUrl() + " status=" + response.statusCode());
        }
        InputStream body = response.body();
        if ("gzip".equalsIgnoreCase(response.headers().firstValue("Content-Encoding").orElse(""))) {
            return new GZIPInputStream(body);
        }
        return body;
    }

    private byte[] ungzip(byte[] compressed) throws IOException {
        try (InputStream in = new GZIPInputStream(new ByteArrayInputStream(compressed));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            return out.toByteArray();
        }
    }

    @FunctionalInterface
    private interface SharedFileLoader {
        byte[] load() throws IOException, InterruptedException;
    }
}

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
import java.util.zip.GZIPInputStream;

public class AgentWsFileTransferService {

    private static final Logger LOG = LogManager.getLogger(AgentWsFileTransferService.class);

    private static final String SETTINGS_FILE_NAME = "settings.xml";
    private static final String SUPPORT_ARCHIVE_FILE_TYPE = "support_archive";
    private static final String DEFAULT_SUPPORT_ARCHIVE_NAME = "agent-support-files.zip";
    private static final String SCRIPT_FILE_NAME = "script.xml";

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public static class TransferFile {
        private final String fileType;
        private final String fileName;
        private final byte[] content;
        private final boolean defaultDataFile;

        public TransferFile(String fileType, String fileName, byte[] content, boolean defaultDataFile) {
            this.fileType = fileType;
            this.fileName = fileName;
            this.content = content;
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

        public boolean isDefaultDataFile() {
            return defaultDataFile;
        }
    }

    public List<TransferFile> buildTransferFiles(AgentTestStartData startData,
                                                 String authorizationHeader,
                                                 File supportFilesArchive)
            throws IOException, InterruptedException {
        List<TransferFile> files = new ArrayList<>();

        files.add(new TransferFile("settings", SETTINGS_FILE_NAME, loadSettingsFile(), false));

        if (supportFilesArchive == null || !supportFilesArchive.exists() || !supportFilesArchive.isFile()) {
            throw new IOException("Missing support files archive for WS transfer");
        }
        String archiveName = supportFilesArchive.getName();
        if (archiveName == null || archiveName.isBlank()) {
            archiveName = DEFAULT_SUPPORT_ARCHIVE_NAME;
        }
        files.add(new TransferFile(
                SUPPORT_ARCHIVE_FILE_TYPE,
                archiveName,
                FileUtils.readFileToByteArray(supportFilesArchive),
                false));

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
        int chunkBytes = configuredChunkBytes > 0 ? configuredChunkBytes : 49152;
        for (TransferFile file : files) {
            sendFile(session, instanceId, jobId, file, chunkBytes);
        }
        return files.size();
    }

    private void sendFile(WebSocketSession session,
                          String instanceId,
                          String jobId,
                          TransferFile file,
                          int chunkBytes) throws IOException {
        byte[] content = file.getContent() != null ? file.getContent() : new byte[0];
        int totalChunks = Math.max(1, (int) Math.ceil((double) content.length / chunkBytes));
        String fileId = UUID.randomUUID().toString();

        AgentWsEnvelope offer = AgentWsEnvelope.fileOffer(
                instanceId,
                jobId,
                fileId,
                file.getFileType(),
                file.getFileName(),
                content.length,
                totalChunks,
                file.isDefaultDataFile());
        sendEnvelope(session, offer);

        if (content.length == 0) {
            AgentWsEnvelope chunk = AgentWsEnvelope.fileChunk(instanceId, jobId, fileId, 0, "");
            sendEnvelope(session, chunk);
            return;
        }

        int chunkIndex = 0;
        for (int offset = 0; offset < content.length; offset += chunkBytes) {
            int len = Math.min(chunkBytes, content.length - offset);
            String base64 = Base64.getEncoder().encodeToString(Arrays.copyOfRange(content, offset, offset + len));
            AgentWsEnvelope chunk = AgentWsEnvelope.fileChunk(instanceId, jobId, fileId, chunkIndex, base64);
            sendEnvelope(session, chunk);
            chunkIndex++;
        }

        LOG.info(new ObjectMessage(Map.of("Message",
                "Sent WS file transfer payload " + file.getFileName() + " chunks=" + totalChunks)));
    }

    private void sendEnvelope(WebSocketSession session, AgentWsEnvelope envelope) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(envelope.toJson()));
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

    private byte[] ungzip(byte[] compressed) throws IOException {
        try (InputStream in = new GZIPInputStream(new ByteArrayInputStream(compressed));
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            return out.toByteArray();
        }
    }
}

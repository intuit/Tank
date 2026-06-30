package com.intuit.tank.vm.agent.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentWsEnvelope {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static final int PROTOCOL_VERSION = 1;
    private static final int BINARY_FILE_CHUNK_MAGIC = 0x54574331;

    public enum Type {
        hello, command, ack, ping, pong, close,
        job_config, file_offer, file_chunk, file_ack,
        status_update
    }

    public enum AckStatus {
        ok, duplicate, failed, unsupported,
        chunk_received, complete, all_files_complete, resume
    }

    @JsonProperty("type")
    private Type type;

    @JsonProperty("instanceId")
    private String instanceId;

    @JsonProperty("sentAtMs")
    private long sentAtMs;

    @JsonProperty("protocolVersion")
    private int protocolVersion = PROTOCOL_VERSION;

    // hello fields
    @JsonProperty("jobId")
    private String jobId;

    @JsonProperty("agentSessionId")
    private String agentSessionId;

    @JsonProperty("capacity")
    private Integer capacity;

    @JsonProperty("needsBootstrap")
    private Boolean needsBootstrap;

    // command fields
    @JsonProperty("commandId")
    private String commandId;

    @JsonProperty("command")
    private String command;

    // ack fields
    @JsonProperty("ackForType")
    private String ackForType;

    @JsonProperty("ackForId")
    private String ackForId;

    @JsonProperty("status")
    private AckStatus status;

    @JsonProperty("error")
    private String error;

    // ping/pong fields
    @JsonProperty("pingId")
    private String pingId;

    @JsonProperty("lastAppliedCommandId")
    private String lastAppliedCommandId;

    // job_config fields
    @JsonProperty("jobConfig")
    private AgentTestStartData jobConfig;

    @JsonProperty("expectedFiles")
    private Integer expectedFiles;

    // file transfer fields
    @JsonProperty("fileId")
    private String fileId;

    @JsonProperty("fileType")
    private String fileType;

    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("totalBytes")
    private Long totalBytes;

    @JsonProperty("totalChunks")
    private Integer totalChunks;

    @JsonProperty("chunkBytes")
    private Integer chunkBytes;

    // How often the receiver should send a chunk_received ack: once every N chunks (on the
    // boundary where (chunkIndex+1) % ackEvery == 0). Lets the sender's credit window advance
    // without an ack per chunk. Null/absent => ack every chunk (legacy behavior).
    @JsonProperty("ackEvery")
    private Integer ackEvery;

    @JsonProperty("isDefaultDataFile")
    private Boolean defaultDataFile;

    @JsonProperty("chunkIndex")
    private Integer chunkIndex;

    @JsonProperty("chunkData")
    private String chunkData;

    @JsonProperty("resumeOffset")
    private Long resumeOffset;

    // close fields
    @JsonProperty("reasonCode")
    private String reasonCode;

    @JsonProperty("reason")
    private String reason;

    // status update fields
    @JsonProperty("instanceStatus")
    private CloudVmStatus instanceStatus;

    public AgentWsEnvelope() {}

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public String getInstanceId() { return instanceId; }
    public void setInstanceId(String instanceId) { this.instanceId = instanceId; }

    public long getSentAtMs() { return sentAtMs; }
    public void setSentAtMs(long sentAtMs) { this.sentAtMs = sentAtMs; }

    public int getProtocolVersion() { return protocolVersion; }
    public void setProtocolVersion(int protocolVersion) { this.protocolVersion = protocolVersion; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getAgentSessionId() { return agentSessionId; }
    public void setAgentSessionId(String agentSessionId) { this.agentSessionId = agentSessionId; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Boolean getNeedsBootstrap() { return needsBootstrap; }
    public void setNeedsBootstrap(Boolean needsBootstrap) { this.needsBootstrap = needsBootstrap; }

    public String getCommandId() { return commandId; }
    public void setCommandId(String commandId) { this.commandId = commandId; }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public String getAckForType() { return ackForType; }
    public void setAckForType(String ackForType) { this.ackForType = ackForType; }

    public String getAckForId() { return ackForId; }
    public void setAckForId(String ackForId) { this.ackForId = ackForId; }

    public AckStatus getStatus() { return status; }
    public void setStatus(AckStatus status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getPingId() { return pingId; }
    public void setPingId(String pingId) { this.pingId = pingId; }

    public String getLastAppliedCommandId() { return lastAppliedCommandId; }
    public void setLastAppliedCommandId(String lastAppliedCommandId) { this.lastAppliedCommandId = lastAppliedCommandId; }

    public AgentTestStartData getJobConfig() { return jobConfig; }
    public void setJobConfig(AgentTestStartData jobConfig) { this.jobConfig = jobConfig; }

    public Integer getExpectedFiles() { return expectedFiles; }
    public void setExpectedFiles(Integer expectedFiles) { this.expectedFiles = expectedFiles; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public Long getTotalBytes() { return totalBytes; }
    public void setTotalBytes(Long totalBytes) { this.totalBytes = totalBytes; }

    public Integer getTotalChunks() { return totalChunks; }
    public void setTotalChunks(Integer totalChunks) { this.totalChunks = totalChunks; }

    public Integer getChunkBytes() { return chunkBytes; }
    public void setChunkBytes(Integer chunkBytes) { this.chunkBytes = chunkBytes; }

    public Integer getAckEvery() { return ackEvery; }
    public void setAckEvery(Integer ackEvery) { this.ackEvery = ackEvery; }

    public Boolean getDefaultDataFile() { return defaultDataFile; }
    public void setDefaultDataFile(Boolean defaultDataFile) { this.defaultDataFile = defaultDataFile; }

    public Integer getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(Integer chunkIndex) { this.chunkIndex = chunkIndex; }

    public String getChunkData() { return chunkData; }
    public void setChunkData(String chunkData) { this.chunkData = chunkData; }

    public Long getResumeOffset() { return resumeOffset; }
    public void setResumeOffset(Long resumeOffset) { this.resumeOffset = resumeOffset; }

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public CloudVmStatus getInstanceStatus() { return instanceStatus; }
    public void setInstanceStatus(CloudVmStatus instanceStatus) { this.instanceStatus = instanceStatus; }

    public String toJson() throws IOException {
        return MAPPER.writeValueAsString(this);
    }

    public static AgentWsEnvelope fromJson(String json) throws IOException {
        return MAPPER.readValue(json, AgentWsEnvelope.class);
    }

    public static ByteBuffer binaryFileChunk(String fileId, int chunkIndex, byte[] bytes, int offset, int len)
            throws IOException {
        if (fileId == null || fileId.isBlank()) {
            throw new IOException("fileId is required for binary file chunk");
        }
        if (bytes == null) {
            bytes = new byte[0];
        }
        if (offset < 0 || len < 0 || offset + len > bytes.length) {
            throw new IOException("Invalid binary file chunk range");
        }
        byte[] fileIdBytes = fileId.getBytes(StandardCharsets.UTF_8);
        if (fileIdBytes.length > 0xFFFF) {
            throw new IOException("fileId too long for binary file chunk");
        }
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES + Integer.BYTES + Short.BYTES + fileIdBytes.length + len);
        buffer.putInt(BINARY_FILE_CHUNK_MAGIC);
        buffer.putInt(chunkIndex);
        buffer.putShort((short) fileIdBytes.length);
        buffer.put(fileIdBytes);
        buffer.put(bytes, offset, len);
        buffer.flip();
        return buffer;
    }

    public static BinaryFileChunk fromBinaryFileChunk(ByteBuffer message) throws IOException {
        ByteBuffer buffer = message.slice();
        if (buffer.remaining() < Integer.BYTES + Integer.BYTES + Short.BYTES) {
            throw new IOException("Invalid binary file chunk header");
        }
        int magic = buffer.getInt();
        if (magic != BINARY_FILE_CHUNK_MAGIC) {
            throw new IOException("Unsupported binary file chunk frame");
        }
        int chunkIndex = buffer.getInt();
        int fileIdLength = Short.toUnsignedInt(buffer.getShort());
        if (fileIdLength <= 0 || fileIdLength > buffer.remaining()) {
            throw new IOException("Invalid binary file chunk fileId length");
        }
        byte[] fileIdBytes = new byte[fileIdLength];
        buffer.get(fileIdBytes);
        byte[] payload = new byte[buffer.remaining()];
        buffer.get(payload);
        return new BinaryFileChunk(new String(fileIdBytes, StandardCharsets.UTF_8), chunkIndex, payload);
    }

    public record BinaryFileChunk(String fileId, int chunkIndex, byte[] payload) {
    }

    // Factory methods for common frame types

    public static AgentWsEnvelope hello(String instanceId, String jobId, String agentSessionId, String lastAppliedCommandId) {
        return hello(instanceId, jobId, agentSessionId, lastAppliedCommandId, null);
    }

    public static AgentWsEnvelope hello(String instanceId, String jobId, String agentSessionId,
                                        String lastAppliedCommandId, Integer capacity) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.hello);
        env.setInstanceId(instanceId);
        env.setJobId(jobId);
        env.setAgentSessionId(agentSessionId);
        env.setLastAppliedCommandId(lastAppliedCommandId);
        env.setCapacity(capacity);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope hello(String instanceId, String jobId, String agentSessionId,
                                        String lastAppliedCommandId, Integer capacity, Boolean needsBootstrap) {
        AgentWsEnvelope env = hello(instanceId, jobId, agentSessionId, lastAppliedCommandId, capacity);
        env.setNeedsBootstrap(needsBootstrap);
        return env;
    }

    public static AgentWsEnvelope command(String commandId, String instanceId, String jobId, String command) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.command);
        env.setCommandId(commandId);
        env.setInstanceId(instanceId);
        env.setJobId(jobId);
        env.setCommand(command);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope ack(String instanceId, String ackForType, String ackForId, AckStatus status) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.ack);
        env.setInstanceId(instanceId);
        env.setAckForType(ackForType);
        env.setAckForId(ackForId);
        env.setStatus(status);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope ping(String pingId) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.ping);
        env.setPingId(pingId);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope pong(String instanceId, String agentSessionId, String pingId, String lastAppliedCommandId) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.pong);
        env.setInstanceId(instanceId);
        env.setAgentSessionId(agentSessionId);
        env.setPingId(pingId);
        env.setLastAppliedCommandId(lastAppliedCommandId);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope close(String instanceId, String reasonCode, String reason) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.close);
        env.setInstanceId(instanceId);
        env.setReasonCode(reasonCode);
        env.setReason(reason);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope jobConfig(String instanceId, String jobId, AgentTestStartData jobConfig, Integer expectedFiles) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.job_config);
        env.setInstanceId(instanceId);
        env.setJobId(jobId);
        env.setJobConfig(jobConfig);
        env.setExpectedFiles(expectedFiles);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope fileOffer(String instanceId, String jobId, String fileId, String fileType,
                                            String fileName, long totalBytes, int totalChunks, int chunkBytes,
                                            Boolean defaultDataFile) {
        return fileOffer(instanceId, jobId, fileId, fileType, fileName, totalBytes, totalChunks,
                chunkBytes, defaultDataFile, null);
    }

    public static AgentWsEnvelope fileOffer(String instanceId, String jobId, String fileId, String fileType,
                                            String fileName, long totalBytes, int totalChunks, int chunkBytes,
                                            Boolean defaultDataFile, Integer ackEvery) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.file_offer);
        env.setInstanceId(instanceId);
        env.setJobId(jobId);
        env.setFileId(fileId);
        env.setFileType(fileType);
        env.setFileName(fileName);
        env.setChunkBytes(chunkBytes);
        env.setTotalBytes(totalBytes);
        env.setTotalChunks(totalChunks);
        env.setDefaultDataFile(defaultDataFile);
        env.setAckEvery(ackEvery);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope fileChunk(String instanceId, String jobId, String fileId, int chunkIndex, String chunkData) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.file_chunk);
        env.setInstanceId(instanceId);
        env.setJobId(jobId);
        env.setFileId(fileId);
        env.setChunkIndex(chunkIndex);
        env.setChunkData(chunkData);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope fileAck(String instanceId, String jobId, String fileId, Integer chunkIndex,
                                          AckStatus status, String error) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.file_ack);
        env.setInstanceId(instanceId);
        env.setJobId(jobId);
        env.setFileId(fileId);
        env.setChunkIndex(chunkIndex);
        env.setStatus(status);
        env.setError(error);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }

    public static AgentWsEnvelope statusUpdate(String instanceId, String jobId, CloudVmStatus instanceStatus) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.status_update);
        env.setInstanceId(instanceId);
        env.setJobId(jobId);
        env.setInstanceStatus(instanceStatus);
        env.setSentAtMs(System.currentTimeMillis());
        return env;
    }
}

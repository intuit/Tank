package com.intuit.tank.vm.agent.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentWsEnvelope {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final int PROTOCOL_VERSION = 1;

    public enum Type {
        hello, command, ack, ping, pong, close
    }

    public enum AckStatus {
        ok, duplicate, failed, unsupported
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

    // close fields
    @JsonProperty("reasonCode")
    private String reasonCode;

    @JsonProperty("reason")
    private String reason;

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

    public String getReasonCode() { return reasonCode; }
    public void setReasonCode(String reasonCode) { this.reasonCode = reasonCode; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String toJson() throws IOException {
        return MAPPER.writeValueAsString(this);
    }

    public static AgentWsEnvelope fromJson(String json) throws IOException {
        return MAPPER.readValue(json, AgentWsEnvelope.class);
    }

    // Factory methods for common frame types

    public static AgentWsEnvelope hello(String instanceId, String jobId, String agentSessionId, String lastAppliedCommandId) {
        AgentWsEnvelope env = new AgentWsEnvelope();
        env.setType(Type.hello);
        env.setInstanceId(instanceId);
        env.setJobId(jobId);
        env.setAgentSessionId(agentSessionId);
        env.setLastAppliedCommandId(lastAppliedCommandId);
        env.setSentAtMs(System.currentTimeMillis());
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
}

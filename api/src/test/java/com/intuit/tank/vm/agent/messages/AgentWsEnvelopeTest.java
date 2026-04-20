package com.intuit.tank.vm.agent.messages;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.Type;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AgentWsEnvelopeTest {

    @Test
    public void testHelloFactory() throws IOException {
        AgentWsEnvelope env = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", "cmd-99");

        assertEquals(Type.hello, env.getType());
        assertEquals("i-123", env.getInstanceId());
        assertEquals("job-1", env.getJobId());
        assertEquals("sess-1", env.getAgentSessionId());
        assertEquals("cmd-99", env.getLastAppliedCommandId());
        assertEquals(AgentWsEnvelope.PROTOCOL_VERSION, env.getProtocolVersion());
        assertTrue(env.getSentAtMs() > 0);
    }

    @Test
    public void testCommandFactory() throws IOException {
        AgentWsEnvelope env = AgentWsEnvelope.command("cmd-1", "i-123", "job-1", "start");

        assertEquals(Type.command, env.getType());
        assertEquals("cmd-1", env.getCommandId());
        assertEquals("i-123", env.getInstanceId());
        assertEquals("job-1", env.getJobId());
        assertEquals("start", env.getCommand());
    }

    @Test
    public void testAckFactory() {
        AgentWsEnvelope env = AgentWsEnvelope.ack("i-123", "command", "cmd-1", AckStatus.ok);

        assertEquals(Type.ack, env.getType());
        assertEquals("i-123", env.getInstanceId());
        assertEquals("command", env.getAckForType());
        assertEquals("cmd-1", env.getAckForId());
        assertEquals(AckStatus.ok, env.getStatus());
    }

    @Test
    public void testPingFactory() {
        AgentWsEnvelope env = AgentWsEnvelope.ping("ping-1");

        assertEquals(Type.ping, env.getType());
        assertEquals("ping-1", env.getPingId());
    }

    @Test
    public void testPongFactory() {
        AgentWsEnvelope env = AgentWsEnvelope.pong("i-123", "sess-1", "ping-1", "cmd-5");

        assertEquals(Type.pong, env.getType());
        assertEquals("i-123", env.getInstanceId());
        assertEquals("sess-1", env.getAgentSessionId());
        assertEquals("ping-1", env.getPingId());
        assertEquals("cmd-5", env.getLastAppliedCommandId());
    }

    @Test
    public void testCloseFactory() {
        AgentWsEnvelope env = AgentWsEnvelope.close("i-123", "shutdown", "Agent shutting down");

        assertEquals(Type.close, env.getType());
        assertEquals("i-123", env.getInstanceId());
        assertEquals("shutdown", env.getReasonCode());
        assertEquals("Agent shutting down", env.getReason());
    }

    @Test
    public void testJsonRoundTrip() throws IOException {
        AgentWsEnvelope original = AgentWsEnvelope.command("cmd-1", "i-123", "job-1", "stop");
        String json = original.toJson();

        assertNotNull(json);
        assertTrue(json.contains("\"type\":\"command\""));
        assertTrue(json.contains("\"commandId\":\"cmd-1\""));
        assertTrue(json.contains("\"command\":\"stop\""));

        AgentWsEnvelope parsed = AgentWsEnvelope.fromJson(json);
        assertEquals(Type.command, parsed.getType());
        assertEquals("cmd-1", parsed.getCommandId());
        assertEquals("i-123", parsed.getInstanceId());
        assertEquals("job-1", parsed.getJobId());
        assertEquals("stop", parsed.getCommand());
    }

    @Test
    public void testJsonNullFieldsOmitted() throws IOException {
        AgentWsEnvelope env = AgentWsEnvelope.ping("ping-1");
        String json = env.toJson();

        // Null fields should not be present
        assertFalse(json.contains("\"instanceId\""));
        assertFalse(json.contains("\"jobId\""));
        assertFalse(json.contains("\"commandId\""));
    }

    @Test
    public void testFromJsonUnknownFieldsIgnored() throws IOException {
        String json = "{\"type\":\"hello\",\"instanceId\":\"i-1\",\"jobId\":\"j-1\",\"agentSessionId\":\"s-1\",\"unknownField\":\"value\",\"sentAtMs\":1000,\"protocolVersion\":1}";
        AgentWsEnvelope env = AgentWsEnvelope.fromJson(json);

        assertEquals(Type.hello, env.getType());
        assertEquals("i-1", env.getInstanceId());
        assertEquals("j-1", env.getJobId());
    }

    @Test
    public void testFromJsonInvalidType() {
        String json = "{\"type\":\"bogus\",\"instanceId\":\"i-1\"}";
        assertThrows(IOException.class, () -> AgentWsEnvelope.fromJson(json));
    }

    @Test
    public void testFromJsonMalformed() {
        assertThrows(IOException.class, () -> AgentWsEnvelope.fromJson("not json"));
    }

    @Test
    public void testFromJsonEmptyObject() throws IOException {
        AgentWsEnvelope env = AgentWsEnvelope.fromJson("{}");
        assertNull(env.getType());
        assertNull(env.getInstanceId());
    }

    @Test
    public void testAckStatusValues() {
        assertEquals(4, AckStatus.values().length);
        assertNotNull(AckStatus.valueOf("ok"));
        assertNotNull(AckStatus.valueOf("duplicate"));
        assertNotNull(AckStatus.valueOf("failed"));
        assertNotNull(AckStatus.valueOf("unsupported"));
    }

    @Test
    public void testTypeValues() {
        assertEquals(6, Type.values().length);
        assertNotNull(Type.valueOf("hello"));
        assertNotNull(Type.valueOf("command"));
        assertNotNull(Type.valueOf("ack"));
        assertNotNull(Type.valueOf("ping"));
        assertNotNull(Type.valueOf("pong"));
        assertNotNull(Type.valueOf("close"));
    }

    @Test
    public void testAckRoundTrip() throws IOException {
        AgentWsEnvelope ack = AgentWsEnvelope.ack("i-123", "command", "cmd-1", AckStatus.duplicate);
        ack.setError("already applied");
        ack.setAgentSessionId("sess-1");

        String json = ack.toJson();
        AgentWsEnvelope parsed = AgentWsEnvelope.fromJson(json);

        assertEquals(Type.ack, parsed.getType());
        assertEquals(AckStatus.duplicate, parsed.getStatus());
        assertEquals("already applied", parsed.getError());
        assertEquals("sess-1", parsed.getAgentSessionId());
    }
}

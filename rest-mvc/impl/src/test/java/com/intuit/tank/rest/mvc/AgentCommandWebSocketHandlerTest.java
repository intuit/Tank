package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.agent.messages.AgentWsEnvelope;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.AckStatus;
import com.intuit.tank.vm.agent.messages.AgentWsEnvelope.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AgentCommandWebSocketHandlerTest {

    private AgentCommandWebSocketHandler handler;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        handler = new AgentCommandWebSocketHandler();
        session = mock(WebSocketSession.class);
        when(session.getId()).thenReturn("test-session-1");
        when(session.isOpen()).thenReturn(true);
    }

    @Test
    void testHelloRegistersSession() throws Exception {
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));

        assertTrue(handler.hasSession("i-123"));

        // Verify ack was sent
        verify(session).sendMessage(argThat(msg -> {
            String payload = ((TextMessage) msg).getPayload();
            return payload.contains("\"type\":\"ack\"") && payload.contains("\"status\":\"ok\"");
        }));
    }

    @Test
    void testHelloReRegisterReplacesSession() throws Exception {
        AgentWsEnvelope hello1 = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello1.toJson()));

        WebSocketSession session2 = mock(WebSocketSession.class);
        when(session2.getId()).thenReturn("test-session-2");
        when(session2.isOpen()).thenReturn(true);

        AgentWsEnvelope hello2 = AgentWsEnvelope.hello("i-123", "job-1", "sess-2", null);
        handler.handleTextMessage(session2, new TextMessage(hello2.toJson()));

        assertTrue(handler.hasSession("i-123"));
        // Old session should have been closed
        verify(session).close(CloseStatus.GOING_AWAY);
    }

    @Test
    void testHelloMissingFieldsClosesSession() throws Exception {
        // Missing jobId and agentSessionId
        AgentWsEnvelope badHello = new AgentWsEnvelope();
        badHello.setType(Type.hello);
        badHello.setInstanceId("i-123");
        badHello.setSentAtMs(System.currentTimeMillis());

        handler.handleTextMessage(session, new TextMessage(badHello.toJson()));

        assertFalse(handler.hasSession("i-123"));
        verify(session).close(CloseStatus.BAD_DATA);
    }

    @Test
    void testInvalidJsonClosesSession() throws Exception {
        handler.handleTextMessage(session, new TextMessage("not valid json"));

        verify(session).close(CloseStatus.BAD_DATA);
    }

    @Test
    void testMissingTypeClosesSession() throws Exception {
        handler.handleTextMessage(session, new TextMessage("{\"instanceId\":\"i-123\"}"));

        verify(session).close(CloseStatus.BAD_DATA);
    }

    @Test
    void testPongUpdatesLastSeen() throws Exception {
        // Register first
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));

        // Send pong
        AgentWsEnvelope pong = AgentWsEnvelope.pong("i-123", "sess-1", "ping-1", "cmd-5");
        handler.handleTextMessage(session, new TextMessage(pong.toJson()));

        // Session should still be active
        assertTrue(handler.hasSession("i-123"));
    }

    @Test
    void testCloseUnregistersSession() throws Exception {
        // Register
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));
        assertTrue(handler.hasSession("i-123"));

        // Close
        AgentWsEnvelope close = AgentWsEnvelope.close("i-123", "shutdown", "done");
        handler.handleTextMessage(session, new TextMessage(close.toJson()));

        assertFalse(handler.hasSession("i-123"));
    }

    @Test
    void testAfterConnectionClosedCleansUp() throws Exception {
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));
        assertTrue(handler.hasSession("i-123"));

        handler.afterConnectionClosed(session, CloseStatus.NORMAL);

        assertFalse(handler.hasSession("i-123"));
    }

    @Test
    void testHasSessionReturnsFalseForUnknown() {
        assertFalse(handler.hasSession("i-unknown"));
    }

    @Test
    void testHasSessionReturnsFalseForClosedSession() throws Exception {
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));

        when(session.isOpen()).thenReturn(false);
        assertFalse(handler.hasSession("i-123"));
    }

    @Test
    void testSendCommandReturnsFalseWhenNoSession() {
        boolean result = handler.sendCommand("i-unknown", "job-1", "start", 1000);
        assertFalse(result);
    }

    @Test
    void testSendCommandReturnsFalseWhenSessionClosed() throws Exception {
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));

        when(session.isOpen()).thenReturn(false);

        boolean result = handler.sendCommand("i-123", "job-1", "start", 1000);
        assertFalse(result);
    }

    @Test
    void testSendCommandTimesOutWithNoAck() throws Exception {
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));

        // Send command with very short timeout - no ack will come
        boolean result = handler.sendCommand("i-123", "job-1", "start", 50);
        assertFalse(result);
    }

    @Test
    void testSendCommandSucceedsWithAck() throws Exception {
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));

        // Simulate the ack arriving when command is sent
        doAnswer(invocation -> {
            TextMessage msg = invocation.getArgument(0);
            AgentWsEnvelope cmd = AgentWsEnvelope.fromJson(msg.getPayload());
            if (cmd.getType() == Type.command) {
                // Simulate agent ack
                AgentWsEnvelope ack = AgentWsEnvelope.ack("i-123", "command", cmd.getCommandId(), AckStatus.ok);
                handler.handleTextMessage(session, new TextMessage(ack.toJson()));
            }
            return null;
        }).when(session).sendMessage(any(TextMessage.class));

        boolean result = handler.sendCommand("i-123", "job-1", "start", 5000);
        assertTrue(result);
    }

    @Test
    void testTransportErrorCleansUpSession() throws Exception {
        AgentWsEnvelope hello = AgentWsEnvelope.hello("i-123", "job-1", "sess-1", null);
        handler.handleTextMessage(session, new TextMessage(hello.toJson()));
        assertTrue(handler.hasSession("i-123"));

        handler.handleTransportError(session, new RuntimeException("connection lost"));

        assertFalse(handler.hasSession("i-123"));
    }
}

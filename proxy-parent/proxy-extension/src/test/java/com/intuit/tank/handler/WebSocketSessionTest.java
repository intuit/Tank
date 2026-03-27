package com.intuit.tank.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketSessionTest {

    @Test
    @DisplayName("P0 #10 — clientFragmentOpcode must be volatile for cross-thread visibility")
    void clientFragmentOpcodeIsVolatile() throws Exception {
        Field field = WebSocketSession.class.getDeclaredField("clientFragmentOpcode");
        assertTrue(Modifier.isVolatile(field.getModifiers()),
                "clientFragmentOpcode must be volatile for cross-thread visibility");
    }

    @Test
    @DisplayName("P0 #10 — serverFragmentOpcode must be volatile for cross-thread visibility")
    void serverFragmentOpcodeIsVolatile() throws Exception {
        Field field = WebSocketSession.class.getDeclaredField("serverFragmentOpcode");
        assertTrue(Modifier.isVolatile(field.getModifiers()),
                "serverFragmentOpcode must be volatile for cross-thread visibility");
    }

    @Test
    @DisplayName("P0 #10 — messageCallback must be volatile")
    void messageCallbackIsVolatile() throws Exception {
        Field field = WebSocketSession.class.getDeclaredField("messageCallback");
        assertTrue(Modifier.isVolatile(field.getModifiers()),
                "messageCallback must be volatile");
    }

    @Test
    @DisplayName("addFrame records TEXT messages correctly")
    void addFrameRecordsTextMessages() {
        WebSocketSession session = new WebSocketSession("ws://example.com/ws", Collections.emptyMap());
        WebSocketFrame frame = new WebSocketFrame(true, WebSocketFrame.Opcode.TEXT, false,
                "hello".getBytes(), null);

        session.addFrame(frame, true);

        assertEquals(1, session.getMessageCount());
        WebSocketSession.CapturedMessage msg = session.getMessages().get(0);
        assertTrue(msg.fromClient);
        assertEquals("hello", msg.getPayloadAsText());
    }

    @Test
    @DisplayName("addFrame handles fragmented messages correctly")
    void addFrameHandlesFragmentation() {
        WebSocketSession session = new WebSocketSession("ws://example.com/ws", Collections.emptyMap());

        // First fragment (opcode=TEXT, FIN=0)
        WebSocketFrame frag1 = new WebSocketFrame(false, WebSocketFrame.Opcode.TEXT, false,
                "hel".getBytes(), null);
        session.addFrame(frag1, true);
        assertEquals(0, session.getMessageCount(), "should not record incomplete fragment");

        // Continuation (opcode=CONTINUATION, FIN=1)
        WebSocketFrame frag2 = new WebSocketFrame(true, WebSocketFrame.Opcode.CONTINUATION, false,
                "lo".getBytes(), null);
        session.addFrame(frag2, true);
        assertEquals(1, session.getMessageCount(), "should record complete message after FIN=1");
        assertEquals("hello", session.getMessages().get(0).getPayloadAsText());
    }

    @Test
    @DisplayName("addFrame skips control frames")
    void addFrameSkipsControlFrames() {
        WebSocketSession session = new WebSocketSession("ws://example.com/ws", Collections.emptyMap());

        session.addFrame(new WebSocketFrame(true, WebSocketFrame.Opcode.PING, false, new byte[0], null), true);
        session.addFrame(new WebSocketFrame(true, WebSocketFrame.Opcode.PONG, false, new byte[0], null), false);
        session.addFrame(new WebSocketFrame(true, WebSocketFrame.Opcode.CLOSE, false, new byte[0], null), true);

        assertEquals(0, session.getMessageCount(), "control frames should not be recorded");
    }

    @Test
    @DisplayName("toTransaction preserves URL and messages")
    void toTransactionPreservesData() {
        WebSocketSession session = new WebSocketSession("ws://example.com/ws",
                java.util.Map.of("Host", "example.com"));
        session.addFrame(new WebSocketFrame(true, WebSocketFrame.Opcode.TEXT, false,
                "test".getBytes(), null), true);
        session.addFrame(new WebSocketFrame(true, WebSocketFrame.Opcode.TEXT, false,
                "reply".getBytes(), null), false);

        var tx = session.toTransaction();
        assertEquals("ws://example.com/ws", tx.getUrl());
        assertEquals(2, tx.getMessages().size());
        assertEquals("example.com", tx.getHandshakeHeaders().get("Host"));
    }

    @Test
    @DisplayName("close marks session as closed and sets endTime")
    void closeMarksSessionClosed() {
        WebSocketSession session = new WebSocketSession("ws://example.com/ws", null);
        assertFalse(session.isClosed());

        session.close();
        assertTrue(session.isClosed());
        assertTrue(session.getEndTime() > 0);
    }

    @Test
    @DisplayName("addFrame is no-op after close")
    void addFrameIgnoredAfterClose() {
        WebSocketSession session = new WebSocketSession("ws://example.com/ws", null);
        session.close();

        session.addFrame(new WebSocketFrame(true, WebSocketFrame.Opcode.TEXT, false,
                "should-be-dropped".getBytes(), null), true);

        assertEquals(0, session.getMessageCount());
    }
}

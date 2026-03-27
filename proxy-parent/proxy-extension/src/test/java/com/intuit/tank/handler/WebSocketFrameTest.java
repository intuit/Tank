package com.intuit.tank.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketFrameTest {

    @Test
    @DisplayName("P2 #36 — isText() returns false for CONTINUATION frames")
    void isTextReturnsFalseForContinuation() {
        // CONTINUATION frame of a binary message should NOT be considered text
        WebSocketFrame frame = new WebSocketFrame(false, WebSocketFrame.Opcode.CONTINUATION,
                false, "data".getBytes(), null);
        assertFalse(frame.isText(), "CONTINUATION frame should not be isText()");
    }

    @Test
    @DisplayName("isText() returns true only for TEXT opcode")
    void isTextReturnsTrueOnlyForText() {
        WebSocketFrame textFrame = new WebSocketFrame(true, WebSocketFrame.Opcode.TEXT,
                false, "hello".getBytes(), null);
        assertTrue(textFrame.isText(), "TEXT frame should be isText()");

        WebSocketFrame binaryFrame = new WebSocketFrame(true, WebSocketFrame.Opcode.BINARY,
                false, new byte[]{1, 2, 3}, null);
        assertFalse(binaryFrame.isText(), "BINARY frame should not be isText()");

        WebSocketFrame closeFrame = new WebSocketFrame(true, WebSocketFrame.Opcode.CLOSE,
                false, new byte[0], null);
        assertFalse(closeFrame.isText(), "CLOSE frame should not be isText()");

        WebSocketFrame pingFrame = new WebSocketFrame(true, WebSocketFrame.Opcode.PING,
                false, new byte[0], null);
        assertFalse(pingFrame.isText(), "PING frame should not be isText()");
    }
}

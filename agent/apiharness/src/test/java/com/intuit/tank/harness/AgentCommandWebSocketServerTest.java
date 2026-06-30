package com.intuit.tank.harness;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AgentCommandWebSocketServerTest {

    @Test
    public void shouldAckEveryChunkWhenAckEveryIsUnsetOrLegacy() {
        assertTrue(AgentCommandWebSocketServer.shouldAckChunk(0, 0));
        assertTrue(AgentCommandWebSocketServer.shouldAckChunk(17, -1));
    }

    @Test
    public void shouldAckOnlyOnConfiguredBoundary() {
        assertFalse(AgentCommandWebSocketServer.shouldAckChunk(30, 32));
        assertTrue(AgentCommandWebSocketServer.shouldAckChunk(31, 32));
    }

    @Test
    public void shouldNotAckNullChunkIndexForPositiveAckEvery() {
        assertFalse(AgentCommandWebSocketServer.shouldAckChunk(null, 32));
    }
}

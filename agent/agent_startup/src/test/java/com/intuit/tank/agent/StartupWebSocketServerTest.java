package com.intuit.tank.agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StartupWebSocketServerTest {

    @Test
    public void shouldAckEveryChunkWhenAckEveryIsUnsetOrLegacy() {
        assertTrue(StartupWebSocketServer.shouldAckChunk(0, 0));
        assertTrue(StartupWebSocketServer.shouldAckChunk(17, -1));
    }

    @Test
    public void shouldAckOnlyOnConfiguredBoundary() {
        assertFalse(StartupWebSocketServer.shouldAckChunk(30, 32));
        assertTrue(StartupWebSocketServer.shouldAckChunk(31, 32));
    }

    @Test
    public void shouldNotAckNullChunkIndexForPositiveAckEvery() {
        assertFalse(StartupWebSocketServer.shouldAckChunk(null, 32));
    }
}

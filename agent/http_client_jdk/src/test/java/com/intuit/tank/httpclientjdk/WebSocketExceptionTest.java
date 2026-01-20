package com.intuit.tank.httpclientjdk;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

/**
 * Unit tests for WebSocketException
 */
class WebSocketExceptionTest {

    @Test
    @DisplayName("Constructor should set pattern and message")
    void testConstructor() {
        WebSocketException ex = new WebSocketException("error", "This is an error message");

        assertEquals("error", ex.getPattern());
        assertEquals("This is an error message", ex.getFailedMessage());
    }

    @Test
    @DisplayName("getMessage should include pattern and message")
    void testGetMessage() {
        WebSocketException ex = new WebSocketException("\"status\":500", "{\"status\":500}");

        String msg = ex.getMessage();
        assertTrue(msg.contains("\"status\":500"));
        assertTrue(msg.contains("Fail-on pattern matched"));
    }

    @Test
    @DisplayName("Should handle null message gracefully")
    void testNullMessage() {
        WebSocketException ex = new WebSocketException("pattern", null);

        assertEquals("pattern", ex.getPattern());
        assertNull(ex.getFailedMessage());
        assertTrue(ex.getMessage().contains("(null)"));
    }

    @Test
    @DisplayName("Should truncate long messages in exception message")
    void testLongMessage() {
        String longMessage = "x".repeat(200);
        WebSocketException ex = new WebSocketException("error", longMessage);

        assertEquals(longMessage, ex.getFailedMessage());
        // The exception message itself should be truncated for readability
        assertTrue(ex.getMessage().length() < longMessage.length() + 100);
    }

    @Test
    @DisplayName("Should be throwable as RuntimeException")
    void testIsRuntimeException() {
        WebSocketException ex = new WebSocketException("test", "message");
        assertTrue(ex instanceof RuntimeException);
    }
}

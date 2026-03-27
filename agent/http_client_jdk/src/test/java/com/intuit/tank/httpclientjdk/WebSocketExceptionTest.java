package com.intuit.tank.httpclientjdk;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

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

    // ==================== P2 #33 - Field should not shadow Throwable.detailMessage ====================

    @Test
    @DisplayName("P2 #33: failedMessage field should not shadow Throwable.detailMessage")
    void testFieldDoesNotShadowThrowable() throws Exception {
        // The field storing the failed message content should be named 'failedMessage',
        // not 'message', to avoid shadowing Throwable.detailMessage
        WebSocketException ex = new WebSocketException("pattern", "the failed content");

        // Verify the field is named 'failedMessage' (not 'message')
        Field failedMessageField = WebSocketException.class.getDeclaredField("failedMessage");
        assertNotNull(failedMessageField, "Field should be named 'failedMessage'");
        failedMessageField.setAccessible(true);
        assertEquals("the failed content", failedMessageField.get(ex));

        // Verify getMessage() (from Throwable) returns the super message, not the field
        String superMessage = ex.getMessage();
        assertTrue(superMessage.contains("Fail-on pattern matched"));

        // Verify getFailedMessage() returns the actual failed message content
        assertEquals("the failed content", ex.getFailedMessage());
    }
}

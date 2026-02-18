package com.intuit.tank.harness.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;

import com.intuit.tank.test.TestGroups;

/**
 * Unit tests for WebSocketRequest
 */
public class WebSocketRequestTest {

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("Default constructor should create empty request")
    public void testDefaultConstructor() {
        WebSocketRequest request = new WebSocketRequest();
        assertNull(request.getUrl());
        assertNull(request.getPayload());
        assertNull(request.getTimeoutMs());
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("Setters and getters should work correctly")
    public void testSettersAndGetters() {
        WebSocketRequest request = new WebSocketRequest();
        
        request.setUrl("ws://localhost:8080/ws");
        request.setPayload("{\"type\":\"test\"}");
        request.setTimeoutMs(5000);
        
        assertEquals("ws://localhost:8080/ws", request.getUrl());
        assertEquals("{\"type\":\"test\"}", request.getPayload());
        assertEquals(5000, request.getTimeoutMs());
    }

    // ==================== Validation Tests ====================

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("CONNECT validation should pass with URL")
    public void testValidateConnectWithUrl() {
        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws");
        
        assertDoesNotThrow(() -> request.validate(WebSocketAction.CONNECT));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("CONNECT validation should fail without URL")
    public void testValidateConnectWithoutUrl() {
        WebSocketRequest request = new WebSocketRequest();
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            request.validate(WebSocketAction.CONNECT);
        });
        assertTrue(ex.getMessage().contains("CONNECT"));
        assertTrue(ex.getMessage().contains("url"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("CONNECT validation should fail with empty URL")
    public void testValidateConnectWithEmptyUrl() {
        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("   ");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            request.validate(WebSocketAction.CONNECT);
        });
        assertTrue(ex.getMessage().contains("CONNECT"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("SEND validation should pass with payload")
    public void testValidateSendWithPayload() {
        WebSocketRequest request = new WebSocketRequest();
        request.setPayload("test message");
        
        assertDoesNotThrow(() -> request.validate(WebSocketAction.SEND));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("SEND validation should fail without payload")
    public void testValidateSendWithoutPayload() {
        WebSocketRequest request = new WebSocketRequest();
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            request.validate(WebSocketAction.SEND);
        });
        assertTrue(ex.getMessage().contains("SEND"));
        assertTrue(ex.getMessage().contains("payload"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("SEND validation should fail with empty payload")
    public void testValidateSendWithEmptyPayload() {
        WebSocketRequest request = new WebSocketRequest();
        request.setPayload("   ");
        
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            request.validate(WebSocketAction.SEND);
        });
        assertTrue(ex.getMessage().contains("SEND"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("DISCONNECT validation should always pass")
    public void testValidateDisconnect() {
        WebSocketRequest request = new WebSocketRequest();
        
        assertDoesNotThrow(() -> request.validate(WebSocketAction.DISCONNECT));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("ASSERT validation should always pass")
    public void testValidateAssert() {
        WebSocketRequest request = new WebSocketRequest();

        assertDoesNotThrow(() -> request.validate(WebSocketAction.ASSERT));
    }

    // ==================== toString Tests ====================

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("toString with URL and short payload")
    public void testToStringWithUrlAndShortPayload() {
        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws");
        request.setPayload("short");
        
        String str = request.toString();
        assertTrue(str.contains("localhost:8080"));
        assertTrue(str.contains("short"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("toString with long payload should truncate")
    public void testToStringWithLongPayload() {
        WebSocketRequest request = new WebSocketRequest();
        request.setPayload("x".repeat(100));
        
        String str = request.toString();
        assertTrue(str.contains("..."));
        assertTrue(str.length() < 200); // Should be truncated
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("toString with null values should not throw")
    public void testToStringWithNullValues() {
        WebSocketRequest request = new WebSocketRequest();
        
        String str = request.toString();
        assertNotNull(str);
        assertTrue(str.contains("WebSocketRequest"));
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    @DisplayName("toString with empty payload should work")
    public void testToStringWithEmptyPayload() {
        WebSocketRequest request = new WebSocketRequest();
        request.setPayload("");
        
        String str = request.toString();
        assertNotNull(str);
    }

    // Note: JAXB marshalling of WebSocketRequest is tested via WebSocketStepJaxbTest
    // since WebSocketRequest is embedded within WebSocketStep
}

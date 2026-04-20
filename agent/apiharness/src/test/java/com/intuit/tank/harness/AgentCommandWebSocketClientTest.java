package com.intuit.tank.harness;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AgentCommandWebSocketClientTest {

    @Test
    void testConstructorSetsFields() {
        AgentCommandWebSocketClient client = new AgentCommandWebSocketClient(
                "http://controller.example.com/tank",
                "/v2/agent/ws/control",
                "test-token",
                "i-123",
                "job-1"
        );
        assertNotNull(client);
    }

    @Test
    void testConstructorWithHttpsUrl() {
        AgentCommandWebSocketClient client = new AgentCommandWebSocketClient(
                "https://controller.example.com/tank",
                "/v2/agent/ws/control",
                "test-token",
                "i-456",
                "job-2"
        );
        assertNotNull(client);
    }

    @Test
    void testCloseBeforeConnect() {
        AgentCommandWebSocketClient client = new AgentCommandWebSocketClient(
                "http://localhost:8080/tank",
                "/v2/agent/ws/control",
                "token",
                "i-789",
                "job-3"
        );
        // Should not throw
        client.close();
    }
}

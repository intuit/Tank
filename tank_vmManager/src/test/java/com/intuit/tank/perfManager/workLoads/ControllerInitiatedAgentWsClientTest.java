package com.intuit.tank.perfManager.workLoads;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class ControllerInitiatedAgentWsClientTest {

    @Test
    public void testHasSessionFalseByDefault() {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        assertFalse(client.hasSession("i-missing"));
    }

    @Test
    public void testSendCommandWithoutSessionReturnsFalse() {
        ControllerInitiatedAgentWsClient client = new ControllerInitiatedAgentWsClient();
        assertFalse(client.sendCommand("i-missing", "job-1", "start", 1000L));
    }
}

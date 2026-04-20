package com.intuit.tank.vm.agent.messages;

/**
 * Interface for sending commands to agents via WebSocket.
 * Implemented by the controller's WS handler, consumed by JobManager via CDI.
 */
public interface AgentWsCommandSender {

    /**
     * Check if an agent has an active WS session.
     */
    boolean hasSession(String instanceId);

    /**
     * Send a command to an agent via WS and wait for ack.
     * @return true if command was acked successfully within timeout
     */
    boolean sendCommand(String instanceId, String jobId, String command, long ackTimeoutMillis);
}

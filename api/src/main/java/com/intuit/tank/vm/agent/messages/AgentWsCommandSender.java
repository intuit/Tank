package com.intuit.tank.vm.agent.messages;

/**
 * Interface for sending commands to agents via WebSocket.
 * Implemented by the controller's WS handler, consumed by JobManager via CDI.
 */
public interface AgentWsCommandSender {

    // Static holder for Spring-CDI bridge fallback
    java.util.concurrent.atomic.AtomicReference<AgentWsCommandSender> STATIC_INSTANCE = new java.util.concurrent.atomic.AtomicReference<>();

    static void setStaticInstance(AgentWsCommandSender sender) {
        STATIC_INSTANCE.set(sender);
    }

    static AgentWsCommandSender getStaticInstance() {
        return STATIC_INSTANCE.get();
    }

    /**
     * Check if an agent has an active WS session.
     */
    boolean hasSession(String instanceId);

    /**
     * Send a command to an agent via WS and wait for ack.
     * @return true if command was acked successfully within timeout
     */
    boolean sendCommand(String instanceId, String jobId, String command, long ackTimeoutMillis);

    /**
     * Check whether initial WS file transfer is complete for the agent.
     * Defaults to true so non-transfer implementations remain compatible.
     */
    default boolean isFileTransferReady(String instanceId) {
        return true;
    }

    default String getWsState(String instanceId) {
        return null;
    }

    default String getTransferProgress(String instanceId) {
        return null;
    }

    default Long getLastSeen(String instanceId) {
        return null;
    }
}

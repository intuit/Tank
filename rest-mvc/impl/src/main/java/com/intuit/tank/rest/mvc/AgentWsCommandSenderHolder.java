package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;

/**
 * Static holder so the Spring-managed WS handler can be accessed by CDI producer.
 * Set by AgentCommandWebSocketConfig at Spring init time.
 */
public class AgentWsCommandSenderHolder {

    private static volatile AgentWsCommandSender instance;

    public static void set(AgentWsCommandSender sender) {
        instance = sender;
    }

    public static AgentWsCommandSender get() {
        return instance;
    }
}

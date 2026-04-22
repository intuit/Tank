package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

/**
 * CDI producer that bridges the Spring-managed AgentCommandWebSocketHandler
 * into the CDI container so JobManager can inject it via Instance<AgentWsCommandSender>.
 */
@ApplicationScoped
public class AgentWsCommandSenderProducer {

    @Produces
    public AgentWsCommandSender getAgentWsCommandSender() {
        return AgentWsCommandSenderHolder.get();
    }
}

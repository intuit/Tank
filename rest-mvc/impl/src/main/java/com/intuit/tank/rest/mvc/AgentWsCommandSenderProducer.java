package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * CDI producer that bridges the Spring-managed AgentCommandWebSocketHandler
 * into the CDI container so JobManager can inject it via Instance<AgentWsCommandSender>.
 */
@ApplicationScoped
public class AgentWsCommandSenderProducer {

    @Produces
    @Named("agentWsCommandSender")
    public AgentWsCommandSender getAgentWsCommandSender() {
        WebApplicationContext ctx = ContextLoader.getCurrentWebApplicationContext();
        if (ctx != null && ctx.containsBean("agentCommandWebSocketHandler")) {
            return ctx.getBean("agentCommandWebSocketHandler", AgentWsCommandSender.class);
        }
        return null;
    }
}

package com.intuit.tank.rest.mvc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class AgentCommandWebSocketConfig implements WebSocketConfigurer {

    @Bean
    public AgentCommandWebSocketHandler agentCommandWebSocketHandler() {
        AgentCommandWebSocketHandler handler = new AgentCommandWebSocketHandler();
        AgentWsCommandSenderHolder.set(handler);
        return handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String path = "/v2/agent/ws/control";
        // Agents connect via JDK HttpClient (no Origin header), not browsers.
        // Allow all origins since auth is handled via bearer token in handshake.
        registry.addHandler(agentCommandWebSocketHandler(), path)
                .setAllowedOriginPatterns("*");
    }
}

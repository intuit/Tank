package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.settings.TankConfig;
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
        return new AgentCommandWebSocketHandler();
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        String path = new TankConfig().getAgentConfig().getCommandWsPath();
        registry.addHandler(agentCommandWebSocketHandler(), path)
                .setAllowedOrigins("*");
    }
}

package com.intuit.tank.rest.mvc;

import com.intuit.tank.vm.settings.TankConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

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
                .setAllowedOriginPatterns("*")
                .addInterceptors(agentTokenInterceptor());
    }

    @Bean
    public HandshakeInterceptor agentTokenInterceptor() {
        return new HandshakeInterceptor() {
            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                           WebSocketHandler wsHandler, Map<String, Object> attributes) {
                String authHeader = request.getHeaders().getFirst("Authorization");
                if (authHeader == null || !authHeader.startsWith("bearer ")) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return false;
                }
                String token = authHeader.substring("bearer ".length()).trim();
                String expectedToken = getExpectedAgentToken();
                if (expectedToken == null || expectedToken.isBlank() || !expectedToken.equals(token)) {
                    response.setStatusCode(HttpStatus.FORBIDDEN);
                    return false;
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                       WebSocketHandler wsHandler, Exception exception) {}
        };
    }

    private String getExpectedAgentToken() {
        try {
            return new TankConfig().getAgentConfig().getAgentToken();
        } catch (Exception e) {
            return null;
        }
    }
}

/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.standalone.agent;

import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.vm.agent.messages.StandaloneAgentRequest;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import tools.jackson.databind.json.JsonMapper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommandListenerTest {

    private final StandaloneAgentStartup agentStartup = mock(StandaloneAgentStartup.class);

    @BeforeEach
    @AfterEach
    public void resetListener() throws Exception {
        staticField("started").setBoolean(null, false);
        staticField("commandToken").set(null, null);
        staticField("agentStarter").set(null, null);
    }

    @ParameterizedTest
    @CsvSource(value = {"NONE", "bearer wrong-token"}, nullValues = "NONE")
    public void testRequestWithoutTheAgentTokenIsRejected(String authorization) throws IOException {
        ByteArrayOutputStream body = new ByteArrayOutputStream();
        HttpExchange exchange = requestExchange(authorization, body);

        captureHandler("secret-token").handle(exchange);

        verify(exchange).sendResponseHeaders(401, "Unauthorized".length());
        assertEquals("Unauthorized", body.toString(StandardCharsets.UTF_8));
        verify(agentStartup, never()).startTest(any());
    }

    @Test
    public void testRequestWithTheAgentTokenStartsTheTest() throws IOException {
        HttpExchange exchange = requestExchange("bearer secret-token", new ByteArrayOutputStream());

        captureHandler("secret-token").handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(agentStartup).startTest(any(StandaloneAgentRequest.class));
    }

    @Test
    public void testRequestNeedsNoTokenWhenTheAgentHasNone() throws IOException {
        HttpExchange exchange = requestExchange(null, new ByteArrayOutputStream());

        captureHandler(null).handle(exchange);

        verify(exchange).sendResponseHeaders(eq(200), anyLong());
        verify(agentStartup).startTest(any(StandaloneAgentRequest.class));
    }

    @Test
    public void testReadsTheRequestTheControllerSends() {
        StandaloneAgentRequest sent = new StandaloneAgentRequest("42", "localhost", 25);
        sent.setStopBehavior(StopBehavior.END_OF_TEST.name());
        // as JobManager.sendRequest writes it
        byte[] body = JsonMapper.builder().build().writeValueAsBytes(sent);

        StandaloneAgentRequest received = CommandListener.getRequest(new ByteArrayInputStream(body));

        assertEquals("42", received.getJobId());
        assertEquals("localhost", received.getInstanceId());
        assertEquals(25, received.getUsers());
        assertEquals(StopBehavior.END_OF_TEST.name(), received.getStopBehavior());
    }

    @Test
    public void testUnreadableRequest() {
        assertNull(CommandListener.getRequest(
                new ByteArrayInputStream("<standalone-agent-request/>".getBytes(StandardCharsets.UTF_8))));
    }

    /** Starts the listener against a mocked server and returns the handler it registered. */
    private HttpHandler captureHandler(String token) {
        try (MockedStatic<HttpServer> httpServer = mockStatic(HttpServer.class)) {
            HttpServer server = mock(HttpServer.class);
            HttpContext context = mock(HttpContext.class);
            when(server.createContext("/")).thenReturn(context);
            httpServer.when(() -> HttpServer.create(any(InetSocketAddress.class), anyInt())).thenReturn(server);

            CommandListener.startHttpServer(CommandListener.PORT, agentStartup, token);

            ArgumentCaptor<HttpHandler> handler = ArgumentCaptor.forClass(HttpHandler.class);
            verify(context).setHandler(handler.capture());
            return handler.getValue();
        }
    }

    /** The job request JobManager.sendRequest posts, with the given Authorization header */
    private static HttpExchange requestExchange(String authorization, ByteArrayOutputStream response) {
        byte[] body = JsonMapper.builder().build().writeValueAsBytes(new StandaloneAgentRequest("42", "localhost", 25));
        Headers requestHeaders = new Headers();
        if (authorization != null) {
            requestHeaders.set("Authorization", authorization);
        }
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestURI()).thenReturn(URI.create("http://localhost:1090/request"));
        when(exchange.getRequestHeaders()).thenReturn(requestHeaders);
        when(exchange.getRequestBody()).thenReturn(new ByteArrayInputStream(body));
        when(exchange.getResponseHeaders()).thenReturn(new Headers());
        when(exchange.getResponseBody()).thenReturn(response);
        return exchange;
    }

    private static Field staticField(String name) throws NoSuchFieldException {
        Field field = CommandListener.class.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }
}

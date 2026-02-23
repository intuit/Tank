package com.intuit.tank.runner.method;

/*
 * #%L
 * Intuit Tank Agent (apiharness)
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import com.intuit.tank.harness.data.HDTestPlan;
import com.intuit.tank.harness.data.WebSocketAction;
import com.intuit.tank.harness.data.WebSocketRequest;
import com.intuit.tank.harness.data.WebSocketStep;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.httpclientjdk.TankWebSocketClient;
import com.intuit.tank.runner.ErrorContainer;
import com.intuit.tank.runner.TestPlanRunner;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

/**
 * Unit tests for WebSocketRunner
 */
public class WebSocketRunnerTest {

    private TestPlanRunner testPlanRunner;

    @BeforeEach
    public void setUp() {
        testPlanRunner = new TestPlanRunner(new HDTestPlan(), 1, "test-http-client");
    }

    @Test
    public void testWebSocketRunnerCreation() {
        // Create WebSocket step
        WebSocketStep step = new WebSocketStep();
        step.setName("Test Step");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("test_conn");

        // Create test context
        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique", 
            new TimerMap(), testPlanRunner);

        // Create WebSocket runner
        WebSocketRunner runner = new WebSocketRunner(context);
        assertNotNull(runner);
    }

    @Test
    public void testInvalidAction() {
        WebSocketStep step = new WebSocketStep();
        step.setName("Invalid Action Test");
        // Don't set action - should be null
        step.setConnectionId("test_conn");

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique", 
            new TimerMap(), testPlanRunner);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    @Test
    public void testConnectFailurePropagatesErrorAndCleansUpClient() {
        WebSocketStep step = new WebSocketStep();
        step.setName("Connect Failure Propagation Test");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("conn-1");

        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws/does-not-exist");
        request.setTimeoutMs(750);
        step.setRequest(request);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Invalid handshake response getStatus: 404"));
        when(client.isConnected()).thenReturn(true);
        when(client.connect(750)).thenReturn(failedFuture);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
        assertNull(context.getWebSocketClient("conn-1"));
        assertFalse(context.getErrors().isEmpty());
        ErrorContainer error = context.getErrors().get(0);
        assertEquals("WEBSOCKET_CONNECT", error.getLocation());
        assertTrue(error.getReason().contains("404"));
        verify(client).disconnect();
    }

    @Test
    public void testConnectReplacesStaleClientForRetry() {
        WebSocketStep step = new WebSocketStep();
        step.setName("Connect Retry Replaces Stale Client Test");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("conn-1");

        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://localhost:8080/ws/test");
        request.setTimeoutMs(1200);
        step.setRequest(request);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient staleClient = mock(TankWebSocketClient.class);
        when(staleClient.isConnected()).thenReturn(false);
        context.setWebSocketClient("conn-1", staleClient);

        try (MockedConstruction<TankWebSocketClient> mockedConstruction = Mockito.mockConstruction(TankWebSocketClient.class,
                (mock, constructionContext) -> {
                    assertEquals("ws://localhost:8080/ws/test", constructionContext.arguments().get(0));
                    when(mock.connect(1200)).thenReturn(CompletableFuture.completedFuture(true));
                })) {
            WebSocketRunner runner = new WebSocketRunner(context);
            String result = runner.execute();

            assertEquals(TankConstants.HTTP_CASE_PASS, result);
            verify(staleClient).disconnect();
            assertEquals(1, mockedConstruction.constructed().size());
            TankWebSocketClient replacementClient = mockedConstruction.constructed().get(0);
            assertEquals(replacementClient, context.getWebSocketClient("conn-1"));
        }
    }

    @Test
    public void testMissingConnectionId() {
        WebSocketStep step = new WebSocketStep();
        step.setName("Missing Connection ID Test");
        step.setAction(WebSocketAction.CONNECT);
        // No connectionId set

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique", 
            new TimerMap(), testPlanRunner);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    @Test
    public void testMissingAction() {
        WebSocketStep step = new WebSocketStep();
        step.setName("Missing Action Test");
        // No action set
        step.setConnectionId("test_conn");

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique", 
            new TimerMap(), testPlanRunner);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    @Test
    public void testConnectWithoutUrl() {
        WebSocketStep step = new WebSocketStep();
        step.setName("Connect Without URL Test");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("test_conn");

        // Request without URL
        WebSocketRequest request = new WebSocketRequest();
        step.setRequest(request);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique", 
            new TimerMap(), testPlanRunner);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
        assertFalse(context.getErrors().isEmpty());
        assertEquals("WEBSOCKET_CONNECT", context.getErrors().get(0).getLocation());
    }

    @Test
    public void testSendWithoutConnection() {
        WebSocketStep step = new WebSocketStep();
        step.setName("Send Without Connection Test");
        step.setAction(WebSocketAction.SEND);
        step.setConnectionId("nonexistent_conn");
        
        WebSocketRequest request = new WebSocketRequest();
        request.setPayload("test message");
        step.setRequest(request);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique", 
            new TimerMap(), testPlanRunner);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_FAIL, result);
    }

    @Test
    public void testDisconnectNonexistentConnection() {
        WebSocketStep step = new WebSocketStep();
        step.setName("Disconnect Nonexistent Connection Test");
        step.setAction(WebSocketAction.DISCONNECT);
        step.setConnectionId("nonexistent_conn");

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique", 
            new TimerMap(), testPlanRunner);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        // Disconnect of nonexistent connection should pass (not an error)
        assertEquals(TankConstants.HTTP_CASE_PASS, result);
    }

    @Test
    public void testConnectWithExistingClientSuccess() throws Exception {
        WebSocketStep step = new WebSocketStep();
        step.setName("Connect Existing Client Test");
        step.setAction(WebSocketAction.CONNECT);
        step.setConnectionId("conn-1");

        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("wss://example/ws");
        request.setTimeoutMs(4000);
        step.setRequest(request);

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        when(client.isConnected()).thenReturn(true);
        when(client.connect(4000)).thenReturn(CompletableFuture.completedFuture(true));
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
        verify(client).connect(4000);
        assertEquals(client, context.getWebSocketClient("conn-1"));
    }

    @Test
    public void testDisconnectExistingConnection() throws Exception {
        WebSocketStep step = new WebSocketStep();
        step.setName("Disconnect Existing Connection Test");
        step.setAction(WebSocketAction.DISCONNECT);
        step.setConnectionId("conn-1");

        Variables variables = new Variables();
        TestStepContext context = new TestStepContext(
            step, variables, "test-plan", "test-unique",
            new TimerMap(), testPlanRunner);

        TankWebSocketClient client = mock(TankWebSocketClient.class);
        context.setWebSocketClient("conn-1", client);

        WebSocketRunner runner = new WebSocketRunner(context);
        String result = runner.execute();

        assertEquals(TankConstants.HTTP_CASE_PASS, result);
        verify(client).disconnect();
        assertNull(context.getWebSocketClient("conn-1"));
    }

    @Test
    public void testWebSocketStepWithEnums() {
        // Test programmatic creation with enums
        WebSocketStep step = new WebSocketStep();
        step.setName("Test WebSocket Step");
        step.setAction(WebSocketAction.SEND);
        step.setConnectionId("test_connection");
        step.setStepIndex(1);
        
        assertEquals("Test WebSocket Step", step.getName());
        assertEquals(WebSocketAction.SEND, step.getAction());
        assertEquals("test_connection", step.getConnectionId());
        assertEquals(1, step.getStepIndex());
        
        // Test getInfo() method with enum
        String info = step.getInfo();
        assertTrue(info.contains("SEND"));
        assertTrue(info.contains("Test WebSocket Step"));
        assertTrue(info.contains("test_connection"));
    }

    @Test
    public void testWebSocketRequestBasicFields() {
        // Test WebSocketRequest basic fields (MVP)
        WebSocketRequest request = new WebSocketRequest();
        request.setUrl("ws://test.example.com");
        request.setPayload("test message");
        request.setTimeoutMs(5000);
        
        assertEquals("ws://test.example.com", request.getUrl());
        assertEquals("test message", request.getPayload());
        assertEquals(Integer.valueOf(5000), request.getTimeoutMs());
    }
}

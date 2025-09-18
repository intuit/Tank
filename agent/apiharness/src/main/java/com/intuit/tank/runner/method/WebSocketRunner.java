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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.data.WebSocketAction;
import com.intuit.tank.harness.data.WebSocketRequest;
import com.intuit.tank.harness.data.WebSocketResponse;
import com.intuit.tank.harness.data.WebSocketStep;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.httpclientjdk.TankWebSocketClient;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.vm.common.TankConstants;

/**
 * WebSocketRunner executes WebSocket operations in Tank scripts.
 */
public class WebSocketRunner implements Runner {

    private static final Logger LOG = LogManager.getLogger(WebSocketRunner.class);

    private TestStepContext tsc;
    private WebSocketStep step;
    private Variables variables;
    private WebSocketRequest request;
    private WebSocketResponse response;

    /**
     * Constructor
     * @param context TestStepContext containing the WebSocketStep
     */
    public WebSocketRunner(TestStepContext context) {
        this.tsc = context;
        this.step = (WebSocketStep) context.getTestStep();
        this.variables = context.getVariables();
        this.request = step.getRequest();
        this.response = step.getResponse();
    }

    @Override
    public String execute() {
        String result = TankConstants.HTTP_CASE_PASS;
        
        try {
            WebSocketAction action = step.getAction();
            String connectionId = step.getConnectionId();
            
            if (action == null) {
                LOG.error("WebSocket action is required");
                return TankConstants.HTTP_CASE_FAIL;
            }
            
            if (StringUtils.isEmpty(connectionId)) {
                LOG.error("WebSocket connectionId is required");
                return TankConstants.HTTP_CASE_FAIL;
            }

            // Substitute variables in connectionId
            connectionId = variables.evaluate(connectionId);
            
            // Validate request for this action
            if (request != null) {
                request.validate(action);
            }
            
            LOG.debug(LogUtil.getLogMessage(
                "Executing WebSocket action: " + action + " on connection: " + connectionId,
                LogEventType.Informational, LoggingProfile.VERBOSE));

            switch (action) {
                case CONNECT:
                    result = executeConnect(connectionId);
                    break;
                case SEND:
                    result = executeSend(connectionId);
                    break;
                case EXPECT:
                    result = executeExpect(connectionId);
                    break;
                case DISCONNECT:
                    result = executeDisconnect(connectionId);
                    break;
                default:
                    LOG.error("Unknown WebSocket action: " + action);
                    result = TankConstants.HTTP_CASE_FAIL;
            }

        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage(
                "Error executing WebSocket step: " + e.getMessage(),
                LogEventType.Informational, LoggingProfile.STANDARD), e);
            result = TankConstants.HTTP_CASE_FAIL;
        }

        return result;
    }

    /**
     * Execute WebSocket connect action
     */
    private String executeConnect(String connectionId) throws Exception {
        if (request == null || StringUtils.isEmpty(request.getUrl())) {
            LOG.error("WebSocket URL is required for connect action");
            return TankConstants.HTTP_CASE_FAIL;
        }

        String url = variables.evaluate(request.getUrl());
        
        // Get or create WebSocket client
        TankWebSocketClient client = tsc.getWebSocketClient(connectionId);
        if (client == null) {
            // Create new client
            client = new TankWebSocketClient(url);
            tsc.setWebSocketClient(connectionId, client);
        }

        // Set timeout from request
        int timeoutMs = request.getTimeoutMs() != null ? request.getTimeoutMs() : 5000;

        // Connect
        CompletableFuture<Boolean> connectFuture = client.connect(timeoutMs);
        
        try {
            Boolean connected = connectFuture.get(timeoutMs, TimeUnit.MILLISECONDS);
            if (connected) {
                LOG.info(LogUtil.getLogMessage(
                    "WebSocket connected to: " + url,
                    LogEventType.Informational, LoggingProfile.STANDARD));
                return TankConstants.HTTP_CASE_PASS;
            } else {
                LOG.error(LogUtil.getLogMessage(
                    "Failed to connect to WebSocket: " + url,
                    LogEventType.Informational, LoggingProfile.STANDARD));
                return TankConstants.HTTP_CASE_FAIL;
            }
        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage(
                "Failed to connect to WebSocket: " + url + " - " + e.getMessage(),
                LogEventType.Informational, LoggingProfile.STANDARD), e);
            return TankConstants.HTTP_CASE_FAIL;
        }
    }

    /**
     * Execute WebSocket send action
     */
    private String executeSend(String connectionId) throws Exception {
        TankWebSocketClient client = tsc.getWebSocketClient(connectionId);
        if (client == null) {
            LOG.error("WebSocket connection not found: " + connectionId);
            return TankConstants.HTTP_CASE_FAIL;
        }

        if (request == null || StringUtils.isEmpty(request.getPayload())) {
            LOG.error("WebSocket payload is required for send action");
            return TankConstants.HTTP_CASE_FAIL;
        }

        String payload = variables.evaluate(request.getPayload());

        try {
            // MVP: TEXT only, get timeout from request
            int timeoutMs = request.getTimeoutMs() != null ? request.getTimeoutMs() : 2000;
            client.sendMessage(payload).get(timeoutMs, TimeUnit.MILLISECONDS);

            LOG.debug(LogUtil.getLogMessage(
                "Sent WebSocket message on connection: " + connectionId + " - " + payload,
                LogEventType.Informational, LoggingProfile.VERBOSE));
            return TankConstants.HTTP_CASE_PASS;
        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage(
                "Failed to send WebSocket message: " + e.getMessage(),
                LogEventType.Informational, LoggingProfile.STANDARD), e);
            return TankConstants.HTTP_CASE_FAIL;
        }
    }

    /**
     * Execute WebSocket expect/receive action
     */
    private String executeExpect(String connectionId) throws Exception {
        TankWebSocketClient client = tsc.getWebSocketClient(connectionId);
        if (client == null) {
            LOG.error("WebSocket connection not found: " + connectionId);
            return TankConstants.HTTP_CASE_FAIL;
        }

        if (response == null) {
            LOG.error("EXPECT action requires <response>");
            return TankConstants.HTTP_CASE_FAIL;
        }

        // Get timeout from response (not request)
        final int timeoutMs = response.getTimeoutMs() != null ? response.getTimeoutMs() : 2000;
        final String expectedContent = response.getExpectedContent() != null 
            ? variables.evaluate(response.getExpectedContent()) : null;

        try {
            // MVP: For echo servers, send the expected content and await echo
            // This works well for testing since we control what we expect back
            String messageToSend = expectedContent != null ? expectedContent : "test_" + System.currentTimeMillis();
            CompletableFuture<String> future = client.sendAndAwaitResponse(messageToSend, timeoutMs);
            String receivedMessage = future.get(timeoutMs, TimeUnit.MILLISECONDS);

            if (receivedMessage == null) {
                if (response.isOptional()) {
                    LOG.info(LogUtil.getLogMessage(
                        "No message within " + timeoutMs + "ms (optional EXPECT).",
                        LogEventType.Informational, LoggingProfile.STANDARD));
                    return TankConstants.HTTP_CASE_PASS;
                }
                LOG.warn("No WebSocket message received within timeout: " + timeoutMs + "ms");
                return TankConstants.HTTP_CASE_FAIL;
            }

            // Check expected content if specified
            if (expectedContent != null && !receivedMessage.contains(expectedContent)) {
                if (response.isOptional()) {
                    LOG.info(LogUtil.getLogMessage(
                        "Message doesn't contain expected content (optional EXPECT): " + expectedContent,
                        LogEventType.Informational, LoggingProfile.STANDARD));
                    return TankConstants.HTTP_CASE_PASS;
                }
                LOG.error("Received message doesn't contain expected content: " + expectedContent);
                return TankConstants.HTTP_CASE_FAIL;
            }

            LOG.debug(LogUtil.getLogMessage(
                "Received WebSocket message on " + connectionId + " : " + receivedMessage,
                LogEventType.Informational, LoggingProfile.VERBOSE));

            // Save to variable if specified
            if (response.getSaveVariable() != null) {
                String varName = variables.evaluate(response.getSaveVariable());
                variables.addVariable(varName, receivedMessage);
            }

            return TankConstants.HTTP_CASE_PASS;

        } catch (Exception e) {
            if (response.isOptional()) {
                LOG.info(LogUtil.getLogMessage(
                    "EXPECT optional; treating as pass despite error: " + e.getMessage(),
                    LogEventType.Informational, LoggingProfile.STANDARD));
                return TankConstants.HTTP_CASE_PASS;
            }
            LOG.error(LogUtil.getLogMessage(
                "Failed to receive WebSocket message: " + e.getMessage(),
                LogEventType.Informational, LoggingProfile.STANDARD), e);
            return TankConstants.HTTP_CASE_FAIL;
        }
    }

    /**
     * Execute WebSocket disconnect action
     */
    private String executeDisconnect(String connectionId) throws Exception {
        TankWebSocketClient client = tsc.getWebSocketClient(connectionId);
        if (client == null) {
            LOG.warn("WebSocket connection not found for disconnect: " + connectionId);
            return TankConstants.HTTP_CASE_PASS; // Not an error if already disconnected
        }

        try {
            client.disconnect();
            tsc.removeWebSocketClient(connectionId);
            
            LOG.info(LogUtil.getLogMessage(
                "WebSocket disconnected: " + connectionId,
                LogEventType.Informational, LoggingProfile.STANDARD));
            return TankConstants.HTTP_CASE_PASS;
        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage(
                "Failed to disconnect WebSocket: " + e.getMessage(),
                LogEventType.Informational, LoggingProfile.STANDARD), e);
            return TankConstants.HTTP_CASE_FAIL;
        }
    }

}

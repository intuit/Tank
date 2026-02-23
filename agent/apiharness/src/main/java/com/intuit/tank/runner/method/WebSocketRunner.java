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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.harness.data.AssertionBlock;
import com.intuit.tank.harness.data.FailOnPattern;
import com.intuit.tank.harness.data.SaveOccurrence;
import com.intuit.tank.harness.data.ValidationData;
import com.intuit.tank.harness.data.WebSocketAction;
import com.intuit.tank.harness.data.WebSocketAssertion;
import com.intuit.tank.harness.data.WebSocketRequest;
import com.intuit.tank.harness.data.WebSocketStep;
import com.intuit.tank.harness.logging.LogUtil;
import com.intuit.tank.harness.test.data.Variables;
import com.intuit.tank.httpclientjdk.MessageStream;
import com.intuit.tank.httpclientjdk.TankWebSocketClient;
import com.intuit.tank.logging.LogEventType;
import com.intuit.tank.logging.LoggingProfile;
import com.intuit.tank.runner.ErrorContainer;
import com.intuit.tank.runner.TestStepContext;
import com.intuit.tank.script.RequestDataPhase;
import com.intuit.tank.vm.common.TankConstants;

/**
 * WebSocketRunner executes WebSocket operations in Tank scripts.
 */
public class WebSocketRunner implements Runner {

    private static final Logger LOG = LogManager.getLogger(WebSocketRunner.class);
    private static final int DEFAULT_TIMEOUT_MS = 5000;

    private TestStepContext tsc;
    private WebSocketStep step;
    private Variables variables;
    private WebSocketRequest request;

    /**
     * Constructor
     * @param context TestStepContext containing the WebSocketStep
     */
    public WebSocketRunner(TestStepContext context) {
        this.tsc = context;
        this.step = (WebSocketStep) context.getTestStep();
        this.variables = context.getVariables();
        this.request = step.getRequest();
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
            
            // fail immediately (except for CONNECT which creates the connection and DISCONNECT which should still run to cleanup)
            if (action != WebSocketAction.CONNECT && action != WebSocketAction.DISCONNECT) {
                TankWebSocketClient existingClient = tsc.getWebSocketClient(connectionId);
                if (existingClient != null && existingClient.hasFailed()) {
                    MessageStream stream = existingClient.getMessageStream();
                    String failPattern = stream != null ? stream.getFailurePattern() : "unknown";
                    LOG.error(LogUtil.getLogMessage(
                        "WebSocket connection " + connectionId + " has already failed due to fail-on pattern: " +
                        failPattern +
                        " - aborting " + action + " action",
                        LogEventType.Validation, LoggingProfile.STANDARD));
                    addValidationError("WEBSOCKET_FAIL_ON", "failOnPattern", failPattern,
                        stream != null ? stream.getFailureMessage() : "", "Connection already marked failed");
                    return TankConstants.HTTP_CASE_FAIL;
                }
            }
            
            // CONNECT validation is handled in executeConnect() to ensure debugger-visible error details.
            if (request != null && action != WebSocketAction.CONNECT) {
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
                case ASSERT:
                    result = executeAssert(connectionId);
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
            addValidationError("WEBSOCKET_CONNECT", "url", "non-empty", "",
                "WebSocket URL is required for connect action");
            return TankConstants.HTTP_CASE_FAIL;
        }

        String url = variables.evaluate(request.getUrl());
        
        TankWebSocketClient client = tsc.getWebSocketClient(connectionId);
        if (client != null && !client.isConnected()) {
            cleanupWebSocketClient(connectionId, client);
            client = null;
        }

        if (client == null) {
            client = new TankWebSocketClient(url);
            tsc.setWebSocketClient(connectionId, client);
        }

        int timeoutMs = request.getTimeoutMs() != null ? request.getTimeoutMs() : DEFAULT_TIMEOUT_MS;
        CompletableFuture<Boolean> connectFuture = client.connect(timeoutMs);
        
        try {
            Boolean connected = connectFuture.get(timeoutMs, TimeUnit.MILLISECONDS);
            if (connected) {
                LOG.info(LogUtil.getLogMessage(
                    "WebSocket connected to: " + url,
                    LogEventType.Informational, LoggingProfile.STANDARD));

                client.createMessageStream(connectionId);

                List<FailOnPattern> failOnPatterns = step.getFailOnPatterns();
                if (failOnPatterns != null && !failOnPatterns.isEmpty()) {
                    MessageStream stream = client.getMessageStream();
                    for (FailOnPattern pattern : failOnPatterns) {
                        stream.addFailOnPattern(pattern.getPattern(), pattern.isRegex());
                        LOG.debug("Registered fail-on pattern: {}", pattern);
                    }
                    LOG.info(LogUtil.getLogMessage(
                        "Registered " + failOnPatterns.size() + " fail-on patterns for connection: " + connectionId,
                        LogEventType.Informational, LoggingProfile.VERBOSE));
                }

                return TankConstants.HTTP_CASE_PASS;
            } else {
                LOG.error(LogUtil.getLogMessage(
                    "Failed to connect to WebSocket: " + url,
                    LogEventType.Informational, LoggingProfile.STANDARD));
                addValidationError("WEBSOCKET_CONNECT", "url", url, "connection rejected",
                    "Failed to connect to WebSocket: " + url);
                cleanupWebSocketClient(connectionId, client);
                return TankConstants.HTTP_CASE_FAIL;
            }
        } catch (Exception e) {
            String actualError = extractConnectError(e);
            LOG.error(LogUtil.getLogMessage(
                "Failed to connect to WebSocket: " + url + " - " + actualError,
                LogEventType.Informational, LoggingProfile.STANDARD), e);
            addValidationError("WEBSOCKET_CONNECT", "url", url, actualError,
                "Failed to connect to WebSocket: " + url + " - " + actualError);
            cleanupWebSocketClient(connectionId, client);
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
            // Fire-and-forget send - messages collected in MessageStream for end-of-session assertions
            client.sendMessage(payload).get();

            LOG.info(LogUtil.getLogMessage(
                "WebSocket SEND successful on connection: " + connectionId,
                LogEventType.Informational, LoggingProfile.STANDARD));
            return TankConstants.HTTP_CASE_PASS;
        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage(
                "Failed to send WebSocket message: " + e.getMessage(),
                LogEventType.Informational, LoggingProfile.STANDARD), e);
            return TankConstants.HTTP_CASE_FAIL;
        }
    }

    /**
     * Execute WebSocket assert action.
     * Validates assertions against collected MessageStream WITHOUT closing the connection.
     * Use this for mid-session validation checkpoints.
     */
    private String executeAssert(String connectionId) throws Exception {
        TankWebSocketClient client = tsc.getWebSocketClient(connectionId);
        if (client == null) {
            LOG.error("WebSocket connection not found for assert: " + connectionId);
            addValidationError("WEBSOCKET_ASSERT", "connectionId", connectionId, "", "WebSocket connection not found for assert");
            return TankConstants.HTTP_CASE_FAIL;
        }

        // Check if connection failed due to fail-on pattern
        if (client.hasFailed()) {
            MessageStream stream = client.getMessageStream();
            String failPattern = stream != null ? stream.getFailurePattern() : "unknown";
            LOG.error(LogUtil.getLogMessage(
                "WebSocket connection " + connectionId + " has failed due to fail-on pattern: " +
                failPattern,
                LogEventType.Validation, LoggingProfile.STANDARD));
            addValidationError("WEBSOCKET_FAIL_ON", "failOnPattern", failPattern,
                stream != null ? stream.getFailureMessage() : "", "Connection failed due to fail-on pattern");
            return TankConstants.HTTP_CASE_FAIL;
        }

        AssertionBlock assertions = step.getAssertions();
        if (assertions == null || assertions.isEmpty()) {
            LOG.warn("No assertions defined for websocket-assert step on connection: " + connectionId);
            return TankConstants.HTTP_CASE_PASS;
        }

        MessageStream stream = client.getMessageStream();
        if (stream == null) {
            LOG.error("No MessageStream found for connection: " + connectionId);
            addValidationError("WEBSOCKET_ASSERT", "messageStream", "present", "missing",
                "No MessageStream found for connection");
            return TankConstants.HTTP_CASE_FAIL;
        }

        return evaluateAssertions(assertions, stream, connectionId);
    }

    /**
     * Execute WebSocket disconnect action.
     * Runs assertions against collected MessageStream BEFORE closing the connection.
     */
    private String executeDisconnect(String connectionId) throws Exception {
        TankWebSocketClient client = tsc.getWebSocketClient(connectionId);
        if (client == null) {
            LOG.warn("WebSocket connection not found for disconnect: " + connectionId);
            return TankConstants.HTTP_CASE_PASS; // Not an error if already disconnected
        }

        String result = TankConstants.HTTP_CASE_PASS;

        try {
            // Get MessageStream for assertions and summary
            MessageStream stream = client.getMessageStream();

            // Debug: Log fail state before checking
            LOG.info(LogUtil.getLogMessage(
                "DISCONNECT checking fail state for " + connectionId + ": hasFailed=" + client.hasFailed() +
                ", streamFailed=" + (stream != null ? stream.hasFailed() : "null") +
                ", failPattern=" + (stream != null ? stream.getFailurePattern() : "null"),
                LogEventType.Informational, LoggingProfile.STANDARD));

            // Check if connection failed due to fail-on pattern
            if (client.hasFailed()) {
                String failPattern = stream != null ? stream.getFailurePattern() : "unknown";
                LOG.error(LogUtil.getLogMessage(
                    "WebSocket connection " + connectionId + " failed due to fail-on pattern: " +
                    failPattern,
                    LogEventType.Validation, LoggingProfile.STANDARD));
                addValidationError("WEBSOCKET_FAIL_ON", "failOnPattern", failPattern,
                    stream != null ? stream.getFailureMessage() : "", "Disconnect detected failed WebSocket connection");
                result = TankConstants.HTTP_CASE_FAIL;
            }

            // Run assertions BEFORE closing (only if not already failed)
            if (result.equals(TankConstants.HTTP_CASE_PASS)) {
                AssertionBlock assertions = step.getAssertions();
                if (assertions != null && !assertions.isEmpty()) {
                    result = evaluateAssertions(assertions, stream, connectionId);
                }
            }

            // Log stream summary
            if (stream != null) {
                LOG.info(LogUtil.getLogMessage(
                    stream.getSummary(),
                    LogEventType.Informational, LoggingProfile.STANDARD));
            }

            // Disconnect and cleanup
            client.disconnect();
            tsc.removeWebSocketClient(connectionId);

            LOG.info(LogUtil.getLogMessage(
                "WebSocket disconnected: " + connectionId + " (result: " + result + ")",
                LogEventType.Informational, LoggingProfile.STANDARD));

            return result;

        } catch (Exception e) {
            LOG.error(LogUtil.getLogMessage(
                "Failed to disconnect WebSocket: " + e.getMessage(),
                LogEventType.Informational, LoggingProfile.STANDARD), e);
            return TankConstants.HTTP_CASE_FAIL;
        }
    }

    /**
     * Evaluate assertions against the collected MessageStream.
     * Called at DISCONNECT or explicit ASSERT action.
     *
     * @param assertions The assertion block containing expects and saves
     * @param stream The MessageStream with collected messages
     * @param connectionId The connection ID for logging
     * @return PASS if all assertions pass, FAIL otherwise
     */
    private String evaluateAssertions(AssertionBlock assertions, MessageStream stream, String connectionId) {
        if (assertions == null || assertions.isEmpty()) {
            LOG.debug("No assertions to evaluate for connection: {}", connectionId);
            return TankConstants.HTTP_CASE_PASS;
        }

        if (stream == null) {
            LOG.error("Cannot evaluate assertions: MessageStream is null for connection: {}", connectionId);
            return TankConstants.HTTP_CASE_FAIL;
        }

        LOG.info(LogUtil.getLogMessage(
            "Evaluating assertions for connection " + connectionId + " (" + stream.getMessageCount() + " messages collected)",
            LogEventType.Informational, LoggingProfile.STANDARD));

        // Evaluate all EXPECT assertions
        for (WebSocketAssertion expect : assertions.getExpects()) {
            String pattern = expect.getPattern();
            boolean isRegex = expect.isRegex();
            int minCount = expect.getEffectiveMinCount();
            Integer maxCount = expect.getMaxCount();

            // Substitute variables in pattern
            String evaluatedPattern = variables.evaluate(pattern);

            int actualCount = stream.countMatching(evaluatedPattern, isRegex);

            // Check minCount
            if (actualCount < minCount) {
                String reason = "Expected pattern '" + evaluatedPattern + "' at least " + minCount +
                    " time(s), found " + actualCount;
                LOG.error(LogUtil.getLogMessage(
                    "Assertion FAILED: expected pattern '" + evaluatedPattern + "' at least " + minCount +
                    " time(s), but found " + actualCount + " match(es) in " + stream.getMessageCount() + " messages",
                    LogEventType.Validation, LoggingProfile.STANDARD));
                addValidationError("WEBSOCKET_ASSERT", evaluatedPattern, String.valueOf(minCount),
                    String.valueOf(actualCount), reason);
                logCollectedMessagesOnFailure(stream);
                return TankConstants.HTTP_CASE_FAIL;
            }

            // Check maxCount if specified
            if (maxCount != null && actualCount > maxCount) {
                String reason = "Expected pattern '" + evaluatedPattern + "' at most " + maxCount +
                    " time(s), found " + actualCount;
                LOG.error(LogUtil.getLogMessage(
                    "Assertion FAILED: expected pattern '" + evaluatedPattern + "' at most " + maxCount +
                    " time(s), but found " + actualCount + " match(es)",
                    LogEventType.Validation, LoggingProfile.STANDARD));
                addValidationError("WEBSOCKET_ASSERT", evaluatedPattern, String.valueOf(maxCount),
                    String.valueOf(actualCount), reason);
                logCollectedMessagesOnFailure(stream);
                return TankConstants.HTTP_CASE_FAIL;
            }

            LOG.info(LogUtil.getLogMessage(
                "Assertion PASSED: pattern '" + evaluatedPattern + "' found " + actualCount + " time(s)" +
                (minCount > 1 ? " (min: " + minCount + ")" : "") +
                (maxCount != null ? " (max: " + maxCount + ")" : ""),
                LogEventType.Validation, LoggingProfile.VERBOSE));
        }

        // Process all SAVE assertions
        for (WebSocketAssertion save : assertions.getSaves()) {
            String pattern = save.getPattern();
            String variableName = save.getVariable();
            SaveOccurrence occurrence = save.getOccurrence();

            if (StringUtils.isEmpty(variableName)) {
                LOG.warn("Save assertion has no variable name specified, skipping");
                continue;
            }

            // Substitute variables in pattern
            String evaluatedPattern = variables.evaluate(pattern);

            // Extract based on occurrence (default to LAST)
            Optional<String> extracted;
            if (occurrence == SaveOccurrence.FIRST) {
                extracted = stream.extractFirst(evaluatedPattern);
            } else {
                extracted = stream.extractLast(evaluatedPattern);
            }

            if (extracted.isPresent()) {
                String value = extracted.get();
                variables.addVariable(variableName, value, true);
                LOG.info(LogUtil.getLogMessage(
                    "Saved value to variable #{" + variableName + "}: " +
                    MessageStream.truncateForLog(value),
                    LogEventType.Informational, LoggingProfile.STANDARD));
            } else {
                LOG.warn(LogUtil.getLogMessage(
                    "Save assertion: pattern '" + evaluatedPattern + "' not found, variable #{" +
                    variableName + "} not set",
                    LogEventType.Informational, LoggingProfile.STANDARD));
            }
        }

        LOG.info(LogUtil.getLogMessage(
            "All assertions PASSED for connection: " + connectionId,
            LogEventType.Validation, LoggingProfile.STANDARD));
        return TankConstants.HTTP_CASE_PASS;
    }

    /**
     * Log collected messages when an assertion fails (for debugging)
     */
    private void logCollectedMessagesOnFailure(MessageStream stream) {
        List<MessageStream.TimestampedMessage> messages = stream.getAllMessages();
        int count = messages.size();
        int maxToLog = Math.min(count, 20);  // Log at most 20 messages

        LOG.error("=== Collected messages ({} total, showing first {}) ===", count, maxToLog);
        for (int i = 0; i < maxToLog; i++) {
            MessageStream.TimestampedMessage msg = messages.get(i);
            LOG.error("  [{}] ({}ms): {}", msg.index(), msg.relativeTimeMs(),
                MessageStream.truncateForLog(msg.content()));
        }
        if (count > maxToLog) {
            LOG.error("  ... and {} more messages", count - maxToLog);
        }
        LOG.error("=== End of collected messages ===");
    }

    private void addValidationError(String location, String key, String expected, String actual, String reason) {
        ValidationData original = new ValidationData();
        original.setKey(StringUtils.defaultString(key));
        original.setCondition("EQUALS");
        original.setValue(StringUtils.defaultString(expected));
        original.setPhase(RequestDataPhase.POST_REQUEST);

        ValidationData interpreted = original.copy();
        interpreted.setValue(StringUtils.defaultString(actual));

        tsc.addError(new ErrorContainer(location, original, interpreted, reason));
    }

    private String extractConnectError(Throwable throwable) {
        Throwable root = throwable;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        return StringUtils.defaultIfEmpty(root.getMessage(), root.getClass().getSimpleName());
    }

    private void cleanupWebSocketClient(String connectionId, TankWebSocketClient client) {
        if (client != null) {
            try {
                client.disconnect();
            } catch (Exception e) {
                LOG.debug("Ignoring disconnect error while cleaning up websocket client for {}", connectionId, e);
            }
        }
        tsc.removeWebSocketClient(connectionId);
    }

}

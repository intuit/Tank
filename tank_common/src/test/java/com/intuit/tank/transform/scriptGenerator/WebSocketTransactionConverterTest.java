package com.intuit.tank.transform.scriptGenerator;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.intuit.tank.conversation.WebSocketMessage;
import com.intuit.tank.conversation.WebSocketTransaction;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link WebSocketTransactionConverter}.
 * Verifies conversion from proxy-recorded WebSocketTransaction objects
 * to ScriptStep objects suitable for the agent's WebSocketRunner.
 */
class WebSocketTransactionConverterTest {

    private static final String TEST_URL = "ws://example.com/socket";
    private static final String TEST_URL_SECURE = "wss://secure.example.com/socket";

    @Test
    @DisplayName("Single WS transaction with no messages produces CONNECT and DISCONNECT steps")
    void convertTransaction_noMessages_producesConnectAndDisconnect() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        assertEquals(2, steps.size(), "Expected CONNECT + DISCONNECT");
        assertRequestDataValue(steps.get(0), "ws-action", "CONNECT");
        assertRequestDataValue(steps.get(0), "ws-url", TEST_URL);
        assertRequestDataContainsKey(steps.get(0), "ws-connection-id");
        assertRequestDataValue(steps.get(1), "ws-action", "DISCONNECT");
    }

    @Test
    @DisplayName("CONNECT step has correct type and name")
    void convertTransaction_connectStep_hasCorrectTypeAndName() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        ScriptStep connectStep = steps.get(0);
        assertEquals("websocket", connectStep.getType());
        assertNotNull(connectStep.getName());
        assertTrue(connectStep.getName().contains("CONNECT"), "Name should contain action");
    }

    @Test
    @DisplayName("Connection ID is consistent across CONNECT, SEND, DISCONNECT")
    void convertTransaction_connectionId_consistentAcrossSteps() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "hello".getBytes(StandardCharsets.UTF_8), System.currentTimeMillis()));

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        String connectId = getRequestDataValue(steps.get(0), "ws-connection-id");
        String sendId = getRequestDataValue(steps.get(1), "ws-connection-id");
        String disconnectId = getRequestDataValue(steps.get(2), "ws-connection-id");

        assertNotNull(connectId);
        assertEquals(connectId, sendId);
        assertEquals(connectId, disconnectId);
    }

    @Test
    @DisplayName("Client messages produce SEND steps with payload")
    void convertTransaction_clientMessages_produceSendSteps() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "hello server".getBytes(StandardCharsets.UTF_8), 1000L));
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "second message".getBytes(StandardCharsets.UTF_8), 2000L));

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        assertEquals(4, steps.size());
        assertRequestDataValue(steps.get(1), "ws-action", "SEND");
        assertRequestDataValue(steps.get(1), "ws-payload", "hello server");
        assertRequestDataValue(steps.get(1), "ws-url", TEST_URL);
        assertRequestDataValue(steps.get(2), "ws-action", "SEND");
        assertRequestDataValue(steps.get(2), "ws-payload", "second message");
    }

    @Test
    @DisplayName("Server messages are ignored (only client messages become SEND steps)")
    void convertTransaction_serverMessages_areIgnored() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);
        tx.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT,
                "server response".getBytes(StandardCharsets.UTF_8), 1000L));

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        assertEquals(2, steps.size());
        assertRequestDataValue(steps.get(0), "ws-action", "CONNECT");
        assertRequestDataValue(steps.get(1), "ws-action", "DISCONNECT");
    }

    @Test
    @DisplayName("Mixed client and server messages produce SEND steps only for client messages")
    void convertTransaction_mixedMessages_onlyClientMessagesBecomeSendSteps() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "request 1".getBytes(StandardCharsets.UTF_8), 1000L));
        tx.addMessage(new WebSocketMessage(false, WebSocketMessage.Type.TEXT,
                "response 1".getBytes(StandardCharsets.UTF_8), 1500L));
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "request 2".getBytes(StandardCharsets.UTF_8), 2000L));

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        assertEquals(4, steps.size());
        assertRequestDataValue(steps.get(1), "ws-payload", "request 1");
        assertRequestDataValue(steps.get(2), "ws-payload", "request 2");
    }

    @Test
    @DisplayName("Step indices are sequential starting from startIndex")
    void convertTransaction_stepIndices_areSequentialFromStartIndex() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "msg".getBytes(StandardCharsets.UTF_8), 1000L));

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 5);

        assertEquals(5, steps.get(0).getStepIndex());
        assertEquals(6, steps.get(1).getStepIndex());
        assertEquals(7, steps.get(2).getStepIndex());
    }

    @Test
    @DisplayName("Secure WebSocket URL (wss://) is preserved in ws-url")
    void convertTransaction_secureUrl_isPreserved() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL_SECURE);

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        assertRequestDataValue(steps.get(0), "ws-url", TEST_URL_SECURE);
    }

    @Test
    @DisplayName("Client message with empty payload produces SEND step with empty ws-payload")
    void convertTransaction_emptyPayload_producesSendWithEmptyPayload() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);
        tx.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "".getBytes(StandardCharsets.UTF_8), 1000L));

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        assertEquals(3, steps.size());
        assertRequestDataValue(steps.get(1), "ws-action", "SEND");
        assertRequestDataValue(steps.get(1), "ws-payload", "");
    }

    @Test
    @DisplayName("Batch conversion of multiple transactions produces correctly indexed steps")
    void convertTransactions_multipleTransactions_producesCorrectlyIndexedSteps() {
        WebSocketTransaction tx1 = new WebSocketTransaction(TEST_URL);
        tx1.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "msg1".getBytes(StandardCharsets.UTF_8), 1000L));

        WebSocketTransaction tx2 = new WebSocketTransaction(TEST_URL_SECURE);
        tx2.addMessage(new WebSocketMessage(true, WebSocketMessage.Type.TEXT,
                "msg2".getBytes(StandardCharsets.UTF_8), 2000L));

        List<ScriptStep> steps = WebSocketTransactionConverter.convertAll(
                List.of(tx1, tx2), 0);

        assertEquals(6, steps.size());
        assertEquals(0, steps.get(0).getStepIndex());
        assertEquals(3, steps.get(3).getStepIndex());
        assertRequestDataValue(steps.get(3), "ws-url", TEST_URL_SECURE);
    }

    @Test
    @DisplayName("Batch conversion with empty list produces empty result")
    void convertTransactions_emptyList_producesEmptyResult() {
        List<ScriptStep> steps = WebSocketTransactionConverter.convertAll(List.of(), 0);
        assertTrue(steps.isEmpty());
    }

    @Test
    @DisplayName("Steps have a WebSocket script group name")
    void convertTransaction_steps_haveScriptGroupName() {
        WebSocketTransaction tx = new WebSocketTransaction(TEST_URL);

        List<ScriptStep> steps = WebSocketTransactionConverter.convert(tx, 0);

        for (ScriptStep step : steps) {
            assertNotNull(step.getScriptGroupName());
            assertTrue(step.getScriptGroupName().startsWith("WebSocket"));
        }
    }

    // --- Helper methods ---

    private void assertRequestDataValue(ScriptStep step, String key, String expectedValue) {
        String actual = getRequestDataValue(step, key);
        assertNotNull(actual, "Expected RequestData with key '" + key + "' but not found");
        assertEquals(expectedValue, actual, "RequestData value mismatch for key '" + key + "'");
    }

    private void assertRequestDataContainsKey(ScriptStep step, String key) {
        assertNotNull(getRequestDataValue(step, key),
                "Expected RequestData with key '" + key + "' but not found");
    }

    private String getRequestDataValue(ScriptStep step, String key) {
        Set<RequestData> data = step.getData();
        if (data == null) return null;
        for (RequestData rd : data) {
            if (key.equals(rd.getKey())) {
                return rd.getValue();
            }
        }
        return null;
    }
}

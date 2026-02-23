package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.fife.ui.rtextarea.RTextArea;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import com.intuit.tank.harness.data.RequestStep;
import com.intuit.tank.harness.data.WebSocketAction;
import com.intuit.tank.harness.data.WebSocketRequest;
import com.intuit.tank.harness.data.WebSocketStep;
import com.intuit.tank.http.BaseRequest;
import com.intuit.tank.http.BaseResponse;

@DisabledIfEnvironmentVariable(named = "SKIP_GUI_TEST", matches = "true")
public class RequestResponsePanelTest {

    private static AgentDebuggerFrame frame;
    private static RequestResponsePanel panel;

    @BeforeAll
    public static void setup() {
        frame = new AgentDebuggerFrame(true, null, null);
        panel = frame.getRequestResponsePanel();
    }

    @AfterAll
    public static void cleanup() {
        panel = null;
        frame = null;
    }

    @Test
    public void testWebSocketShowsSentReceivedAndPassFail() throws Exception {
        DebugStep webSocketDebugStep = buildWebSocketDebugStep(
                "conn-primary", "ws://localhost:8080/ws/test", "hello-payload", false,
                "entry-msg", "exit-msg");

        runOnEdt(() -> panel.stepChanged(webSocketDebugStep));

        JLabel requestLabel = getField("requestLabel", JLabel.class);
        JLabel responseLabel = getField("responseLabel", JLabel.class);
        RTextArea responseTA = getField("responseTA", RTextArea.class);
        JToggleButton sentViewTB = getField("sentViewTB", JToggleButton.class);
        JToggleButton receivedViewTB = getField("receivedViewTB", JToggleButton.class);
        JToggleButton beforeStepTB = getField("beforeStepTB", JToggleButton.class);

        assertEquals("WebSocket Step:", requestLabel.getText());
        assertEquals("Received (After Step):", responseLabel.getText());
        assertTrue(responseTA.getText().contains("Fail-on Status: Pass"));
        assertTrue(responseTA.getText().contains("exit-msg"));

        runOnEdt(sentViewTB::doClick);
        assertEquals("Sent (This Step):", responseLabel.getText());
        assertTrue(responseTA.getText().contains("Payload:\nhello-payload"));
        assertFalse(beforeStepTB.isEnabled());

        runOnEdt(receivedViewTB::doClick);
        runOnEdt(beforeStepTB::doClick);
        assertEquals("Received (Before Step):", responseLabel.getText());
        assertTrue(responseTA.getText().contains("entry-msg"));
    }

    @Test
    public void testMixedHttpAndWebSocketStepSwitchesBackToRequestResponse() throws Exception {
        DebugStep webSocketDebugStep = buildWebSocketDebugStep(
                "conn-secondary", "ws://localhost:8080/ws/test", "mixed-payload", true,
                "before-ws", "after-ws");
        DebugStep httpDebugStep = buildHttpDebugStep("HTTP REQUEST LOG", "HTTP RESPONSE LOG");

        runOnEdt(() -> panel.stepChanged(webSocketDebugStep));
        JPanel webSocketControls = getField("webSocketControls", JPanel.class);
        RTextArea responseTA = getField("responseTA", RTextArea.class);
        assertTrue(webSocketControls.isVisible());
        assertTrue(responseTA.getText().contains("Fail-on Status: Fail"));

        runOnEdt(() -> panel.stepChanged(httpDebugStep));

        JLabel requestLabel = getField("requestLabel", JLabel.class);
        JLabel responseLabel = getField("responseLabel", JLabel.class);
        RTextArea requestTA = getField("requestTA", RTextArea.class);

        assertFalse(webSocketControls.isVisible());
        assertEquals("Request:", requestLabel.getText());
        assertEquals("Response:", responseLabel.getText());
        assertEquals("HTTP REQUEST LOG", requestTA.getText());
        assertEquals("HTTP RESPONSE LOG", responseTA.getText());
    }

    private static DebugStep buildWebSocketDebugStep(
            String connectionId,
            String url,
            String payload,
            boolean failed,
            String entryMessage,
            String exitMessage) {
        WebSocketRequest request = new WebSocketRequest();
        request.setUrl(url);
        request.setPayload(payload);

        WebSocketStep step = new WebSocketStep();
        step.setName("ws-step");
        step.setAction(WebSocketAction.SEND);
        step.setConnectionId(connectionId);
        step.setRequest(request);

        DebugStep debugStep = new DebugStep(step);

        DebugStep.MessageStreamSnapshot entrySnapshot = new DebugStep.MessageStreamSnapshot();
        entrySnapshot.setConnectionId(connectionId);
        entrySnapshot.setConnected(true);
        entrySnapshot.setFailed(false);
        entrySnapshot.setMessageCount(1);
        entrySnapshot.setElapsedTimeMs(5);
        entrySnapshot.setMessages(java.util.List.of(createMessage(0, 1L, entryMessage)));

        DebugStep.MessageStreamSnapshot exitSnapshot = new DebugStep.MessageStreamSnapshot();
        exitSnapshot.setConnectionId(connectionId);
        exitSnapshot.setConnected(true);
        exitSnapshot.setFailed(failed);
        exitSnapshot.setFailurePattern(failed ? "poison" : null);
        exitSnapshot.setFailureMessage(failed ? "matched poison" : null);
        exitSnapshot.setMessageCount(2);
        exitSnapshot.setElapsedTimeMs(10);
        exitSnapshot.setMessages(java.util.List.of(
                createMessage(0, 1L, entryMessage),
                createMessage(1, 6L, exitMessage)));

        Map<String, DebugStep.MessageStreamSnapshot> entryMap = new LinkedHashMap<String, DebugStep.MessageStreamSnapshot>();
        entryMap.put(connectionId, entrySnapshot);
        Map<String, DebugStep.MessageStreamSnapshot> exitMap = new LinkedHashMap<String, DebugStep.MessageStreamSnapshot>();
        exitMap.put(connectionId, exitSnapshot);

        debugStep.setEntryMessageStreams(entryMap);
        debugStep.setExitMessageStreams(exitMap);
        return debugStep;
    }

    private static DebugStep buildHttpDebugStep(String requestLog, String responseLog) {
        RequestStep requestStep = new RequestStep();
        requestStep.setName("http-step");

        DebugStep debugStep = new DebugStep(requestStep);
        debugStep.setRequest(new StubRequest(requestLog));
        debugStep.setResponse(new StubResponse(responseLog));
        return debugStep;
    }

    private static DebugStep.MessageSnapshot createMessage(int index, long relativeTimeMs, String content) {
        DebugStep.MessageSnapshot message = new DebugStep.MessageSnapshot();
        message.setIndex(index);
        message.setRelativeTimeMs(relativeTimeMs);
        message.setTimestamp(System.currentTimeMillis());
        message.setContent(content);
        return message;
    }

    private static void runOnEdt(Runnable runnable) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            SwingUtilities.invokeAndWait(runnable);
        }
    }

    private static <T> T getField(String fieldName, Class<T> type) throws Exception {
        Field field = RequestResponsePanel.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        return type.cast(field.get(panel));
    }

    private static class StubRequest extends BaseRequest {
        private final String log;

        StubRequest(String log) {
            super(null, null);
            this.log = log;
        }

        @Override
        public String getLogMsg() {
            return log;
        }

        @Override
        public void setKey(String key, String value) {
            // no-op
        }

        @Override
        public String getKey(String key) {
            return null;
        }

        @Override
        public void setNamespace(String name, String value) {
            // no-op
        }
    }

    private static class StubResponse extends BaseResponse {
        private final String log;

        StubResponse(String log) {
            this.log = log;
        }

        @Override
        public String getValue(String key) {
            return null;
        }

        @Override
        public String getLogMsg() {
            return log;
        }
    }
}

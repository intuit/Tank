package com.intuit.tank.script;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.util.Messages;

public class WebSocketStepEditorTest {

    private WebSocketStepEditor editor;
    private ScriptEditor scriptEditor;
    private Messages messages;

    @BeforeEach
    public void setUp() throws Exception {
        editor = new WebSocketStepEditor();
        scriptEditor = Mockito.mock(ScriptEditor.class);
        messages = Mockito.mock(Messages.class);

        injectField(editor, "scriptEditor", scriptEditor);
        injectField(editor, "messages", messages);
    }

    @Test
    public void testAddToScript_DoesNotInsertWhenValidationFails() {
        editor.setAction("CONNECT");
        editor.setUrl("   ");

        editor.addToScript();

        verify(messages).error(anyString());
        verify(scriptEditor, never()).insert(Mockito.any(ScriptStep.class));
    }

    @Test
    public void testAddAssertStep_DoesNotInsertWithoutConnection() {
        editor.setAction("ASSERT");
        editor.addExpectEntry();
        editor.getExpectEntries().get(0).setPattern("connected");

        editor.addToScript();

        verify(messages).error(anyString());
        verify(scriptEditor, never()).insert(Mockito.any(ScriptStep.class));
    }

    @Test
    public void testAddAssertStep_DoesNotInsertWithoutAssertions() {
        editor.setAction("ASSERT");
        editor.setSelectedConnectionId("conn-1");

        editor.addToScript();

        verify(messages).error(anyString());
        verify(scriptEditor, never()).insert(Mockito.any(ScriptStep.class));
    }

    @Test
    public void testGetAvailableConnections_UsesWsConnectionIdNotComments() {
        String url = "ws://localhost/socket";
        ScriptStep connect = createWebSocketStep("WS_CONNECT", url, "real-conn", "user-note-connect");
        ScriptStep disconnect = createWebSocketStep("WS_DISCONNECT", url, "real-conn", "different-user-note");

        List<ScriptStep> steps = new ArrayList<>();
        steps.add(connect);
        steps.add(disconnect);
        when(scriptEditor.getSteps()).thenReturn(steps);

        List<String> available = editor.getAvailableConnections();

        assertFalse(available.contains(url));
    }

    @Test
    public void testAddSendStep_ResolvesConnectionIdFromWsConnectionId() {
        String url = "ws://localhost/send";
        ScriptStep connect = createWebSocketStep("WS_CONNECT", url, "real-conn", "user-friendly-note");

        List<ScriptStep> steps = new ArrayList<>();
        steps.add(connect);
        when(scriptEditor.getSteps()).thenReturn(steps);

        editor.setAction("SEND");
        editor.setUrl(url);
        editor.setPayload("payload");

        editor.addToScript();

        ArgumentCaptor<ScriptStep> captor = ArgumentCaptor.forClass(ScriptStep.class);
        verify(scriptEditor).insert(captor.capture());

        ScriptStep inserted = captor.getValue();
        assertEquals("real-conn", getDataValue(inserted, "ws-connection-id"));
        assertEquals("real-conn", inserted.getComments());
    }

    @Test
    public void testAddAssertStep_UsesSelectedConnectionAndPersistsAssertions() {
        String url = "ws://localhost/assert";
        ScriptStep connect = createWebSocketStep("WS_CONNECT", url, "conn-assert", "legacy");

        List<ScriptStep> steps = new ArrayList<>();
        steps.add(connect);
        when(scriptEditor.getSteps()).thenReturn(steps);

        editor.setAction("ASSERT");
        editor.setSelectedConnectionId("conn-assert");

        editor.addExpectEntry();
        WebSocketStepEditor.ExpectAssertionEntry expect = editor.getExpectEntries().get(0);
        expect.setPattern("ready");
        expect.setRegex(true);
        expect.setMinCount("1");

        editor.addSaveEntry();
        WebSocketStepEditor.SaveAssertionEntry save = editor.getSaveEntries().get(0);
        save.setPattern("id:(\\d+)");
        save.setVariable("lastId");
        save.setOccurrence("last");

        editor.addToScript();

        ArgumentCaptor<ScriptStep> captor = ArgumentCaptor.forClass(ScriptStep.class);
        verify(scriptEditor).insert(captor.capture());
        ScriptStep inserted = captor.getValue();

        assertEquals("conn-assert", getDataValue(inserted, "ws-connection-id"));
        assertEquals("assert", getDataValue(inserted, "ws-action"));
        assertEquals("ready", getDataValue(inserted, "ws-assert-expect.0.pattern"));
        assertEquals("true", getDataValue(inserted, "ws-assert-expect.0.regex"));
        assertEquals("1", getDataValue(inserted, "ws-assert-expect.0.min"));
        assertEquals("id:(\\d+)", getDataValue(inserted, "ws-assert-save.0.pattern"));
        assertEquals("lastId", getDataValue(inserted, "ws-assert-save.0.variable"));
    }

    @Test
    public void testAddConnectStep_PersistsFailOnPatterns() {
        editor.setAction("CONNECT");
        editor.setUrl("ws://localhost/connect");
        editor.setTimeoutMs("1200");

        editor.addFailOnEntry();
        WebSocketStepEditor.FailOnEntry entry = editor.getFailOnEntries().get(0);
        entry.setPattern("error");
        entry.setRegex(true);

        editor.addToScript();

        ArgumentCaptor<ScriptStep> captor = ArgumentCaptor.forClass(ScriptStep.class);
        verify(scriptEditor).insert(captor.capture());
        ScriptStep inserted = captor.getValue();

        assertEquals("error", getDataValue(inserted, "ws-fail-on.0.pattern"));
        assertEquals("true", getDataValue(inserted, "ws-fail-on.0.regex"));
        assertEquals("1200", getDataValue(inserted, "ws-timeout-ms"));
        assertTrue(getDataValue(inserted, "ws-connection-id").length() > 0);
    }

    private ScriptStep createWebSocketStep(String method, String url, String connectionId, String comments) {
        ScriptStep step = new ScriptStep();
        step.setType("websocket");
        step.setMethod(method);
        step.setComments(comments);

        Set<RequestData> data = new HashSet<>();
        data.add(createRequestData("ws-url", url));
        data.add(createRequestData("ws-connection-id", connectionId));
        step.setData(data);
        return step;
    }

    private RequestData createRequestData(String key, String value) {
        RequestData data = new RequestData();
        data.setType("websocket");
        data.setKey(key);
        data.setValue(value);
        return data;
    }

    private String getDataValue(ScriptStep step, String key) {
        for (RequestData data : step.getData()) {
            if (key.equals(data.getKey())) {
                return data.getValue();
            }
        }
        return null;
    }

    private void injectField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

package com.intuit.tank.script;

import static com.intuit.tank.util.ButtonLabel.ADD_LABEL;
import static com.intuit.tank.util.ButtonLabel.EDIT_LABEL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.util.Messages;

/**
 * Editor bean for creating and editing WebSocket steps.
 */
@Named
@ConversationScoped
public class WebSocketStepEditor implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(WebSocketStepEditor.class);

    private static final String ACTION_CONNECT = "CONNECT";
    private static final String ACTION_SEND = "SEND";
    private static final String ACTION_ASSERT = "ASSERT";
    private static final String ACTION_DISCONNECT = "DISCONNECT";

    // WebSocket constants
    private static final String WEBSOCKET = "websocket";
    private static final String WEBSOCKET_ACTION = "ws-action";
    private static final String WEBSOCKET_URL = "ws-url";
    private static final String WEBSOCKET_CONNECTION_ID = "ws-connection-id";
    private static final String WEBSOCKET_PAYLOAD = "ws-payload";
    private static final String WEBSOCKET_TIMEOUT_MS = "ws-timeout-ms";
    private static final String WEBSOCKET_FAIL_ON_PREFIX = "ws-fail-on.";
    private static final String WEBSOCKET_ASSERT_EXPECT_PREFIX = "ws-assert-expect.";
    private static final String WEBSOCKET_ASSERT_SAVE_PREFIX = "ws-assert-save.";

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private ScriptStep step;
    private boolean editMode;

    private String action = ACTION_CONNECT;
    private String url = "";         // For CONNECT/SEND: input URL
    private String payload = "";     // For SEND: message payload
    private String selectedConnectionId = "";
    private String timeoutMs = "";

    private List<FailOnEntry> failOnEntries = new ArrayList<>();
    private List<ExpectAssertionEntry> expectEntries = new ArrayList<>();
    private List<SaveAssertionEntry> saveEntries = new ArrayList<>();

    private String buttonLabel = ADD_LABEL;

    public void insertWebSocket() {
        this.step = null;
        this.editMode = false;
        resetFields();
        buttonLabel = ADD_LABEL;
    }

    public void editWebSocket(ScriptStep existingStep) {
        this.step = existingStep;
        this.editMode = true;
        this.buttonLabel = EDIT_LABEL;

        // Extract existing step data for editing
        RequestData actionData = findData(existingStep, WEBSOCKET_ACTION);
        if (actionData != null) {
            action = actionData.getValue().toUpperCase();
        }

        // All WebSocket steps now store URL in ws-url field
        RequestData urlData = findData(existingStep, WEBSOCKET_URL);
        url = urlData != null ? urlData.getValue() : "";

        RequestData payloadData = findData(existingStep, WEBSOCKET_PAYLOAD);
        payload = payloadData != null ? payloadData.getValue() : "";

        RequestData timeoutData = findData(existingStep, WEBSOCKET_TIMEOUT_MS);
        timeoutMs = timeoutData != null ? timeoutData.getValue() : "";

        selectedConnectionId = StringUtils.defaultString(getStepConnectionId(existingStep));
        if ((ACTION_ASSERT.equals(action) || ACTION_DISCONNECT.equals(action)) && StringUtils.isBlank(url)) {
            url = getUrlForConnectionId(selectedConnectionId);
        }

        loadAdvancedData(existingStep);
    }

    public void addToScript() {
        if (!validate()) {
            return;
        }

        if (editMode && step != null) {
            applyToExistingStep();
        } else {
            createNewStep();
        }
        resetFields();
    }

    private void createNewStep() {
        ScriptStep newStep;
        Integer timeout = parseTimeoutMs();
        if (ACTION_CONNECT.equals(action)) {
            String connId = generateUniqueConnectionId(url);
            newStep = ScriptStepFactory.createWebSocketConnect(connId, url, timeout);
            LOG.info("Created CONNECT step with connectionId={}, url={}", connId, url);
        } else if (ACTION_SEND.equals(action)) {
            String connId = getConnectionIdForUrl(url);
            newStep = ScriptStepFactory.createWebSocketSend(connId, url, payload, timeout);
            LOG.info("Created SEND step with connectionId={}, url={}", connId, url);
        } else if (ACTION_ASSERT.equals(action)) {
            String connId = selectedConnectionId;
            String selectedUrl = getUrlForConnectionId(connId);
            newStep = ScriptStepFactory.createWebSocketAssert(connId, selectedUrl);
            LOG.info("Created ASSERT step with connectionId={}, url={}", connId, selectedUrl);
        } else {
            String connId = selectedConnectionId;
            String selectedUrl = getUrlForConnectionId(connId);
            newStep = ScriptStepFactory.createWebSocketDisconnect(connId, selectedUrl);
            LOG.info("Created DISCONNECT step with connectionId={}, url={}", connId, selectedUrl);
        }

        applyAdvancedData(newStep);
        scriptEditor.insert(newStep);
    }

    private void applyToExistingStep() {
        String connId;
        String resolvedUrl = url;
        Integer timeout = parseTimeoutMs();

        if (ACTION_CONNECT.equals(action)) {
            step.setMethod("WS_CONNECT");
            connId = getStepConnectionId(step);
            if (StringUtils.isBlank(connId)) {
                connId = generateUniqueConnectionId(url);
            }
        } else if (ACTION_SEND.equals(action)) {
            step.setMethod("WS_SEND");
            connId = getConnectionIdForUrl(url);
        } else if (ACTION_ASSERT.equals(action)) {
            step.setMethod("WS_ASSERT");
            connId = selectedConnectionId;
            resolvedUrl = getUrlForConnectionId(connId);
        } else {
            step.setMethod("WS_DISCONNECT");
            connId = selectedConnectionId;
            resolvedUrl = getUrlForConnectionId(connId);
        }

        step.setComments(connId); // Compatibility mirror only

        if (step.getData() == null) {
            step.setData(new HashSet<RequestData>());
        }

        updateData(step, WEBSOCKET_ACTION, action.toLowerCase());
        updateData(step, WEBSOCKET_CONNECTION_ID, connId);

        if (ACTION_CONNECT.equals(action)) {
            updateData(step, WEBSOCKET_URL, url);
            removeData(step, WEBSOCKET_PAYLOAD);
            if (timeout != null) {
                updateData(step, WEBSOCKET_TIMEOUT_MS, timeout.toString());
            } else {
                removeData(step, WEBSOCKET_TIMEOUT_MS);
            }
        } else if (ACTION_SEND.equals(action)) {
            updateData(step, WEBSOCKET_URL, url);
            updateData(step, WEBSOCKET_PAYLOAD, payload);
            if (timeout != null) {
                updateData(step, WEBSOCKET_TIMEOUT_MS, timeout.toString());
            } else {
                removeData(step, WEBSOCKET_TIMEOUT_MS);
            }
        } else {
            updateData(step, WEBSOCKET_URL, resolvedUrl);
            removeData(step, WEBSOCKET_PAYLOAD);
            removeData(step, WEBSOCKET_TIMEOUT_MS);
        }

        applyAdvancedData(step);

        ScriptUtil.updateStepLabel(step);
    }

    private boolean validate() {
        if (!validateTimeoutMs()) {
            return false;
        }

        if (ACTION_CONNECT.equals(action)) {
            if (StringUtils.isBlank(url)) {
                messages.error("WebSocket URL is required for CONNECT");
                return false;
            }
            return validateFailOnEntries();
        }

        if (ACTION_SEND.equals(action)) {
            if (StringUtils.isBlank(url)) {
                messages.error("WebSocket URL is required for SEND");
                return false;
            }
            return true;
        }

        if (ACTION_ASSERT.equals(action)) {
            if (StringUtils.isBlank(selectedConnectionId)) {
                messages.error("Connection selection is required for ASSERT");
                return false;
            }
            if (!validateAssertionEntries()) {
                return false;
            }
            if (!hasAssertionRows()) {
                messages.error("At least one assertion is required for ASSERT");
                return false;
            }
            return true;
        }

        if (ACTION_DISCONNECT.equals(action)) {
            if (StringUtils.isBlank(selectedConnectionId)) {
                messages.error("Connection selection is required for DISCONNECT");
                return false;
            }
            return validateAssertionEntries();
        }

        messages.error("Unsupported WebSocket action: " + action);
        return false;
    }

    private void resetFields() {
        this.action = ACTION_CONNECT;
        this.url = "";
        this.payload = "";
        this.selectedConnectionId = "";
        this.timeoutMs = "";
        this.failOnEntries = new ArrayList<>();
        this.expectEntries = new ArrayList<>();
        this.saveEntries = new ArrayList<>();
        this.buttonLabel = ADD_LABEL;
    }

    private RequestData findData(ScriptStep step, String key) {
        if (step.getData() == null) {
            return null;
        }
        for (RequestData rd : step.getData()) {
            if (key.equals(rd.getKey())) {
                return rd;
            }
        }
        return null;
    }

    private void updateData(ScriptStep step, String key, String value) {
        RequestData data = findData(step, key);
        if (data == null) {
            data = new RequestData();
            data.setType(WEBSOCKET);
            data.setKey(key);
            step.getData().add(data);
        }
        data.setValue(value);
    }

    private void removeData(ScriptStep step, String key) {
        if (step.getData() == null) {
            return;
        }
        step.getData().removeIf(rd -> key.equals(rd.getKey()));
    }

    private void removeDataByPrefix(ScriptStep step, String prefix) {
        if (step.getData() == null) {
            return;
        }
        step.getData().removeIf(rd -> rd.getKey() != null && rd.getKey().startsWith(prefix));
    }

    private Integer parseTimeoutMs() {
        if (StringUtils.isBlank(timeoutMs)) {
            return null;
        }
        return Integer.valueOf(timeoutMs.trim());
    }

    private boolean validateTimeoutMs() {
        if (StringUtils.isBlank(timeoutMs)) {
            return true;
        }
        try {
            Integer value = Integer.valueOf(timeoutMs.trim());
            if (value < 0) {
                messages.error("Timeout must be 0 or greater");
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            messages.error("Timeout must be a valid integer");
            return false;
        }
    }

    private boolean validateFailOnEntries() {
        for (FailOnEntry entry : getFailOnEntries()) {
            if (entry.isRegex() && StringUtils.isBlank(entry.getPattern())) {
                messages.error("Fail-on pattern is required when regex is enabled");
                return false;
            }
        }
        return true;
    }

    private boolean validateAssertionEntries() {
        for (ExpectAssertionEntry entry : getExpectEntries()) {
            boolean hasConfiguration = entry.isRegex()
                    || StringUtils.isNotBlank(entry.getMinCount())
                    || StringUtils.isNotBlank(entry.getMaxCount());
            if (hasConfiguration && StringUtils.isBlank(entry.getPattern())) {
                messages.error("Expect assertion pattern is required");
                return false;
            }
            if (!validateCountPair(entry.getMinCount(), entry.getMaxCount(), "Expect assertion")) {
                return false;
            }
        }

        for (SaveAssertionEntry entry : getSaveEntries()) {
            boolean hasConfiguration = entry.isRegex()
                    || StringUtils.isNotBlank(entry.getVariable())
                    || StringUtils.isNotBlank(entry.getOccurrence());
            if (hasConfiguration && StringUtils.isBlank(entry.getPattern())) {
                messages.error("Save assertion pattern is required");
                return false;
            }
            if (StringUtils.isNotBlank(entry.getPattern()) && StringUtils.isBlank(entry.getVariable())) {
                messages.error("Save assertion variable is required");
                return false;
            }
        }
        return true;
    }

    private boolean validateCountPair(String minValue, String maxValue, String context) {
        Integer min = null;
        Integer max = null;

        if (StringUtils.isNotBlank(minValue)) {
            try {
                min = Integer.valueOf(minValue.trim());
                if (min < 0) {
                    messages.error(context + " min count must be 0 or greater");
                    return false;
                }
            } catch (NumberFormatException e) {
                messages.error(context + " min count must be a valid integer");
                return false;
            }
        }

        if (StringUtils.isNotBlank(maxValue)) {
            try {
                max = Integer.valueOf(maxValue.trim());
                if (max < 0) {
                    messages.error(context + " max count must be 0 or greater");
                    return false;
                }
            } catch (NumberFormatException e) {
                messages.error(context + " max count must be a valid integer");
                return false;
            }
        }

        if (min != null && max != null && min > max) {
            messages.error(context + " min count cannot be greater than max count");
            return false;
        }

        return true;
    }

    private boolean hasAssertionRows() {
        for (ExpectAssertionEntry entry : getExpectEntries()) {
            if (StringUtils.isNotBlank(entry.getPattern())) {
                return true;
            }
        }
        for (SaveAssertionEntry entry : getSaveEntries()) {
            if (StringUtils.isNotBlank(entry.getPattern())) {
                return true;
            }
        }
        return false;
    }

    private void applyAdvancedData(ScriptStep targetStep) {
        if (targetStep.getData() == null) {
            targetStep.setData(new HashSet<RequestData>());
        }

        removeDataByPrefix(targetStep, WEBSOCKET_FAIL_ON_PREFIX);
        removeDataByPrefix(targetStep, WEBSOCKET_ASSERT_EXPECT_PREFIX);
        removeDataByPrefix(targetStep, WEBSOCKET_ASSERT_SAVE_PREFIX);

        if (ACTION_CONNECT.equals(action)) {
            int index = 0;
            for (FailOnEntry entry : getFailOnEntries()) {
                if (StringUtils.isBlank(entry.getPattern())) {
                    continue;
                }
                updateData(targetStep, WEBSOCKET_FAIL_ON_PREFIX + index + ".pattern", entry.getPattern().trim());
                updateData(targetStep, WEBSOCKET_FAIL_ON_PREFIX + index + ".regex", Boolean.toString(entry.isRegex()));
                index++;
            }
            return;
        }

        if (!ACTION_ASSERT.equals(action) && !ACTION_DISCONNECT.equals(action)) {
            return;
        }

        int expectIndex = 0;
        for (ExpectAssertionEntry entry : getExpectEntries()) {
            if (StringUtils.isBlank(entry.getPattern())) {
                continue;
            }
            updateData(targetStep, WEBSOCKET_ASSERT_EXPECT_PREFIX + expectIndex + ".pattern", entry.getPattern().trim());
            updateData(targetStep, WEBSOCKET_ASSERT_EXPECT_PREFIX + expectIndex + ".regex", Boolean.toString(entry.isRegex()));
            if (StringUtils.isNotBlank(entry.getMinCount())) {
                updateData(targetStep, WEBSOCKET_ASSERT_EXPECT_PREFIX + expectIndex + ".min", entry.getMinCount().trim());
            }
            if (StringUtils.isNotBlank(entry.getMaxCount())) {
                updateData(targetStep, WEBSOCKET_ASSERT_EXPECT_PREFIX + expectIndex + ".max", entry.getMaxCount().trim());
            }
            expectIndex++;
        }

        int saveIndex = 0;
        for (SaveAssertionEntry entry : getSaveEntries()) {
            if (StringUtils.isBlank(entry.getPattern())) {
                continue;
            }
            updateData(targetStep, WEBSOCKET_ASSERT_SAVE_PREFIX + saveIndex + ".pattern", entry.getPattern().trim());
            updateData(targetStep, WEBSOCKET_ASSERT_SAVE_PREFIX + saveIndex + ".regex", Boolean.toString(entry.isRegex()));
            updateData(targetStep, WEBSOCKET_ASSERT_SAVE_PREFIX + saveIndex + ".variable", StringUtils.defaultString(entry.getVariable()).trim());
            String occurrence = StringUtils.defaultIfBlank(entry.getOccurrence(), "last");
            updateData(targetStep, WEBSOCKET_ASSERT_SAVE_PREFIX + saveIndex + ".occurrence", occurrence.trim().toLowerCase());
            saveIndex++;
        }
    }

    private void loadAdvancedData(ScriptStep existingStep) {
        failOnEntries = new ArrayList<>();
        expectEntries = new ArrayList<>();
        saveEntries = new ArrayList<>();

        if (existingStep.getData() == null) {
            return;
        }

        Map<Integer, FailOnEntry> failOnByIndex = new HashMap<>();
        Map<Integer, ExpectAssertionEntry> expectByIndex = new HashMap<>();
        Map<Integer, SaveAssertionEntry> saveByIndex = new HashMap<>();

        for (RequestData data : existingStep.getData()) {
            String key = data.getKey();
            if (StringUtils.isBlank(key)) {
                continue;
            }

            IndexedKey indexedKey = parseIndexedKey(key, WEBSOCKET_FAIL_ON_PREFIX);
            if (indexedKey != null) {
                FailOnEntry entry = failOnByIndex.computeIfAbsent(indexedKey.getIndex(), idx -> new FailOnEntry());
                if ("pattern".equals(indexedKey.getField())) {
                    entry.setPattern(data.getValue());
                } else if ("regex".equals(indexedKey.getField())) {
                    entry.setRegex(Boolean.parseBoolean(data.getValue()));
                }
                continue;
            }

            indexedKey = parseIndexedKey(key, WEBSOCKET_ASSERT_EXPECT_PREFIX);
            if (indexedKey != null) {
                ExpectAssertionEntry entry = expectByIndex.computeIfAbsent(indexedKey.getIndex(), idx -> new ExpectAssertionEntry());
                if ("pattern".equals(indexedKey.getField())) {
                    entry.setPattern(data.getValue());
                } else if ("regex".equals(indexedKey.getField())) {
                    entry.setRegex(Boolean.parseBoolean(data.getValue()));
                } else if ("min".equals(indexedKey.getField())) {
                    entry.setMinCount(data.getValue());
                } else if ("max".equals(indexedKey.getField())) {
                    entry.setMaxCount(data.getValue());
                }
                continue;
            }

            indexedKey = parseIndexedKey(key, WEBSOCKET_ASSERT_SAVE_PREFIX);
            if (indexedKey != null) {
                SaveAssertionEntry entry = saveByIndex.computeIfAbsent(indexedKey.getIndex(), idx -> new SaveAssertionEntry());
                if ("pattern".equals(indexedKey.getField())) {
                    entry.setPattern(data.getValue());
                } else if ("regex".equals(indexedKey.getField())) {
                    entry.setRegex(Boolean.parseBoolean(data.getValue()));
                } else if ("variable".equals(indexedKey.getField())) {
                    entry.setVariable(data.getValue());
                } else if ("occurrence".equals(indexedKey.getField())) {
                    entry.setOccurrence(data.getValue());
                }
            }
        }

        failOnEntries = mapToOrderedList(failOnByIndex);
        expectEntries = mapToOrderedList(expectByIndex);
        saveEntries = mapToOrderedList(saveByIndex);
    }

    private IndexedKey parseIndexedKey(String key, String prefix) {
        if (!key.startsWith(prefix)) {
            return null;
        }
        String remaining = key.substring(prefix.length());
        int dotIndex = remaining.indexOf('.');
        if (dotIndex <= 0 || dotIndex >= remaining.length() - 1) {
            return null;
        }
        String indexValue = remaining.substring(0, dotIndex);
        String field = remaining.substring(dotIndex + 1);
        if (!StringUtils.isNumeric(indexValue)) {
            return null;
        }
        return new IndexedKey(Integer.parseInt(indexValue), field);
    }

    private <T> List<T> mapToOrderedList(Map<Integer, T> map) {
        if (map.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> indexes = new ArrayList<>(map.keySet());
        Collections.sort(indexes);
        List<T> ordered = new ArrayList<>();
        for (Integer index : indexes) {
            ordered.add(map.get(index));
        }
        return ordered;
    }

    private String generateUniqueConnectionId(String wsUrl) {
        String baseId = generateConnectionIdFromUrl(wsUrl);
        
        // Check for collisions with existing WebSocket steps in the script
        Set<String> existingIds = getExistingWebSocketConnectionIds();
        
        if (!existingIds.contains(baseId)) {
            return baseId; // No collision, use base ID
        }
        
        // Handle collision by appending a number
        int counter = 2;
        String uniqueId;
        do {
            uniqueId = baseId + "_" + counter;
            counter++;
        } while (existingIds.contains(uniqueId));
        
        return uniqueId;
    }

    private String generateConnectionIdFromUrl(String wsUrl) {
        if (StringUtils.isBlank(wsUrl)) {
            return "connection";
        }
        
        try {
            // Extract meaningful parts from URL
            // ws://example.com/chat/room123 -> "chat_room123"
            // wss://api.example.com:8080/notifications -> "notifications"
            
            String url = wsUrl.replaceFirst("^wss?://", ""); // Remove protocol
            String[] parts = url.split("/");
            
            if (parts.length > 1) {
                // Use path parts: example.com/chat/room123 -> "chat_room123"
                StringBuilder pathId = new StringBuilder();
                for (int i = 1; i < parts.length; i++) {
                    if (pathId.length() > 0) pathId.append("_");
                    pathId.append(parts[i].replaceAll("[^a-zA-Z0-9]", ""));
                }
                return pathId.length() > 0 ? pathId.toString() : "connection";
            } else {
                // Use hostname: example.com -> "example_com"
                return parts[0].split(":")[0].replaceAll("[^a-zA-Z0-9]", "_");
            }
        } catch (Exception e) {
            // Fallback to simple hash
            return "connection_" + Math.abs(wsUrl.hashCode() % 10000);
        }
    }

    private Set<String> getExistingWebSocketConnectionIds() {
        Set<String> existingIds = new HashSet<>();
        
        if (scriptEditor != null && scriptEditor.getSteps() != null) {
            for (ScriptStep step : scriptEditor.getSteps()) {
                if ("websocket".equals(step.getType())) {
                    String connectionId = getStepConnectionId(step);
                    if (StringUtils.isNotBlank(connectionId)) {
                        existingIds.add(connectionId);
                    }
                }
            }
        }
        
        return existingIds;
    }

    private Map<String, String> getActiveConnectionsById() {
        Map<String, String> connectedUrlsById = new HashMap<>();
        Set<String> disconnectedIds = new HashSet<>();

        if (scriptEditor == null || scriptEditor.getSteps() == null) {
            return connectedUrlsById;
        }

        for (ScriptStep scriptStep : scriptEditor.getSteps()) {
            if (!"websocket".equals(scriptStep.getType())) {
                continue;
            }

            String method = scriptStep.getMethod();
            String connectionId = getStepConnectionId(scriptStep);
            if (StringUtils.isBlank(connectionId)) {
                continue;
            }

            if ("WS_CONNECT".equals(method)) {
                RequestData urlData = findData(scriptStep, WEBSOCKET_URL);
                if (urlData != null && StringUtils.isNotBlank(urlData.getValue())) {
                    connectedUrlsById.put(connectionId, urlData.getValue());
                }
            } else if ("WS_DISCONNECT".equals(method)) {
                disconnectedIds.add(connectionId);
            }
        }

        for (String disconnectedId : disconnectedIds) {
            connectedUrlsById.remove(disconnectedId);
        }

        return connectedUrlsById;
    }

    /**
     * Returns active connection URLs for compatibility with existing callers.
     */
    public List<String> getAvailableConnections() {
        List<String> urls = new ArrayList<>(getActiveConnectionsById().values());
        Collections.sort(urls);
        return urls;
    }

    public List<SelectItem> getAvailableConnectionOptions() {
        Map<String, String> activeConnections = getActiveConnectionsById();
        List<String> connectionIds = new ArrayList<>(activeConnections.keySet());
        Collections.sort(connectionIds);

        List<SelectItem> options = new ArrayList<>();
        for (String connectionId : connectionIds) {
            String connectionUrl = activeConnections.get(connectionId);
            options.add(new SelectItem(connectionId, connectionId + " - " + connectionUrl));
        }

        if (StringUtils.isNotBlank(selectedConnectionId) && !activeConnections.containsKey(selectedConnectionId)) {
            String fallbackUrl = getUrlForConnectionId(selectedConnectionId);
            if (StringUtils.isBlank(fallbackUrl)) {
                fallbackUrl = "unknown";
            }
            options.add(new SelectItem(selectedConnectionId, selectedConnectionId + " - " + fallbackUrl));
        }

        return options;
    }
    
    /**
     * Find the connectionId for a given WebSocket URL.
     * SEND remains manual URL entry, so this resolves the id from existing connect steps when possible.
     */
    private String getConnectionIdForUrl(String targetUrl) {
        if (StringUtils.isBlank(targetUrl) || scriptEditor == null || scriptEditor.getSteps() == null) {
            LOG.warn("Cannot find connectionId - targetUrl is blank or no steps available");
            return null;
        }
        
        // Find the most recent CONNECT step with matching URL
        for (int i = scriptEditor.getSteps().size() - 1; i >= 0; i--) {
            ScriptStep step = scriptEditor.getSteps().get(i);
            if ("websocket".equals(step.getType()) && "WS_CONNECT".equals(step.getMethod())) {
                RequestData urlData = findData(step, WEBSOCKET_URL);
                if (urlData != null && targetUrl.equals(urlData.getValue())) {
                    // Found matching URL, return ws-connection-id first, then comments (legacy)
                    String connectionId = getStepConnectionId(step);
                    LOG.info("Found connectionId={} for url={}", connectionId, targetUrl);
                    return connectionId;
                }
            }
        }
        
        // Log available connections for debugging
        StringBuilder availableConnections = new StringBuilder();
        for (ScriptStep step : scriptEditor.getSteps()) {
            if ("websocket".equals(step.getType()) && "WS_CONNECT".equals(step.getMethod())) {
                RequestData urlData = findData(step, WEBSOCKET_URL);
                if (urlData != null) {
                    availableConnections.append(urlData.getValue()).append(", ");
                }
            }
        }
        LOG.warn("No connectionId found for url={}, available connections: [{}]", 
                 targetUrl, availableConnections.toString());
        
        return null;
    }
    
    /**
     * Find the URL for a given connectionId.
     * Used when editing SEND/DISCONNECT steps to populate the URL field.
     */
    private String getUrlForConnectionId(String connectionId) {
        if (StringUtils.isBlank(connectionId) || scriptEditor == null || scriptEditor.getSteps() == null) {
            return "";
        }
        
        // Find the CONNECT step with matching connectionId
        for (ScriptStep step : scriptEditor.getSteps()) {
            if ("websocket".equals(step.getType()) && 
                "WS_CONNECT".equals(step.getMethod()) && 
                connectionId.equals(getStepConnectionId(step))) {
                RequestData urlData = findData(step, WEBSOCKET_URL);
                if (urlData != null) {
                    return urlData.getValue();
                }
            }
        }
        
        return "";
    }

    public String getUrlForSelectedConnection() {
        return getUrlForConnectionId(selectedConnectionId);
    }

    private String getStepConnectionId(ScriptStep scriptStep) {
        RequestData connIdData = findData(scriptStep, WEBSOCKET_CONNECTION_ID);
        if (connIdData != null && StringUtils.isNotBlank(connIdData.getValue())) {
            return connIdData.getValue();
        }
        return scriptStep.getComments();
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getSelectedConnectionId() {
        return selectedConnectionId;
    }

    public void setSelectedConnectionId(String selectedConnectionId) {
        this.selectedConnectionId = selectedConnectionId;
    }

    public String getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(String timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public List<FailOnEntry> getFailOnEntries() {
        if (failOnEntries == null) {
            failOnEntries = new ArrayList<>();
        }
        return failOnEntries;
    }

    public void addFailOnEntry() {
        getFailOnEntries().add(new FailOnEntry());
    }

    public void removeFailOnEntry(int index) {
        if (index >= 0 && index < getFailOnEntries().size()) {
            getFailOnEntries().remove(index);
        }
    }

    public List<ExpectAssertionEntry> getExpectEntries() {
        if (expectEntries == null) {
            expectEntries = new ArrayList<>();
        }
        return expectEntries;
    }

    public void addExpectEntry() {
        getExpectEntries().add(new ExpectAssertionEntry());
    }

    public void removeExpectEntry(int index) {
        if (index >= 0 && index < getExpectEntries().size()) {
            getExpectEntries().remove(index);
        }
    }

    public List<SaveAssertionEntry> getSaveEntries() {
        if (saveEntries == null) {
            saveEntries = new ArrayList<>();
        }
        return saveEntries;
    }

    public void addSaveEntry() {
        getSaveEntries().add(new SaveAssertionEntry());
    }

    public void removeSaveEntry(int index) {
        if (index >= 0 && index < getSaveEntries().size()) {
            getSaveEntries().remove(index);
        }
    }

    public String getButtonLabel() {
        return buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }

    public static class FailOnEntry {
        private String pattern;
        private boolean regex;

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public boolean isRegex() {
            return regex;
        }

        public void setRegex(boolean regex) {
            this.regex = regex;
        }
    }

    public static class ExpectAssertionEntry {
        private String pattern;
        private boolean regex;
        private String minCount;
        private String maxCount;

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public boolean isRegex() {
            return regex;
        }

        public void setRegex(boolean regex) {
            this.regex = regex;
        }

        public String getMinCount() {
            return minCount;
        }

        public void setMinCount(String minCount) {
            this.minCount = minCount;
        }

        public String getMaxCount() {
            return maxCount;
        }

        public void setMaxCount(String maxCount) {
            this.maxCount = maxCount;
        }
    }

    public static class SaveAssertionEntry {
        private String pattern;
        private boolean regex;
        private String variable;
        private String occurrence = "last";

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public boolean isRegex() {
            return regex;
        }

        public void setRegex(boolean regex) {
            this.regex = regex;
        }

        public String getVariable() {
            return variable;
        }

        public void setVariable(String variable) {
            this.variable = variable;
        }

        public String getOccurrence() {
            return occurrence;
        }

        public void setOccurrence(String occurrence) {
            this.occurrence = occurrence;
        }
    }

    private static class IndexedKey {
        private final int index;
        private final String field;

        private IndexedKey(int index, String field) {
            this.index = index;
            this.field = field;
        }

        private int getIndex() {
            return index;
        }

        private String getField() {
            return field;
        }
    }
}

package com.intuit.tank.script;

import static com.intuit.tank.util.ButtonLabel.ADD_LABEL;
import static com.intuit.tank.util.ButtonLabel.EDIT_LABEL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.ConversationScoped;
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
    private static final String ACTION_DISCONNECT = "DISCONNECT";

    // WebSocket constants
    private static final String WEBSOCKET = "websocket";
    private static final String WEBSOCKET_ACTION = "ws-action";
    private static final String WEBSOCKET_URL = "ws-url";
    private static final String WEBSOCKET_CONNECTION_ID = "ws-connection-id";
    private static final String WEBSOCKET_PAYLOAD = "ws-payload";
    private static final String WEBSOCKET_TIMEOUT_MS = "ws-timeout-ms";

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private ScriptStep step;
    private boolean editMode;

    private String action = ACTION_CONNECT;
    private String url = "";         // For CONNECT: input URL; For SEND/DISCONNECT: selected URL from dropdown
    private String payload = "";     // For SEND: message payload

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
        ScriptStep step;
        if (ACTION_CONNECT.equals(action)) {
            // CONNECT: Auto-generate connection ID from URL
            String connId = generateUniqueConnectionId(url);
            step = ScriptStepFactory.createWebSocketConnect(connId, url, null);
            LOG.info("Created CONNECT step with connectionId={}, url={}", connId, url);
        } else if (ACTION_SEND.equals(action)) {
            // SEND: Find connectionId for the selected URL
            String connId = getConnectionIdForUrl(url);
            step = ScriptStepFactory.createWebSocketSend(connId, url, payload, null);
            LOG.info("Created SEND step with connectionId={}, url={}", connId, url);
        } else {
            // DISCONNECT: Find connectionId for the selected URL
            String connId = getConnectionIdForUrl(url);
            step = ScriptStepFactory.createWebSocketDisconnect(connId, url);
            LOG.info("Created DISCONNECT step with connectionId={}, url={}", connId, url);
        }
        
        scriptEditor.insert(step);
    }

    private void applyToExistingStep() {
        String connId;

        // Set method based on action
        if (ACTION_CONNECT.equals(action)) {
            step.setMethod("WS_CONNECT");
            connId = getStepConnectionId(step);
            if (StringUtils.isBlank(connId)) {
                connId = generateUniqueConnectionId(url);
            }
            step.setComments(connId); // Compatibility mirror only
        } else if (ACTION_SEND.equals(action)) {
            step.setMethod("WS_SEND");
            connId = getConnectionIdForUrl(url);
            step.setComments(connId);  // Compatibility mirror only
        } else {
            step.setMethod("WS_DISCONNECT");
            connId = getConnectionIdForUrl(url);
            step.setComments(connId);  // Compatibility mirror only
        }

        if (step.getData() == null) {
            step.setData(new HashSet<RequestData>());
        }

        updateData(step, WEBSOCKET_ACTION, action.toLowerCase());

        if (ACTION_CONNECT.equals(action)) {
            updateData(step, WEBSOCKET_URL, url);
            updateData(step, WEBSOCKET_CONNECTION_ID, connId);
            removeData(step, WEBSOCKET_PAYLOAD);
        } else if (ACTION_SEND.equals(action)) {
            updateData(step, WEBSOCKET_URL, url);  // Add URL for display
            updateData(step, WEBSOCKET_CONNECTION_ID, connId);
            updateData(step, WEBSOCKET_PAYLOAD, payload);
        } else {
            updateData(step, WEBSOCKET_URL, url);  // Add URL for display
            updateData(step, WEBSOCKET_CONNECTION_ID, connId);
            removeData(step, WEBSOCKET_PAYLOAD);
        }

        ScriptUtil.updateStepLabel(step);
    }

    private boolean validate() {
        boolean valid = true;

        if (ACTION_CONNECT.equals(action)) {
            // URL is required for CONNECT
            if (StringUtils.isBlank(url)) {
                messages.error("WebSocket URL is required for CONNECT");
                valid = false;
            }
        } else if (ACTION_SEND.equals(action)) {
            // URL (connection) required for SEND
            // Payload is optional (allows empty messages for ACKs, pings, etc.)
            if (StringUtils.isBlank(url)) {
                messages.error("WebSocket URL is required for SEND");
                valid = false;
            }
        } else {
            // URL (connection) required for DISCONNECT
            if (StringUtils.isBlank(url)) {
                messages.error("WebSocket URL is required for DISCONNECT");
                valid = false;
            }
        }
        
        return valid;
    }

    private void resetFields() {
        this.action = ACTION_CONNECT;
        this.url = "";
        this.payload = "";
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

    private String findConnectionIdByUrl(String targetUrl) {
        if (StringUtils.isBlank(targetUrl) || scriptEditor == null || scriptEditor.getSteps() == null) {
            return "connection_" + System.currentTimeMillis(); // Fallback
        }
        
        // Find the most recent CONNECT step with matching URL
        for (int i = scriptEditor.getSteps().size() - 1; i >= 0; i--) {
            ScriptStep step = scriptEditor.getSteps().get(i);
            if ("websocket".equals(step.getType()) && "WS_CONNECT".equals(step.getMethod())) {
                RequestData urlData = findData(step, WEBSOCKET_URL);
                if (urlData != null && targetUrl.equals(urlData.getValue())) {
                    // Found matching URL, extract connection ID from ws-connection-id first, then comments (legacy)
                    String connectionId = getStepConnectionId(step);
                    return StringUtils.isNotBlank(connectionId)
                           ? connectionId
                           : generateConnectionIdFromUrl(targetUrl);
                }
            }
        }
        
        // No matching connection found, generate new ID
        return generateConnectionIdFromUrl(targetUrl);
    }

    /**
     * Get list of available WebSocket connections (as URLs) that can be used for SEND/DISCONNECT.
     * Returns URLs that have CONNECT steps but no corresponding DISCONNECT steps.
     */
    public List<String> getAvailableConnections() {
        List<String> availableUrls = new ArrayList<>();
        Map<String, String> connectedUrlsById = new HashMap<>();  // connectionId -> URL
        Set<String> disconnectedIds = new HashSet<>();
        
        if (scriptEditor != null && scriptEditor.getSteps() != null) {
            // Find all CONNECT and DISCONNECT steps
            for (ScriptStep step : scriptEditor.getSteps()) {
                if ("websocket".equals(step.getType())) {
                    String method = step.getMethod();
                    String connectionId = getStepConnectionId(step);
                    
                    if ("WS_CONNECT".equals(method) && StringUtils.isNotBlank(connectionId)) {
                        // Get URL from step data
                        RequestData urlData = findData(step, WEBSOCKET_URL);
                        if (urlData != null && StringUtils.isNotBlank(urlData.getValue())) {
                            connectedUrlsById.put(connectionId, urlData.getValue());
                        }
                    } else if ("WS_DISCONNECT".equals(method) && StringUtils.isNotBlank(connectionId)) {
                        disconnectedIds.add(connectionId);
                    }
                }
            }
            
            // Return URLs for connections that are connected but not yet disconnected
            for (Map.Entry<String, String> entry : connectedUrlsById.entrySet()) {
                String connectionId = entry.getKey();
                String url = entry.getValue();
                if (!disconnectedIds.contains(connectionId) && !availableUrls.contains(url)) {
                    availableUrls.add(url);
                }
            }
        }
        
        availableUrls.sort(String::compareTo); // Sort alphabetically
        return availableUrls;
    }
    
    /**
     * Find the connectionId for a given WebSocket URL.
     * Used when creating SEND/DISCONNECT steps from a selected URL.
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

    private String getStepConnectionId(ScriptStep scriptStep) {
        RequestData connIdData = findData(scriptStep, WEBSOCKET_CONNECTION_ID);
        if (connIdData != null && StringUtils.isNotBlank(connIdData.getValue())) {
            return connIdData.getValue();
        }
        return scriptStep.getComments();
    }

    // ConnectionId removed - now auto-generated internally

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

    public String getButtonLabel() {
        return buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }
}

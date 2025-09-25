package com.intuit.tank.script;

import static com.intuit.tank.util.ButtonLabel.ADD_LABEL;
import static com.intuit.tank.util.ButtonLabel.EDIT_LABEL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.commons.lang3.StringUtils;

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

    private static final String ACTION_CONNECT = "CONNECT";
    private static final String ACTION_DISCONNECT = "DISCONNECT";

    // WebSocket constants
    private static final String WEBSOCKET = "websocket";
    private static final String WEBSOCKET_ACTION = "ws-action";
    private static final String WEBSOCKET_URL = "ws-url";
    private static final String WEBSOCKET_TIMEOUT_MS = "ws-timeout-ms";

    @Inject
    private ScriptEditor scriptEditor;

    @Inject
    private Messages messages;

    private ScriptStep step;
    private boolean editMode;

    private String action = ACTION_CONNECT;
    private String url = "";

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

        RequestData urlData = findData(existingStep, WEBSOCKET_URL);
        url = urlData != null ? urlData.getValue() : "";
    }

    public void addToScript() {
        if (!validate()) {
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
            // Auto-generate connection ID from URL
            String connectionId = generateUniqueConnectionId(url);
            step = ScriptStepFactory.createWebSocketConnect(connectionId, url, null); // No timeout
        } else {
            // DISCONNECT: Find connection by URL matching
            String connectionId = findConnectionIdByUrl(url);
            step = ScriptStepFactory.createWebSocketDisconnect(connectionId);
        }
        
        scriptEditor.insert(step);
    }

    private void applyToExistingStep() {
        // Comments left blank as per design decision
        step.setMethod(action.equals(ACTION_CONNECT) ? "WS_CONNECT" : "WS_DISCONNECT");

        if (step.getData() == null) {
            step.setData(new HashSet<RequestData>());
        }

        updateData(step, WEBSOCKET_ACTION, action.toLowerCase());

        if (ACTION_CONNECT.equals(action)) {
            updateData(step, WEBSOCKET_URL, url);
        } else {
            updateData(step, WEBSOCKET_URL, url); // URL needed for both CONNECT and DISCONNECT
        }

        ScriptUtil.updateStepLabel(step);
    }

    private boolean validate() {
        boolean valid = true;

        // URL is required for both CONNECT and DISCONNECT
        if (StringUtils.isBlank(url)) {
            messages.error("WebSocket URL is required");
            valid = false;
        }
        
        return valid;
    }

    private void resetFields() {
        this.action = ACTION_CONNECT;
        this.url = "";
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
                if ("websocket".equals(step.getType()) && StringUtils.isNotBlank(step.getComments())) {
                    existingIds.add(step.getComments());
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
                    // Found matching URL, extract connection ID from comments or generate one
                    return StringUtils.isNotBlank(step.getComments()) ? 
                           step.getComments() : generateConnectionIdFromUrl(targetUrl);
                }
            }
        }
        
        // No matching connection found, generate new ID
        return generateConnectionIdFromUrl(targetUrl);
    }

    /**
     * Get list of available WebSocket connections that can be disconnected.
     * Returns connections that have CONNECT steps but no corresponding DISCONNECT steps.
     */
    public List<String> getAvailableConnections() {
        List<String> availableConnections = new ArrayList<>();
        Set<String> connectedIds = new HashSet<>();
        Set<String> disconnectedIds = new HashSet<>();
        
        if (scriptEditor != null && scriptEditor.getSteps() != null) {
            // Find all CONNECT and DISCONNECT steps
            for (ScriptStep step : scriptEditor.getSteps()) {
                if ("websocket".equals(step.getType()) && StringUtils.isNotBlank(step.getComments())) {
                    String connectionId = step.getComments();
                    String method = step.getMethod();
                    
                    if ("CONNECT".equals(method)) {
                        connectedIds.add(connectionId);
                    } else if ("DISCONNECT".equals(method)) {
                        disconnectedIds.add(connectionId);
                    }
                }
            }
            
            // Return connections that are connected but not yet disconnected
            for (String connectedId : connectedIds) {
                if (!disconnectedIds.contains(connectedId)) {
                    availableConnections.add(connectedId);
                }
            }
        }
        
        availableConnections.sort(String::compareTo); // Sort alphabetically
        return availableConnections;
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

    // Timeout and RequestName fields removed per requirements

    public String getButtonLabel() {
        return buttonLabel;
    }

    public void setButtonLabel(String buttonLabel) {
        this.buttonLabel = buttonLabel;
    }
}

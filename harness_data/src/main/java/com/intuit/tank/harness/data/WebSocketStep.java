package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * WebSocketStep represents a WebSocket operation in a Tank script.
 */
@XmlType(name = "webSocketStep", propOrder = { 
    "name", "scriptGroupName", "comments", "onFail", "action", "connectionId", 
    "request", "response" 
}, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class WebSocketStep extends TestStep implements FailableStep {

    @XmlAttribute
    private String name;
    
    @XmlAttribute
    private String scriptGroupName;
    
    @XmlAttribute(name = "description")
    private String comments;
    
    @XmlAttribute
    private String onFail;
    
    @XmlAttribute
    private WebSocketAction action;
    
    @XmlAttribute
    private String connectionId;
    
    @XmlElement(name = "request")
    private WebSocketRequest request;
    
    @XmlElement(name = "response")
    private WebSocketResponse response;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the scriptGroupName
     */
    public String getScriptGroupName() {
        return scriptGroupName;
    }

    /**
     * @param scriptGroupName the scriptGroupName to set
     */
    public void setScriptGroupName(String scriptGroupName) {
        this.scriptGroupName = scriptGroupName;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @return the onFail
     */
    public String getOnFail() {
        return onFail;
    }

    /**
     * @param onFail the onFail to set
     */
    public void setOnFail(String onFail) {
        this.onFail = onFail;
    }

    /**
     * @return the action
     */
    public WebSocketAction getAction() {
        return action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(WebSocketAction action) {
        this.action = action;
    }

    /**
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = WebSocketAction.fromValue(action);
    }

    /**
     * @return the connectionId
     */
    public String getConnectionId() {
        return connectionId;
    }

    /**
     * @param connectionId the connectionId to set
     */
    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    /**
     * @return the request
     */
    public WebSocketRequest getRequest() {
        return request;
    }

    /**
     * @param request the request to set
     */
    public void setRequest(WebSocketRequest request) {
        this.request = request;
    }

    /**
     * @return the response
     */
    public WebSocketResponse getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(WebSocketResponse response) {
        this.response = response;
    }

    @Override
    public String getInfo() {
        String actionDesc = action != null ? action.toString().toUpperCase() : "WEBSOCKET";
        String connDesc = connectionId != null ? "(" + connectionId + ")" : "";
        String nameDesc = name != null ? name : "WebSocket Operation";
        return actionDesc + "_" + nameDesc + connDesc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + getInfo();
    }
}

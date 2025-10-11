package com.intuit.tank.script;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.http.AuthScheme;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.ScriptStep;

public class ScriptStepFactory {

    // WebSocket constants (local definitions)
    private static final String WEBSOCKET = "websocket";
    private static final String WEBSOCKET_ACTION = "ws-action";
    private static final String WEBSOCKET_URL = "ws-url";
    private static final String WEBSOCKET_TIMEOUT_MS = "ws-timeout-ms";

    public static ScriptStep createVariable(String key, String value) {
        ScriptStep step = new ScriptStep();
        step.setType(ScriptConstants.VARIABLE);
        RequestData reqData = new RequestData();
        reqData.setKey(key);
        reqData.setValue(value);
        Set<RequestData> set = new HashSet<RequestData>();
        set.add(reqData);
        step.setData(set);
        return step;
    }

    public static ScriptStep createThinkTime(String minTime, String maxTime) {
        ScriptStep think = new ScriptStep();
        think.setType(ScriptConstants.THINK_TIME);

        RequestData minRD = new RequestData();
        minRD.setType(ScriptConstants.THINK_TIME);
        minRD.setKey(ScriptConstants.MIN_TIME);
        minRD.setValue(minTime);

        RequestData maxRD = new RequestData();
        maxRD.setType(ScriptConstants.THINK_TIME);
        maxRD.setKey(ScriptConstants.MAX_TIME);
        maxRD.setValue(maxTime);

        Set<RequestData> ds = new HashSet<RequestData>();
        ds.add(minRD);
        ds.add(maxRD);
        think.setData(ds);
        think.setComments("ThinkTime " + minRD.getValue() + "-" + maxRD.getValue());

        return think;
    }
    
    public static ScriptStep createAuthentication(String userName, String password, String realm, AuthScheme scheme, String host, String port) {
        ScriptStep ret = new ScriptStep();
        ret.setType(ScriptConstants.AUTHENTICATION);
        Set<RequestData> ds = new HashSet<RequestData>();

        RequestData rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_USER_NAME);
        rd.setValue(userName);
        ds.add(rd);
        
        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_PASSWORD);
        rd.setValue(password);
        ds.add(rd);
        
        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_REALM);
        rd.setValue(realm);
        ds.add(rd);
        
        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_SCHEME);
        rd.setValue(scheme.name());
        ds.add(rd);
        
        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_HOST);
        rd.setValue(host);
        ds.add(rd);
        
        rd = new RequestData();
        rd.setType(ScriptConstants.AUTHENTICATION);
        rd.setKey(ScriptConstants.AUTH_PORT);
        rd.setValue(port);
        ds.add(rd);

      
        ret.setData(ds);
        ScriptUtil.updateStepLabel(ret);
        ret.setComments("Authenticator " + scheme.name() + " " + host);
        return ret;
    }
    
    

    public static ScriptStep createSleepTime(String delay) {
        ScriptStep sleep = new ScriptStep();
        sleep.setType(ScriptConstants.SLEEP);
        sleep.setComments("SLEEP " + delay);
        RequestData data = new RequestData();
        data.setType(ScriptConstants.SLEEP);
        data.setKey(ScriptConstants.TIME);
        data.setValue(delay);
        Set<RequestData> ds = new HashSet<RequestData>();
        ds.add(data);
        sleep.setData(ds);
        return sleep;
    }

    public static ScriptStep createLogic(String name, String script) {
        ScriptStep ret = new ScriptStep();
        ret.setType(ScriptConstants.LOGIC);
        ret.setComments("Logic Step: " + name);
        ret.setName(name);
        RequestData data = new RequestData();
        data.setType(ScriptConstants.LOGIC);
        data.setKey(ScriptConstants.SCRIPT);
        data.setValue(script);
        Set<RequestData> ds = new HashSet<RequestData>();
        ds.add(data);
        ret.setData(ds);
        return ret;
    }

    public static ScriptStep createCookie(String name, String value, String domain, String path) {
        ScriptStep ret = new ScriptStep();
        Set<RequestData> ds = new HashSet<RequestData>();
        ret.setData(ds);
        ret.setType(ScriptConstants.COOKIE);
        RequestData data = new RequestData();
        data.setType(ScriptConstants.COOKIE);
        data.setKey(ScriptConstants.COOKIE_NAME);
        data.setValue(name);
        ds.add(data);
        data = new RequestData();
        data.setType(ScriptConstants.COOKIE);
        data.setKey(ScriptConstants.COOKIE_VALUE);
        data.setValue(value);
        ds.add(data);
        data = new RequestData();
        data.setType(ScriptConstants.COOKIE);
        data.setKey(ScriptConstants.COOKIE_DOMAIN);
        data.setValue(domain);
        ds.add(data);
        data = new RequestData();
        data.setType(ScriptConstants.COOKIE);
        data.setKey(ScriptConstants.COOKIE_PATH);
        data.setValue(StringUtils.isNotBlank(path) ? path : "/");
        ds.add(data);
        ret.setComments("Cookie Step: " + name + " = " + value);
        return ret;
    }

    public static ScriptStep createClearCookies() {
        ScriptStep clear = new ScriptStep();
        clear.setType(ScriptConstants.CLEAR);
        // clear.setData(new HashSet<RequestData>().add(arg0))
        clear.setComments("Clear Cookies");
        return clear;
    }

    public static ScriptStep createWebSocketConnect(String connectionId, String url, Integer timeoutMs) {
        ScriptStep step = new ScriptStep();
        step.setType(WEBSOCKET);
        step.setMethod("WS_CONNECT");
        // Store connectionId in comments field so SEND/DISCONNECT can reference it
        step.setComments(connectionId);

        Set<RequestData> data = new HashSet<RequestData>();

        RequestData actionData = new RequestData();
        actionData.setType(WEBSOCKET);
        actionData.setKey(WEBSOCKET_ACTION);
        actionData.setValue("connect");
        data.add(actionData);

        RequestData urlData = new RequestData();
        urlData.setType(WEBSOCKET);
        urlData.setKey(WEBSOCKET_URL);
        urlData.setValue(url);
        data.add(urlData);

        if (timeoutMs != null) {
            RequestData timeoutData = new RequestData();
            timeoutData.setType(WEBSOCKET);
            timeoutData.setKey(WEBSOCKET_TIMEOUT_MS);
            timeoutData.setValue(timeoutMs.toString());
            data.add(timeoutData);
        }

        step.setData(data);
        // Set default name for WebSocket Connect
        step.setName("WebSocket Connect");
        return step;
    }

    public static ScriptStep createWebSocketDisconnect(String connectionId, String url) {
        ScriptStep step = new ScriptStep();
        step.setType(WEBSOCKET);
        step.setMethod("WS_DISCONNECT");
        // Comments left blank as per design decision

        Set<RequestData> data = new HashSet<RequestData>();

        RequestData actionData = new RequestData();
        actionData.setType(WEBSOCKET);
        actionData.setKey(WEBSOCKET_ACTION);
        actionData.setValue("disconnect");
        data.add(actionData);

        // Add URL for UI display
        RequestData urlData = new RequestData();
        urlData.setType(WEBSOCKET);
        urlData.setKey(WEBSOCKET_URL);
        urlData.setValue(url);
        data.add(urlData);

        step.setData(data);
        // Set default name for WebSocket Disconnect
        step.setName("WebSocket Disconnect");
        return step;
    }

    /**
     * Create a WebSocket SEND step.
     * 
     * @param connectionId The connection ID to send the message on
     * @param url The WebSocket URL (for UI display)
     * @param payload The message payload to send
     * @param timeoutMs Optional timeout in milliseconds
     * @return The configured ScriptStep
     */
    public static ScriptStep createWebSocketSend(String connectionId, String url, String payload, Integer timeoutMs) {
        ScriptStep step = new ScriptStep();
        step.setType(WEBSOCKET);
        step.setMethod("WS_SEND");
        // Store connectionId in comments field (used by agent runner)
        step.setComments(connectionId);

        Set<RequestData> data = new HashSet<RequestData>();

        RequestData actionData = new RequestData();
        actionData.setType(WEBSOCKET);
        actionData.setKey(WEBSOCKET_ACTION);
        actionData.setValue("send");
        data.add(actionData);

        // Add URL for UI display
        RequestData urlData = new RequestData();
        urlData.setType(WEBSOCKET);
        urlData.setKey(WEBSOCKET_URL);
        urlData.setValue(url);
        data.add(urlData);

        // Add connectionId as request data (for backend)
        RequestData connIdData = new RequestData();
        connIdData.setType(WEBSOCKET);
        connIdData.setKey("ws-connection-id");
        connIdData.setValue(connectionId);
        data.add(connIdData);

        // Add payload
        RequestData payloadData = new RequestData();
        payloadData.setType(WEBSOCKET);
        payloadData.setKey("ws-payload");
        payloadData.setValue(payload != null ? payload : "");
        data.add(payloadData);

        if (timeoutMs != null) {
            RequestData timeoutData = new RequestData();
            timeoutData.setType(WEBSOCKET);
            timeoutData.setKey(WEBSOCKET_TIMEOUT_MS);
            timeoutData.setValue(timeoutMs.toString());
            data.add(timeoutData);
        }

        step.setData(data);
        // Set default name for WebSocket Send
        step.setName("WebSocket Send");
        return step;
    }

}

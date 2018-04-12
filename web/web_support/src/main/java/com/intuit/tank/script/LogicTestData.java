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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.intuit.tank.common.ScriptUtil;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.ScriptConstants;

/**
 * 
 * LogicTestData
 * 
 * @author dangleton
 * 
 */
public class LogicTestData implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> varMap = new HashMap<String, String>();
    private Map<String, String> requestHeaderMap = new HashMap<String, String>();
    private Map<String, String> responseHeaderMap = new HashMap<String, String>();
    private String requestBody;
    private String responseBody;

    public String newKey;
    public String newValue;
    private Map<String, String> currentMap = new HashMap<String, String>();

    /**
     * 
     * @param step
     */
    public LogicTestData(ScriptStep step, Script script) {
        varMap.put("mode", "test");
        varMap.put("THREAD_ID", Long.toString(Thread.currentThread().getId()));
        for (ScriptStep s : script.getScriptSteps()) {
            for (String key : ScriptUtil.getDeclaredVariables(s).keySet()) {
                if (!varMap.containsKey(key)) {
                    varMap.put(key, "");
                }
            }
        }
        if (step != null) {
            // vars stored in
            for (RequestData rd : step.getData()) {
                if (!rd.getKey().equals(ScriptConstants.SCRIPT) && !rd.getType().equals(ScriptConstants.SCRIPT)) {
                    varMap.put(rd.getKey(), rd.getValue());
                }
            }
            // requestHeaders
            for (RequestData rd : step.getRequestheaders()) {
                requestHeaderMap.put(rd.getKey(), rd.getValue());
            }

            // responseHeaders
            for (RequestData rd : step.getResponseheaders()) {
                responseHeaderMap.put(rd.getKey(), rd.getValue());
            }
            requestBody = step.getPayload();
            if (StringUtils.isBlank(step.getPayload())) {
                requestBody = buildBody(step.getPostDatas());
            }
            responseBody = step.getResponse();
        }
    }

    private String buildBody(Set<RequestData> postDatas) {
        StringBuilder sb = new StringBuilder();
        for (RequestData rd : postDatas) {
            if (sb.length() != 0) {
                sb.append('&');
            }
            try {
                sb.append(URLEncoder.encode(rd.getKey(), "utf8")).append('=')
                        .append(URLEncoder.encode(rd.getValue(), "utf8"));
            } catch (UnsupportedEncodingException e) {
                // never happens
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @param step
     */
    public void setInStep(ScriptStep step) {
        // vars stored in data
        for (Entry<String, String> entry : varMap.entrySet()) {
            step.getData().add(new RequestData(entry.getKey(), entry.getValue(), ScriptConstants.TEST_DATA));
        }
        // requestHeaders
        for (Entry<String, String> entry : requestHeaderMap.entrySet()) {
            step.getRequestheaders().add(new RequestData(entry.getKey(), entry.getValue(), ScriptConstants.TEST_DATA));
        }

        // responseHeaders
        for (Entry<String, String> entry : responseHeaderMap.entrySet()) {
            step.getResponseheaders().add(new RequestData(entry.getKey(), entry.getValue(), ScriptConstants.TEST_DATA));
        }

        step.setPayload(requestBody);
        step.setResponse(responseBody);
    }

    /**
     * @return the varMap
     */
    public Map<String, String> getVarMap() {
        return varMap;
    }

    /**
     * @return the requestHeaderMap
     */
    public Map<String, String> getRequestHeaderMap() {
        return requestHeaderMap;
    }

    /**
     * @return the responseHeaderMap
     */
    public Map<String, String> getResponseHeaderMap() {
        return responseHeaderMap;
    }

    /**
     * @return the varMap
     */
    public List<Map.Entry<String, String>> getVariables() {
        ArrayList<Entry<String, String>> ret = new ArrayList<Map.Entry<String, String>>(varMap.entrySet());
        Collections.sort(ret, new MapEntryComparator());
        return ret;
    }

    /**
     * @param varMap
     *            the varMap to set
     */
    public void addVariable(String key, String value) {
        this.varMap.put(key, value);
    }

    /**
     * @param varMap
     *            the varMap to set
     */
    public void removeVariable(String key) {
        this.varMap.remove(key);
    }

    /**
     * @return the requestHeaderMap
     */
    public List<Map.Entry<String, String>> getRequestHeaders() {
        ArrayList<Entry<String, String>> ret = new ArrayList<Map.Entry<String, String>>(requestHeaderMap.entrySet());
        ret.sort(new MapEntryComparator());
        return ret;
    }

    /**
     * @param varMap
     *            the varMap to set
     */
    public void addRequestHeader(String key, String value) {
        this.requestHeaderMap.put(key, value);
    }

    /**
     * @param varMap
     *            the varMap to set
     */
    public void removeRequestHeader(String key) {
        this.requestHeaderMap.remove(key);
    }

    /**
     * @return the responseHeaderMap
     */
    public List<Map.Entry<String, String>> getResponseHeaders() {
        ArrayList<Entry<String, String>> ret = new ArrayList<Map.Entry<String, String>>(responseHeaderMap.entrySet());
        Collections.sort(ret, new MapEntryComparator());
        return ret;
    }

    /**
     * @param varMap
     *            the varMap to set
     */
    public void addResponseHeader(String key, String value) {
        this.responseHeaderMap.put(key, value);
    }

    /**
     * @param varMap
     *            the varMap to set
     */
    public void removeResponseHeader(String key) {
        this.responseHeaderMap.remove(key);
    }

    /**
     * @return the requestBody
     */
    public String getRequestBody() {
        return requestBody;
    }

    /**
     * @param requestBody
     *            the requestBody to set
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * @return the resonseBody
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * @param resonseBody
     *            the resonseBody to set
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public void newMapValue(Map<String, String> map) {
        newKey = "";
        newValue = "";
        this.currentMap = map;
    }

    public void addMapValue() {
        if (currentMap != null && StringUtils.isNotBlank(newKey) && StringUtils.isNotBlank(newValue)) {
            currentMap.put(newKey.trim(), newValue.trim());
        }
    }

    /**
     * @return the newKey
     */
    public String getNewKey() {
        return newKey;
    }

    /**
     * @param newKey
     *            the newKey to set
     */
    public void setNewKey(String newKey) {
        this.newKey = newKey;
    }

    /**
     * @return the newValue
     */
    public String getNewValue() {
        return newValue;
    }

    /**
     * @param newValue
     *            the newValue to set
     */
    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    private static class MapEntryComparator implements Comparator<Entry<String, String>> {

        @Override
        public int compare(Entry<String, String> o1, Entry<String, String> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }

    }

}

package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.intuit.tank.script.RequestDataType;

import org.apache.commons.lang3.StringUtils;

public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    private String uuid = UUID.randomUUID().toString();

    private String method;

    private String type = "request";

    private String label;

    private String url;

    private String result;

    private String mimetype;

    private String loggingKey;

    private String name;

    private String onFail = "abort";

    private int stepIndex;

    private String simplePath; // path without query string

    private String hostname;

    private String protocol;

    private String comments;

    private String respFormat;

    private String reqFormat = "nvp";

    private String response;

    private String payload;

    private Set<RequestData> requestheaders = new HashSet<RequestData>();

    private Set<RequestData> responseheaders = new HashSet<RequestData>();

    private Set<RequestData> requestCookies = new HashSet<RequestData>(); // sent cookies

    private Set<RequestData> responseCookies = new HashSet<RequestData>(); // received cookies

    private Set<RequestData> postDatas = new HashSet<RequestData>();

    private Set<RequestData> queryStrings = new HashSet<RequestData>();

    private Set<RequestData> data = new HashSet<RequestData>();

    private Set<RequestData> responseData = new HashSet<RequestData>();

    /**
     * @return the payload
     */
    public String getPayload() {
        return payload;
    }

    /**
     * @param payload
     *            the payload to set
     */
    public void setPayload(String payload) {
        this.payload = payload;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @return the loggingKey
     */
    public String getLoggingKey() {
        return loggingKey;
    }

    /**
     * @param loggingKey
     *            the loggingKey to set
     */
    public void setLoggingKey(String loggingKey) {
        this.loggingKey = loggingKey;
    }

    /**
     * @param uuid
     *            the uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Set<RequestData> getData() {
        return data;
    }

    public void setData(Set<RequestData> data) {
        this.data = data;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.setLabel(url);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMimetype() {
        return mimetype;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }

    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOnFail() {
        return onFail != null ? onFail : "abort";
    }

    public void setOnFail(String onFail) {
        this.onFail = onFail;
    }

    public Set<RequestData> getRequestheaders() {
        return requestheaders;
    }

    public void setRequestheaders(Set<RequestData> requestheaders) {
        this.requestheaders = requestheaders;
    }

    public Set<RequestData> getResponseheaders() {
        return responseheaders;
    }

    public void setResponseheaders(Set<RequestData> responseheaders) {
        this.responseheaders = responseheaders;
    }

    public Set<RequestData> getRequestCookies() {
        return requestCookies;
    }

    public void setRequestCookies(Set<RequestData> cookies) {
        this.requestCookies = cookies;
    }

    public Set<RequestData> getResponseCookies() {
        return responseCookies;
    }

    public void setResponseCookies(Set<RequestData> setCookies) {
        this.responseCookies = setCookies;
    }

    public Set<RequestData> getPostDatas() {
        return postDatas;
    }

    public void setPostDatas(Set<RequestData> postDatas) {
        this.postDatas = postDatas;
    }

    public Set<RequestData> getQueryStrings() {
        return queryStrings;
    }

    public void setQueryStrings(Set<RequestData> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public int getStepIndex() {
        return stepIndex;
    }

    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    public String getSimplePath() {
        return StringUtils.trim(simplePath);
    }

    public void setSimplePath(String simplePath) {
        this.simplePath = simplePath;
    }

    public String getHostname() {
        return StringUtils.trim(hostname);
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Set<RequestData> getResponseData() {
        return responseData;
    }

    public void setResponseData(Set<RequestData> responseData) {
        this.responseData = responseData;
    }

    public String getRespFormat() {
        return respFormat;
    }

    public void setRespFormat(String respFormat) {
        this.respFormat = respFormat;
    }

    public String getReqFormat() {
        return reqFormat;
    }

    public void setReqFormat(String reqFormat) {
        this.reqFormat = reqFormat;
    }

    public boolean isHasValidation() {
        return responseData.stream().anyMatch(data -> !isAssignment(data));
    }

    public boolean isHasAssignments() {
        return responseData.stream().anyMatch(Request::isAssignment);
    }

    /**
     * @param data
     * @return
     */
    private static boolean isAssignment(RequestData data) {
        if (data.getType().equals(RequestDataType.bodyAssignment.name())
                || data.getType().equals(RequestDataType.cookieAssignment.name())
                || data.getType().equals(RequestDataType.headerAssignment.name())
                || (data.getType().equals("responseData") && data.getValue() != null
                        && data.getValue().startsWith("=") && !data.getValue().startsWith("=="))) {
            return true;
        }
        return false;
    }
}

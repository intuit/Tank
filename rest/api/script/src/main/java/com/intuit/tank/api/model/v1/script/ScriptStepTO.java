package com.intuit.tank.api.model.v1.script;

/*
 * #%L
 * Script Rest API
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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "scriptStep", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ScriptStepTO", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "uuid",
        "created",
        "modified",
        "method",
        "scriptGroupName",
        "loggingKey",
        "type",
        "label",
        "url",
        "result",
        "mimetype",
        "name",
        "onFail",
        "stepIndex",
        "simplePath",
        "hostname",
        "protocol",
        "payload",
        "comments",
        "respFormat",
        "reqFormat",
        "requestheaders",
        "responseheaders",
        "requestCookies",
        "responseCookies",
        "postDatas",
        "queryStrings",
        "response",
        "data",
        "responseData"
})
public class ScriptStepTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "uuid", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String uuid;

    @XmlElement(name = "created", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date created;

    @XmlElement(name = "modified", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date modified;

    @XmlElement(name = "scriptGroupName", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String scriptGroupName;

    @XmlElement(name = "method", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String method;

    @XmlElement(name = "type", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String type;

    @XmlElement(name = "response", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String response;

    @XmlElement(name = "label", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String label;

    @XmlElement(name = "url", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String url;

    @XmlElement(name = "loggingKey", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String loggingKey;

    @XmlElement(name = "result", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String result;

    @XmlElement(name = "mimetype", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String mimetype;

    @XmlElement(name = "name", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String name;

    @XmlElement(name = "payload", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String payload;

    @XmlElement(name = "onFail", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String onFail;

    @XmlElement(name = "stepIndex", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private int stepIndex; // used in SilkPerformer script

    @XmlElement(name = "simplePath", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String simplePath; // path without query string

    @XmlElement(name = "hostname", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String hostname;

    @XmlElement(name = "protocol", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String protocol;

    @XmlElement(name = "comments", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String comments;

    @XmlElement(name = "respFormat", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String respFormat;

    @XmlElement(name = "reqFormat", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String reqFormat;

    @XmlElement(name = "requestheaders", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<StepDataTO> requestheaders = new HashSet<StepDataTO>();

    @XmlElement(name = "responseheaders", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<StepDataTO> responseheaders = new HashSet<StepDataTO>();

    @XmlElement(name = "requestCookies", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<StepDataTO> requestCookies = new HashSet<StepDataTO>(); // sent cookies

    @XmlElement(name = "responseCookies", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<StepDataTO> responseCookies = new HashSet<StepDataTO>(); // received cookies

    @XmlElement(name = "postDatas", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<StepDataTO> postDatas = new HashSet<StepDataTO>();

    @XmlElement(name = "queryStrings", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<StepDataTO> queryStrings = new HashSet<StepDataTO>();

    @XmlElement(name = "data", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<StepDataTO> data = new HashSet<StepDataTO>();

    @XmlElement(name = "responseData", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Set<StepDataTO> responseData = new HashSet<StepDataTO>();

    /**
     * 
     */
    public ScriptStepTO() {

    }

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
     * @return the id
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created
     *            the created to set
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * @param modified
     *            the modified to set
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * @return the scriptGroupName
     */
    public String getScriptGroupName() {
        return scriptGroupName;
    }

    /**
     * @param scriptGroupName
     *            the scriptGroupName to set
     */
    public void setScriptGroupName(String scriptGroupName) {
        this.scriptGroupName = scriptGroupName;
    }

    /**
     * @return the loggingKey
     */
    public String getLoggingKey() {
        return loggingKey;
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

    /**
     * @param loggingKey
     *            the loggingKey to set
     */
    public void setLoggingKey(String loggingKey) {
        this.loggingKey = loggingKey;
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @param method
     *            the method to set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label
     *            the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result
     *            the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the mimetype
     */
    public String getMimetype() {
        return mimetype;
    }

    /**
     * @param mimetype
     *            the mimetype to set
     */
    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the onFail
     */
    public String getOnFail() {
        return onFail;
    }

    /**
     * @param onFail
     *            the onFail to set
     */
    public void setOnFail(String onFail) {
        this.onFail = onFail;
    }

    /**
     * @return the stepIndex
     */
    public int getStepIndex() {
        return stepIndex;
    }

    /**
     * @param stepIndex
     *            the stepIndex to set
     */
    public void setStepIndex(int stepIndex) {
        this.stepIndex = stepIndex;
    }

    /**
     * @return the simplePath
     */
    public String getSimplePath() {
        return simplePath;
    }

    /**
     * @param simplePath
     *            the simplePath to set
     */
    public void setSimplePath(String simplePath) {
        this.simplePath = simplePath;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname
     *            the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * @param comments
     *            the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
    }

    /**
     * @return the respFormat
     */
    public String getRespFormat() {
        return respFormat;
    }

    /**
     * @param respFormat
     *            the respFormat to set
     */
    public void setRespFormat(String respFormat) {
        this.respFormat = respFormat;
    }

    /**
     * @return the reqFormat
     */
    public String getReqFormat() {
        return reqFormat;
    }

    /**
     * @param reqFormat
     *            the reqFormat to set
     */
    public void setReqFormat(String reqFormat) {
        this.reqFormat = reqFormat;
    }

    /**
     * @return the requestheaders
     */
    public Set<StepDataTO> getRequestheaders() {
        return requestheaders;
    }

    /**
     * @param requestheaders
     *            the requestheaders to set
     */
    public void setRequestheaders(Set<StepDataTO> requestheaders) {
        this.requestheaders = requestheaders;
    }

    /**
     * @return the responseheaders
     */
    public Set<StepDataTO> getResponseheaders() {
        return responseheaders;
    }

    /**
     * @param responseheaders
     *            the responseheaders to set
     */
    public void setResponseheaders(Set<StepDataTO> responseheaders) {
        this.responseheaders = responseheaders;
    }

    /**
     * @return the requestCookies
     */
    public Set<StepDataTO> getRequestCookies() {
        return requestCookies;
    }

    /**
     * @param requestCookies
     *            the requestCookies to set
     */
    public void setRequestCookies(Set<StepDataTO> requestCookies) {
        this.requestCookies = requestCookies;
    }

    /**
     * @return the responseCookies
     */
    public Set<StepDataTO> getResponseCookies() {
        return responseCookies;
    }

    /**
     * @param responseCookies
     *            the responseCookies to set
     */
    public void setResponseCookies(Set<StepDataTO> responseCookies) {
        this.responseCookies = responseCookies;
    }

    /**
     * @return the postDatas
     */
    public Set<StepDataTO> getPostDatas() {
        return postDatas;
    }

    /**
     * @param postDatas
     *            the postDatas to set
     */
    public void setPostDatas(Set<StepDataTO> postDatas) {
        this.postDatas = postDatas;
    }

    /**
     * @return the queryStrings
     */
    public Set<StepDataTO> getQueryStrings() {
        return queryStrings;
    }

    /**
     * @param queryStrings
     *            the queryStrings to set
     */
    public void setQueryStrings(Set<StepDataTO> queryStrings) {
        this.queryStrings = queryStrings;
    }

    /**
     * @return the data
     */
    public Set<StepDataTO> getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(Set<StepDataTO> data) {
        this.data = data;
    }

    /**
     * @return the responseData
     */
    public Set<StepDataTO> getResponseData() {
        return responseData;
    }

    /**
     * @param responseData
     *            the responseData to set
     */
    public void setResponseData(Set<StepDataTO> responseData) {
        this.responseData = responseData;
    }

}

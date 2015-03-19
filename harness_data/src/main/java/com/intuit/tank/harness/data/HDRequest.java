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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "request", propOrder = { "label", "method", "reqFormat", "path", "port", "protocol", "host", "payload",
        "requestHeaders",
        "postDatas", "queryString" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
public class HDRequest {

    @XmlAttribute(name = "name")
    private String method;
    @XmlAttribute(name = "format")
    private String reqFormat;
    @XmlAttribute(name = "loggingKey", required = false)
    private String loggingKey;
    @XmlElement(name = "path")
    private String path;
    @XmlElement(name = "port")
    private String port;
    @XmlElement(name = "protocol")
    private String protocol;
    @XmlElement(name = "host")
    private String host;
    @XmlElement(name = "label")
    private String label;

    @XmlElement(name = "payload")
    private String payload;

    @XmlElementWrapper(name = "headers")
    private List<Header> requestHeaders;
    @XmlElementWrapper(name = "body")
    private List<Header> postDatas;
    @XmlElementWrapper(name = "queryString")
    private List<Header> queryString;

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
     * @return the requestHeaders
     */
    public List<Header> getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * @param requestHeaders
     *            the requestHeaders to set
     */
    public void setRequestHeaders(List<Header> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    /**
     * @return the postDatas
     */
    public List<Header> getPostDatas() {
        return postDatas;
    }

    /**
     * @param postDatas
     *            the postDatas to set
     */
    public void setPostDatas(List<Header> postDatas) {
        this.postDatas = postDatas;
    }

    /**
     * @return the queryString
     */
    public List<Header> getQueryString() {
        return queryString;
    }

    /**
     * @param queryString
     *            the queryString to set
     */
    public void setQueryString(List<Header> queryString) {
        this.queryString = queryString;
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
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(String port) {
        this.port = port;
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
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

}

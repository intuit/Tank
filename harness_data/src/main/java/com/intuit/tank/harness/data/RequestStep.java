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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "testStep", propOrder = { "name", "scriptGroupName", "comments", "onFail", "request", "response" }, namespace = HarnessDataNamespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class RequestStep extends TestStep implements FailableStep {

    @XmlAttribute
    private String name;
    @XmlAttribute
    private String scriptGroupName;
    @XmlAttribute(name = "description")
    private String comments;
    @XmlAttribute
    private String onFail;
    @XmlElement(name = "request")
    private HDRequest request;
    @XmlElement(name = "response")
    private HDResponse response;

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
     * @return the request
     */
    public HDRequest getRequest() {
        return request;
    }

    /**
     * @param request
     *            the request to set
     */
    public void setRequest(HDRequest request) {
        this.request = request;
    }

    /**
     * @return the response
     */
    public HDResponse getResponse() {
        return response;
    }

    /**
     * @param response
     *            the response to set
     */
    public void setResponse(HDResponse response) {
        this.response = response;
    }

    @Override
    public String getInfo() {
        return request.getLabel();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " : " + name;
    }
}

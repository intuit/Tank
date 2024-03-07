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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.inject.Named;

import com.intuit.tank.project.RequestData;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

@Named
@ConversationScoped
public class RequestHeaderEditor implements Serializable {

    private List<RequestData> requestHeaders;

    public void editRequestHeaders(Set<RequestData> requestHeaders) {
        this.requestHeaders = new ArrayList<RequestData>(requestHeaders);
    }

    /**
     * @param requestHeaders
     *            the requestHeaders to set
     */
    public void setRequestHeaders(List<RequestData> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    /**
     * @return
     */
    public List<RequestData> getRequestHeaders() {
        if (requestHeaders == null) {
            requestHeaders = new ArrayList<RequestData>();
        }
        return requestHeaders;
    }

    public String getStyle(String key) {
        return (key == null || !ConverterUtil.includedHeader(key)) ? "gray" : "";
    }

    public void insertHeader() {
        RequestData rd = new RequestData();
        rd.setKey("Key");
        rd.setValue("Value");
        rd.setType("requestHeader");
        requestHeaders.add(rd);
    }

    public void removeHeader(RequestData rd) {
        requestHeaders.remove(rd);
    }

    public Set<RequestData> getRequestDataSet() {
        return new HashSet<RequestData>(getRequestHeaders());
    }
}

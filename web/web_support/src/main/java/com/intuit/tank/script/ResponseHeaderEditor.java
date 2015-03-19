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

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import com.intuit.tank.project.RequestData;

@Named
@ConversationScoped
public class ResponseHeaderEditor implements Serializable {

    private List<RequestData> responseHeaders;

    public void editResponseHeaders(Set<RequestData> headers) {
        this.responseHeaders = new ArrayList<RequestData>(headers);
    }

    /**
     * @param responseHeaders
     *            the responseHeaders to set
     */
    public void setResponseHeaders(List<RequestData> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     * @return
     */
    public List<RequestData> getResponseHeaders() {
        if (responseHeaders == null) {
            responseHeaders = new ArrayList<RequestData>();
        }
        return responseHeaders;
    }

    public Set<RequestData> getRequestDataSet() {
        Set<RequestData> reqDataSet = new HashSet<RequestData>(getResponseHeaders());
        return reqDataSet;
    }

}

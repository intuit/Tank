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
public class RequestCookiesEditor implements Serializable {

    private List<RequestData> requestCookies;

    public void editRequestCookies(Set<RequestData> requestHeaders) {
        this.requestCookies = new ArrayList<RequestData>(requestHeaders);
    }

    /**
     * @param requestHeaders
     *            the requestHeaders to set
     */
    public void setRequestCookies(List<RequestData> cookies) {
        this.requestCookies = cookies;
    }

    /**
     * @return
     */
    public List<RequestData> getRequestCookies() {
        if (requestCookies == null) {
            requestCookies = new ArrayList<RequestData>();
        }
        return requestCookies;
    }

    public void insertCookie() {
        RequestData rd = new RequestData();
        rd.setKey("Cookie");
        rd.setValue("Value");
        rd.setType("cookie");
        getRequestCookies().add(rd);
    }

    public void removeCookie(RequestData rd) {
        requestCookies.remove(rd);
    }

    public Set<RequestData> getRequestDataSet() {
        Set<RequestData> reqDataSet = new HashSet<RequestData>(getRequestCookies());
        return reqDataSet;
    }

}

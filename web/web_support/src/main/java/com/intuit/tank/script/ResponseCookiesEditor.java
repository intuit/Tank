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

@Named
@ConversationScoped
public class ResponseCookiesEditor implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<RequestData> responseCookies;

    public void editResponseCookies(Set<RequestData> cookies) {
        this.responseCookies = new ArrayList<RequestData>(cookies);
    }

    /**
     * @return the responseCookies
     */
    public List<RequestData> getResponseCookies() {
        if (responseCookies == null) {
            responseCookies = new ArrayList<RequestData>();
        }
        return responseCookies;
    }

    /**
     * @param responseCookies
     *            the responseCookies to set
     */
    public void setResponseCookies(List<RequestData> responseCookies) {
        this.responseCookies = responseCookies;
    }

    public Set<RequestData> getRequestDataSet() {
        return new HashSet<RequestData>(getResponseCookies());
    }

}

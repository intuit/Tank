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
public class QueryStringEditor implements Serializable {

    private List<RequestData> queryStrings;

    public void editQueryStrings(Set<RequestData> queryStrings) {
        this.queryStrings = new ArrayList<RequestData>(queryStrings);
    }

    /**
     * @return the queryStrings
     */
    public List<RequestData> getQueryStrings() {
        if (queryStrings == null) {
            queryStrings = new ArrayList<RequestData>();
        }
        return queryStrings;
    }

    /**
     * @param queryStrings
     *            the queryStrings to set
     */
    public void setQueryStrings(List<RequestData> queryStrings) {
        this.queryStrings = queryStrings;
    }

    public void insertQueryString() {
        RequestData rd = new RequestData();
        rd.setKey("Parameter");
        rd.setValue("Value");
        rd.setType("queryString");

        getQueryStrings().add(rd);
    }

    public void removeQueryString(RequestData rd) {
        queryStrings.remove(rd);
    }

    public Set<RequestData> getRequestDataSet() {
        Set<RequestData> reqDataSet = new HashSet<RequestData>(getQueryStrings());
        return reqDataSet;
    }

}

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
public class PostDataEditor implements Serializable {

    private List<RequestData> postData;

    public void editPostData(Set<RequestData> postData) {
        this.postData = new ArrayList<RequestData>(postData);
    }

    /**
     * @return the postData
     */
    public List<RequestData> getPostData() {
        if (postData == null) {
            postData = new ArrayList<RequestData>();
        }
        return postData;
    }

    /**
     * @param postData
     *            the postData to set
     */
    public void setPostData(List<RequestData> postData) {
        this.postData = postData;
    }

    public void insertParameter() {
        RequestData rd = new RequestData();
        rd.setKey("Parameter");
        rd.setValue("Value");
        rd.setType("requestPostData");
        getPostData().add(rd);
    }

    public void removePostData(RequestData rd) {
        postData.remove(rd);
    }

    public Set<RequestData> getRequestDataSet() {
        return new HashSet<RequestData>(getPostData());
    }

}

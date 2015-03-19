package com.intuit.tank.search.script;

/*
 * #%L
 * Script Search
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

public enum ScriptSearchField {

    scriptId("scriptId"),
    uuid("uuid"),
    method("method"),
    scriptGroupName("scriptGroupName"),
    url("url"),
    simplePath("simplePath"),
    mimeType("mimeType"),
    loggingKey("loggingKey"),
    name("name"),
    onFail("onFail"),
    label("label"),
    protocol("protocol"),
    comments("comments"),
    hostName("hostName"),
    requestHeaders("requestHeaders"),
    responseHeaders("responseHeaders"),
    requestCookies("requestCookies"),
    responseCookies("responseCookies"),
    postDatas("postDatas"),
    queryString("queryString"),
    responseContent("responseContent"),
    search("search"),
    data("data"),
    minTime("minTime"),
    maxTime("maxTime"),
    sleepTime("sleepTime"),
    variableKey("variableKey"),
    variableValue("variableValue"),
    type("type");

    private String value;

    private ScriptSearchField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

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

public enum ScriptStepSection {

    simplePath(ScriptSearchField.simplePath, "Path"),
    host(ScriptSearchField.hostName, "Host"),
    label(ScriptSearchField.label, "Label"),
    method(ScriptSearchField.method, "Method"),
    protocol(ScriptSearchField.protocol, "Protocol"),
    url(ScriptSearchField.url, "URL"),
    queryString(ScriptSearchField.queryString, "Query String"),
    requestCookies(ScriptSearchField.requestCookies, "Request Cookies"),
    responseCookies(ScriptSearchField.responseCookies, "Response Cookies"),
    requestHeaders(ScriptSearchField.requestHeaders, "Request Headers"),
    responseHeaders(ScriptSearchField.responseHeaders, "Response Headers"),
    responseContent(ScriptSearchField.responseContent, "Response Content"),
    postData(ScriptSearchField.postDatas, "Post Datas"),
    thinkTime(ScriptSearchField.data, "Think time"),
    sleepTime(ScriptSearchField.data, "Sleep time"),
    variable(ScriptSearchField.data, "Variable"),
    all(ScriptSearchField.search, "All"),
    minTime(ScriptSearchField.minTime, "Min time"),
    maxTime(ScriptSearchField.maxTime, "Max time");

    private ScriptSearchField field;
    private String value;

    private ScriptStepSection(ScriptSearchField field, String value) {
        this.field = field;
        this.value = value;
    }

    /**
     * @return the field
     */
    public ScriptSearchField getField() {
        return field;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

}

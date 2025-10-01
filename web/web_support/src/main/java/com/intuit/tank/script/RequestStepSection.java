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

public enum RequestStepSection implements Section {

    searchRequest("All Request Fields"),
    simplePath("Path"),
    host("Host"),
    method("Method"),
    protocol("Protocol"),
    url("URL"),
    mimeType("Mime-type"),
    logginKey("Logging Key"),
    name("Request Name"),
    scriptGroupName("Script Group Name"),
    queryStringKey("Query String Key"),
    queryStringValue("Query String Value"),
    requestCookieKey("Request Cookie Key"),
    requestCookieValue("Request Cookie Value"),
    responseCookieKey("Response Cookie Key"),
    responseCookieValue("Response Cookie Value"),
    requestHeaderKey("Request Header Key"),
    requestHeaderValue("Request Header Value"),
    responseHeaderKey("Response Header Key"),
    responseHeaderValue("Response Header Value"),
    responseContent("Response Content"),
    postDataKey("Post Data Key"),
    postDataValue("Post Data Value");

    private String display;

    private RequestStepSection(String display) {
        this.display = display;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return name();
    }

    @Override
    public String getDisplay() {
        return display;
    }

}

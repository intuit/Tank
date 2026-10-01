package com.intuit.tank.filter;

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

public enum ConditionScope {
    hostname("Hostname"),
    path("Path"),
    queryString("Query String"),
    postData("Post data");

    private String value;

    public String getValue() {
        return value;
    }

    private ConditionScope(String value) {
        this.value = value;
    }

}

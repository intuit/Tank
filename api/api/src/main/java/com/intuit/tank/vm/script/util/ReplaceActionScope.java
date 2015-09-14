package com.intuit.tank.vm.script.util;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

/**
 * 
 * ReplaceActionScope definitions of replacement actions
 * 
 * @author dangleton
 * 
 */
public enum ReplaceActionScope {

    requestCookie("requestCookie"),
    requestHeader("requestHeader"),
    queryString("queryString"),
    postData("postData"),
    validation("validation"),
    onfail("onFailure"),
    assignment("assignment"),
    path("path"),
    host("host");

    private String value;

    /**
     * 
     * @param value
     */
    private ReplaceActionScope(String value) {
        this.value = value;
    }

    /**
     * getst the string value for this enum type
     * 
     * @return the value
     */
    public String getValue() {
        return value;
    }

}

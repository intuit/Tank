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

public enum AddActionScope {

    responseData("responseData"),
    validation("validation"),
    assignment("assignment"),
    thinkTime("thinkTime"),
    sleepTime("sleepTime"),
    requestCookie("requestCookie"),
    requestHeader("requestHeader"),
    queryString("queryString"),
    postData("postData");

    private String value;

    private AddActionScope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

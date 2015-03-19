package com.intuit.tank.proxy.config;

/*
 * #%L
 * proxy-extension
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

public enum CommonHeaders {
    contentType("Content-Type"),
    path("Path"),
    all("all"),
    host("Host");

    private String value;

    private CommonHeaders(String value) {
        this.value = value;
    }
}

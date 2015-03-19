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

public enum ScriptSearchScope {

    all("All"),
    request("Request"),
    thinkTime("Think time"),
    sleepTime("Sleep time"),
    variable("Variable");

    private String value;

    private ScriptSearchScope(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

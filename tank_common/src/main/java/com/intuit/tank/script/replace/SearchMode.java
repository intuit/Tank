package com.intuit.tank.script.replace;

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

public enum SearchMode {

    valueOnly("Value Only"),
    keyOnly("Key Only"),
    all("All");

    private String display;

    SearchMode(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

}

package com.intuit.tank.util;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

public enum ScriptFilterType {
    INTERNAL("Internal Filter"),
    EXTERNAL("External Filter");

    private String display;

    ScriptFilterType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}

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

public enum CommonSection implements Section {
    search("search all");

    private String display;

    private CommonSection(String display) {
        this.display = display;
    }

    @Override
    public String getValue() {
        return name();
    }

    @Override
    public String getDisplay() {
        return display;
    }
}

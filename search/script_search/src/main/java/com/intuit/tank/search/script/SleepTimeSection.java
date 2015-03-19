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

public enum SleepTimeSection implements Section {

    sleepTime("Sleep Time");

    private String display;

    private SleepTimeSection(String display) {
        this.display = display;
    }

    public String getValue() {
        return name();
    }

    @Override
    public String getDisplay() {
        return display;
    }

}

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

/**
 * 
 * @author pquinn
 * 
 */

public enum ReplaceMode {

    KEY("Key"),
    VALUE("Value");

    private String display;

    ReplaceMode(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

}

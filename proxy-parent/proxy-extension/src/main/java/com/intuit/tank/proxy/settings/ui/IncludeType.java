/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy.settings.ui;

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

/**
 * IncludeType
 * 
 * @author dangleton
 * 
 */
public enum IncludeType {

    TRANSACTION_INCLUDE("Inclusions"),
    TRANSACTION_EXCLUDE("Exclusions"),
    BODY_INCLUDE("Body Inclusions"),
    BODY_EXCLUDE("Body Exclustions");

    private String display;

    /**
     * @param display
     */
    private IncludeType(String display) {
        this.display = display;
    }

    /**
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

}

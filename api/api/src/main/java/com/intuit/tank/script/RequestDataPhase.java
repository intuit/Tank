/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.script;

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
 * RequestDataPhase
 * 
 * @author dangleton
 * 
 */
public enum RequestDataPhase {

    PRE_REQUEST("Pre Request"),
    POST_REQUEST("Post Request");

    private String display;

    /**
     * @param display
     */
    private RequestDataPhase(String display) {
        this.display = display;
    }

    /**
     * @return the display
     */
    public String getDisplay() {
        return display;
    }

}

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.tools.script;

/*
 * #%L
 * External Script Engine
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;

/**
 * LoggingOutputLogger
 * 
 * @author dangleton
 * 
 */
public class StringOutputLogger implements OutputLogger, Serializable {

    private static final long serialVersionUID = 1L;
    private StringBuilder sb = new StringBuilder();

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScrollContent(boolean autoScroll) {
        // nothing to do

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void log(String text) {
        sb.append(text);
    }

    public void logLine(String text) {
        log(text + '\n');
    }

    /**
     * 
     * @return
     */
    public String getOutput() {
        return sb.toString();
    }

    @Override
    public void debug(String text) {
        logLine(text);
    }

    @Override
    public void error(String text) {
        logLine("ERROR: " + text);
    }

}

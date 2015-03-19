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

/**
 * OutputLogger
 * 
 * @author dangleton
 * 
 */
public interface OutputLogger {

    /**
     * Sets whether to autoscroll the container;
     * 
     * @param autoScroll
     *            true to autoscroll
     */
    public void setScrollContent(boolean autoScroll);

    /**
     * logs the text.
     * 
     * @param text
     *            the text to log
     */
    public void log(String text);

    /**
     * logs the text with error context.
     * 
     * @param text
     *            the text to log
     */
    public void error(String text);

    /**
     * logs the text at debug level.
     * 
     * @param text
     *            the text to log
     */
    public void debug(String text);
}

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * LoggingOutputLogger
 * 
 * @author dangleton
 * 
 */
public class NullOutputLogger implements OutputLogger {

    /**
     * @inheritDoc
     */
    @Override
    public void setScrollContent(boolean autoScroll) {
        // nothing to do

    }

    /**
     * @inheritDoc
     */
    @Override
    public void log(String text) {
        // nothing to do swallow message
    }

    @Override
    public void debug(String text) {
        // nothing to do swallow message

    }

    @Override
    public void error(String text) {
        // TODO Auto-generated method stub

    }

}

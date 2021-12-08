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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LoggingOutputLogger
 * 
 * @author dangleton
 * 
 */
public class LoggingOutputLogger implements OutputLogger {
    private static final Logger LOG = LogManager.getLogger(LoggingOutputLogger.class);

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
        LOG.info(redactSSN(text));
    }

    @Override
    public void debug(String text) {
        LOG.debug(text);
    }

    @Override
    public void error(String text) {
        LOG.error(text);
    }

    /**
     * Redacts SSNs of the form `"ssn":"#########"`
     * @param text
     * @return
     */
    public String redactSSN(String text) {
        Pattern ssnDetector = Pattern.compile("((\"ssn\")|(\"social\")):\"\\d{3}(-?)\\d{2}(-?)\\d{4}\"");
        Matcher m = ssnDetector.matcher(text);
        return m.replaceAll("\"ssn\":\"{SSN_REDACTED}\"");
    }

}

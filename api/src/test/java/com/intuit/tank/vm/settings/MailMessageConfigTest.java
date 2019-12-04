/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.settings;

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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;

import com.intuit.tank.test.TestGroups;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MailMessageConfigTest
 * 
 * @author dangleton
 * 
 */
public class MailMessageConfigTest {

    @BeforeEach
    public void init() {
    	LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
    	Configuration config = ctx.getConfiguration();
    	config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME).setLevel(Level.INFO);
    	ctx.updateLoggers();  // This causes all Loggers to refetch information from their LoggerConfig.
        System.getProperties().setProperty("WATS_PROPERTIES", "src/test/resources");
    }

    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testConfig() {
        MailMessageConfig config = new MailMessageConfig();
        assertEquals(2, config.getAllMessages().size());
        MailMessage msg = config.getMailMessage("TEST");
        assertEquals("Test Subject", msg.getSubject());
        assertEquals("<p>Test Body</p>", msg.getBody());
        assertEquals("Test Body", msg.getPlainTextBody());
        for (MailMessage m : config.getAllMessages()) {
            assertTrue(!m.getPlainTextBody().contains("<"));
            assertTrue(!m.getPlainTextBody().contains(">"));
        }
        msg = config.getMailMessage("BOGUS");
        assertEquals("Default Test Subject", msg.getSubject());
        assertEquals("<p>Default Test Body</p>", msg.getBody());
        assertEquals("Default Test Body", msg.getPlainTextBody());
    }
}

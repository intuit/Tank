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

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.intuit.tank.vm.settings.MailMessage;
import com.intuit.tank.vm.settings.MailMessageConfig;
import com.intuit.tank.test.TestGroups;

/**
 * MailMessageConfigTest
 * 
 * @author dangleton
 * 
 */
public class MailMessageConfigTest {

    @BeforeClass
    public void init() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);
        System.getProperties().setProperty("WATS_PROPERTIES", "src/test/resources");
    }

    @Test(groups = TestGroups.FUNCTIONAL)
    public void testConfig() {
        MailMessageConfig config = new MailMessageConfig();
        Assert.assertEquals(2, config.getAllMessages().size());
        MailMessage msg = config.getMailMessage("TEST");
        Assert.assertEquals("Test Subject", msg.getSubject());
        Assert.assertEquals("<p>Test Body</p>", msg.getBody());
        Assert.assertEquals("Test Body", msg.getPlainTextBody());
        for (MailMessage m : config.getAllMessages()) {
            Assert.assertTrue(!m.getPlainTextBody().contains("<"));
            Assert.assertTrue(!m.getPlainTextBody().contains(">"));
        }
        msg = config.getMailMessage("BOGUS");
        Assert.assertEquals("Default Test Subject", msg.getSubject());
        Assert.assertEquals("<p>Default Test Body</p>", msg.getBody());
        Assert.assertEquals("Default Test Body", msg.getPlainTextBody());
    }
}

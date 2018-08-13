/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.proxy.config;

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

import org.testng.Assert;
import org.testng.annotations.Test;

import com.intuit.tank.proxy.config.CommonsProxyConfiguration;
import com.intuit.tank.test.TestGroups;

/**
 * ProxyConfigurationTest
 * 
 * @author dangleton
 * 
 */
public class ProxyConfigurationTest {
    @Test(groups = TestGroups.FUNCTIONAL)
    public void testConfigParse() throws Exception {
        CommonsProxyConfiguration c = new CommonsProxyConfiguration("src/test/resources/test-config.xml");
        Assert.assertNotNull(c);
        Assert.assertEquals(c.getPort(), 8188);
        Assert.assertEquals(c.getOutputFile(), "testOutputFile.xml");
        Assert.assertFalse(c.getInclusions().isEmpty());
        Assert.assertFalse(c.getExclusions().isEmpty());
        Assert.assertFalse(c.getBodyInclusions().isEmpty());
        Assert.assertFalse(c.getBodyExclusions().isEmpty());
    }
}

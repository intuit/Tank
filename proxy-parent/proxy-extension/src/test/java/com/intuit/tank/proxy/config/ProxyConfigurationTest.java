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

import com.intuit.tank.proxy.config.CommonsProxyConfiguration;
import com.intuit.tank.test.TestGroups;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProxyConfigurationTest
 * 
 * @author dangleton
 * 
 */
public class ProxyConfigurationTest {
    @Test
    @Tag(TestGroups.FUNCTIONAL)
    public void testConfigParse() throws Exception {
        CommonsProxyConfiguration c = new CommonsProxyConfiguration("src/test/resources/test-config.xml");
        assertNotNull(c);
        assertEquals(c.getPort(), 8188);
        assertEquals(c.getOutputFile(), "testOutputFile.xml");
        assertFalse(c.getInclusions().isEmpty());
        assertFalse(c.getExclusions().isEmpty());
        assertFalse(c.getBodyInclusions().isEmpty());
        assertFalse(c.getBodyExclusions().isEmpty());
    }
}

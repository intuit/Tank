package com.intuit.tank.service.util;

/*
 * #%L
 * Rest Service Common Classes
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import javax.ws.rs.core.CacheControl;

import org.junit.*;

import com.intuit.tank.service.util.ResponseUtil;

import static org.junit.Assert.*;

/**
 * The class <code>ResponseUtilTest</code> contains tests for the class <code>{@link ResponseUtil}</code>.
 *
 * @generatedBy CodePro at 12/16/14 4:40 PM
 */
public class ResponseUtilTest {
    /**
     * Run the CacheControl getNoStoreCacheControl() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 4:40 PM
     */
    @Test
    public void testGetNoStoreCacheControl_1()
        throws Exception {

        CacheControl result = ResponseUtil.getNoStoreCacheControl();

        assertNotNull(result);
        assertEquals("private, no-cache, no-store, no-transform, max-age=0, s-maxage=0", result.toString());
        assertEquals(true, result.isPrivate());
        assertEquals(true, result.isNoTransform());
        assertEquals(true, result.isNoCache());
        assertEquals(true, result.isNoStore());
        assertEquals(0, result.getSMaxAge());
        assertEquals(false, result.isProxyRevalidate());
        assertEquals(false, result.isMustRevalidate());
        assertEquals(0, result.getMaxAge());
    }
}
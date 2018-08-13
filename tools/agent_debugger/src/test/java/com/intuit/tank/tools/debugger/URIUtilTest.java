package com.intuit.tank.tools.debugger;

/*
 * #%L
 * Intuit Tank Agent Debugger
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.jupiter.api.*;

import com.intuit.tank.tools.debugger.URIUtil;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>URIUtilTest</code> contains tests for the class <code>{@link URIUtil}</code>.
 *
 * @generatedBy CodePro at 12/16/14 3:41 PM
 */
public class URIUtilTest {
    /**
     * Run the StringBuffer encodePath(StringBuffer,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testEncodePath_1()
        throws Exception {
        StringBuffer buf = null;
        String path = "";

        StringBuffer result = URIUtil.encodePath(buf, path);

        assertEquals(null, result);
    }

    /**
     * Run the StringBuffer encodePath(StringBuffer,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testEncodePath_2()
        throws Exception {
        StringBuffer buf = null;
        String path = "aa";

        StringBuffer result = URIUtil.encodePath(buf, path);

        assertEquals(null, result);
    }

    /**
     * Run the StringBuffer encodePath(StringBuffer,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testEncodePath_3()
        throws Exception {
        StringBuffer buf = null;
        String path = "aa";

        StringBuffer result = URIUtil.encodePath(buf, path);

        assertEquals(null, result);
    }

    /**
     * Run the StringBuffer encodePath(StringBuffer,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testEncodePath_4()
        throws Exception {
        StringBuffer buf = null;
        String path = "aa";

        StringBuffer result = URIUtil.encodePath(buf, path);

        assertEquals(null, result);
    }

    /**
     * Run the StringBuffer encodePath(StringBuffer,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testEncodePath_5()
        throws Exception {
        StringBuffer buf = null;
        String path = "aa";

        StringBuffer result = URIUtil.encodePath(buf, path);

        assertEquals(null, result);
    }

    /**
     * Run the StringBuffer encodePath(StringBuffer,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testEncodePath_6()
        throws Exception {
        StringBuffer buf = null;
        String path = "aa";

        StringBuffer result = URIUtil.encodePath(buf, path);

        assertEquals(null, result);
    }

    /**
     * Run the StringBuffer encodePath(StringBuffer,String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/16/14 3:41 PM
     */
    @Test
    public void testEncodePath_7()
        throws Exception {
        StringBuffer buf = new StringBuffer();
        String path = "";

        StringBuffer result = URIUtil.encodePath(buf, path);

        assertNotNull(result);
        assertEquals("", result.toString());
        assertEquals(0, result.length());
        assertEquals(16, result.capacity());
    }
}
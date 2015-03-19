package com.intuit.tank.navigation;

/*
 * #%L
 * JSF Support Beans
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import org.junit.*;

import com.intuit.tank.navigation.SiteSection;

import static org.junit.Assert.*;

/**
 * The class <code>SiteSectionTest</code> contains tests for the class <code>{@link SiteSection}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class SiteSectionTest {
    /**
     * Run the boolean isMatch(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsMatch_1()
        throws Exception {
        SiteSection fixture = SiteSection.admin;
        String viewId = "/admin/index.html";

        boolean result = fixture.isMatch(viewId);

        assertTrue(result);
    }

    /**
     * Run the boolean isMatch(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsMatch_2()
        throws Exception {
        SiteSection fixture = SiteSection.admin;
        String viewId = "/other/index.html";

        boolean result = fixture.isMatch(viewId);

        assertTrue(!result);
    }
}
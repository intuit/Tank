package com.intuit.tank.harness.data;

/*
 * #%L
 * Harness Data
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.intuit.tank.harness.data.CookieStep;

/**
 * The class <code>CookieStepTest</code> contains tests for the class <code>{@link CookieStep}</code>.
 * 
 * @generatedBy CodePro at 9/10/14 9:36 AM
 */
public class CookieStepTest {
    /**
     * Run the CookieStep() constructor test.
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testCookieStep_1()
            throws Exception {
        CookieStep result = new CookieStep();
        assertNotNull(result);
    }

    /**
     * Run the String getCookieDomain() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetCookieDomain_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;

        String result = fixture.getCookieDomain();

        assertEquals("", result);
    }

    /**
     * Run the String getCookieName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetCookieName_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;

        String result = fixture.getCookieName();

        assertEquals("", result);
    }

    /**
     * Run the String getCookiePath() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetCookiePath_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;

        String result = fixture.getCookiePath();

        assertEquals("", result);
    }

    /**
     * Run the String getCookieValue() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetCookieValue_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;

        String result = fixture.getCookieValue();

        assertEquals("", result);
    }

    /**
     * Run the String getInfo() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testGetInfo_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;

        String result = fixture.getInfo();

        assertEquals("Set Cookie: domain: ;  = ", result);
    }

    /**
     * Run the void setCookieDomain(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetCookieDomain_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;
        String cookieDomain = "";

        fixture.setCookieDomain(cookieDomain);

    }

    /**
     * Run the void setCookieName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetCookieName_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;
        String cookieName = "";

        fixture.setCookieName(cookieName);

    }

    /**
     * Run the void setCookiePath(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetCookiePath_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;
        String cookiePath = "";

        fixture.setCookiePath(cookiePath);

    }

    /**
     * Run the void setCookieValue(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testSetCookieValue_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;
        String cookieValue = "";

        fixture.setCookieValue(cookieValue);

    }

    /**
     * Run the String toString() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/10/14 9:36 AM
     */
    @Test
    public void testToString_1()
            throws Exception {
        CookieStep fixture = new CookieStep();
        fixture.setCookiePath("");
        fixture.setCookieValue("");
        fixture.setCookieDomain("");
        fixture.setCookieName("");
        fixture.stepIndex = 1;

        String result = fixture.toString();

        assertEquals("CookieStep : =", result);
    }
}
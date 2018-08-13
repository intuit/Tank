package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
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

import com.intuit.tank.project.WatsRevisionInfo;

/**
 * The class <code>WatsRevisionInfoTest</code> contains tests for the class <code>{@link WatsRevisionInfo}</code>.
 *
 * @generatedBy CodePro at 12/15/14 1:34 PM
 */
public class WatsRevisionInfoTest {
    /**
     * Run the WatsRevisionInfo() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testWatsRevisionInfo_1()
        throws Exception {
        WatsRevisionInfo result = new WatsRevisionInfo();
        assertNotNull(result);
    }

    /**
     * Run the String getUsername() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testGetUsername_1()
        throws Exception {
        WatsRevisionInfo fixture = new WatsRevisionInfo();
        fixture.setUsername("");

        String result = fixture.getUsername();

        assertEquals("", result);
    }

    /**
     * Run the void setUsername(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 1:34 PM
     */
    @Test
    public void testSetUsername_1()
        throws Exception {
        WatsRevisionInfo fixture = new WatsRevisionInfo();
        fixture.setUsername("");
        String username = "";

        fixture.setUsername(username);

    }
}
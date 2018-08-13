package com.intuit.tank.api.model.v1.user;

/*
 * #%L
 * User Rest API
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

import com.intuit.tank.api.model.v1.user.UserCredentials;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>UserCredentialsTest</code> contains tests for the class <code>{@link UserCredentials}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:41 PM
 */
public class UserCredentialsTest {
    /**
     * Run the UserCredentials() constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testUserCredentials_1()
        throws Exception {

        UserCredentials result = new UserCredentials();

        assertNotNull(result);
        assertEquals(null, result.getName());
        assertEquals(null, result.getPass());
    }

    /**
     * Run the UserCredentials(String,String) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testUserCredentials_2()
        throws Exception {
        String name = "";
        String pass = "";

        UserCredentials result = new UserCredentials(name, pass);

        assertNotNull(result);
        assertEquals("", result.getName());
        assertEquals("", result.getPass());
    }

    /**
     * Run the String getName() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testGetName_1()
        throws Exception {
        UserCredentials fixture = new UserCredentials("", "");

        String result = fixture.getName();

        assertEquals("", result);
    }

    /**
     * Run the String getPass() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testGetPass_1()
        throws Exception {
        UserCredentials fixture = new UserCredentials("", "");

        String result = fixture.getPass();

        assertEquals("", result);
    }

    /**
     * Run the void setName(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testSetName_1()
        throws Exception {
        UserCredentials fixture = new UserCredentials("", "");
        String name = "";

        fixture.setName(name);

    }

    /**
     * Run the void setPass(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:41 PM
     */
    @Test
    public void testSetPass_1()
        throws Exception {
        UserCredentials fixture = new UserCredentials("", "");
        String pass = "";

        fixture.setPass(pass);

    }
}
package com.intuit.tank.vm.common;

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

import org.junit.*;

import com.intuit.tank.vm.common.UsernameProvider;

import static org.junit.Assert.*;

/**
 * The class <code>UsernameProviderCpTest</code> contains tests for the class <code>{@link UsernameProvider}</code>.
 * 
 * @generatedBy CodePro at 9/3/14 3:44 PM
 */
public class UsernameProviderCpTest {
    /**
     * Run the String getUserName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetUserName_1()
            throws Exception {
        UsernameProvider fixture = new UsernameProvider();
        fixture.setUserName((String) null);

        String result = fixture.getUserName();

        assertEquals("system", result);
    }

    /**
     * Run the String getUserName() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testGetUserName_2()
            throws Exception {
        UsernameProvider fixture = new UsernameProvider();
        fixture.setUserName("");

        String result = fixture.getUserName();

        assertEquals("", result);
    }

    /**
     * Run the void setUserName(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 9/3/14 3:44 PM
     */
    @Test
    public void testSetUserName_1()
            throws Exception {
        UsernameProvider fixture = new UsernameProvider();
        fixture.setUserName("");
        String userName = "";

        fixture.setUserName(userName);

    }
}
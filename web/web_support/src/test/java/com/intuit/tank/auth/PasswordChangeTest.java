package com.intuit.tank.auth;

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

import com.intuit.tank.auth.AccountModify;

import static org.junit.Assert.*;

/**
 * The class <code>PasswordChangeTest</code> contains tests for the class <code>{@link AccountModify}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class PasswordChangeTest {
    /**
     * Run the PasswordChange() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testPasswordChange_1()
        throws Exception {
        AccountModify result = new AccountModify();
        assertNotNull(result);
    }

    /**
     * Run the String getPassword() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetPassword_1()
        throws Exception {
        AccountModify fixture = new AccountModify();
        fixture.setPassword("");
        fixture.setPasswordConfirm("");

        String result = fixture.getPassword();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.auth.PasswordChange.setPassword(PasswordChange.java:78)
        assertNotNull(result);
    }

    /**
     * Run the String getPasswordConfirm() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testGetPasswordConfirm_1()
        throws Exception {
        AccountModify fixture = new AccountModify();
        fixture.setPassword("");
        fixture.setPasswordConfirm("");

        String result = fixture.getPasswordConfirm();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.auth.PasswordChange.setPassword(PasswordChange.java:78)
        assertNotNull(result);
    }

    /**
     * Run the boolean isSucceeded() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsSucceeded_1()
        throws Exception {
        AccountModify fixture = new AccountModify();
        fixture.setPassword("");
        fixture.setPasswordConfirm("");

        boolean result = fixture.isSucceeded();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.auth.PasswordChange.setPassword(PasswordChange.java:78)
        assertTrue(!result);
    }

    /**
     * Run the boolean isSucceeded() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testIsSucceeded_2()
        throws Exception {
        AccountModify fixture = new AccountModify();
        fixture.setPassword("");
        fixture.setPasswordConfirm("");

        boolean result = fixture.isSucceeded();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.auth.PasswordChange.setPassword(PasswordChange.java:78)
        assertTrue(!result);
    }


    /**
     * Run the void setPassword(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetPassword_1()
        throws Exception {
        AccountModify fixture = new AccountModify();
        fixture.setPassword("");
        fixture.setPasswordConfirm("");
        String password = "";

        fixture.setPassword(password);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.auth.PasswordChange.setPassword(PasswordChange.java:78)
    }

    /**
     * Run the void setPasswordConfirm(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testSetPasswordConfirm_1()
        throws Exception {
        AccountModify fixture = new AccountModify();
        fixture.setPassword("");
        fixture.setPasswordConfirm("");
        String passwordConfirm = "";

        fixture.setPasswordConfirm(passwordConfirm);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.auth.PasswordChange.setPassword(PasswordChange.java:78)
    }
}
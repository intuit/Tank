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

import static org.junit.Assert.*;

import com.intuit.tank.auth.UserRequest;
import com.intuit.tank.project.User;

/**
 * The class <code>UserRequestTest</code> contains tests for the class <code>{@link UserRequest}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class UserRequestTest {
    /**
     * Run the UserRequest() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testUserRequest_1()
        throws Exception {
        UserRequest result = new UserRequest();
        assertNotNull(result);
    }

    /**
     * Run the String getPasswordConfirm() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetPasswordConfirm_1()
        throws Exception {
        UserRequest fixture = new UserRequest();
        fixture.setPasswordConfirm("");

        String result = fixture.getPasswordConfirm();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
        //       at com.intuit.tank.auth.UserRequest.<init>(UserRequest.java:31)
        assertNotNull(result);
    }

    

    /**
     * Run the void setPasswordConfirm(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetPasswordConfirm_1()
        throws Exception {
        UserRequest fixture = new UserRequest();
        fixture.setPasswordConfirm("");
        String passwordConfirm = "";

        fixture.setPasswordConfirm(passwordConfirm);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
        //       at com.intuit.tank.auth.UserRequest.<init>(UserRequest.java:31)
    }
}
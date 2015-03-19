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

import com.intuit.tank.auth.TankUser;
import com.intuit.tank.project.User;

/**
 * The class <code>TankUserTest</code> contains tests for the class <code>{@link TankUser}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class TankUserTest {
    /**
     * Run the TankUser(User) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testTankUser_1()
        throws Exception {
        User userEntity = new User();

        TankUser result = new TankUser(userEntity);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
        assertNotNull(result);
    }

    /**
     * Run the String getId() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetId_1()
        throws Exception {
        TankUser fixture = new TankUser(new User());

        String result = fixture.getId();
        assertNull(result);
    }

    /**
     * Run the String getKey() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetKey_1()
        throws Exception {
        TankUser fixture = new TankUser(new User());

        String result = fixture.getKey();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
        assertNotNull(result);
    }

    /**
     * Run the User getUserEntity() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetUserEntity_1()
        throws Exception {
        TankUser fixture = new TankUser(new User());

        User result = fixture.getUserEntity();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
        assertNotNull(result);
    }

    /**
     * Run the void setUserEntity(User) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetUserEntity_1()
        throws Exception {
        TankUser fixture = new TankUser(new User());
        User userEntity = new User();

        fixture.setUserEntity(userEntity);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.User.<init>(User.java:61)
    }
}
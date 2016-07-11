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
import org.picketlink.Identity;
import org.picketlink.internal.DefaultIdentity;

import static org.junit.Assert.*;

import com.intuit.tank.auth.InternalSecurity;
import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.OwnableEntity;

/**
 * The class <code>InternalSecurityTest</code> contains tests for the class <code>{@link InternalSecurity}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:53 PM
 */
public class InternalSecurityTest {

    /**
     * Run the boolean loginChecker(Identity) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testLoginChecker_1()
        throws Exception {
        InternalSecurity fixture = new InternalSecurity();
        Identity identity = new DefaultIdentity();

        boolean result = fixture.loginChecker(identity);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.auth.InternalSecurity.loginChecker(InternalSecurity.java:36)
        assertTrue(!result);
    }

    /**
     * Run the boolean loginChecker(Identity) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testLoginChecker_2()
        throws Exception {
        InternalSecurity fixture = new InternalSecurity();
        Identity identity = new DefaultIdentity();

        boolean result = fixture.loginChecker(identity);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.auth.InternalSecurity.loginChecker(InternalSecurity.java:36)
        assertTrue(!result);
    }

    /**
     * Run the boolean ownerChecker(Identity,OwnableEntity) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testOwnerChecker_1()
        throws Exception {
        InternalSecurity fixture = new InternalSecurity();
        Identity identity = new DefaultIdentity();
        OwnableEntity item = new ColumnPreferences();

        boolean result = fixture.ownerChecker(identity, item);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.ColumnPreferences.<init>(ColumnPreferences.java:61)
        assertTrue(!result);
    }

    /**
     * Run the boolean ownerChecker(Identity,OwnableEntity) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testOwnerChecker_2()
        throws Exception {
        InternalSecurity fixture = new InternalSecurity();
        Identity identity = new DefaultIdentity();
        OwnableEntity item = new ColumnPreferences();

        boolean result = fixture.ownerChecker(identity, item);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.ColumnPreferences.<init>(ColumnPreferences.java:61)
        assertTrue(!result);
    }

    /**
     * Run the boolean ownerChecker(Identity,OwnableEntity) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testOwnerChecker_3()
        throws Exception {
        InternalSecurity fixture = new InternalSecurity();
        Identity identity = new DefaultIdentity();
        OwnableEntity item = new ColumnPreferences();

        boolean result = fixture.ownerChecker(identity, item);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.ColumnPreferences.<init>(ColumnPreferences.java:61)
        assertTrue(!result);
    }

    /**
     * Run the boolean ownerChecker(Identity,OwnableEntity) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:53 PM
     */
    @Test
    public void testOwnerChecker_4()
        throws Exception {
        InternalSecurity fixture = new InternalSecurity();
        Identity identity = new DefaultIdentity();
        OwnableEntity item = new ColumnPreferences();

        boolean result = fixture.ownerChecker(identity, item);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.project.ColumnPreferences.<init>(ColumnPreferences.java:61)
        assertTrue(!result);
    }
}
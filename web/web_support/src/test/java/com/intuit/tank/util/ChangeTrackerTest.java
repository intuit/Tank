package com.intuit.tank.util;

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

import com.intuit.tank.util.ChangeTracker;

import static org.junit.Assert.*;

/**
 * The class <code>ChangeTrackerTest</code> contains tests for the class <code>{@link ChangeTracker}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class ChangeTrackerTest {
    /**
     * Run the ChangeTracker() constructor test.
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testChangeTracker_1()
        throws Exception {
        ChangeTracker result = new ChangeTracker();
        assertNotNull(result);
    }

    /**
     * Run the boolean isDirty() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsDirty_1()
        throws Exception {
        ChangeTracker fixture = new ChangeTracker();
        fixture.setDirty(true);

        boolean result = fixture.isDirty();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.ChangeTracker.setDirty(ChangeTracker.java:37)
        assertTrue(result);
    }

    /**
     * Run the boolean isDirty() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsDirty_2()
        throws Exception {
        ChangeTracker fixture = new ChangeTracker();
        fixture.setDirty(false);

        boolean result = fixture.isDirty();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.ChangeTracker.setDirty(ChangeTracker.java:37)
        assertTrue(!result);
    }

    /**
     * Run the void markDirty() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testMarkDirty_1()
        throws Exception {
        ChangeTracker fixture = new ChangeTracker();
        fixture.setDirty(true);

        fixture.markDirty();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.ChangeTracker.setDirty(ChangeTracker.java:37)
    }

    /**
     * Run the void reset() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testReset_1()
        throws Exception {
        ChangeTracker fixture = new ChangeTracker();
        fixture.setDirty(true);

        fixture.reset();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.ChangeTracker.setDirty(ChangeTracker.java:37)
    }

    /**
     * Run the void setDirty(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetDirty_1()
        throws Exception {
        ChangeTracker fixture = new ChangeTracker();
        fixture.setDirty(true);
        boolean dirty = true;

        fixture.setDirty(dirty);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.ChangeTracker.setDirty(ChangeTracker.java:37)
    }
}
package com.intuit.tank.wrapper;

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

import org.junit.jupiter.api.*;

import com.intuit.tank.wrapper.SelectableWrapper;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The class <code>SelectableWrapperTest</code> contains tests for the class <code>{@link SelectableWrapper}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:54 PM
 */
public class SelectableWrapperTest {
    /**
     * Run the SelectableWrapper(T) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSelectableWrapper_1()
        throws Exception {

        SelectableWrapper result = new SelectableWrapper(null);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.wrapper.SelectableWrapper.<init>(SelectableWrapper.java:23)
        assertNotNull(result);
    }

    /**
     * Run the Object getEntity() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testGetEntity_1()
        throws Exception {
        SelectableWrapper fixture = new SelectableWrapper((Object) null);
        fixture.setSelected(true);

        Object result = fixture.getEntity();
    }

    /**
     * Run the boolean isSelected() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsSelected_1()
        throws Exception {
        SelectableWrapper fixture = new SelectableWrapper((Object) null);
        fixture.setSelected(true);

        boolean result = fixture.isSelected();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.wrapper.SelectableWrapper.<init>(SelectableWrapper.java:23)
        assertTrue(result);
    }

    /**
     * Run the boolean isSelected() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testIsSelected_2()
        throws Exception {
        SelectableWrapper fixture = new SelectableWrapper((Object) null);
        fixture.setSelected(false);

        boolean result = fixture.isSelected();
    }

    /**
     * Run the void setSelected(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:54 PM
     */
    @Test
    public void testSetSelected_1()
        throws Exception {
        SelectableWrapper fixture = new SelectableWrapper((Object) null);
        fixture.setSelected(true);
        boolean selected = true;

        fixture.setSelected(selected);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.wrapper.SelectableWrapper.<init>(SelectableWrapper.java:23)
    }
}
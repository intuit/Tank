package com.intuit.tank.script;

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

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.script.SearchOptionWrapper;
import com.intuit.tank.search.script.Section;

/**
 * The class <code>SearchOptionWrapperTest</code> contains tests for the class <code>{@link SearchOptionWrapper}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class SearchOptionWrapperTest {
    /**
     * Run the SearchOptionWrapper(Section) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSearchOptionWrapper_1()
        throws Exception {
        Section value = null;

        SearchOptionWrapper result = new SearchOptionWrapper(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SearchOptionWrapper.<init>(SearchOptionWrapper.java:12)
        assertNotNull(result);
    }


    /**
     * Run the Section getValue() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetValue_1()
        throws Exception {
        SearchOptionWrapper fixture = new SearchOptionWrapper((Section) null);
        fixture.setSelected(true);

        Section result = fixture.getValue();
    }

    /**
     * Run the boolean isSelected() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsSelected_1()
        throws Exception {
        SearchOptionWrapper fixture = new SearchOptionWrapper((Section) null);
        fixture.setSelected(true);

        boolean result = fixture.isSelected();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SearchOptionWrapper.<init>(SearchOptionWrapper.java:12)
        assertTrue(result);
    }

    /**
     * Run the boolean isSelected() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsSelected_2()
        throws Exception {
        SearchOptionWrapper fixture = new SearchOptionWrapper((Section) null);
        fixture.setSelected(false);

        boolean result = fixture.isSelected();
    }

    /**
     * Run the void setSelected(boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetSelected_1()
        throws Exception {
        SearchOptionWrapper fixture = new SearchOptionWrapper((Section) null);
        fixture.setSelected(true);
        boolean selected = true;

        fixture.setSelected(selected);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SearchOptionWrapper.<init>(SearchOptionWrapper.java:12)
    }

    /**
     * Run the void setValue(Section) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetValue_1()
        throws Exception {
        SearchOptionWrapper fixture = new SearchOptionWrapper((Section) null);
        fixture.setSelected(true);
        Section value = null;

        fixture.setValue(value);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.script.SearchOptionWrapper.<init>(SearchOptionWrapper.java:12)
    }
}
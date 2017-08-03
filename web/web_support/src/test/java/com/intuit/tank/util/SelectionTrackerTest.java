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

import static org.junit.Assert.*;

import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.DataFileBrowser;
import com.intuit.tank.util.Multiselectable;
import com.intuit.tank.util.SelectionTracker;

/**
 * The class <code>SelectionTrackerTest</code> contains tests for the class <code>{@link SelectionTracker}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
@org.junit.Ignore
public class SelectionTrackerTest {
    /**
     * Run the SelectionTracker(Multiselectable<T>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSelectionTracker_1()
        throws Exception {
        Multiselectable<DataFile> selectable = new DataFileBrowser();

        SelectionTracker result = new SelectionTracker(selectable);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the boolean hasSelected() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testHasSelected_1()
        throws Exception {
        SelectionTracker fixture = new SelectionTracker(new DataFileBrowser());

        boolean result = fixture.hasSelected();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertTrue(!result);
    }


    /**
     * Run the void selectAll() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSelectAll_1()
        throws Exception {
        SelectionTracker fixture = new SelectionTracker(new DataFileBrowser());

        fixture.selectAll();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void selectAll() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSelectAll_2()
        throws Exception {
        SelectionTracker fixture = new SelectionTracker(new DataFileBrowser());

        fixture.selectAll();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void selectAll() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSelectAll_3()
        throws Exception {
        SelectionTracker fixture = new SelectionTracker(new DataFileBrowser());

        fixture.selectAll();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void unselectAll() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testUnselectAll_1()
        throws Exception {
        SelectionTracker fixture = new SelectionTracker(new DataFileBrowser());

        fixture.unselectAll();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void unselectAll() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testUnselectAll_2()
        throws Exception {
        SelectionTracker fixture = new SelectionTracker(new DataFileBrowser());

        fixture.unselectAll();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }

    /**
     * Run the void unselectAll() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testUnselectAll_3()
        throws Exception {
        SelectionTracker fixture = new SelectionTracker(new DataFileBrowser());

        fixture.unselectAll();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        //       at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        //       at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
    }
}
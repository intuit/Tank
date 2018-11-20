package com.intuit.tank.prefs;

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

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.model.SelectItem;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.columns.Columns;
import org.primefaces.event.ColumnResizeEvent;
import org.primefaces.extensions.behavior.javascript.JavascriptBehavior;
import org.primefaces.extensions.component.dynaform.DynaForm;

import com.intuit.tank.PreferencesBean;
import com.intuit.tank.prefs.PreferencesChangedListener;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.project.ColumnPreferences;

/**
 * The class <code>TablePreferencesTest</code> contains tests for the class <code>{@link TablePreferences}</code>.
 *
 * @generatedBy CodePro at 12/15/14 3:51 PM
 */
public class TablePreferencesTest {
    /**
     * Run the TablePreferences(List<? extends ColumnPreferences>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testTablePreferences_1()
        throws Exception {
        List<? extends ColumnPreferences> prefs = new LinkedList();

        TablePreferences result = new TablePreferences(prefs);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertNotNull(result);
    }

    /**
     * Run the TablePreferences(List<? extends ColumnPreferences>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testTablePreferences_2()
        throws Exception {
        List<? extends ColumnPreferences> prefs = new LinkedList();

        TablePreferences result = new TablePreferences(prefs);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertNotNull(result);
    }

    /**
     * Run the TablePreferences(List<? extends ColumnPreferences>) constructor test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testTablePreferences_3()
        throws Exception {
        List<? extends ColumnPreferences> prefs = new LinkedList();

        TablePreferences result = new TablePreferences(prefs);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertNotNull(result);
    }

    /**
     * Run the int getMaxTotalSize(int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testGetMaxTotalSize_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        int max = 1;

        int result = fixture.getMaxTotalSize(max);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertEquals(1, result);
    }


    /**
     * Run the int getSize(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testGetSize_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        String colName = "";

        int result = fixture.getSize(colName);
        assertEquals(50, result);
    }

    /**
     * Run the int getTotalSize() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testGetTotalSize_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());

        int result = fixture.getTotalSize();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertEquals(20, result);
    }

    /**
     * Run the int getTotalSize() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testGetTotalSize_2()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());

        int result = fixture.getTotalSize();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertEquals(20, result);
    }

    /**
     * Run the int getTotalSize() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testGetTotalSize_3()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());

        int result = fixture.getTotalSize();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertEquals(20, result);
    }

    /**
     * Run the List<SelectItem> getVisibiltyList() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testGetVisibiltyList_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());

        List<SelectItem> result = fixture.getVisibiltyList();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertNotNull(result);
    }

    /**
     * Run the List<String> getVisibleColumns() method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testGetVisibleColumns_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());

        List<String> result = fixture.getVisibleColumns();

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertNotNull(result);
    }

    /**
     * Run the boolean isVisible(String) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testIsVisible_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        String colName = "";

        boolean result = fixture.isVisible(colName);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
        assertTrue(!result);
    }


    /**
     * Run the void registerListener(PreferencesChangedListener) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testRegisterListener_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        PreferencesChangedListener listener = new PreferencesBean();

        fixture.registerListener(listener);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }

    /**
     * Run the void setSize(String,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testSetSize_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        String colName = "";
        int newSize = 1;

        fixture.setSize(colName, newSize);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }

    /**
     * Run the void setSize(String,int) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testSetSize_2()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        String colName = "";
        int newSize = 1;

        fixture.setSize(colName, newSize);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }

    /**
     * Run the void setVisible(String,boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testSetVisible_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        String colName = "";
        boolean newVisible = true;

        fixture.setVisible(colName, newVisible);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }

    /**
     * Run the void setVisible(String,boolean) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testSetVisible_2()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        String colName = "";
        boolean newVisible = true;

        fixture.setVisible(colName, newVisible);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }

    /**
     * Run the void setVisibleColumns(List<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testSetVisibleColumns_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        List<String> colNames = new LinkedList();

        fixture.setVisibleColumns(colNames);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }

    /**
     * Run the void setVisibleColumns(List<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testSetVisibleColumns_2()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        List<String> colNames = new LinkedList();

        fixture.setVisibleColumns(colNames);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }

    /**
     * Run the void setVisibleColumns(List<String>) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testSetVisibleColumns_3()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        List<String> colNames = new LinkedList();

        fixture.setVisibleColumns(colNames);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }

    /**
     * Run the void unregisterListener(PreferencesChangedListener) method test.
     *
     * @throws Exception
     *
     * @generatedBy CodePro at 12/15/14 3:51 PM
     */
    @Test
    public void testUnregisterListener_1()
        throws Exception {
        TablePreferences fixture = new TablePreferences(new LinkedList());
        fixture.registerListener(new PreferencesBean());
        PreferencesChangedListener listener = new PreferencesBean();

        fixture.unregisterListener(listener);

        // An unexpected exception was thrown in user code while executing this test:
        //    java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        //       at com.intuit.tank.prefs.TablePreferences.<init>(TablePreferences.java:48)
    }
}
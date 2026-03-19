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

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.behavior.Behavior;
import jakarta.faces.model.SelectItem;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.Mockito;

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

    private TablePreferences createPrefsWithColumn(String colName, int size, boolean visible, boolean hideable) {
        ColumnPreferences cp = new ColumnPreferences(colName, colName + "_display", size,
                visible ? ColumnPreferences.Visibility.VISIBLE : ColumnPreferences.Visibility.HIDDEN,
                hideable ? ColumnPreferences.Hidability.HIDABLE : ColumnPreferences.Hidability.NON_HIDABLE);
        List<ColumnPreferences> prefs = new LinkedList<>();
        prefs.add(cp);
        return new TablePreferences(prefs);
    }

    @Test
    public void testSetSize_WhenColExists_UpdatesAndFiresEvent() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);
        PreferencesChangedListener listener = mock(PreferencesChangedListener.class);
        fixture.registerListener(listener);

        fixture.setSize("col1", 200);

        assertEquals(200, fixture.getSize("col1"));
        verify(listener).prefsChanged();
    }

    @Test
    public void testSetVisible_WhenColExists_UpdatesAndFiresEvent() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);
        PreferencesChangedListener listener = mock(PreferencesChangedListener.class);
        fixture.registerListener(listener);

        fixture.setVisible("col1", false);

        assertFalse(fixture.isVisible("col1"));
        verify(listener).prefsChanged();
    }

    @Test
    public void testIsVisible_WhenColExists_ReturnsCorrectValue() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);
        assertTrue(fixture.isVisible("col1"));

        TablePreferences fixture2 = createPrefsWithColumn("col2", 100, false, true);
        assertFalse(fixture2.isVisible("col2"));
    }

    @Test
    public void testGetSize_WhenColExists_ReturnsActualSize() {
        TablePreferences fixture = createPrefsWithColumn("col1", 150, true, true);
        assertEquals(150, fixture.getSize("col1"));
    }

    @Test
    public void testGetTotalSize_WithNonZeroColumns() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);
        int total = fixture.getTotalSize();
        // visible col with size 100 => (100 + 20) + 20 = 140
        assertEquals(140, total);
    }

    @Test
    public void testGetMaxTotalSize_WhenResultExceedsMax() {
        TablePreferences fixture = createPrefsWithColumn("col1", 500, true, true);
        // total would be (500+20)+40 = 560, max=100 => return max
        assertEquals(100, fixture.getMaxTotalSize(100));
    }

    @Test
    public void testGetMaxTotalSize_WhenResultUnderMax() {
        TablePreferences fixture = createPrefsWithColumn("col1", 50, true, true);
        // total = (50+20)+40 = 110, max=500 => return 110
        assertEquals(110, fixture.getMaxTotalSize(500));
    }

    @Test
    public void testSetVisibleColumns_WithActualCol_UpdatesVisibility() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);

        fixture.setVisibleColumns(new LinkedList<>());
        assertFalse(fixture.isVisible("col1"));

        fixture.setVisibleColumns(List.of("col1"));
        assertTrue(fixture.isVisible("col1"));
    }

    @Test
    public void testOnResize_CallsSetSizeWithExtractedId() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);

        ColumnResizeEvent event = mock(ColumnResizeEvent.class);
        org.primefaces.component.api.UIColumn column = mock(org.primefaces.component.api.UIColumn.class);
        when(event.getColumn()).thenReturn(column);
        when(column.getClientId()).thenReturn("form:table:col1");
        when(event.getWidth()).thenReturn(250);

        fixture.onResize(event);

        assertEquals(250, fixture.getSize("col1"));
    }

    @Test
    public void testFireEvent_NotifiesAllListeners() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);

        PreferencesChangedListener l1 = mock(PreferencesChangedListener.class);
        PreferencesChangedListener l2 = mock(PreferencesChangedListener.class);
        fixture.registerListener(l1);
        fixture.registerListener(l2);

        fixture.setSize("col1", 300);

        verify(l1).prefsChanged();
        verify(l2).prefsChanged();
    }

    @Test
    public void testUnregisterListener_RemovedListenerNotNotified() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);
        PreferencesChangedListener l1 = mock(PreferencesChangedListener.class);
        fixture.registerListener(l1);
        fixture.unregisterListener(l1);

        fixture.setSize("col1", 200);

        verify(l1, never()).prefsChanged();
    }

    @Test
    public void testGetVisibiltyList_WithHideableCol_ReturnsItem() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);
        List<SelectItem> list = fixture.getVisibiltyList();
        assertEquals(1, list.size());
        assertEquals("col1", list.get(0).getValue());
    }

    @Test
    public void testGetVisibleColumns_WithVisibleCol_ContainsColName() {
        TablePreferences fixture = createPrefsWithColumn("col1", 100, true, true);
        List<String> visible = fixture.getVisibleColumns();
        assertTrue(visible.contains("col1"));
    }
}
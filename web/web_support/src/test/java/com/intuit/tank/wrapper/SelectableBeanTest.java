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

import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;
import javax.faces.event.AjaxBehaviorEvent;

import org.junit.*;

import static org.junit.Assert.*;

import org.primefaces.extensions.behavior.javascript.JavascriptBehavior;
import org.primefaces.extensions.component.dynaform.DynaForm;

import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.DataFileBrowser;
import com.intuit.tank.view.filter.ViewFilterType;
import com.intuit.tank.wrapper.SelectableBean;
import com.intuit.tank.wrapper.SelectableWrapper;

/**
 * The class <code>SelectableBeanTest</code> contains tests for the class <code>{@link SelectableBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class SelectableBeanTest {
    /**
     * Run the void deleteSelected() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDeleteSelected_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.deleteSelected();
    }

    /**
     * Run the void deleteSelected() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDeleteSelected_2()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.deleteSelected();
    }

    /**
     * Run the void deleteSelected() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testDeleteSelected_3()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.deleteSelected();
    }

    /**
     * Run the List<SelectableWrapper<Object>> getFilteredData() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetFilteredData_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<SelectableWrapper<DataFile>> result = fixture.getFilteredData();
    }

    /**
     * Run the List<SelectableWrapper<Object>> getFilteredData() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetFilteredData_2()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<SelectableWrapper<DataFile>> result = fixture.getFilteredData();
    }

    /**
     * Run the List<SelectableWrapper<Object>> getSelectionList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectionList_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<SelectableWrapper<DataFile>> result = fixture.getSelectionList();
        assertNotNull(result);
    }

    /**
     * Run the List<SelectableWrapper<Object>> getSelectionList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectionList_2()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<SelectableWrapper<DataFile>> result = fixture.getSelectionList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the List<SelectableWrapper<Object>> getSelectionList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectionList_3()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<SelectableWrapper<DataFile>> result = fixture.getSelectionList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the List<SelectableWrapper<Object>> getSelectionList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetSelectionList_4()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<SelectableWrapper<DataFile>> result = fixture.getSelectionList();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the TablePreferences getTablePrefs() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetTablePrefs_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        TablePreferences result = fixture.getTablePrefs();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the TableViewState getTableState() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetTableState_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        TableViewState result = fixture.getTableState();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the ViewFilterType getViewFilterType() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetViewFilterType_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        ViewFilterType result = fixture.getViewFilterType();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.util.SelectionTracker.<init>(SelectionTracker.java:32)
        // at com.intuit.tank.wrapper.SelectableBean.<init>(SelectableBean.java:32)
        // at com.intuit.tank.project.DataFileBrowser.<init>(DataFileBrowser.java:43)
        assertNotNull(result);
    }

    /**
     * Run the ViewFilterType[] getViewFilterTypeList() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetViewFilterTypeList_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        ViewFilterType[] result = fixture.getViewFilterTypeList();
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
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.hasSelected();
        assertTrue(!result);
    }

    /**
     * Run the boolean hasSelected() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testHasSelected_2()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.hasSelected();
        assertTrue(!result);
    }

    /**
     * Run the void refresh() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testRefresh_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.refresh();
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
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.selectAll();
    }

    /**
     * Run the void setFilteredData(List<SelectableWrapper<T>>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetFilteredData_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<SelectableWrapper<DataFile>> filteredData = new LinkedList();

        fixture.setFilteredData(filteredData);
    }

    /**
     * Run the void setFilteredData(List<SelectableWrapper<T>>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetFilteredData_2()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<SelectableWrapper<DataFile>> filteredData = null;

        fixture.setFilteredData(filteredData);
    }

    /**
     * Run the void setViewFilterType(ViewFilterType) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetViewFilterType_1()
            throws Exception {
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ViewFilterType viewFilterType = ViewFilterType.ALL;

        fixture.setViewFilterType(viewFilterType);
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
        DataFileBrowser fixture = new DataFileBrowser();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.unselectAll();
    }
}
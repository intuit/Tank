package com.intuit.tank.project;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.Behavior;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.extensions.behavior.javascript.JavascriptBehavior;
import org.primefaces.extensions.component.dynaform.DynaForm;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

import com.intuit.tank.job.JobNodeBean;
import com.intuit.tank.job.ProjectNodeBean;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.project.ColumnPreferences;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.JobQueueManager;
import com.intuit.tank.project.JobTreeTableBean;
import com.intuit.tank.project.Project;

/**
 * The class <code>JobTreeTableBeanTest</code> contains tests for the class <code>{@link JobTreeTableBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class JobTreeTableBeanTest {

    /**
     * Run the List<String> getAllTpsKeys() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetAllTpsKeys_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        List<String> result = fixture.getAllTpsKeys();

    }

    /**
     * Run the int getRefreshInterval() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRefreshInterval_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        int result = fixture.getRefreshInterval();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
        assertEquals(0, result);
    }

    /**
     * Run the String getRefreshTimeSeconds() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRefreshTimeSeconds_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        fixture.setRefreshTimeSeconds("30");
        String result = fixture.getRefreshTimeSeconds();
        assertNotNull(result);
        assertEquals("30", fixture.getRefreshTimeSeconds());
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
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        TablePreferences result = fixture.getTablePrefs();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
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
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        TableViewState result = fixture.getTableState();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
        assertNotNull(result);
    }

    /**
     * Run the boolean isFilterFinished() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsFilterFinished_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.isFilterFinished();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
        assertTrue(result);
    }

    /**
     * Run the boolean isFilterFinished() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsFilterFinished_2()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.isFilterFinished();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
        assertTrue(result);
    }

    /**
     * Run the boolean isRefreshEnabled() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testIsRefreshEnabled_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.isRefreshEnabled();
        assertTrue(!result);
    }

    /**
     * Run the void keysChanged() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testKeysChanged_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        fixture.keysChanged();

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }

    /**
     * Run the void observe(JobQueue) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testObserve_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        JobQueue queueEvent = new JobQueue();

        fixture.observe(queueEvent);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }

    /**
     * Run the void setFilterFinished(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetFilterFinished_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        boolean filterFinished = true;

        fixture.setFilterFinished(filterFinished);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }

    /**
     * Run the void setFilterFinished(boolean) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetFilterFinished_2()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        boolean filterFinished = true;

        fixture.setFilterFinished(filterFinished);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }

    /**
     * Run the void setRefreshTimeSeconds(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetRefreshTimeSeconds_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String refreshTimeSeconds = "";

        fixture.setRefreshTimeSeconds(refreshTimeSeconds);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }

    /**
     * Run the void setRefreshTimeSeconds(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetRefreshTimeSeconds_2()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String refreshTimeSeconds = "";

        fixture.setRefreshTimeSeconds(refreshTimeSeconds);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }

    /**
     * Run the void setRefreshTimeSeconds(String) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetRefreshTimeSeconds_3()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        String refreshTimeSeconds = "";

        fixture.setRefreshTimeSeconds(refreshTimeSeconds);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }

    /**
     * Run the void setSelectedTpsKeys(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetSelectedTpsKeys_1()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<String> keys = new LinkedList();

        fixture.setSelectedTpsKeys(keys);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }

    /**
     * Run the void setSelectedTpsKeys(List<String>) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testSetSelectedTpsKeys_2()
            throws Exception {
        JobQueueManager fixture = new JobQueueManager();
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        List<String> keys = new LinkedList();

        fixture.setSelectedTpsKeys(keys);

        // An unexpected exception was thrown in user code while executing this test:
        // java.lang.NoClassDefFoundError: com_cenqua_clover/CoverageRecorder
        // at com.intuit.tank.dao.BaseDao.<init>(BaseDao.java:56)
        // at com.intuit.tank.dao.OwnableDao.<init>(OwnableDao.java:27)
        // at com.intuit.tank.dao.ProjectDao.<init>(ProjectDao.java:29)
        // at com.intuit.tank.project.JobTreeTableBean.<init>(JobTreeTableBean.java:96)
        // at com.intuit.tank.project.JobQueueManager.<init>(JobQueueManager.java:8)
    }
}
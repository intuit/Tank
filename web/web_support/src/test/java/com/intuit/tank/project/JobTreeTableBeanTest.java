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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.PreferencesBean;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.job.ProjectNodeBean;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.vmManager.models.UserDetail;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * The class <code>JobTreeTableBeanTest</code> contains tests for the class <code>{@link JobTreeTableBean}</code>.
 * 
 * @generatedBy CodePro at 12/15/14 3:52 PM
 */
public class JobTreeTableBeanTest {

    @InjectMocks
    private JobQueueManager fixture;

    @Mock
    private VMTracker vmTracker;

    @Mock
    private Security security;

    @Mock
    private PreferencesBean preferencesBean;

    @Mock
    JobQueueDao jobQueueDao;

    @Mock
    ProjectDao projectDao;

    @Mock
    Messages messages;

    private AutoCloseable closeable;

    @BeforeEach
    void initService() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    /**
     * Run the int getRefreshInterval() method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testGetRefreshInterval_1() {
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
    public void testGetRefreshTimeSeconds_1() {
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
    public void testGetTablePrefs_1() {
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
    public void testGetTableState_1() {
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
    public void testIsFilterFinished_1() {
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
    public void testIsFilterFinished_2() {
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
    public void testIsRefreshEnabled_1() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());

        boolean result = fixture.isRefreshEnabled();
        assertTrue(!result);
    }

    /**
     * Run the void observe(JobQueue) method test.
     * 
     * @throws Exception
     * 
     * @generatedBy CodePro at 12/15/14 3:52 PM
     */
    @Test
    public void testObserve_1() {
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
    public void testSetFilterFinished_1() {
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
    public void testSetFilterFinished_2() {
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
    public void testSetRefreshTimeSeconds_1() {
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
    public void testSetRefreshTimeSeconds_2() {
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
    public void testSetRefreshTimeSeconds_3() {
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

    @Test
    public void testRefreshData() {

        JobQueue jobQueue = new JobQueue();
        jobQueue.setProjectId(10);
        jobQueue.addJob(new JobInstance());
        Mockito.when(jobQueueDao.findRecent(Mockito.any(Date.class))).thenReturn(List.of(jobQueue));
        Mockito.when(projectDao.findById(Mockito.anyInt())).thenReturn(new Project());

        AWSXRay.beginSegment("test");
        fixture.refreshData();
        AWSXRay.endSegment();
    }

    @Test
    public void testCurrentJobInstanceForTPS() {
        fixture.setCurrentJobInstanceForTPS(new ProjectNodeBean(new Project()));
    }

    @Test
    public void testCurrentJobInstanceForUser() {
        List<UserDetail> details = new ArrayList<>();
        details.add(new UserDetail("script1", 10));
        details.add(new UserDetail("script2", 20));
        details.add(new UserDetail("script3", 30));
        List<UserDetail> details2 = new ArrayList<>();
        details2.add(new UserDetail("script1", 40));
        details2.add(new UserDetail("script2", 50));
        details2.add(new UserDetail("script3", 60));
        List<UserDetail> details3 = new ArrayList<>();
        details3.add(new UserDetail("script1", 40));
        details3.add(new UserDetail("script2", 30));
        details3.add(new UserDetail("script3", 20));
        LocalDateTime now = LocalDateTime.of(2023, 12, 25, 10, 30);
        Map<Date, List<UserDetail>> statusDetails = Map.of(
                Date.from(Instant.from(now.atZone(ZoneId.systemDefault()))), details,
                Date.from(Instant.from(now.minusSeconds(30).atZone(ZoneId.systemDefault()))), details2,
                Date.from(Instant.from(now.minusSeconds(60).atZone(ZoneId.systemDefault()))), new ArrayList<>(),
                Date.from(Instant.from(now.minusSeconds(120).atZone(ZoneId.systemDefault()))), details3);

        ProjectNodeBean pnb = new ProjectNodeBean(new Project());
        pnb.setStatusDetailMap(statusDetails);
        fixture.setCurrentJobInstanceForUser(pnb);

        assertEquals(3, fixture.getChartModel().getData().getDataSet().size());
        assertEquals("[10:28:00, 10:29:00, 10:29:30, 10:30:00]", fixture.getChartModel().getData().getLabels().toString());
    }

    @Test
    public void testDeleteJobInstance() {
        fixture.deleteJobInstance(new ProjectNodeBean(new Project()));
    }
}
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
import com.intuit.tank.job.ActJobNodeBean;
import com.intuit.tank.job.JobNodeBean;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.job.ProjectNodeBean;
import com.intuit.tank.util.Messages;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.UserDetail;
import org.apache.commons.lang3.time.FastDateFormat;
import org.junit.jupiter.api.*;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.TreeNode;

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
    public void testRefreshData_WithVmContainer_CoversVmStatusBranch() {
        JobQueue jobQueue = new JobQueue();
        jobQueue.setProjectId(10);
        JobInstance ji = new JobInstance();
        jobQueue.addJob(ji);
        Mockito.when(jobQueueDao.findRecent(Mockito.any(Date.class))).thenReturn(List.of(jobQueue));
        Mockito.when(projectDao.findById(Mockito.anyInt())).thenReturn(new Project());

        // vmTracker returns a container with VM statuses - exercises the container != null branch
        CloudVmStatusContainer container = new CloudVmStatusContainer();
        Mockito.when(vmTracker.getVmStatusForJob(Mockito.anyString())).thenReturn(container);

        AWSXRay.beginSegment("test_vm_container");
        fixture.refreshData();
        AWSXRay.endSegment();
    }

    @Test
    public void testRefreshData_WithAdhocTrackerJobs_CoversAdhocBranch() {
        // Empty queue from DB
        Mockito.when(jobQueueDao.findRecent(Mockito.any(Date.class))).thenReturn(List.of());

        // Tracker has a job not in any queue
        CloudVmStatusContainer trackerContainer = new CloudVmStatusContainer();
        trackerContainer.setJobId("999");
        Mockito.when(vmTracker.getAllJobs()).thenReturn(java.util.Set.of(trackerContainer));
        Mockito.when(vmTracker.getVmStatusForJob(Mockito.eq("999"))).thenReturn(trackerContainer);
        Mockito.when(jobQueueDao.findForJobId(Mockito.eq(999))).thenReturn(null);
        Mockito.when(preferencesBean.getDateTimeFormat()).thenReturn(FastDateFormat.getInstance("yyyy-MM-dd"));

        AWSXRay.beginSegment("test_adhoc");
        fixture.refreshData();
        AWSXRay.endSegment();
    }

    @Test
    public void testInit_SetsTablePrefs() {
        com.intuit.tank.project.Preferences prefs = Mockito.mock(com.intuit.tank.project.Preferences.class);
        Mockito.when(preferencesBean.getPreferences()).thenReturn(prefs);
        Mockito.when(prefs.getJobsTableColumns()).thenReturn(new LinkedList<>());

        fixture.init();

        assertNotNull(fixture.tablePrefs);
    }

    @Test
    public void testCurrentJobInstanceForTPS() {
        ProjectNodeBean pnb = new ProjectNodeBean(new Project());
        pnb.addJob(new ActJobNodeBean("1", new CloudVmStatusContainer(), FastDateFormat.getInstance()));
        pnb.addJob(new ActJobNodeBean("2", new CloudVmStatusContainer(), FastDateFormat.getInstance()));
        fixture.setCurrentJobInstanceForTPS(pnb);

        assertEquals("{\"data\":" +
                "{\"datasets\":[{\"label\":\"Total TPS\",\"lineTension\":0.2}]}," +
                "\"options\":{\"responsive\":true,\"maintainAspectRatio\":false,\"plugins\":{\"title\":{\"display\":true,\"text\":\"TPS Chart\"},\"legend\":{\"position\":\"right\"}},\"scales\":{\"y\":{\"type\":\"linear\",\"beginAtZero\":true}}},\"type\":\"line\"}", fixture.getTpsChartModel());
    }

    @Test
    public void testCurrentJobInstanceForUser() {
        List<UserDetail> details1 = new ArrayList<>();
        details1.add(new UserDetail("script1", 10));
        details1.add(new UserDetail("script2", 20));
        details1.add(new UserDetail("script3", 30));
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
                Date.from(Instant.from(now.atZone(ZoneId.systemDefault()))), Collections.emptyList(),
                Date.from(Instant.from(now.plusSeconds(30).atZone(ZoneId.systemDefault()))), details1,
                Date.from(Instant.from(now.plusSeconds(60).atZone(ZoneId.systemDefault()))), Collections.emptyList(),
                Date.from(Instant.from(now.plusSeconds(90).atZone(ZoneId.systemDefault()))), details2,
                Date.from(Instant.from(now.plusSeconds(120).atZone(ZoneId.systemDefault()))), details3,
                Date.from(Instant.from(now.plusSeconds(150).atZone(ZoneId.systemDefault()))), Collections.emptyList());

        ProjectNodeBean pnb = new ProjectNodeBean(new Project());
        pnb.setStatusDetailMap(statusDetails);
        fixture.setCurrentJobInstanceForUser(pnb);

        assertEquals("{\"data\":" +
                "{\"labels\":[\"10:30:30\",\"10:31:00\",\"10:31:30\",\"10:32:00\",\"10:32:30\"],\"datasets\":[" +
                "{\"data\":[10,null,40,40,null],\"label\":\"script1\",\"lineTension\":0.2}," +
                "{\"data\":[20,null,50,30,null],\"label\":\"script2\",\"lineTension\":0.2}," +
                "{\"data\":[30,null,60,20,null],\"label\":\"script3\",\"lineTension\":0.2}]}," +
                "\"options\":{\"responsive\":true,\"maintainAspectRatio\":false,\"plugins\":{\"title\":{\"display\":true,\"text\":\"Users Chart\"},\"legend\":{\"position\":\"right\"}},\"scales\":{\"y\":{\"type\":\"linear\",\"beginAtZero\":true}}},\"type\":\"line\"}", fixture.getChartModel());

    }

    @Test
    public void testDeleteJobInstance() {
        fixture.deleteJobInstance(new ProjectNodeBean(new Project()));
    }

    @Test
    public void testGetTimeZone_DefaultLosAngeles() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        java.util.TimeZone tz = fixture.getTimeZone();
        assertNotNull(tz);
        assertEquals("America/Los_Angeles", tz.getID());
    }

    @Test
    public void testSetTimeZone_UpdatesTimeZone() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        fixture.setTimeZone("UTC");
        assertEquals("UTC", fixture.getTimeZone().getID());
    }

    @Test
    public void testGetChartModel_InitiallyNull() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        assertNull(fixture.getChartModel());
    }

    @Test
    public void testGetTpsChartModel_InitiallyNull() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        assertNull(fixture.getTpsChartModel());
    }

    @Test
    public void testGetCurrentJobInstance_InitiallyNull() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        assertNull(fixture.getCurrentJobInstance());
    }

    @Test
    public void testSetCurrentJobInstance_SetsValue() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        ProjectNodeBean node = new ProjectNodeBean(new Project());
        fixture.setCurrentJobInstance(node);
        assertEquals(node, fixture.getCurrentJobInstance());
    }

    @Test
    public void testSetCurrentJobInstanceForUser_NullInstance_SetsNull() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        fixture.setCurrentJobInstanceForUser(null);
        assertNull(fixture.getCurrentJobInstance());
        assertNull(fixture.getChartModel()); // chartModel stays null when currentJobInstance is null
    }

    @Test
    public void testSetRefreshTimeSeconds_ValidAboveMin_SetsInterval() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        fixture.setRefreshTimeSeconds("30");
        assertEquals(30, fixture.getRefreshInterval());
        assertTrue(fixture.isRefreshEnabled());
    }

    @Test
    public void testSetRefreshTimeSeconds_ValidBelowMin_WarnMessage() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        fixture.setRefreshTimeSeconds("5");
        assertEquals(0, fixture.getRefreshInterval()); // not changed
        Mockito.verify(messages).warn(Mockito.anyString());
    }

    @Test
    public void testSetRefreshTimeSeconds_NonNumeric_WarnMessage() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        fixture.setRefreshTimeSeconds("notanumber");
        assertEquals(0, fixture.getRefreshInterval());
        Mockito.verify(messages).warn(Mockito.anyString());
    }

    @Test
    public void testSetRefreshTimeSeconds_EmptyString_SetsZero() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        fixture.setRefreshTimeSeconds("30"); // set to 30 first
        fixture.setRefreshTimeSeconds("");   // then clear
        assertEquals(0, fixture.getRefreshInterval());
    }

    @Test
    public void testSetFilterFinished_SameValue_NoRefresh() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        // default is true; setting true again does nothing
        fixture.setFilterFinished(true);
        assertTrue(fixture.isFilterFinished());
    }

    @Test
    public void testIsRefreshEnabled_WhenIntervalSet_ReturnsTrue() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        fixture.setRefreshTimeSeconds("15");
        assertTrue(fixture.isRefreshEnabled());
    }

    @Test
    public void testSetFilterFinished_DifferentValue_TriggersRefresh() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        Mockito.when(jobQueueDao.findRecent(Mockito.any(Date.class))).thenReturn(List.of());
        AWSXRay.beginSegment("test_ff_diff");
        // default filterFinished is true; setting false triggers refreshData()
        fixture.setFilterFinished(false);
        AWSXRay.endSegment();
        assertFalse(fixture.isFilterFinished());
    }

    @Test
    public void testCanControlJob_DelegatesToNode() {
        JobNodeBean node = Mockito.mock(JobNodeBean.class);
        Mockito.when(node.canControlJob(Mockito.any(Security.class))).thenReturn(true);
        assertTrue(fixture.canControlJob(node));

        Mockito.when(node.canControlJob(Mockito.any(Security.class))).thenReturn(false);
        assertFalse(fixture.canControlJob(node));
    }

    private TreeNode buildTreeAndGetRoot() {
        fixture.tableState = new TableViewState();
        fixture.tablePrefs = new TablePreferences(new LinkedList());
        JobQueue jobQueue = new JobQueue();
        jobQueue.setProjectId(10);
        jobQueue.addJob(new JobInstance());
        Mockito.when(jobQueueDao.findRecent(Mockito.any(Date.class))).thenReturn(List.of(jobQueue));
        Mockito.when(projectDao.findById(Mockito.anyInt())).thenReturn(new Project());
        return fixture.getRootNode();
    }

    @Test
    public void testOnNodeExpand_SetsNodeExpanded() {
        AWSXRay.beginSegment("test_expand");
        buildTreeAndGetRoot(); // populates nodeMap with "0"

        NodeExpandEvent event = Mockito.mock(NodeExpandEvent.class);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        JobNodeBean jnb = Mockito.mock(JobNodeBean.class);
        Mockito.when(event.getTreeNode()).thenReturn(treeNode);
        Mockito.when(treeNode.getData()).thenReturn(jnb);
        Mockito.when(jnb.getId()).thenReturn("0"); // project node id is "0"

        assertDoesNotThrow(() -> fixture.onNodeExpand(event));
        AWSXRay.endSegment();
    }

    @Test
    public void testOnNodeCollapse_SetsNodeCollapsedAndParentsExpanded() {
        AWSXRay.beginSegment("test_collapse");
        buildTreeAndGetRoot(); // populates nodeMap with "0"

        NodeCollapseEvent event = Mockito.mock(NodeCollapseEvent.class);
        TreeNode treeNode = Mockito.mock(TreeNode.class);
        JobNodeBean jnb = Mockito.mock(JobNodeBean.class);
        Mockito.when(event.getTreeNode()).thenReturn(treeNode);
        Mockito.when(treeNode.getData()).thenReturn(jnb);
        Mockito.when(jnb.getId()).thenReturn("0"); // project node id is "0"

        assertDoesNotThrow(() -> fixture.onNodeCollapse(event));
        AWSXRay.endSegment();
    }
}
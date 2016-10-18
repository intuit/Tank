/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.primefaces.model.chart.ChartSeries;

import com.intuit.tank.PreferencesBean;
import com.intuit.tank.PropertyComparer;
import com.intuit.tank.PropertyComparer.SortOrder;
import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.model.v1.cloud.ProjectStatusContainer;
import com.intuit.tank.api.model.v1.cloud.UserDetail;
import com.intuit.tank.api.model.v1.cloud.ValidationStatus;
import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.job.ActJobNodeBean;
import com.intuit.tank.job.JobNodeBean;
import com.intuit.tank.job.ProjectNodeBean;
import com.intuit.tank.job.VMNodeBean;
import com.intuit.tank.prefs.TablePreferences;
import com.intuit.tank.prefs.TableViewState;
import com.intuit.tank.reporting.api.ResultsReader;
import com.intuit.tank.reporting.api.TPSInfo;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.vm.common.util.MethodTimer;

/**
 * JobTreeTableBean
 * 
 * @author dangleton
 * 
 */
public abstract class JobTreeTableBean implements Serializable {

    private static final String TOTAL_TPS_SERIES_KEY = "Total TPS";
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(JobTreeTableBean.class);

    private static final int MIN_REFRESH = 10;
    private static final int INITIAL_SIZE = 10;

    protected TreeNode rootNode;

    @Inject
    private VMTracker tracker;

    @Inject
    private Messages messages;
    
    @Inject
    private Security security;
    
    @Inject
    private PreferencesBean preferencesBean;

    @Inject
    private ExceptionHandler exceptionHandler;

    private ProjectDao projectDao = new ProjectDao();

    private boolean filterFinished = true;
    private String refreshTimeSeconds;
    private int refreshInterval;

    private JobNodeBean currentJobInstance;
    private TrackingCartesianChartModel chartModel;
    private TrackingCartesianChartModel tpsChartModel;
    private List<String> allTpsKeys;
    private Map<String, List<String>> selectedTpsKeys = new HashMap<String, List<String>>();

    protected abstract Integer getRootJobId();

    private Map<String, TreeNode> nodeMap = new HashMap<String, TreeNode>();
//    private Map<Date, Map<String, TPSInfo>> tpsMap = new HashMap<Date, Map<String, TPSInfo>>();

    @Inject
    private PreferencesBean userPrefs;

    protected TablePreferences tablePrefs;
    protected TableViewState tableState = new TableViewState();
    private Date lastDate = null;

    @PostConstruct
    public void init() {
        tablePrefs = new TablePreferences(userPrefs.getPreferences().getJobsTableColumns());
        tablePrefs.registerListener(userPrefs);
    }

    /**
     * @return the tablePrefs
     */
    public TablePreferences getTablePrefs() {
        return tablePrefs;
    }

    /**
     * @return the tableState
     */
    public TableViewState getTableState() {
        return tableState;
    }

    public void deleteJobInstance(JobNodeBean bean) {
        if (bean.isDeleteable()) {
            try {
                JobInstance jobInstance = new JobInstanceDao().findById(Integer.parseInt(bean.getId()));
                JobQueueDao jobQueueDao = new JobQueueDao();
                Workload workload = new WorkloadDao().findById(jobInstance.getWorkloadId());
                JobQueue queue = jobQueueDao.findOrCreateForProjectId(workload.getProject().getId());
                JobInstance instance = null;
                for (JobInstance job : queue.getJobs()) {
                    if (job.getId() == jobInstance.getId()) {
                        instance = job;
                        break;
                    }
                }
                if (instance != null) {
                    queue.getJobs().remove(instance);
                    queue = new JobQueueDao().saveOrUpdate(queue);
                }
                refreshData();
                messages.info("Job " + jobInstance.getName() + " has been deleted.");
            } catch (Exception e) {
                LOG.error("Error deleting node with id of " + bean.getId(), e);
                exceptionHandler.handle(e);
            }
        } else {
            messages.warn(bean.getName() + " cannot be deleted.");
        }
    }

    /**
     * 
     * @return
     */
    public boolean isRefreshEnabled() {
        return refreshInterval >= MIN_REFRESH;
    }

    public JobNodeBean getCurrentJobInstance() {
        return currentJobInstance;
    }

    public void setCurrentJobInstance(JobNodeBean currentJobInstance) {
        this.currentJobInstance = currentJobInstance;
    }

    public void setCurrentJobInstanceForUser(JobNodeBean currentJobInstance) {
        setCurrentJobInstance(currentJobInstance);
        initChartModel();
    }

    public void setCurrentJobInstanceForTPS(JobNodeBean currentJobInstance) {
        setCurrentJobInstance(currentJobInstance);
//        tpsMap.clear();
//        lastDate = new Date(0);
        initializeTpsModel();
    }

    public void keysChanged() {
        initializeTpsModel();
    }

    /**
     * @return the allTpsKeys
     */
    public List<String> getAllTpsKeys() {
        return allTpsKeys;
    }

    /**
     * @return the selectedTpsKeys
     */
    public List<String> getSelectedTpsKeys() {
        if (currentJobInstance != null) {
            return selectedTpsKeys.get(currentJobInstance.getName());
        }
        return null;
    }

    /**
     * @return the selectedTpsKeys
     */
    public void setSelectedTpsKeys(List<String> keys) {
        if (currentJobInstance != null) {
            selectedTpsKeys.put(currentJobInstance.getName(), keys);
        }
    }

    /**
     * @return the chartModel
     */
    public TrackingCartesianChartModel getChartModel() {
        return chartModel;
    }

    /**
     * @return the tpsChartModel
     */
    public TrackingCartesianChartModel getTpsChartModel() {
        return tpsChartModel;
    }

    private void initChartModel() {
        LOG.info("Initializing user chart model...");
        chartModel = null;
        if (currentJobInstance != null && currentJobInstance.getStatusDetailMap() != null) {
            chartModel = new TrackingCartesianChartModel();

            Map<String, ChartSeries> seriesMap = new HashMap<String, ChartSeries>();
            Map<Date, List<UserDetail>> detailMap = currentJobInstance.getStatusDetailMap();
            List<Date> dateList = new ArrayList<Date>(detailMap.keySet());
            Collections.sort(dateList);
            for (Date d : dateList) {
                for (UserDetail detail : detailMap.get(d)) {
                    ChartSeries series = seriesMap.get(detail.getScript());
                    if (series == null) {
                        series = new ChartSeries(detail.getScript());
                        chartModel.addSeries(series);
                        seriesMap.put(detail.getScript(), series);
                    }
                    series.set(d.getTime(), detail.getUsers());
                    chartModel.addDate(d);
                }
            }
            chartModel.setExtender("userDetailsExtender");
        }

    }

    private void initializeTpsModel() {
        LOG.info("Initializing TPS chart model...");

        MethodTimer mt = new MethodTimer(LOG, getClass(), "initializeTpsModel");
        tpsChartModel = null;
        if (currentJobInstance != null) {
            Set<String> keySet = new HashSet<String>();
            List<String> list = selectedTpsKeys.get(currentJobInstance.getName());
            boolean initKeys = false;
            if (list == null) {
                list = new ArrayList<String>();
                list.add(TOTAL_TPS_SERIES_KEY);
                selectedTpsKeys.put(currentJobInstance.getName(), list);
                initKeys = true;
            }
            tpsChartModel = new TrackingCartesianChartModel();
            tpsChartModel.setExtender("tpsDetailsExtender");
            Map<String, ChartSeries> seriesMap = new HashMap<String, ChartSeries>();
            Map<Date, Map<String, TPSInfo>> tpsDetailMap = getTpsMap();
            mt.markAndLog("get tpsMap from DynamoDb");
            List<Date> dateList = new ArrayList<Date>(tpsDetailMap.keySet());
            Collections.sort(dateList);
//            if (dateList.size() > 0) {
//                lastDate = new Date(dateList.get(dateList.size() - 1).getTime() + 1000);
//            }
            ChartSeries totalSeries = new ChartSeries(TOTAL_TPS_SERIES_KEY);
            for (Date d : dateList) {
                int total = 0;
                for (TPSInfo info : tpsDetailMap.get(d).values()) {
                    if (initKeys && list.size() < INITIAL_SIZE) {
                        list.add(info.getKey());
                    }
                    keySet.add(info.getKey());
                    if (list.contains(info.getKey())) {
                        ChartSeries series = seriesMap.get(info.getKey());
                        if (series == null) {
                            series = new ChartSeries(info.getKey());
                            tpsChartModel.addSeries(series);
                            seriesMap.put(info.getKey(), series);
                        }
                        series.set(d.getTime(), info.getTPS());
                    }
                    total += info.getTPS();
                    tpsChartModel.addDate(d);
                }
                totalSeries.set(d.getTime(), total);
            }
            if (list.contains(TOTAL_TPS_SERIES_KEY) && totalSeries.getData().size() > 0) {
                tpsChartModel.addSeries(totalSeries);
            }
            allTpsKeys = new ArrayList<String>(keySet);
            Collections.sort(allTpsKeys);
            allTpsKeys.add(0, TOTAL_TPS_SERIES_KEY);
        } else {
            LOG.info("currentJobInstance is null");
        }
        mt.endAndLog();
    }

    private Map<Date, Map<String, TPSInfo>> getTpsMap() {
        Map<Date, Map<String, TPSInfo>> ret = new HashMap<Date, Map<String, TPSInfo>>();
        try {
            JobNodeBean vmInstance = null;
            List<JobNodeBean> jobNodes = new ArrayList<JobNodeBean>();
            if (currentJobInstance.getType().equalsIgnoreCase("project")) {
                // get all jobIds
                if (currentJobInstance.getSubNodes() != null) {
                    jobNodes.addAll(currentJobInstance.getSubNodes());
                }
            } else if (currentJobInstance.getType().equalsIgnoreCase("job")) {
                jobNodes.add(currentJobInstance);
            } else if (currentJobInstance.getType().equalsIgnoreCase("vm")) {
                vmInstance = currentJobInstance;
            }
            ResultsReader resultsReader = ReportingFactory.getResultsReader();
            if (!jobNodes.isEmpty()) {
                List<String> jobs = new ArrayList<String>();
                for (JobNodeBean jobNode : jobNodes) {
                    jobs.add(jobNode.getJobId());
                }
                ret = resultsReader.getTpsMapForJob(this.lastDate, jobs.toArray(new String[jobs.size()]));
            }
            if (vmInstance != null) {
                ret = resultsReader.getTpsMapForInstance(this.lastDate, vmInstance.getJobId(), vmInstance.getId());
            }

        } catch (Exception e) {
            LOG.error("Error getting TPS map.");
        }
        return ret;
    }

    /**
     * 
     * @return
     */
    public int getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * @return the refreshTimeSeconds
     */
    public String getRefreshTimeSeconds() {
        return refreshTimeSeconds;
    }

    /**
     * @param refreshTimeSeconds
     *            the refreshTimeSeconds to set
     */
    public void setRefreshTimeSeconds(String refreshTimeSeconds) {
        if (NumberUtils.isNumber(refreshTimeSeconds)) {
            int num = Integer.parseInt(refreshTimeSeconds);
            if (num >= MIN_REFRESH) {
                refreshInterval = num;
                this.refreshTimeSeconds = refreshTimeSeconds;
            } else {
                messages.warn("Refresh Interval must be at least " + MIN_REFRESH + " seconds.");
            }
        } else if (StringUtils.isEmpty(refreshTimeSeconds)) {
            refreshInterval = 0;
            this.refreshTimeSeconds = refreshTimeSeconds;
        } else {
            messages.warn("Refresh Interval must be an interger with a minimum value of " + MIN_REFRESH + " seconds.");
        }
    }

    /**
     * @return the filterFinished
     */
    public boolean isFilterFinished() {
        return filterFinished;
    }

    /**
     * @param filterFinished
     *            the filterFinished to set
     */
    public void setFilterFinished(boolean filterFinished) {
        if (filterFinished != this.filterFinished) {
            this.filterFinished = filterFinished;
            refreshData();
        }
    }

    /**
     * Refreshes the data nodes.
     */

    public void refreshData() {
        rootNode = null;
        this.getRootNode();
    }

    /**
     * @return the rootNode
     */
    public TreeNode getRootNode() {
        if (rootNode == null) {
            buildTree();
            updateExpansionStatus(rootNode);
            refreshCurrentJobInstance(rootNode);
            rootNode.setSelected(false);
        }
        return rootNode;
    }

    private void refreshCurrentJobInstance(TreeNode rootNode2) {
        JobNodeBean currJob = getCurrentJobInstance();
        if (currJob != null && currJob.equals(rootNode2.getData())) {
            setCurrentJobInstance((JobNodeBean) rootNode2.getData());
            return;
        }

        for (TreeNode node : rootNode2.getChildren()) {
            refreshCurrentJobInstance(node);
        }
    }

    private void updateExpansionStatus(TreeNode rootNode2) {
        for (TreeNode node : rootNode2.getChildren()) {
            updateExpansionStatus(node);
            if (node.getData() != null) {
                String id = ((JobNodeBean) node.getData()).getId();
                if (nodeMap.get(id) != null) {
                    node.setExpanded(nodeMap.get(id).isExpanded());
                }
                nodeMap.put(id, node);
            }
        }
    }

    private void buildTree() {
        MethodTimer mt = new MethodTimer(LOG, this.getClass(), "buildTree");
        Integer rootJob = getRootJobId();
        JobQueueDao jqd = new JobQueueDao();
        Set<String> trackerJobs = getTrackerJobIds();
        mt.markAndLog("get tracker jobs");
        Map<Integer, TreeNode> jobNodeMap = new HashMap<Integer, TreeNode>();
        if (rootJob == null || rootJob == 0) {
            List<JobQueue> queuedJobs = jqd.findRecent();
            mt.markAndLog("find all active jobs");
            rootNode = new DefaultTreeNode("root", null);
            for (JobQueue jobQueue : queuedJobs) {
                TreeNode projectNode = createJobNode(trackerJobs, jobQueue);
                if (projectNode != null && projectNode.getChildCount() != 0) {
                    jobNodeMap.put(jobQueue.getProjectId(), projectNode);
                    projectNode.setParent(rootNode);
                    rootNode.getChildren().add(projectNode);
                }
            }
            mt.markAndLog("Added all queued Jobs");
            if (!trackerJobs.isEmpty()) {
                TreeNode unknownNode = new DefaultTreeNode(getProjectNodeBean("unknown"), null);
                for (String id : trackerJobs) {// left over nodes that the tracker is tracking
                    // create job nodes now
                    createAdhocJobNode(jqd, jobNodeMap, unknownNode, id);
                }
                if (unknownNode.getChildCount() > 0) {
                    unknownNode.setParent(rootNode);
                    rootNode.getChildren().add(unknownNode);
                }
                mt.markAndLog("Added all unknown Jobs");
            }
        } else {
            JobQueue jobQueue = jqd.findOrCreateForProjectId(rootJob);
            rootNode = createJobNode(trackerJobs, jobQueue);
        }
        mt.endAndLog();
    }

    // private void buildTree() {
    // MethodTimer mt = new MethodTimer(LOG, this.getClass(), "buildTree");
    // Integer rootJob = getRootJobId();
    // JobQueueDao jqd = new JobQueueDao();
    // Set<String> trackerJobs = getTrackerJobIds();
    //
    // mt.markAndLog("get tracker jobs");
    // if (rootJob == null || rootJob == 0) {
    // JobInstanceDao jobInstanceDao = new JobInstanceDao();
    // List<JobInstance> activeJobs = jobInstanceDao.findNotCompleted();
    // mt.markAndLog("find all active jobs");
    //
    // List<JobQueue> queuedJobs = jqd.findForJobs(activeJobs);
    // mt.markAndLog("find all active job queues");
    // // List<JobQueue> queuedJobs = jqd.findAll();
    // Collections.sort(queuedJobs, new PropertyComparer<JobQueue>(JobQueue.PROPERTY_PROJECT_ID));
    // rootNode = new DefaultTreeNode("root", null);
    // for (JobQueue jobQueue : queuedJobs) {
    // TreeNode projectNode = createJobNode(trackerJobs, jobQueue);
    // if (projectNode != null && projectNode.getChildCount() != 0) {
    // projectNode.setParent(rootNode);
    // rootNode.getChildren().add(projectNode);
    // }
    // }
    // mt.markAndLog("Added all queued Jobs");
    // if (!trackerJobs.isEmpty()) {
    // TreeNode projectNode = new DefaultTreeNode(getProjectNodeBean("unknown"), rootNode);
    // for (String id : trackerJobs) {
    // // create job nodes now
    // TreeNode adhocNode = createAdhocJobNode(id);
    // adhocNode.setParent(projectNode);
    // projectNode.getChildren().add(adhocNode);
    // }
    // mt.markAndLog("Added all unknown Jobs");
    // }
    // } else {
    // JobQueue jobQueue = jqd.findOrCreateForProjectId(rootJob);
    // rootNode = createJobNode(trackerJobs, jobQueue);
    // }
    // mt.endAndLog();
    // }

    /**
     * @param trackerJobs
     * @param jobQueue
     */
    private TreeNode createJobNode(Set<String> trackerJobs, JobQueue jobQueue) {
        MethodTimer mt = new MethodTimer(LOG, getClass(), "createJobNode for project " + jobQueue.getProjectId());
        TreeNode projectNode = null;
        Project p = projectDao.findById(jobQueue.getProjectId());
        // mt.markAndLog("getProject");
        if (p != null) {
            // Map<Date, Map<String, TPSInfo>> totalTPSDetails = new HashMap<Date, Map<String, TPSInfo>>();
            ProjectNodeBean pnb = new ProjectNodeBean(p);
            boolean hasRights = security.isOwner(p);
            projectNode = new DefaultTreeNode(pnb, null);
            List<JobInstance> jobs = new ArrayList<JobInstance>(jobQueue.getJobs());
            // mt.markAndLog("get jobs");
            Collections.sort(jobs, new PropertyComparer<JobInstance>(JobInstance.PROPERTY_ID, SortOrder.DESCENDING));
            int projectActive = 0;
            int projectTotal = 0;
            ValidationStatus projectFailures = new ValidationStatus();
            for (JobInstance jobInstance : jobs) {
                CloudVmStatusContainer container = tracker.getVmStatusForJob(Integer.toString(jobInstance.getId()));
                trackerJobs.remove(Integer.toString(jobInstance.getId()));
                if (!filterFinished || jobInstance.getEndTime() == null) {
                    ActJobNodeBean jobInstanceNode = new ActJobNodeBean(jobInstance, hasRights, preferencesBean.getDateTimeFormat());
                    pnb.addJob(jobInstanceNode);
                    TreeNode jobNode = new DefaultTreeNode(jobInstanceNode, null);
                    List<VMNodeBean> vmNodes = getVMStatus(jobInstance, hasRights);
                    int jobInstanceActive = 0;
                    int jobInstanceTotal = 0;
                    ValidationStatus jobInstanceFailures = new ValidationStatus();
                    for (VMNodeBean vmNodeBean : vmNodes) {
                        jobInstanceNode.addVMBean(vmNodeBean);
                        new DefaultTreeNode(vmNodeBean, jobNode);
                        if (NumberUtils.isNumber(vmNodeBean.getActiveUsers())) {
                            jobInstanceActive += Integer.parseInt(vmNodeBean.getActiveUsers());
                        }
                        if (NumberUtils.isNumber(vmNodeBean.getTotalUsers())) {
                            jobInstanceTotal += Integer.parseInt(vmNodeBean.getTotalUsers());
                        }
                        jobInstanceFailures.addFailures(vmNodeBean.getNumFailures());
                        jobInstanceNode.setTps(vmNodeBean.getTps() + jobInstanceNode.getTps());
                    }
                    jobInstanceNode.setNumFailures(jobInstanceFailures);
                    if (container != null) {
                        jobInstanceNode.setUserDetails(container.getUserDetails());
                        jobInstanceNode.setStatusDetailMap(container.getDetailMap());
                        // jobInstanceNode.setTpsDetailMap(container.getTpsMap());
                        // combineTpsDetails(totalTPSDetails, container.getTpsMap());
                    }
                    jobInstanceNode.setActiveUsers(Integer.toString(jobInstanceActive));
                    projectActive += jobInstanceActive;
                    projectTotal += jobInstanceTotal;
                    projectFailures.addFailures(jobInstanceFailures);
                    jobNode.setParent(projectNode);
                    projectNode.getChildren().add(jobNode);
                    jobInstanceNode.reCalculate();
                    pnb.setTps(pnb.getTps() + jobInstanceNode.getTps());
                }
            }
            pnb.setActiveUsers(Integer.toString(projectActive));
            // mt.markAndLog("processed job instances.");

            pnb.setTotalUsers(Integer.toString(projectTotal));
            // pnb.setTpsDetailMap(totalTPSDetails);
            pnb.setNumFailures(projectFailures);
            pnb.reCalculate();
            ProjectStatusContainer projectStatusContainer = tracker.getProjectStatusContainer(Integer.toString(p
                    .getId()));
            if (projectStatusContainer != null) {
                pnb.setUserDetails(projectStatusContainer.getUserDetails());
                pnb.setStatusDetailMap(projectStatusContainer.getDetailMap());
            }
            pnb.reCalculate();
        }
        mt.endAndLog();
        return projectNode;
    }

    private TreeNode createAdhocJobNode(JobQueueDao jqd, Map<Integer, TreeNode> jobNodeMap, TreeNode parent,
            String jobId) {
        // this needs to be a JobNode, not a projectNode
        // need to make new constructor for ActJobNodeBean that just sets empty strings?
        CloudVmStatusContainer container = tracker.getVmStatusForJob(jobId);
        ActJobNodeBean jobBeanNode = new ActJobNodeBean(jobId, container, preferencesBean.getDateTimeFormat());
        JobQueue jq = jqd.findForJobId(Integer.valueOf(jobId));
        if (jq != null) {
            TreeNode projectNode = jobNodeMap.get(jq.getProjectId());
            if (projectNode != null) {
                parent = projectNode;
            }
        }
        TreeNode adhocNode = new DefaultTreeNode(jobBeanNode, null);
        List<VMNodeBean> vmNodes = getVMStatus(jobId, true);
        int nodeActive = 0;
        int nodeTotal = 0;
        ValidationStatus nodeFailures = new ValidationStatus();
        for (VMNodeBean vmNodeBean : vmNodes) {
            new DefaultTreeNode(vmNodeBean, adhocNode);
            jobBeanNode.addVMBean(vmNodeBean);
            if (NumberUtils.isNumber(vmNodeBean.getActiveUsers())) {
                nodeActive += Integer.parseInt(vmNodeBean.getActiveUsers());
            }
            if (NumberUtils.isNumber(vmNodeBean.getTotalUsers())) {
                nodeTotal += Integer.parseInt(vmNodeBean.getTotalUsers());
            }
            nodeFailures.addFailures(vmNodeBean.getNumFailures());
            jobBeanNode.setTps(jobBeanNode.getTps() + vmNodeBean.getTps());
        }
        jobBeanNode.setStatusDetailMap(container.getDetailMap());
        jobBeanNode.setJobId(jobId);
        jobBeanNode.setId(jobId);
        // jobBeanNode.setTpsDetailMap(container.getTpsMap());
        jobBeanNode.setActiveUsers(Integer.toString(nodeActive));
        jobBeanNode.setTotalUsers(Integer.toString(nodeTotal));
        jobBeanNode.setNumFailures(nodeFailures);
        jobBeanNode.setUserDetails(container.getUserDetails());
        jobBeanNode.reCalculate();
        adhocNode.setParent(parent);
        parent.getChildren().add(adhocNode);
        return adhocNode;
    }

    /**
     * 
     */
    private Set<String> getTrackerJobIds() {
        Set<String> result = new HashSet<String>();
        Set<CloudVmStatusContainer> allJobs = tracker.getAllJobs();
        for (CloudVmStatusContainer c : allJobs) {
            result.add(c.getJobId());
        }
        return result;
    }

    private List<VMNodeBean> getVMStatus(JobInstance jobInstance, boolean hasRights) {
        return getVMStatus(String.valueOf(jobInstance.getId()), hasRights);
    }

    private List<VMNodeBean> getVMStatus(String jobId, boolean hasRights) {
        List<VMNodeBean> vmNodes = new ArrayList<VMNodeBean>();
        CloudVmStatusContainer container = tracker.getVmStatusForJob(jobId);
        if (container != null) {
            Set<CloudVmStatus> statuses = container.getStatuses();
            for (CloudVmStatus cloudVmStatus : statuses) {
                VMNodeBean vmNode = new VMNodeBean(cloudVmStatus, hasRights, preferencesBean.getDateTimeFormat());
                vmNode.setStatusDetailMap(container.getDetailMap());
                vmNode.setTps(cloudVmStatus.getTotalTps());
                vmNodes.add(vmNode);
            }
        }
        return vmNodes;
    }

    private ProjectNodeBean getProjectNodeBean(String projectName) {
        return new ProjectNodeBean(projectName);

    }

    public void onNodeExpand(NodeExpandEvent event) {
        JobNodeBean jnb = (JobNodeBean) event.getTreeNode().getData();
        String id = jnb.getId();
        nodeMap.get(id).setExpanded(true);
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        JobNodeBean jnb = (JobNodeBean) event.getTreeNode().getData();
        String id = jnb.getId();
        TreeNode node = nodeMap.get(id);
        node.setExpanded(false);
        TreeNode parent = node.getParent();
        while (parent != null) {
            parent.setExpanded(true);
            parent = parent.getParent();
        }
    }

    public boolean canControlJob(JobNodeBean node) {
        return node.canControlJob(security);
    }
}

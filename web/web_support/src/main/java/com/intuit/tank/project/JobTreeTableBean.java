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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.intuit.tank.vm.vmManager.models.*;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

import com.amazonaws.xray.AWSXRay;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;
import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import com.intuit.tank.PreferencesBean;
import com.intuit.tank.PropertyComparer;
import com.intuit.tank.PropertyComparer.SortOrder;
import com.intuit.tank.vm.vmManager.VMTracker;
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
import software.xdev.chartjs.model.charts.LineChart;
import software.xdev.chartjs.model.data.LineData;
import software.xdev.chartjs.model.dataset.LineDataset;
import software.xdev.chartjs.model.options.LegendOptions;
import software.xdev.chartjs.model.options.LineOptions;
import software.xdev.chartjs.model.options.Plugins;
import software.xdev.chartjs.model.options.Title;

/**
 * JobTreeTableBean
 * 
 * @author dangleton
 * 
 */
public abstract class JobTreeTableBean implements Serializable {

    private static final Logger LOG = LogManager.getLogger(JobTreeTableBean.class);
    private static final String TOTAL_TPS_SERIES_KEY = "Total TPS";
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_TIME_ZONE = "America/Los_Angeles";
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private static final int MIN_REFRESH = 10;
    private static final int INITIAL_SIZE = 10;

    protected TreeNode rootNode;

    @Inject
    private VMTracker vmTracker;

    @Inject
    private Messages messages;

    @Inject
    private Security security;
    
    @Inject
    private PreferencesBean preferencesBean;

    @Inject
    private ExceptionHandler exceptionHandler;

    private ProjectDao projectDao = new ProjectDao();

    private JobQueueDao jobQueueDao = new JobQueueDao();

    private boolean filterFinished = true;
    private String refreshTimeSeconds;
    private int refreshInterval;

    private JobNodeBean currentJobInstance;
    private String chartModel;
    private String tpsChartModel;

    protected abstract Integer getRootJobId();

    private Map<String, TreeNode> nodeMap = new HashMap<String, TreeNode>();
//    private Map<Date, Map<String, TPSInfo>> tpsMap = new HashMap<Date, Map<String, TPSInfo>>();

    protected TablePreferences tablePrefs;
    protected TableViewState tableState = new TableViewState();
    private TimeZone timeZone = TimeZone.getTimeZone(DEFAULT_TIME_ZONE);
    private Date lastDate = null;

    @PostConstruct
    public void init() {
        tablePrefs = new TablePreferences(preferencesBean.getPreferences().getJobsTableColumns());
        tablePrefs.registerListener(preferencesBean);
    }

    /**
     * @return the user TimeZone
     */
    public TimeZone getTimeZone() {
        return timeZone;
    }

    /**
     * @param timeZone  the browsers TimeZone
     */
    public void setTimeZone(String timeZone) {
        this.timeZone = TimeZone.getTimeZone(timeZone);
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
                JobInstance jobInstance = new JobInstanceDao().findById(Integer.valueOf(bean.getId()));
                Workload workload = new WorkloadDao().findById(jobInstance.getWorkloadId());
                JobQueue queue = jobQueueDao.findOrCreateForProjectId(workload.getProject().getId());
                JobInstance instance = queue.getJobs().stream().filter(job -> job.getId() == jobInstance.getId()).findFirst().orElse(null);
                if (instance != null) {
                    queue.getJobs().remove(instance);
                    jobQueueDao.saveOrUpdate(queue);
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
        initializeTpsModel();
    }

    /**
     * @return the chartModel
     */
    public String getChartModel() {
        return chartModel;
    }

    /**
     * @return the tpsChartModel
     */
    public String getTpsChartModel() {
        return tpsChartModel;
    }

    private void initChartModel() {
        LOG.info("Initializing user chart model...");
        chartModel = null;
        if (currentJobInstance != null && currentJobInstance.getStatusDetailMap() != null) {
            List<String> labels = new ArrayList<>();
            Map<String, ArrayList<Number>> datasetMap = new HashMap<>();
            Map<Date, List<UserDetail>> detailMap = currentJobInstance.getStatusDetailMap();
            detailMap.keySet().stream().sorted()
                    .forEach(d -> {
                        labels.add(sdf.format(d.getTime()));
                        detailMap.get(d)
                                .forEach(detail -> {
                                    ArrayList<Number> dataset = datasetMap.computeIfAbsent(detail.getScript(), k -> new ArrayList<>());
                                    dataset.add(detail.getUsers());
                                });
                        datasetMap.forEach((key, value) -> {
                            while (value.size() < labels.size()) {
                                value.add(null);
                            }
                        });
                    });
            LineData lineData = new LineData();
            lineData.setLabels(labels);
            datasetMap.forEach((key, value) -> {
                lineData.addDataset(new LineDataset()
                        .setData(value)
                        .setLabel(key)
                        .setLineTension(0.5f));
                    });
            chartModel = new LineChart()
                    .setData(lineData)
                    .setOptions(new LineOptions()
                            .setResponsive(true)
                            .setMaintainAspectRatio(false)
                            .setPlugins(new Plugins()
                                    .setLegend(new LegendOptions().setPosition("right"))
                                    .setTitle(new Title()
                                            .setDisplay(true)
                                            .setText("TPS Chart")))
                    ).toJson();
        }
    }

    private void initializeTpsModel() {
        LOG.info("Initializing TPS chart model...");
        AWSXRay.beginSubsegment("Initialize TpsModel");
        tpsChartModel = null;
        if (currentJobInstance != null) {
            List<String> labels = new ArrayList<>();
            Map<String, ArrayList<Number>> datasetMap = new HashMap<>();
            Map<Date, Map<String, TPSInfo>> tpsDetailMap = getTpsMap();
            tpsDetailMap.keySet().stream().sorted()
                    .forEach(d -> {
                        labels.add(sdf.format(d.getTime()));
                        tpsDetailMap.get(d).values()
                                .forEach(info -> {
                                    ArrayList<Number> dataset = datasetMap.computeIfAbsent(info.getKey(), k -> new ArrayList<>());
                                    dataset.add(info.getTPS());
                                    datasetMap.get(TOTAL_TPS_SERIES_KEY).add(info.getTPS());
                                });
                        datasetMap.forEach((key, value) -> {
                            while (value.size() < labels.size()) {
                                value.add(null);
                            }
                        });
                    });

            ArrayList<Number> total = new ArrayList<>();
            datasetMap.forEach((key, value) -> value
                    .forEach(v -> {
                        if (total.size() < value.size()) { total.add(0); }
                        total.set(value.indexOf(v), (int) total.get(value.indexOf(v)) + (int) v);
            }));
            datasetMap.put(TOTAL_TPS_SERIES_KEY, total);

            LineData lineData = new LineData();
            lineData.setLabels(labels);
            datasetMap.forEach((key, value) -> {
                lineData.addDataset(new LineDataset()
                        .setData(value)
                        .setLabel(key)
                        .setLineTension(0.5f));
            });
            tpsChartModel = new LineChart()
                    .setData(lineData)
                    .setOptions(new LineOptions()
                            .setResponsive(true)
                            .setMaintainAspectRatio(false)
                            .setPlugins(new Plugins()
                                    .setLegend(new LegendOptions().setPosition("right"))
                                    .setTitle(new Title()
                                            .setDisplay(true)
                                            .setText("TPS Chart")))
                    ).toJson();
        } else {
            LOG.info("currentJobInstance is null");
        }
        AWSXRay.endSubsegment();
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
                ret = resultsReader.getTpsMapForJob(this.lastDate, jobNodes.stream().map(JobNodeBean::getJobId).toArray(String[]::new));
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
        if (NumberUtils.isCreatable(refreshTimeSeconds)) {
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

    private void refreshCurrentJobInstance(TreeNode<TreeNode> rootNode2) {
        JobNodeBean currJob = getCurrentJobInstance();
        if (currJob != null && currJob.equals(rootNode2.getData())) {
            setCurrentJobInstance((JobNodeBean) rootNode2.getData());
            return;
        }

        for (TreeNode node : rootNode2.getChildren()) {
            refreshCurrentJobInstance(node);
        }
    }

    private void updateExpansionStatus(TreeNode<TreeNode> rootNode2) {
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
        AWSXRay.beginSubsegment("Build.Tree");
        Integer rootJob = getRootJobId();
        Set<String> trackerJobs = getTrackerJobIds();
        Map<Integer, TreeNode> jobNodeMap = new HashMap<Integer, TreeNode>();
        if (rootJob == null || rootJob == 0) {
            AWSXRay.beginSubsegment("Build.Tree.findRecent");
            Date dateMinus1Week = DateUtils.addWeeks(new Date(),-1);
            List<JobQueue> queuedJobs = jobQueueDao.findRecent(dateMinus1Week);
            AWSXRay.endSubsegment();
            rootNode = new DefaultTreeNode("root", null);
            for (JobQueue jobQueue : queuedJobs) {
                TreeNode projectNode = createJobNode(trackerJobs, jobQueue);
                if (projectNode != null && projectNode.getChildCount() != 0) {
                    jobNodeMap.put(jobQueue.getProjectId(), projectNode);
                    projectNode.setParent(rootNode);
                    rootNode.getChildren().add(projectNode);
                }
            }
            if (!trackerJobs.isEmpty()) {
                TreeNode unknownNode = new DefaultTreeNode(new ProjectNodeBean("unknown"), null);
                for (String id : trackerJobs) {// left over nodes that the tracker is tracking
                    // create job nodes now
                    createAdhocJobNode(jobNodeMap, unknownNode, id);
                }
                if (unknownNode.getChildCount() > 0) {
                    unknownNode.setParent(rootNode);
                    rootNode.getChildren().add(unknownNode);
                }
            }
        } else {
            JobQueue jobQueue = jobQueueDao.findOrCreateForProjectId(rootJob);
            rootNode = createJobNode(trackerJobs, jobQueue);
        }
        AWSXRay.endSubsegment();
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
        AWSXRay.beginSubsegment("Create.JobNode.ProjectId." + jobQueue.getProjectId());
        TreeNode projectNode = null;
        Project p = projectDao.findById(jobQueue.getProjectId());  // This is an expensive repetitive operation
        if (p != null) {
            // Map<Date, Map<String, TPSInfo>> totalTPSDetails = new HashMap<Date, Map<String, TPSInfo>>();
            ProjectNodeBean pnb = new ProjectNodeBean(p);
            boolean hasRights = security.isOwner(p);
            projectNode = new DefaultTreeNode(pnb, null);
            List<JobInstance> jobs = new ArrayList<>(jobQueue.getJobs());
            jobs.sort(new PropertyComparer<JobInstance>(JobInstance.PROPERTY_ID, SortOrder.DESCENDING));
            int projectActive = 0;
            int projectTotal = 0;
            ValidationStatus projectFailures = new ValidationStatus();
            for (JobInstance jobInstance : jobs) {

                trackerJobs.remove(Integer.toString(jobInstance.getId()));
                if (!filterFinished || jobInstance.getEndTime() == null) {
                    ActJobNodeBean jobInstanceNode = new ActJobNodeBean(jobInstance, hasRights, preferencesBean.getDateTimeFormat());
                    pnb.addJob(jobInstanceNode);
                    TreeNode jobNode = new DefaultTreeNode(jobInstanceNode, null);
                    int jobInstanceActive = 0;
                    int jobInstanceTotal = 0;
                    ValidationStatus jobInstanceFailures = new ValidationStatus();
                    CloudVmStatusContainer container = vmTracker.getVmStatusForJob(Integer.toString(jobInstance.getId()));
                    if ( container != null ) {
                        List<VMNodeBean> vmNodes = getVMStatus(container, hasRights);

                        for (VMNodeBean vmNodeBean : vmNodes) {
                            jobInstanceNode.addVMBean(vmNodeBean);
                            new DefaultTreeNode(vmNodeBean, jobNode);
                            if (NumberUtils.isCreatable(vmNodeBean.getActiveUsers())) {
                                jobInstanceActive += Integer.parseInt(vmNodeBean.getActiveUsers());
                            }
                            if (NumberUtils.isCreatable(vmNodeBean.getTotalUsers())) {
                                jobInstanceTotal += Integer.parseInt(vmNodeBean.getTotalUsers());
                            }
                            jobInstanceFailures.addFailures(vmNodeBean.getNumFailures());
                            jobInstanceNode.setTps(vmNodeBean.getTps() + jobInstanceNode.getTps());
                        }

                        jobInstanceNode.setUserDetails(container.getUserDetails());
                        jobInstanceNode.setStatusDetailMap(container.getDetailMap());
                        // jobInstanceNode.setTpsDetailMap(container.getTpsMap());
                        // combineTpsDetails(totalTPSDetails, container.getTpsMap());
                    }
                    jobInstanceNode.setNumFailures(jobInstanceFailures);
                    jobInstanceNode.setActiveUsers(Integer.toString(jobInstanceActive));
                    jobInstanceNode.setIncrementStrategy(jobInstance.getIncrementStrategy().getDisplay());
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

            pnb.setTotalUsers(Integer.toString(projectTotal));
            // pnb.setTpsDetailMap(totalTPSDetails);
            pnb.setNumFailures(projectFailures);
            pnb.reCalculate();
            ProjectStatusContainer projectStatusContainer = vmTracker.getProjectStatusContainer(Integer.toString(p
                    .getId()));
            if (projectStatusContainer != null) {
                pnb.setUserDetails(projectStatusContainer.getUserDetails());
                pnb.setStatusDetailMap(projectStatusContainer.getDetailMap());
            }
            pnb.reCalculate();
        }
        AWSXRay.endSubsegment();
        return projectNode;
    }

    private TreeNode createAdhocJobNode(Map<Integer, TreeNode> jobNodeMap, TreeNode parent, String jobId) {
        AWSXRay.beginSubsegment("Create.AdhocNode.JobId." + jobId);
        // this needs to be a JobNode, not a projectNode
        // need to make new constructor for ActJobNodeBean that just sets empty strings?
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(jobId);
        ActJobNodeBean jobBeanNode = new ActJobNodeBean(jobId, container, preferencesBean.getDateTimeFormat());
        JobQueue jq = jobQueueDao.findForJobId(Integer.valueOf(jobId));
        if (jq != null) {
            TreeNode projectNode = jobNodeMap.get(jq.getProjectId());
            if (projectNode != null) {
                parent = projectNode;
            }
        }
        TreeNode adhocNode = new DefaultTreeNode(jobBeanNode, null);
        List<VMNodeBean> vmNodes = getVMStatus(container, true);
        int nodeActive = 0;
        int nodeTotal = 0;
        ValidationStatus nodeFailures = new ValidationStatus();
        for (VMNodeBean vmNodeBean : vmNodes) {
            new DefaultTreeNode(vmNodeBean, adhocNode);
            jobBeanNode.addVMBean(vmNodeBean);
            if (NumberUtils.isCreatable(vmNodeBean.getActiveUsers())) {
                nodeActive += Integer.parseInt(vmNodeBean.getActiveUsers());
            }
            if (NumberUtils.isCreatable(vmNodeBean.getTotalUsers())) {
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
        AWSXRay.endSubsegment();
        return adhocNode;
    }

    /**
     * 
     */
    private Set<String> getTrackerJobIds() {
        Set<CloudVmStatusContainer> allJobs = vmTracker.getAllJobs();
        return allJobs.stream().map(CloudVmStatusContainer::getJobId).collect(Collectors.toSet());
    }

    private List<VMNodeBean> getVMStatus(@Nonnull CloudVmStatusContainer container, boolean hasRights) {
        List<VMNodeBean> vmNodes = new ArrayList<VMNodeBean>();
        for (CloudVmStatus cloudVmStatus : container.getStatuses()) {
            VMNodeBean vmNode = new VMNodeBean(cloudVmStatus, hasRights, preferencesBean.getDateTimeFormat());
            vmNode.setStatusDetailMap(container.getDetailMap());
            vmNode.setTps(cloudVmStatus.getTotalTps());
            vmNodes.add(vmNode);
        }
        return vmNodes;
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

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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.intuit.tank.auth.TankSecurityContext;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;

import com.intuit.tank.PreferencesBean;
import com.intuit.tank.ProjectBean;
import com.intuit.tank.dao.BaseDao;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.util.TestParameterContainer;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.settings.VmInstanceType;

@Named
@ConversationScoped
public class JobMaker implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LogManager.getLogger(JobMaker.class);

    private ProjectBean projectBean;
    
    private String name;

    private String submitInfo;

    @Inject
    @Modified
    private Event<JobQueue> jobQueueEvent;

    @Inject
    private Event<JobEvent> jobEventProducer;

    @Inject
    private Messages messages;

    @Inject
    private PreferencesBean preferences;

    @Inject
    private UsersAndTimes usersAndTimes;

    protected JobInstance proposedJobInstance;

    private String jobDetails;

    @Inject
    private TankSecurityContext securityContext;

    /**
     * 
     */
    public JobMaker() {
    }

    public void init(ProjectBean projectBean) {
    	this.projectBean = projectBean;
    }

    /**
     * 
     * @return
     */
    public String getTankClientClass() {
        return projectBean.getJobConfiguration().getTankClientClass();
    }
    /**
     * 
     * @return
     */
    public void setTankClientClass(String tankClientClass) {
        projectBean.getJobConfiguration().setTankClientClass(tankClientClass);
    }

    /**
     * 
     * @return
     */
    public String getLoggingProfile() {
        return projectBean.getJobConfiguration().getLoggingProfile();
    }
    
    /**
     * 
     * @param loggingProfile
     */
    public void setLoggingProfile(String loggingProfile) {
        projectBean.getJobConfiguration().setLoggingProfile(loggingProfile);
    }

    /**
     * @return the stopBehavior
     */
    public String getStopBehavior() {
        return projectBean.getJobConfiguration().getStopBehavior();
    }

    /**
     * @param stopBehavior
     *            the stopBehavior to set
     */
    public void setStopBehavior(String stopBehavior) {
        projectBean.getJobConfiguration().setStopBehavior(stopBehavior);
    }

    /**
     * 
     * @return
     */
    public String getName() {
        if(projectBean.getJobConfiguration().getIncrementStrategy().equals(IncrementStrategy.increasing)) {
            return !StringUtils.isEmpty(name) ? name : projectBean.getName() + "_" + usersAndTimes.getTotalUsers()
                    + "_users_"
                    + preferences.getTimestampFormat().format(new Date());
        } else {
            return !StringUtils.isEmpty(name) ? name : projectBean.getName() + "_nonlinear_"
                    + preferences.getTimestampFormat().format(new Date());
        }
    }

    /**
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     */
    public String getReportingMode() {
        return projectBean.getJobConfiguration().getReportingMode();
    }

    /**
     * 
     * @param reportingMode
     */
    public void setReportingMode(String reportingMode) {
        projectBean.getJobConfiguration().setReportingMode(reportingMode);
    }

    /**
     * 
     * @return
     */
    public String getLocation() {
        return projectBean.getJobConfiguration().getLocation();
    }

    /**
     * 
     * @return
     */
    public String getVmInstanceType() {
        return projectBean.getJobConfiguration().getVmInstanceType();
    }

    /**
     * 
     * @return
     */
    public void setVmInstanceType(String type) {
        if (StringUtils.isNotEmpty(type)) {
            if (!type.equals(getVmInstanceType())) {
                setNumUsersPerAgent(getDefaultNumUsers(type));
            }
            projectBean.getJobConfiguration().setVmInstanceType(type);
        }
    }

    private int getDefaultNumUsers(String type) {
        return new TankConfig().getVmManagerConfig()
                .getInstanceTypes().stream()
                .filter(t -> t.getName().equals(type))
                .findFirst().map(VmInstanceType::getUsers)
                .orElse(-1);
    }

    /**
     * 
     * @return
     */
    public int getNumUsersPerAgent() {
        return projectBean.getJobConfiguration().getNumUsersPerAgent();
    }

    /**
     * 
     * @return
     */
    public void setNumUsersPerAgent(int numUsers) {
        if (numUsers > 0) {
            projectBean.getJobConfiguration().setNumUsersPerAgent(numUsers);
        }
    }

    public double getTargetRatePerAgent() {
        return (projectBean.getJobConfiguration().getTargetRatePerAgent() != null) ?
                projectBean.getJobConfiguration().getTargetRatePerAgent() : 1.00 ;
    }

    public void setTargetRatePerAgent(double targetRatePerAgent) {
        if (targetRatePerAgent > 0.0) {
            projectBean.getJobConfiguration().setTargetRatePerAgent(targetRatePerAgent);
        }
    }

    public int getNumAgents() {
        return projectBean.getJobConfiguration().getNumAgents();
    }

    public void setNumAgents(int numAgents) {
        if (numAgents > 0) {
            projectBean.getJobConfiguration().setNumAgents(numAgents);
        }
    }

    /**
     * 
     * @return
     */
    public boolean isUseEips() {
        return projectBean.getJobConfiguration().isUseEips();
    }
    
    /**
     * 
     * @return
     */
    public void setUseEips(boolean b) {
            projectBean.getJobConfiguration().setUseEips(b);
    }

    /**
     *
     * @return
     */
    public boolean isUseTwoStep() {
        return projectBean.getJobConfiguration().isUseTwoStep();
    }

    /**
     *
     * @return
     */
    public void setUseTwoStep(boolean b) {
        projectBean.getJobConfiguration().setUseTwoStep(b);
    }

    /**
     * 
     * @param location
     */
    public void setLocation(String location) {
        projectBean.getJobConfiguration().setLocation(location);
    }

    /**
     * @return the submitInfo
     */
    public String getSubmitInfo() {
        return submitInfo;
    }

    /**
     * @param submitInfo
     *            the submitInfo to set
     */
    public void setSubmitInfo(String submitInfo) {
        this.submitInfo = submitInfo;
    }

    /**
     * 
     * @return
     */
    public JobInstance getProposedJobInstance() {
        return proposedJobInstance;
    }

    /**
     * 
     */
    public void createJobInstance() {
        proposedJobInstance = null;
        jobDetails = null;
        if (projectBean.doSave()) {
            DataFileDao dataFileDao = new DataFileDao();
            JobNotificationDao jobNotificationDao = new JobNotificationDao();
            JobRegionDao jobRegionDao = new JobRegionDao();
            Workload workload = projectBean.getWorkload();
            proposedJobInstance = new JobInstance(workload, getName());
            proposedJobInstance.setLoggingProfile(getLoggingProfile());
            proposedJobInstance.setCreator(securityContext.getCallerPrincipal().getName());
            proposedJobInstance.setScheduledTime(new Date());
            proposedJobInstance.setUseEips(isUseEips());
            proposedJobInstance.setUseTwoStep((isUseTwoStep()));
            proposedJobInstance.setTankClientClass(getTankClientClass());
            proposedJobInstance.setLocation(getLocation());
            proposedJobInstance.setVmInstanceType(getVmInstanceType());
            proposedJobInstance.setNumUsersPerAgent(getNumUsersPerAgent());
            proposedJobInstance.setNumAgents(getNumAgents());
            proposedJobInstance.setReportingMode(getReportingMode());
            proposedJobInstance.getVariables().putAll(workload.getJobConfiguration().getVariables());
            // set version info
            proposedJobInstance.getDataFileVersions().addAll(
                    getVersions(dataFileDao, workload.getJobConfiguration().getDataFileIds(), DataFile.class));
            proposedJobInstance.getNotificationVersions().addAll(
                    getVersions(jobNotificationDao, workload.getJobConfiguration().getNotifications()));
            Set<JobRegion> jobRegions = JobRegionDao.cleanRegions(usersAndTimes.getJobRegions());
            proposedJobInstance.setVariables(new HashMap<String, String>(workload.getJobConfiguration()
                    .getVariables()));
            proposedJobInstance.setAllowOverride(workload.getJobConfiguration().isAllowOverride());
            proposedJobInstance.getJobRegionVersions().addAll(getVersions(jobRegionDao, jobRegions));
            JobValidator validator = new JobValidator(workload.getTestPlans(), proposedJobInstance.getVariables(),
                    false);
            long maxDuration = workload.getTestPlans().stream()
                    .mapToLong(plan -> validator.getDurationMs(plan.getName()))
                    .max().orElse(0);
            TestParameterContainer times = TestParamUtil.evaluateTestTimes(maxDuration, projectBean
                    .getJobConfiguration().getRampTimeExpression(), projectBean.getJobConfiguration()
                    .getSimulationTimeExpression());
            proposedJobInstance.setExecutionTime(maxDuration);
            proposedJobInstance.setRampTime(times.getRampTime());
            proposedJobInstance.setSimulationTime(times.getSimulationTime());
            int totalVirtualUsers = jobRegions.stream()
                    .mapToInt(region -> (int) TestParamUtil.evaluateExpression(region.getUsers(), maxDuration,
                            times.getSimulationTime(), times.getRampTime())).sum();
            proposedJobInstance.setTotalVirtualUsers(totalVirtualUsers);
        }
    }

    public String getJobDetails() {
        if (jobDetails == null) {
            try {
                jobDetails = createJobDetails();
            } catch (Exception e) {
                LOG.error("Error building jobDetails: {}", e, e);
                return "Error building jobDetails: ";
            }
        }
        return jobDetails;
    }

    private String createJobDetails() {
        if (proposedJobInstance != null) {
            Workload workload = projectBean.getWorkload();
            JobValidator validator = new JobValidator(workload.getTestPlans(), proposedJobInstance.getVariables(),
                    false);
            return JobDetailFormatter.createJobDetails(validator, workload, proposedJobInstance);
        }
        return "Job is incomplete.";
    }

    public void addJobToQueue() {
        if (proposedJobInstance != null) {
            JobQueueDao jobQueueDao = new JobQueueDao();
            Workload workload = projectBean.getWorkload();
            JobQueue queue = jobQueueDao.findOrCreateForProjectId(projectBean.getProject().getId());
            proposedJobInstance.setJobDetails(jobDetails);
            queue.addJob(proposedJobInstance);
            jobQueueDao.saveOrUpdate(queue);
            storeScript(Integer.toString(proposedJobInstance.getId()), workload, proposedJobInstance);
            messages.info("Job has been submitted successfully");
            jobQueueEvent.fire(queue);
            jobEventProducer.fire(new JobEvent(Integer.toString(proposedJobInstance.getId()), "",
                    JobLifecycleEvent.QUEUE_ADD));
            setName(null);
            proposedJobInstance = null;
        }
    }

    public void save() {

    }

    private void storeScript(String jobId, Workload workload, JobInstance job) {
        new WorkloadDao().loadScriptsForWorkload(workload);
        HDWorkload hdWorkload = ConverterUtil.convertWorkload(workload, job);
        String scriptString = ConverterUtil.getWorkloadXML(hdWorkload);
        ProjectDaoUtil.storeScriptFile(jobId, scriptString);
    }

    /**
     * @return
     */
    public boolean isValid() {
        if (proposedJobInstance == null) {
            return false;
        }
        if (StringUtils.isEmpty(name)) {
            return false;
        }
        if (proposedJobInstance.getTotalVirtualUsers() <= 0 && proposedJobInstance.getIncrementStrategy().equals(IncrementStrategy.increasing)) {
            return false;
        }
        if (proposedJobInstance.getTerminationPolicy() == TerminationPolicy.time
                && proposedJobInstance.getSimulationTime() == 0) {
            return false;
        }
        int userPercentage = projectBean.getWorkload().getTestPlans().stream().mapToInt(TestPlan::getUserPercentage).sum();
        if (userPercentage != 100) {
            return false;
        }
        if(proposedJobInstance.getIncrementStrategy().equals(IncrementStrategy.standard)){
            int regionPercentage = projectBean.getWorkload().getJobConfiguration().getJobRegions().stream()
                    .mapToInt(r -> Integer.parseInt(r.getPercentage())).sum();
            if (regionPercentage != 100) {
                return false;
            }
        }
        return hasScripts();
    }

    private boolean hasScripts() {
        return projectBean.getWorkload().getTestPlans().stream()
                .flatMap(testPlan -> testPlan.getScriptGroups().stream())
                .anyMatch(scriptGroup -> !scriptGroup.getScriptGroupSteps().isEmpty());
    }

    /**
     * @param dao
     * @param dataFileIds
     * @param entityClass
     * @return
     */
    @SuppressWarnings("rawtypes")
    private Set<EntityVersion> getVersions(BaseDao dao, Set<Integer> dataFileIds,
            Class<? extends BaseEntity> entityClass) {
        HashSet<EntityVersion> result = new HashSet<EntityVersion>();
        for (Integer id : dataFileIds) {
            int versionId = dao.getHeadRevisionNumber(id);
            result.add(new EntityVersion(id, versionId, entityClass));
        }
        return result;
    }

    /**
     * @param dao
     * @param entities
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Set<EntityVersion> getVersions(BaseDao dao, Set<? extends BaseEntity> entities) {
        HashSet<Integer> ids = new HashSet<Integer>();
        Class entityClass = null;
        for (BaseEntity entity : entities) {
            ids.add(entity.getId());
            entityClass = entity.getClass();
        }
        return getVersions(dao, ids, entityClass);
    }

}

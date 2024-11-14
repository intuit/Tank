package com.intuit.tank;

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
import java.util.*;
import java.util.stream.Collectors;

import com.intuit.tank.project.*;
import jakarta.enterprise.context.ConversationScoped;
import jakarta.enterprise.context.Conversation;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import com.amazonaws.xray.AWSXRay;
import com.intuit.tank.auth.TankSecurityContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.intuit.tank.util.Messages;

import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.settings.AccessRight;

@Named
@ConversationScoped
public class ProjectBean implements Serializable {

    private static final Logger LOG = LogManager.getLogger(ProjectBean.class);

    private static final long serialVersionUID = 1L;
    private Project project;

    @Inject
    private UsersAndTimes usersAndTimes;

    @Inject
    private TankSecurityContext securityContext;
    
    @Inject
    private Conversation conversation;
    
    @Inject
    private Security security;

    @Inject
    private JobMaker jobMaker;

    @Inject
    private AssociateDataFileBean dataFileBean;

    @Inject
    private WorkloadScripts workloadScripts;

    @Inject
    private NotificationsEditor notificationsEditor;

    @Inject
    private ProjectVariableEditor projectVariableEditor;

    @Inject
    private Messages messages;

    @Inject
    private ExceptionHandler exceptionHandler;

    @Inject
    @Modified
    private Event<ModifiedProjectMessage> projectEvent;

    private String saveAsName;

    /**
     * @return the saveAsName
     */
    public String getSaveAsName() {
        return saveAsName;
    }

    /**
     * @param saveAsName
     *            the saveAsName to set
     */
    public void setSaveAsName(String saveAsName) {
        this.saveAsName = saveAsName;
    }

    /**
     * @return the Name
     */
    public String getName() {
        return getProject().getName();
    }

    /**
     * @param name
     *            the Name to set
     */
    public void setName(String name) {
        getProject().setName(name);
    }
    
    /**
     * @return the Comment
     */
    public String getComments() {
        return getProject().getComments();
    }
    
    /**
     * @param comment
     *            the Comment to set
     */
    public void setComments(String comment) {
        getProject().setComments(comment);
    }

    /**
     * Initializes all the data variables of the class.
     * 
     * @param prj
     */
    public void openProject(Project prj) {
    	conversation.begin();
        AWSXRay.createSubsegment("Open.Project." + prj.getName(), (subsegment) -> {
            subsegment.putAnnotation("project", prj.getName());
            doOpenProject(prj);
        });
    }

    /**
     * @param prj
     */
    private void doOpenProject(Project prj) {
        this.project = new ProjectDao().findByIdEager(prj.getId());
        LOG.info("Opening Project " + prj + " workloads " + this.project.getWorkloads());
        usersAndTimes.init();
        notificationsEditor.init();
        jobMaker.init(this);
        dataFileBean.init();
        workloadScripts.init();
        projectVariableEditor.init();
        this.saveAsName = project.getName();
        if (!canEditProject()) {
            messages.warn("You do not have permission to edit this project.");
        }
    }

    public String cancel() {
    	conversation.end();
        return "success";
    }

    /**
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * @return the workload
     */
    public Workload getWorkload() {
        return project.getWorkloads().get(0);
    }

    /**
     * @return the configuration
     */
    public JobConfiguration getJobConfiguration() {
        return getWorkload().getJobConfiguration();
    }

    /**
     * @return the workload type of the job
     */
    public String getWorkloadType() {
        return getWorkload().getJobConfiguration().getIncrementStrategy().toString();
    }

    /**
     * Sets the workloadType to either increasing or standard
     * 
     * @param workloadType
     */
    public void setWorkloadType(String workloadType) {
        Workload workload = getWorkload();
        if (workloadType.equals(IncrementStrategy.increasing)) {
            workload.getJobConfiguration().setIncrementStrategy(IncrementStrategy.increasing);
        } else {
            workload.getJobConfiguration().setIncrementStrategy(IncrementStrategy.standard);
        }
    }

      // TODO: BaseJob needs updating to support start and end rate
//    /**
//     * @return the start rate for job
//     */
//    public int getStartRate() {
//        return getWorkload().getJobConfiguration().getStartRate();
//    }
//
//    /**
//     * Sets the start rate for the job
//     *
//     * @param startRate
//     */
//    public void setStartRate(int startRate) {
//        Workload workload = getWorkload();
//        workload.getJobConfiguration().setStartRate(startRate);
//    }

    /**
     * @return the end rate for job
     */
    public double getEndRate() {
        return getWorkload().getJobConfiguration().getTargetRampRate();
    }

    /**
     * Sets the end rate for the job
     *
     * @param endRate
     */
    public void setEndRate(double endRate) {
        Workload workload = getWorkload();
        workload.getJobConfiguration().setTargetRampRate(endRate);
    }

    /**
     * Saves the Project object in the database.
     */
    public void save() {
        AWSXRay.createSubsegment("Save.Project." + project.getName(), (subsegment) -> {
            subsegment.putAnnotation("project", project.getName());
            doSave();
        });
        messages.info("Project " + project.getName() + " has been saved.");
    }

    public void saveAs() {
        if (StringUtils.isEmpty(saveAsName)) {
            messages.error("You must give the script a name.");
            return;
        }
        try {
            String originalName = project.getName();
            if (originalName.equals(saveAsName)) {
                save();
            } else {
                Project copied = copyProject();
                copied.getWorkloads().get(0).getJobConfiguration().setVariables(new HashMap<String,String>());
                copied = new ProjectDao().saveOrUpdateProject(copied);
                save(); // FIXME Hack for original project losing data. Do not know why this works
                projectVariableEditor.copyTo(copied.getWorkloads().get(0));
                new WorkloadDao().saveOrUpdate(copied.getWorkloads().get(0));
                doOpenProject(copied);
                projectEvent.fire(new ModifiedProjectMessage(project, this));
                messages.info("Project " + originalName + " has been saved as " + project.getName() + ".");
            }
        } catch (Exception e) {
        	LOG.error(e.getMessage());
            messages.error(e.getMessage());
        }
    }

    /**
     * @return
     */
    private Project copyProject() {
        Project ret = new Project();
        Workload workload = new Workload();
        workload.setParent(ret);
        workload.setName(saveAsName);
        ret.setWorkloads(Collections.singletonList(workload));
        ret.setComments(project.getComments());
        ret.setCreator(securityContext.getCallerPrincipal().getName());
        ret.setName(saveAsName);
        ret.setProductName(project.getProductName());
        ret.setScriptDriver(project.getScriptDriver());

        JobConfiguration originalJobConfig = project.getWorkloads().get(0).getJobConfiguration();
        JobConfiguration newJobConfig = copyJobConfiguration(originalJobConfig, workload);
        workload.setJobConfiguration(newJobConfig);

        usersAndTimes.copyTo(workload);
        workloadScripts.copyTo(workload);
        notificationsEditor.copyTo(workload);
        return ret;
    }

    private JobConfiguration copyJobConfiguration(JobConfiguration original, Workload newParent) {
        JobConfiguration copy = new JobConfiguration();
        copy.setBaselineVirtualUsers(original.getBaselineVirtualUsers());
        copy.setTargetRampRate(original.getTargetRampRate());
        copy.setDataFileIds(new HashSet<>(original.getDataFileIds()));
        copy.setIncrementStrategy(original.getIncrementStrategy());
        copy.setLocation(original.getLocation());
        copy.setReportingMode(original.getReportingMode());
        copy.setSimulationTime(original.getSimulationTime());
        copy.setSimulationTimeExpression(original.getSimulationTimeExpression());
        copy.setRampTime(original.getRampTime());
        copy.setRampTimeExpression(original.getRampTimeExpression());
        copy.setTerminationPolicy(original.getTerminationPolicy());
        copy.setUserIntervalIncrement(original.getUserIntervalIncrement());
        copy.setNumUsersPerAgent(original.getNumUsersPerAgent());
        copy.setTargetRatePerAgent(original.getTargetRatePerAgent());
        copy.setStopBehavior(original.getStopBehavior());
        copy.setVmInstanceType(original.getVmInstanceType());
        copy.setVariables(new HashMap<>(original.getVariables()));
        copy.setJobRegions(original.getJobRegions().stream()
                .map(this::copyRegion)
                .collect(Collectors.toSet()));
        copy.setParent(newParent);
        return copy;
    }

    private JobRegion copyRegion(JobRegion original) {
        JobRegion copy = new JobRegion();
        copy.setRegion(original.getRegion());
        copy.setUsers(original.getUsers());
        copy.setPercentage(original.getPercentage());
        return copy;
    }

    /**
     * Saves the Project object in the database.
     */
    public boolean doSave() {
        try {
            usersAndTimes.save();
            dataFileBean.save();
            notificationsEditor.save();
            projectVariableEditor.save();
            project = new ProjectDao().saveOrUpdateProject(project);
            workloadScripts.save();
            // doOpenProject(project);
            projectEvent.fire(new ModifiedProjectMessage(project, this));
            return true;
        } catch (Exception e) {
            LOG.error("Error saving: " + e, e);
            exceptionHandler.handle(e);
        }
        return false;
    }

    public boolean canEditProject() {
        return security.hasRight(AccessRight.EDIT_PROJECT) || security.isOwner(project);
    }

}

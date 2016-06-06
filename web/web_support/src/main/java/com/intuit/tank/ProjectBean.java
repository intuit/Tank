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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Conversation;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.seam.international.status.Messages;
import org.picketlink.Identity;

import com.intuit.tank.auth.Security;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.project.AssociateDataFileBean;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobMaker;
import com.intuit.tank.project.NotificationsEditor;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectVariableEditor;
import com.intuit.tank.project.UsersAndTimes;
import com.intuit.tank.project.Workload;
import com.intuit.tank.project.WorkloadScripts;
import com.intuit.tank.qualifier.Modified;
import com.intuit.tank.util.ExceptionHandler;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.settings.AccessRight;

@Named
@ConversationScoped
public class ProjectBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(ProjectBean.class);

    private static final long serialVersionUID = 1L;
    private Project project;

    @Inject
    private UsersAndTimes usersAndTimes;

    @Inject
    private Identity identity;
    
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
     * @param Name
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
     * @param Comment
     *            the Comment to set
     */
    public void setComments(String comment) {
        getProject().setComments(comment);
    }

    /**
     * Initializes all the data variables of the class.
     * 
     * @param project
     */
    public void openProject(Project prj) {
    	conversation.setTimeout(300000);
    	conversation.begin();
        doOpenProject(prj);
    }

    /**
     * @param prj
     */
    private void doOpenProject(Project prj) {
        this.project = new ProjectDao().findById(prj.getId());
        LOG.info("Opening Project " + prj + " workloads " + project.getWorkloads());
        usersAndTimes.init();
        notificationsEditor.init();
        jobMaker.init();
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

    /**
     * Saves the Project object in the database.
     */
    public void save() {
        doSave();
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
        List<Workload> workloads = new ArrayList<Workload>();
        workload.setName(saveAsName);
        workloads.add(workload);
        ret.setWorkloads(workloads);
        ret.setComments(project.getComments());
        ret.setCreator(identity.getAccount().getId());
        ret.setName(saveAsName);
        ret.setProductName(project.getProductName());
        ret.setScriptDriver(project.getScriptDriver());

        usersAndTimes.copyTo(workload);
        workloadScripts.copyTo(workload);

        notificationsEditor.copyTo(workload);
        return ret;
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

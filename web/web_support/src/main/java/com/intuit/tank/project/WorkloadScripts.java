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
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;

import com.intuit.tank.util.Messages;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;

import com.intuit.tank.ProjectBean;
import com.intuit.tank.dao.ScriptGroupDao;
import com.intuit.tank.script.ScriptLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Named
@ConversationScoped
public class WorkloadScripts implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(WorkloadScripts.class);

    @Inject
    private Messages messages;

    @Inject
    private ScriptLoader scriptLoader;

    @Inject
    private ProjectBean projectBean;

    private ScriptGroup currentScriptGroup;
    private DualListModel<Script> scriptSelectionModel;
    private ScriptGroup scriptGroup;
    private int loop;
    private int insertIndex;

    private TestPlan currentTestPlan;

    private int tabIndex = 0;

    @PostConstruct
    public void postConstruct() {
        List<TestPlan> testPlans = projectBean.getWorkload().getTestPlans();
        if (testPlans.size() == 0) {
            addTestPlan(TestPlan.builder().name("Test Plan").usersPercentage(100).build());
        } else if (testPlans.size() > tabIndex) {
            this.currentTestPlan = testPlans.get(tabIndex);
        } else {
            this.currentTestPlan = testPlans.get(0);
        }
    }

    public void addTestPlan(TestPlan plan) {
        projectBean.getWorkload().addTestPlan(plan);
        // this.tabIndex = projectBean.getWorkload().getTestPlans().size() - 1;
        // this.currentTestPlan = plan;
    }

    public void onChange(TabChangeEvent event) {
        TabView parent = (TabView) event.getTab().getParent();
        this.tabIndex = parent.getActiveIndex();
        this.currentTestPlan = projectBean.getWorkload().getTestPlans().get(tabIndex);
    }

    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int tabIndex) {
        this.tabIndex = tabIndex;
    }

    public ScriptGroup getCurrentScriptGroup() {
        try {
            if (currentScriptGroup == null) {
                currentScriptGroup = new ScriptGroup();
                currentScriptGroup.setLoop(1);
                LOG.info("getCurrentScriptGroup() - Created new script group ");
            } else {
                LOG.info("getCurrentScriptGroup() - Returning existing script group " + currentScriptGroup.getName());
            }
            return currentScriptGroup;
        } catch (Exception e) {
            LOG.error("getCurrentScriptGroup() - Error getting current script group", e);
            return null;
        }
    }

    public void setCurrentScriptGroup(ScriptGroup currentScriptGroup) {
        try {
            this.currentScriptGroup = currentScriptGroup;
            LOG.info("setCurrentScriptGroup() - Set current script group " + currentScriptGroup.getName());
            initScriptSelectionModel();
        } catch (Exception e) {
            LOG.error("setCurrentScriptGroup() - Error setting current script group", e);
        }
    }

    /**
     * initializes the current group object.
     */
    public void initCurrentGroup() {
        try {
            currentScriptGroup = new ScriptGroup();
            currentScriptGroup.setLoop(1);
            LOG.info("initCurrentGroup() - Created new script group ");
        } catch (Exception e) {
            LOG.error("initCurrentGroup() - Error initializing current script group", e);
        }
    }

    public void setInsertIndex(int rowIndex) {
        this.insertIndex = rowIndex;
    }

    /**
     * Initializes the class variables with appropriate references
     * 
     */
    public void init() {
        initScriptSelectionModel();
    }

    public List<TestPlan> getTestPlans() {
        return projectBean.getWorkload().getTestPlans();
    }

    /**
     * @return the list of script groups in the workload
     */
    public List<ScriptGroup> getScriptGroups() {
        return currentTestPlan.getScriptGroups();
    }

    public TestPlan getCurrentTestPlan() {
        return currentTestPlan;
    }

    public void setCurrentTestPlan(TestPlan currentTestPlan) {
        this.currentTestPlan = currentTestPlan;
    }

    /**
     * Adds current script group to the workload.
     */
    public void addScriptGroup() {
        if ("<Script Group Name>".equalsIgnoreCase(currentScriptGroup.getName())) {
            messages.error("Please give the Script Group a name.");
        } else {
            currentTestPlan.addScriptGroupAt(currentScriptGroup, this.insertIndex);
        }
    }

    /**
     * Deletes the script group from the workload. This does not persist the changes to the database.
     * 
     * @param group
     */
    public void delete(ScriptGroup group) {
        if (!currentTestPlan.getScriptGroups().remove(group)) {
            messages.warn("Could not remove Script Group " + group.getName() + ".");
        } else {
            messages.info("Script Group " + group.getName() + " has been removed.");
        }
    }

    public void deleteTestPlan(TestPlan plan) {
        if (projectBean.getWorkload().getTestPlans().size() > 1) {

            if (!projectBean.getWorkload().getTestPlans().remove(plan)) {
                messages.warn("Could not remove Test Plan " + plan.getName() + ".");
            } else {
                messages.info("Test Plan " + plan.getName() + " has been removed.");
                if (tabIndex > 0) {
                    tabIndex = tabIndex - 1;
                }
                this.currentTestPlan = projectBean.getWorkload().getTestPlans().get(tabIndex);
            }
        }
    }

    public boolean isDeleteTestPlanDisabled() {
        return projectBean.getWorkload().getTestPlans().size() <= 1;
    }

    /**
     * Saves the workload.
     */
    public void saveGroup() {
        save();
    }

    /**
     * persists the workload in the database
     */
    public void save() {
        // this.workload = new WorkloadDao().saveOrUpdate(workload);
        if (tabIndex >= projectBean.getWorkload().getTestPlans().size()) {
            tabIndex = 0;
        }
        this.setCurrentTestPlan(projectBean.getWorkload().getTestPlans().get(tabIndex));
    }

    public void copyTo(Workload copyTo) {
        for (TestPlan copyFromTp : projectBean.getWorkload().getTestPlans()) {
            TestPlan tp = new TestPlan();
            tp.setName(copyFromTp.getName());
            tp.setUserPercentage(copyFromTp.getUserPercentage());
            copyTo.addTestPlan(tp);
            for (ScriptGroup copyFromSg : copyFromTp.getScriptGroups()) {
                ScriptGroup sg = new ScriptGroup();
                sg.setLoop(copyFromSg.getLoop());
                sg.setName(copyFromSg.getName());
                sg.setPosition(copyFromSg.getPosition());
                for (ScriptGroupStep copyStep : copyFromSg.getScriptGroupSteps()) {
                    ScriptGroupStep step = new ScriptGroupStep();
                    step.setLoop(copyStep.getLoop());
                    step.setPosition(copyFromSg.getPosition());
                    step.setScript(copyStep.getScript());
                    sg.addScriptGroupStep(step);
                }

                tp.addScriptGroup(sg);
            }
        }
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(int loop) {
        this.loop = loop;
    }

    /**
     * @return the scriptSelectionModel
     */
    public DualListModel<Script> getScriptSelectionModel() {
        try {
            if (scriptSelectionModel.getTarget().size() > 0) {
                LOG.info("WorkloadScripts - getScriptSelectionModel() - script list latest entry " + scriptSelectionModel.getTarget().get(0).getName());
            }
        } catch (Exception e) {
            LOG.error("WorkloadScripts - getScriptSelectionModel() error: " + e.getMessage());
        }
        return scriptSelectionModel;
    }

    /**
     * @param scriptSelectionModel
     *            the scriptSelectionModel to set
     */
    public void setScriptSelectionModel(DualListModel<Script> scriptSelectionModel) {
        try {
            LOG.info("WorkloadScripts - setScriptSelectionModel() - script list size " + scriptSelectionModel.getTarget().size());
            if (scriptSelectionModel.getTarget().size() > 0) {
                LOG.info("WorkloadScripts - setScriptSelectionModel() - script list latest entry " + scriptSelectionModel.getTarget().get(0).getName());
            }
            this.scriptSelectionModel = scriptSelectionModel;
        } catch (Exception e) {
            LOG.error("WorkloadScripts - setScriptSelectionModel() error: " + e.getMessage());
        }
    }

    private void initScriptSelectionModel() {
        try {
            scriptSelectionModel = new DualListModel<Script>(
                    scriptLoader.getVersionEntities(),
                    new ArrayList<Script>());
            LOG.info("WorkloadScripts - initScriptSelectionModel() - source list size " + scriptSelectionModel.getSource().size());
            LOG.info("WorkloadScripts - initScriptSelectionModel() - target list size " + scriptSelectionModel.getTarget().size());
            LOG.info("WorkloadScripts - initScriptSelectionModel() - source list last entry " + scriptSelectionModel.getSource().get(0).getName());
            if(scriptSelectionModel.getTarget().size() > 0) {
                LOG.info("WorkloadScripts - initScriptSelectionModel() - target list last entry " + scriptSelectionModel.getTarget().get(0).getName());
            }
        } catch (Exception e) {
            LOG.error("WorkloadScripts - initScriptSelectionModel() error: " + e.getMessage());
        }

    }

    public ScriptGroup getScriptGroup() {
        try {
            if (scriptGroup != null) {
                LOG.info("WorkloadScripts - getScriptGroup() name: " + scriptGroup.getName());
            } else {
                LOG.info("WorkloadScripts - getScriptGroup() scriptGroup is null");
            }
            return scriptGroup;
        } catch (Exception e) {
            LOG.error("WorkloadScripts - getScriptGroup() error: " + e.getMessage());
            return null;
        }
    }

    public void setScriptGroup(ScriptGroup scripGroup) {
        try {
            this.scriptGroup = scripGroup;
            if(this.scriptGroup != null) {
                LOG.info("WorkloadScripts - setScriptGroup() name: " + scriptGroup.getName());
                LOG.info("WorkloadScripts - setScriptGroup() size: " + scriptGroup.getScriptGroupSteps().size());
            } else {
                LOG.info("WorkloadScripts - setScriptGroup() scriptGroup is null");
            }
        } catch (Exception e) {
            LOG.error("WorkloadScripts - setScriptGroup() error: " + e.getMessage());
        }
    }

    public void addScriptGroupStep() {
        try {
            LOG.info("WorkloadScripts - addScriptGroupStep() start");
            for (Script s : scriptSelectionModel.getTarget()) {
                ScriptGroupStep sgs = new ScriptGroupStep();
                LOG.info("WorkloadScripts - addScriptGroupStep() add script: " + s.getName());
                sgs.setScript(s);
                sgs.setLoop(1);
                scriptGroup.addScriptGroupStep(sgs);
                int lastEntry = scriptGroup.getScriptGroupSteps().size() - 1;
                LOG.info("WorkloadScripts - addScriptGroupStep() added ScriptGroupStep: " + sgs.getScriptGroup().getScriptGroupSteps().get(lastEntry).getScript().getName());
            }
            initScriptSelectionModel();
        } catch (Exception e) {
            LOG.error("WorkloadScripts - addScriptGroupStep() error: " + e.getMessage());
        }
    }

    public void deleteScriptGroupStep(ScriptGroupStep sgs) {
        try {
            LOG.info("WorkloadScripts - deleteScriptGroupStep() deleting " + sgs.getScript().getName() + " from ScriptGroup " + sgs.getScriptGroup().getName());
            scriptGroup.getScriptGroupSteps().remove(sgs);
        } catch (Exception e) {
            LOG.error("WorkloadScripts - deleteScriptGroupStep() error: " + e.getMessage());
        }
    }

    public List<ScriptGroupStep> getSteps() {
        try {
            int lastEntry = scriptGroup.getScriptGroupSteps().size() - 1;
            if (scriptGroup == null) {
                LOG.info("WorkloadScripts - getSteps() scriptGroup is null");
                return new ArrayList<ScriptGroupStep>();
            }
            if (scriptGroup.getScriptGroupSteps().size() > 0) {
                LOG.info("WorkloadScripts - getSteps() scriptGroupSteps latest entry: " + scriptGroup.getScriptGroupSteps().get(lastEntry).getScript().getName());
                LOG.info("WorkloadScripts - getSteps() scriptGroupSteps size: " + scriptGroup.getScriptGroupSteps().size());
            } else {
                LOG.info("WorkloadScripts - getSteps() scriptGroupSteps is empty");
            }
            return scriptGroup.getScriptGroupSteps();
        } catch (Exception e) {
            LOG.error("WorkloadScripts - getSteps() error: " + e.getMessage());
            return new ArrayList<ScriptGroupStep>();
        }
    }

    public void saveScriptGroup() {
        new ScriptGroupDao().saveOrUpdate(scriptGroup);
    }

    public void finishEditing() {

    }

}

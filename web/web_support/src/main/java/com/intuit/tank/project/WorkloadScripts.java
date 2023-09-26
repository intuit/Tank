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
import org.primefaces.event.SelectEvent;
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

    private List<Script> selectedAvailableScripts;
    private List<Script> selectedSelectedScripts;

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

    public List<Script> getSelectedAvailableScripts() {
        return selectedAvailableScripts;
    }

    public void setSelectedAvailableScripts(List<Script> selectedAvailableScripts) {
        this.selectedAvailableScripts = selectedAvailableScripts;
    }

    public List<Script> getSelectedSelectedScripts() {
        return selectedSelectedScripts;
    }

    public void setSelectedSelectedScripts(List<Script> selectedSelectedScripts) {
        this.selectedSelectedScripts = selectedSelectedScripts;
    }

    public void addAllToTarget() {
        scriptSelectionModel.getTarget().addAll(scriptSelectionModel.getSource());
        scriptSelectionModel.getSource().clear();
    }

    public void addToTarget() {
        if(!selectedAvailableScripts.isEmpty()) {
            scriptSelectionModel.getTarget().addAll(selectedAvailableScripts);
            scriptSelectionModel.getSource().removeAll(selectedAvailableScripts);
        }
    }

    public void removeFromTarget() {
        if(!selectedSelectedScripts.isEmpty()) {
            scriptSelectionModel.getSource().addAll(0, selectedSelectedScripts);
            scriptSelectionModel.getTarget().removeAll(selectedSelectedScripts);
        }
    }

    public void removeAllFromTarget() {
        scriptSelectionModel.getSource().addAll(scriptSelectionModel.getTarget());
        scriptSelectionModel.getTarget().clear();
    }

    public void onSourceSelect(SelectEvent event) {
        selectedAvailableScripts = (List<Script>) event.getObject();
    }

    public void onTargetSelect(SelectEvent event) {
        selectedSelectedScripts = (List<Script>) event.getObject();
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
        if (currentScriptGroup == null) {
            currentScriptGroup = new ScriptGroup();
            currentScriptGroup.setLoop(1);
        }
        return currentScriptGroup;
    }

    public void setCurrentScriptGroup(ScriptGroup currentScriptGroup) {
        this.currentScriptGroup = currentScriptGroup;
        initScriptSelectionModel();
    }

    /**
     * initializes the current group object.
     */
    public void initCurrentGroup() {
        currentScriptGroup = new ScriptGroup();
        currentScriptGroup.setLoop(1);
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
        return scriptSelectionModel;
    }

    /**
     * @param scriptSelectionModel
     *            the scriptSelectionModel to set
     */
    public void setScriptSelectionModel(DualListModel<Script> scriptSelectionModel) {
        this.scriptSelectionModel = scriptSelectionModel;
    }

    private void initScriptSelectionModel() {
        scriptSelectionModel = new DualListModel<Script>(
                scriptLoader.getVersionEntities(),
                new ArrayList<Script>());

    }

    public ScriptGroup getScriptGroup() {
        return scriptGroup;
    }

    public void setScriptGroup(ScriptGroup scripGroup) {
        this.scriptGroup = scripGroup;
    }

    public void addScriptGroupStep() {
        for (Script s : scriptSelectionModel.getTarget()) {
            ScriptGroupStep sgs = new ScriptGroupStep();
            sgs.setScript(s);
            sgs.setLoop(1);
            scriptGroup.addScriptGroupStep(sgs);
        }
        initScriptSelectionModel();
    }

    public void deleteScriptGroupStep(ScriptGroupStep sgs) {
        scriptGroup.getScriptGroupSteps().remove(sgs);
        scriptSelectionModel.getSource().add(sgs.getScript());
        initScriptSelectionModel();
    }

    public List<ScriptGroupStep> getSteps() {
        if (scriptGroup == null) {
            return new ArrayList<ScriptGroupStep>();
        }
        return scriptGroup.getScriptGroupSteps();
    }

    public void saveScriptGroup() {
        new ScriptGroupDao().saveOrUpdate(scriptGroup);
    }

    public void finishEditing() {

    }

}

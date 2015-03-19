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
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.math.NumberUtils;
import org.jboss.seam.international.status.Messages;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DualListModel;

import com.intuit.tank.ProjectBean;
import com.intuit.tank.dao.ScriptGroupDao;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.script.ScriptLoader;

@Named
@ConversationScoped
public class WorkloadScripts implements Serializable {

    private static final long serialVersionUID = 1L;

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
        this.insertIndex = -1;
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String param = params.get("insertIndex");
        if (param != null && NumberUtils.isNumber(param)) {
            insertIndex = Integer.parseInt(param);
        }
    }

    /**
     * Initializes the class variables with appropriate references
     * 
     * @param project
     * @param workload
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
            currentTestPlan.addScriptGroupAt(currentScriptGroup, insertIndex);
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

    /**
     * Helps in creating a new script group
     */
    public void newScriptGroup() {
        throw new NotImplementedException();
    }

    public void editScriptSteps(ScriptGroup scriptGroup) {
        this.scriptGroup = scriptGroup;
        initScriptSelectionModel();
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
        scriptSelectionModel = new DualListModel<Script>();
        List<Script> scripts = scriptLoader.getVersionEntities();
        for (Script s : scripts) {
            scriptSelectionModel.getSource().add(s);
        }
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

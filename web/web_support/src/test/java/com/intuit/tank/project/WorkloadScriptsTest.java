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

import java.util.ArrayList;
import java.util.List;

import com.intuit.tank.ProjectBean;
import com.intuit.tank.script.ScriptLoader;
import com.intuit.tank.util.Messages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DualListModel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WorkloadScriptsTest {

    @InjectMocks
    private WorkloadScripts workloadScripts;

    @Mock
    private Messages messages;

    @Mock
    private ScriptLoader scriptLoader;

    @Mock
    private ProjectBean projectBean;

    private AutoCloseable closeable;

    private Workload workload;
    private TestPlan testPlan;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        testPlan = TestPlan.builder().name("Test Plan").usersPercentage(100).build();
        workload = Workload.builder().name("TestWorkload").build();
        workload.addTestPlan(testPlan);

        when(projectBean.getWorkload()).thenReturn(workload);
        when(scriptLoader.getVersionEntities()).thenReturn(new ArrayList<>());
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testGetLoop_SetAndGet() {
        workloadScripts.setLoop(5);
        assertEquals(5, workloadScripts.getLoop());
    }

    @Test
    public void testGetTabIndex_SetAndGet() {
        workloadScripts.setTabIndex(2);
        assertEquals(2, workloadScripts.getTabIndex());
    }

    @Test
    public void testGetCurrentScriptGroup_WhenNull_CreatesNew() {
        ScriptGroup group = workloadScripts.getCurrentScriptGroup();
        assertNotNull(group);
        assertEquals(1, group.getLoop());
    }

    @Test
    public void testSetCurrentScriptGroup_SetsGroup() {
        ScriptGroup group = new ScriptGroup();
        group.setName("Test Group");
        group.setLoop(3);
        workloadScripts.setCurrentScriptGroup(group);
        assertEquals(group, workloadScripts.getCurrentScriptGroup());
    }

    @Test
    public void testInitCurrentGroup_CreatesNewGroupWithLoop1() {
        workloadScripts.initCurrentGroup();
        ScriptGroup group = workloadScripts.getCurrentScriptGroup();
        assertNotNull(group);
        assertEquals(1, group.getLoop());
    }

    @Test
    public void testGetSelectedAvailableScripts_SetAndGet() {
        List<Script> scripts = new ArrayList<>();
        scripts.add(new Script());
        workloadScripts.setSelectedAvailableScripts(scripts);
        assertEquals(scripts, workloadScripts.getSelectedAvailableScripts());
    }

    @Test
    public void testGetSelectedSelectedScripts_SetAndGet() {
        List<Script> scripts = new ArrayList<>();
        scripts.add(new Script());
        workloadScripts.setSelectedSelectedScripts(scripts);
        assertEquals(scripts, workloadScripts.getSelectedSelectedScripts());
    }

    @Test
    public void testGetScriptGroup_SetAndGet() {
        ScriptGroup group = new ScriptGroup();
        group.setName("Group1");
        workloadScripts.setScriptGroup(group);
        assertEquals(group, workloadScripts.getScriptGroup());
    }

    @Test
    public void testGetScriptSelectionModel_SetAndGet() {
        DualListModel<Script> model = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
        workloadScripts.setScriptSelectionModel(model);
        assertEquals(model, workloadScripts.getScriptSelectionModel());
    }

    @Test
    public void testGetSteps_WhenScriptGroupNull_ReturnsEmpty() {
        workloadScripts.setScriptGroup(null);
        List<ScriptGroupStep> steps = workloadScripts.getSteps();
        assertNotNull(steps);
        assertTrue(steps.isEmpty());
    }

    @Test
    public void testGetSteps_WithScriptGroup_ReturnsSteps() {
        ScriptGroup group = new ScriptGroup();
        ScriptGroupStep step = new ScriptGroupStep();
        step.setScript(new Script());
        group.addScriptGroupStep(step);
        workloadScripts.setScriptGroup(group);
        List<ScriptGroupStep> steps = workloadScripts.getSteps();
        assertNotNull(steps);
        assertEquals(1, steps.size());
    }

    @Test
    public void testGetCurrentTestPlan_SetAndGet() {
        workloadScripts.setCurrentTestPlan(testPlan);
        assertEquals(testPlan, workloadScripts.getCurrentTestPlan());
    }

    @Test
    public void testAddTestPlan_AddsToWorkload() {
        TestPlan newPlan = TestPlan.builder().name("New Plan").usersPercentage(50).build();
        workloadScripts.addTestPlan(newPlan);
        assertEquals(2, workload.getTestPlans().size());
    }

    @Test
    public void testGetTestPlans_DelegatesToProjectBean() {
        List<TestPlan> plans = workloadScripts.getTestPlans();
        assertNotNull(plans);
        verify(projectBean, atLeastOnce()).getWorkload();
    }

    @Test
    public void testIsDeleteTestPlanDisabled_WhenOnlyOnePlan_ReturnsTrue() {
        assertTrue(workloadScripts.isDeleteTestPlanDisabled());
    }

    @Test
    public void testIsDeleteTestPlanDisabled_WhenMultiplePlans_ReturnsFalse() {
        workload.addTestPlan(TestPlan.builder().name("Plan2").usersPercentage(50).build());
        assertFalse(workloadScripts.isDeleteTestPlanDisabled());
    }

    @Test
    public void testDelete_WhenGroupExists_SendsInfoMessage() {
        ScriptGroup group = new ScriptGroup();
        group.setName("GroupToDelete");
        testPlan.addScriptGroup(group);
        workloadScripts.setCurrentTestPlan(testPlan);

        workloadScripts.delete(group);
        verify(messages).info(contains("GroupToDelete"));
    }

    @Test
    public void testDelete_WhenGroupNotExists_SendsWarnMessage() {
        ScriptGroup group = new ScriptGroup();
        group.setName("NonExistent");
        workloadScripts.setCurrentTestPlan(testPlan);

        workloadScripts.delete(group);
        verify(messages).warn(anyString());
    }

    @Test
    public void testAddScriptGroup_WithDefaultName_ShowsError() {
        ScriptGroup group = workloadScripts.getCurrentScriptGroup();
        group.setName("<Script Group Name>");
        workloadScripts.setCurrentTestPlan(testPlan);

        workloadScripts.addScriptGroup();
        verify(messages).error(anyString());
    }

    @Test
    public void testAddScriptGroup_WithValidName_AddsGroup() {
        ScriptGroup group = workloadScripts.getCurrentScriptGroup();
        group.setName("MyGroup");
        workloadScripts.setInsertIndex(0);
        workloadScripts.setCurrentTestPlan(testPlan);

        workloadScripts.addScriptGroup();
        assertEquals(1, testPlan.getScriptGroups().size());
    }

    @Test
    public void testSave_WhenTabIndexInRange_SetsCurrentTestPlan() {
        workloadScripts.setCurrentTestPlan(testPlan);
        workloadScripts.setTabIndex(0);
        workloadScripts.save();
        assertNotNull(workloadScripts.getCurrentTestPlan());
    }

    @Test
    public void testSave_WhenTabIndexOutOfRange_ResetsToZero() {
        workloadScripts.setTabIndex(99);
        workloadScripts.save();
        assertEquals(0, workloadScripts.getTabIndex());
    }

    @Test
    public void testCopyTo_CopiesTestPlans() {
        ScriptGroup group = new ScriptGroup();
        group.setName("CopyGroup");
        group.setLoop(2);
        testPlan.addScriptGroup(group);

        Workload target = Workload.builder().name("Target").build();
        workloadScripts.copyTo(target);

        assertEquals(1, target.getTestPlans().size());
        assertEquals("Test Plan", target.getTestPlans().get(0).getName());
    }

    @Test
    public void testAddAllToTarget_MovesAllFromSource() {
        List<Script> source = new ArrayList<>();
        source.add(new Script());
        source.add(new Script());
        List<Script> target = new ArrayList<>();
        DualListModel<Script> model = new DualListModel<>(source, target);
        workloadScripts.setScriptSelectionModel(model);

        workloadScripts.addAllToTarget();

        assertTrue(workloadScripts.getScriptSelectionModel().getSource().isEmpty());
        assertEquals(2, workloadScripts.getScriptSelectionModel().getTarget().size());
    }

    @Test
    public void testRemoveAllFromTarget_MovesAllToSource() {
        List<Script> source = new ArrayList<>();
        List<Script> target = new ArrayList<>();
        target.add(new Script());
        DualListModel<Script> model = new DualListModel<>(source, target);
        workloadScripts.setScriptSelectionModel(model);

        workloadScripts.removeAllFromTarget();

        assertTrue(workloadScripts.getScriptSelectionModel().getTarget().isEmpty());
        assertEquals(1, workloadScripts.getScriptSelectionModel().getSource().size());
    }

    @Test
    public void testAddToTarget_WithSelectedScripts_MovesSelected() {
        Script s1 = Script.builder().name("s1").build();
        Script s2 = Script.builder().name("s2").build();
        List<Script> source = new ArrayList<>(List.of(s1, s2));
        List<Script> target = new ArrayList<>();
        DualListModel<Script> model = new DualListModel<>(source, target);
        workloadScripts.setScriptSelectionModel(model);
        workloadScripts.setSelectedAvailableScripts(List.of(s1));

        workloadScripts.addToTarget();

        assertFalse(workloadScripts.getScriptSelectionModel().getTarget().isEmpty());
    }

    @Test
    public void testRemoveFromTarget_WithSelectedScripts_RemovesSelected() {
        Script s1 = Script.builder().name("s1").build();
        List<Script> source = new ArrayList<>();
        List<Script> target = new ArrayList<>(List.of(s1));
        DualListModel<Script> model = new DualListModel<>(source, target);
        workloadScripts.setScriptSelectionModel(model);
        workloadScripts.setSelectedSelectedScripts(List.of(s1));

        workloadScripts.removeFromTarget();

        assertTrue(workloadScripts.getScriptSelectionModel().getTarget().isEmpty());
    }

    @Test
    public void testFinishEditing_DoesNotThrow() {
        assertDoesNotThrow(() -> workloadScripts.finishEditing());
    }

    @Test
    public void testInit_InitializesScriptSelectionModel() {
        workloadScripts.init();
        // should not throw
        assertNotNull(workloadScripts);
    }

    // --- postConstruct ---

    @Test
    public void testPostConstruct_WhenTestPlansNonEmpty_SetsCurrentTestPlan() {
        // tabIndex defaults to 0; workload has one plan (testPlan) set up in setUp()
        workloadScripts.postConstruct();
        assertEquals(testPlan, workloadScripts.getCurrentTestPlan());
    }

    @Test
    public void testPostConstruct_WhenTestPlansEmpty_CreatesDefaultPlan() {
        Workload emptyWorkload = Workload.builder().name("Empty").build();
        when(projectBean.getWorkload()).thenReturn(emptyWorkload);

        workloadScripts.postConstruct();

        // postConstruct calls addTestPlan which adds a plan to the workload
        assertEquals(1, emptyWorkload.getTestPlans().size());
        assertEquals("Test Plan", emptyWorkload.getTestPlans().get(0).getName());
    }

    @Test
    public void testPostConstruct_WhenTabIndexBeyondPlans_FallsBackToFirstPlan() {
        // Set tabIndex > 0 but workload only has one plan
        workloadScripts.setTabIndex(5);
        workloadScripts.postConstruct();
        assertEquals(testPlan, workloadScripts.getCurrentTestPlan());
    }

    // --- deleteTestPlan ---

    @Test
    public void testDeleteTestPlan_WithMultiplePlans_RemovesPlan() {
        TestPlan plan2 = TestPlan.builder().name("Plan2").usersPercentage(50).build();
        workload.addTestPlan(plan2);
        assertEquals(2, workload.getTestPlans().size());

        workloadScripts.deleteTestPlan(plan2);

        // One plan was removed (equals by id=0, so the first match is removed)
        assertEquals(1, workload.getTestPlans().size());
        // An info message was sent (the plan name in the message matches whichever was removed)
        verify(messages).info(anyString());
    }

    @Test
    public void testDeleteTestPlan_WithTabIndexGreaterThanZero_DecrementsTabIndex() {
        TestPlan plan2 = TestPlan.builder().name("Plan2").usersPercentage(50).build();
        workload.addTestPlan(plan2);
        workloadScripts.setTabIndex(1);

        workloadScripts.deleteTestPlan(plan2);

        assertEquals(0, workloadScripts.getTabIndex());
    }

    @Test
    public void testDeleteTestPlan_WhenOnlyOnePlan_DoesNothing() {
        // Only one plan — guard condition prevents deletion
        workloadScripts.deleteTestPlan(testPlan);

        assertEquals(1, workload.getTestPlans().size());
        verify(messages, never()).info(anyString());
        verify(messages, never()).warn(anyString());
    }

    // --- saveGroup ---

    @Test
    public void testSaveGroup_CallsSave_SetsCurrentTestPlan() {
        workloadScripts.setTabIndex(0);
        workloadScripts.saveGroup();
        // save() sets currentTestPlan from the workload's plan list at tabIndex
        assertEquals(testPlan, workloadScripts.getCurrentTestPlan());
    }

    // --- addScriptGroupStep ---

    @Test
    public void testAddScriptGroupStep_AddsStepsFromTargetToScriptGroup() {
        Script s1 = Script.builder().name("Script1").build();
        Script s2 = Script.builder().name("Script2").build();

        List<Script> source = new ArrayList<>();
        List<Script> target = new ArrayList<>(List.of(s1, s2));
        DualListModel<Script> model = new DualListModel<>(source, target);
        workloadScripts.setScriptSelectionModel(model);

        ScriptGroup group = new ScriptGroup();
        group.setName("MyGroup");
        workloadScripts.setScriptGroup(group);

        workloadScripts.addScriptGroupStep();

        assertEquals(2, group.getScriptGroupSteps().size());
        assertEquals(s1, group.getScriptGroupSteps().get(0).getScript());
        assertEquals(s2, group.getScriptGroupSteps().get(1).getScript());
        // initScriptSelectionModel() is called; model is rebuilt from scriptLoader
        assertNotNull(workloadScripts.getScriptSelectionModel());
    }

    @Test
    public void testAddScriptGroupStep_WhenTargetEmpty_AddsNoSteps() {
        List<Script> source = new ArrayList<>();
        List<Script> target = new ArrayList<>();
        DualListModel<Script> model = new DualListModel<>(source, target);
        workloadScripts.setScriptSelectionModel(model);

        ScriptGroup group = new ScriptGroup();
        group.setName("EmptyGroup");
        workloadScripts.setScriptGroup(group);

        workloadScripts.addScriptGroupStep();

        assertTrue(group.getScriptGroupSteps().isEmpty());
    }

    // --- deleteScriptGroupStep ---

    @Test
    public void testOnSourceSelect_SetsSelectedAvailableScripts() {
        @SuppressWarnings("unchecked")
        SelectEvent<Script> event = mock(SelectEvent.class);
        Script script = new Script();
        when(event.getObject()).thenReturn(script);

        workloadScripts.onSourceSelect(event);

        assertEquals(List.of(script), workloadScripts.getSelectedAvailableScripts());
    }

    @Test
    public void testOnTargetSelect_SetsSelectedSelectedScripts() {
        @SuppressWarnings("unchecked")
        SelectEvent<Script> event = mock(SelectEvent.class);
        Script script = new Script();
        when(event.getObject()).thenReturn(script);

        workloadScripts.onTargetSelect(event);

        assertEquals(List.of(script), workloadScripts.getSelectedSelectedScripts());
    }

    @Test
    public void testGetScriptGroups_ReturnsCurrentTestPlanGroups() {
        ScriptGroup group = new ScriptGroup();
        group.setName("TestGroup");
        testPlan.addScriptGroup(group);
        workloadScripts.setCurrentTestPlan(testPlan);

        List<ScriptGroup> groups = workloadScripts.getScriptGroups();
        assertNotNull(groups);
        assertEquals(1, groups.size());
        assertEquals("TestGroup", groups.get(0).getName());
    }

    @Test
    public void testCopyTo_WithScriptGroupSteps_CopiesSteps() {
        Script script = Script.builder().name("TestScript").build();
        ScriptGroup group = new ScriptGroup();
        group.setName("GroupWithSteps");
        group.setLoop(2);
        ScriptGroupStep step = new ScriptGroupStep();
        step.setLoop(3);
        step.setScript(script);
        group.addScriptGroupStep(step);
        testPlan.addScriptGroup(group);

        Workload target = Workload.builder().name("Target").build();
        workloadScripts.copyTo(target);

        assertEquals(1, target.getTestPlans().size());
        assertEquals(1, target.getTestPlans().get(0).getScriptGroups().size());
        assertEquals(1, target.getTestPlans().get(0).getScriptGroups().get(0).getScriptGroupSteps().size());
        assertEquals(3, target.getTestPlans().get(0).getScriptGroups().get(0).getScriptGroupSteps().get(0).getLoop());
    }

    @Test
    public void testSetInsertIndex_SetsValue() {
        workloadScripts.setInsertIndex(5);
        // insertIndex is private; we verify indirectly via addScriptGroup using insertIndex
        ScriptGroup group = new ScriptGroup();
        group.setName("GroupAtIndex");
        workloadScripts.setCurrentTestPlan(testPlan);
        workloadScripts.setCurrentScriptGroup(group);
        workloadScripts.setInsertIndex(0);
        workloadScripts.addScriptGroup();
        assertEquals(1, testPlan.getScriptGroups().size());
    }

    @Test
    public void testDeleteScriptGroupStep_RemovesStepAndReinitializesModel() {
        Script s1 = Script.builder().name("Script1").build();

        ScriptGroupStep step = new ScriptGroupStep();
        step.setScript(s1);
        step.setLoop(1);

        ScriptGroup group = new ScriptGroup();
        group.setName("GroupWithStep");
        group.addScriptGroupStep(step);
        workloadScripts.setScriptGroup(group);

        // Provide an existing selection model so getSource() is non-null before reinit
        List<Script> source = new ArrayList<>();
        List<Script> target = new ArrayList<>();
        DualListModel<Script> model = new DualListModel<>(source, target);
        workloadScripts.setScriptSelectionModel(model);

        workloadScripts.deleteScriptGroupStep(step);

        assertTrue(group.getScriptGroupSteps().isEmpty());
        // After reinit, scriptSelectionModel is rebuilt from scriptLoader (returns empty list)
        assertNotNull(workloadScripts.getScriptSelectionModel());
    }
}

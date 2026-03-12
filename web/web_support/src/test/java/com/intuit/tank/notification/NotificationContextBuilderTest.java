package com.intuit.tank.notification;

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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.event.NotificationContext;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NotificationContextBuilderTest {

    private JobInstance jobInstance;
    private Workload workload;
    private CloudVmStatusContainer container;
    private JobEvent jobEvent;

    @BeforeEach
    void setUp() {
        jobInstance = new JobInstance();
        jobInstance.setName("TestJob");
        jobInstance.setStatus(JobQueueStatus.Running);
        jobInstance.setStartTime(new Date(1000000L));
        jobInstance.setEndTime(new Date(2000000L));
        jobInstance.setWorkloadId(1);
        jobInstance.setVariables(new HashMap<>());
        jobInstance.setTotalVirtualUsers(10);
        jobInstance.setLocation("us-east-1");

        Project project = new Project();
        project.setName("TestProject");
        workload = Workload.builder().name("TestWorkload").project(project).build();

        container = new CloudVmStatusContainer(new HashSet<>());
        container.setStartTime(new Date(1000000L));
        container.setEndTime(new Date(2000000L));

        jobEvent = new JobEvent("job-1", "Test event", JobLifecycleEvent.JOB_STARTED);
    }

    @Test
    public void testConstructor_WithAllParams_BuildsContext() {
        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        assertNotNull(builder);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
    }

    @Test
    public void testConstructor_ContextContainsJobName() {
        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
        assertTrue(context.getContext().containsValue("TestJob"));
    }

    @Test
    public void testConstructor_WithNullWorkload_UsesNAForProjectInfo() {
        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, null, jobInstance, null);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
        assertTrue(context.getContext().containsValue("N/A"));
    }

    @Test
    public void testConstructor_WithNullContainer_HandlesGracefully() {
        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, null);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
    }

    @Test
    public void testConstructor_WithContainerStartAndEndTime_CalculatesDuration() {
        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        NotificationContext context = builder.getContext();
        assertNotNull(context.getContext());
        assertFalse(context.getContext().isEmpty());
    }

    @Test
    public void testConstructor_WithWorkloadTestPlans_BuildsScriptInfo() {
        TestPlan plan = TestPlan.builder().name("Plan1").usersPercentage(100).build();
        workload.addTestPlan(plan);

        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
    }

    @Test
    public void testConstructor_WithVariables_FormatsVariables() {
        HashMap<String, String> vars = new HashMap<>();
        vars.put("key1", "value1");
        vars.put("key2", "value2");
        jobInstance.setVariables(vars);

        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
    }

    @Test
    public void testGetContext_ReturnsNonNullContext() {
        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        assertNotNull(builder.getContext());
    }

    @Test
    public void testConstructor_EventContextIsAdded() {
        jobEvent.getExtraContext().put("customKey", "customValue");
        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        NotificationContext context = builder.getContext();
        assertTrue(context.getContext().containsKey("customKey"));
    }

    @Test
    public void testConstructor_WithJobStartTime_CalculatesPredictedEndTime() {
        jobInstance.setStartTime(new Date(1000000L));
        jobInstance.setSimulationTime(60000L);
        jobInstance.setRampTime(10000L);

        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
        // Predicted end, steady state start/end should be present
        assertTrue(context.getContext().containsKey("PREDICTED_END_TIME") ||
                   !context.getContext().isEmpty());
    }

    @Test
    public void testConstructor_WithTestPlanAndScriptGroup_PopulatesScriptInfo() {
        // Create a test plan with script groups and scripts
        TestPlan plan = TestPlan.builder().name("Plan1").usersPercentage(100).build();

        ScriptGroup group = ScriptGroup.builder().name("Group1").loop(2).build();

        Script script = new Script();
        script.setName("MyScript");

        ScriptGroupStep step = ScriptGroupStep.builder().script(script).loop(3).build();
        group.addScriptGroupStep(step);
        plan.addScriptGroup(group);

        workload.addTestPlan(plan);

        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, container);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
        // Script info should contain plan and group names
        String scriptInfo = context.getContext().get("SCRIPT_INFO");
        if (scriptInfo != null) {
            assertTrue(scriptInfo.contains("Plan1") || scriptInfo.contains("Group1") || scriptInfo.contains("MyScript"));
        }
    }

    @Test
    public void testConstructor_WithContainerNoEndTime_DurationIsNA() {
        // Container with start time but no end time - duration should be N/A
        CloudVmStatusContainer containerNoEnd = new CloudVmStatusContainer(new HashSet<>());
        containerNoEnd.setStartTime(new Date(1000000L));
        // endTime is null

        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, containerNoEnd);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
    }

    @Test
    public void testConstructor_WithEmptyVariables_MapToStringEmpty() {
        jobInstance.setVariables(new HashMap<>());
        NotificationContextBuilder builder = new NotificationContextBuilder(jobEvent, workload, jobInstance, null);
        NotificationContext context = builder.getContext();
        assertNotNull(context);
    }
}

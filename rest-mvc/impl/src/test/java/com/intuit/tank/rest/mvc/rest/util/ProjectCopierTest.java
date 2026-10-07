/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ProjectCopierTest {

    @Test
    void copiesEverythingIntoNewEntities() {
        Project source = ProjectFixtures.project(1, "Original", "alice");
        Project copy = ProjectCopier.copy(source, "Copy", "bob");

        assertEquals(0, copy.getId(), "the copy is unsaved");
        assertEquals("Copy", copy.getName());
        assertEquals("bob", copy.getCreator());
        assertEquals("Payroll", copy.getProductName());
        assertEquals("comments", copy.getComments());

        Workload workload = copy.getWorkloads().get(0);
        assertEquals("Copy", workload.getName());
        assertEquals(0, workload.getId());
        JobConfiguration config = workload.getJobConfiguration();
        JobConfiguration original = source.getWorkloads().get(0).getJobConfiguration();
        assertNotSame(original, config);
        assertEquals("30m", config.getSimulationTimeExpression());
        assertEquals("5m", config.getRampTimeExpression());
        assertEquals(2, config.getBaselineVirtualUsers());
        assertEquals(3, config.getUserIntervalIncrement());
        assertEquals("c5.large", config.getVmInstanceType());
        assertTrue(config.isAllowOverride(), "allowOverride was not copied by the old web UI copy");
        assertEquals(Map.of("host", "example.com"), config.getVariables());
        assertEquals(java.util.Set.of(7), config.getDataFileIds());

        JobRegion region = config.getJobRegions().iterator().next();
        assertEquals(0, region.getId(), "regions are new rows");
        assertEquals("100", region.getUsers());

        assertEquals(1, config.getNotifications().size());
        assertEquals(List.of(JobLifecycleEvent.JOB_FINISHED), config.getNotifications().iterator().next().getLifecycleEvents());

        TestPlan plan = workload.getTestPlans().get(0);
        assertEquals("Main", plan.getName());
        assertEquals(100, plan.getUserPercentage());
        assertEquals(2, plan.getScriptGroups().get(0).getLoop());
        ScriptGroupStep step = plan.getScriptGroups().get(0).getScriptGroupSteps().get(0);
        assertEquals(3, step.getLoop());
        assertEquals(42, step.getScript().getId(), "steps refer to the same script");
        assertSame(plan.getScriptGroups().get(0), step.getScriptGroup());
    }

    @Test
    void copyIsIndependentOfSource() {
        Project source = ProjectFixtures.project(1, "Original", "alice");
        Project copy = ProjectCopier.copy(source, "Copy", "bob");
        copy.getWorkloads().get(0).getJobConfiguration().getVariables().put("extra", "1");
        copy.getWorkloads().get(0).getTestPlans().clear();
        assertFalse(source.getWorkloads().get(0).getJobConfiguration().getVariables().containsKey("extra"));
        assertEquals(1, source.getWorkloads().get(0).getTestPlans().size());
    }
}

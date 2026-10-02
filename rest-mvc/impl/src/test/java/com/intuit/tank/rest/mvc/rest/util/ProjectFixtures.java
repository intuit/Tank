/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.models.ProjectDetail;
import com.intuit.tank.vm.api.enumerated.IncrementStrategy;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * A saved-looking project graph for tests: one workload, one test plan with one group and one script.
 */
public final class ProjectFixtures {

    public static final Date MODIFIED = new Date(1_700_000_000_000L);

    private ProjectFixtures() {
    }

    public static Script script(int id, String name) {
        Script script = new Script();
        script.setId(id);
        script.setName(name);
        return script;
    }

    public static Project project(int id, String name, String owner) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setCreator(owner);
        project.setProductName("Payroll");
        project.setComments("comments");
        project.setModified(MODIFIED);

        Workload workload = new Workload();
        workload.setId(id * 10);
        workload.setName(name);
        workload.setParent(project);
        JobConfiguration config = workload.getJobConfiguration();
        config.setParent(workload);
        config.setIncrementStrategy(IncrementStrategy.increasing);
        config.setTerminationPolicy(TerminationPolicy.time);
        config.setSimulationTimeExpression("30m");
        config.setRampTimeExpression("5m");
        config.setBaselineVirtualUsers(2);
        config.setUserIntervalIncrement(3);
        config.setVmInstanceType("c5.large");
        config.setAllowOverride(true);
        config.getVariables().put("host", "example.com");
        config.getDataFileIds().add(7);
        JobRegion east = new JobRegion(VMRegion.US_EAST, "100", "0");
        east.setId(501);
        config.getJobRegions().add(east);
        JobNotification notification = new JobNotification();
        notification.setSubject("done");
        notification.setRecipientList("ops@example.com");
        notification.setLifecycleEvents(new ArrayList<>(List.of(JobLifecycleEvent.JOB_FINISHED)));
        config.getNotifications().add(notification);

        TestPlan plan = new TestPlan();
        plan.setName("Main");
        plan.setUserPercentage(100);
        ScriptGroup group = new ScriptGroup();
        group.setName("Login");
        group.setLoop(2);
        ScriptGroupStep step = new ScriptGroupStep();
        step.setScript(script(42, "login script"));
        step.setLoop(3);
        group.addScriptGroupStep(step);
        plan.addScriptGroup(group);
        workload.addTestPlan(plan);

        List<Workload> workloads = new ArrayList<>();
        workloads.add(workload);
        project.setWorkloads(workloads);
        return project;
    }

    /** A valid editor payload matching {@link #project} with the given changes applied by the caller. */
    public static ProjectDetail detail(String name, String owner) {
        return new ProjectDetail(1, name, "Payroll", "comments", owner, null, MODIFIED,
                new ProjectDetail.Settings(IncrementStrategy.increasing, TerminationPolicy.time, "30m", "5m", 2, 3, 1.0,
                        "unspecified", "END_OF_SCRIPT_GROUP", "STANDARD", "c5.large", 4000, 1.0, false,
                        "com.intuit.tank.httpclient4.TankHttpClient4", "none", true),
                List.of(new ProjectDetail.RegionUsers(VMRegion.US_EAST, "100", "0")),
                List.of(new ProjectDetail.TestPlanDetail("Main", 100, List.of(
                        new ProjectDetail.ScriptGroupDetail("Login", 2, List.of(new ProjectDetail.ScriptRef(42, null, 3)))))),
                Map.of("host", "example.com"), List.of(7), null);
    }
}

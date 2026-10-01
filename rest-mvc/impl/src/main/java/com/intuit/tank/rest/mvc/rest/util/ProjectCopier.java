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
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Deep copy of a project for "save as": settings, regions, test plans, script groups, variables,
 * data files and notifications. The copy is new and unsaved, and refers to the same scripts.
 * Shared by the web UI and the REST API.
 */
public final class ProjectCopier {

    private ProjectCopier() {
    }

    /**
     * @param source a project loaded with its workload, job configuration and test plans
     * @param name   the copy's name
     * @param owner  the copy's creator
     */
    public static Project copy(Project source, String name, String owner) {
        Project copy = new Project();
        copy.setName(name);
        copy.setCreator(owner);
        copy.setComments(source.getComments());
        copy.setProductName(source.getProductName());
        copy.setScriptDriver(source.getScriptDriver());

        Workload sourceWorkload = source.getWorkloads().get(0);
        Workload workload = new Workload();
        workload.setName(name);
        workload.setParent(copy);
        workload.setJobConfiguration(copyJobConfiguration(sourceWorkload.getJobConfiguration(), workload));
        for (TestPlan plan : sourceWorkload.getTestPlans()) {
            workload.addTestPlan(copyTestPlan(plan));
        }
        List<Workload> workloads = new ArrayList<>();
        workloads.add(workload);
        copy.setWorkloads(workloads);
        return copy;
    }

    static JobConfiguration copyJobConfiguration(JobConfiguration original, Workload parent) {
        JobConfiguration copy = new JobConfiguration();
        copy.setIncrementStrategy(original.getIncrementStrategy());
        copy.setTerminationPolicy(original.getTerminationPolicy());
        copy.setLocation(original.getLocation());
        copy.setRampTime(original.getRampTime());
        copy.setRampTimeExpression(original.getRampTimeExpression());
        copy.setSimulationTime(original.getSimulationTime());
        copy.setSimulationTimeExpression(original.getSimulationTimeExpression());
        copy.setExecutionTime(original.getExecutionTime());
        copy.setBaselineVirtualUsers(original.getBaselineVirtualUsers());
        copy.setUserIntervalIncrement(original.getUserIntervalIncrement());
        copy.setTargetRampRate(original.getTargetRampRate());
        copy.setVmInstanceType(original.getVmInstanceType());
        copy.setNumUsersPerAgent(original.getNumUsersPerAgent());
        copy.setNumAgents(original.getNumAgents());
        copy.setTargetRatePerAgent(original.getTargetRatePerAgent());
        copy.setLoggingProfile(original.getLoggingProfile());
        copy.setStopBehavior(original.getStopBehavior());
        copy.setTankClientClass(original.getTankClientClass());
        copy.setReportingMode(original.getReportingMode());
        copy.setAllowOverride(original.isAllowOverride());
        copy.setUseEips(original.isUseEips());
        copy.setUseTwoStep(original.isUseTwoStep());
        copy.setVariables(new HashMap<>(original.getVariables()));
        copy.setDataFileIds(new HashSet<>(original.getDataFileIds()));
        copy.setJobRegions(original.getJobRegions().stream()
                .map(r -> new JobRegion(r.getRegion(), r.getUsers(), r.getPercentage()))
                .collect(Collectors.toCollection(HashSet::new)));
        copy.setNotifications(original.getNotifications().stream()
                .map(ProjectCopier::copyNotification)
                .collect(Collectors.toCollection(HashSet::new)));
        copy.setParent(parent);
        return copy;
    }

    private static JobNotification copyNotification(JobNotification original) {
        JobNotification copy = new JobNotification();
        copy.setSubject(original.getSubject());
        copy.setBody(original.getBody());
        copy.setRecipientList(original.getRecipientList());
        copy.setLifecycleEvents(new ArrayList<>(original.getLifecycleEvents()));
        return copy;
    }

    private static TestPlan copyTestPlan(TestPlan original) {
        TestPlan copy = new TestPlan();
        copy.setName(original.getName());
        copy.setUserPercentage(original.getUserPercentage());
        for (ScriptGroup group : original.getScriptGroups()) {
            ScriptGroup groupCopy = new ScriptGroup();
            groupCopy.setName(group.getName());
            groupCopy.setLoop(group.getLoop());
            for (ScriptGroupStep step : group.getScriptGroupSteps()) {
                ScriptGroupStep stepCopy = new ScriptGroupStep();
                stepCopy.setLoop(step.getLoop());
                stepCopy.setScript(step.getScript());
                groupCopy.addScriptGroupStep(stepCopy);
            }
            copy.addScriptGroup(groupCopy);
        }
        return copy;
    }
}

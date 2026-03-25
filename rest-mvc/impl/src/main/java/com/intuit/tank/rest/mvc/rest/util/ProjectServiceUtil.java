/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.Workload;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.projects.models.*;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class ProjectServiceUtil {

    private static final Logger LOG = LogManager.getLogger(ProjectServiceUtil.class);

    private ProjectServiceUtil() { }

    public static ProjectTO projectToTransferObject(Project p) {
        Workload workload = p.getWorkloads().get(0);
        JobConfiguration config = workload.getJobConfiguration();

        List<AutomationTestPlan> testPlans = workload.getTestPlans().stream()
                .map(testPlan -> AutomationTestPlan.builder()
                        .withName(testPlan.getName())
                        .withUserPercentage(testPlan.getUserPercentage())
                        .withPosition(testPlan.getPosition())
                        .withScriptGroups(testPlan.getScriptGroups().stream()
                                .map(sg -> AutomationScriptGroup.builder()
                                        .withName(sg.getName())
                                        .withLoop(sg.getLoop())
                                        .withPosition(sg.getPosition())
                                        .withScripts(sg.getScriptGroupSteps().stream()
                                                .map(step -> AutomationScriptGroupStep.builder()
                                                        .withScriptId(step.getScript().getId())
                                                        .withName(step.getScript().getName())
                                                        .withLoop(step.getLoop())
                                                        .withPosition(step.getPosition())
                                                        .build())
                                                .collect(Collectors.toList()))
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        List<KeyPair> variables = config.getVariables().entrySet().stream()
                .map(e -> new KeyPair(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return ProjectTO.builder()
                .withComments(p.getComments())
                .withCreated(p.getCreated())
                .withModified(p.getModified())
                .withCreator(p.getCreator())
                .withId(p.getId())
                .withName(p.getName())
                .withProductName(p.getProductName())
                .withRampTime(config.getRampTimeExpression())
                .withStopBehavior(config.getStopBehavior())
                .withSimulationTime(config.getSimulationTime())
                .withTerminationPolicy(config.getTerminationPolicy())
                .withWorkloadType(config.getIncrementStrategy())
                .withLocation(config.getLocation())
                .withUserIntervalIncrement(config.getUserIntervalIncrement())
                .withJobRegions(config.getJobRegions().stream()
                        .map(jobRegion -> AutomationJobRegion.builder()
                                .withRegion(jobRegion.getRegion())
                                .withUsers(jobRegion.getUsers())
                                .build())
                        .collect(Collectors.toSet()))
                .withTestPlans(testPlans)
                .withVariables(variables)
                .withDataFileIds(config.getDataFileIds())
                .build();
    }

    /**
     * @param jobId
     * @return
     */
    public static synchronized File getScriptFile(String jobId) {
        File file = ProjectDaoUtil.getScriptFile(jobId);
        if (!file.exists()) {
            if (NumberUtils.isCreatable(jobId)) {
                try {
                    String scriptString = getScriptString(new JobInstanceDao().findById(Integer.valueOf(jobId)));
                    ProjectDaoUtil.storeScriptFile(jobId, scriptString);
                } catch (Exception e) {
                    LOG.error("Erorr writing file: " + e, e);
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Job is not a stored job.");
            }
        }
        return file;
    }

    /**
     * @param job
     * @return
     */
    public static String getScriptString(JobInstance job) {
        WorkloadDao dao = new WorkloadDao();
        Workload workload = dao.findById(job.getWorkloadId());
        workload.getTestPlans();
        dao.loadScriptsForWorkload(workload);
        HDWorkload hdWorkload = ConverterUtil.convertWorkload(workload, job);
        return ConverterUtil.getWorkloadXML(hdWorkload);
    }

}

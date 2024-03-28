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
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.Workload;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.rest.mvc.rest.models.projects.*;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class ProjectServiceUtil {

    private static final Logger LOG = LogManager.getLogger(ProjectServiceUtil.class);

    private ProjectServiceUtil() { }

    public static ProjectTO projectToTransferObject(Project p) {
        JobConfiguration config = p.getWorkloads().get(0).getJobConfiguration();
        ProjectTO.ProjectTOBuilder ret = ProjectTO.builder()
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
                .withUserIntervalIncrement(config.getUserIntervalIncrement());

        for(JobRegion jobRegion : config.getJobRegions()) {
            ret.withJobRegion(new AutomationJobRegion(jobRegion.getRegion(), jobRegion.getUsers()));
        }

        for(TestPlan testPlan : p.getWorkloads().get(0).getTestPlans()) {
            List<AutomationScriptGroup> retScriptGroups = new ArrayList<>();
            for(ScriptGroup sg : testPlan.getScriptGroups()) {
                List<AutomationScriptGroupStep> retScripts = sg.getScriptGroupSteps().stream()
                        .map(script -> AutomationScriptGroupStep.builder()
                                .withScriptId(script.getScript().getId())
                                .withName(script.getScript().getName())
                                .withLoop(script.getLoop())
                                .withPosition(script.getPosition())
                                .build())
                        .collect(Collectors.toList());
                retScriptGroups.add(AutomationScriptGroup.builder()
                        .withName(sg.getName())
                        .withLoop(sg.getLoop())
                        .withPosition(sg.getPosition())
                        .withScripts(retScripts)
                        .build());
            }
            ret.withTestPlan(AutomationTestPlan.builder()
                    .withName(testPlan.getName())
                    .withUserPercentage(testPlan.getUserPercentage())
                    .withPosition(testPlan.getPosition())
                    .withScriptGroups(retScriptGroups)
                    .build());
        }

        for (Entry<String, String> entry : config.getVariables().entrySet()) {
            ret.withVariable(new KeyPair(entry.getKey(), entry.getValue()));
        }
        for (Integer dataFileId : config.getDataFileIds()) {
            ret.withDataFileId(dataFileId);
        }
        return ret.build();
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

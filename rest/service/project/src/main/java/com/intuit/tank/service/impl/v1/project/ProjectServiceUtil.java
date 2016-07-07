/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.project;

/*
 * #%L
 * Project Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.File;
import java.util.Map.Entry;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.api.model.v1.project.KeyPair;
import com.intuit.tank.api.model.v1.project.ProjectTO;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.perfManager.workLoads.util.WorkloadScriptUtil;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectDTO;
import com.intuit.tank.project.Workload;

/**
 * ProjectServiceUtil
 * 
 * @author dangleton
 * 
 */
public class ProjectServiceUtil {

    private static final Logger LOG = Logger.getLogger(ProjectServiceUtil.class);

    private ProjectServiceUtil() {

    }

    public static ProjectTO projectToTransferObject(Project p) {
        ProjectTO ret = new ProjectTO();
        ret.setComments(p.getComments());
        ret.setCreated(p.getCreated());
        ret.setModified(p.getModified());
        ret.setCreator(p.getCreator());
        ret.setId(p.getId());
        ret.setName(p.getName());
        ret.setProductName(p.getProductName());
        JobConfiguration config = p.getWorkloads().get(0).getJobConfiguration();
        for (Entry<String, String> entry : config.getVariables().entrySet()) {
            ret.getVariables().add(new KeyPair(entry.getKey(), entry.getValue()));
        }
        for (Integer dataFileId : config.getDataFileIds()) {
            ret.getDataFileIds().add(dataFileId);
        }
        return ret;
    }

    /**
     * @param job
     * @return
     */
    public static synchronized File getScriptFile(String jobId) {
        File f = ProjectDaoUtil.getScriptFile(jobId);
        if (!f.exists()) {
            if (NumberUtils.isNumber(jobId)) {
                try {
                    String scriptString = getScriptString(new JobInstanceDao().findById(Integer.parseInt(jobId)));
                    ProjectDaoUtil.storeScriptFile(jobId, scriptString);
                } catch (Exception e) {
                    LOG.error("Erorr writing file: " + e, e);
                    throw new RuntimeException(e);
                }
            } else {
                throw new RuntimeException("Job is not a stored job.");
            }
        }

        return f;
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
        return WorkloadScriptUtil.getScriptForWorkload(workload, job);
    }

}

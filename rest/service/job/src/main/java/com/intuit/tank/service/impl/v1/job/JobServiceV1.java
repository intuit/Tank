/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.job;

/*
 * #%L
 * Job Rest Service
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import com.intuit.tank.api.model.v1.job.JobContainer;
import com.intuit.tank.api.model.v1.job.JobTO;
import com.intuit.tank.api.service.v1.job.JobService;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.Project;
import com.intuit.tank.util.CreateDateComparator;
import com.intuit.tank.util.CreateDateComparator.SortOrder;

/**
 * AutomationServiceV1
 * 
 * @author dangleton
 * 
 */
@Path(JobService.SERVICE_RELATIVE_PATH)
public class JobServiceV1 implements JobService {

    // private static final Logger LOG = LogManager.getLogger(FilterServiceV1.class);

    // @Context
    // private ServletContext servletContext;

    /**
     * @inheritDoc
     */
    @Override
    public String ping() {
        return "PONG " + getClass().getSimpleName();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getJob(int jobId) {
        ResponseBuilder response = Response.ok();
        JobInstanceDao dao = new JobInstanceDao();
        JobInstance job = dao.findById(jobId);
        if (job != null) {
            response.entity(JobServiceUtil.jobToTO(job));
        } else {
            response = Response.status(Status.NOT_FOUND);
        }
        return response.build();
    }

    /**
     * @inheritDoc
     */
    @Override
    public Response getJobsForProject(int projectId) {
        ResponseBuilder response = Response.ok();
        Project prj = new ProjectDao().findById(projectId);
        if (prj != null) {
            JobQueue queue = new JobQueueDao().findOrCreateForProjectId(projectId);
            List<JobInstance> jobs = new ArrayList<JobInstance>(queue.getJobs());
            jobs.sort(new CreateDateComparator(SortOrder.DESCENDING));
            List<JobTO> list = jobs.stream().map(JobServiceUtil::jobToTO).collect(Collectors.toList());
            response.entity(new JobContainer(list));
        } else {
            response = Response.status(Status.NOT_FOUND);
        }
        return response.build();
    }
}

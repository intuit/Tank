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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import com.intuit.tank.api.model.v1.project.ProjectContainer;
import com.intuit.tank.api.model.v1.project.ProjectTO;
import com.intuit.tank.api.service.v1.project.ProjectService;
import com.intuit.tank.dao.BaseDao;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.perfManager.workLoads.util.WorkloadScriptUtil;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.ProjectDTO;
import com.intuit.tank.project.Workload;
import com.intuit.tank.service.impl.v1.cloud.JobController;
import com.intuit.tank.service.util.ResponseUtil;
import com.intuit.tank.service.util.ServletInjector;
import com.intuit.tank.vm.common.util.ReportUtil;

/**
 * ProjectServiceV1
 * 
 * @author dangleton
 * 
 */
@Path(ProjectService.SERVICE_RELATIVE_PATH)
public class ProjectServiceV1 implements ProjectService {

    private static final Logger LOG = Logger.getLogger(ProjectServiceV1.class);

    @Context
    private ServletContext servletContext;

    /**
     * @{inheritDoc
     */
    @Override
    public String ping() {
        return "PONG " + getClass().getSimpleName();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response deleteProject(int projectId) {
        return delete(projectId);
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response deleteProjectPost(int projectId) {
        return delete(projectId);
    }

    /**
     * Check that project exists and delete it.
     * 
     * @param projectId
     * @return Response suitable to pass to client
     */
    private Response delete(int projectId) {
        ResponseBuilder responseBuilder = Response.noContent();
        ProjectDao dao = new ProjectDao();
        try {
            Project project = dao.findById(projectId);
            if (project == null) {
                LOG.warn("Proect with id " + projectId + "does not exist.");
                responseBuilder.status(Status.BAD_REQUEST);
                responseBuilder.entity("Project with id " + projectId + " does not exist.");

            } else {
                dao.delete(project);
            }
        } catch (RuntimeException e) {
            LOG.error("Error deleting project: " + e, e);
            responseBuilder.status(Status.INTERNAL_SERVER_ERROR);
            responseBuilder.entity("An error occurred while deleting the project.");
        }
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public StreamingOutput getTestScriptForJob(String jobId) {
        File f = ProjectDaoUtil.getScriptFile(jobId);
        if (!f.exists()) {
            if (NumberUtils.isNumber(jobId)) {
                JobInstance job = new JobInstanceDao().findById(Integer.parseInt(jobId));
                if (job == null) {
                    throw new RuntimeException("Cannot find Job with id of " + jobId);
                }
                ProjectDaoUtil.storeScriptFile(jobId, ProjectServiceUtil.getScriptString(job));
                f = ProjectDaoUtil.getScriptFile(jobId);
            } else {
                throw new RuntimeException("Cannot create job script for non persisted jobs");

            }
        }
        final File file = f;
        return new StreamingOutput() {
            public void write(OutputStream outputStream) {
                BufferedReader in = null;
                // Get the object of DataInputStream
                try {
                    in = new BufferedReader(new FileReader(file));
                    IOUtils.copy(in, outputStream);
                } catch (IOException e) {
                    LOG.error("Error streaming file: " + e.toString(), e);
                    throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
                } finally {
                    IOUtils.closeQuietly(in);
                }
            }
        };
    }

    /**
     * @{inheritDoc
     */
    @Override
    public StreamingOutput getTestScriptForProject(Integer projectId) {
        ProjectDao dao = new ProjectDao();
        Project p = dao.findById(projectId);
        if (p == null) {
            throw new RuntimeException("Cannot find Project with id of " + projectId);
        }
        p.getWorkloads().get(0).getJobConfiguration().getTankClientClass();			//Necessary to keep the hibernate from loosing datarefernece.
        Workload workload = new WorkloadDao().loadScriptsForWorkload(p.getWorkloads().get(0));
        final String scriptString = WorkloadScriptUtil.getScriptForWorkload(workload, p.getWorkloads().get(0).getJobConfiguration());
        return new StreamingOutput() {
            public void write(OutputStream outputStream) {
                // Get the object of DataInputStream
                try {
                    IOUtils.write(scriptString, outputStream);
                } catch (IOException e) {
                    LOG.error("Error streaming file: " + e.toString(), e);
                    throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
                } finally {
                }
            }
        };
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response downloadTestScriptForJob(String jobId) {
        ResponseBuilder responseBuilder = Response.ok();
        String filename = "job_" + jobId + "_H.xml";
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).entity(getTestScriptForJob(jobId));
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response downloadTestScriptForProject(Integer projectId) {
        ResponseBuilder responseBuilder = Response.ok();
        String filename = "project_" + projectId + "_H.xml";
        responseBuilder.header("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        responseBuilder.cacheControl(ResponseUtil.getNoStoreCacheControl());
        responseBuilder.type(MediaType.APPLICATION_OCTET_STREAM_TYPE).entity(getTestScriptForProject(projectId));
        return responseBuilder.build();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public Response getProjectNames() {
        ResponseBuilder responseBuilder = Response.ok();
        List<Project> all = new ProjectDao().findAll();
        List<ProjectTO> to = new ArrayList<ProjectTO>();
        for (Project p : all) {
            to.add(ProjectServiceUtil.projectToTransferObject(p));
        }
        ProjectContainer container = new ProjectContainer(to);
        responseBuilder.entity(container);
        return responseBuilder.build();
    }

    @Override
    public Response runProject(Integer projectId) {
        ResponseBuilder responseBuilder = Response.ok();
        ProjectDao projectDao = new ProjectDao();
        Project project = projectDao.findById(projectId);
        if (project == null) {
            responseBuilder.status(Status.NOT_FOUND);
            responseBuilder.entity("Cannot find Project with id " + projectId);
        } else {
            JobInstance job = addJobToQueue(project);
            LOG.info("Job (" + job.getId() + ") requested with values: " + job);
            String jobId = Integer.toString(job.getId());

            JobController controller = new ServletInjector<JobController>().getManagedBean(servletContext,
                    JobController.class);
            controller.startJob(jobId);
            responseBuilder = Response.ok();
            // add jobId to response
            responseBuilder.entity("Started job with id " + jobId);
        }

        return responseBuilder.build();
    }

    public JobInstance addJobToQueue(Project p) {

        JobQueueDao jobQueueDao = new JobQueueDao();
        // WorkloadDao wdao = new WorkloadDao();
        DataFileDao dataFileDao = new DataFileDao();
        JobNotificationDao jobNotificationDao = new JobNotificationDao();
        JobRegionDao jobRegionDao = new JobRegionDao();
        JobInstanceDao jobInstanceDao = new JobInstanceDao();

        Workload workload = p.getWorkloads().get(0);
        JobConfiguration jc = workload.getJobConfiguration();
        JobQueue queue = jobQueueDao.findOrCreateForProjectId(p.getId());
        String name = p.getName() + "_" + workload.getJobConfiguration().getTotalVirtualUsers() + "_users_"
                + ReportUtil.getTimestamp(new Date());
        JobInstance jobInstance = new JobInstance(workload, name);
        jobInstance.setScheduledTime(new Date());
        jobInstance.setLocation(jc.getLocation());
        jobInstance.setReportingMode(jc.getReportingMode());
        jobInstance.getVariables().putAll(jc.getVariables());
        // set version info
        if (jc.getDataFileIds() != null) {
            jobInstance.getDataFileVersions().addAll(getVersions(dataFileDao, jc.getDataFileIds(), DataFile.class));
        }
        jobInstance.getNotificationVersions().addAll(
                getVersions(jobNotificationDao, workload.getJobConfiguration().getNotifications()));

        Set<JobRegion> jobRegions = JobRegionDao.cleanRegions(jc.getJobRegions());
        jobInstance.setVariables(new HashMap<String, String>(workload.getJobConfiguration().getVariables()));
        jobInstance.setAllowOverride(workload.getJobConfiguration().isAllowOverride());
        jobInstance.getJobRegionVersions().addAll(getVersions(jobRegionDao, jobRegions));

        jobInstance = jobInstanceDao.saveOrUpdate(jobInstance);
        queue.addJob(jobInstance);

        jobQueueDao.saveOrUpdate(queue);
        storeScript(Integer.toString(jobInstance.getId()), workload, jobInstance);
        return jobInstance;
    }

    /**
     * @param dataFileDao2
     * @param dataFileIds
     * @return
     */
    @SuppressWarnings("rawtypes")
    private Set<EntityVersion> getVersions(BaseDao dao, Collection<Integer> dataFileIds,
            Class<? extends BaseEntity> entityClass) {
        HashSet<EntityVersion> result = new HashSet<EntityVersion>();
        for (Integer id : dataFileIds) {
            int versionId = dao.getHeadRevisionNumber(id);
            result.add(new EntityVersion(id, versionId, entityClass));
        }
        return result;
    }

    /**
     * @param dataFileDao2
     * @param dataFileIds
     * @return
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Set<EntityVersion> getVersions(BaseDao dao, Set<? extends BaseEntity> entities) {
        HashSet<Integer> ids = new HashSet<Integer>();
        Class entityClass = null;
        for (BaseEntity entity : entities) {
            ids.add(entity.getId());
            entityClass = entity.getClass();
        }
        return getVersions(dao, ids, entityClass);
    }

    private void storeScript(String jobId, Workload workload, JobInstance job) {
        new WorkloadDao().loadScriptsForWorkload(workload);
        String scriptString = WorkloadScriptUtil.getScriptForWorkload(workload, job);
        ProjectDaoUtil.storeScriptFile(jobId, scriptString);
    }

}

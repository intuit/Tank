/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.jobs;

import com.intuit.tank.dao.BaseDao;
import com.intuit.tank.dao.DataFileDao;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.dao.JobRegionDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.harness.StopBehavior;
import com.intuit.tank.project.BaseEntity;
import com.intuit.tank.project.DataFile;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobConfiguration;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.JobRegion;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptGroup;
import com.intuit.tank.project.ScriptGroupStep;
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceInternalServerException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.jobs.models.JobContainer;
import com.intuit.tank.jobs.models.JobTO;
import com.intuit.tank.jobs.models.CreateJobRequest;
import com.intuit.tank.jobs.models.CreateJobRegion;
import com.intuit.tank.rest.mvc.rest.util.*;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.JobQueueEventSender;
import com.intuit.tank.rest.mvc.rest.util.JobInstanceFactory;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.util.CreateDateComparator;
import com.intuit.tank.util.CreateDateComparator.SortOrder;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.xray.AWSXRay;
import org.springframework.beans.factory.annotation.Autowired;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceForbiddenAccessException;
import com.intuit.tank.rest.mvc.rest.security.JobAuthorization;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.vm.settings.AccessRight;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.annotation.Nonnull;
import jakarta.servlet.ServletContext;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;

@Service
public class JobServiceV2Impl implements JobServiceV2 {

    @Autowired
    private ServletContext servletContext;

    private static final Logger LOGGER = LogManager.getLogger(JobServiceV2Impl.class);

    @Override
    public String ping() {
        return "PONG " + getClass().getInterfaces()[0].getSimpleName();
    }

    @Override
    public JobTO getJob(Integer jobId)  {
        try {
            JobInstanceDao dao = new JobInstanceDao();
            JobInstance job = dao.findById(jobId);
            if (job != null) {
                return JobServiceUtil.jobToTO(job);
            } else {
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Error returning job: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("jobs", "job", e);
        }
    }

    @Override
    public JobContainer getJobsByProject(Integer projectId) {
        try {
            Project prj = new ProjectDao().findByIdEager(projectId);
            if (prj != null) {
                JobQueue queue = new JobQueueDao().findOrCreateForProjectId(projectId);
                List<JobInstance> jobs = new ArrayList<JobInstance>(queue.getJobs());
                jobs.removeIf(j -> j.getStatus() == JobQueueStatus.Deleted);
                jobs.sort(new CreateDateComparator(SortOrder.DESCENDING));
                List<JobTO> list = jobs.stream().map(JobServiceUtil::jobToTO).collect(Collectors.toList());
                return JobContainer.builder().withJobs(list).build();
            } else {
                return null;
            }
        } catch (Exception e){
            LOGGER.error("Error returning jobs by project: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("jobs", "jobs by project", e);
        }
    }

    @Override
    public JobContainer getAllJobs() {
        try {
            JobInstanceDao dao = new JobInstanceDao();
            List<JobInstance> jobs = dao.findAll();
            jobs.removeIf(j -> j.getStatus() == JobQueueStatus.Deleted);
            if (!jobs.isEmpty()) {
                jobs.sort(new CreateDateComparator(SortOrder.DESCENDING));
                List<JobTO> list = jobs.stream().map(JobServiceUtil::jobToTO).collect(Collectors.toList());
                return JobContainer.builder().withJobs(list).build();
            } else {
                return null;
            }
        } catch (Exception e){
            LOGGER.error("Error returning all jobs: {}", e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("jobs", "all jobs", e);
        }
    }

    @Override
    public Map<String, String> createJob(CreateJobRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            Integer projectId = request.getProjectId();
            if (projectId != null) {
                ProjectDao projectDao = new ProjectDao();
                Project project = new ProjectDao().findByIdEager(projectId);
                // creating a job saves the project's job configuration, so it needs the same rights as editing it
                RestAuthorization.requireRightOrOwner(AccessRight.EDIT_PROJECT, project, "jobs");
                buildJobConfiguration(request, project);
                project = projectDao.saveOrUpdateProject(project);
                JobInstance job = addJobToQueue(project, request);
                sendQueuedEvent(job);
                response.put("JobId", Integer.toString(job.getId()));
                response.put("status", "created");
            }
        } catch (GenericServiceForbiddenAccessException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Error creating job: {}", e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException("jobs", "job", e);
        }
        return response;
    }

    @Override
    public String getJobStatus(Integer jobId){
        try {
            JobInstance job = new JobInstanceDao().findById(jobId);
            if (job != null) {
                return job.getStatus().name();
            }
        } catch (Exception e) {
            LOGGER.error("Error returning job status: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("job", "status", e);
        }
        return null;
    }

    @Override
    public CloudVmStatusContainer getJobVMStatus(String jobId){
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(
                    servletContext, JobEventSender.class);
            return controller.getVmStatusForJob(jobId);
        } catch (Exception e) {
            LOGGER.error("Error returning Job Instance Status: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("job", "instance status", e);
        }
    }

    @Override
    public List<Map<String, String>> getAllJobStatus(){
        try {
            List<Map<String, String>> response = new ArrayList<>();
            JobInstanceDao dao = new JobInstanceDao();
            List<JobInstance> jobs = dao.findAll();
            for (JobInstance job : jobs) {
                Map<String, String> entry = new HashMap<>();
                if (job != null) {
                    entry.put("jobId", Integer.toString(job.getId()));
                    entry.put("status", job.getStatus().name());
                    response.add(entry);
                }
            }
            return response;
        } catch (Exception e) {
            LOGGER.error("Error returning all job statuses: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("job", "list of job statuses", e);
        }
    }

    @Override
    public StreamingResponseBody getTestScriptForJob(Integer jobId){
        String jobID = Integer.toString(jobId);
        File f = ProjectDaoUtil.getScriptFile(jobID);
        if (!f.exists()) {
            if (NumberUtils.isCreatable(jobID)) {
                JobInstance job = new JobInstanceDao().findById(jobId);
                if (job == null) {
                    LOGGER.error("Could not find job with job id: " + jobID);
                    throw new GenericServiceResourceNotFoundException("jobs", "job harness XML script file", null);
                }
                ProjectDaoUtil.storeScriptFile(jobID, ProjectServiceUtil.getScriptString(job));
                f = ProjectDaoUtil.getScriptFile(jobID);
            } else {
                LOGGER.error("Cannot create job script for non persisted jobs");
                throw new GenericServiceResourceNotFoundException("jobs", "job harness XML script file", null);
            }
        }
        final File file = f;
        return (OutputStream outputStream) -> {
            try ( BufferedReader in = new BufferedReader(new FileReader(file)) ) {
                IOUtils.copy(in, outputStream, StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOGGER.error("Error streaming job harness file: {}", e.getMessage(), e);
                throw new GenericServiceInternalServerException("jobs", "streaming output of job harness XML script file", e);
            }
        };
    }

    @Override
    public Map<String, StreamingResponseBody> downloadTestScriptForJob(Integer jobId) {
        Map<String, StreamingResponseBody> payload = new HashMap<String, StreamingResponseBody>();
        String filename = "job_" + jobId + "_H.xml";
        StreamingResponseBody streamingResponse = getTestScriptForJob(jobId);
        payload.put(filename, streamingResponse);
        return payload;
    }

    // Job Status Setters

    @Override
    public String startJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        JobAuthorization.requireJobControl(jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.startJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error starting job: {}", String.valueOf(e));
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to start", e);
        }
    }

    @Override
    public String stopJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        JobAuthorization.requireJobControl(jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.stopJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error stopping job: {}", String.valueOf(e));
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to stop", e);
        }
    }

    @Override
    public String pauseJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        JobAuthorization.requireJobControl(jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.pauseRampJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error pausing job: {}", String.valueOf(e));
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to pause", e);
        }
    }

    @Override
    public String resumeJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        JobAuthorization.requireJobControl(jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.resumeRampJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error resuming job: " + e);
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to resume", e);
        }
    }

    @Override
    public String killJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        JobAuthorization.requireJobControl(jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.killJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error killing job: {}", String.valueOf(e));
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to terminate", e);
        }
    }

    // Job Service Util

    public static void buildJobConfiguration(@Nonnull CreateJobRequest request, Project project) {
        JobConfiguration jobConfiguration = (project != null && project.getWorkloads() != null && !project.getWorkloads().isEmpty())
                ? project.getWorkloads().getFirst().getJobConfiguration()
                : null;

        if(jobConfiguration == null){
           throw new GenericServiceCreateOrUpdateException("jobs", "job", null);
        }

        if (StringUtils.isNotEmpty(request.getRampTime())) {
            jobConfiguration.setRampTimeExpression(request.getRampTime());
        }

        if (StringUtils.isNotEmpty(request.getSimulationTime())) {
            jobConfiguration.setSimulationTimeExpression(request.getSimulationTime());
        }

        if(request.getWorkloadType() != null) {
            jobConfiguration.setIncrementStrategy(request.getWorkloadType());
        }

        jobConfiguration.setUserIntervalIncrement(request.getUserIntervalIncrement());
        jobConfiguration.setTargetRampRate(request.getTargetRampRate());
        jobConfiguration.setTargetRatePerAgent(request.getTargetRatePerAgent());

        jobConfiguration.setStopBehavior(StringUtils.isNotEmpty(request.getStopBehavior())
                ? request.getStopBehavior() : StopBehavior.END_OF_SCRIPT_GROUP.name());

        if (StringUtils.isNotEmpty(request.getVmInstance())) {
            jobConfiguration.setVmInstanceType(request.getVmInstance());
        }
        jobConfiguration.setNumUsersPerAgent(request.getNumUsersPerAgent());

        boolean hasSimTime = jobConfiguration.getSimulationTime() > 0
                || (StringUtils.isNotBlank(request.getSimulationTime())
                && !"0".equals(request.getSimulationTime()));
        jobConfiguration
                .setTerminationPolicy(hasSimTime ? TerminationPolicy.time : TerminationPolicy.script);

        if (request.getJobRegions() != null) {
            setJobRegions(request, jobConfiguration);
        }
    }

    private static void setJobRegions(@Nonnull CreateJobRequest request, JobConfiguration jobConfiguration) {
        if(jobConfiguration == null || jobConfiguration.getJobRegions() == null){
            return;
        }
        jobConfiguration.getJobRegions().clear();
        JobRegionDao jrd = new JobRegionDao();
        if (request.getJobRegions() != null) {
            for (CreateJobRegion r : request.getJobRegions()) {
                if (StringUtils.isNotEmpty(r.getRegion())) {
                    String users = r.getUsers() != null ? r.getUsers() : "0";
                    String percentage = r.getPercentage() != null ? r.getPercentage() : "0";
                    JobRegion jr = jrd.saveOrUpdate(
                            new JobRegion(VMRegion.getRegionFromZone(r.getRegion()), users, percentage));
                    jobConfiguration.getJobRegions().add(jr);
                }
            }
        }
    }

    /**
     * The job is already queued, so a failure to announce it is logged rather than reported to the caller.
     */
    private void sendQueuedEvent(JobInstance job) {
        try {
            new ServletInjector<JobQueueEventSender>().getManagedBean(servletContext, JobQueueEventSender.class)
                    .jobQueued(job.getId());
        } catch (RuntimeException e) {
            LOGGER.warn("Job {} was queued but the queue event could not be sent: {}", job.getId(), e.toString());
        }
    }

    public static JobInstance addJobToQueue(Project project, CreateJobRequest request) {
        Workload workload = project.getWorkloads().getFirst();
        String projectName = request.getProjectName() != null ? request.getProjectName() : project.getName();
        JobInstanceFactory.Proposal proposal = JobInstanceFactory.propose(workload, projectName,
                StringUtils.trimToNull(request.getJobInstanceName()), RestAuthorization.currentUserName());
        JobInstance jobInstance = proposal.job();
        jobInstance.setJobDetails(JobDetailFormatter.createJobDetails(proposal.validator(), workload, jobInstance));
        clearLoadedScriptSteps(workload);
        new WorkloadDao().saveOrUpdate(workload);
        return JobInstanceFactory.queue(project.getId(), workload, jobInstance).job();
    }

    private static void clearLoadedScriptSteps(Workload workload) {
        workload.getTestPlans().stream()
                .flatMap(plan -> plan.getScriptGroups().stream())
                .flatMap(group -> group.getScriptGroupSteps().stream())
                .map(ScriptGroupStep::getScript)
                .filter(script -> script != null && script.getScriptSteps() != null)
                .forEach(script -> script.getScriptSteps().clear());
    }



}

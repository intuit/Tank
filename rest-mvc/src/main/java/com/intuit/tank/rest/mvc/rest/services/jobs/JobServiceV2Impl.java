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
import com.intuit.tank.project.TestPlan;
import com.intuit.tank.project.Workload;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceInternalServerException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobContainer;
import com.intuit.tank.rest.mvc.rest.models.jobs.JobTO;
import com.intuit.tank.rest.mvc.rest.models.jobs.CreateJobRequest;
import com.intuit.tank.rest.mvc.rest.models.jobs.CreateJobRegion;
import com.intuit.tank.rest.mvc.rest.util.*;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.util.TestParamUtil;
import com.intuit.tank.util.TestParameterContainer;
import com.intuit.tank.util.CreateDateComparator;
import com.intuit.tank.util.CreateDateComparator.SortOrder;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.api.enumerated.TerminationPolicy;
import com.intuit.tank.vm.common.util.ReportUtil;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.amazonaws.xray.AWSXRay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.*;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.servlet.ServletContext;

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
            LOGGER.error("Error returning job: " + e.getMessage(), e);
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
                jobs.sort(new CreateDateComparator(SortOrder.DESCENDING));
                List<JobTO> list = jobs.stream().map(JobServiceUtil::jobToTO).collect(Collectors.toList());
                return new JobContainer(list);
            } else {
                return null;
            }
        } catch (Exception e){
            LOGGER.error("Error returning jobs by project: " + e.getMessage(), e);
            throw new GenericServiceResourceNotFoundException("jobs", "jobs by project", e);
        }
    }

    @Override
    public JobContainer getAllJobs() {
        try {
            JobInstanceDao dao = new JobInstanceDao();
            List<JobInstance> jobs = dao.findAll();
            if (!jobs.isEmpty()) {
                jobs.sort(new CreateDateComparator(SortOrder.DESCENDING));
                List<JobTO> list = jobs.stream().map(JobServiceUtil::jobToTO).collect(Collectors.toList());
                return new JobContainer(list);
            } else {
                return null;
            }
        } catch (Exception e){
            LOGGER.error("Error returning all jobs: " + e.getMessage(), e);
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
                buildJobConfiguration(request, project);
                project = projectDao.saveOrUpdateProject(project);
                JobInstance job = addJobToQueue(project, request);
                response.put("JobId", Integer.toString(job.getId()));
                response.put("status", "created");
            }
        } catch (Exception e) {
            LOGGER.error("Error creating job: " + e.getMessage(), e);
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
                LOGGER.error("Error streaming job harness file: " + e.getMessage(), e);
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

    // Job Actions

    @Override
    public String startJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.startJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error starting job: " + e);
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to start", e);
        }
    }

    @Override
    public String stopJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.stopJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error stopping job: " + e);
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to stop", e);
        }
    }

    @Override
    public String pauseJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.pauseRampJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error pausing job: " + e);
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to pause", e);
        }
    }

    @Override
    public String resumeJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
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
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.killJob(Integer.toString(jobId));
            return getJobStatus(jobId);
        } catch (Exception e) {
            LOGGER.error("Error killing job: " + e);
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to terminate", e);
        }
    }

    @Override
    public String deleteJob(Integer jobId) {
        AWSXRay.getCurrentSegment().putAnnotation("jobId", jobId);
        try {
            JobEventSender controller = new ServletInjector<JobEventSender>().getManagedBean(servletContext,
                    JobEventSender.class);
            controller.deleteJob(Integer.toString(jobId));
            return getJobStatus(jobId).equals("Created") ? "Deleted" : "Cannot delete running job";
        } catch (Exception e) {
            LOGGER.error("Error deleting job: ", e);
            throw new GenericServiceCreateOrUpdateException("jobs", "job status to aborted", e);
        }
    }

    // Job Service Util

    public static void buildJobConfiguration(@Nonnull CreateJobRequest request, Project project) {
        JobConfiguration jobConfiguration = project.getWorkloads().get(0).getJobConfiguration();

        if (StringUtils.isNotEmpty(request.getRampTime())) {
            jobConfiguration.setRampTimeExpression(request.getRampTime());
        }

        if (StringUtils.isNotEmpty(request.getSimulationTime())) {
            jobConfiguration.setSimulationTimeExpression(request.getSimulationTime());
        }

        jobConfiguration.setUserIntervalIncrement(request.getUserIntervalIncrement());
        jobConfiguration.setStopBehavior(StringUtils.isNotEmpty(request.getStopBehavior())
                ? request.getStopBehavior() : StopBehavior.END_OF_SCRIPT_GROUP.name());

        jobConfiguration.setVmInstanceType(request.getVmInstance());
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
        jobConfiguration.getJobRegions().clear();
        JobRegionDao jrd = new JobRegionDao();
        for (CreateJobRegion r : request.getJobRegions()) {
            if (StringUtils.isNotEmpty(r.getRegion())) {
                JobRegion jr = jrd.saveOrUpdate(
                        new JobRegion(VMRegion.getRegionFromZone(r.getRegion()), r.getUsers()));
                jobConfiguration.getJobRegions().add(jr);
            }
        }
    }

    public static JobInstance addJobToQueue(Project project, CreateJobRequest request) {
        JobQueueDao jobQueueDao = new JobQueueDao();
        DataFileDao dataFileDao = new DataFileDao();
        JobNotificationDao jobNotificationDao = new JobNotificationDao();
        JobInstanceDao jobInstanceDao = new JobInstanceDao();

        Workload workload = project.getWorkloads().get(0);
        JobConfiguration jc = workload.getJobConfiguration();
        JobQueue queue = jobQueueDao.findOrCreateForProjectId(project.getId());
        JobInstance jobInstance = new JobInstance(workload, buildJobInstanceName(request, workload, project));
        jobInstance.setScheduledTime(new Date());
        jobInstance.setLocation(jc.getLocation());
        jobInstance.setLoggingProfile(jc.getLoggingProfile());
        jobInstance.setStopBehavior(jc.getStopBehavior());
        jobInstance.setVmInstanceType(jc.getVmInstanceType());
        jobInstance.setNumUsersPerAgent(jc.getNumUsersPerAgent());
        jobInstance.setReportingMode(jc.getReportingMode());
        jobInstance.getVariables().putAll(jc.getVariables());
        // set version info
        jobInstance.getDataFileVersions()
                .addAll(getVersions(dataFileDao, workload.getJobConfiguration().getDataFileIds(), DataFile.class));

        jobInstance.getNotificationVersions()
                .addAll(getVersions(jobNotificationDao, workload.getJobConfiguration().getNotifications()));
        JobValidator validator = new JobValidator(workload.getTestPlans(), jobInstance.getVariables(), false);
        long maxDuration = 0;
        for (TestPlan plan : workload.getTestPlans()) {
            maxDuration = Math.max(validator.getDurationMs(plan.getName()), maxDuration);
        }
        TestParameterContainer times = TestParamUtil.evaluateTestTimes(maxDuration, jc.getRampTimeExpression(),
                jc.getSimulationTimeExpression());
        jobInstance.setExecutionTime(maxDuration);
        jobInstance.setRampTime(times.getRampTime());
        jobInstance.setSimulationTime(times.getSimulationTime());
        int totalVirtualUsers = 0;
        for (JobRegion region : jc.getJobRegions()) {
            totalVirtualUsers += TestParamUtil.evaluateExpression(region.getUsers(), maxDuration,
                    times.getSimulationTime(), times.getRampTime());
            jobInstance.getJobRegionVersions().add(new EntityVersion(region.getId(), 0, JobRegion.class));
        }
        jobInstance.setTotalVirtualUsers(totalVirtualUsers);
        queue.addJob(jobInstance);
        workload = new WorkloadDao().saveOrUpdate(workload);
        String jobDetails = JobDetailFormatter.createJobDetails(
                new JobValidator(workload.getTestPlans(), jobInstance.getVariables(), false), workload, jobInstance);
        jobInstance.setJobDetails(jobDetails);
        jobInstance = jobInstanceDao.saveOrUpdate(jobInstance);
        jobQueueDao.saveOrUpdate(queue);

        ResponseUtil.storeScript(Integer.toString(jobInstance.getId()), workload, jobInstance);
        return jobInstance;
    }

    private static String buildJobInstanceName(CreateJobRequest request, Workload workload, Project project) {
        String projectName = request.getProjectName() == null ? project.getName() : request.getProjectName();
        return StringUtils.isNotEmpty(request.getJobInstanceName()) ? request.getJobInstanceName()
                : projectName + "_" + workload.getJobConfiguration().getTotalVirtualUsers() + "_users_"
                + ReportUtil.getTimestamp(new Date());
    }

    private static Set<EntityVersion> getVersions(BaseDao dao, Collection<Integer> dataFileIds,
                                                  Class<? extends BaseEntity> entityClass) {
        HashSet<EntityVersion> result = new HashSet<>();
        for (Integer id : dataFileIds) {
            int versionId = dao.getHeadRevisionNumber(id);
            result.add(new EntityVersion(id, versionId, entityClass));
        }
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Set<EntityVersion> getVersions(BaseDao dao, Set<? extends BaseEntity> entities) {
        HashSet<Integer> ids = new HashSet<>();
        Class entityClass = null;
        for (BaseEntity entity : entities) {
            ids.add(entity.getId());
            entityClass = entity.getClass();
        }
        return getVersions(dao, ids, entityClass);
    }
}

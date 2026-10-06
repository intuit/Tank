/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.jobs;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Project;
import com.intuit.tank.project.Workload;
import com.intuit.tank.reporting.api.ResultsReader;
import com.intuit.tank.reporting.factory.ReportingFactory;
import com.intuit.tank.rest.mvc.rest.cloud.JobEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.JobLockManager;
import com.intuit.tank.rest.mvc.rest.cloud.JobQueueEventSender;
import com.intuit.tank.rest.mvc.rest.cloud.ServletInjector;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceBadRequestException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceConflictException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.rest.mvc.rest.models.Failures;
import com.intuit.tank.rest.mvc.rest.models.JobActionResult;
import com.intuit.tank.rest.mvc.rest.models.JobDetails;
import com.intuit.tank.rest.mvc.rest.models.JobLaunchRequest;
import com.intuit.tank.rest.mvc.rest.models.JobPreview;
import com.intuit.tank.rest.mvc.rest.models.JobTree;
import com.intuit.tank.rest.mvc.rest.models.ProjectValidation;
import com.intuit.tank.rest.mvc.rest.models.QueuedJob;
import com.intuit.tank.rest.mvc.rest.models.Timeseries;
import com.intuit.tank.rest.mvc.rest.security.JobAuthorization;
import com.intuit.tank.rest.mvc.rest.security.RestAuthorization;
import com.intuit.tank.rest.mvc.rest.util.JobCharts;
import com.intuit.tank.rest.mvc.rest.util.JobDetailFormatter;
import com.intuit.tank.rest.mvc.rest.util.JobInstanceFactory;
import com.intuit.tank.rest.mvc.rest.util.JobTreeBuilder;
import com.intuit.tank.rest.mvc.rest.util.ProjectValidator;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.settings.AccessRight;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import jakarta.servlet.ServletContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class JobQueueServiceV2Impl implements JobQueueServiceV2 {

    private static final Logger LOGGER = LogManager.getLogger(JobQueueServiceV2Impl.class);
    private static final String SERVICE = "jobs";
    static final int MAX_NAME_LENGTH = 255;

    @Autowired
    private ServletContext servletContext;

    @Override
    public JobPreview previewJob(Integer projectId, JobLaunchRequest request) {
        String creator = RestAuthorization.requireUser(SERVICE).getName();
        Project project = findProject(projectId);
        Checked checked = check(project, request, creator);
        JobInstance job = checked.proposal.job();
        return new JobPreview(job.getName(), checked.errors.isEmpty(), checked.errors, checked.validation.warnings(),
                job.getTotalVirtualUsers(), job.getRampTime(), job.getSimulationTime(),
                job.getExecutionTime() != null ? job.getExecutionTime() : 0, checked.details);
    }

    @Override
    public QueuedJob queueJob(Integer projectId, JobLaunchRequest request) {
        String creator = RestAuthorization.requireUser(SERVICE).getName();
        Project project = findProject(projectId);
        RestAuthorization.requireRightOrOwner(AccessRight.CONTROL_JOB, project, SERVICE);
        Checked checked = check(project, request, creator);
        if (!checked.errors.isEmpty()) {
            throw new GenericServiceBadRequestException(SERVICE, "job", String.join("; ", checked.errors));
        }
        JobInstance job = checked.proposal.job();
        job.setJobDetails(checked.details);
        JobInstanceFactory.Queued queued;
        try {
            queued = JobInstanceFactory.queue(project.getId(), project.getWorkloads().get(0), job);
        } catch (RuntimeException e) {
            LOGGER.error("Error queueing job for project {}: {}", projectId, e.getMessage(), e);
            throw new GenericServiceCreateOrUpdateException(SERVICE, "job", e);
        }
        bean(JobQueueEventSender.class).jobQueued(queued.job().getId());
        LOGGER.info("{} queued job {} ({}) for project {}", creator, queued.job().getId(), queued.job().getName(), projectId);
        return new QueuedJob(queued.job().getId(), queued.job().getName(), queued.job().getStatus().name());
    }

    @Override
    public JobTree getJobTree(Integer projectId, boolean includeFinished) {
        RestAuthorization.requireUser(SERVICE);
        JobTreeBuilder builder = new JobTreeBuilder(bean(VMTracker.class), AgentWsCommandSender.getStaticInstance(),
                project -> RestAuthorization.hasRight(AccessRight.CONTROL_JOB)
                        || (project != null && RestAuthorization.isOwner(project)),
                new JobQueueDao(), new ProjectDao());
        return builder.build(projectId, includeFinished, new Date());
    }

    @Override
    public JobActionResult controlJob(Integer jobId, String actionName) {
        RestAuthorization.requireUser(SERVICE);
        JobAction action = parseAction(actionName);
        JobInstance job = findJob(jobId);
        JobAuthorization.requireJobControl(jobId);
        String status = job.getStatus().name();
        JobTree.Actions allowed = JobTreeBuilder.jobActions(status, job.isUseTwoStep(), true);
        if (!isAllowed(action, allowed)) {
            throw new GenericServiceConflictException(SERVICE, "Job " + jobId + " is " + status + "; cannot " + action.getPath());
        }
        JobEventSender sender = bean(JobEventSender.class);
        String id = Integer.toString(jobId);
        switch (action) {
            case START -> sender.startJob(id);
            case START_LOAD -> sender.startAgents(id);
            case PAUSE -> sender.pauseJob(id);
            case RESUME -> sender.restartJob(id);
            case PAUSE_RAMP -> sender.pauseRampJob(id);
            case RESUME_RAMP -> sender.resumeRampJob(id);
            case STOP -> sender.stopJob(id);
            case KILL -> sender.killJob(id);
        }
        LOGGER.info("{} sent {} to job {}", RestAuthorization.currentUserName(), action.getPath(), jobId);
        JobInstance after = new JobInstanceDao().findById(jobId);
        return new JobActionResult(id, action.getPath(), after != null ? after.getStatus().name() : status);
    }

    @Override
    public JobActionResult controlAgent(String instanceId, String actionName) {
        RestAuthorization.requireUser(SERVICE);
        JobAction action = parseAction(actionName);
        if (!action.isForAgents()) {
            throw new GenericServiceBadRequestException(SERVICE, "action", action.getPath() + " applies to whole jobs only");
        }
        VMTracker tracker = bean(VMTracker.class);
        CloudVmStatus vm = StringUtils.isBlank(instanceId) ? null : tracker.getStatus(instanceId);
        if (vm == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "agent " + instanceId, null);
        }
        JobAuthorization.requireJobControl(vm.getJobId());
        String vmStatus = vm.getVmStatus() != null ? vm.getVmStatus().name() : "";
        String jobStatus = vm.getJobStatus() != null ? vm.getJobStatus().name() : "";
        if (!isAllowed(action, JobTreeBuilder.agentActions(vmStatus, jobStatus, true))) {
            throw new GenericServiceConflictException(SERVICE,
                    "Agent " + instanceId + " is " + vmStatus + " (" + jobStatus + "); cannot " + action.getPath());
        }
        JobEventSender sender = bean(JobEventSender.class);
        switch (action) {
            case PAUSE -> sender.pauseAgent(instanceId);
            case RESUME -> sender.restartAgent(instanceId);
            case PAUSE_RAMP -> sender.pauseRampInstance(instanceId);
            case RESUME_RAMP -> sender.resumeRampInstance(instanceId);
            case STOP -> sender.stopAgent(instanceId);
            case KILL -> sender.killInstance(instanceId);
            default -> throw new IllegalStateException(action.name());
        }
        LOGGER.info("{} sent {} to agent {} of job {}", RestAuthorization.currentUserName(), action.getPath(),
                instanceId, vm.getJobId());
        CloudVmStatus after = tracker.getStatus(instanceId);
        return new JobActionResult(instanceId, action.getPath(),
                after != null && after.getVmStatus() != null ? after.getVmStatus().name() : vmStatus);
    }

    @Override
    public void deleteJob(Integer jobId) {
        RestAuthorization.requireUser(SERVICE);
        findJob(jobId);
        JobAuthorization.requireJobControl(jobId);
        synchronized (JobLockManager.getLock(Integer.toString(jobId))) {
            JobInstanceDao dao = new JobInstanceDao();
            JobInstance job = dao.findById(jobId);
            if (job == null) {
                throw new GenericServiceResourceNotFoundException(SERVICE, "job " + jobId, null);
            }
            if (job.getStatus() != JobQueueStatus.Created) {
                throw new GenericServiceConflictException(SERVICE,
                        "Job " + jobId + " is " + job.getStatus() + "; only jobs that have not started can be deleted");
            }
            job.setStatus(JobQueueStatus.Deleted);
            dao.saveOrUpdate(job);
        }
        LOGGER.info("{} deleted job {}", RestAuthorization.currentUserName(), jobId);
    }

    @Override
    public JobDetails getJobDetails(Integer jobId) {
        RestAuthorization.requireUser(SERVICE);
        JobInstance job = findJob(jobId);
        CloudVmStatusContainer container = bean(VMTracker.class).getVmStatusForJob(Integer.toString(jobId));
        Set<CloudVmStatus> agents = container != null ? container.getStatuses() : Set.of();
        int activeUsers = agents.stream().mapToInt(CloudVmStatus::getCurrentUsers).sum();
        Failures failures = agents.stream().map(vm -> Failures.of(vm.getValidationFailures()))
                .reduce(Failures.NONE, Failures::plus);
        return new JobDetails(job.getId(), job.getName(), job.getStatus().name(), job.getCreator(), job.getCreated(),
                job.getStartTime(), job.getEndTime(), activeUsers, job.getTotalVirtualUsers(), failures,
                job.getJobDetails());
    }

    @Override
    public Timeseries getUserSeries(Integer jobId) {
        RestAuthorization.requireUser(SERVICE);
        findJob(jobId);
        CloudVmStatusContainer container = bean(VMTracker.class).getVmStatusForJob(Integer.toString(jobId));
        return JobCharts.users(container != null ? container.getDetailMap() : null);
    }

    @Override
    public Timeseries getTpsSeries(Integer jobId, String instanceId, Long sinceMs) {
        RestAuthorization.requireUser(SERVICE);
        findJob(jobId);
        ResultsReader reader = ReportingFactory.getResultsReader();
        if (reader == null) {
            LOGGER.warn("No results reader is configured; returning no TPS data for job {}", jobId);
            return JobCharts.tps(null);
        }
        Date since = sinceMs != null ? new Date(sinceMs) : null;
        String id = Integer.toString(jobId);
        return JobCharts.tps(StringUtils.isNotBlank(instanceId)
                ? reader.getTpsMapForInstance(since, id, instanceId)
                : reader.getTpsMapForJob(since, id));
    }

    private record Checked(JobInstanceFactory.Proposal proposal, ProjectValidation validation, List<String> errors,
                           String details) {
    }

    /**
     * Builds the job the project would queue now and every reason it could not be queued.
     */
    private Checked check(Project project, JobLaunchRequest request, String creator) {
        String name = request != null ? StringUtils.trimToNull(request.name()) : null;
        if (request != null && request.name() != null && name == null) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name must not be blank");
        }
        if (name != null && name.length() > MAX_NAME_LENGTH) {
            throw new GenericServiceBadRequestException(SERVICE, "name", "name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        Workload workload = project.getWorkloads().get(0);
        JobInstanceFactory.Proposal proposal;
        try {
            proposal = JobInstanceFactory.propose(workload, project.getName(), name, creator);
        } catch (IllegalArgumentException e) {
            throw new GenericServiceBadRequestException(SERVICE, "project",
                    "the project's times or users cannot be evaluated: " + e.getMessage());
        }
        ProjectValidation validation = ProjectValidator.validate(project, proposal.validator());
        List<String> errors = new ArrayList<>(validation.errors());
        String details = JobDetailFormatter.createJobDetails(proposal.validator(), workload, proposal.job());
        return new Checked(proposal, validation, errors, details);
    }

    private static boolean isAllowed(JobAction action, JobTree.Actions allowed) {
        return switch (action) {
            case START -> allowed.start();
            case START_LOAD -> allowed.startLoad();
            case PAUSE -> allowed.pause();
            case RESUME -> allowed.resume();
            case PAUSE_RAMP -> allowed.pauseRamp();
            case RESUME_RAMP -> allowed.resumeRamp();
            case STOP -> allowed.stop();
            case KILL -> allowed.kill();
        };
    }

    private static JobAction parseAction(String name) {
        return JobAction.fromPath(name).orElseThrow(() -> new GenericServiceBadRequestException(SERVICE, "action",
                "unknown action " + name + "; use start, start-load, pause, resume, pause-ramp, resume-ramp, stop or kill"));
    }

    private static Project findProject(Integer projectId) {
        Project project = projectId != null ? new ProjectDao().findByIdEager(projectId) : null;
        if (project == null) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "project " + projectId, null);
        }
        return project;
    }

    private static JobInstance findJob(Integer jobId) {
        JobInstance job = jobId != null ? new JobInstanceDao().findById(jobId) : null;
        if (job == null || job.getStatus() == JobQueueStatus.Deleted) {
            throw new GenericServiceResourceNotFoundException(SERVICE, "job " + jobId, null);
        }
        return job;
    }

    private <T> T bean(Class<T> type) {
        return new ServletInjector<T>().getManagedBean(servletContext, type);
    }
}

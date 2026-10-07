/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.dao.JobQueueDao;
import com.intuit.tank.dao.ProjectDao;
import com.intuit.tank.job.JobStatusHelper;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobQueue;
import com.intuit.tank.project.Project;
import com.intuit.tank.rest.mvc.rest.models.Failures;
import com.intuit.tank.rest.mvc.rest.models.JobTree;
import com.intuit.tank.rest.mvc.rest.models.JobTree.Actions;
import com.intuit.tank.rest.mvc.rest.models.JobTree.AgentNode;
import com.intuit.tank.rest.mvc.rest.models.JobTree.JobNode;
import com.intuit.tank.rest.mvc.rest.models.JobTree.ProjectJobs;
import com.intuit.tank.vm.agent.messages.AgentWsCommandSender;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Builds the job tree the web UI's job queue pages show: each project's recent jobs with their agents'
 * live status from the {@link VMTracker}, plus jobs that agents report but no queue lists.
 */
public class JobTreeBuilder {

    /** How far back the all-projects view looks for queues with activity. */
    static final long RECENT_MS = 7L * 24 * 60 * 60 * 1000;

    private final VMTracker tracker;
    private final AgentWsCommandSender wsSender;
    private final Predicate<Project> canControl;
    private final JobQueueDao queueDao;
    private final ProjectDao projectDao;

    /**
     * @param wsSender   the agent WebSocket sender, or null when none is running
     * @param canControl whether the caller may control jobs of a project; null for jobs with no project
     */
    public JobTreeBuilder(VMTracker tracker, AgentWsCommandSender wsSender, Predicate<Project> canControl,
                          JobQueueDao queueDao, ProjectDao projectDao) {
        this.tracker = tracker;
        this.wsSender = wsSender;
        this.canControl = canControl;
        this.queueDao = queueDao;
        this.projectDao = projectDao;
    }

    /**
     * @param projectId       only this project's jobs, or null for every project with recent activity
     * @param includeFinished whether to include jobs that have ended
     */
    public JobTree build(Integer projectId, boolean includeFinished, Date now) {
        List<JobQueue> queues = projectId != null
                ? queueDao.getForProjectIds(List.of(projectId))
                : queueDao.findRecent(new Date(now.getTime() - RECENT_MS));
        Map<Integer, Project> projects = projectsById(queues);
        Set<String> untracked = tracker.getAllJobs().stream()
                .map(CloudVmStatusContainer::getJobId)
                .collect(Collectors.toCollection(HashSet::new));

        Map<Integer, ProjectBuilder> byProject = new LinkedHashMap<>();
        for (JobQueue queue : queues) {
            Project project = projects.get(queue.getProjectId());
            if (project == null) {
                continue;
            }
            boolean control = canControl.test(project);
            ProjectBuilder node = byProject.computeIfAbsent(project.getId(), id -> new ProjectBuilder(project));
            queue.getJobs().stream()
                    .sorted(Comparator.comparingInt(JobInstance::getId).reversed())
                    .forEach(job -> {
                        untracked.remove(Integer.toString(job.getId()));
                        if (job.getStatus() == JobQueueStatus.Deleted || (!includeFinished && job.getEndTime() != null)) {
                            return;
                        }
                        node.add(jobNode(job, control));
                    });
        }

        List<JobNode> otherJobs = new ArrayList<>();
        if (projectId == null) {
            for (String jobId : untracked.stream().sorted().toList()) {
                JobQueue queue = NumberUtils.isDigits(jobId)
                        ? queueDao.findForJobId(Integer.valueOf(jobId)) : null;
                ProjectBuilder node = queue != null ? byProject.get(queue.getProjectId()) : null;
                Project project = node != null ? node.project : null;
                JobNode adhoc = adhocJobNode(jobId, tracker.getVmStatusForJob(jobId), canControl.test(project));
                if (node != null) {
                    node.add(adhoc);
                } else {
                    otherJobs.add(adhoc);
                }
            }
        }

        List<ProjectJobs> result = byProject.values().stream()
                .filter(p -> !p.jobs.isEmpty())
                .map(ProjectBuilder::build)
                .collect(Collectors.toList());
        return new JobTree(result, otherJobs, now);
    }

    private Map<Integer, Project> projectsById(List<JobQueue> queues) {
        List<Integer> ids = queues.stream().map(JobQueue::getProjectId).distinct().collect(Collectors.toList());
        if (ids.isEmpty()) {
            return Map.of();
        }
        return projectDao.findForIds(ids).stream().collect(Collectors.toMap(Project::getId, p -> p, (a, b) -> a));
    }

    private JobNode jobNode(JobInstance job, boolean control) {
        String status = job.getStatus().name();
        CloudVmStatusContainer container = tracker.getVmStatusForJob(Integer.toString(job.getId()));
        List<AgentNode> agents = agents(container, control);
        return new JobNode(Integer.toString(job.getId()), job.getName(), status, job.getIncrementStrategy().getDisplay(),
                agents.stream().mapToInt(AgentNode::activeUsers).sum(),
                job.getTotalVirtualUsers(),
                agents.stream().mapToInt(AgentNode::tps).sum(),
                agents.stream().map(AgentNode::failures).reduce(Failures.NONE, Failures::plus),
                job.getStartTime(), job.getEndTime(), job.isUseTwoStep(),
                jobActions(status, job.isUseTwoStep(), control), agents);
    }

    private JobNode adhocJobNode(String jobId, CloudVmStatusContainer container, boolean control) {
        String status = container != null && container.getStatus() != null ? container.getStatus().name() : "";
        List<AgentNode> agents = agents(container, control);
        return new JobNode(jobId, jobId, status, null,
                agents.stream().mapToInt(AgentNode::activeUsers).sum(),
                agents.stream().mapToInt(AgentNode::totalUsers).sum(),
                agents.stream().mapToInt(AgentNode::tps).sum(),
                agents.stream().map(AgentNode::failures).reduce(Failures.NONE, Failures::plus),
                container != null ? container.getStartTime() : null, container != null ? container.getEndTime() : null,
                false, jobActions(status, false, control), agents);
    }

    private List<AgentNode> agents(CloudVmStatusContainer container, boolean control) {
        if (container == null) {
            return List.of();
        }
        return container.getStatuses().stream()
                .sorted(Comparator.comparing(CloudVmStatus::getInstanceId, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(vm -> agentNode(vm, control))
                .collect(Collectors.toList());
    }

    private AgentNode agentNode(CloudVmStatus vm, boolean control) {
        String status = vm.getVmStatus() != null ? vm.getVmStatus().name() : "";
        String jobStatus = vm.getJobStatus() != null ? vm.getJobStatus().name() : "";
        String instanceId = vm.getInstanceId();
        return new AgentNode(instanceId, vm.getVmRegion() != null ? vm.getVmRegion().name() : null, status, jobStatus,
                vm.getCurrentUsers(), vm.getTotalUsers(), vm.getTotalTps(), Failures.of(vm.getValidationFailures()),
                vm.getStartTime(), vm.getEndTime(),
                wsSender != null ? wsSender.getWsState(instanceId) : null,
                wsSender != null ? wsSender.getTransferProgress(instanceId) : null,
                wsSender != null ? wsSender.getLastSeen(instanceId) : null,
                agentActions(status, jobStatus, control));
    }

    /**
     * The job actions the web UI's job tree offers for a status.
     */
    public static Actions jobActions(String status, boolean useTwoStep, boolean control) {
        if (!control || status == null || status.isEmpty()) {
            return new Actions(control, false, false, false, false, false, false, false, false, false);
        }
        return new Actions(true,
                status.equals(JobQueueStatus.Created.name()) || status.equals(JobQueueStatus.Queued.name()),
                useTwoStep && JobStatusHelper.canStartLoad(status),
                JobStatusHelper.canBePaused(status),
                status.equals(JobQueueStatus.Paused.name()),
                JobStatusHelper.canRampBePaused(status),
                status.equals(JobQueueStatus.RampPaused.name()),
                JobStatusHelper.canBeStopped(status),
                JobStatusHelper.canBeKilled(status),
                JobStatusHelper.canBeDeleted(status));
    }

    /**
     * The actions for one agent. A paused agent's VM keeps running, so pause and resume follow its job status.
     */
    public static Actions agentActions(String vmStatus, String jobStatus, boolean control) {
        if (!control || vmStatus == null || vmStatus.isEmpty()) {
            return new Actions(control, false, false, false, false, false, false, false, false, false);
        }
        boolean paused = JobStatus.Paused.name().equals(jobStatus);
        return new Actions(true, false, false,
                !paused && JobStatusHelper.canBePaused(vmStatus),
                paused,
                JobStatusHelper.canRampBePaused(vmStatus),
                vmStatus.equals(VMStatus.rampPaused.name()),
                JobStatusHelper.canBeStopped(vmStatus),
                JobStatusHelper.canBeKilled(vmStatus),
                false);
    }

    private static final class ProjectBuilder {
        private final Project project;
        private final List<JobNode> jobs = new ArrayList<>();

        ProjectBuilder(Project project) {
            this.project = project;
        }

        void add(JobNode job) {
            jobs.add(job);
        }

        ProjectJobs build() {
            return new ProjectJobs(project.getId(), project.getName(),
                    jobs.stream().mapToInt(JobNode::activeUsers).sum(),
                    jobs.stream().flatMap(j -> j.agents().stream()).mapToInt(AgentNode::totalUsers).sum(),
                    jobs.stream().mapToInt(JobNode::tps).sum(),
                    jobs.stream().map(JobNode::failures).reduce(Failures.NONE, Failures::plus),
                    jobs);
        }
    }
}

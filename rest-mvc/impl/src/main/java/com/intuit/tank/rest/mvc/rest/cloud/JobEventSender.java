/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.cloud;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.WorkloadDao;
import com.intuit.tank.dao.util.ProjectDaoUtil;
import com.intuit.tank.harness.data.HDWorkload;
import com.intuit.tank.perfManager.workLoads.JobManager;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.Workload;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import com.intuit.tank.transform.scriptGenerator.ConverterUtil;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.perfManager.AgentChannel;
import com.intuit.tank.vm.settings.TankConfig;
import com.intuit.tank.vm.vmManager.VMTerminator;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vmManager.environment.amazon.AmazonInstance;

import com.amazonaws.xray.AWSXRay;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

@Named
@RequestScoped
public class JobEventSender {

    private static final Logger LOG = LogManager.getLogger(JobEventSender.class);

    @Inject
    private VMTracker vmTracker;

    @Inject
    private VMTerminator terminator;

    @Inject
    private JobManager jobManager;

    @Inject
    private AgentChannel agentChannel;

    @Inject
    private Event<JobEvent> jobEventProducer;

    public String startJob(String jobId) {
        AWSXRay.beginSubsegment("Start.Job." + jobId);
        JobInstanceDao jobInstanceDao = new JobInstanceDao();
        JobInstance job = jobInstanceDao.findById(Integer.valueOf(jobId));
        synchronized (jobId) {
            if (job.getStatus() == JobQueueStatus.Created) {// only start if new job to init agents
                // save the job
                job.setStatus(JobQueueStatus.Starting);
                jobInstanceDao.saveOrUpdate(job);

                ProjectDaoUtil.storeScriptFile(jobId, getScriptString(job));

                vmTracker.removeStatusForJob(jobId);
                jobManager.startJob(job.getId());
                jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.AGENT_LAUNCHED));
            }
        }
        AWSXRay.endSubsegment();
        return jobId;
    }

    public String startAgents(String jobId) {
        AWSXRay.beginSubsegment("Start.Agents." + jobId);
        JobInstanceDao jobInstanceDao = new JobInstanceDao();
        JobInstance job = jobInstanceDao.findById(Integer.valueOf(jobId));
        synchronized (jobId) {
            if (job.getStatus() == JobQueueStatus.Starting) {// only start if agents initialized
                jobManager.startAgents(jobId);
            }
            jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.JOB_STARTED));
        }
        AWSXRay.endSubsegment();
        return jobId;
    }

    /**
     * Use the AWS SDK to terminate instances.
     * If no instances can be found, set jobStatus to Completed
     */
    public void killJob(String jobId, boolean fireEvent) {
        List<String> instanceIds = getInstancesForJob(jobId);
        vmTracker.stopJob(jobId);
        if (instanceIds.isEmpty()) {
            JobInstanceDao dao = new JobInstanceDao();
            JobInstance job = dao.findById(Integer.parseInt(jobId));
            if (job != null) {
                job.setStatus(JobQueueStatus.Completed);
                job.setEndTime(new Date());
                dao.saveOrUpdate(job);
            }
        } else {
            killInstances(instanceIds);
        }

        if (fireEvent) {
            jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.JOB_KILLED));
        }
    }

    public void killJob(String jobId) {
        killJob(jobId, true);
    }

    public Set<CloudVmStatusContainer> killAllJobs() {
    	Set<CloudVmStatusContainer> jobs = vmTracker.getAllJobs();
        for (CloudVmStatusContainer job : jobs) {
            String jobId = job.getJobId();
            killJob(jobId, true);
        }
    	return jobs;
    }

    public void killInstance(String instanceId) { killInstances(Collections.singletonList(instanceId)); }

    public void killInstances(List<String> instanceIds) {
        agentChannel.killAgents(instanceIds);

        if (!vmTracker.isDevMode()) {
            for (VMRegion region : new TankConfig().getVmManagerConfig().getRegions()) {
                AmazonInstance amzInstance = new AmazonInstance(region);
                amzInstance.killInstances(instanceIds);
            }
        }
        String jobId = null;
        for (String instanceId : instanceIds) {
            CloudVmStatus status = new CloudVmStatus(vmTracker.getStatus(instanceId));
            status.setCurrentUsers(0);
            status.setEndTime(new Date());
            status.setJobStatus(JobStatus.Completed);
            status.setVmStatus(VMStatus.terminated);
            vmTracker.setStatus(status);
            jobId = status.getJobId();
        }
        if (jobId != null) {
            checkJobStatus(jobId);
        }
    }

    public Set<CloudVmStatusContainer> stopAllJobs() {
    	Set<CloudVmStatusContainer> jobs = vmTracker.getAllJobs();
        for (CloudVmStatusContainer job : jobs) {
            String jobId = (job).getJobId();
            List<String> instanceIds = getInstancesForJob(jobId);
            vmTracker.stopJob(jobId);
            stopAgents(instanceIds);
            jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.JOB_STOPPED));
        }
    	return jobs;
    }

    public void stopJob(String jobId) {
        List<String> instanceIds = getInstancesForJob(jobId);
        vmTracker.stopJob(jobId);
        stopAgents(instanceIds);
        jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.JOB_STOPPED));
    }

    public void stopAgent(String instanceId) {
        stopAgents(Collections.singletonList(instanceId));

    }

    public void stopAgents(List<String> instanceIds) {
        agentChannel.stopAgents(instanceIds);
    }

    public void pauseJob(String jobId) {
        List<String> instanceIds = getInstancesForJob(jobId);
        pauseAgents(instanceIds);
        jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.JOB_PAUSED));
    }

    public void pauseAgent(String instanceId) {
        pauseAgents(Collections.singletonList(instanceId));
    }

    public void pauseAgents(List<String> instanceIds) {
        agentChannel.pauseAgents(instanceIds);
    }

    public void restartJob(String jobId) {
        List<String> instanceIds = getInstancesForJob(jobId);
        restartAgents(instanceIds);
        jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.JOB_RESUMED));
    }

    public void restartAgent(String instanceId) {
        restartAgents(Collections.singletonList(instanceId));
    }

    public void restartAgents(List<String> instanceIds) {
        agentChannel.restartAgents(instanceIds);
    }

    public void pauseRampInstance(String instanceId) {
        pauseRampInstances(Collections.singletonList(instanceId));
    }

    public void pauseRampJob(String jobId) {
        List<String> instanceIds = getInstancesForJob(jobId);
        pauseRampInstances(instanceIds);
        jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.RAMP_PAUSED));
    }

    public void pauseRampInstances(List<String> instanceIds) {
        agentChannel.pauseRamp(instanceIds);
    }

    public void resumeRampInstance(String instanceId) {
        resumeRampInstances(Collections.singletonList(instanceId));
    }

    public void resumeRampJob(String jobId) {
        List<String> instanceIds = getInstancesForJob(jobId);
        resumeRampInstances(instanceIds);
        jobEventProducer.fire(new JobEvent(jobId, "", JobLifecycleEvent.RAMP_RESUMED));
    }

    public void resumeRampInstances(List<String> instanceIds) {
        agentChannel.resumeRamp(instanceIds);
    }

    private List<String> getInstancesForJob(String jobId) {
        List<String> instanceIds = new ArrayList<String>();
        CloudVmStatusContainer statuses = vmTracker.getVmStatusForJob(jobId);
        if (statuses != null) {
            instanceIds = statuses.getStatuses().stream().map(CloudVmStatus::getInstanceId).collect(Collectors.toList());
            String commaSeparatedIds = statuses.getStatuses().stream()
                    .map(CloudVmStatus::getInstanceId)
                    .collect(Collectors.joining(","));
            LOG.info("JobEventSender getInstancesForJob yields {} for jobId {}", commaSeparatedIds, jobId);
        }
        return instanceIds;
    }

    // Agent/VM Status Methods

    public CloudVmStatus getVmStatus(String instanceId) {
        return vmTracker.getStatus(instanceId);
    }

    public void setVmStatus(final String instanceId, final CloudVmStatus status) {
        vmTracker.setStatus(status);
        if (status.getJobStatus() == JobStatus.Completed || status.getVmStatus() == VMStatus.terminated) {
            // will terrminate instance after waiting for some cleanup time
            terminator.terminate(status.getInstanceId());
            // check job status and kill off instances appropriately
            checkJobStatus(status.getJobId());
        }
    }

    public CloudVmStatusContainer getVmStatusForJob(String jobId) {
        return vmTracker.getVmStatusForJob(jobId);
    }

    public void checkJobStatus(String jobId) {
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(jobId);
        LOG.info("Checking Job Status to see if we can kill reporting instances. Container=" + container);
        if (container != null) {
            if (container.getEndTime() != null) {
                JobInstanceDao dao = new JobInstanceDao();
                // hack to see if this is an automatino job

                // set the status of the JobInstance to finished.
                JobInstance finishedJob = dao.findById(Integer.valueOf(jobId));
                if (finishedJob.getEndTime() == null) {
                    finishedJob.setEndTime(new Date());
                    finishedJob.setStatus(JobQueueStatus.Completed);
                    dao.saveOrUpdate(finishedJob);
                }
                List<JobQueueStatus> statuses = Arrays.asList(JobQueueStatus.Running,JobQueueStatus.Starting );
                List<JobInstance> instances = dao.getForStatus(statuses);
                LOG.info("Checking Job Status to see if we can kill reporting instances. found running instances: "
                        + instances.size());
                boolean killModal = true;
                boolean killNonRegional = true;

                for (JobInstance job : instances) {
                    CloudVmStatusContainer statusForJob = vmTracker.getVmStatusForJob(Integer.toString(job.getId()));
                    if (!jobId.equals(Integer.toString(job.getId())) && statusForJob != null
                            && statusForJob.getEndTime() == null) {
                        LOG.info("Found another job that is not finished: " + job);
                    }
                }
                if (killNonRegional || killModal) {
                    for (CloudVmStatusContainer statusForJob : vmTracker.getAllJobs()) {
                        if (statusForJob.getEndTime() == null && !NumberUtils.isCreatable(statusForJob.getJobId())) {
                            killNonRegional = false;
                            killModal = false;
                            LOG.info("Cannot kill Reporting instances because of automation job id: "
                                    + statusForJob.getJobId());
                        }
                    }
                }
            } else {
                LOG.info("Container does not have end time set so cannot kill reporting instances.");
            }
        }

    }

    public static String getScriptString(JobInstance job) {
        WorkloadDao dao = new WorkloadDao();
        Workload workload = dao.findById(job.getWorkloadId());
        workload.getTestPlans();
        dao.loadScriptsForWorkload(workload);
        HDWorkload hdWorkload = ConverterUtil.convertWorkload(workload, job);
        return ConverterUtil.getWorkloadXML(hdWorkload);
    }
}

package com.intuit.tank.rest.mvc;

import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.vmManager.VMTerminator;
import com.intuit.tank.vm.vmManager.VMTracker;
import com.intuit.tank.vm.vmManager.models.CloudVmStatus;
import com.intuit.tank.vm.vmManager.models.CloudVmStatusContainer;
import com.intuit.tank.vm.vmManager.models.VMStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ApplicationScoped
public class AgentStatusLifecycle {

    private static final Logger LOG = LogManager.getLogger(AgentStatusLifecycle.class);

    @Inject
    private VMTracker vmTracker;

    @Inject
    private VMTerminator terminator;

    public void setVmStatus(final String instanceId, final CloudVmStatus status) {
        status.setInstanceId(instanceId);
        vmTracker.setStatus(status);
        if (isTerminalStatus(status)) {
            terminator.terminate(status.getInstanceId());
            checkJobStatus(status.getJobId());
        }
    }

    private boolean isTerminalStatus(CloudVmStatus status) {
        return status.getJobStatus() == JobStatus.Completed
                || status.getVmStatus() == VMStatus.terminated
                || status.getVmStatus() == VMStatus.replaced;
    }

    public void checkJobStatus(String jobId) {
        CloudVmStatusContainer container = vmTracker.getVmStatusForJob(jobId);
        LOG.info("Checking Job Status to see if we can kill reporting instances. Container=" + container);
        if (container != null) {
            if (container.getEndTime() != null) {
                JobInstanceDao dao = new JobInstanceDao();

                JobInstance finishedJob = dao.findById(Integer.valueOf(jobId));
                if (finishedJob != null && finishedJob.getEndTime() == null
                        && finishedJob.getStatus() != JobQueueStatus.Deleted) {
                    finishedJob.setEndTime(new Date());
                    finishedJob.setStatus(JobQueueStatus.Completed);
                    dao.saveOrUpdate(finishedJob);
                }
                List<JobQueueStatus> statuses = Arrays.asList(JobQueueStatus.Running, JobQueueStatus.Starting);
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
}

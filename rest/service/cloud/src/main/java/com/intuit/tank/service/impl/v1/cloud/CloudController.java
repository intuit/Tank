/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.service.impl.v1.cloud;

/*
 * #%L
 * Cloud Rest Service
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.api.model.v1.cloud.VMStatus;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.mail.MailService;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.vm.api.enumerated.JobQueueStatus;
import com.intuit.tank.vm.api.enumerated.JobStatus;
import com.intuit.tank.vm.vmManager.VMChannel;
import com.intuit.tank.vm.vmManager.VMTerminator;

/**
 * CloudServiceV1
 * 
 * @author dangleton
 * 
 */
public class CloudController {

    private static final Logger LOG = LogManager.getLogger(CloudController.class);

    @Inject
    private VMTerminator terminator;

    @Inject
    private VMTracker vmTracker;

    @Inject
    private MailService mailService;

    @Inject
    private VMChannel channel;

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(10, 50, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<Runnable>(50), Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    /**
     * @inheritDoc
     */
    public CloudVmStatus getVmStatus(String instanceId) {
        return vmTracker.getStatus(instanceId);
    }

    /**
     * @inheritDoc
     */
    public void setVmStatus(final String instanceId, final CloudVmStatus status) {

        Runnable task = () -> {
            vmTracker.setStatus(status);
            if (status.getJobStatus() == JobStatus.Completed || status.getVmStatus() == VMStatus.terminated) {
                // will terrminate instance after waiting for some cleanup time
                terminator.terminate(status.getInstanceId());
                // check job status and kill off instances appropriately
                checkJobStatus(status.getJobId(), mailService);
            }

        };
        if (status.getJobStatus() == JobStatus.Completed || status.getVmStatus() == VMStatus.terminated) {
            Thread t = new Thread(task);
            t.setDaemon(true);
            t.start();
        } else {
            EXECUTOR.execute(task);
        }

    }

    /**
     * @inheritDoc
     */
    public CloudVmStatusContainer getVmStatusForJob(String jobId) {
        return vmTracker.getVmStatusForJob(jobId);
    }

    public void checkJobStatus(String jobId) {
        checkJobStatus(jobId, mailService);
    }

    /**
     * @param jobId
     * @param mailService
     */
    private void checkJobStatus(String jobId, MailService mailService) {
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
                LOG.info("Container does not have end time set so cannot kill reporting instaces.");
            }
        }

    }

}

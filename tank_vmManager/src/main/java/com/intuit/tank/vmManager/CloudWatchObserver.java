package com.intuit.tank.vmManager;

/*
 * #%L
 * VmManager
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.api.cloud.VMTracker;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.api.model.v1.cloud.CloudVmStatusContainer;
import com.intuit.tank.dao.JobInstanceDao;
import com.intuit.tank.dao.JobNotificationDao;
import com.intuit.tank.project.EntityVersion;
import com.intuit.tank.project.JobInstance;
import com.intuit.tank.project.JobNotification;
import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.api.enumerated.RecipientType;
import com.intuit.tank.vm.api.enumerated.VMRegion;
import com.intuit.tank.vm.event.JobEvent;
import com.intuit.tank.vm.vmManager.Recipient;
import com.intuit.tank.vmManager.environment.amazon.CloudwatchInstance;

@ApplicationScoped
public class CloudWatchObserver {

    private static final Logger LOG = LogManager.getLogger(CloudWatchObserver.class);

    @Inject
    private JobInstanceDao dao;

    @Inject
    private JobNotificationDao notificationDao;

    @Inject
    private VMTracker vmTracker;

    /**
     * 
     * @param jobEvent
     */
    public void observe(@Observes(notifyObserver = Reception.ALWAYS) JobEvent jobEvent) {
        if (jobEvent.getEvent() == JobLifecycleEvent.AGENT_STARTED) {
            JobInstance job = dao.findById(Integer.valueOf(jobEvent.getJobId()));
            for (EntityVersion version : job.getNotificationVersions()) {
                JobNotification not = notificationDao.findById(version.getObjectId());
                if (not != null && not.getLifecycleEvents() != null
                        && not.getLifecycleEvents().contains(JobLifecycleEvent.AGENT_EXCESSIVE_CPU)) {
                    LOG.info("Adding watches for job " + job.getId());
                    addWatches(job, not);
                    break;
                }
            }
        } else if (jobEvent.getEvent() == JobLifecycleEvent.JOB_FINISHED) {
            JobInstance job = dao.findById(Integer.valueOf(jobEvent.getJobId()));
            for (EntityVersion version : job.getNotificationVersions()) {
                JobNotification not = notificationDao.findById(version.getObjectId());
                if (not != null && not.getLifecycleEvents() != null
                        && not.getLifecycleEvents().contains(JobLifecycleEvent.AGENT_EXCESSIVE_CPU)) {
                    LOG.info("Adding watches for job " + job.getId());
                    removeWatches(job, not);
                    break;
                }
            }
        }
    }

    /**
     * 
     * @param job
     * @param not
     */
    private void addWatches(final JobInstance job, final JobNotification not) {
        new Thread(new Runnable() {
            public void run() {
                String jobId = Integer.toString(job.getId());
                CloudVmStatusContainer vmStatusForJob = vmTracker.getVmStatusForJob(jobId);
                Set<CloudVmStatus> statuses = vmStatusForJob.getStatuses();
                Map<VMRegion, Set<String>> instanceMap = new HashMap<VMRegion, Set<String>>();
                for (CloudVmStatus s : statuses) {
                    s.getVmRegion();
                    Set<String> set = instanceMap.get(s.getVmRegion());
                    if (set == null) {
                        set = new HashSet<String>();
                        instanceMap.put(s.getVmRegion(), set);
                    }
                    set.add(s.getInstanceId());
                }
                for (Entry<VMRegion, Set<String>> entry : instanceMap.entrySet()) {
                    CloudwatchInstance cwInstance = new CloudwatchInstance(entry.getKey());
                    for (Recipient recip : not.getRecipients()) {
                        if (recip.getType() == RecipientType.email) {
                            String email = recip.getAddress();
                            cwInstance.addWatch(entry.getValue(), email, jobId);
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 
     * @param job
     * @param not
     */
    private void removeWatches(final JobInstance job, final JobNotification not) {
        new Thread(new Runnable() {
            public void run() {
                String jobId = Integer.toString(job.getId());
                CloudVmStatusContainer vmStatusForJob = vmTracker.getVmStatusForJob(jobId);
                Set<CloudVmStatus> statuses = vmStatusForJob.getStatuses();
                Map<VMRegion, Set<String>> instanceMap = new HashMap<VMRegion, Set<String>>();
                for (CloudVmStatus s : statuses) {
                    s.getVmRegion();
                    Set<String> set = instanceMap.get(s.getVmRegion());
                    if (set == null) {
                        set = new HashSet<String>();
                        instanceMap.put(s.getVmRegion(), set);
                    }
                    set.add(s.getInstanceId());
                }
                for (VMRegion vmRegion : instanceMap.keySet()) {
                    CloudwatchInstance cwInstance = new CloudwatchInstance(vmRegion);
                    for (Recipient recip : not.getRecipients()) {
                        if (recip.getType() == RecipientType.email) {
                            String email = recip.getAddress();
                            cwInstance.removeWatch(email, jobId);
                        }
                    }
                }
            }
        }).start();
    }
}

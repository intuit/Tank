/**
 *  Copyright 2015-2026 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.cloud;

import com.intuit.tank.vm.api.enumerated.JobLifecycleEvent;
import com.intuit.tank.vm.event.JobEvent;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * Announces that a job was added to a project's queue, as the web UI does when it queues a job, so that
 * "added to queue" notifications and other {@link JobEvent} observers see REST-queued jobs too.
 */
public class JobQueueEventSender {

    @Inject
    private Event<JobEvent> jobEventProducer;

    public void jobQueued(int jobId) {
        jobEventProducer.fire(new JobEvent(Integer.toString(jobId), "", JobLifecycleEvent.QUEUE_ADD));
    }
}

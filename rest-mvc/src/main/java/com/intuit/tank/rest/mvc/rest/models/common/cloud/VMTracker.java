/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.models.common.cloud;

import com.intuit.tank.rest.mvc.rest.models.common.CloudVmStatus;
import com.intuit.tank.rest.mvc.rest.models.common.CloudVmStatusContainer;
import com.intuit.tank.rest.mvc.rest.models.common.ProjectStatusContainer;
import com.intuit.tank.vm.event.JobEvent;
import javax.inject.Named;

import javax.annotation.Nonnull;
import java.util.Set;

@Named
public interface VMTracker {

    /**
     * 
     * @param projectId
     * @return
     */
    public ProjectStatusContainer getProjectStatusContainer(String projectId);

    /**
     * 
     * @param event
     *            the event to publish
     */
    public void publishEvent(JobEvent event);

    /**
     * Gets the Status of the instance.
     * 
     * @param instanceId
     *            the instance id to get the status for
     * @return the status or null
     */
    public CloudVmStatus getStatus(@Nonnull String instanceId);

    /**
     * Sets the status of the specified instance.
     * 
     * @param status
     *            the status to set to
     */
    public void setStatus(@Nonnull CloudVmStatus status);

    /**
     * @param jobId
     *            the id of the job to get the vmstatuses for.
     * @return the container with all the statuses.
     */
    public CloudVmStatusContainer getVmStatusForJob(String jobId);

    /**
     * Tests if we are in dev mode for killing of instances sample data.
     * 
     * @return
     */
    public boolean isDevMode();

    /**
     * Remove the specified instance of a job.
     * 
     * @param instanceId
     *            the instance of the jkob to remove
     */
    public void removeStatusForInstance(String instanceId);

    /**
     * Removes the Statuses for the specified job Id
     * 
     * @param jobId
     *            the job to remove
     */
    public void removeStatusForJob(String jobId);

    /**
     * 
     * @return
     */
    public Set<CloudVmStatusContainer> getAllJobs();

    /**
     * @param id
     *            the job id
     */
    public boolean isRunning(String id);

    /**
     * 
     * @param id
     *            the job Id
     */
    public void stopJob(String id);

}

/**
 *  Copyright 2015-2023 Intuit Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 */
package com.intuit.tank.rest.mvc.rest.services.agent;

import com.intuit.tank.api.model.v1.cloud.CloudVmStatus;
import com.intuit.tank.rest.mvc.rest.models.agent.TankHttpClientDefinitionContainer;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceCreateOrUpdateException;
import com.intuit.tank.rest.mvc.rest.controllers.errors.GenericServiceResourceNotFoundException;
import com.intuit.tank.vm.agent.messages.Headers;
import com.intuit.tank.vm.agent.messages.AgentAvailability;
import com.intuit.tank.vm.agent.messages.AgentTestStartData;
import com.intuit.tank.vm.agent.messages.AgentData;

import java.io.File;

public interface AgentServiceV2 {

    /**
     * Test method to test if the service is up
     *
     * @return
     *         non-null String value
     */
    public String ping();

    /**
     * Returns the agent settings file
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors in returning agent settings file
     *
     * @return agent settings file:
     *         string of the settings file
     */
    public String getSettings();

    /**
     * Returns the agent support files (jar files)
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there are errors in returning the zipped support files
     *
     * @return Streaming output of a gzipped archive of the support files
     */
    public File getSupportFiles();

    /**
     * Sets the agent status to ready by registering
     * the agent to a job that has been started
     *
     * @param agentData
     *            agentData payload
     * @throws GenericServiceResourceNotFoundException
     *         if there is an error registering agent for a job
     * @return AgentStartData JSON response
     */
    public AgentTestStartData agentReady(AgentData agentData);

    /**
     * Gets the agent headers
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there is an error returning agent headers
     *
     * @return XML of agent headers
     */
    public Headers getHeaders();

    /**
     * Gets the agent client definitions
     *
     * @throws GenericServiceResourceNotFoundException
     *        if there is an error returning agent clients
     *
     * @return XML of agent client definitions
     */
    public TankHttpClientDefinitionContainer getClients();

    /**
     * Sets the availability status for a standalone agent
     *
     * @throws GenericServiceCreateOrUpdateException
     *        if there is an error setting standalone agent availability
     */
    public void setStandaloneAgentAvailability(AgentAvailability availability);


    // Instance status operations

    /**
     * Gets the specific agent instance status
     *
     * @param instanceId instanceId for instance
     *
     * @throws GenericServiceResourceNotFoundException
     *         if there is an error returning instance status
     *
     * @return agent instance status JSON response
     */
    public CloudVmStatus getInstanceStatus(String instanceId);

    /**
     * Updates the specific agent instance status
     *
     * @param instanceId instanceId for instance
     * @param status VM Status payload
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error setting instance status
     */
    public void setInstanceStatus(String instanceId, CloudVmStatus status);

    /**
     * Stops an instance based on the provided instanceId
     *
     * @param instanceId instanceId for instance
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error stopping instance
     *
     * @return instance status
     */
    public String stopInstance(String instanceId);

    /**
     * Pauses a running instance based on the provided instanceId
     *
     * @param instanceId instanceId for instance
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error pausing instance
     *
     * @return instance status
     */
    public String pauseInstance(String instanceId);

    /**
     * Resumes a paused instance based on the provided instanceId
     *
     * @param instanceId instanceId for instance
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error resuming instance
     *
     * @return instance status
     */
    public String resumeInstance(String instanceId);

    /**
     * Kills an instance based on the provided instanceId
     *
     * @param instanceId instanceId for instance
     *
     * @throws GenericServiceCreateOrUpdateException
     *         if there is an error terminating instance
     *
     * @return instance status
     */
    public String killInstance(String instanceId);

}

/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.scheduleManager;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.vm.agent.messages.WatsAgentCompletedResponse;
import com.intuit.tank.vm.perfManager.RequestAgents;
import com.intuit.tank.vm.vmManager.VMRequest;

/**
 * AgentDispatcher
 * 
 * @author dangleton
 * 
 */
public interface AgentDispatcher {

    /**
     * Post a request for a virtual machine to the vmManager
     * 
     * @param vmInfo
     *            The virtual machine information
     */
    public abstract void vmManagerRequest(VMRequest vmInfo);

    /**
     * Process a message from the PerfManager for virtual machines
     * 
     * @param agentRequest
     *            The request object
     */
    public abstract void processAgentsMessage(RequestAgents agentRequest);

    public abstract void processAgentCompletedResponse(WatsAgentCompletedResponse moObject);

}
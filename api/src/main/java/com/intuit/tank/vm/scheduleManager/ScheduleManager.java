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

import com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest;
import com.intuit.tank.vm.agent.messages.AgentMngrAPIResponse;

/**
 * ScheduleManager
 * 
 * @author dangleton
 * 
 */
public interface ScheduleManager {

    /**
     * Adds a job to the
     * 
     * @param request
     */
    public abstract void addJob(AgentMngrAPIRequest request);

    public abstract void addAgent(AgentMngrAPIResponse agent);

    public abstract JobRequestData getJob(String jobId);

}
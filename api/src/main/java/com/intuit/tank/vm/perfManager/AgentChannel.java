/**
 * Copyright 2011 Intuit Inc. All Rights Reserved
 */
package com.intuit.tank.vm.perfManager;

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

import java.util.List;

/**
 * AgentChannel
 * 
 * @author dangleton
 * 
 */
public interface AgentChannel {

    /**
     * Sends a stop request to all agents on the specific instanceId.
     * 
     * @param instanceIds
     *            the instanceIds to stop
     */
    public void stopAgents(List<String> instanceIds);

    /**
     * Sends a pause request to all agents on the specific instanceId.
     * 
     * @param instanceIds
     *            the instanceIds to stop
     */
    public void pauseAgents(List<String> instanceIds);

    /**
     * Sends a restart request to all agents on the specific instanceId.
     * 
     * @param instanceIds
     *            the instanceIds to stop
     */
    public void restartAgents(List<String> instanceIds);

    /**
     * Sends a pause ramp request to all agents on the specific instanceId.
     * 
     * @param instanceIds
     */
    public void pauseRamp(List<String> instanceIds);

    /**
     * Sends a restart request to pause the ramping to all agents on the specific instanceId.
     * 
     * @param instanceIds
     */
    public void resumeRamp(List<String> instanceIds);

    /**
     * Sends a restart request to pause the ramping to all agents on the specific instanceId.
     * 
     * @param instanceIds
     */
    public void killAgents(List<String> instanceIds);
}
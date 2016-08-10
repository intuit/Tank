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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.intuit.tank.vm.agent.messages.AgentMngrAPIRequest;
import com.intuit.tank.vm.agent.messages.AgentMngrAPIResponse;

public class JobRequestData implements Serializable {

    private static final long serialVersionUID = 6161373334420827343L;

    static Logger logger = LogManager.getLogger(JobRequestData.class);

    private AgentMngrAPIRequest request = null;
    private List<AgentMngrAPIResponse> data = null;
    private int userCount = 0;

    public JobRequestData(AgentMngrAPIRequest request) {
        this.request = request;
        this.data = new ArrayList<AgentMngrAPIResponse>();
    }

    public void addAgent(AgentMngrAPIResponse agent) {
        this.data.add(agent);
        userCount += agent.getNumberVirtualUsers();
    }

    public List<AgentMngrAPIResponse> getAgents() {
        return this.data;
    }

    public AgentMngrAPIRequest getRequest() {
        return this.request;
    }

    public boolean isFullfilled() {
        try {
            logger.info("requestedVirtUsers: " + request.totalNumberVirtualUsers() + "  current:" + this.userCount
                    + " request:" + request.toString() + " numAgents: " + data.size());

            if (request.totalNumberVirtualUsers() <= this.userCount) {
                logger.debug("job fulfilled returning true.");
                return true;
            }
            return false;
        } catch (Exception ex) {
            logger.error("Error: " + ex.getMessage(), ex);
            return false;
        }
    }
}

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
import java.util.Hashtable;

import com.intuit.tank.vm.api.enumerated.VMProvider;

/*
 * Class represents a request for agents from a Perf Manager to the schedule
 * manager. A user can specify specific providers it wants agents from (ie
 * amazon, pharos,...) or none and just the total number of concurrent users
 * needed. Schedule manager will use the data in this request to determine 
 * how many VM's are needed to meet the agent request.
 * 
 * The requestor Id for the perf manager job is included to allow the scheduler
 * manager to send the agent information back to the initiating perf manager.
 * 
 */

public class AgentRequest implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = -1466683720562368885L;
    private int requestorId;
    private int totalNumberUsers;
    private Hashtable<VMProvider, Integer> providers;

    public AgentRequest(int id, int users) {
        requestorId = id;
        totalNumberUsers = users;
        providers = new Hashtable<VMProvider, Integer>();
    }

    public AgentRequest(int id) {
        requestorId = id;
        totalNumberUsers = 0;
        providers = new Hashtable<VMProvider, Integer>();
    }

    public Hashtable<VMProvider, Integer> getProviders() {
        return providers;
    }

    public int getNumberUsers() {
        return totalNumberUsers;
    }

    public int getRequestorId() {
        return requestorId;
    }

    public void addProvider(VMProvider provider, int numUsers) {
        providers.put(provider, numUsers);
        totalNumberUsers += numUsers;
    }
}

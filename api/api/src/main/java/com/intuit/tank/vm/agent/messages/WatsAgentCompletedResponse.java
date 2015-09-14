package com.intuit.tank.vm.agent.messages;

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

import com.intuit.tank.vm.api.enumerated.VMProvider;

/**
 * Sent from Agent to Schedule Manager when a job is completed.
 * 
 */
public class WatsAgentCompletedResponse {
    private VMProvider provider;
    private String instanceId;

    public WatsAgentCompletedResponse(VMProvider provider, String instanceId) {
        this.provider = provider;
        this.instanceId = instanceId;
    }

    public VMProvider getProvider() {
        return provider;
    }

    public String getInstanceId() {
        return instanceId;
    }
}
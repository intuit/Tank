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

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.VMRegion;

public class AgentMngrAPIResponse implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 4667338851749143876L;
    private String queueName;
    private int numberVirtualUsers;
    private String jobId;
    private String instanceId;
    private VMRegion region;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public int getNumberVirtualUsers() {
        return numberVirtualUsers;
    }

    public void setNumberVirtualUsers(int numberVirtualUsers) {
        this.numberVirtualUsers = numberVirtualUsers;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * @return the region
     */
    public VMRegion getRegion() {
        return region;
    }

    /**
     * @param region
     *            the region to set
     */
    public void setRegion(VMRegion region) {
        this.region = region;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}

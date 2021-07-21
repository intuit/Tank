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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.intuit.tank.vm.api.enumerated.VMRegion;

/**
 * AgentData represents data about an agent. Used to let the controller know when the instance is ready
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "agent-data", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AgentData", namespace = Namespace.NAMESPACE_V1, propOrder = {
        "jobId", "instanceId", "instanceUrl", "region", "capacity", "users", "zone" })
public class AgentData implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "job-id", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String jobId;

    @XmlElement(name = "instance-id", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceId;

    @XmlElement(name = "instance-url", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceUrl;

    @XmlElement(name = "region", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private VMRegion region;

    @XmlElement(name = "capacity", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int capacity;

    @XmlElement(name = "users", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int users;

    @XmlElement(name = "zone", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private String zone;

    public AgentData() {
        super();
    }

    public AgentData(String jobId, String instanceId, String instanceUrl,
            int capacity, VMRegion region, String zone) {
        super();
        this.jobId = jobId;
        this.instanceId = instanceId;
        this.instanceUrl = instanceUrl;
        this.capacity = capacity;
        this.region = region;
        this.zone = zone;
    }

    public VMRegion getRegion() {
        return region;
    }

    public String getJobId() {
        return jobId;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public String getInstanceUrl() {
        return instanceUrl;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getUsers() {
        return users;
    }

    public void setUsers(int users) {
        this.users = users;
    }

    public String getZone() {
        return zone;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AgentData)) {
            return false;
        }
        AgentData o = (AgentData) obj;
        return new EqualsBuilder().append(o.instanceId, instanceId).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(21, 25).append(instanceId).toHashCode();
    }

}

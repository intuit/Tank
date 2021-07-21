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
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * AgentData represents data about an agent. Used to let the controller know when the instance is ready
 * 
 * @author dangleton
 * 
 */
@XmlRootElement(name = "agent-availability", namespace = Namespace.NAMESPACE_V1)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AgentAvailablity", namespace = Namespace.NAMESPACE_V1)
public class AgentAvailability implements Serializable {

    private static final long serialVersionUID = 1L;

    @XmlElement(name = "instance-id", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceId;

    @XmlElement(name = "instance-url", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private String instanceUrl;

    @XmlElement(name = "capacity", namespace = Namespace.NAMESPACE_V1, required = true, nillable = false)
    private int capacity;

    @XmlElement(name = "timestamp", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private Date timestamp;

    @XmlElement(name = "availability-status", namespace = Namespace.NAMESPACE_V1, required = false, nillable = false)
    private AgentAvailabilityStatus availabilityStatus = AgentAvailabilityStatus.AVAILABLE;

    public AgentAvailability() {
        super();
    }

    public AgentAvailability(String instanceId, String instanceUrl, int capacity, AgentAvailabilityStatus status) {
        super();
        this.instanceId = instanceId;
        this.instanceUrl = instanceUrl;
        this.capacity = capacity;
        this.availabilityStatus = status;
        this.timestamp = new Date();
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

    public AgentAvailabilityStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(AgentAvailabilityStatus availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AgentAvailability)) {
            return false;
        }
        AgentAvailability o = (AgentAvailability) obj;
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
